package com.netsdl.android.main.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.DeviceMaster;
import com.netsdl.android.common.db.StoreMaster;
import com.netsdl.android.common.Util;
import com.netsdl.android.main.R;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class PreMain {

	public static final int LAYOUT_COMMON33a = R.layout.common33a;
	final View view;
	final LayoutInflater inflater;
	LinearLayout linearLayoutType;
	final FrameLayout coreLayout;
	MainActivity parent;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String[] spinnerOperatorNos;
	String[] spinnerOperatorNames;

	public PreMain(MainActivity parent) {
		this.parent = parent;
		inflater = LayoutInflater.from(parent);
		view = inflater.inflate(LAYOUT_COMMON33a, null);

		coreLayout = (FrameLayout) view.findViewById(R.id.core);

	}

	public void init() {
		parent.status = MainActivity.Status.PreMain;
		parent.setContentView(view);
		coreLayout.removeAllViews();
		initPrinterIP();
		switch (parent.type) {
		case type1:
			initType1();
			break;
		case type2:
			initType2();
			break;
		case type3:
			initType3();
			break;
		default:
			linearLayoutType = (LinearLayout) inflater.inflate(R.layout.type1,
					null);
		}

		final Button buttonBack = (Button) parent.findViewById(R.id.buttonBack);
		buttonBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				parent.function.init();
			}
		});

		final Button buttonEnter = (Button) parent
				.findViewById(R.id.buttonEnter);
		buttonEnter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				parent.main.init();
			}
		});

		((EditText) parent.findViewById(R.id.editDocumentDate))
				.setInputType(InputType.TYPE_NULL);

		((EditText) parent.findViewById(R.id.editDocumentDate))
				.setOnTouchListener(new OnTouchListener() {

					public boolean onTouch(View v, MotionEvent event) {

						try {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								Date date = sdf
										.parse(parent.deviceItem.documentDate);
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);

								new DatePickerDialog(
										parent,
										new OnDateSetListener() {

											public void onDateSet(
													DatePicker view, int year,
													int monthOfYear,
													int dayOfMonth) {
												Calendar calendar = Calendar
														.getInstance();
												calendar.set(Calendar.YEAR,
														year);
												calendar.set(Calendar.MONTH,
														monthOfYear);
												calendar.set(
														Calendar.DAY_OF_MONTH,
														dayOfMonth);
												String strDocumentDate = sdf
														.format(calendar
																.getTime());

												parent.deviceItem.documentDate = strDocumentDate;
												((EditText) parent
														.findViewById(R.id.editDocumentDate))
														.setText(strDocumentDate);

											}
										}, calendar.get(Calendar.YEAR),
										calendar.get(Calendar.MONTH), calendar
												.get(Calendar.DAY_OF_MONTH))
										.show();
							}
							return true;

						} catch (ParseException e) {
						}

						return false;
					}
				});

		((EditText) parent.findViewById(R.id.editDocumentDate))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
					}
				});

		((Spinner) parent.findViewById(R.id.spinnerOperator))
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int position, long id) {
						parent.deviceItem.operator = new String[2];
						parent.deviceItem.operator[0] = spinnerOperatorNos[position];
						parent.deviceItem.operator[1] = spinnerOperatorNames[position];
						// parent.deviceItem.operator[0] = adapterView
						// .getItemAtPosition(position).toString();

					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

	}

	private void initType1() {
		linearLayoutType = (LinearLayout) inflater
				.inflate(R.layout.type1, null);
		coreLayout.addView(linearLayoutType);

		String strDeviceId = Util.getLocalDeviceId(parent);
		if (strDeviceId == null || strDeviceId.trim().length() == 0)
			strDeviceId = Util.DEFAULT_LOCAL_DEVICE_ID;
		parent.deviceItem.deviceID = strDeviceId;
		try {
			Object[] deviceMasterObjs = DatabaseHelper.getSingleColumn(
					parent.getContentResolver(), new Object[] { "1",
							strDeviceId }, DeviceMaster.class);
			if (deviceMasterObjs == null) {
				strDeviceId = Util.DEFAULT_LOCAL_DEVICE_ID;
				deviceMasterObjs = DatabaseHelper.getSingleColumn(
						parent.getContentResolver(), new Object[] { "1",
								strDeviceId }, DeviceMaster.class);
				parent.deviceItem.deviceID = strDeviceId;
			}

			if (deviceMasterObjs != null) {
				String[] shop = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_01,
						DeviceMaster.COLUMNS)).split(";");

				((EditText) parent.findViewById(R.id.editShop))
						.setText(shop[1]);
				parent.deviceItem.shop = shop;

				String[] custom = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_02,
						DeviceMaster.COLUMNS)).split(";");
				((EditText) parent.findViewById(R.id.editCustomer))
						.setText(custom[1]);
				parent.deviceItem.custom = custom;

				String[] salesType = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_03,
						DeviceMaster.COLUMNS)).split(";");
				((EditText) parent.findViewById(R.id.editSalesType))
						.setText(salesType[1]);
				parent.deviceItem.salesType = salesType;

				String strDocumentDate = (String) DatabaseHelper
						.getColumnValue(deviceMasterObjs,
								DeviceMaster.COLUMN_FIELD_04,
								DeviceMaster.COLUMNS);
				if (strDocumentDate == null
						|| strDocumentDate.trim().length() == 0) {
					Calendar now = Calendar.getInstance();
					now.setTimeInMillis(System.currentTimeMillis());
					strDocumentDate = sdf.format(now.getTime());
				}
				parent.deviceItem.documentDate = strDocumentDate;
				((EditText) parent.findViewById(R.id.editDocumentDate))
						.setText(strDocumentDate);

				String strOperator = (String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_05,
						DeviceMaster.COLUMNS);
				Spinner spinnerOperator = (Spinner) parent
						.findViewById(R.id.spinnerOperator);
				spinnerOperatorNos = strOperator.split(":");
				spinnerOperatorNames = new String[spinnerOperatorNos.length];
				for (int i = 0; i < spinnerOperatorNos.length; i++) {
					Object[] storeObjs = DatabaseHelper.getSingleColumn(parent
							.getContentResolver(), new Object[] { Integer
							.parseInt(spinnerOperatorNos[i]) },
							StoreMaster.class);
					if (storeObjs != null) {
						spinnerOperatorNames[i] = (String) DatabaseHelper
								.getColumnValue(storeObjs,
										StoreMaster.COLUMN_NAME,
										StoreMaster.COLUMNS);
					} else {
						spinnerOperatorNames[i] = "";
					}

				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent,
						android.R.layout.simple_spinner_item,
						spinnerOperatorNames);
				spinnerOperator.setAdapter(adapter);
				parent.deviceItem.operator = new String[2];
				parent.deviceItem.operator[0] = spinnerOperatorNos[0];
				parent.deviceItem.operator[1] = spinnerOperatorNames[0];

				String strRemarks = (String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_06,
						DeviceMaster.COLUMNS);
				((EditText) parent.findViewById(R.id.editRemarks))
						.setText(strRemarks);
				parent.deviceItem.remarks = strRemarks;
			}

		} catch (IllegalArgumentException e1) {
		} catch (SecurityException e1) {
		} catch (IllegalAccessException e1) {
		} catch (NoSuchFieldException e1) {
		}

	}

	private void initType2() {

		linearLayoutType = (LinearLayout) inflater
				.inflate(R.layout.type2, null);
		coreLayout.addView(linearLayoutType);

		String strDeviceId = Util.getLocalDeviceId(parent);
		if (strDeviceId == null || strDeviceId.trim().length() == 0)
			strDeviceId = Util.DEFAULT_LOCAL_DEVICE_ID;
		parent.deviceItem.deviceID = strDeviceId;
		try {
			Object[] deviceMasterObjs = DatabaseHelper.getSingleColumn(
					parent.getContentResolver(), new Object[] { "2",
							strDeviceId }, DeviceMaster.class);
			if (deviceMasterObjs == null) {
				strDeviceId = Util.DEFAULT_LOCAL_DEVICE_ID;
				deviceMasterObjs = DatabaseHelper.getSingleColumn(
						parent.getContentResolver(), new Object[] { "2",
								strDeviceId }, DeviceMaster.class);
				parent.deviceItem.deviceID = strDeviceId;
			}

			if (deviceMasterObjs != null) {
				String[] shop = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_01,
						DeviceMaster.COLUMNS)).split(";");

				((EditText) parent.findViewById(R.id.editShop))
						.setText(shop[1]);
				parent.deviceItem.shop = shop;

				String[] custom = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_02,
						DeviceMaster.COLUMNS)).split(";");
				((EditText) parent.findViewById(R.id.editCustomer))
						.setText(custom[1]);
				parent.deviceItem.custom = custom;

				String[] salesType = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_03,
						DeviceMaster.COLUMNS)).split(";");
				((EditText) parent.findViewById(R.id.editSalesType))
						.setText(salesType[1]);
				parent.deviceItem.salesType = salesType;

				String strDocumentDate = (String) DatabaseHelper
						.getColumnValue(deviceMasterObjs,
								DeviceMaster.COLUMN_FIELD_04,
								DeviceMaster.COLUMNS);
				if (strDocumentDate == null
						|| strDocumentDate.trim().length() == 0) {
					Calendar now = Calendar.getInstance();
					now.setTimeInMillis(System.currentTimeMillis());
					strDocumentDate = sdf.format(now.getTime());
				}
				parent.deviceItem.documentDate = strDocumentDate;
				((EditText) parent.findViewById(R.id.editDocumentDate))
						.setText(strDocumentDate);

				String strOperator = (String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_05,
						DeviceMaster.COLUMNS);
				Spinner spinnerOperator = (Spinner) parent
						.findViewById(R.id.spinnerOperator);
				spinnerOperatorNos = strOperator.split(":");
				spinnerOperatorNames = new String[spinnerOperatorNos.length];
				for (int i = 0; i < spinnerOperatorNos.length; i++) {
					Object[] storeObjs = DatabaseHelper.getSingleColumn(parent
							.getContentResolver(), new Object[] { Integer
							.parseInt(spinnerOperatorNos[i]) },
							StoreMaster.class);
					if (storeObjs != null) {
						spinnerOperatorNames[i] = (String) DatabaseHelper
								.getColumnValue(storeObjs,
										StoreMaster.COLUMN_NAME,
										StoreMaster.COLUMNS);
					} else {
						spinnerOperatorNames[i] = "";
					}

				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent,
						android.R.layout.simple_spinner_item,
						spinnerOperatorNames);
				spinnerOperator.setAdapter(adapter);
				parent.deviceItem.operator = new String[2];
				parent.deviceItem.operator[0] = spinnerOperatorNos[0];
				parent.deviceItem.operator[1] = spinnerOperatorNames[0];

				String strRemarks = (String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_06,
						DeviceMaster.COLUMNS);
				((EditText) parent.findViewById(R.id.editRemarks))
						.setText(strRemarks);
				parent.deviceItem.remarks = strRemarks;
			}

		} catch (IllegalArgumentException e1) {
		} catch (SecurityException e1) {
		} catch (IllegalAccessException e1) {
		} catch (NoSuchFieldException e1) {
		}
	}

	private void initType3() {

		linearLayoutType = (LinearLayout) inflater
				.inflate(R.layout.type3, null);
		coreLayout.addView(linearLayoutType);

		String strDeviceId = Util.getLocalDeviceId(parent);
		if (strDeviceId == null || strDeviceId.trim().length() == 0)
			strDeviceId = Util.DEFAULT_LOCAL_DEVICE_ID;
		parent.deviceItem.deviceID = strDeviceId;
		try {
			Object[] deviceMasterObjs = DatabaseHelper.getSingleColumn(
					parent.getContentResolver(), new Object[] { "3",
							strDeviceId }, DeviceMaster.class);
			if (deviceMasterObjs == null) {
				strDeviceId = Util.DEFAULT_LOCAL_DEVICE_ID;
				deviceMasterObjs = DatabaseHelper.getSingleColumn(
						parent.getContentResolver(), new Object[] { "3",
								strDeviceId }, DeviceMaster.class);
				parent.deviceItem.deviceID = strDeviceId;
			}

			if (deviceMasterObjs != null) {
				String[] outOfShop = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_01,
						DeviceMaster.COLUMNS)).split(";");

				((EditText) parent.findViewById(R.id.editOutOfShop))
						.setText(outOfShop[1]);
				parent.deviceItem.outOfShop = outOfShop;

				String[] custom = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_02,
						DeviceMaster.COLUMNS)).split(";");
				((EditText) parent.findViewById(R.id.editCustomer))
						.setText(custom[1]);
				//默认的VIP，off_rate为100
				String[] c2 = new String[3];
				System.arraycopy(custom, 0, c2, 0, 2);
				c2[2] = "100";
				parent.deviceItem.custom = c2;

				String[] salesType = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_03,
						DeviceMaster.COLUMNS)).split(";");
				((EditText) parent.findViewById(R.id.editSalesType))
						.setText(salesType[1]);
				parent.deviceItem.salesType = salesType;

				String strDocumentDate = (String) DatabaseHelper
						.getColumnValue(deviceMasterObjs,
								DeviceMaster.COLUMN_FIELD_04,
								DeviceMaster.COLUMNS);
				if (strDocumentDate == null
						|| strDocumentDate.trim().length() == 0) {
					Calendar now = Calendar.getInstance();
					now.setTimeInMillis(System.currentTimeMillis());
					strDocumentDate = sdf.format(now.getTime());
				}
				parent.deviceItem.documentDate = strDocumentDate;
				((EditText) parent.findViewById(R.id.editDocumentDate))
						.setText(strDocumentDate);

				String strOperator = (String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_05,
						DeviceMaster.COLUMNS);
				Spinner spinnerOperator = (Spinner) parent
						.findViewById(R.id.spinnerOperator);
				spinnerOperatorNos = strOperator.split(":");
				spinnerOperatorNames = new String[spinnerOperatorNos.length];
				for (int i = 0; i < spinnerOperatorNos.length; i++) {
					Object[] storeObjs = DatabaseHelper.getSingleColumn(parent
							.getContentResolver(), new Object[] { Integer
							.parseInt(spinnerOperatorNos[i]) },
							StoreMaster.class);
					if (storeObjs != null) {
						spinnerOperatorNames[i] = (String) DatabaseHelper
								.getColumnValue(storeObjs,
										StoreMaster.COLUMN_NAME,
										StoreMaster.COLUMNS);
					} else {
						spinnerOperatorNames[i] = "";
					}

				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent,
						android.R.layout.simple_spinner_item,
						spinnerOperatorNames);
				spinnerOperator.setAdapter(adapter);
				parent.deviceItem.operator = new String[2];
				parent.deviceItem.operator[0] = spinnerOperatorNos[0];
				parent.deviceItem.operator[1] = spinnerOperatorNames[0];

				String strRemarks = (String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_06,
						DeviceMaster.COLUMNS);
				((EditText) parent.findViewById(R.id.editRemarks))
						.setText(strRemarks);
				parent.deviceItem.remarks = strRemarks;
			}

		} catch (IllegalArgumentException e1) {
		} catch (SecurityException e1) {
		} catch (IllegalAccessException e1) {
		} catch (NoSuchFieldException e1) {
		}

	}

	private void initPrinterIP() {
		String strDeviceId = Util.getLocalDeviceId(parent);

		try {
			Object[] deviceMasterObjs = DatabaseHelper.getSingleColumn(
					parent.getContentResolver(), new Object[] { "9",
							strDeviceId }, DeviceMaster.class);

			if (deviceMasterObjs != null) {
				parent.deviceItem.printWSDL = (String) DatabaseHelper
						.getColumnValue(deviceMasterObjs,
								DeviceMaster.COLUMN_FIELD_20,
								DeviceMaster.COLUMNS);
				parent.deviceItem.printNameSpace = (String) DatabaseHelper
						.getColumnValue(deviceMasterObjs,
								DeviceMaster.COLUMN_FIELD_19,
								DeviceMaster.COLUMNS);
				parent.deviceItem.printMethod = (String) DatabaseHelper
						.getColumnValue(deviceMasterObjs,
								DeviceMaster.COLUMN_FIELD_18,
								DeviceMaster.COLUMNS);
				String[] printFlags = ((String) DatabaseHelper.getColumnValue(
						deviceMasterObjs, DeviceMaster.COLUMN_FIELD_17,
						DeviceMaster.COLUMNS)).split(":");
				parent.deviceItem.printFlagIn = printFlags[0];
				parent.deviceItem.printFlagOut = printFlags[1];
			}

		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (NoSuchFieldException e) {
		}
	}

}
