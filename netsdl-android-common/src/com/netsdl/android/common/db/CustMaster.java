package com.netsdl.android.common.db;

import android.content.Context;

public class CustMaster extends DatabaseHandler {

	// Contacts Table Columns names
	public static final String COLUMN_CUST_NO = "cust_no";
	public static final String COLUMN_CUST_NAME = "cust_name";
	public static final String COLUMN_CUST_TYPE = "cust_type";
	public static final String COLUMN_CUST_CAT = "cust_cat";
	public static final String COLUMN_OFF_RATE = "off_rate";

	public static final String[] COLUMNS = { COLUMN_CUST_NO, COLUMN_CUST_NAME,
		COLUMN_CUST_TYPE, COLUMN_CUST_CAT,COLUMN_OFF_RATE };

	public static final String[] KEYS = { COLUMN_CUST_NO };

	private static final Class<String> TYPE_CUST_NO = String.class;
	private static final Class<String> TYPE_CUST_NAME = String.class;
	private static final Class<String> TYPE_CUST_TYPE = String.class;
	private static final Class<String> TYPE_CUST_CAT = String.class;
	private static final Class<Integer> TYPE_OFF_RATE = Integer.class;

	public static final Class<?>[] TYPES = { TYPE_CUST_NO, TYPE_CUST_NAME, TYPE_CUST_TYPE,
		TYPE_CUST_CAT,TYPE_OFF_RATE };

	public static final Class<?>[] KEY_TYPES = { TYPE_CUST_NO };

	public CustMaster(Context context) {
		super(context);
	}

	// Contacts table name
	public static final String TABLE_NAME = "cust_master";

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
				+ COLUMN_CUST_NO + " varchar(100), " + COLUMN_CUST_NAME + " varchar(256) ,"+ COLUMN_CUST_TYPE + " varchar(32) ,"+ COLUMN_CUST_CAT + " varchar(32) ,"+ COLUMN_OFF_RATE + " int(10) ,"+" PRIMARY KEY ("+COLUMN_CUST_NO+")" + ")";

		return CREATE_TABLE;
	}
}
