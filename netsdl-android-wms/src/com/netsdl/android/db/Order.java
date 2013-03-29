package com.netsdl.android.db;

import android.content.Context;

public class Order extends DatabaseHandler {

	// Contacts Table Columns names
	public static final String COLUMN_ORDER_NO= "order_no";
	public static final String COLUMN_DEVICE_ID = "device_id";
	public static final String COLUMN_ORDER_DATE = "order_date";
	public static final String COLUMN_CREATE_DATE = "create_date";
	public static final String COLUMN_ORDER_TYPE= "order_type";
	public static final String COLUMN_INV_TYPE= "inv_type";
	public static final String COLUMN_RTN = "rtn";
	public static final String COLUMN_WH_NO = "wh_no";
	public static final String COLUMN_WH_NAME = "wh_name";
	public static final String COLUMN_CUST_NO = "cust_no";
	public static final String COLUMN_CUST_NAME = "cust_name";
	public static final String COLUMN_USER_NO= "user_no";
	public static final String COLUMN_USER_NAME = "user_name";
	public static final String COLUMN_FLAG = "flag";
	public static final String COLUMN_SKU_CD = "sku_cd";
	public static final String COLUMN_ITEM_NAME = "item_name";
	public static final String COLUMN_QTY = "qty";

	public static final String[] COLUMNS = { COLUMN_ORDER_NO, COLUMN_DEVICE_ID, COLUMN_ORDER_DATE, COLUMN_CREATE_DATE, COLUMN_ORDER_TYPE,COLUMN_INV_TYPE,
		COLUMN_RTN, COLUMN_WH_NO, COLUMN_WH_NAME, COLUMN_CUST_NO, COLUMN_CUST_NAME, COLUMN_USER_NO, COLUMN_USER_NAME,
		COLUMN_FLAG, COLUMN_SKU_CD, COLUMN_ITEM_NAME, COLUMN_QTY };

	public static final String[] KEYS = { COLUMN_ORDER_NO,COLUMN_SKU_CD };

	public static final Class<String> TYPE_ORDER_NO= String.class;
	public static final Class<String> TYPE_DEVICE_ID = String.class;
	public static final Class<String> TYPE_ORDER_DATE = String.class;
	public static final Class<String> TYPE_CREATE_DATE =String.class;
	public static final Class<String> TYPE_ORDER_TYPE= String.class;
	public static final Class<String> TYPE_INV_TYPE= String.class;
	public static final Class<Integer> TYPE_RTN = Integer.class;
	public static final Class<String> TYPE_WH_NO = String.class;
	public static final Class<String> TYPE_WH_NAME = String.class;
	public static final Class<String> TYPE_CUST_NO = String.class;
	public static final Class<String> TYPE_CUST_NAME = String.class;
	public static final Class<String> TYPE_USER_NO= String.class;
	public static final Class<String> TYPE_USER_NAME = String.class;
	public static final Class<String> TYPE_FLAG = String.class;
	public static final Class<String> TYPE_SKU_CD = String.class;
	public static final Class<String> TYPE_ITEM_NAME = String.class;
	public static final Class<Integer> TYPE_QTY = Integer.class;

	public static final Class<?>[] TYPES = { TYPE_ORDER_NO, TYPE_DEVICE_ID, TYPE_ORDER_DATE, TYPE_CREATE_DATE, TYPE_ORDER_TYPE,TYPE_INV_TYPE, TYPE_RTN,
		TYPE_WH_NO, TYPE_WH_NAME, TYPE_CUST_NO, TYPE_CUST_NAME, TYPE_USER_NO, TYPE_USER_NAME, TYPE_FLAG, TYPE_SKU_CD, TYPE_ITEM_NAME, TYPE_QTY };

	public static final Class<?>[] KEY_TYPES = { TYPE_ORDER_NO ,TYPE_SKU_CD};

	public Order(Context context) {
		super(context);
	}

	// Contacts table name
	public static final String TABLE_NAME = "order_data";

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
				+ COLUMN_ORDER_NO + " varchar(32) NOT NULL,"				
				+ COLUMN_DEVICE_ID + " varchar(32) NOT NULL,"
				+ COLUMN_ORDER_DATE + " datetime(8)," 
				+ COLUMN_CREATE_DATE + " datetime(8)," 
				+ COLUMN_ORDER_TYPE + " varchar(32),"
				+ COLUMN_INV_TYPE + " varchar(32),"
				+ COLUMN_RTN + " int," 
				+ COLUMN_WH_NO + " varchar(32),"
				+ COLUMN_WH_NAME + " varchar(64)," 
				+ COLUMN_CUST_NO+ " varchar(32)," 
				+ COLUMN_CUST_NAME + " varchar(64),"
				+ COLUMN_USER_NO + " varchar(32)," 
				+ COLUMN_USER_NAME+ " varchar(64)," 
				+ COLUMN_FLAG + " varchar(32),"
				+ COLUMN_SKU_CD + " varchar(32) NOT NULL," 
				+ COLUMN_ITEM_NAME+ " varchar(128),"  
				+ COLUMN_QTY + " decimal(16, 2),"
				+ " primary key ( " + COLUMN_ORDER_NO
				+ " ," + COLUMN_SKU_CD + " ) " + ")";
		return CREATE_TABLE;
	}

}
