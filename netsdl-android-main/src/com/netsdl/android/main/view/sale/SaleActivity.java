/**
 * 
 */
package com.netsdl.android.main.view.sale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

/**
 * @author jasper
 * 
 */
public class SaleActivity extends Activity {
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

	private SimpleAdapter listItemAdapter;

	private List payMethodList;
	
	private EditText editSearch;

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

		if(editSearch==null)
			editSearch = ((EditTextButton)this
					.findViewById(R.id.editSearch)).getInputEditText();
		// 初始化数字按钮
		int[] buttons = new int[] { R.id.button0, R.id.button1, R.id.button2,
				R.id.button3, R.id.button4, R.id.button5, R.id.button6,
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
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); 
				
				if (payDialog.showDialog(displayMetrics.widthPixels,displayMetrics.heightPixels)) {
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
//		final EditText editSearch = ((EditTextButton)this
//				.findViewById(R.id.editSearch)).getInputEditText();
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
			listItemAdapter = new SimpleAdapter(listViewItem.getContext(),
					dataList, R.layout.view_sale_detail, new String[] { "seq",
							SkuMaster.COLUMN_ITEM_NAME,
							SkuMaster.COLUMN_ITEM_PRICE, "qty", "subTotal",
							"discount" }, new int[] { R.id.seqTextView,
							R.id.itemNameTextView, R.id.priceTextView,
							R.id.qtyTextView, R.id.subTextView,
							R.id.discountTextView });
			listViewItem.setAdapter(listItemAdapter);
		}
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
				((TextView) this.findViewById(R.id.salemanTextView))
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
			((TextView) this.findViewById(R.id.saleDateTextView))
					.setText(saleDate);
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

	// button输入
	private void buttonInput(String str) {
		if (str == null)
			return;
//		final EditText editSearch = (EditText) this
//				.findViewById(R.id.editSearch);
		String t = editSearch.getText().toString().trim();
		t = t + str.trim();
		editSearch.setText(t);
	}

	// 条码扫描
	private void procBarCodeSearch() {
//		final EditText editSearch = (EditText) this
//				.findViewById(R.id.editSearch);
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
				((TextView) this.findViewById(R.id.saleMassageTextView))
						.setText((String) m.get(SkuMaster.COLUMN_ITEM_NAME));

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
			// 销售价
			BigDecimal pPrice = (BigDecimal) m.get(SkuMaster.COLUMN_ITEM_PRICE);
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_PRICE,
					PosTable.COLUMNS)] = pPrice.toString();
			// 数量
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_QTY,
					PosTable.COLUMNS)] = m.get("qty").toString();
			// 原价合计
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_AMT,
					PosTable.COLUMNS)] = itemPrice.multiply(
					new BigDecimal(m.get("qty").toString())).toString();
			// 小计
			item[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_AMT,
					PosTable.COLUMNS)] = m.get("subTotal").toString();
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
	}

	// 构造插入销售数据的基本结构
	private String[] getBaseInsertPosTableString(String strUUID,
			String timestamp, String flag) {

		String strs[] = new String[] { strUUID, this.saleDate, timestamp, "DO",
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
}
