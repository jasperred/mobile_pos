/**
 * 
 */
package com.netsdl.android.main.view.manage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.netsdl.android.common.Constant;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.CustMaster;
import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.DeviceMaster;
import com.netsdl.android.common.db.PosTable;
import com.netsdl.android.common.db.SkuMaster;
import com.netsdl.android.common.db.StoreMaster;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.FunctionActivity;
import com.netsdl.android.main.view.MainActivity;

/**
 * @author jasper
 *
 */
public class ReturnToWhActivity extends Activity {
	private String[] custNos;
	private String[] custNames;
	private String[] operatorNos;
	private String[] operatorNames;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String localShopCode;
	private String localShopName;
	private Map<String, Map> barcodeMap;//缓存SKU
	private List<String> barcodeList;
	private final String ORDER_TYPE = "RO";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_return_to_wh);
		init();
		initContral();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
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
							returnToWhConfirm();
						} catch (RuntimeException e) {
							Toast.makeText(ReturnToWhActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		// 返品主界面取消事件
		((Button) this.findViewById(R.id.backButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							backToFunction();
						} catch (RuntimeException e) {
							Toast.makeText(ReturnToWhActivity.this,
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
		//扫描
		EditText scanEditText = (EditText) this
				.findViewById(R.id.scanEditText);
		scanEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				try {
					if (keyCode == KeyEvent.KEYCODE_ENTER
							&& event.getAction() == KeyEvent.ACTION_UP) {
						barcodeSearch();
						return true;
					}
					return false;
				} catch (RuntimeException e) {
					Toast.makeText(ReturnToWhActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		});
		scanEditText.setFocusable(true);
		//初始化明细的表头
		ListView detailView = ((ListView) this
				.findViewById(R.id.detailListView));
		if (detailView.getHeaderViewsCount() == 0) {
			LinearLayout lin = new LinearLayout(this);
			lin.setOrientation(LinearLayout.VERTICAL);
			LinearLayout lin1 = new LinearLayout(this);
			lin1.setOrientation(LinearLayout.HORIZONTAL);//item name
			lin1.setGravity(Gravity.LEFT);
			LinearLayout lin2 = new LinearLayout(this);
			lin2.setOrientation(LinearLayout.HORIZONTAL);//others
			TextView skuTextView = new TextView(this);
			skuTextView.setGravity(Gravity.LEFT);
			//skuTextView.setTextColor(Color.WHITE);
			//skuTextView.setTextSize(30);
			skuTextView.setText("SKU编码");
			TextView itemTextView = new TextView(this);
			itemTextView.setGravity(Gravity.LEFT);
			//itemTextView.setTextColor(Color.WHITE);
			//itemTextView.setTextSize(30);
			itemTextView.setText("商品编码");
			TextView titleTextView = new TextView(this);
			titleTextView.setGravity(Gravity.LEFT);
			//titleTextView.setTextColor(Color.WHITE);
			//titleTextView.setTextSize(30);
			titleTextView.setText("商品名称");
			TextView qtyTextView = new TextView(this);
			qtyTextView.setGravity(Gravity.RIGHT);
			//qtyTextView.setTextColor(Color.WHITE);
			//qtyTextView.setTextSize(30);
			qtyTextView.setText("数量");
			lin1.addView(titleTextView, 0, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT, 1));
			lin2.addView(skuTextView, 0, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT, 1));
			lin2.addView(itemTextView, 1, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT, 1));
			lin2.addView(qtyTextView, 2, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT, 1));
			lin.addView(lin1, 0, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 1));
			lin.addView(lin2, 1, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 1));
			detailView.addHeaderView(lin);
		}
		// 初始化时Detail增加一条空记录，否则头无法显示
		SimpleAdapter listItemAdapter = new SimpleAdapter(
				detailView.getContext(), new ArrayList(), R.layout.view_detail,
				new String[] { SkuMaster.COLUMN_SKU_CD,
						SkuMaster.COLUMN_ITEM_CD, SkuMaster.COLUMN_ITEM_NAME,
						"qty" }, new int[] { R.id.priceTextView,
						R.id.subTextView, R.id.moneyTextView,
						R.id.qtyTextView });
		detailView.setAdapter(listItemAdapter);
		if (barcodeMap == null)
			barcodeMap = new HashMap();
		barcodeMap.clear();
		if (barcodeList == null)
			barcodeList = new ArrayList();
		barcodeList.clear();
	}

	// 退货确认，保存后清空数据
	private void returnToWhConfirm() {
		ListView detailView = ((ListView) this
				.findViewById(R.id.detailListView));
		int count = detailView.getAdapter().getCount();
		if (count <=1) {
			Toast.makeText(this, R.string.messageNoScan, Toast.LENGTH_LONG)
					.show();
			return;
		}
		final String uuid = Util.getUUID();
		String returnDate = ((EditText) this
				.findViewById(R.id.returnDateEditText)).getText().toString();
		Spinner whSpinner = (Spinner) this.findViewById(R.id.returnToWhSpinner);
		int wh = whSpinner.getSelectedItemPosition();
		Spinner opSpinner = (Spinner) this.findViewById(R.id.operatorSpinner);
		int op = opSpinner.getSelectedItemPosition();
		Date date = new Date();
		Map head = new HashMap();
		head.put(PosTable.COLUMN_ORDER_NO, uuid);
		head.put(PosTable.COLUMN_CREATE_DATE,
				Util.dateToString(date, "yyyy-MM-dd HH:mm:ss"));
		head.put(PosTable.COLUMN_ORDER_DATE, returnDate);
		head.put(PosTable.COLUMN_WH_NO, this.localShopCode);
		head.put(PosTable.COLUMN_WH_NAME, this.localShopName);
		head.put(PosTable.COLUMN_CUST_NO, this.custNos[wh]);
		head.put(PosTable.COLUMN_CUST_NAME, this.custNames[wh]);
		head.put(PosTable.COLUMN_USER_NO, this.operatorNos[op]);
		head.put(PosTable.COLUMN_USER_NAME, this.operatorNames[op]);
		head.put(PosTable.COLUMN_RTN, -1);
		head.put(PosTable.COLUMN_FLAG, "SKU");
		head.put(PosTable.COLUMN_ORDER_TYPE, this.ORDER_TYPE);
		head.put(PosTable.COLUMN_END_DAY,0);
		head.put(PosTable.COLUMN_BO_TYPE,"");
		for (int i = 1; i < count; i++) {
			Map mj = (Map) detailView.getAdapter().getItem(i);
			mj.putAll(head);
			try {
				DatabaseHelper.insert(getContentResolver(), mj, PosTable.class);
			} catch (IllegalArgumentException e) {
				failProcess(uuid);
				return;
			} catch (SecurityException e) {
				failProcess(uuid);
				return;
			} catch (IllegalAccessException e) {
				failProcess(uuid);
				return;
			} catch (NoSuchFieldException e) {
				failProcess(uuid);
				return;
			}
		}
		clearData();
		Toast.makeText(this, R.string.messageSuccess, Toast.LENGTH_LONG)
		.show();
	}

	//fail -- delete
	private void failProcess(final String uuid) {
		try {
			DatabaseHelper.delete(getContentResolver(), PosTable.COLUMN_ORDER_NO, new String[]{uuid}, PosTable.class);
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
		Toast.makeText(this, R.string.messageFail, Toast.LENGTH_LONG)
		.show();
	}
	
	//返回到功能界面
	private void backToFunction() {
		Intent gotoIntent = new Intent(
				ReturnToWhActivity.this,
				FunctionActivity.class);
		Bundle bundle = new Bundle();
		// 临时方法，IS_LOGIN为true表示已经是登录状态，在MainActivity的onCreate处理中直接进到function界面
		bundle.putBoolean(Constant.IS_LOGIN, true);
		gotoIntent.putExtras(bundle);
		ReturnToWhActivity.this
				.startActivity(gotoIntent);
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
	
	//扫描
	private void barcodeSearch() {
		EditText scanEditText = (EditText) this
				.findViewById(R.id.scanEditText);
		String str = scanEditText.getText().toString().trim();
		if (str.length() == 0)
			return;
		Object[] sl = null;
		try {
			sl = DatabaseHelper.getSingleColumn(getContentResolver(), new String[] { str }, new String[] { SkuMaster.COLUMN_BAR_CODE }, SkuMaster.class);
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
		// 未找到商品
		if (sl == null || sl.length == 0) {
			Toast.makeText(this, R.string.messageNoSku, Toast.LENGTH_LONG)
					.show();
		} else {
			Map m = barcodeMap.get(str);
			if (m == null) {
				Map mm = new HashMap();
				mm.put(PosTable.COLUMN_SKU_CD, sl[DatabaseHelper.getColumnIndex(SkuMaster.COLUMN_SKU_CD, SkuMaster.COLUMNS)]);
				mm.put(PosTable.COLUMN_ITEM_NAME, sl[DatabaseHelper.getColumnIndex(SkuMaster.COLUMN_ITEM_NAME, SkuMaster.COLUMNS)]);
				mm.put(PosTable.COLUMN_QTY, 1);
				mm.put(SkuMaster.COLUMN_ITEM_CD, sl[DatabaseHelper.getColumnIndex(SkuMaster.COLUMN_ITEM_CD, SkuMaster.COLUMNS)]);
				
				barcodeMap.put(str, mm);
				barcodeList.add(str);
			} else {
				m.put(PosTable.COLUMN_QTY, (Integer) m.get(PosTable.COLUMN_QTY) + 1);
			}
			ListView detailView = ((ListView) this
					.findViewById(R.id.detailListView));
			List l = new ArrayList();
			for (String b : barcodeList) {
				l.add(barcodeMap.get(b));
			}
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					detailView.getContext(), l, R.layout.view_detail,
					new String[] {SkuMaster.COLUMN_ITEM_NAME,  SkuMaster.COLUMN_SKU_CD,
							SkuMaster.COLUMN_ITEM_CD,
							"qty" }, new int[] {
						R.id.moneyTextView, R.id.priceTextView, R.id.subTextView,
							R.id.qtyTextView });
			detailView.setAdapter(listItemAdapter);
			countSubTotal(1);
		}
		scanEditText.setText("");
	}

	//计算小计
	private void countSubTotal(int qty)
	{
		TextView subTotalTextView = (TextView) this
				.findViewById(R.id.subTotalTextView);
		String s = subTotalTextView.getText().toString();
		int q = 0;
		if(s.length()>0)
		{
			q = Integer.parseInt(s);
		}
		q+=qty;
		subTotalTextView.setText(""+q);
	}
	//清空数据
	private void clearData()
	{
		((TextView) this
		.findViewById(R.id.subTotalTextView)).setText("0");
		if(barcodeMap!=null)
			barcodeMap.clear();
		if(barcodeList!=null)
			barcodeList.clear();
		ListView detailView = ((ListView) this
				.findViewById(R.id.detailListView));
		SimpleAdapter listItemAdapter = new SimpleAdapter(
				detailView.getContext(), new ArrayList(), R.layout.view_detail,
				new String[] { SkuMaster.COLUMN_SKU_CD,
						SkuMaster.COLUMN_ITEM_CD,
						SkuMaster.COLUMN_ITEM_NAME, "qty" }, new int[] {
						R.id.priceTextView, R.id.subTextView,
						R.id.moneyTextView, R.id.qtyTextView });
		detailView.setAdapter(listItemAdapter);

		//默认日期
		EditText orderDateET = ((EditText) this
				.findViewById(R.id.returnDateEditText));
		orderDateET.setText(this.sdf.format(new Date()));
	}
}
