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
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import com.netsdl.android.main.view.customer.EditTextButton;
import com.netsdl.android.main.view.customer.InputDialog;

/**
 * @author jasper
 *
 */
public class ReturnToWhActivity extends Activity implements OnItemClickListener,
OnItemLongClickListener {
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
	
	private EditText scanEditText;
	
	private List<Map<String, Object>> dataList;// 保存销售数据
	private Map<String, Integer> dataMap;// 保存barcode在dataList中的位置
	private int seq;// 输入顺序

	private ItemAdapter listItemAdapter;
	private int currentPosition = -1;// 记录明细选择的位置

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_return_to_wh);
		setTitle(R.string.return_to_wh_title);
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
		if(scanEditText==null)
		scanEditText = ((EditTextButton) this
				.findViewById(R.id.scanEditTextButton)).getInputEditText();
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
		if (dataMap == null)
			dataMap = new HashMap<String, Integer>(20);
		dataMap.clear();
		if (dataList == null)
			dataList = new ArrayList(20);
		dataList.clear();
		//初始化明细的表头
		final ListView listViewItem = (ListView) this
				.findViewById(R.id.detailListView);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout head = (LinearLayout) inflater.inflate(
				R.layout.view_detail, null);
		head.setBackgroundColor(Color.LTGRAY);
		head.setBaselineAligned(true);
		listViewItem.addHeaderView(head);
		if (listItemAdapter == null) {
			listItemAdapter = new ItemAdapter(this);
			listViewItem.setAdapter(listItemAdapter);
		}

		listViewItem.setOnItemClickListener(this);
		listViewItem.setOnItemLongClickListener(this);
		if (barcodeMap == null)
			barcodeMap = new HashMap();
		barcodeMap.clear();
		if (barcodeList == null)
			barcodeList = new ArrayList();
		barcodeList.clear();
	}

	// 明细长按事件
	public boolean onItemLongClick(AdapterView<?> listview, View arg1,
			int position, long arg3) {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(40);
		currentPosition = position - 1;
		listItemAdapter.notifyDataSetChanged();
		return true;
	}

	public void onItemClick(AdapterView<?> listview, View arg1, int position,
			long arg3) {
		currentPosition = -1;
		listItemAdapter.notifyDataSetChanged();
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
			//Map m = barcodeMap.get(str);
			Integer i = dataMap.get(str);
			Map m = null;
			if (i != null)
				m = dataList.get(i);
			if (m == null) {
				dataMap.put(str, seq);
				seq++;
				m = new HashMap(10, 1F);
				m.put("seq", seq);
				m.put(SkuMaster.COLUMN_BAR_CODE, str);
				m.put(PosTable.COLUMN_SKU_CD, sl[DatabaseHelper.getColumnIndex(SkuMaster.COLUMN_SKU_CD, SkuMaster.COLUMNS)]);
				m.put(PosTable.COLUMN_ITEM_NAME, sl[DatabaseHelper.getColumnIndex(SkuMaster.COLUMN_ITEM_NAME, SkuMaster.COLUMNS)]);
				m.put(PosTable.COLUMN_QTY, 1);
				m.put(SkuMaster.COLUMN_ITEM_CD, sl[DatabaseHelper.getColumnIndex(SkuMaster.COLUMN_ITEM_CD, SkuMaster.COLUMNS)]);
				dataList.add(m);
//				barcodeMap.put(str, m);
//				barcodeList.add(str);
			} else {
				m.put(PosTable.COLUMN_QTY, (Integer) m.get(PosTable.COLUMN_QTY) + 1);
			}
//			ListView detailView = ((ListView) this
//					.findViewById(R.id.detailListView));
//			List l = new ArrayList();
//			for (String b : barcodeList) {
//				l.add(barcodeMap.get(b));
//			}
//			SimpleAdapter listItemAdapter = new SimpleAdapter(
//					detailView.getContext(), l, R.layout.view_detail,
//					new String[] {SkuMaster.COLUMN_ITEM_NAME,  SkuMaster.COLUMN_SKU_CD,
//							SkuMaster.COLUMN_ITEM_CD,
//							"qty" }, new int[] {
//						R.id.moneyTextView, R.id.priceTextView, R.id.subTextView,
//							R.id.qtyTextView });
//			detailView.setAdapter(listItemAdapter);
			this.listItemAdapter.notifyDataSetChanged();
			countSubTotal();
		}
		scanEditText.setText("");
	}

	//计算小计
	private void countSubTotal()
	{
		TextView subTotalTextView = (TextView) this
				.findViewById(R.id.subTotalTextView);
		int q = 0;
		for(Map m:dataList)
		{
			Integer qty = (Integer)m.get(PosTable.COLUMN_QTY);
			if(qty!=null)
			q += qty;
		}
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

		if (dataList != null)
			dataList.clear();
		if (dataMap != null)
			dataMap.clear();
		this.listItemAdapter.notifyDataSetChanged();

		//默认日期
		EditText orderDateET = ((EditText) this
				.findViewById(R.id.returnDateEditText));
		orderDateET.setText(this.sdf.format(new Date()));
	}
	class ItemAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;

		public ItemAdapter(Activity activity) {
			this.context = activity;
			inflater = LayoutInflater.from(context);
		}

		public int getItemViewType(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return dataList.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return dataList.get(arg0);
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.view_detail,
						parent, false);
				holder = new ViewHolder();
				holder.seqTextView = (TextView) convertView
						.findViewById(R.id.seqTextView);
				holder.skuCdTextView = (TextView) convertView
						.findViewById(R.id.skuCdTextView);
				holder.itemNameTextView = (TextView) convertView
						.findViewById(R.id.itemNameTextView);
				holder.itemCdTextView = (TextView) convertView
						.findViewById(R.id.itemCdTextView);
				holder.qtyTextView = (TextView) convertView
						.findViewById(R.id.qtyTextView);
				holder.linearLayout = (LinearLayout) convertView
						.findViewById(R.id.layout_other);
				holder.item_qty_layout = (LinearLayout) convertView
						.findViewById(R.id.item_qty_layout);
				holder.item_delete_layout = (LinearLayout) convertView
						.findViewById(R.id.item_delete_layout);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Map data = dataList.get(position);
			holder.seqTextView.setText(data.get("seq").toString());
			holder.skuCdTextView.setText(data
					.get(SkuMaster.COLUMN_SKU_CD).toString());
			holder.itemNameTextView.setText(data
					.get(SkuMaster.COLUMN_ITEM_NAME).toString());
			holder.itemCdTextView.setText(data.get(SkuMaster.COLUMN_ITEM_CD)
					.toString());
			holder.qtyTextView.setText(data.get("qty").toString());
			if (position == currentPosition) {
				holder.linearLayout.setVisibility(View.VISIBLE);
				holder.item_qty_layout.setClickable(true);
				holder.item_delete_layout.setClickable(true);
				
				// 数量调整
				holder.item_qty_layout
						.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {

								InputDialog inputDialog = new InputDialog(
										ReturnToWhActivity.this);
								inputDialog.setInt(true);
								inputDialog.setTitle(R.string.messageQtyChange);
								DisplayMetrics displayMetrics = new DisplayMetrics();
								getWindowManager().getDefaultDisplay()
										.getMetrics(displayMetrics);

								if (inputDialog.showDialog(
										displayMetrics.widthPixels,
										displayMetrics.heightPixels)) {
									String inputStr = inputDialog.getInputStr();
									Map m = dataList.get(currentPosition);
									m.put(PosTable.COLUMN_QTY, new Integer(inputStr));
									listItemAdapter.notifyDataSetChanged();
									countSubTotal();
								}
								currentPosition = -1;
							}
						});
				
				holder.item_delete_layout
						.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								dataList.remove(currentPosition);
								dataMap.clear();
								for (int i = 0; i < dataList.size(); i++) {
									Map m = dataList.get(i);
									m.put("seq", i+1);
									dataMap.put((String) m
											.get(SkuMaster.COLUMN_BAR_CODE),
											i);
								}
								seq = dataList.size();
								listItemAdapter.notifyDataSetChanged();
								currentPosition = -1;
								countSubTotal();
							}
						});
			} else {
				holder.linearLayout.setVisibility(View.GONE);
				holder.item_qty_layout.setClickable(false);
				holder.item_delete_layout.setClickable(false);
			}

			return convertView;
		}

		class ViewHolder {
			public TextView seqTextView;
			public TextView skuCdTextView;
			public TextView itemNameTextView;
			public TextView itemCdTextView;
			public TextView qtyTextView;
			public LinearLayout linearLayout;
			public LinearLayout item_qty_layout;
			public LinearLayout item_delete_layout;
		}
	}
}
