package com.netsdl.android.db;

import android.content.Context;

public class UserMaster extends DatabaseHandler {
	
	public static final String TABLE_NAME = "user";

	public static final String COLUMN_USER_NO = "user_no";
	public static final String COLUMN_MD5 = "md5";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_PJ = "pj_no";
	public static final String COLUMN_ROLE = "role";
	public static final String[] COLUMNS = { COLUMN_PJ,COLUMN_USER_NO, COLUMN_MD5,
		COLUMN_NAME, COLUMN_ROLE };
	public static final String[] KEYS = { COLUMN_PJ, COLUMN_USER_NO};
	
	private static final Class<String> TYPE_USER_NO = String.class;
	private static final Class<String> TYPE_MD5 = String.class;
	private static final Class<String> TYPE_NAME = String.class;
	private static final Class<String> TYPE_PJ = String.class;
	private static final Class<String> TYPE_ROLE = String.class;
	
	public static final Class<?>[] TYPES = {TYPE_PJ,TYPE_USER_NO, TYPE_MD5, TYPE_NAME,
		TYPE_ROLE };
	
	public static final Class<?>[] KEY_TYPES = { TYPE_PJ,TYPE_USER_NO };
	

	public UserMaster(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

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
				+ COLUMN_PJ + " varchar(30), " + COLUMN_USER_NO + " varchar(30) ," + COLUMN_MD5
				+ " varchar(256), " + COLUMN_NAME + " varchar(256) ," 
				+ COLUMN_ROLE + " varchar(256) ,"+" PRIMARY KEY ("+COLUMN_PJ+","+COLUMN_USER_NO+")" + ")";
		return CREATE_TABLE;
	}

}
