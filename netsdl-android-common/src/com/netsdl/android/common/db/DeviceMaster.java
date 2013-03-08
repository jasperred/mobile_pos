package com.netsdl.android.common.db;

import android.content.Context;

public class DeviceMaster extends DatabaseHandler {

	// Contacts Table Columns names
	public static final String COLUMN_INIT_ID = "init_id"; // 初始化ID
	public static final String COLUMN_DEVICE_ID = "device_id"; // 设备ID
	public static final String COLUMN_FIELD_01 = "field_01"; // 域01
	public static final String COLUMN_FIELD_02 = "field_02"; // 域02
	public static final String COLUMN_FIELD_03 = "field_03"; // 域03
	public static final String COLUMN_FIELD_04 = "field_04"; // 域04--(1-营业日期)
	public static final String COLUMN_FIELD_05 = "field_05"; // 域05
	public static final String COLUMN_FIELD_06 = "field_06"; // 域06
	public static final String COLUMN_FIELD_07 = "field_07"; // 域07
	public static final String COLUMN_FIELD_08 = "field_08"; // 域08
	public static final String COLUMN_FIELD_09 = "field_09"; // 域09
	public static final String COLUMN_FIELD_10 = "field_10"; // 域10
	public static final String COLUMN_FIELD_11 = "field_11"; // 域11
	public static final String COLUMN_FIELD_12 = "field_12"; // 域12
	public static final String COLUMN_FIELD_13 = "field_13"; // 域13
	public static final String COLUMN_FIELD_14 = "field_14"; // 域14
	public static final String COLUMN_FIELD_15 = "field_15"; // 域15
	public static final String COLUMN_FIELD_16 = "field_16"; // 域16
	public static final String COLUMN_FIELD_17 = "field_17"; // 域17
	public static final String COLUMN_FIELD_18 = "field_18"; // 域18
	public static final String COLUMN_FIELD_19 = "field_19"; // 域19
	public static final String COLUMN_FIELD_20 = "field_20"; // 域20

	public static final String[] COLUMNS = { COLUMN_INIT_ID, COLUMN_DEVICE_ID,
			COLUMN_FIELD_01, COLUMN_FIELD_02, COLUMN_FIELD_03, COLUMN_FIELD_04,
			COLUMN_FIELD_05, COLUMN_FIELD_06, COLUMN_FIELD_07, COLUMN_FIELD_08,
			COLUMN_FIELD_09, COLUMN_FIELD_10, COLUMN_FIELD_11, COLUMN_FIELD_12,
			COLUMN_FIELD_13, COLUMN_FIELD_14, COLUMN_FIELD_15, COLUMN_FIELD_16,
			COLUMN_FIELD_17, COLUMN_FIELD_18, COLUMN_FIELD_19, COLUMN_FIELD_20, };

	public static final String[] KEYS = { COLUMN_INIT_ID, COLUMN_DEVICE_ID };

	public static final Class<Integer> TYPE_INIT_ID = Integer.class;
	public static final Class<String> TYPE_DEVICE_ID = String.class;
	public static final Class<String> TYPE_FIELD_01 = String.class;
	public static final Class<String> TYPE_FIELD_02 = String.class;
	public static final Class<String> TYPE_FIELD_03 = String.class;
	public static final Class<String> TYPE_FIELD_04 = String.class;
	public static final Class<String> TYPE_FIELD_05 = String.class;
	public static final Class<String> TYPE_FIELD_06 = String.class;
	public static final Class<String> TYPE_FIELD_07 = String.class;
	public static final Class<String> TYPE_FIELD_08 = String.class;
	public static final Class<String> TYPE_FIELD_09 = String.class;
	public static final Class<String> TYPE_FIELD_10 = String.class;
	public static final Class<String> TYPE_FIELD_11 = String.class;
	public static final Class<String> TYPE_FIELD_12 = String.class;
	public static final Class<String> TYPE_FIELD_13 = String.class;
	public static final Class<String> TYPE_FIELD_14 = String.class;
	public static final Class<String> TYPE_FIELD_15 = String.class;
	public static final Class<String> TYPE_FIELD_16 = String.class;
	public static final Class<String> TYPE_FIELD_17 = String.class;
	public static final Class<String> TYPE_FIELD_18 = String.class;
	public static final Class<String> TYPE_FIELD_19 = String.class;
	public static final Class<String> TYPE_FIELD_20 = String.class;

	public static final Class<?>[] TYPES = { TYPE_INIT_ID, TYPE_DEVICE_ID,
			TYPE_FIELD_01, TYPE_FIELD_02, TYPE_FIELD_03, TYPE_FIELD_04,
			TYPE_FIELD_05, TYPE_FIELD_06, TYPE_FIELD_07, TYPE_FIELD_08,
			TYPE_FIELD_09, TYPE_FIELD_10, TYPE_FIELD_11, TYPE_FIELD_12,
			TYPE_FIELD_13, TYPE_FIELD_14, TYPE_FIELD_15, TYPE_FIELD_16,
			TYPE_FIELD_17, TYPE_FIELD_18, TYPE_FIELD_19, TYPE_FIELD_20, };

	public static final Class<?>[] KEY_TYPES = { TYPE_INIT_ID, TYPE_DEVICE_ID };

	public DeviceMaster(Context context) {
		super(context);
	}

	// Contacts table name
	public static final String TABLE_NAME = "device_master";

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String[] getColumns() {
		return COLUMNS;
	}

	@Override
	public String[] getKeys() {
		return KEYS;
	}

	@Override
	public Class<?>[] getTypes() {
		return TYPES;
	}

	@Override
	public Class<?>[] getKeyTypes() {
		return KEY_TYPES;
	}

	@Override
	public String getCreateTableSql() {
		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ COLUMN_INIT_ID + " int," + COLUMN_DEVICE_ID
				+ " varchar(256)," + COLUMN_FIELD_01 + " varchar(256),"
				+ COLUMN_FIELD_02 + " varchar(256)," + COLUMN_FIELD_03
				+ " varchar(256)," + COLUMN_FIELD_04 + " varchar(256),"
				+ COLUMN_FIELD_05 + " varchar(256)," + COLUMN_FIELD_06
				+ " varchar(256)," + COLUMN_FIELD_07 + " varchar(256),"
				+ COLUMN_FIELD_08 + " varchar(256)," + COLUMN_FIELD_09
				+ " varchar(256)," + COLUMN_FIELD_10 + " varchar(256),"
				+ COLUMN_FIELD_11 + " varchar(256)," + COLUMN_FIELD_12
				+ " varchar(256)," + COLUMN_FIELD_13 + " varchar(256),"
				+ COLUMN_FIELD_14 + " varchar(256)," + COLUMN_FIELD_15
				+ " varchar(256)," + COLUMN_FIELD_16 + " varchar(256),"
				+ COLUMN_FIELD_17 + " varchar(256)," + COLUMN_FIELD_18
				+ " varchar(256)," + COLUMN_FIELD_19 + " varchar(256),"
				+ COLUMN_FIELD_20 + " varchar(256)" + ", primary key ( "
				+ COLUMN_DEVICE_ID + " ," + COLUMN_INIT_ID + " ) " + " )";
		return CREATE_TABLE;
	}

}
