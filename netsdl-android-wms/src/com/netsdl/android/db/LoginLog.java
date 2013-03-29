package com.netsdl.android.db;

import android.content.Context;

public class LoginLog extends DatabaseHandler {
	
	public static final String TABLE_NAME = "login_log";

	public static final String COLUMN_ID = "login_id";
	public static final String COLUMN_USER_NO = "user_no";
	public static final String COLUMN_PJ = "pj_no";
	public static final String COLUMN_LOGIN_TIME= "login_time";
	public static final String COLUMN_STATUS = "status";
	public static final String[] COLUMNS = {COLUMN_ID, COLUMN_PJ,COLUMN_USER_NO, COLUMN_LOGIN_TIME ,COLUMN_STATUS};
	public static final String[] KEYS = {COLUMN_ID};
	
	private static final Class<String> TYPE_USER_NO = String.class;
	private static final Class<String> TYPE_ID = String.class;
	private static final Class<String> TYPE_PJ = String.class;
	private static final Class<String> TYPE_LOGIN_TIME = String.class;
	private static final Class<Integer> TYPE_STATUS  = Integer.class;
	
	public static final Class<?>[] TYPES = {TYPE_ID,TYPE_PJ,TYPE_USER_NO, TYPE_LOGIN_TIME ,TYPE_STATUS};
	
	public static final Class<?>[] KEY_TYPES = {TYPE_ID};
	

	public LoginLog(Context context) {
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
				+COLUMN_ID + " varchar(50) ," + COLUMN_PJ + " varchar(30), " +  COLUMN_USER_NO
				+ " varchar(30), " + COLUMN_LOGIN_TIME + " datetime," 
				+ COLUMN_STATUS + " int,"+" PRIMARY KEY ("+COLUMN_ID+")" + ")";
		return CREATE_TABLE;
	}

}
