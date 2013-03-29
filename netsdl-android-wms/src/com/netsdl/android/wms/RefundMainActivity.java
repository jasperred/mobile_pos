package com.netsdl.android.wms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.netsdl.android.db.CustMaster;

public class RefundMainActivity extends Activity {
	
	private String whNo;
	private String whName;
	private String userNo;
	private String userName;
	private String[] custNos;
	private String[] custNames;
	private String[] invType = new String[] { "A", "B", "C" };

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refund_main);
		Bundle data = this.getIntent().getExtras();
		if(data==null)
		{
			Toast.makeText(RefundMainActivity.this, R.string.messageNoWh,
					Toast.LENGTH_SHORT).show();
			return;
		}
		whNo = data.getString("whNo");
		whName = data.getString("whName");
		userNo = data.getString("userNo");
		userName = data.getString("userName");
		init();
		initData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void init() {
		((Button) this.findViewById(R.id.backButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							backModule();
						} catch (RuntimeException e) {
							Toast.makeText(RefundMainActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

					private void backModule() {
						Intent gotoIntent = new Intent(RefundMainActivity.this,
								ModuleActivity.class);
						RefundMainActivity.this.startActivity(gotoIntent);
					}

				});
		((Button) this.findViewById(R.id.confirmButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							confirmRefund();
						} catch (RuntimeException e) {
							Toast.makeText(RefundMainActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
	}

	// 初始化数据，下拉框
	private void initData() {
		CustMaster cm = new CustMaster(this);
		List<Map> cl = cm.find(CustMaster.COLUMNS,CustMaster.COLUMN_CUST_TYPE+" = ? and "+CustMaster.COLUMN_CUST_CAT+" = ?",new String[]{ "CUST", "WH" });
		if (cl != null && cl.size() > 0) {
			custNos = new String[cl.size()];
			custNames = new String[cl.size()];
			for (int i = 0; i < cl.size(); i++) {
				Map<String, String> m = cl.get(i);
				custNos[i] = m.get(CustMaster.COLUMN_CUST_NO);
				custNames[i] = m.get(CustMaster.COLUMN_CUST_NAME);
			}

			Spinner custSpinner = (Spinner) this.findViewById(R.id.custSpinner);
			ArrayAdapter<String> custAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, custNames);
			custSpinner.setAdapter(custAdapter);
		}
		Spinner invTypeSpinner = (Spinner) this
				.findViewById(R.id.inventoryTypeSpinner);
		ArrayAdapter<String> invTypeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, invType);
		invTypeSpinner.setAdapter(invTypeAdapter);
		((EditText) this.findViewById(R.id.refundDateEditText))
				.setInputType(InputType.TYPE_NULL);
		((EditText) this.findViewById(R.id.refundDateEditText)).setText(sdf
				.format(new Date()));
		((EditText) this.findViewById(R.id.refundDateEditText))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							orderDateAction();
						}
						return true;
					}
				});
	}

	// 日期选择
	private void orderDateAction() {
		final EditText orderDateET = ((EditText) this
				.findViewById(R.id.refundDateEditText));

		Date date = null;
		if (orderDateET.getText().toString().trim().length() == 0)
			date = new Date();
		else
			try {
				date = sdf.parse(orderDateET.getText().toString().trim());
			} catch (ParseException e) {
				date = new Date();
			}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		new DatePickerDialog(this, new OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				String strDocumentDate = sdf.format(calendar.getTime());

				orderDateET.setText(strDocumentDate);

			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)).show();
	}

	// 进入扫描界面
	private void confirmRefund() {
		Spinner custSpinner = (Spinner) this.findViewById(R.id.custSpinner);
		int c = custSpinner.getSelectedItemPosition();
		if(c<0)
			return;
		String custNo = this.custNos[c];
		Spinner invTypeSpinner = (Spinner) this
				.findViewById(R.id.inventoryTypeSpinner);
		String invType = (String)invTypeSpinner.getSelectedItem();
		if(invType==null)
		{
			return;
		}
		String date = ((EditText) this.findViewById(R.id.refundDateEditText)).getText().toString();
		if(date==null||date.trim().length()==0)
		{
			return;
		}

		Intent gotoIntent = new Intent(RefundMainActivity.this,
				RefundDetailActivity.class);
        Bundle bundle = new Bundle(); 
        bundle.putString("custNo", custNo); 
        bundle.putString("custName", this.custNames[c]); 
        bundle.putString("invType", invType); 
        bundle.putString("refundDate", date); 
        bundle.putInt("rtn", -1); 
        bundle.putString("whNo", whNo); 
        bundle.putString("whName", whName); 
        bundle.putString("userNo", userNo); 
        bundle.putString("userName", userName); 
        bundle.putString("orderType", "DO"); 
        gotoIntent.putExtras(bundle);
		RefundMainActivity.this.startActivity(gotoIntent);
	}

}
