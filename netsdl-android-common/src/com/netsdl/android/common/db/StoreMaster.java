package com.netsdl.android.common.db;

import android.content.Context;

public class StoreMaster extends DatabaseHandler {

	// Contacts Table Columns names
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_MD5 = "md5";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ROLE = "role";

	public static final String[] COLUMNS = { COLUMN_ID, COLUMN_MD5,
			COLUMN_NAME, COLUMN_ROLE };

	public static final String[] KEYS = { COLUMN_ID };

	private static final Class<Integer> TYPE_ID = Integer.class;
	private static final Class<String> TYPE_MD5 = String.class;
	private static final Class<String> TYPE_NAME = String.class;
	private static final Class<String> TYPE_ROLE = String.class;

	public static final Class<?>[] TYPES = { TYPE_ID, TYPE_MD5, TYPE_NAME,
			TYPE_ROLE };

	public static final Class<?>[] KEY_TYPES = { TYPE_ID };

	public StoreMaster(Context context) {
		super(context);
	}

	// Contacts table name
	public static final String TABLE_NAME = "store_master";

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
				+ COLUMN_ID + " int PRIMARY KEY NOT NULL " + "," + COLUMN_MD5
				+ " varchar(256) " + "," + COLUMN_NAME + " varchar(256) " + ","
				+ COLUMN_ROLE + " varchar(256) " + ")";
		return CREATE_TABLE;
	}
}
