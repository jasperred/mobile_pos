/**
 * 
 */
package com.netsdl.android.main.view.manage;

import java.io.File;
import java.math.BigDecimal;
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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.netsdl.android.common.Constant;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.CheckOrderTable;
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
import com.netsdl.android.main.view.manage.ReturnToWhActivity.ItemAdapter;
import com.netsdl.android.main.view.manage.ReturnToWhActivity.ItemAdapter.ViewHolder;

/**
 * @author jasper
 * 
 */
public class ShopCheckUploadActivity extends Activity {

	private List<Map<String, Object>> dataList;// 保存数据

	private ItemAdapter listItemAdapter;
	private int currentPosition = -1;// 记录明细选择的位置

	private String checkFilePath;
	private String checkFileBakPath;
	private String bakDay;
	private String ftpUrl;
	private String ftpPath;
	private String ftpUser;
	private String ftpPassword;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_upload);
		setTitle(R.string.check_upload_title);
		init();
		initData();

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
		// 刷新文件事件
		((Button) this.findViewById(R.id.refreshButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							refreshFile();
						} catch (RuntimeException e) {
							Toast.makeText(ShopCheckUploadActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		// 上传文件事件
		((Button) this.findViewById(R.id.uploadCheckFileButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							uploadFile();
						} catch (RuntimeException e) {
							Toast.makeText(ShopCheckUploadActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		// 上传全部文件
		((Button) this.findViewById(R.id.uploadAllCheckFileButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							uploadAllFile();
						} catch (RuntimeException e) {
							Toast.makeText(ShopCheckUploadActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		// 删除文件
		((Button) this.findViewById(R.id.deleteCheckFileButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							deleteFile();
						} catch (RuntimeException e) {
							Toast.makeText(ShopCheckUploadActivity.this,
									e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}

				});
		if (dataList == null)
			dataList = new ArrayList(20);
		dataList.clear();
		// 初始化明细的表头
		final ListView listViewItem = (ListView) this
				.findViewById(R.id.detailListView);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout head = (LinearLayout) inflater.inflate(
				R.layout.view_check_file_detail, null);
		head.setBackgroundColor(Color.LTGRAY);
		head.setBaselineAligned(true);
		listViewItem.addHeaderView(head);
		if (listItemAdapter == null) {
			listItemAdapter = new ItemAdapter(this);
			listViewItem.setAdapter(listItemAdapter);
		}
	}

	// 初始化数据组件控制
	private void initData() {
		Object[] objs = null;
		try {
			objs = DatabaseHelper.getSingleColumn(this.getContentResolver(),
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
		String path = (String) DatabaseHelper.getColumnValue(objs,
				DeviceMaster.COLUMN_FIELD_02, DeviceMaster.COLUMNS);
		ftpUrl = DatabaseHelper.getColumnValue(objs,
				DeviceMaster.COLUMN_FIELD_13, DeviceMaster.COLUMNS).toString();
		ftpPath = DatabaseHelper.getColumnValue(objs,
				DeviceMaster.COLUMN_FIELD_14, DeviceMaster.COLUMNS).toString();
		ftpUser = DatabaseHelper.getColumnValue(objs,
				DeviceMaster.COLUMN_FIELD_15, DeviceMaster.COLUMNS).toString();
		ftpPassword = DatabaseHelper.getColumnValue(objs,
				DeviceMaster.COLUMN_FIELD_16, DeviceMaster.COLUMNS).toString();
		bakDay = (String) DatabaseHelper.getColumnValue(objs,
				DeviceMaster.COLUMN_FIELD_03, DeviceMaster.COLUMNS);
		if (path != null) {
			checkFilePath = path + File.separatorChar + "check";
			checkFileBakPath = path + File.separatorChar + "check_bak";
		}
		refreshFile();
	}

	// 刷新文件列表
	protected void refreshFile() {
		dataList.clear();
		if (checkFilePath != null) {
			File[] fileList = new File(checkFilePath).listFiles();
			for (File f : fileList) {
				Map m = new HashMap();
				m.put("fileName", f.getName());
				long size = f.length();
				BigDecimal s = new BigDecimal(size).divide(
						new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
				m.put("fileSize", s.doubleValue() + "K");
				m.put("filePath", f.getAbsolutePath());
				dataList.add(m);
			}
		}
		this.listItemAdapter.notifyDataSetChanged();
	}

	// 删除文件
	protected void deleteFile() {
		for (Map m : dataList) {
			if (m.get("check") != null && (Boolean) m.get("check")) {
				String path = (String) m.get("filePath");
				new File(path).delete();
			}
		}
		refreshFile();
	}

	protected void uploadAllFile() {
		for (Map m : dataList) {
			String path = (String) m.get("filePath");
			String fileName = (String) m.get("fileName");
			bakAndUploadFile(path, fileName);
		}
		refreshFile();
	}

	protected void uploadFile() {
		for (Map m : dataList) {
			if (m.get("check") != null && (Boolean) m.get("check")) {
				String path = (String) m.get("filePath");
				String fileName = (String) m.get("fileName");
				bakAndUploadFile(path, fileName);
			}
		}
		refreshFile();
	}

	// 上传和备份文件
	private void bakAndUploadFile(final String file, final String fileName) {
		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					// 备份数据
					bakup(checkFileBakPath, fileName);
					// 删除生成的文件
					new File(file).delete();
					break;
				case 1:
					break;
				}
			};
		};
		new Thread(new Runnable() {

			public void run() {

				// 上传数据
				boolean b = Util.ftpUpload(checkFilePath, fileName, ftpUrl,
						ftpPath, ftpUser, ftpPassword);
				// 上传成功才备份
				if (b) {
					handler.sendEmptyMessage(0);
				} else
					handler.sendEmptyMessage(1);
			}
		}).start();
	}

	// 备份数据，并删除多余备份
	private void bakup(String filepath, String filename) {

		// 无备份路径不备份文件，备份路径无法建立也不备份文件
		if (checkFileBakPath == null || checkFileBakPath.trim().length() == 0)
			return;
		File bakDir = new File(checkFileBakPath);
		try {
			if (!bakDir.exists())
				bakDir.mkdirs();
		} catch (Exception e) {
			return;
		}
		int copyFile = Util.CopyFile(filepath + File.separatorChar + filename,
				checkFileBakPath + File.separatorChar + filename);
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
				// 得到文件的日期
				String sn = null;
				try {
					sn = f.getName().substring(
							f.getName().lastIndexOf("-") + 1,
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
				convertView = inflater.inflate(R.layout.view_check_file_detail,
						parent, false);
				holder = new ViewHolder();
				holder.fileCheckCheckBox = (CheckBox) convertView
						.findViewById(R.id.fileCheckCheckBox);
				holder.fileCheckCheckBox
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {

								Map data = dataList.get(position);
								data.put("check", isChecked);
							}
						});
				holder.fileNameTextView = (TextView) convertView
						.findViewById(R.id.fileNameTextView);
				holder.fileSizeTextView = (TextView) convertView
						.findViewById(R.id.fileSizeTextView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Map data = dataList.get(position);
			holder.fileCheckCheckBox.setChecked(false);
			holder.fileNameTextView.setText(data.get("fileName").toString());
			holder.fileSizeTextView.setText(data.get("fileSize").toString());
			if (position == currentPosition) {
			} else {
			}

			return convertView;
		}

		class ViewHolder {
			public CheckBox fileCheckCheckBox;
			public TextView fileNameTextView;
			public TextView fileSizeTextView;
		}
	}
}
