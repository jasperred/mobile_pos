package com.netsdl.android.main.view;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netsdl.android.common.Constant;
import com.netsdl.android.common.Structs;
import com.netsdl.android.common.Structs.DeviceItem;
import com.netsdl.android.common.Structs.Item;
import com.netsdl.android.common.Structs.Type;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.PaymentMaster;
import com.netsdl.android.common.db.PosTable;
import com.netsdl.android.common.db.SkuMaster;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.list.ItemList;
import com.netsdl.android.main.view.list.PayList;

public class Main {
	public final LayoutInflater inflater;
	public static final int LAYOUT_MAIN = R.layout.main;
	public MainActivity parent;
	ItemList itemList;
	PayList payList;

	Status status = Status.barcode;
	final View view;
	final FrameLayout underInputLayout;
	final LinearLayout linearLayoutBarcodeSearch;
	final LinearLayout linearLayoutPayMethod;
	final EditText editSearch;

	final Button buttonPay;
	final Button buttonConfirm;
	final Button buttonReturn;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Main(MainActivity parent) {
		this.parent = parent;
		itemList = new ItemList(this);
		payList = new PayList(this);

		inflater = LayoutInflater.from(parent);
		view = inflater.inflate(LAYOUT_MAIN, null);

		underInputLayout = (FrameLayout) view
				.findViewById(R.id.underInputLayout);

		linearLayoutBarcodeSearch = (LinearLayout) inflater.inflate(
				R.layout.barcode_search, null);
		linearLayoutPayMethod = (LinearLayout) inflater.inflate(
				R.layout.paymethod, null);

		editSearch = (EditText) view.findViewById(R.id.editSearch);

		buttonPay = (Button) view.findViewById(R.id.buttonPay);
		buttonConfirm = (Button) view.findViewById(R.id.buttonConfirm);
		buttonReturn = (Button) view.findViewById(R.id.buttonReturn);

//		Display dd = parent.getWindowManager().getDefaultDisplay();
//		DisplayMetrics dm = new DisplayMetrics();
//		dd.getMetrics(dm);
//		float m_ScaledDensity = dm.scaledDensity;
//		//String sample = buttonReturn.getText().toString();
//		String sample = "NeedsToFIt";
//		Rect bounds = new Rect();
//		Paint p = new Paint();
//		p.setTypeface(buttonReturn.getTypeface());
//		int maxFont;
//		for (maxFont = 1; -bounds.top <= buttonReturn.getHeight()
//				&& bounds.right <= buttonReturn.getWidth(); maxFont++) {
//			p.setTextSize(maxFont);
//			p.getTextBounds(sample, 0, sample.length(), bounds);
//		}
//		maxFont = (int) ((maxFont - 1) / m_ScaledDensity);
//		buttonReturn.setTextSize(maxFont);
//		Log.d("maxFont", maxFont+"");
//		System.out.println(maxFont);

	}

	public void init() {

		parent.status = MainActivity.Status.Main;

		parent.setContentView(view);
		// parent.setContentView(LAYOUT_MAIN);

		underInputLayout.removeAllViews();
		underInputLayout.addView(linearLayoutBarcodeSearch);

		EditSearchTextWatcher editSearchTextWatcher = new EditSearchTextWatcher(
				parent);
		editSearch.addTextChangedListener(editSearchTextWatcher);

		initEditSearch();

		initNumberKey();

		initButtonPay();

		initLinearLayoutPayMethod();

		itemList.init();
		payList.init();

	}

	private void initNumberKey() {
		int[] buttons = new int[] { R.id.button0, R.id.button1, R.id.button2,
				R.id.button3, R.id.button4, R.id.button5, R.id.button6,
				R.id.button7, R.id.button8, R.id.button9 };
		for (int i : buttons) {
			final Button button = (Button) parent.findViewById(i);

			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					editSearch.setText(editSearch.getText().toString()
							+ Integer.parseInt(button.getText().toString()));
				}
			});
		}

		((Button) parent.findViewById(R.id.buttonConfirm))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (status == Status.barcode) {
							procBarCodeSearch();
						} else {
							underInputLayout.removeAllViews();
							underInputLayout.addView(linearLayoutBarcodeSearch);
							status = Status.barcode;
							editSearch.setText("");
						}

						setButtonPay();

					}

				});

		((Button) parent.findViewById(R.id.buttonReturn))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						parent.preMain.init();
					}
				});

		final Button buttonBack = (Button) parent.findViewById(R.id.buttonBack);

		buttonBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (editSearch.getText().length() <= 0) {

				} else {
					editSearch.setText(editSearch.getText().delete(
							editSearch.getText().length() - 1,
							editSearch.getText().length()));
				}
				setButtonPay();
			}
		});

		final Button buttonClear = (Button) parent
				.findViewById(R.id.buttonClear);
		buttonClear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (editSearch.getText().length() <= 0)
					return;
				editSearch.setText("");
			}
		});
	}

	private void initButtonPay() {

		buttonPay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (status == Status.barcode) {
					underInputLayout.removeAllViews();
					underInputLayout.addView(linearLayoutPayMethod);
					status = Status.payment;
					editSearch.setText("");

				} else {
					//判断是否有商品
					if(parent.mapItem.size()==0)
					{
						Toast.makeText(parent, R.string.pay_msg1,
								Toast.LENGTH_LONG).show();
						return;
					}
					//判断支付是否完成
					if(getTotalItemStdPrice().subtract(
							getTotalPay()).doubleValue()>0)
					{
						Toast.makeText(parent, R.string.pay_msg2,
								Toast.LENGTH_LONG).show();
						return;
					}
					boolean isPrintOK = true;
					try {
						closeThis();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
					} catch (IllegalAccessException e) {
					} catch (NoSuchFieldException e) {
					} catch (IOException e) {
						e.printStackTrace();
						isPrintOK = false;
					} catch (XmlPullParserException e) {
						e.printStackTrace();
						isPrintOK = false;
					}
					if (!isPrintOK) {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								parent);
						alertDialogBuilder.setTitle("小票打印错误");
						alertDialogBuilder.setMessage("请确认打印服务是否良好");
						alertDialogBuilder.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});

						alertDialogBuilder.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
						alertDialogBuilder.setCancelable(false);
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}

					// init();
					parent.mapSkuMaster = new HashMap<Integer, Object[]>();
					// parent.mapPaymentMaster = new HashMap<Integer,
					// Object[]>();
					parent.mapItem = new HashMap<Integer, Item>();
					parent.mapPay = new HashMap<Integer, BigDecimal>();

					underInputLayout.removeAllViews();
					underInputLayout.addView(linearLayoutBarcodeSearch);
					status = Status.barcode;

				}
				setTotal();
				setButtonPay();

			}
		});
	}

	private void initEditSearch() {
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

	}

	public BigDecimal getTotalPay() {
		BigDecimal bigDecimal = new BigDecimal(0);
		for (Entry<Integer, BigDecimal> entry : parent.mapPay.entrySet()) {
			// Integer id = entry.getKey();
			BigDecimal pay = entry.getValue();
			bigDecimal = bigDecimal.add(pay);
		}

		return bigDecimal;
	}

	public BigDecimal getTotalItemStdPrice() {
		BigDecimal bigDecimal = new BigDecimal(0);

		for (Entry<Integer, Item> entry : parent.mapItem.entrySet()) {
			Item item = entry.getValue();

			// bigDecimal = bigDecimal.add(((BigDecimal) parent.mapSkuMaster
			// .get(skuID)[parent.skuMaster
			// .getColumnIndex(SkuMaster.COLUMN_ITEM_STD_PRICE)])
			// .multiply(new BigDecimal(item.count)));

			bigDecimal = bigDecimal.add(item.lumpSum);

		}

		return bigDecimal;
	}

	private void procBarCodeSearch() {
		String str = editSearch.getText().toString();
		if (str.length() == 0)
			return;
		try {
			Object[] objs = DatabaseHelper
					.getSingleColumn(parent.getContentResolver(),
							new Object[] { str },
							new String[] { SkuMaster.COLUMN_BAR_CODE },
							SkuMaster.class);

			// Object[] objs = parent.skuMaster.getSingleColumn(
			// new Object[] { str },
			// new String[] { SkuMaster.COLUMN_BAR_CODE });

			if (objs != null) {

				Integer skuId = (Integer) DatabaseHelper.getColumnValue(objs,
						SkuMaster.COLUMN_SKU_ID, SkuMaster.COLUMNS);

				if (!parent.mapSkuMaster.containsKey(skuId)) {
					parent.mapSkuMaster.put(skuId, objs);
				}
				if (!parent.mapItem.containsKey(skuId)) {
					if (parent.mapItem.size() <= 10) {
						Item item = new Structs().new Item();
						item.count = 1;
						//itemPrice
						BigDecimal itemPrice = (BigDecimal) DatabaseHelper
						.getColumnValue(objs,
								SkuMaster.COLUMN_ITEM_PRICE,
								SkuMaster.COLUMNS);
						//根据itemPrice计算VIP折扣
						String or = null;
						if(parent.deviceItem.custom!=null&&parent.deviceItem.custom.length>2&&parent.deviceItem.custom[2]!=null)
							or = parent.deviceItem.custom[2];
						else
							or = "100";
						itemPrice = itemPrice.multiply(new BigDecimal(or).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_UP));
						item.price = itemPrice;
						item.oldPrice = itemPrice;
						item.itemDiscount = 100;
						item.lumpSum = item.price.multiply(new BigDecimal(
								item.count));
						parent.mapItem.put(skuId, item);
					} else {
						Toast.makeText(parent, R.string.msg_item_over_10,
								Toast.LENGTH_SHORT).show();
						editSearch.setText("");
					}

				} else {
					Item item = parent.mapItem.get(skuId);
					item.count += 1;
					//itemPrice
					BigDecimal itemPrice = (BigDecimal) DatabaseHelper
					.getColumnValue(objs,
							SkuMaster.COLUMN_ITEM_PRICE,
							SkuMaster.COLUMNS);
					//根据itemPrice计算VIP折扣
					String or = null;
					if(parent.deviceItem.custom!=null&&parent.deviceItem.custom.length>2&&parent.deviceItem.custom[2]!=null)
						or = parent.deviceItem.custom[2];
					else
						or = "100";
					itemPrice = itemPrice.multiply(new BigDecimal(or).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_UP));
					item.price = itemPrice;
					item.oldPrice = itemPrice;
					item.itemDiscount = 100;
					item.lumpSum = item.price.multiply(new BigDecimal(
							item.count));

					parent.mapItem.put(skuId, item);
				}

				listViewNotifyDataSetChanged(R.id.listViewItem);

				setTotal();
				editSearch.setText("");

				setButtonPay();

				// Log.d("textView.getText()", textView.getText().toString());
			} else {
				Toast.makeText(parent, R.string.msg_no_sku, Toast.LENGTH_SHORT)
						.show();
				editSearch.setText("");
			}
		} catch (NumberFormatException nfe) {
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (NoSuchFieldException e) {
		}
	}

	public void setButtonPay() {
		if (status == Status.barcode) {
			buttonPay.setText(R.string.Pay);
			buttonPay.setEnabled(parent.mapItem.size() > 0);
			buttonConfirm.setText(R.string.Confirm);
			buttonConfirm
					.setEnabled(editSearch.getText().toString().length() > 0);
		} else {
			buttonPay.setText(R.string.Close);
			buttonPay.setEnabled(parent.mapPay.size() > 0);
			buttonConfirm.setText(R.string.Return);
			buttonConfirm.setEnabled(true);
		}

	}

	//支付button初始化
	private void initLinearLayoutPayMethod() {
		//最多8个button
		int[] buttonPays = new int[] { R.id.buttonPay1, R.id.buttonPay2,
				R.id.buttonPay3, R.id.buttonPay4, R.id.buttonPay5,
				R.id.buttonPay6, R.id.buttonPay7, R.id.buttonPay8 };

		// final Object[][] objss = parent.paymentMaster.getMultiColumn(
		// new String[] {}, new String[] {}, null, null,
		// new String[] { PaymentMaster.COLUMN_SORT }, null, true);

		try {
			final Object[][] objss = DatabaseHelper.getMultiColumn(
					parent.getContentResolver(), new String[] {},
					new String[] {}, null, null,
					new String[] { PaymentMaster.COLUMN_SORT }, null, true,
					PaymentMaster.class);
			for (int i = 0; i < buttonPays.length; i++) {
				Button buttonPay = (Button) linearLayoutPayMethod
						.findViewById(buttonPays[i]);
				if (i < objss.length) {

					buttonPay.setText((String) DatabaseHelper.getColumnValue(
							objss[i], PaymentMaster.COLUMN_NAME,
							PaymentMaster.COLUMNS));

					final Integer id = (Integer) DatabaseHelper.getColumnValue(
							objss[i], PaymentMaster.COLUMN_ID,
							PaymentMaster.COLUMNS);
					if(id==null)
						continue;
					parent.mapPaymentMaster.put(id, objss[i]);

					buttonPay.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							if (editSearch.getText().toString().trim().length() <= 0)
								return;
							parent.mapPay.put(id, new BigDecimal(editSearch
									.getText().toString()));
							editSearch.setText("");

							listViewNotifyDataSetChanged(R.id.listViewPay);

							setTotal();

							setButtonPay();
						}
					});
				} else {
					buttonPay.setText("");
					buttonPay.setEnabled(false);
				}

			}

		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (NoSuchFieldException e) {
		}

	}

	//结单处理
	private void closeThis() throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException,
			IOException, XmlPullParserException {
		// String strUUID = Util.getUUID();
		String strUUID = Util.getUUID();

		// Object[] objPos = DatabaseHelper.getSingleColumn(
		// parent.getContentResolver(), PosTable.class);

		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		String timestamp = sdf.format(now.getTime());
		StringBuffer sbItems = new StringBuffer();
		//销售明细
		for (Entry<Integer, Item> entry : parent.mapItem.entrySet()) {
			Integer id = entry.getKey();
			Item item = entry.getValue();

			Object[] objs = DatabaseHelper.getSingleColumn(
					parent.getContentResolver(), new Object[] { id },
					new String[] { SkuMaster.COLUMN_SKU_ID }, SkuMaster.class);

			if (objs != null) {
				DatabaseHelper.insert(
						parent.getContentResolver(),
						getInsertPosTableString(strUUID, timestamp,
								parent.type, parent.deviceItem, item, objs),
						PosTable.class);

				sbItems.append((String) DatabaseHelper.getColumnValue(objs,
						SkuMaster.COLUMN_SKU_CD, SkuMaster.COLUMNS));
				sbItems.append(":");
				sbItems.append((String) DatabaseHelper.getColumnValue(objs,
						SkuMaster.COLUMN_ITEM_CAT_NAME, SkuMaster.COLUMNS));
				sbItems.append(":");

				sbItems.append(item.price);
				sbItems.append(":");
				sbItems.append(item.count);
				sbItems.append(":");
				sbItems.append(item.lumpSum);
				sbItems.append(";");
			}
		}

		StringBuffer sbPays = new StringBuffer();
		//支付方式
		for (Entry<Integer, BigDecimal> entry : parent.mapPay.entrySet()) {
			Integer id = entry.getKey();
			BigDecimal count = entry.getValue();

			Object[] objs = DatabaseHelper.getSingleColumn(
					parent.getContentResolver(), new Object[] { id },
					new String[] { PaymentMaster.COLUMN_ID },
					PaymentMaster.class);
			if (objs != null) {
				DatabaseHelper.insert(
						parent.getContentResolver(),
						getInsertPosTableString(strUUID, timestamp,
								parent.type, parent.deviceItem, count, objs),
						PosTable.class);
				sbPays.append(count.toString());
				sbPays.append(";");
			}

		}

		//找零
		String strs[] = getInsertPosTableString(strUUID, timestamp,
				parent.type, parent.deviceItem, false);

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_SKU_CD,
				PosTable.COLUMNS)] = Constant.ORDER_PAY_CHANGE_CD;

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_NAME,
				PosTable.COLUMNS)] = Constant.ORDER_PAY_CHANGE_NAME;

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_COST,
				PosTable.COLUMNS)] = "0";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_PRICE,
				PosTable.COLUMNS)] = "0";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_PRICE,
				PosTable.COLUMNS)] = "0";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_QTY,
				PosTable.COLUMNS)] = "1";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_AMT,
				PosTable.COLUMNS)] = "0";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_AMT,
				PosTable.COLUMNS)] = getTotalItemStdPrice().subtract(
				getTotalPay()).toString();
		DatabaseHelper
				.insert(parent.getContentResolver(), strs, PosTable.class);

		if (Boolean.valueOf(parent.deviceItem.printFlagIn))
			printThis(strUUID, timestamp, sbItems.toString(), sbPays.toString());

	}

	private void printThis(String strUUID, String timestamp, String items,
			String pays) throws IOException, XmlPullParserException {
		if (parent.deviceItem.printWSDL == null
				|| parent.deviceItem.printWSDL.trim().length() == 0)
			return;

		StringBuffer sbHead = new StringBuffer();

		sbHead.append(parent.deviceItem.shop[1]);
		sbHead.append(";");

		sbHead.append(strUUID);
		sbHead.append(";");

		sbHead.append(timestamp);
		sbHead.append(";");

		sbHead.append(parent.deviceItem.operator[1]);
		sbHead.append(";");

		sbHead.append(parent.deviceItem.deviceID);

		SoapObject rpc = new SoapObject(parent.deviceItem.printNameSpace,
				parent.deviceItem.printMethod);

		rpc.addProperty("heads", sbHead.toString());
		rpc.addProperty("items", items);
		rpc.addProperty("pays", pays);
		rpc.addProperty("remarks", parent.deviceItem.remarks);
		rpc.addProperty("isPrintOut",
				Boolean.valueOf(parent.deviceItem.printFlagOut));
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER12);

		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE httpTransportSE = new HttpTransportSE(
				parent.deviceItem.printWSDL);
		httpTransportSE.debug = true;
		httpTransportSE.call(parent.deviceItem.printNameSpace, envelope);
		// SoapObject result = (SoapObject) envelope.getResponse();
		Object object = (Object) envelope.getResponse();
	}

	private String[] getInsertPosTableString(String strUUID, String timestamp,
			Type type, DeviceItem deviceItem, Item item, Object[] objs) {
		String strs[] = getInsertPosTableString(strUUID, timestamp, type,
				deviceItem, true);
		BigDecimal itemPrice = (BigDecimal) DatabaseHelper
		.getColumnValue(objs, SkuMaster.COLUMN_ITEM_PRICE,
				SkuMaster.COLUMNS);
		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_SKU_CD,
				PosTable.COLUMNS)] = (String) DatabaseHelper.getColumnValue(
				objs, SkuMaster.COLUMN_SKU_CD, SkuMaster.COLUMNS);

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_NAME,
				PosTable.COLUMNS)] = (String) DatabaseHelper.getColumnValue(
				objs, SkuMaster.COLUMN_ITEM_NAME, SkuMaster.COLUMNS);

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_COST,
				PosTable.COLUMNS)] = ((BigDecimal) DatabaseHelper
				.getColumnValue(objs, SkuMaster.COLUMN_ITEM_COST,
						SkuMaster.COLUMNS)).toString();

		//标准单价
		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_PRICE,
				PosTable.COLUMNS)] = itemPrice.toString();

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_PRICE,
				PosTable.COLUMNS)] = item.price.toString();

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_QTY,
				PosTable.COLUMNS)] = item.count.toString();

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_AMT,
				PosTable.COLUMNS)] = ((BigDecimal) DatabaseHelper
				.getColumnValue(objs, SkuMaster.COLUMN_ITEM_PRICE,
						SkuMaster.COLUMNS))
				.multiply(new BigDecimal(item.count)).toString();

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_AMT,
				PosTable.COLUMNS)] = item.lumpSum.toString();
		//日结标记
		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_END_DAY,
				PosTable.COLUMNS)] = "0";
		//扣率，这个扣率根据VIP来计算
		String or = null;
		if(parent.deviceItem.custom!=null&&parent.deviceItem.custom.length>2&&parent.deviceItem.custom[2]!=null)
			or = parent.deviceItem.custom[2];
		else
			or = "100";
		BigDecimal offrate = new BigDecimal(or);
		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_OFF_RATE,
				PosTable.COLUMNS)] = offrate.toString();
		//销售价
		//BigDecimal pPrice = item.price.multiply(new BigDecimal(item.itemDiscount)).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_UP);
		BigDecimal pPrice = item.price;
		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_PRICE,
				PosTable.COLUMNS)] = pPrice.toString();
		//建议销售价
		BigDecimal pStdPrice = itemPrice.multiply(offrate).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_UP);
		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_STD_PRICE,
				PosTable.COLUMNS)] =pStdPrice.toString();
		//折扣
		BigDecimal pDiscount = null;
		if(pStdPrice.doubleValue()==0)
			pDiscount = new BigDecimal(100);
		else
			pDiscount = pPrice.divide(pStdPrice,2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_DISCOUNT,
				PosTable.COLUMNS)] = ""+pDiscount.intValue();

		return strs;

	}

	private String[] getInsertPosTableString(String strUUID, String timestamp,
			Type type, DeviceItem deviceItem, BigDecimal count, Object[] objs) {
		String strs[] = getInsertPosTableString(strUUID, timestamp, type,
				deviceItem, false);

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_SKU_CD,
				PosTable.COLUMNS)] = ((Integer) DatabaseHelper.getColumnValue(
				objs, PaymentMaster.COLUMN_ID, PaymentMaster.COLUMNS))
				.toString();

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_NAME,
				PosTable.COLUMNS)] = (String) DatabaseHelper.getColumnValue(
				objs, PaymentMaster.COLUMN_NAME, PaymentMaster.COLUMNS);

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_ITEM_COST,
				PosTable.COLUMNS)] = "0";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_PRICE,
				PosTable.COLUMNS)] = "0";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_PRICE,
				PosTable.COLUMNS)] = "0";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_QTY,
				PosTable.COLUMNS)] = "1";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_S_AMT,
				PosTable.COLUMNS)] = "0";

		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_P_AMT,
				PosTable.COLUMNS)] = count.toString();
		//日结标记
		strs[DatabaseHelper.getColumnIndex(PosTable.COLUMN_END_DAY,
				PosTable.COLUMNS)] = "0";

		return strs;

	}

	private String[] getInsertPosTableString(String strUUID, String timestamp,
			Type type, DeviceItem deviceItem, boolean isItem) {

		String strs[] = new String[] { strUUID, parent.deviceItem.documentDate,
				timestamp, parent.type.toDocumentType().toString(),
				parent.type.toRtnType().toString(), parent.deviceItem.shop[0],
				parent.deviceItem.shop[1], parent.deviceItem.custom[0],
				parent.deviceItem.custom[1], parent.deviceItem.operator[0],
				parent.deviceItem.operator[1],
				isItem ? Constant.ORDER_FLAG_SKU : Constant.ORDER_FLAG_PAY, "",
				"", "", "", "", "", "", "","0", "0", "0", "0" };
		return strs;
	}

	class EditSearchTextWatcher implements TextWatcher {
		Context context;

		public EditSearchTextWatcher(Context context) {
			this.context = context;
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			setButtonPay();

		}

		public void afterTextChanged(Editable s) {

		}

	}

	public void listViewNotifyDataSetChanged(int id) {
		ListView listView = (ListView) parent.findViewById(id);
		((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
	}

	public void setTotal() {
		((TextView) parent.findViewById(R.id.all_item_fee))
				.setText(getTotalItemStdPrice().toString());

		((TextView) parent.findViewById(R.id.all_pay_fee))
				.setText(getTotalPay().toString());

		((TextView) parent.findViewById(R.id.textViewQty))
				.setText(getTotalPay().subtract(getTotalItemStdPrice())
						.toString());

	}

	public enum Status {
		barcode, payment
	}

}
