package com.netsdl.android.common.db;

import android.content.Context;

public class DbMaster extends DatabaseHandler {

	// Contacts Table Columns names
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_VERSION = "version";

	public static final String[] COLUMNS = { COLUMN_NAME, COLUMN_VERSION };

	public static final String[] KEYS = { COLUMN_NAME };

	private static final Class<String> TYPE_NAME = String.class;
	private static final Class<String> TYPE_VERSION = String.class;

	public static final Class<?>[] TYPES = { TYPE_NAME, TYPE_VERSION };

	public static final Class<?>[] KEY_TYPES = { TYPE_NAME };

	public DbMaster(Context context) {
		super(context);
	}

	// Contacts table name
	public static final String TABLE_NAME = "db_master";

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
		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
				+ " (" + COLUMN_NAME + " varchar(256) PRIMARY KEY NOT NULL ,"
				+ COLUMN_VERSION + " varchar(256) )";
		return CREATE_TABLE;
	}

}
