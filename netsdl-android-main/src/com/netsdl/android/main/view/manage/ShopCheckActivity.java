/**
 * 
 */
package com.netsdl.android.main.view.manage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
import com.netsdl.android.common.db.CheckOrderTable;
import com.netsdl.android.common.db.CustMaster;
import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.DeviceMaster;
import com.netsdl.android.common.db.CheckOrderTable;
import com.netsdl.android.common.db.CheckOrderTable;
import com.netsdl.android.common.db.PosTable;
import com.netsdl.android.common.db.StoreMaster;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.FunctionActivity;
import com.netsdl.android.main.view.customer.EditTextButton;
import com.netsdl.android.main.view.customer.InputDialog;

/**
 * @author jasper
 * 
 */
public class ShopCheckActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener {
	private String[] custNos;
	private String[] custNames;
	private String[] operatorNos;
	private String[] operatorNames;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String localShopCode;
	private String localShopName;
	private Map<String, Map> barcodeMap;// 缓存SKU
	private List<String> barcodeList;
	private final String ORDER_TYPE = "DO";
	private EditText scanEditText;
	private EditText checkNoEditText;

	private List<Map<String, Object>> dataList;// 保存销售数据
	private Map<String, Integer> dataMap;// 保存barcode在dataList中的位置
	private int seq;// 输入顺序

	private ItemAdapter listItemAdapter;
	private int currentPosition = -1;// 记录明细选择的位置
	
	private String checkOrderId;//盘点ID

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check);
		setTitle(R.string.check_title);
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
							checkConfirm();
						} catch (RuntimeException e) {
							Toast.makeText(ShopCheckActivity.this,
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
							Toast.makeText(ShopCheckActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		// 返品主界面取消事件
		((Button) this.findViewById(R.id.newCheckFileButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							newCheckFile();
						} catch (RuntimeException e) {
							Toast.makeText(ShopCheckActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		// 日期
		((EditText) this.findViewById(R.id.checkDateEditText))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							orderDateAction();
						}
						return true;
					}
				});
		// 扫描
		if (scanEditText == null)
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
					Toast.makeText(ShopCheckActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		});
		if (checkNoEditText == null)
			checkNoEditText = ((EditText) this
					.findViewById(R.id.checkNoEditText));
		checkNoEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				try {
					if (keyCode == KeyEvent.KEYCODE_ENTER
							&& event.getAction() == KeyEvent.ACTION_UP) {
						checkNoSearch();
						return true;
					}
					return false;
				} catch (RuntimeException e) {
					Toast.makeText(ShopCheckActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		});
		checkNoEditText.requestFocus();
		if (dataMap == null)
			dataMap = new HashMap<String, Integer>(20);
		dataMap.clear();
		if (dataList == null)
			dataList = new ArrayList(20);
		dataList.clear();
		// 初始化明细的表头
		final ListView listViewItem = (ListView) this
				.findViewById(R.id.detailListView);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout head = (LinearLayout) inflater.inflate(
				R.layout.view_check_detail, null);
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

	// 盘点确认，保存后清空数据
	private void checkConfirm() {
		ListView detailView = ((ListView) this
				.findViewById(R.id.detailListView));
		int count = detailView.getAdapter().getCount();
		if (count <= 1) {
			Toast.makeText(this, R.string.messageNoScan, Toast.LENGTH_LONG)
					.show();
			return;
		}
		String checkNo = this.checkNoEditText.getText().toString().trim();
		if(checkNo.length()==0)
		{
			return;
		}
		//先删除
		try {
			DatabaseHelper.delete(getContentResolver(), CheckOrderTable.COLUMN_CHECK_NO+" = ? and "+CheckOrderTable.COLUMN_WH_NO + " = ?", new String[]{checkNo,FunctionActivity.localShopCode}, CheckOrderTable.class);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// T	// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String checkDate = ((EditText) this
				.findViewById(R.id.checkDateEditText)).getText().toString();
		Date date = new Date();
		Map head = new HashMap();
		if(checkOrderId==null)
			checkOrderId = Util.getUUID();
		head.put(CheckOrderTable.COLUMN_CHECK_ORDER_ID, checkOrderId);
		head.put(CheckOrderTable.COLUMN_CHECK_NO, checkNo);
		head.put(CheckOrderTable.COLUMN_CREATE_DATE,
				Util.dateToString(date, "yyyy-MM-dd HH:mm:ss"));
		head.put(CheckOrderTable.COLUMN_CHECK_DATE, checkDate);
		head.put(CheckOrderTable.COLUMN_WH_NO, FunctionActivity.localShopCode);
		head.put(CheckOrderTable.COLUMN_WH_NAME, FunctionActivity.localShopName);
		head.put(CheckOrderTable.COLUMN_USER_NO, FunctionActivity.userId);
		head.put(CheckOrderTable.COLUMN_USER_NAME, FunctionActivity.userName);
		head.put(CheckOrderTable.COLUMN_STATUS, "START");
		head.put(CheckOrderTable.COLUMN_TOTAL_QTY, new Integer(((TextView) this
				.findViewById(R.id.subTotalTextView)).getText().toString()));
		for (int i = 1; i < count; i++) {
			Map mj = (Map) detailView.getAdapter().getItem(i);
			mj.putAll(head);
			mj.put(CheckOrderTable.COLUMN_CHECK_LINE_NO, i);
			try {
				DatabaseHelper.insert(getContentResolver(), mj, CheckOrderTable.class);
			} catch (IllegalArgumentException e) {
				failProcess(checkNo);
				return;
			} catch (SecurityException e) {
				failProcess(checkNo);
				return;
			} catch (IllegalAccessException e) {
				failProcess(checkNo);
				return;
			} catch (NoSuchFieldException e) {
				failProcess(checkNo);
				return;
			}
		}
		((Button) this.findViewById(R.id.newCheckFileButton)).setEnabled(true);
		((Button) this.findViewById(R.id.confirmButton)).setEnabled(false);
		//clearData();
		Toast.makeText(this, R.string.messageSuccess, Toast.LENGTH_LONG).show();
	}

	// fail -- delete
	private void failProcess(final String uuid) {
		try {
			DatabaseHelper.delete(getContentResolver(),
					CheckOrderTable.COLUMN_CHECK_NO, new String[] { uuid },
					CheckOrderTable.class);
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
		Toast.makeText(this, R.string.messageFail, Toast.LENGTH_LONG).show();
	}

	// 返回到功能界面
	private void backToFunction() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("确认清除吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				clearData();
			}

		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	// 初始化数据组件控制
	private void initContral() {
		((EditText) this
				.findViewById(R.id.localShopEditText)).setText(FunctionActivity.localShopName);
		((EditText) this
				.findViewById(R.id.operatorNameEditText)).setText(FunctionActivity.userName);
		// 默认日期
		EditText orderDateET = ((EditText) this
				.findViewById(R.id.checkDateEditText));
		orderDateET.setText(this.sdf.format(new Date()));
	}

	// 日期选择
	private void orderDateAction() {
		final EditText orderDateET = ((EditText) this
				.findViewById(R.id.checkDateEditText));

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

	// 盘点单号检查
	private void checkNoSearch() {
		String checkNo = this.checkNoEditText.getText().toString().trim();
		if (checkNo.length() == 0) {
			Toast.makeText(this, R.string.messageNoCheckNo, Toast.LENGTH_LONG)
					.show();
			return;
		}
		Button newCheckFileButton = (Button) this.findViewById(R.id.newCheckFileButton);
		newCheckFileButton.setEnabled(false);
		clearData();
		this.checkNoEditText.setText(checkNo);
		// 查询盘点单
		Object[][] datas = null;
		try {
			datas = DatabaseHelper.getMultiColumn(getContentResolver(), new String[]{checkNo}, new String[]{CheckOrderTable.COLUMN_CHECK_NO},null,
					null, new String[]{CheckOrderTable.COLUMN_CHECK_LINE_NO}, null, true, CheckOrderTable.class);
			
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
		
		// 加载盘点数据
		if(datas!=null&&datas.length>0)
		{
			for(int i=0;i<datas.length;i++)
			{
				if(i==0)
				{
					String checkDate = (String) datas[i][DatabaseHelper.getColumnIndex(CheckOrderTable.COLUMN_CHECK_DATE, CheckOrderTable.COLUMNS)];
					if(checkDate==null)
						checkDate = Util.dateToString(new Date(), "yyyy-MM-dd");
					((EditText) this
							.findViewById(R.id.checkDateEditText)).setText(checkDate);
					BigDecimal tq = (BigDecimal)datas[i][DatabaseHelper.getColumnIndex(CheckOrderTable.COLUMN_TOTAL_QTY, CheckOrderTable.COLUMNS)];
					if(tq==null)tq = new BigDecimal(0);
					((TextView) this.findViewById(R.id.subTotalTextView)).setText(tq.intValue()+"");
					checkOrderId = (String) datas[i][DatabaseHelper.getColumnIndex(CheckOrderTable.COLUMN_CHECK_ORDER_ID, CheckOrderTable.COLUMNS)];
				}
				Map m = new HashMap();
				Integer line = (Integer) datas[i][DatabaseHelper.getColumnIndex(CheckOrderTable.COLUMN_CHECK_LINE_NO, CheckOrderTable.COLUMNS)];
				seq = line;
				m.put("seq",line);
				m.put(CheckOrderTable.COLUMN_BARCODE, datas[i][DatabaseHelper.getColumnIndex(CheckOrderTable.COLUMN_BARCODE, CheckOrderTable.COLUMNS)]);
				BigDecimal qty = (BigDecimal)datas[i][DatabaseHelper.getColumnIndex(CheckOrderTable.COLUMN_QTY, CheckOrderTable.COLUMNS)];
				m.put(CheckOrderTable.COLUMN_QTY, qty==null?0:qty.intValue());
				dataList.add(m);
			}
			newCheckFileButton.setEnabled(true);
		}
		// 新盘点单
		else
		{
			checkOrderId = null;
			Toast.makeText(this, R.string.messageNewCheck, Toast.LENGTH_LONG)
					.show();
		}
		this.checkNoEditText.setEnabled(false);
		this.scanEditText.setEnabled(true);
		this.scanEditText.requestFocus();
	}

	// 扫描
	private void barcodeSearch() {
		String str = scanEditText.getText().toString().trim();
		if (str.length() == 0)
			return;
		// Map m = barcodeMap.get(str);
		Integer i = dataMap.get(str);
		Map m = null;
		if (i != null)
			m = dataList.get(i);
		if (m == null) {
			dataMap.put(str, seq);
			seq++;
			m = new HashMap(10, 1F);
			m.put("seq", seq);
			m.put(CheckOrderTable.COLUMN_BARCODE, str);
			m.put(CheckOrderTable.COLUMN_QTY, 1);
			dataList.add(m);
		} else {
			m.put(CheckOrderTable.COLUMN_QTY, (Integer) m.get(CheckOrderTable.COLUMN_QTY) + 1);
		}
		this.listItemAdapter.notifyDataSetChanged();
		countSubTotal();

		scanEditText.setText("");
	}

	// 计算小计
	private void countSubTotal() {
		TextView subTotalTextView = (TextView) this
				.findViewById(R.id.subTotalTextView);
		int q = 0;
		for (Map m : dataList) {
			Integer qty = (Integer) m.get(CheckOrderTable.COLUMN_QTY);
			if (qty != null)
				q += qty;
		}
		subTotalTextView.setText("" + q);
	}

	// 清空数据
	private void clearData() {
		this.checkNoEditText.setEnabled(true);
		this.checkNoEditText.setText("");
		this.checkNoEditText.requestFocus();
		this.scanEditText.setEnabled(false);
		((TextView) this.findViewById(R.id.subTotalTextView)).setText("0");
		if (barcodeMap != null)
			barcodeMap.clear();
		if (barcodeList != null)
			barcodeList.clear();

		if (dataList != null)
			dataList.clear();
		if (dataMap != null)
			dataMap.clear();
		this.listItemAdapter.notifyDataSetChanged();
		// 默认日期
		EditText orderDateET = ((EditText) this
				.findViewById(R.id.checkDateEditText));
		orderDateET.setText(this.sdf.format(new Date()));
		((Button) this.findViewById(R.id.newCheckFileButton)).setEnabled(false);
		((Button) this.findViewById(R.id.confirmButton)).setEnabled(true);
	}
	
	//生成盘点文件
	private void newCheckFile()
	{
		//查询盘点内容
		Object[][] datas = null;
		Object[] deviceMasterObjs = null;
		try {
			datas = DatabaseHelper.getMultiColumn(getContentResolver(), new String[]{this.checkOrderId}, new String[]{CheckOrderTable.COLUMN_CHECK_ORDER_ID},null,
					null, new String[]{CheckOrderTable.COLUMN_CHECK_LINE_NO}, null, true, CheckOrderTable.class);
			deviceMasterObjs = DatabaseHelper.getSingleColumn(
					this.getContentResolver(),
					new Object[] { "9", Util.getLocalDeviceId(this) },
					DeviceMaster.class);
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
		if(datas==null||datas.length==0||datas[0].length<CheckOrderTable.COLUMNS.length)
		{
			Toast.makeText(this, R.string.messageNoData, Toast.LENGTH_LONG)
			.show();
			return;
		}
		String bakpath = (String) DatabaseHelper.getColumnValue(
				deviceMasterObjs, DeviceMaster.COLUMN_FIELD_02,
				DeviceMaster.COLUMNS);
		//生成文件
		BigDecimal tq = (BigDecimal)datas[0][DatabaseHelper.getColumnIndex(CheckOrderTable.COLUMN_TOTAL_QTY, CheckOrderTable.COLUMNS)];
		String checkDate = (String)datas[0][DatabaseHelper.getColumnIndex(CheckOrderTable.COLUMN_CHECK_DATE, CheckOrderTable.COLUMNS)];
		String path = bakpath + File.separatorChar + "check";
		String filename = FunctionActivity.localShopCode + "-check-"+tq+"-" + checkDate + "."
				+ Util.getLocalDeviceId(this);
		try {
			outputFile(path,datas,filename);
			Toast.makeText(this, R.string.messageSuccess, Toast.LENGTH_LONG)
			.show();
		} catch (FileNotFoundException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
			.show();
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
			.show();
		}
	}

	// 输出文件
	private void outputFile(String filepath, Object[][] datas, String filename)
			throws FileNotFoundException, UnsupportedEncodingException {
		File path = new File(filepath);
		if (!path.exists())
			path.mkdirs();
		PrintStream output = null;
		try {
			output = new PrintStream(filepath + File.separatorChar + filename,
					Constant.UTF_8);
			String[] columns = PosTable.COLUMNS;
			for (Object[] data:datas) {
				int j = 0;
				for (Object o:data) {
					if (o == null)
						o = "";
					output.print(o);
					if (j != columns.length - 1) {
						output.print(',');
					}
					j++;
				}
				output.println();
				output.flush();
			}
		} finally {
			if (output != null)
				output.close();
		}
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
				convertView = inflater.inflate(R.layout.view_check_detail, parent,
						false);
				holder = new ViewHolder();
				holder.seqTextView = (TextView) convertView
						.findViewById(R.id.seqTextView);
				holder.barcodeTextView = (TextView) convertView
						.findViewById(R.id.barcodeTextView);
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
			holder.barcodeTextView.setText(data.get(CheckOrderTable.COLUMN_BARCODE)
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
										ShopCheckActivity.this);
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
									m.put(CheckOrderTable.COLUMN_QTY, new Integer(
											inputStr));
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
									m.put("seq", i + 1);
									dataMap.put((String) m
											.get(CheckOrderTable.COLUMN_BARCODE), i);
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
			public TextView barcodeTextView;
			public TextView qtyTextView;
			public LinearLayout linearLayout;
			public LinearLayout item_qty_layout;
			public LinearLayout item_delete_layout;
		}
	}
}
