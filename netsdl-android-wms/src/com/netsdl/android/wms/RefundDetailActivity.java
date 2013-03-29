package com.netsdl.android.wms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.netsdl.android.Util;
import com.netsdl.android.db.DeviceMaster;
import com.netsdl.android.db.Order;
import com.netsdl.android.db.SkuMaster;
import com.netsdl.android.db.UserMaster;

public class RefundDetailActivity extends Activity {

	private String custNo;
	private String custName;
	private String invType;
	private String refundDate;
	private String orderType;
	private Integer rtn;
	private String whNo;
	private String whName;
	private String userNo;
	private String userName;
	private Map<String, Map> barcodeMap;
	private List<String> barcodeList;

	private SkuMaster sm;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				showMainActivity();
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = this.getIntent().getExtras();
		setContentView(R.layout.activity_refund_detail);
		custNo = data.getString("custNo");
		custName = data.getString("custName");
		invType = data.getString("invType");
		refundDate = data.getString("refundDate");
		rtn = data.getInt("rtn");
		whNo = data.getString("whNo");
		whName = data.getString("whName");
		userNo = data.getString("userNo");
		userName = data.getString("userName");
		orderType = data.getString("orderType");
		init();
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
							Intent gotoIntent = new Intent(
									RefundDetailActivity.this,
									RefundMainActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("whNo", whNo);
							bundle.putString("whName", whName);
							bundle.putString("userNo", userNo);
							bundle.putString("userName", userName);
							gotoIntent.putExtras(bundle);
							RefundDetailActivity.this.startActivity(gotoIntent);
						} catch (RuntimeException e) {
							Toast.makeText(RefundDetailActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		((Button) this.findViewById(R.id.confirmButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							saveRefund();
						} catch (RuntimeException e) {
							Toast.makeText(RefundDetailActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});

		EditText barcodeSconEditText = (EditText) this
				.findViewById(R.id.barcodeScanEditText);
		barcodeSconEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				try {
					if (keyCode == KeyEvent.KEYCODE_ENTER
							&& event.getAction() == KeyEvent.ACTION_UP) {
						barcodeSearch();
						return true;
					}
					return false;
				} catch (RuntimeException e) {
					Toast.makeText(RefundDetailActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		});
		barcodeSconEditText.setFocusable(true);
		sm = new SkuMaster(this);

		ListView detailView = ((ListView) this
				.findViewById(R.id.detailListView));
		if (detailView.getHeaderViewsCount() == 0) {
			LinearLayout lin = new LinearLayout(this);
			TextView skuTextView = new TextView(this);
			skuTextView.setGravity(Gravity.CENTER);
			skuTextView.setTextSize(30);
			skuTextView.setText("SKU编码");
			TextView itemTextView = new TextView(this);
			itemTextView.setGravity(Gravity.CENTER);
			itemTextView.setTextSize(30);
			itemTextView.setText("商品编码");
			TextView titleTextView = new TextView(this);
			titleTextView.setGravity(Gravity.CENTER);
			titleTextView.setTextSize(30);
			titleTextView.setText("商品名称");
			TextView qtyTextView = new TextView(this);
			qtyTextView.setGravity(Gravity.CENTER);
			qtyTextView.setTextSize(30);
			qtyTextView.setText("数量");
			lin.addView(skuTextView, 0, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT, 1));
			lin.addView(itemTextView, 1, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT, 2));
			lin.addView(titleTextView, 2, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT, 3));
			lin.addView(qtyTextView, 3, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.FILL_PARENT, 4));
			detailView.addHeaderView(lin);
		}
		// 初始化时Detail增加一条空记录，否则头无法显示
		SimpleAdapter listItemAdapter = new SimpleAdapter(
				detailView.getContext(), new ArrayList(), R.layout.view_detail,
				new String[] { SkuMaster.COLUMN_SKU_CD,
						SkuMaster.COLUMN_ITEM_CD, SkuMaster.COLUMN_ITEM_NAME,
						"qty" }, new int[] { R.id.skuCdTextView,
						R.id.itemCdTextView, R.id.itemNameTextView,
						R.id.qtyTextView });
		detailView.setAdapter(listItemAdapter);
		if (barcodeMap == null)
			barcodeMap = new HashMap();
		barcodeMap.clear();
		if (barcodeList == null)
			barcodeList = new ArrayList();
		barcodeList.clear();
	}

	private void barcodeSearch() {
		EditText barcodeSconEditText = (EditText) this
				.findViewById(R.id.barcodeScanEditText);
		String str = barcodeSconEditText.getText().toString().trim();
		if (str.length() == 0)
			return;
		List<Map> sl = sm.find(new String[] { SkuMaster.COLUMN_SKU_CD,
				SkuMaster.COLUMN_ITEM_CD, SkuMaster.COLUMN_ITEM_NAME },
				SkuMaster.COLUMN_BAR_CODE + " = ? ", new String[] { str });

		// 未找到商品
		if (sl == null || sl.size() == 0) {
			Toast.makeText(this, R.string.messageNoSku, Toast.LENGTH_LONG)
					.show();
		} else {
			Map s = sl.get(0);
			Map m = barcodeMap.get(str);
			if (m == null) {
				s.put(Order.COLUMN_QTY, 1);
				barcodeMap.put(str, s);
				barcodeList.add(str);
			} else {
				m.put(Order.COLUMN_QTY, (Integer) m.get(Order.COLUMN_QTY) + 1);
			}
			ListView detailView = ((ListView) this
					.findViewById(R.id.detailListView));
			List l = new ArrayList();
			for (String b : barcodeList) {
				l.add(barcodeMap.get(b));
			}
			SimpleAdapter listItemAdapter = new SimpleAdapter(
					detailView.getContext(), l, R.layout.view_detail,
					new String[] { SkuMaster.COLUMN_SKU_CD,
							SkuMaster.COLUMN_ITEM_CD,
							SkuMaster.COLUMN_ITEM_NAME, "qty" }, new int[] {
							R.id.skuCdTextView, R.id.itemCdTextView,
							R.id.itemNameTextView, R.id.qtyTextView });
			detailView.setAdapter(listItemAdapter);
		}
		barcodeSconEditText.setText("");
	}

	// 保存退货单
	private void saveRefund() {
		ListView detailView = ((ListView) this
				.findViewById(R.id.detailListView));
		int count = detailView.getAdapter().getCount();
		if (count <=1) {
			Toast.makeText(this, R.string.messageNoScan, Toast.LENGTH_LONG)
					.show();
			return;
		}
		Date date = new Date();
		final String uuid = Util.getUUID();
		String deviceId = Util.getLocalDeviceId(this);
		Map head = new HashMap();
		head.put(Order.COLUMN_ORDER_NO, uuid);
		head.put(Order.COLUMN_DEVICE_ID, deviceId);
		head.put(Order.COLUMN_CREATE_DATE,
				Util.dateToString(date, "yyyy-MM-dd HH:mm:ss"));
		head.put(Order.COLUMN_ORDER_DATE, this.refundDate);
		head.put(Order.COLUMN_WH_NO, this.whNo);
		head.put(Order.COLUMN_WH_NAME, this.whName);
		head.put(Order.COLUMN_CUST_NO, this.custNo);
		head.put(Order.COLUMN_CUST_NAME, this.custName);
		head.put(Order.COLUMN_USER_NO, this.userNo);
		head.put(Order.COLUMN_USER_NAME, this.userName);
		head.put(Order.COLUMN_RTN, this.rtn);
		head.put(Order.COLUMN_FLAG, "INSERT");
		head.put(Order.COLUMN_ORDER_TYPE, this.orderType);
		head.put(Order.COLUMN_INV_TYPE, this.invType);
		Order order = new Order(this);

		for (int i = 1; i < count; i++) {
			Map mj = (Map) detailView.getAdapter().getItem(i);
			mj.putAll(head);
			order.insert(mj, null);
		}

		final ProgressDialog progressDialog = ProgressDialog.show(this,
				this.getString(R.string.waiting),
				this.getString(R.string.Uploading), true);
		new Thread(new Runnable() {
			@Override
			public void run() {

				processUploadData(uuid);
				progressDialog.dismiss();
				handler.sendEmptyMessage(1);
			}
		}).start();
	}

	// 生成文件上传数据
	private void processUploadData(String uuid) {
		Order order = new Order(this);
		// 生成文件
		List<Map> orderList = order.find(Order.COLUMNS, Order.COLUMN_ORDER_NO
				+ " = ?", new String[] { uuid });
		if (orderList == null || orderList.size() == 0) {
			Toast.makeText(this, R.string.messageFail, Toast.LENGTH_LONG)
					.show();
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		String timestamp = sdf.format(now.getTime());

		String localDeviceId = Util.getLocalDeviceId(this);
		String filepath = this.getFilesDir().getPath().toString()
				+ File.separatorChar + "data";
		File dir = new File(filepath);
		if (!dir.exists())
			dir.mkdirs();
		String filename = localDeviceId + "_refund" + "." + timestamp;
		PrintStream output = null;
		FileInputStream input = null;
		try {
			output = new PrintStream(filepath + File.separatorChar + filename,
					"utf-8");
			String[] cls = Order.COLUMNS;
			for (Map m : orderList) {
				StringBuffer str = new StringBuffer();
				for (int j = 0; j < cls.length; j++) {
					if (cls[j].equals(Order.COLUMN_FLAG))
						continue;
					str.append(m.get(cls[j]));
					if (j != cls.length - 1) {
						str.append(',');
					}
				}
				output.print(str);
				output.println();
				output.flush();
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 更新Flag
		Map updateOrder = new HashMap();
		updateOrder.put(Order.COLUMN_FLAG, "UPDATED");
		order.update(updateOrder, Order.COLUMN_ORDER_NO + " = ?",
				new String[] { uuid });
		// 上传文件
		DeviceMaster dm = new DeviceMaster(this);
		List<Map> dl = dm.find(new String[] { DeviceMaster.COLUMN_FIELD_02,
				DeviceMaster.COLUMN_FIELD_03, DeviceMaster.COLUMN_FIELD_13,
				DeviceMaster.COLUMN_FIELD_14, DeviceMaster.COLUMN_FIELD_15,
				DeviceMaster.COLUMN_FIELD_16 }, DeviceMaster.COLUMN_DEVICE_ID
				+ " = ? and " + DeviceMaster.COLUMN_INIT_ID + " = ?",
				new String[] { Util.getLocalDeviceId(this), "10" });
		if (dl != null && dl.size() > 0) {
			Map d = dl.get(0);
			String ftpUrl = (String) d.get(DeviceMaster.COLUMN_FIELD_13);
			String ftpPath = (String) d.get(DeviceMaster.COLUMN_FIELD_14);
			String ftpUser = (String) d.get(DeviceMaster.COLUMN_FIELD_15);
			String ftpPassword = (String) d.get(DeviceMaster.COLUMN_FIELD_16);
			Util.ftpUpload(filepath, filename, ftpUrl, ftpPath, ftpUser,
					ftpPassword);
			String bakPath = (String) d.get(DeviceMaster.COLUMN_FIELD_02);
			String bakDay = (String) d.get(DeviceMaster.COLUMN_FIELD_03);
			// 备份文件
			bakup(filepath, filename, bakPath, bakDay);
		}
	}

	// 返回到主界面
	private void showMainActivity() {
		Intent gotoIntent = new Intent(RefundDetailActivity.this,
				RefundMainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("whNo", whNo);
		bundle.putString("whName", whName);
		bundle.putString("userNo", userNo);
		bundle.putString("userName", userName);
		gotoIntent.putExtras(bundle);
		RefundDetailActivity.this.startActivity(gotoIntent);
	}

	// 备份数据，并删除多余备份
	private void bakup(String filepath, String filename, String bakPath,
			String bakDay) {

		// 无备份路径不备份文件，备份路径无法建立也不备份文件
		if (bakPath == null || bakPath.trim().length() == 0)
			return;
		bakPath = bakPath.trim();
		File bakDir = new File(bakPath);
		try {
			if (!bakDir.exists())
				bakDir.mkdirs();
		} catch (Exception e) {
			return;
		}
		int copyFile = Util.CopyFile(filepath + File.separatorChar + filename,
				bakPath + File.separatorChar + filename);
		// 备份文件保留，默认30天
		if (bakDay == null || bakDay.trim().length() == 0)
			bakDay = "30";
		Integer bd = null;
		try {
			bd = new Integer(bakDay);
		} catch (Exception e) {
			bd = new Integer(30);
		}
		File[] bakFiles = bakDir.listFiles();
		if (bakFiles != null && bakFiles.length > bd) {
			List<File> fl = new ArrayList();
			for (File f : bakFiles)
				fl.add(f);
			// 排序
			Collections.sort(fl, new Comparator<File>() {

				public int compare(File o1, File o2) {
					return o1.getName().compareToIgnoreCase(o2.getName()) * -1;
				}
			});
			// 按日期和数量比较，大于bd的删除
			Map<String, String> fm = new HashMap();
			for (File f : fl) {
				String sn = f.getName().substring(
						f.getName().lastIndexOf(".") + 1);
				String d = sn.substring(0, 8);
				if (fm.get(d) != null)
					continue;
				else {
					if (fm.size() > bd)
						f.delete();
					else
						fm.put(d, d);
				}
			}
		}
	}
}
