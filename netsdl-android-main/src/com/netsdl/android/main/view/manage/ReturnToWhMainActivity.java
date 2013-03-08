/**
 * 
 */
package com.netsdl.android.main.view.manage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.netsdl.android.common.Constant;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.CustMaster;
import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.DeviceMaster;
import com.netsdl.android.common.db.StoreMaster;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.MainActivity;

/**
 * @author jasper
 * 
 */
public class ReturnToWhMainActivity extends Activity {
	private String[] custNos;
	private String[] custNames;
	private String[] operatorNos;
	private String[] operatorNames;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String localShopCode;
	private String localShopName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_return_to_wh_main);
		init();
		initContral();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub

	}

	private void init() {
		// 返品主表确认事件
		((Button) this.findViewById(R.id.confirmButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							toDetailView();
						} catch (RuntimeException e) {
							Toast.makeText(ReturnToWhMainActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		// 返品主界面取消事件
		((Button) this.findViewById(R.id.backButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							Intent gotoIntent = new Intent(
									ReturnToWhMainActivity.this,
									MainActivity.class);
							Bundle bundle = new Bundle();
							// 临时方法，IS_LOGIN为true表示已经是登录状态，在MainActivity的onCreate处理中直接进到function界面
							bundle.putBoolean(Constant.IS_LOGIN, true);
							gotoIntent.putExtras(bundle);
							ReturnToWhMainActivity.this
									.startActivity(gotoIntent);
						} catch (RuntimeException e) {
							Toast.makeText(ReturnToWhMainActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		// 日期
		((EditText) this.findViewById(R.id.returnDateEditText))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							orderDateAction();
						}
						return true;
					}
				});
	}

	// 转到明细界面
	private void toDetailView() {
		Intent gotoIntent = new Intent(ReturnToWhMainActivity.this,
				ReturnToWhDetailActivity.class);
		// 需要把主界面输入信息传给明细界面
		Bundle bundle = new Bundle();

		gotoIntent.putExtras(bundle);
		ReturnToWhMainActivity.this.startActivity(gotoIntent);
	}

	// 初始化数据组件控制
	private void initContral() {
		// 店铺只显示
		EditText shopET = (EditText) this.findViewById(R.id.localShopEditText);
		shopET.setEnabled(false);
		try {
			Object[] ss = DatabaseHelper.getSingleColumn(getContentResolver(), new String[]{"1",Util.getLocalDeviceId(this.getApplicationContext())}, new String[]{DeviceMaster.COLUMN_INIT_ID,DeviceMaster.COLUMN_DEVICE_ID}, DeviceMaster.class);
			if(ss!=null&&ss.length>0)
			{
				String sss = (String)ss[DatabaseHelper.getColumnIndex(DeviceMaster.COLUMN_FIELD_01, DeviceMaster.COLUMNS)];
				if(sss!=null)
				{
					String[] sps = sss.split(";");
					if(sps.length==2)
					{
						this.localShopCode = sps[0];
						this.localShopName = sps[1];			
						shopET.setText(localShopName);
					}
				}
				
			}
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 退货仓库
		try {
			Object[][] cl = DatabaseHelper.getMultiColumn(getContentResolver(),
					new String[] { Constant.CUST_TYPE, Constant.CUST_WH },
					new String[] { CustMaster.COLUMN_CUST_TYPE,
							CustMaster.COLUMN_CUST_CAT }, null, null, null,
					null, false, CustMaster.class);
			if (cl != null && cl.length > 0) {
				custNos = new String[cl.length];
				custNames = new String[cl.length];
				for (int i = 0; i < cl.length; i++) {
					custNos[i] = (String) cl[i][0];
					custNames[i] = (String) cl[i][1];
				}

				Spinner custSpinner = (Spinner) this
						.findViewById(R.id.returnToWhSpinner);
				ArrayAdapter<String> custAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_spinner_item, custNames);
				custSpinner.setAdapter(custAdapter);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 操作人
		try {
			Object[][] sl = DatabaseHelper.getMultiColumn(getContentResolver(),
					new String[]{},
					new String[]{}, null, null, null,
					null, false, StoreMaster.class);
			if (sl != null && sl.length > 0) {
				operatorNos = new String[sl.length];
				operatorNames = new String[sl.length];
				for (int i = 0; i < sl.length; i++) {
					operatorNos[i] = sl[i][0]==null?"":sl[i][0].toString();
					operatorNames[i] = (String) sl[i][2];
				}

				Spinner shopSpinner = (Spinner) this
						.findViewById(R.id.operatorSpinner);
				ArrayAdapter<String> shopAdapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, operatorNames);
				shopSpinner.setAdapter(shopAdapter);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//默认日期
		EditText orderDateET = ((EditText) this
				.findViewById(R.id.returnDateEditText));
		orderDateET.setText(this.sdf.format(new Date()));
	}

	// 日期选择
	private void orderDateAction() {
		final EditText orderDateET = ((EditText) this
				.findViewById(R.id.returnDateEditText));

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

}
