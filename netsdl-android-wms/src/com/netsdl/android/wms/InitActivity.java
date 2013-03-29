package com.netsdl.android.wms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.netsdl.android.ConfigProperties;
import com.netsdl.android.Util;
import com.netsdl.android.db.CustMaster;
import com.netsdl.android.db.DbMaster;
import com.netsdl.android.db.DeviceMaster;
import com.netsdl.android.db.Order;
import com.netsdl.android.db.SkuMaster;
import com.netsdl.android.db.UserMaster;
import com.netsdl.android.service.CommonServices;

public class InitActivity extends Activity {

	private Map<String, String> infoSku;
	private Map<String, String> infoUser;
	private Map<String, String> infoCust;
	private Map<String, String> infoDevice;
	private boolean downloadThread;
	private String userNo;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		Bundle data = this.getIntent().getExtras();
		if(data!=null)
		{
			userNo = data.getString("userNo");
		}
		// 事件初始化
		init();
		// UI组件控制
		initControl();
		// 本地版本检查
		checkLocalVersion();
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				versionInfoSetup();
				break;
			case 1:
				checkLocalVersion();
				break;
			}
		};
	};

	private void init() {
		infoSku = new HashMap<String, String>();
		infoUser = new HashMap<String, String>();
		infoCust = new HashMap<String, String>();
		infoDevice = new HashMap<String, String>();
		((Button) this.findViewById(R.id.backButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							Intent gotoIntent = new Intent(InitActivity.this,
									ModuleActivity.class);
							InitActivity.this.startActivity(gotoIntent);
						} catch (RuntimeException e) {
							Toast.makeText(InitActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
		((Button) this.findViewById(R.id.checkDbButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							new Thread(new Runnable() {

								@Override
								public void run() {

									checkDbUpdate();
									handler.sendEmptyMessage(0);
								}
							}).start();
						} catch (RuntimeException e) {
							Toast.makeText(InitActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		((Button) this.findViewById(R.id.userMasterDownloadButton))
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						try {
							userDownload();
						} catch (RuntimeException e) {
							Toast.makeText(InitActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
		((Button) this.findViewById(R.id.skuMasterDownloadButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							skuDownload();
						} catch (RuntimeException e) {
							Toast.makeText(InitActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}

					}

				});
		((Button) this.findViewById(R.id.custMasterDownloadButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							custDownload();
						} catch (RuntimeException e) {
							Toast.makeText(InitActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}

					}

				});
		((Button) this.findViewById(R.id.deviceMasterDownloadButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							deviceDownload();
						} catch (RuntimeException e) {
							Toast.makeText(InitActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}

					}

				});
		((Button) this.findViewById(R.id.dataUploadButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							final ProgressDialog progressDialog = ProgressDialog
									.show(InitActivity.this, InitActivity.this
											.getString(R.string.waiting),
											InitActivity.this
													.getString(R.string.Uploading),
											true);
							new Thread(new Runnable() {
								@Override
								public void run() {

									dataUpload();
									progressDialog.dismiss();
								}
							}).start();
						} catch (RuntimeException e) {
							Toast.makeText(InitActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
		((Button) this.findViewById(R.id.buttonDeviceInfo))
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					popupConfigWindow();
				} catch (RuntimeException e) {
					Toast.makeText(InitActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}

			}

		});
	}

	private void initControl() {

		((Button) this.findViewById(R.id.skuMasterDownloadButton))
				.setEnabled(false);
		((Button) this.findViewById(R.id.custMasterDownloadButton))
				.setEnabled(false);
		((Button) this.findViewById(R.id.userMasterDownloadButton))
				.setEnabled(false);
		((Button) this.findViewById(R.id.deviceMasterDownloadButton))
				.setEnabled(false);
		if(userNo!=null)
		{
			((Button) this.findViewById(R.id.dataUploadButton)).setEnabled(true);
		}
		else
		{
			((Button) this.findViewById(R.id.dataUploadButton)).setEnabled(false);
		}

	}

	// 本地版本检查
	private void checkLocalVersion() {
		DbMaster dm = new DbMaster(InitActivity.this);
		List<Map> dmList = dm.findAll();
		if (dmList == null || dmList.size() == 0) {
			((TextView) this.findViewById(R.id.userMasterLocalVersionTextView))
					.setText("");
			((TextView) this.findViewById(R.id.skuMasterLocalVersionTextView))
					.setText("");
			((TextView) this.findViewById(R.id.custMasterLocalVersionTextView))
					.setText("");
			((TextView) this
					.findViewById(R.id.deviceMasterLocalVersionTextView))
					.setText("");
			return;
		}
		for (Map m : dmList) {
			if (m.get(DbMaster.COLUMN_NAME) == null)
				continue;
			if (m.get(DbMaster.COLUMN_NAME).equals("user_master")) {
				if (m.get(DbMaster.COLUMN_VERSION) != null)
					((TextView) this
							.findViewById(R.id.userMasterLocalVersionTextView))
							.setText((String) m.get(DbMaster.COLUMN_VERSION));
			}
			if (m.get(DbMaster.COLUMN_NAME).equals("sku_master")) {
				if (m.get(DbMaster.COLUMN_VERSION) != null)
					((TextView) this
							.findViewById(R.id.skuMasterLocalVersionTextView))
							.setText((String) m.get(DbMaster.COLUMN_VERSION));
			}
			if (m.get(DbMaster.COLUMN_NAME).equals("cust_master")) {
				if (m.get(DbMaster.COLUMN_VERSION) != null)
					((TextView) this
							.findViewById(R.id.custMasterLocalVersionTextView))
							.setText((String) m.get(DbMaster.COLUMN_VERSION));
			}
			if (m.get(DbMaster.COLUMN_NAME).equals("device_master")) {
				if (m.get(DbMaster.COLUMN_VERSION) != null)
					((TextView) this
							.findViewById(R.id.deviceMasterLocalVersionTextView))
							.setText((String) m.get(DbMaster.COLUMN_VERSION));
			}
		}
	}

	// 检查远程更新
	private void checkDbUpdate() {
		// 判断是否登录
		// 未登录则只有用户数据可下载
		Map ul = CommonServices.getLoginUserInfo(InitActivity.this);
		String url = ConfigProperties.getProperties("wms.config.url");
		if (url == null || url.trim().length() == 0) {
			Toast.makeText(InitActivity.this, R.string.configUrlNoFind,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// URL下固定路径，得到device_update.txt、payment_update.txt、sku_update.txt、store_update.txt、cust_update.txt配置信息
		Map<String, String> remoteInfo = getUpdateConfigUrl(url);
		if (remoteInfo == null)
			return;
		// 配置文件中包含更新地址、版本和记录数
		chekcSkuUpdate(remoteInfo.get("SkuUpdateUrl"));
		Map m = getDataConfig(remoteInfo.get("StoreUpdateUrl"));
		if (m != null)
			infoUser.putAll(m);
		m = getDataConfig(remoteInfo.get("CustUpdateUrl"));
		if (m != null)
			infoCust.putAll(m);
		m = getDataConfig(remoteInfo.get("DeviceUpdateUrl"));
		if (m != null)
			infoDevice.putAll(m);

	}

	// 版本信息设置
	private void versionInfoSetup() {
		if (infoSku.get("URL") == null) {
			((TextView) this.findViewById(R.id.skuMasterRemoteVersionTextView))
					.setText(R.string.updateError);
			((Button) this.findViewById(R.id.skuMasterDownloadButton))
					.setEnabled(false);
		} else {
			((TextView) this.findViewById(R.id.skuMasterRemoteVersionTextView))
					.setText(infoSku.get("VERSION"));
			((Button) this.findViewById(R.id.skuMasterDownloadButton))
					.setEnabled(true);
		}

		if (infoUser.get("URL") == null) {
			((TextView) this.findViewById(R.id.userMasterRemoteVersionTextView))
					.setText(R.string.updateError);
			((Button) this.findViewById(R.id.userMasterDownloadButton))
					.setEnabled(false);
		} else {
			((TextView) this.findViewById(R.id.userMasterRemoteVersionTextView))
					.setText(infoUser.get("VERSION"));
			((Button) this.findViewById(R.id.userMasterDownloadButton))
					.setEnabled(true);
		}

		if (infoCust.get("URL") == null) {
			((TextView) this.findViewById(R.id.custMasterRemoteVersionTextView))
					.setText(R.string.updateError);
			((Button) this.findViewById(R.id.custMasterDownloadButton))
					.setEnabled(false);
		} else {
			((TextView) this.findViewById(R.id.custMasterRemoteVersionTextView))
					.setText(infoCust.get("VERSION"));
			((Button) this.findViewById(R.id.custMasterDownloadButton))
					.setEnabled(true);
		}

		if (infoDevice.get("URL") == null) {
			((TextView) this
					.findViewById(R.id.deviceMasterRemoteVersionTextView))
					.setText(R.string.updateError);
			((Button) this.findViewById(R.id.deviceMasterDownloadButton))
					.setEnabled(false);
		} else {
			((TextView) this
					.findViewById(R.id.deviceMasterRemoteVersionTextView))
					.setText(infoDevice.get("VERSION"));
			((Button) this.findViewById(R.id.deviceMasterDownloadButton))
					.setEnabled(true);
		}

		Map m = CommonServices.getLoginUserInfo(this);
		if (m == null) {
			((Button) this.findViewById(R.id.userMasterDownloadButton))
					.setEnabled(true);
			((Button) this.findViewById(R.id.skuMasterDownloadButton))
					.setEnabled(false);
			((Button) this.findViewById(R.id.custMasterDownloadButton))
					.setEnabled(false);
			((Button) this.findViewById(R.id.deviceMasterDownloadButton))
					.setEnabled(false);
//			((Button) this.findViewById(R.id.dataUploadButton))
//					.setEnabled(false);
		} else {
			((Button) this.findViewById(R.id.userMasterDownloadButton))
					.setEnabled(true);
			((Button) this.findViewById(R.id.skuMasterDownloadButton))
					.setEnabled(true);
			((Button) this.findViewById(R.id.custMasterDownloadButton))
					.setEnabled(true);
			((Button) this.findViewById(R.id.deviceMasterDownloadButton))
					.setEnabled(true);
//			((Button) this.findViewById(R.id.dataUploadButton))
//					.setEnabled(true);
		}
	}

	// 得到配置文件地址
	private Map getUpdateConfigUrl(String url) {
		SoapObject rpc = new SoapObject("http://netsdl.com/webservices/",
				"GetInitInfo");

		rpc.addProperty("deviceId", Util.getLocalDeviceId(InitActivity.this));
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
			// m.put("PaymentUpdateUrl",
			// result.getPropertyAsString("PaymentUpdateUrl"));
			m.put("SkuUpdateUrl", result.getPropertyAsString("SkuUpdateUrl"));
			m.put("StoreUpdateUrl",
					result.getPropertyAsString("StoreUpdateUrl"));
			m.put("DeviceUpdateUrl",
					result.getPropertyAsString("DeviceUpdateUrl"));
			m.put("CustUpdateUrl", result.getPropertyAsString("CustUpdateUrl"));
			Log.d("ws", result.getPropertyAsString("ErrorCode"));
			return m;
		} catch (IOException e) {
			Log.d("ws", e.getMessage());
		} catch (XmlPullParserException e) {
			Log.d("ws", e.getMessage());
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

		String currentVersion = ((TextView) this
				.findViewById(R.id.skuMasterLocalVersionTextView)).getText()
				.toString().trim();
		// 如果本地版本在服务器版本的区间之内，则更新增量文件，反之更新全量文件
		if (currentVersion.compareToIgnoreCase(skuMap.get("fromVersion")
				.toString()) >= 0
				&& currentVersion.compareToIgnoreCase(skuMap.get("endVersion")
						.toString()) <= 0) {
			infoSku.put("URL", baseUrl + skuMap.get("addSkuUrl"));
			infoSku.put("VERSION", skuMap.get("endVersion"));
			infoSku.put("ROWS", skuMap.get("addSkuRows"));
		} else {
			infoSku.put("URL", baseUrl + skuMap.get("allSkuUrl"));
			infoSku.put("VERSION", skuMap.get("endVersion"));
			infoSku.put("ROWS", skuMap.get("allSkuRows"));
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
			map.put("URL", baseUrl + line);
			// version
			line = in.readLine();
			if (line == null)
				return map;
			map.put("VERSION", line);
			// rows
			line = in.readLine();
			if (line == null)
				return map;
			map.put("ROWS", line);
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

	private void userDownload() {
		UserMaster um = new UserMaster(this);
		downloadProgress(this.getString(R.string.userMaster),
				Integer.parseInt(infoUser.get("ROWS")), infoUser.get("URL"), um);
	}

	private void skuDownload() {
		SkuMaster sm = new SkuMaster(this);
		downloadProgress(this.getString(R.string.skuMaster),
				Integer.parseInt(infoSku.get("ROWS")), infoSku.get("URL"), sm);
	}

	private void custDownload() {
		CustMaster cm = new CustMaster(this);
		downloadProgress(this.getString(R.string.custMaster),
				Integer.parseInt(infoCust.get("ROWS")), infoCust.get("URL"), cm);
	}

	private void deviceDownload() {
		DeviceMaster dm = new DeviceMaster(this);
		downloadProgress(this.getString(R.string.deviceMaster),
				Integer.parseInt(infoDevice.get("ROWS")),
				infoDevice.get("URL"), dm);
	}

	private void localVersionSave(String name, String version) {
		DbMaster dm = new DbMaster(this);
		Map data = new HashMap();
		data.put(DbMaster.COLUMN_NAME, name);
		data.put(DbMaster.COLUMN_VERSION, version);
		long r = dm.update(data, DbMaster.COLUMN_NAME + " = ?",
				new String[] { name });
		if (r == 0)
			dm.insert(data, null);
	}

	// 下载进度条处理
	private void downloadProgress(String title, int rows, final String url,
			final Object master) {
		// 下载线程控制
		downloadThread = false;
		final ProgressDialog dialog = new ProgressDialog(this);
		// 设置进度条风格，风格为圆形，旋转的
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 设置ProgressDialog 标题
		dialog.setTitle(R.string.message);
		// 设置ProgressDialog 提示信息
		dialog.setMessage(title + this.getString(R.string.Loading));
		// 设置ProgressDialog 标题图标
		dialog.setIcon(android.R.drawable.ic_dialog_alert);
		// 设置ProgressDialog的最大进度
		dialog.setMax(rows);
		// 设置ProgressDialog 的一个Button
		dialog.setButton("取消", new ProgressDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadThread = true;
			}
		});
		// 设置ProgressDialog 是否可以按退回按键取消
		dialog.setCancelable(true);
		// 显示
		dialog.show();
		new Thread() {
			public void run() {

				try {
					BufferedReader in = Util.getBufferedReaderFromURI(url,
							"utf-8");
					int i = 0;
					while (true) {
						if (downloadThread)
							break;
						String line = in.readLine();
						if (line == null || line.trim().length() == 0)
							break;
						String[] data = line.split(",");
						// 用户
						if (master instanceof UserMaster) {
							// 目前无PJ
							if (data.length != 4)
								continue;
							UserMaster um = (UserMaster) master;
							Map m = new HashMap();
							m.put(UserMaster.COLUMN_USER_NO, data[0]);
							m.put(UserMaster.COLUMN_MD5, data[1]);
							m.put(UserMaster.COLUMN_NAME, data[2]);
							m.put(UserMaster.COLUMN_ROLE, data[3]);
							long r = um.update(m, UserMaster.COLUMN_USER_NO
									+ " = ?", new String[] { data[0] });
							if (r == 0) {
								um.insert(m, null);
							}
						}
						// Sku
						else if (master instanceof SkuMaster) {
							if (data.length != SkuMaster.COLUMNS.length)
								continue;
							SkuMaster sm = (SkuMaster) master;
							Map m = new HashMap();
							for (int j = 1; j < SkuMaster.COLUMNS.length; j++) {
								m.put(SkuMaster.COLUMNS[j], data[j]);
							}
							m.put(SkuMaster.COLUMN_SKU_ID, data[21]);
							long r = sm.update(m, SkuMaster.COLUMN_BAR_CODE
									+ " = ?", new String[] { data[21] });
							if (r == 0) {
								sm.insert(m, SkuMaster.COLUMN_SKU_ID);
							}
						}
						// 客户
						else if (master instanceof CustMaster) {
							if (data.length <4)
								continue;
							CustMaster cm = (CustMaster) master;
							Map m = new HashMap();
							m.put(CustMaster.COLUMN_CUST_NO, data[0]);
							m.put(CustMaster.COLUMN_CUST_NAME, data[1]);
							m.put(CustMaster.COLUMN_CUST_TYPE, data[2]);
							m.put(CustMaster.COLUMN_CUST_CAT, data[3]);
							if (data.length ==5)
								m.put(CustMaster.COLUMN_OFF_RATE, data[4]);
							long r = cm.update(m, CustMaster.COLUMN_CUST_NO
									+ " = ?", new String[] { data[0] });
							if (r == 0) {
								cm.insert(m, null);
							}
						}
						// 设备数据
						else if (master instanceof DeviceMaster) {
							if (data.length < 3)
								continue;
							DeviceMaster dm = (DeviceMaster) master;
							Map m = new HashMap();
							int len = DeviceMaster.COLUMNS.length < data.length ? DeviceMaster.COLUMNS.length
									: data.length;
							for (int j = 0; j < len; j++) {
								m.put(DeviceMaster.COLUMNS[j], data[j]);
							}
							long r = dm
									.update(m, DeviceMaster.COLUMN_DEVICE_ID
											+ " = ? and "
											+ DeviceMaster.COLUMN_INIT_ID
											+ " = ?", new String[] { data[1],
											data[0] });
							if (r == 0) {
								dm.insert(m, null);
							}
						}
						i++;
						dialog.setProgress(i);
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 没中断线程的情况下，保存版本信息
				if (!downloadThread) {
					if (master instanceof UserMaster) {
						localVersionSave("user_master", infoUser.get("VERSION"));
					}
					// Sku
					else if (master instanceof SkuMaster) {
						localVersionSave("sku_master", infoSku.get("VERSION"));
					}
					// 客户
					else if (master instanceof CustMaster) {
						localVersionSave("cust_master", infoCust.get("VERSION"));
					}
					// 设备
					else if (master instanceof DeviceMaster) {
						localVersionSave("device_master",
								infoDevice.get("VERSION"));
					}
					handler.sendEmptyMessage(1);
				}
				dialog.cancel();
			}
		}.start();
	}

	// 数据上传
	private void dataUpload() {
		Order order = new Order(this);

		String filepath = this.getFilesDir().getPath().toString()
				+ File.separatorChar + "data";
		List<Map> orderList = order.find(Order.COLUMNS, Order.COLUMN_FLAG
				+ " = ?", new String[] { "INSERT" });
		if (orderList != null && orderList.size() > 0) {
			// 生成文件
			createFile(orderList, filepath);
			// 更新Flag
			Map updateOrder = new HashMap();
			updateOrder.put(Order.COLUMN_FLAG, "UPDATED");
			order.update(updateOrder, Order.COLUMN_FLAG + " = ?",
					new String[] { "INSERT" });
		}
		File dir = new File(filepath);
		File[] files = dir.listFiles();
		// 无文件直接返回
		if (files == null || files.length == 0) {
			return;
		}
		// 上传文件
		DeviceMaster dm = new DeviceMaster(this);
		List<Map> dl = dm.find(new String[] { DeviceMaster.COLUMN_FIELD_02,DeviceMaster.COLUMN_FIELD_03,DeviceMaster.COLUMN_FIELD_13,DeviceMaster.COLUMN_FIELD_14,DeviceMaster.COLUMN_FIELD_15,DeviceMaster.COLUMN_FIELD_16 },
				DeviceMaster.COLUMN_DEVICE_ID + " = ? and "
						+ DeviceMaster.COLUMN_INIT_ID + " = ?", new String[] {
						Util.getLocalDeviceId(this), "10" });
		if (dl != null && dl.size() > 0) {
			Map d = dl.get(0);
			String ftpUrl = (String) d.get(DeviceMaster.COLUMN_FIELD_13);
			String ftpPath = (String) d.get(DeviceMaster.COLUMN_FIELD_14);
			String ftpUser = (String) d.get(DeviceMaster.COLUMN_FIELD_15);
			String ftpPassword = (String) d.get(DeviceMaster.COLUMN_FIELD_16);
			String bakPath = (String) d.get(DeviceMaster.COLUMN_FIELD_02);
			String bakDay = (String) d.get(DeviceMaster.COLUMN_FIELD_03);
			for (File f : files) {
				Util.ftpUpload(filepath, f.getName(), ftpUrl, ftpPath, ftpUser,
						ftpPassword);
				// 备份文件
				bakup(filepath, f.getName(), bakPath, bakDay);
				//删除文件
				f.delete();
			}
		}
	}

	private void createFile(List<Map> orderList, String filepath) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		String timestamp = sdf.format(now.getTime());

		String localDeviceId = Util.getLocalDeviceId(this);
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
	
	//查看设备信息
	private void popupConfigWindow() {
		new AlertDialog.Builder(this)  
		.setTitle(this.getText(R.string.deviceInfo))  
		.setItems(new String[] {this.getText(R.string.deviceId)+"："+Util
				.getLocalDeviceId(this),this.getText(R.string.deviceMac)+"："+Util
				.getLocalMacAddress(this),this.getText(R.string.deviceIp)+"："+Util
				.getIpAddress(this)
				//,this.getText(R.string.deviceDirectory)+"："+Util.ExternalStorageDirectory()
				}, null)  
		.setNegativeButton("确定", null)  
		.show();  
		
	}
}
