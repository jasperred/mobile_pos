/**
 * 
 */
package com.netsdl.android.main.view.sale;

import java.math.BigDecimal;
import java.text.ParseException;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netsdl.android.common.Constant;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.DeviceMaster;
import com.netsdl.android.common.db.PaymentMaster;
import com.netsdl.android.common.db.PosTable;
import com.netsdl.android.common.db.SkuMaster;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.customer.EditTextButton;
import com.netsdl.android.main.view.customer.InputDialog;

/**
 * @author jasper
 * 
 */
public class SaleActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener {
	private String userId;
	private String userName;
	private String role;
	private String localShopName;
	private String localShopCode;
	private String custNo;
	private String custName;
	public String saleDate;
	private String offRate = "100";

	private List<Map<String, Object>> dataList;// 保存销售数据
	private Map<String, Integer> dataMap;// 保存barcode在dataList中的位置
	private int seq;// 输入顺序

	private ItemAdapter listItemAdapter;
	private int currentPosition = -1;// 记录明细选择的位置

	private List payMethodList;

	private EditText editSearch;
	private TextView msTextView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale);
		setTitle(R.string.sale_title);
		initData();
		init();
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

		if (editSearch == null)
			editSearch = ((EditTextButton) this.findViewById(R.id.editSearch))
					.getInputEditText();
		if (msTextView == null)
			msTextView = (TextView) this.findViewById(R.id.saleMassageTextView);
		// 初始化数字按钮
		int[] buttons = new int[] { R.id.button0, R.id.checkUploadButton, R.id.uploadCheckFileButton,
				R.id.uploadAllCheckFileButton, R.id.deleteCheckFileButton, R.id.button5, R.id.button6,
				R.id.button7, R.id.button8, R.id.button9 };
		int c = 0;
		for (int i : buttons) {
			final Button button = (Button) this.findViewById(i);
			final int cc = c;
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					buttonInput("" + cc);
				}
			});
			c++;
		}
		// 确认输入，查询条码
		final Button buttonConfirm = (Button) this
				.findViewById(R.id.buttonConfirm);
		buttonConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				procBarCodeSearch();
			}
		});
		// 结算
		final Button buttonPay = (Button) this.findViewById(R.id.buttonPoint);
		buttonPay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (dataList.size() == 0) {
					return;
				}
				PayDialog payDialog = new PayDialog(SaleActivity.this);
				payDialog.setPayMethodList(payMethodList);
				payDialog.setPayable(new BigDecimal(
						((TextView) SaleActivity.this
								.findViewById(R.id.amtTextView)).getText()
								.toString()));
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(
						displayMetrics);

				if (payDialog.showDialog(displayMetrics.widthPixels,
						displayMetrics.heightPixels)) {
					List payResultList = payDialog.getPayResultList();
					try {
						endPay(payResultList);
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
				}
			}
		});
		// 条码扫描
		// final EditText editSearch = ((EditTextButton)this
		// .findViewById(R.id.editSearch)).getInputEditText();
		editSearch.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_UP) {
					procBarCodeSearch();
					return true;
				}
				return false;
			}
		});
		// 商品列表
		final ListView listViewItem = (ListView) this
				.findViewById(R.id.listViewItem);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout head = (LinearLayout) inflater.inflate(
				R.layout.view_sale_detail, null);
		head.setBackgroundColor(Color.LTGRAY);
		head.setBaselineAligned(true);
		listViewItem.addHeaderView(head);
		if (listItemAdapter == null) {
			listItemAdapter = new ItemAdapter(this);
			listViewItem.setAdapter(listItemAdapter);
		}

		listViewItem.setOnItemClickListener(this);
		listViewItem.setOnItemLongClickListener(this);
	}

	// 初始化数据
	private void initData() {
		Bundle data = this.getIntent().getExtras();
		if (data != null) {
			userId = data.getString("userId");
			userName = data.getString("userName");
			role = data.getString("role");
			localShopName = data.getString("localShopName");
			localShopCode = data.getString("localShopCode");
			if (userName != null)
				((TextView) this.findViewById(R.id.checkTypeTextView))
						.setText(userName);
			if (localShopName != null)
				((TextView) this.findViewById(R.id.shopNameTextView))
						.setText(localShopName);
		}

		if (dataMap == null)
			dataMap = new HashMap<String, Integer>(20);
		dataMap.clear();
		if (dataList == null)
			dataList = new ArrayList(20);
		dataList.clear();
		if (payMethodList == null)
			payMethodList = new ArrayList();
		payMethodList.clear();
		try {
			// 销售日期取日结日期
			Object[] deviceMasterObjs = DatabaseHelper.getSingleColumn(
					this.getContentResolver(),
					new Object[] { "1", Util.getLocalDeviceId(this) },
					DeviceMaster.class);
			this.saleDate = (String) DatabaseHelper.getColumnValue(
					deviceMasterObjs, DeviceMaster.COLUMN_FIELD_04,
					DeviceMaster.COLUMNS);
			if (saleDate == null || saleDate.trim().length() == 0)
				saleDate = Util.dateToString(new Date(), "yyyy-MM-dd");
			final EditText slet = ((EditText) this
					.findViewById(R.id.saleDateEditText));
			slet.setText(saleDate);
			slet.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(Util.toDateForString(saleDate,
								"yyyy-MM-dd"));
						DatePickerDialog dd = new DatePickerDialog(
								SaleActivity.this, new OnDateSetListener() {

									public void onDateSet(DatePicker view,
											int year, int monthOfYear,
											int dayOfMonth) {
										Calendar calendar = Calendar
												.getInstance();
										calendar.set(Calendar.YEAR, year);
										calendar.set(Calendar.MONTH,
												monthOfYear);
										calendar.set(Calendar.DAY_OF_MONTH,
												dayOfMonth);
										slet.setText(Util.dateToString(
												calendar.getTime(),
												"yyyy-MM-dd"));
									}
								}, calendar.get(Calendar.YEAR), calendar
										.get(Calendar.MONTH), calendar
										.get(Calendar.DAY_OF_MONTH));
						dd.setTitle(R.string.document_date);
						dd.show();
					}
					return true;

				}
			});
			// 默认客户
			String custom = (String) DatabaseHelper.getColumnValue(
					deviceMasterObjs, DeviceMaster.COLUMN_FIELD_02,
					DeviceMaster.COLUMNS);
			if (custom != null) {
				String[] cts = custom.split(";");
				if (cts != null && cts.length >= 2) {
					this.custNo = cts[0];
					this.custName = cts[1];
				}
			}
			// 支付方式
			Object[][] obj = DatabaseHelper.getMultiColumn(
					this.getContentResolver(), new String[] {},
					new String[] {}, null, null,
					new String[] { PaymentMaster.COLUMN_SORT }, null, true,
					PaymentMaster.class);
			if (obj != null && obj.length > 0) {
				for (Object[] o : obj) {
					Map m = new HashMap();
					Object id = o[DatabaseHelper.getColumnIndex(
							PaymentMaster.COLUMN_ID, PaymentMaster.COLUMNS)];

					m.put(PaymentMaster.COLUMN_ID,
							id == null ? "" : id.toString());
					m.put(PaymentMaster.COLUMN_NAME, o[DatabaseHelper
							.getColumnIndex(PaymentMaster.COLUMN_NAME,
									PaymentMaster.COLUMNS)]);
					m.put(PaymentMaster.COLUMN_SORT, o[DatabaseHelper
							.getColumnIndex(PaymentMaster.COLUMN_SORT,
									PaymentMaster.COLUMNS)]);
					payMethodList.add(m);
				}

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

	// button输入
	private void buttonInput(String str) {
		if (str == null)
			return;
		// final EditText editSearch = (EditText) this
		// .findViewById(R.id.editSearch);
		String t = editSearch.getText().toString().trim();
		t = t + str.trim();
		editSearch.setText(t);
	}

	// 条码扫描
	private void procBarCodeSearch() {
		// final EditText editSearch = (EditText) this
		// .findViewById(R.id.editSearch);
		String str = editSearch.getText().toString().trim();
		if (str.length() == 0)
			return;
		try {
			Object[] objs = DatabaseHelper
					.getSingleColumn(this.getContentResolver(),
							new Object[] { str },
							new String[] { SkuMaster.COLUMN_BAR_CODE },
							SkuMaster.class);
			if (objs != null) {
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
					m.put(SkuMaster.COLUMN_ITEM_NAME, DatabaseHelper
							.getColumnValue(objs, SkuMaster.COLUMN_ITEM_NAME,
									SkuMaster.COLUMNS));
					m.put(SkuMaster.COLUMN_ITEM_CD, DatabaseHelper
							.getColumnValue(objs, SkuMaster.COLUMN_ITEM_CD,
									SkuMaster.COLUMNS));
					m.put(SkuMaster.COLUMN_SKU_CD, DatabaseHelper
							.getColumnValue(objs, SkuMaster.COLUMN_SKU_CD,
									SkuMaster.COLUMNS));
					m.put(SkuMaster.COLUMN_ITEM_STD_PRICE, DatabaseHelper
							.getColumnValue(objs,
									SkuMaster.COLUMN_ITEM_STD_PRICE,
									SkuMaster.COLUMNS));
					m.put(SkuMaster.COLUMN_ITEM_COST, DatabaseHelper
							.getColumnValue(objs, SkuMaster.COLUMN_ITEM_COST,
									SkuMaster.COLUMNS));
					BigDecimal price = (BigDecimal) DatabaseHelper
							.getColumnValue(objs, SkuMaster.COLUMN_ITEM_PRICE,
									SkuMaster.COLUMNS);
					// 未折扣价格
					m.put("oldPrice", price);
					// m.put("qty", 1);
					// if (price != null)
					// m.put("subTotal", price);
					// else
					// m.put("subTotal", new BigDecimal(0));
					// m.put("discount", new BigDecimal(0));// 计算后显示的折扣
					// //
					// 折扣比率和折扣金额只能同时有一个,如果discountRate不为100，则用discountAmt计算，否则用discountAmt计算
					// m.put("discountRate", new BigDecimal(100));// 通过比率计算折扣
					// m.put("discountAmt", new BigDecimal(0));// 通过金额折扣
					dataList.add(m);
					changeItem(m, price, 1, new BigDecimal(100),
							new BigDecimal(0));
				} else {
					BigDecimal price = (BigDecimal) m
							.get(SkuMaster.COLUMN_ITEM_PRICE);
					Integer qty = (Integer) m.get("qty");
					BigDecimal discountRate = (BigDecimal) m
							.get("discountRate");
					BigDecimal discountAmt = (BigDecimal) m.get("discountAmt");
					// 数量加1
					qty++;
					changeItem(m, price, qty, discountRate, discountAmt);
				}
				msTextView.setText((String) m.get(SkuMaster.COLUMN_ITEM_NAME));

			} else {
				Toast.makeText(this, R.string.msg_no_sku, Toast.LENGTH_SHORT)
						.show();
			}
		} catch (NumberFormatException nfe) {
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (NoSuchFieldException e) {
		} finally {
			editSearch.setText("");
		}
	}

	// 修改销售明细
	private void changeItem(Map m, BigDecimal price, Integer qty,
			BigDecimal discountRate, BigDecimal discountAmt) {
		// 合计金额，价格*数量
		BigDecimal subTotal = null;
		if (price != null && qty != null) {
			subTotal = price.multiply(new BigDecimal(qty));
		} else {
			subTotal = new BigDecimal(0);
		}
		if (price.intValue() == price.doubleValue()) {
			price = new BigDecimal(price.intValue());
		}
		// 小计的计算，价格*数量*折扣率-折扣金额
		if (discountRate == null || discountRate.doubleValue() > 100) {
			discountRate = new BigDecimal(100);
		}
		if (discountAmt == null)
			discountAmt = new BigDecimal(0);
		BigDecimal st = subTotal.multiply(discountRate)
				.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP)
				.subtract(discountAmt);
		if (st.intValue() == st.doubleValue()) {
			st = new BigDecimal(st.intValue());
		}
		// 最终折扣金额，合计金额-小计
		BigDecimal discount = subTotal.subtract(st);
		if (discount.intValue() == discount.doubleValue()) {
			discount = new BigDecimal(discount.intValue());
		}
		m.put(SkuMaster.COLUMN_ITEM_PRICE, price);
		m.put("qty", qty);
		m.put("discount", discount);
		m.put("subTotal", st);
		listItemAdapter.notifyDataSetChanged();
		count();
	}

	// 统计信息，合计、数量
	private void count() {
		int qty = 0;
		BigDecimal amt = new BigDecimal(0);
		for (Map m : dataList) {
			Integer q = (Integer) m.get("qty");
			BigDecimal st = (BigDecimal) m.get("subTotal");
			if (q != null)
				qty = qty + q;
			if (st != null)
				amt = amt.add(st);
		}
		amt = Util.round(amt);
		((TextView) this.findViewById(R.id.amtTextView)).setText("" + amt);
		((TextView) this.findViewById(R.id.countQtyTextView)).setText("" + qty);
	}

	// 结单处理
	private void endPay(List<Map> payResultList)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		if (dataList.size() == 0) {
			Toast.makeText(this, R.string.pay_msg1, Toast.LENGTH_LONG).show();
			return;
		}
		String uuid = Util.getUUID();
		String ctime = Util.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
		// 明细保存
		for (Map m : dataList) {
			String[] item = getBaseInsertPosTableString(uuid, ctime,
					Constant.ORDER_FLAG_SKU);
			BigDecimal itemPrice = (BigDecimal) m.get("oldPrice");
			// sku
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_SKU_CD,
					PosTable.COLUMNS)] = (String) m
					.get(SkuMaster.COLUMN_SKU_CD);
			// itemName
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_NAME,
					PosTable.COLUMNS)] = (String) m
					.get(SkuMaster.COLUMN_ITEM_NAME);
			// itemCost
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_COST,
					PosTable.COLUMNS)] = m.get(SkuMaster.COLUMN_ITEM_COST)
					.toString();

			// 标准单价
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_PRICE,
					PosTable.COLUMNS)] = itemPrice.toString();
			// 数量
			Integer qty = (Integer) m.get("qty");
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_QTY,
					PosTable.COLUMNS)] = qty.toString();
			// 原价合计
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_AMT,
					PosTable.COLUMNS)] = itemPrice.multiply(
					new BigDecimal(m.get("qty").toString())).toString();
			// 小计
			BigDecimal subTotal = (BigDecimal) m.get("subTotal");
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_AMT,
					PosTable.COLUMNS)] = subTotal.toString();
			// 日结标记
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_END_DAY,
					PosTable.COLUMNS)] = "0";
			// 扣率，这个扣率根据VIP来计算
			BigDecimal offrate = new BigDecimal(offRate);
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_OFF_RATE,
					PosTable.COLUMNS)] = offrate.toString();
			// 建议销售价
			BigDecimal pStdPrice = itemPrice.multiply(offrate).divide(
					new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_STD_PRICE,
					PosTable.COLUMNS)] = pStdPrice.toString();
			// 销售价
			BigDecimal price = (BigDecimal) m.get(SkuMaster.COLUMN_ITEM_PRICE);
			//计算后的销售价，小计/数量
			BigDecimal pPrice = subTotal.divide(new BigDecimal(qty), 2,
					BigDecimal.ROUND_HALF_UP);
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_PRICE,
					PosTable.COLUMNS)] = pPrice.toString();
			// 折扣
			BigDecimal pDiscount = null;
			if (pStdPrice.doubleValue() == 0)
				pDiscount = new BigDecimal(100);
			else
				pDiscount = pPrice.divide(pStdPrice, 2,
						BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_DISCOUNT,
					PosTable.COLUMNS)] = "" + pDiscount.intValue();
			DatabaseHelper.insert(this.getContentResolver(), item,
					PosTable.class);
		}
		// 支付方式保存
		for (Map m : payResultList) {
			String[] pay = getBaseInsertPosTableString(uuid, ctime,
					Constant.ORDER_FLAG_PAY);
			pay[DatabaseHelper.getColumnIndex(PosTable.COLUMN_SKU_CD,
					PosTable.COLUMNS)] = m.get("id").toString();

			pay[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_NAME,
					PosTable.COLUMNS)] = m.get("payMethod").toString();

			pay[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_COST,
					PosTable.COLUMNS)] = "0";

			pay[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_PRICE,
					PosTable.COLUMNS)] = "0";

			pay[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_PRICE,
					PosTable.COLUMNS)] = "0";

			pay[DatabaseHelper.getColumnIndex(PosTable.COLUMN_QTY,
					PosTable.COLUMNS)] = "1";

			pay[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_AMT,
					PosTable.COLUMNS)] = "0";

			pay[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_AMT,
					PosTable.COLUMNS)] = m.get("money").toString();
			// 日结标记
			pay[DatabaseHelper.getColumnIndex(PosTable.COLUMN_END_DAY,
					PosTable.COLUMNS)] = "0";
			DatabaseHelper.insert(this.getContentResolver(), pay,
					PosTable.class);
		}
		// 清空数据
		clearData();
		Toast.makeText(this, R.string.pay_msg3, Toast.LENGTH_LONG).show();
		msTextView.setText(R.string.pay_msg3);
	}

	// 构造插入销售数据的基本结构
	private String[] getBaseInsertPosTableString(String strUUID,
			String timestamp, String flag) {

		String strs[] = new String[] { strUUID, ((EditText) this
				.findViewById(R.id.saleDateEditText)).getText().toString(), timestamp, "DO",
				"1", this.localShopCode, this.localShopName, this.custNo,
				this.custName, this.userId, this.userName, flag, "", "", "",
				"", "", "", "", "", "0", "0", "0", "0", "" };
		return strs;
	}

	private void clearData() {
		dataList.clear();
		dataMap.clear();
		count();
		seq = 0;
		listItemAdapter.notifyDataSetChanged();
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
				convertView = inflater.inflate(R.layout.view_sale_detail,
						parent, false);
				holder = new ViewHolder();
				holder.seqTextView = (TextView) convertView
						.findViewById(R.id.seqTextView);
				holder.skuCdTextView = (TextView) convertView
						.findViewById(R.id.skuCdTextView);
				holder.itemNameTextView = (TextView) convertView
						.findViewById(R.id.itemNameTextView);
				holder.priceTextView = (TextView) convertView
						.findViewById(R.id.priceTextView);
				holder.qtyTextView = (TextView) convertView
						.findViewById(R.id.qtyTextView);
				holder.subTextView = (TextView) convertView
						.findViewById(R.id.subTextView);
				holder.discountTextView = (TextView) convertView
						.findViewById(R.id.discountTextView);
				holder.linearLayout = (LinearLayout) convertView
						.findViewById(R.id.layout_other);
				holder.item_price_layout = (LinearLayout) convertView
						.findViewById(R.id.item_price_layout);
				holder.item_qty_layout = (LinearLayout) convertView
						.findViewById(R.id.item_qty_layout);
				holder.item_discount_layout = (LinearLayout) convertView
						.findViewById(R.id.item_discount_layout);
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
			holder.priceTextView.setText(data.get(SkuMaster.COLUMN_ITEM_PRICE)
					.toString());
			holder.qtyTextView.setText(data.get("qty").toString());
			holder.subTextView.setText(data.get("subTotal").toString());
			holder.discountTextView.setText(data.get("discount").toString());
			if (position == currentPosition) {
				holder.linearLayout.setVisibility(View.VISIBLE);
				holder.item_price_layout.setClickable(true);
				holder.item_qty_layout.setClickable(true);
				holder.item_discount_layout.setClickable(true);
				holder.item_delete_layout.setClickable(true);
				// 价格调整
				holder.item_price_layout
						.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
								InputDialog inputDialog = new InputDialog(
										SaleActivity.this);
								inputDialog
										.setTitle(R.string.messagePriceChange);
								DisplayMetrics displayMetrics = new DisplayMetrics();
								getWindowManager().getDefaultDisplay()
										.getMetrics(displayMetrics);

								if (inputDialog.showDialog(
										displayMetrics.widthPixels,
										displayMetrics.heightPixels)) {
									String inputStr = inputDialog.getInputStr();
									Map m = dataList.get(currentPosition);
									changeItem(m, Util.round(new BigDecimal(
											inputStr)), (Integer) m.get("qty"),
											(BigDecimal) m.get("discountRate"),
											(BigDecimal) m.get("discountAmt"));
									msTextView
											.setText(R.string.messagePriceChange);
								}
								currentPosition = -1;

							}
						});
				// 数量调整
				holder.item_qty_layout
						.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {

								InputDialog inputDialog = new InputDialog(
										SaleActivity.this);
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
									changeItem(
											m,
											(BigDecimal) m
													.get(SkuMaster.COLUMN_ITEM_PRICE),
											new BigDecimal(inputStr).intValue(),
											(BigDecimal) m.get("discountRate"),
											(BigDecimal) m.get("discountAmt"));
									msTextView
											.setText(R.string.messageQtyChange);
								}
								currentPosition = -1;
							}
						});
				// 折扣调整
				holder.item_discount_layout
						.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {

								InputDialog inputDialog = new InputDialog(
										SaleActivity.this);
								inputDialog.setTitle("输入折扣比例:1~100");
								inputDialog.setInt(true);
								inputDialog.setMax(new BigDecimal(100));
								inputDialog.setMin(new BigDecimal(1));
								DisplayMetrics displayMetrics = new DisplayMetrics();
								getWindowManager().getDefaultDisplay()
										.getMetrics(displayMetrics);

								if (inputDialog.showDialog(
										displayMetrics.widthPixels,
										displayMetrics.heightPixels)) {
									String inputStr = inputDialog.getInputStr();
									Map m = dataList.get(currentPosition);
									changeItem(m, (BigDecimal) m
											.get(SkuMaster.COLUMN_ITEM_PRICE),
											(Integer) m.get("qty"), Util
													.round(new BigDecimal(
															inputStr)),
											new BigDecimal(0));
									msTextView
											.setText(R.string.messageDiscountChange);
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
							}
						});
			} else {
				holder.linearLayout.setVisibility(View.GONE);
				holder.item_price_layout.setClickable(false);
				holder.item_qty_layout.setClickable(false);
				holder.item_discount_layout.setClickable(false);
				holder.item_delete_layout.setClickable(false);
			}

			return convertView;
		}

		class ViewHolder {
			public TextView seqTextView;
			public TextView skuCdTextView;
			public TextView itemNameTextView;
			public TextView priceTextView;
			public TextView qtyTextView;
			public TextView subTextView;
			public TextView discountTextView;
			public LinearLayout linearLayout;
			public LinearLayout item_price_layout;
			public LinearLayout item_qty_layout;
			public LinearLayout item_discount_layout;
			public LinearLayout item_delete_layout;
		}
	}
}
