package com.netsdl.android.init.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.netsdl.android.common.Constant;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.CustMaster;
import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.DbMaster;
import com.netsdl.android.common.db.DeviceMaster;
import com.netsdl.android.common.db.PaymentMaster;
import com.netsdl.android.common.db.PosTable;
import com.netsdl.android.common.db.SkuMaster;
import com.netsdl.android.common.db.StoreMaster;
import com.netsdl.android.common.dialog.progress.AbstractProgressThread;
import com.netsdl.android.init.ConfigProperties;
import com.netsdl.android.init.R;
import com.netsdl.android.init.dialog.progress.commodity.CommodityProgressDialog;
import com.netsdl.android.init.dialog.progress.commodity.CommodityProgressHandler;
import com.netsdl.android.init.dialog.progress.commodity.CommodityProgressThread;
import com.netsdl.android.init.dialog.progress.cust.CustProgressDialog;
import com.netsdl.android.init.dialog.progress.cust.CustProgressHandler;
import com.netsdl.android.init.dialog.progress.cust.CustProgressThread;
import com.netsdl.android.init.dialog.progress.device.DeviceProgressDialog;
import com.netsdl.android.init.dialog.progress.device.DeviceProgressHandler;
import com.netsdl.android.init.dialog.progress.device.DeviceProgressThread;
import com.netsdl.android.init.dialog.progress.payment.PaymentProgressDialog;
import com.netsdl.android.init.dialog.progress.payment.PaymentProgressHandler;
import com.netsdl.android.init.dialog.progress.payment.PaymentProgressThread;
import com.netsdl.android.init.dialog.progress.store.StoreProgressDialog;
import com.netsdl.android.init.dialog.progress.store.StoreProgressHandler;
import com.netsdl.android.init.dialog.progress.store.StoreProgressThread;

public class Init {
	public static final String KEY_SKU = "sku";
	public static final String STORE = "store";
	public static final String PAYMENT = "payment";
	public static final String DEVICE = "device";
	public static final String CUST = "cust";

	public static final int LAYOUT_INIT = R.layout.init;
	InitActivity parent;
	public Map<String, String> initInfo;
	public Map<String, String> infoSku;
	public Map<String, String> infoStore;
	public Map<String, String> infoPayment;
	public Map<String, String> infoDevice;
	public Map<String, String> infoCust;
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				showRemoteVersionInfo();
				break;
			case 1:
				break;
			}
		};
	};

	public Init(InitActivity parent) {
		this.parent = parent;
		initInfo = new HashMap<String, String>();
		infoSku = new HashMap<String, String>();
		infoStore = new HashMap<String, String>();
		infoPayment = new HashMap<String, String>();
		infoDevice = new HashMap<String, String>();
		infoCust = new HashMap<String, String>();

	}

	public void init() {
		parent.setContentView(LAYOUT_INIT);
		setVersion();
		initButtonViewConfig();
		initButtonSaleDate();
		initButtonCheckDB();
		initButtonUpdateCommodity();
		initButtonUpdateStore();
		initButtonUpdatePayment();
		initButtonUpdateDevice();
		initButtonUpdateCust();
		initUploadData();

		((Button) parent.findViewById(R.id.buttonViewConfig)).setEnabled(true);
	}

	private void setVersion() {
		boolean canNext = setSkuVersion();
		canNext &= setStoreVersion();
		canNext &= setPaymentVersion();
		canNext &= setDeviceVersion();
		canNext &= setCustVersion();
		// ((Button)
		// parent.findViewById(R.id.buttonViewConfig)).setEnabled(canNext);
	}

	private boolean setSkuVersion() {
		return setVersion(SkuMaster.class, R.id.CommodityDataLocalVersion);
	}

	private boolean setStoreVersion() {
		return setVersion(StoreMaster.class, R.id.StoreDataLocalVersion);
	}

	private boolean setPaymentVersion() {
		return setVersion(PaymentMaster.class, R.id.PaymentDataLocalVersion);
	}

	private boolean setDeviceVersion() {
		return setVersion(DeviceMaster.class, R.id.DeviceDataLocalVersion);
	}

	private boolean setCustVersion() {
		return setVersion(CustMaster.class, R.id.CustDataLocalVersion);
	}

	private boolean setVersion(Class<?> clazz, int rid) {
		try {
			Object[] objs;
			objs = parent.data.dbMaster
					.getSingleColumn(new Object[] { (String) clazz.getField(
							Constant.TABLE_NAME).get(clazz) });
			String version = (String) DatabaseHelper.getColumnValue(objs,
					DbMaster.COLUMN_VERSION, DbMaster.COLUMNS);
			if (version != null) {
				((TextView) parent.findViewById(rid)).setText(version);
				return true;
			} else {
				return false;
			}
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (NoSuchFieldException e) {
		}
		return false;
	}

	private void initUploadData() {

		((Button) parent.findViewById(R.id.buttonUploadData))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						AlertDialog.Builder builder = new Builder(parent);
						builder.setMessage("确认日结吗？");
						builder.setTitle("提示");
						builder.setPositiveButton("确认", new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								endDay();
							}
						});
						builder.setNegativeButton("取消", new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
						builder.create().show();
					}
				});
	}

	// 日结功能
	private void endDay() {
		try {
			// 1.将需要上传的数据生成文件
			newDataFile();
			// 2.文件上传和备份
			uploadAndBakFile();
			// 3.营业日期加1
			addSaleDate();
			// 删除销售数据
			// parent.data.posTable.deleteByKey(new String[] {
			// PosTable.COLUMN_END_DAY },new String[] { "1" });
			Toast.makeText(parent, R.string.endDayMessage2, Toast.LENGTH_SHORT)
					.show();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 成功上传文件后备份文件
	private void uploadAndBakFile() throws IllegalAccessException,
			NoSuchFieldException {
		String filepath = parent.getFilesDir().getPath().toString();
		Object[] objs = parent.data.deviceMaster.getSingleColumn(new String[] {
				"9", Util.getLocalDeviceId(parent) }, new String[] {
				DeviceMaster.COLUMN_INIT_ID, DeviceMaster.COLUMN_DEVICE_ID });
		if (objs != null) {
			String ftpUrl = DatabaseHelper.getColumnValue(objs,
					DeviceMaster.COLUMN_FIELD_13, DeviceMaster.COLUMNS)
					.toString();
			String ftpPath = DatabaseHelper.getColumnValue(objs,
					DeviceMaster.COLUMN_FIELD_14, DeviceMaster.COLUMNS)
					.toString();
			String ftpUser = DatabaseHelper.getColumnValue(objs,
					DeviceMaster.COLUMN_FIELD_15, DeviceMaster.COLUMNS)
					.toString();
			String ftpPassword = DatabaseHelper.getColumnValue(objs,
					DeviceMaster.COLUMN_FIELD_16, DeviceMaster.COLUMNS)
					.toString();
			// 将filepath下全部文件上传
			String spath = filepath + File.separatorChar + "sale";
			File[] saleUploadFiles = new File(spath).listFiles();
			if (saleUploadFiles != null) {
				for (File uf : saleUploadFiles) {
					// 上传数据
					boolean b = Util.ftpUpload(spath, uf.getName(), ftpUrl,
							ftpPath, ftpUser, ftpPassword);
					// 上传成功才备份
					if (b) {
						// 备份数据
						bakup(spath, uf.getName());
						// 删除生成的文件
						uf.delete();
					}
				}
			}
			String rpath = filepath + File.separatorChar + "return";
			File[] returnUploadFiles = new File(rpath).listFiles();
			if (returnUploadFiles != null) {
				for (File uf : returnUploadFiles) {
					// 上传数据
					boolean b = Util.ftpUpload(rpath, uf.getName(), ftpUrl,
							ftpPath, ftpUser, ftpPassword);
					// 上传成功才备份
					if (b) {
						// 备份数据
						bakup(rpath, uf.getName());
						// 删除生成的文件
						uf.delete();
					}
				}
			}
			String tpath = filepath + File.separatorChar + "transfer";
			File[] transferUploadFiles = new File(tpath).listFiles();
			if (transferUploadFiles != null) {
				for (File uf : transferUploadFiles) {
					// 上传数据
					boolean b = Util.ftpUpload(tpath, uf.getName(), ftpUrl,
							ftpPath, ftpUser, ftpPassword);
					// 上传成功才备份
					if (b) {
						// 备份数据
						bakup(tpath, uf.getName());
						// 删除生成的文件
						uf.delete();
					}
				}
			}
		}
	}

	// 分不同类型生成相应文件（销售、退货、移动、盘点等）
	private void newDataFile() throws IllegalAccessException,
			NoSuchFieldException, FileNotFoundException,
			UnsupportedEncodingException {

		// 取未日结的数据
		// Object[][] objss = parent.data.posTable.getMultiColumn(
		// new String[] { "0" },
		// new String[] { PosTable.COLUMN_END_DAY }, null, null,
		// new String[] { PosTable.COLUMN_CREATE_DATE }, null, true);
		List<Map> dataList = parent.data.posTable.find(true, PosTable.COLUMNS,
				PosTable.COLUMN_END_DAY + " = ?", new String[] { "0" }, null,
				null, null, null);
		// 无数据直接返回
		if (dataList == null || dataList.size() == 0) {
			return;
		}
		// 用于数据整理，KEY（SALE:销售，RETURN：退货，TRANSFER：移动）
		Map<String, List> data = new HashMap(200);
		for (Map d : dataList) {
			String key = null;
			String order_type = (String) d.get(PosTable.COLUMN_ORDER_TYPE);
			if (order_type == null)
				continue;
			String bo_type = (String) d.get(PosTable.COLUMN_BO_TYPE);
			Integer rtn = (Integer) d.get(PosTable.COLUMN_RTN);
			if (rtn == null)
				continue;
			if (order_type.equalsIgnoreCase("DO")) {
				// 销售和销售退货
				if (bo_type == null || bo_type.trim().length() == 0)
					key = "SALE";
				// 移动
				else if (bo_type.equals("MV") && rtn == 1)
					key = "TRANSFER";
			} else if (order_type.equalsIgnoreCase("RO")) {
				// 退货
				if (rtn == -1)
					key = "RETURN";
			}
			if (key == null)
				continue;
			List l = data.get(key);
			if (l == null)
				l = new ArrayList();
			l.add(d);
			data.put(key, l);
		}
		if (data.size() == 0)
			return;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		String timestamp = sdf.format(now.getTime());

		String localDeviceId = Util.getLocalDeviceId(parent);
		String filepath = parent.getFilesDir().getPath().toString();
		// 销售和销售退货数据文件
		List<Map> saleList = data.get("SALE");
		if (saleList != null) {
			// 店铺号用第一条记录的店铺号
			String whNo = (String) saleList.get(0).get(PosTable.COLUMN_WH_NO);
			if (whNo == null)
				whNo = "";
			String path = filepath + File.separatorChar + "sale";
			String filename = whNo + "-sale-" + timestamp + "." + localDeviceId;
			outputFile(path, saleList, filename);
		}
		// 退货到仓库的数据文件
		List<Map> returnList = data.get("RETURN");
		if (returnList != null) {

			// 店铺号用第一条记录的店铺号
			String whNo = (String) returnList.get(0).get(PosTable.COLUMN_WH_NO);
			if (whNo == null)
				whNo = "";
			String path = filepath + File.separatorChar + "return";
			String filename = whNo + "-return-" + timestamp + "."
					+ localDeviceId;
			outputFile(path, returnList, filename);
		}
		// 店铺移动的数据文件
		List<Map> transferList = data.get("TRANSFER");
		if (transferList != null) {

			// 店铺号用第一条记录的店铺号
			String whNo = (String) transferList.get(0).get(
					PosTable.COLUMN_WH_NO);
			if (whNo == null)
				whNo = "";
			String path = filepath + File.separatorChar + "transfer";
			String filename = whNo + "-transfer-" + timestamp + "."
					+ localDeviceId;
			outputFile(path, transferList, filename);
		}
		// 盘点的数据文件

		// 更新日结标记
		Map<String, Object> mapData = new HashMap<String, Object>();
		mapData.put(PosTable.COLUMN_END_DAY, 1);
		parent.data.posTable.update(mapData, new String[] { "0" },
				new String[] { PosTable.COLUMN_END_DAY });
	}

	// 输出文件
	private void outputFile(String filepath, List<Map> dataList, String filename)
			throws FileNotFoundException, UnsupportedEncodingException {
		File path = new File(filepath);
		if (!path.exists())
			path.mkdirs();
		PrintStream output = null;
		try {
			output = new PrintStream(filepath + File.separatorChar + filename,
					Constant.UTF_8);
			String[] columns = PosTable.COLUMNS;
			for (Map d : dataList) {
				for (int j = 0; j < columns.length; j++) {
					// 日结标记不导出
					if (columns[j].equals(PosTable.COLUMN_END_DAY))
						continue;
					Object o = d.get(columns[j]);
					if (o == null)
						o = "";
					output.print(o);
					if (j != columns.length - 1) {
						output.print(',');
					}
				}
				output.println();
				output.flush();
			}
		} finally {
			if (output != null)
				output.close();
		}
	}

	// 备份数据，并删除多余备份
	private void bakup(String filepath, String filename)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		Object[] deviceMasterObjs = DatabaseHelper.getSingleColumn(
				parent.getContentResolver(),
				new Object[] { "9", Util.getLocalDeviceId(parent) },
				DeviceMaster.class);
		String bakpath = (String) DatabaseHelper.getColumnValue(
				deviceMasterObjs, DeviceMaster.COLUMN_FIELD_02,
				DeviceMaster.COLUMNS);
		String bakday = (String) DatabaseHelper.getColumnValue(
				deviceMasterObjs, DeviceMaster.COLUMN_FIELD_03,
				DeviceMaster.COLUMNS);
		// 无备份路径不备份文件，备份路径无法建立也不备份文件
		if (bakpath == null || bakpath.trim().length() == 0)
			return;
		bakpath = bakpath.trim();
		File bakDir = new File(bakpath);
		try {
			if (!bakDir.exists())
				bakDir.mkdirs();
		} catch (Exception e) {
			return;
		}
		int copyFile = Util.CopyFile(filepath + File.separatorChar + filename,
				bakpath + File.separatorChar + filename);
		// 备份文件保留，默认30天
		if (bakday == null || bakday.trim().length() == 0)
			bakday = "30";
		Integer bd = null;
		try {
			bd = new Integer(bakday);
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
				// 得到文件的日期
				String sn = null;
				try {
					sn = f.getName().substring(
							f.getName().lastIndexOf(".") + 1,
							f.getName().lastIndexOf(".") - 1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (sn == null || sn.length() < 8)
					continue;
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

	// 营业日期增加
	private void addSaleDate() throws IllegalAccessException,
			NoSuchFieldException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Object[] deviceMasterObjs = DatabaseHelper.getSingleColumn(
				parent.getContentResolver(),
				new Object[] { "1", Util.getLocalDeviceId(parent) },
				DeviceMaster.class);
		String strDocumentDate = (String) DatabaseHelper.getColumnValue(
				deviceMasterObjs, DeviceMaster.COLUMN_FIELD_04,
				DeviceMaster.COLUMNS);
		Date date = null;
		if (strDocumentDate == null || strDocumentDate.trim().length() == 0)
			date = new Date();
		else
			date = sdf.parse(strDocumentDate);
		date = new Date(date.getTime() + 24 * 60 * 60 * 1000);
		Map<String, Object> sd = new HashMap();
		sd.put(DeviceMaster.COLUMN_FIELD_04, sdf.format(date));
		parent.data.deviceMaster.update(sd,
				new String[] { "1", Util.getLocalDeviceId(parent) },
				new String[] { DeviceMaster.COLUMN_INIT_ID,
						DeviceMaster.COLUMN_DEVICE_ID });
	}

	private void initButtonViewConfig() {
		((Button) parent.findViewById(R.id.buttonViewConfig))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						popupConfigWindow();
					}

				});
	}

	private void initButtonSaleDate() {
		((Button) parent.findViewById(R.id.buttonSaleDate))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						final SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						try {
							// 查询营业日期
							Object[] deviceMasterObjs = DatabaseHelper.getSingleColumn(
									parent.getContentResolver(),
									new Object[] { "1",
											Util.getLocalDeviceId(parent) },
									DeviceMaster.class);
							String strDocumentDate = (String) DatabaseHelper
									.getColumnValue(deviceMasterObjs,
											DeviceMaster.COLUMN_FIELD_04,
											DeviceMaster.COLUMNS);
							Date date = null;
							if (strDocumentDate == null
									|| strDocumentDate.trim().length() == 0)
								date = new Date();
							else
								date = sdf.parse(strDocumentDate);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);

							DatePickerDialog dd = new DatePickerDialog(parent,
									new OnDateSetListener() {

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
											String strDocumentDate = sdf
													.format(calendar.getTime());

											// 保存营业日期
											try {
												Map<String, Object> sd = new HashMap();
												sd.put(DeviceMaster.COLUMN_FIELD_04,
														strDocumentDate);
												parent.data.deviceMaster
														.update(sd,
																new String[] {
																		"1",
																		Util.getLocalDeviceId(parent) },
																new String[] {
																		DeviceMaster.COLUMN_INIT_ID,
																		DeviceMaster.COLUMN_DEVICE_ID });
											} catch (IllegalArgumentException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											} catch (SecurityException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											} catch (IllegalAccessException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											} catch (NoSuchFieldException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									}, calendar.get(Calendar.YEAR), calendar
											.get(Calendar.MONTH), calendar
											.get(Calendar.DAY_OF_MONTH));
							dd.setTitle(R.string.saleDate);
							dd.show();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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

				});
	}

	// 更新检查
	private void initButtonCheckDB() {

		((Button) parent.findViewById(R.id.buttonCheckDB))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							new Thread(new Runnable() {

								public void run() {

									checkUpdate2();
									handler.sendEmptyMessage(0);
								}
							}).start();
						} catch (RuntimeException e) {
							Toast.makeText(parent, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	private void checkUpdate2() {
		try {
			clearUpdateInfo();
			setVersion();
			// String strLocalMacAddress =
			// Util.getLocalMacAddress(parent);
			// Log.d("LocalMacAddress",strLocalMacAddress);

			String strLocalDeviceId = Util.getLocalDeviceId(parent);
			// Log.d("LocalDeviceId", strLocalDeviceId);

			initInfo = Util.getInitInfo();

			if (initInfo.get(Init.KEY_SKU) != null)
				infoSku = Util.getInfo(initInfo.get(Init.KEY_SKU));

			if (initInfo.get(Init.STORE) != null)
				infoStore = Util.getInfo(initInfo.get(Init.STORE));

			if (initInfo.get(Init.PAYMENT) != null)
				infoPayment = Util.getInfo(initInfo.get(Init.PAYMENT));

			if (initInfo.get(Init.DEVICE) != null)
				infoDevice = Util.getInfo(initInfo.get(Init.DEVICE));
			if (initInfo.get(Init.CUST) != null)
				infoCust = Util.getInfo(initInfo.get(Init.CUST));

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showRemoteVersionInfo() {
		if (infoSku.get(Constant.VERSION) == null) {
			((TextView) parent.findViewById(R.id.CommodityDataHostVersion))
					.setText(R.string.updateError);
			((Button) parent.findViewById(R.id.buttonUpdateCommodity))
					.setEnabled(false);
		} else {
			((TextView) parent.findViewById(R.id.CommodityDataHostVersion))
					.setText(infoSku.get(Constant.VERSION));
			((Button) parent.findViewById(R.id.buttonUpdateCommodity))
					.setEnabled(true);
		}

		if (infoStore.get(Constant.VERSION) == null) {
			((TextView) parent.findViewById(R.id.StoreDataHostVersion))
					.setText(R.string.updateError);
			((Button) parent.findViewById(R.id.buttonUpdateStore))
					.setEnabled(false);
		} else {
			((TextView) parent.findViewById(R.id.StoreDataHostVersion))
					.setText(infoStore.get(Constant.VERSION));
			((Button) parent.findViewById(R.id.buttonUpdateStore))
					.setEnabled(true);
		}

		if (infoPayment.get(Constant.VERSION) == null) {
			((TextView) parent.findViewById(R.id.PaymentDataHostVersion))
					.setText(R.string.updateError);
			((Button) parent.findViewById(R.id.buttonUpdatePayment))
					.setEnabled(false);
		} else {
			((TextView) parent.findViewById(R.id.PaymentDataHostVersion))
					.setText(infoPayment.get(Constant.VERSION));
			((Button) parent.findViewById(R.id.buttonUpdatePayment))
					.setEnabled(true);
		}

		if (infoDevice.get(Constant.VERSION) == null) {
			((TextView) parent.findViewById(R.id.DeviceDataHostVersion))
					.setText(R.string.updateError);
			((Button) parent.findViewById(R.id.buttonUpdateDevice))
					.setEnabled(false);
		} else {
			((TextView) parent.findViewById(R.id.DeviceDataHostVersion))
					.setText(infoDevice.get(Constant.VERSION));
			((Button) parent.findViewById(R.id.buttonUpdateDevice))
					.setEnabled(true);
		}
		if (infoCust.get(Constant.VERSION) == null) {
			((TextView) parent.findViewById(R.id.CustDataHostVersion))
					.setText(R.string.updateError);
			((Button) parent.findViewById(R.id.buttonUpdateCust))
					.setEnabled(false);
		} else {
			((TextView) parent.findViewById(R.id.CustDataHostVersion))
					.setText(infoDevice.get(Constant.VERSION));
			((Button) parent.findViewById(R.id.buttonUpdateCust))
					.setEnabled(true);
		}
	}

	// 清空版本信息
	private void clearUpdateInfo() {
		initInfo.clear();
		infoSku.clear();
		infoStore.clear();
		infoPayment.clear();
		infoDevice.clear();
		((TextView) parent.findViewById(R.id.CommodityDataLocalVersion))
				.setText("");
		((TextView) parent.findViewById(R.id.CommodityDataHostVersion))
				.setText("");
		((Button) parent.findViewById(R.id.buttonUpdateCommodity))
				.setEnabled(false);

		((TextView) parent.findViewById(R.id.StoreDataLocalVersion))
				.setText("");
		((TextView) parent.findViewById(R.id.StoreDataHostVersion)).setText("");
		((Button) parent.findViewById(R.id.buttonUpdateStore))
				.setEnabled(false);

		((TextView) parent.findViewById(R.id.PaymentDataLocalVersion))
				.setText("");
		((TextView) parent.findViewById(R.id.PaymentDataHostVersion))
				.setText("");
		((Button) parent.findViewById(R.id.buttonUpdatePayment))
				.setEnabled(false);

		((TextView) parent.findViewById(R.id.DeviceDataLocalVersion))
				.setText("");
		((TextView) parent.findViewById(R.id.DeviceDataHostVersion))
				.setText("");
		((Button) parent.findViewById(R.id.buttonUpdateDevice))
				.setEnabled(false);
	}

	private void checkUpdate() {
		clearUpdateInfo();
		setVersion();
		// 通过Config配置得到店铺数据的更新位置URL
		String url = ConfigProperties.getProperties("pos.config.url");
		if (url == null || url.trim().length() == 0) {
			Toast.makeText(parent, R.string.configUrlNoFind, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		// URL下固定路径，得到device_update.txt、payment_update.txt、sku_update.txt、store_update.txt配置信息
		Map<String, String> remoteInfo = getUpdateConfigUrl(url);
		if (remoteInfo == null)
			return;
		// 配置文件中包含更新地址、版本和记录数
		try {
			chekcSkuUpdate(remoteInfo.get("SkuUpdateUrl"));
			infoStore = getDataConfig(remoteInfo.get("StoreUpdateUrl"));
			infoPayment = getDataConfig(remoteInfo.get("PaymentUpdateUrl"));
			infoDevice = getDataConfig(remoteInfo.get("DeviceUpdateUrl"));
			infoCust = getDataConfig(remoteInfo.get("CustUpdateUrl"));
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 得到配置文件地址
	private Map getUpdateConfigUrl(String url) {
		SoapObject rpc = new SoapObject("http://netsdl.com/webservices/",
				"GetInitInfo");

		rpc.addProperty("deviceId", Util.getLocalDeviceId(this.parent));
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER12);

		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE httpTransportSE = new HttpTransportSE(url);
		httpTransportSE.debug = true;
		try {
			httpTransportSE.call("http://netsdl.com/webservices/", envelope);
			SoapObject result = (SoapObject) envelope.getResponse();
			Map m = new HashMap();
			m.put("ErrorCode", result.getPropertyAsString("ErrorCode"));
			m.put("ErrorMessage", result.getPropertyAsString("ErrorMessage"));
			m.put("PaymentUpdateUrl",
					result.getPropertyAsString("PaymentUpdateUrl"));
			m.put("SkuUpdateUrl", result.getPropertyAsString("SkuUpdateUrl"));
			m.put("StoreUpdateUrl",
					result.getPropertyAsString("StoreUpdateUrl"));
			m.put("DeviceUpdateUrl",
					result.getPropertyAsString("DeviceUpdateUrl"));
			m.put("CustUpdateUrl", result.getPropertyAsString("CustUpdateUrl"));
			Log.d("ws", result.getPropertyAsString("ErrorCode"));
			return m;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 通过配置文件得到更新的配置
	private Map getDataConfig(String userUrl) {
		String baseUrl = userUrl.substring(0, userUrl.lastIndexOf("/") + 1);
		BufferedReader in = null;
		try {
			in = null;
			in = Util.getBufferedReaderFromURI(userUrl);
			Map<String, String> map = new HashMap<String, String>();
			// url
			String line = in.readLine();
			if (line == null)
				return map;
			map.put(Constant.URL, baseUrl + line);
			// version
			line = in.readLine();
			if (line == null)
				return map;
			map.put(Constant.VERSION, line);
			// rows
			line = in.readLine();
			if (line == null)
				return map;
			map.put(Constant.ROWS, line);
			return map;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	// 商品更新检查
	private void chekcSkuUpdate(String skuUrl) {
		String baseUrl = skuUrl.substring(0, skuUrl.lastIndexOf("/") + 1);
		Map<String, String> skuMap = getSkuConfig(skuUrl);
		// 配置文件不全的情况直接返回
		if (skuMap.size() != 6) {
			return;
		}

		String currentVersion = ((TextView) parent
				.findViewById(R.id.CommodityDataLocalVersion)).getText()
				.toString().trim();
		// 如果本地版本在服务器版本的区间之内，则更新增量文件，反之更新全量文件
		if (currentVersion.compareToIgnoreCase(skuMap.get("fromVersion")
				.toString()) >= 0
				&& currentVersion.compareToIgnoreCase(skuMap.get("endVersion")
						.toString()) <= 0) {
			infoSku.put(Constant.URL, baseUrl + skuMap.get("addSkuUrl"));
			infoSku.put(Constant.VERSION, skuMap.get("endVersion"));
			infoSku.put(Constant.ROWS, skuMap.get("addSkuRows"));
		} else {
			infoSku.put(Constant.URL, baseUrl + skuMap.get("allSkuUrl"));
			infoSku.put(Constant.VERSION, skuMap.get("endVersion"));
			infoSku.put(Constant.ROWS, skuMap.get("allSkuRows"));
		}
	}

	// 通过配置文件得到SKU更新的配置
	private Map getSkuConfig(String skuUrl) {
		BufferedReader in = null;
		try {
			in = null;
			in = Util.getBufferedReaderFromURI(skuUrl);
			Map<String, String> map = new HashMap<String, String>();
			// 全部下载的文件名
			String line = in.readLine();
			if (line == null)
				return map;
			map.put("allSkuUrl", line);
			// 全部下载行数
			line = in.readLine();
			if (line == null)
				return map;
			map.put("allSkuRows", line);
			// 增量下载的文件名
			line = in.readLine();
			if (line == null)
				return map;
			map.put("addSkuUrl", line);
			// 增量下载行数
			line = in.readLine();
			if (line == null)
				return map;
			map.put("addSkuRows", line);
			// 增量下载的起始戳
			line = in.readLine();
			if (line == null)
				return map;
			map.put("fromVersion", line);
			// 增量下载的结束戳
			line = in.readLine();
			if (line == null)
				return map;
			map.put("endVersion", line);
			return map;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private void initButtonUpdateCommodity() {
		((Button) parent.findViewById(R.id.buttonUpdateCommodity))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						final CommodityProgressDialog commodityProgressDialog = new CommodityProgressDialog(
								parent);
						CommodityProgressHandler commodityProgressHandler = new CommodityProgressHandler(
								commodityProgressDialog);
						final CommodityProgressThread commodityProgressThread = new CommodityProgressThread(
								commodityProgressHandler);
						commodityProgressThread
								.setSkuMaster(parent.data.skuMaster);
						commodityProgressThread.setUrl(infoSku
								.get(Constant.URL));
						commodityProgressHandler
								.setCommodityProgressThread(commodityProgressThread);
						commodityProgressDialog
								.setProgressThread(commodityProgressThread);

						int rows;
						try {
							rows = Integer.parseInt(infoSku.get(Constant.ROWS));
						} catch (NumberFormatException nfe) {
							rows = 100;
						}
						commodityProgressDialog.setProgressMax(rows);

						commodityProgressDialog
								.setOnDismissListener(new OnDismissListener() {
									public void onDismiss(DialogInterface dialog) {
										try {
											if (commodityProgressThread.mState == AbstractProgressThread.STATE_DONE) {

												parent.data.dbMaster
														.deleteByKey(
																null,
																new String[] { infoSku
																		.get(Constant.VERSION) });

												parent.data.dbMaster.insert(new String[] {
														SkuMaster.TABLE_NAME,
														infoSku.get(Constant.VERSION) });

												setVersion();
												// setSkuVersion();
												// if (setStoreVersion()
												// && setPaymentVersion()
												// && setDeviceVersion()) {
												// ((Button) parent
												// .findViewById(R.id.buttonNext))
												// .setEnabled(true);
												// } else {
												// ((Button) parent
												// .findViewById(R.id.buttonNext))
												// .setEnabled(false);
												// }

											}

										} catch (IllegalArgumentException e) {
										} catch (SecurityException e) {
										} catch (IllegalAccessException e) {
										} catch (NoSuchFieldException e) {
										}

									}
								});

						parent.mapDialogable.put(
								commodityProgressDialog.hashCode(),
								commodityProgressDialog);
						parent.showDialog(commodityProgressDialog.hashCode());

						Log.d("Init", "parent.showDialog");

					}
				});
	}

	private void initButtonUpdateStore() {
		((Button) parent.findViewById(R.id.buttonUpdateStore))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						final StoreProgressDialog storeProgressDialog = new StoreProgressDialog(
								parent);
						StoreProgressHandler storeProgressHandler = new StoreProgressHandler(
								storeProgressDialog);
						final StoreProgressThread storeProgressThread = new StoreProgressThread(
								storeProgressHandler);
						storeProgressThread
								.setStoreMaster(parent.data.storeMaster);
						storeProgressThread.setUrl(infoStore.get(Constant.URL));
						storeProgressHandler
								.setStoreProgressThread(storeProgressThread);
						storeProgressDialog
								.setProgressThread(storeProgressThread);

						int rows;
						try {
							rows = Integer.parseInt(infoStore
									.get(Constant.ROWS));
						} catch (NumberFormatException nfe) {
							rows = 100;
						}
						storeProgressDialog.setProgressMax(rows);

						storeProgressDialog
								.setOnDismissListener(new OnDismissListener() {
									public void onDismiss(DialogInterface dialog) {
										try {
											if (storeProgressThread.mState == AbstractProgressThread.STATE_DONE) {
												parent.data.dbMaster
														.deleteByKey(
																null,
																new String[] { infoStore
																		.get(Constant.VERSION) });
												parent.data.dbMaster.insert(new String[] {
														StoreMaster.TABLE_NAME,
														infoStore
																.get(Constant.VERSION) });

												setVersion();
												// setStoreVersion();
												// if (setSkuVersion()
												// && setPaymentVersion()) {
												// ((Button) parent
												// .findViewById(R.id.buttonNext))
												// .setEnabled(true);
												// } else {
												// ((Button) parent
												// .findViewById(R.id.buttonNext))
												// .setEnabled(false);
												// }
											}

										} catch (IllegalArgumentException e) {
										} catch (SecurityException e) {
										} catch (IllegalAccessException e) {
										} catch (NoSuchFieldException e) {
										}

									}
								});

						parent.mapDialogable.put(
								storeProgressDialog.hashCode(),
								storeProgressDialog);
						parent.showDialog(storeProgressDialog.hashCode());

						Log.d("Init", "parent.showDialog");

					}
				});
	}

	private void initButtonUpdatePayment() {
		((Button) parent.findViewById(R.id.buttonUpdatePayment))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						final PaymentProgressDialog paymentProgressDialog = new PaymentProgressDialog(
								parent);
						PaymentProgressHandler paymentProgressHandler = new PaymentProgressHandler(
								paymentProgressDialog);
						final PaymentProgressThread paymentProgressThread = new PaymentProgressThread(
								paymentProgressHandler);
						paymentProgressThread
								.setPaymentMaster(parent.data.paymentMaster);
						paymentProgressThread.setUrl(infoPayment
								.get(Constant.URL));
						paymentProgressHandler
								.setPaymentProgressThread(paymentProgressThread);
						paymentProgressDialog
								.setProgressThread(paymentProgressThread);

						int rows;
						try {
							rows = Integer.parseInt(infoPayment
									.get(Constant.ROWS));
						} catch (NumberFormatException nfe) {
							rows = 100;
						}
						paymentProgressDialog.setProgressMax(rows);

						paymentProgressDialog
								.setOnDismissListener(new OnDismissListener() {
									public void onDismiss(DialogInterface dialog) {
										try {
											if (paymentProgressThread.mState == AbstractProgressThread.STATE_DONE) {
												parent.data.dbMaster
														.deleteByKey(
																null,
																new String[] { infoPayment
																		.get(Constant.VERSION) });

												parent.data.dbMaster.insert(new String[] {
														PaymentMaster.TABLE_NAME,
														infoPayment
																.get(Constant.VERSION) });

												setVersion();
												// setPaymentVersion();
												// if (setSkuVersion()
												// && setStoreVersion()) {
												// ((Button) parent
												// .findViewById(R.id.buttonNext))
												// .setEnabled(true);
												// } else {
												// ((Button) parent
												// .findViewById(R.id.buttonNext))
												// .setEnabled(false);
												// }
											}

										} catch (IllegalArgumentException e) {
										} catch (SecurityException e) {
										} catch (IllegalAccessException e) {
										} catch (NoSuchFieldException e) {
										}

									}
								});

						parent.mapDialogable.put(
								paymentProgressDialog.hashCode(),
								paymentProgressDialog);
						parent.showDialog(paymentProgressDialog.hashCode());

						Log.d("Init", "parent.showDialog");

					}
				});
	}

	private void initButtonUpdateDevice() {
		((Button) parent.findViewById(R.id.buttonUpdateDevice))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						final DeviceProgressDialog deviceProgressDialog = new DeviceProgressDialog(
								parent);
						DeviceProgressHandler deviceProgressHandler = new DeviceProgressHandler(
								deviceProgressDialog);
						final DeviceProgressThread deviceProgressThread = new DeviceProgressThread(
								deviceProgressHandler);
						deviceProgressThread
								.setDeviceMaster(parent.data.deviceMaster);
						deviceProgressThread.setUrl(infoDevice
								.get(Constant.URL));
						deviceProgressHandler
								.setDeviceProgressThread(deviceProgressThread);
						deviceProgressDialog
								.setProgressThread(deviceProgressThread);

						int rows;
						try {
							rows = Integer.parseInt(infoDevice
									.get(Constant.ROWS));
						} catch (NumberFormatException nfe) {
							rows = 100;
						}
						deviceProgressDialog.setProgressMax(rows);

						deviceProgressDialog
								.setOnDismissListener(new OnDismissListener() {
									public void onDismiss(DialogInterface dialog) {
										try {
											if (deviceProgressThread.mState == AbstractProgressThread.STATE_DONE) {
												parent.data.dbMaster
														.deleteByKey(
																null,
																new String[] { infoDevice
																		.get(Constant.VERSION) });

												parent.data.dbMaster.insert(new String[] {
														DeviceMaster.TABLE_NAME,
														infoDevice
																.get(Constant.VERSION) });

												setVersion();
												// setDeviceVersion();
												// if (setSkuVersion()
												// && setStoreVersion()) {
												// ((Button) parent
												// .findViewById(R.id.buttonNext))
												// .setEnabled(true);
												// } else {
												// ((Button) parent
												// .findViewById(R.id.buttonNext))
												// .setEnabled(false);
												// }
											}

										} catch (IllegalArgumentException e) {
										} catch (SecurityException e) {
										} catch (IllegalAccessException e) {
										} catch (NoSuchFieldException e) {
										}

									}
								});

						parent.mapDialogable.put(
								deviceProgressDialog.hashCode(),
								deviceProgressDialog);
						parent.showDialog(deviceProgressDialog.hashCode());

						Log.d("Init", "parent.showDialog");

					}
				});
	}

	private void initButtonUpdateCust() {
		((Button) parent.findViewById(R.id.buttonUpdateCust))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						final CustProgressDialog custProgressDialog = new CustProgressDialog(
								parent);
						CustProgressHandler custProgressHandler = new CustProgressHandler(
								custProgressDialog);
						final CustProgressThread custProgressThread = new CustProgressThread(
								custProgressHandler);
						custProgressThread
								.setCustMaster(parent.data.custMaster);
						custProgressThread.setUrl(infoCust.get(Constant.URL));
						custProgressHandler
								.setCustProgressThread(custProgressThread);
						custProgressDialog
								.setProgressThread(custProgressThread);

						int rows;
						try {
							rows = Integer.parseInt(infoCust.get(Constant.ROWS));
						} catch (NumberFormatException nfe) {
							rows = 100;
						}
						custProgressDialog.setProgressMax(rows);

						custProgressDialog
								.setOnDismissListener(new OnDismissListener() {
									public void onDismiss(DialogInterface dialog) {
										try {
											if (custProgressThread.mState == AbstractProgressThread.STATE_DONE) {
												parent.data.dbMaster
														.deleteByKey(
																null,
																new String[] { infoCust
																		.get(Constant.VERSION) });

												parent.data.dbMaster.insert(new String[] {
														CustMaster.TABLE_NAME,
														infoCust.get(Constant.VERSION) });

												setVersion();
											}

										} catch (IllegalArgumentException e) {
										} catch (SecurityException e) {
										} catch (IllegalAccessException e) {
										} catch (NoSuchFieldException e) {
										}

									}
								});

						parent.mapDialogable.put(custProgressDialog.hashCode(),
								custProgressDialog);
						parent.showDialog(custProgressDialog.hashCode());

						Log.d("Init", "parent.showDialog");

					}
				});
	}

	private void popupConfigWindow() {
		LayoutInflater layoutInflater = (LayoutInflater) parent
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.config, null);

		((TextView) view.findViewById(R.id.deviceid)).setText(Util
				.getLocalDeviceId(parent));
		((TextView) view.findViewById(R.id.mac)).setText(Util
				.getLocalMacAddress(parent));
		((TextView) view.findViewById(R.id.ip)).setText(Util
				.getIpAddress(parent));

		// ((TextView) view.findViewById(R.id.ip)).setText(Util
		// .getIpAddress(parent));
		((TextView) view.findViewById(R.id.externalStorageDirectory))
				.setText(Util.ExternalStorageDirectory());

		final PopupWindow popupWindow = new PopupWindow(view, parent
				.getWindowManager().getDefaultDisplay().getWidth() / 2, parent
				.getWindowManager().getDefaultDisplay().getHeight() / 2);

		((Button) view.findViewById(R.id.close))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						popupWindow.dismiss();
					}
				});

		popupWindow.showAtLocation(parent.findViewById(R.id.init),
				Gravity.CENTER, 0, 0);
	}

}
