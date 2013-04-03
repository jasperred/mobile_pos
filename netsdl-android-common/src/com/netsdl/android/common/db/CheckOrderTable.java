package com.netsdl.android.common.db;

import java.math.BigDecimal;

import android.content.Context;

public class CheckOrderTable extends DatabaseHandler {
	public static final String COLUMN_CHECK_NO = "check_no"; // 单据号
	public static final String COLUMN_CHECK_ORDER_ID = "check_order_id"; // 单据号
	public static final String COLUMN_CHECK_LINE_NO = "check_line_no"; // 单据号
	public static final String COLUMN_CHECK_DATE = "check_date"; // 单据日期
	public static final String COLUMN_CREATE_DATE = "create_date"; // 登录日期
	public static final String COLUMN_WH_NO = "wh_no"; // 仓库NO
	public static final String COLUMN_WH_NAME = "wh_name"; // 仓库名称
	public static final String COLUMN_USER_NO = "user_no"; // 营业员NO
	public static final String COLUMN_USER_NAME = "user_name"; // 营业员名称
	public static final String COLUMN_BARCODE = "barcode"; // SKU编码
	public static final String COLUMN_QTY = "qty"; // 数量
	public static final String COLUMN_TOTAL_QTY = "total_qty"; // 数量
	public static final String COLUMN_STATUS = "status"; // 标准金额

	public static final String[] COLUMNS = { COLUMN_CHECK_NO,COLUMN_CHECK_ORDER_ID,COLUMN_CHECK_LINE_NO,
			COLUMN_CHECK_DATE, COLUMN_CREATE_DATE, COLUMN_WH_NO,
			COLUMN_WH_NAME, COLUMN_USER_NO, COLUMN_USER_NAME, COLUMN_BARCODE,
			COLUMN_QTY, COLUMN_TOTAL_QTY, COLUMN_STATUS };

	public static final String[] KEYS = { COLUMN_CHECK_ORDER_ID,COLUMN_CHECK_LINE_NO };

	public static final Class<String> TYPE_CHECK_NO = String.class;
	public static final Class<String> TYPE_CHECK_ORDER_ID = String.class;
	public static final Class<Integer> TYPE_CHECK_LINE_NO = Integer.class;
	public static final Class<String> TYPE_CHECK_DATE = String.class;
	public static final Class<String> TYPE_CREATE_DATE = String.class;
	public static final Class<String> TYPE_WH_NO = String.class;
	public static final Class<String> TYPE_WH_NAME = String.class;
	public static final Class<String> TYPE_USER_NO = String.class;
	public static final Class<String> TYPE_USER_NAME = String.class;
	public static final Class<String> TYPE_BARCODE = String.class;
	public static final Class<BigDecimal> TYPE_QTY = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_TOTAL_QTY = BigDecimal.class;
	public static final Class<String> TYPE_STATUS = String.class;

	public static final Class<?>[] TYPES = { TYPE_CHECK_NO, TYPE_CHECK_ORDER_ID,TYPE_CHECK_LINE_NO,TYPE_CHECK_DATE,
			TYPE_CREATE_DATE, TYPE_WH_NO, TYPE_WH_NAME, TYPE_USER_NO,
			TYPE_USER_NAME, TYPE_BARCODE, TYPE_QTY, TYPE_TOTAL_QTY,
			TYPE_STATUS, };

	public static final Class<?>[] KEY_TYPES = { TYPE_CHECK_ORDER_ID, TYPE_CHECK_LINE_NO };

	public CheckOrderTable(Context context) {
		super(context);
	}

	// Contacts table name
	public static final String TABLE_NAME = "check_order";

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
				+ COLUMN_CHECK_NO + " varchar(32) NOT NULL,"
				+ COLUMN_CHECK_ORDER_ID+ " varchar(64) NOT NULL,"
				+ COLUMN_CHECK_LINE_NO+ " int(8),"
				+ COLUMN_CHECK_DATE + " datetime(8)," 
				+ COLUMN_CREATE_DATE+ " datetime(8)," 
				+ COLUMN_WH_NO + " varchar(32),"
				+ COLUMN_WH_NAME + " varchar(64)," 
				+ COLUMN_USER_NO + " varchar(32)," 
				+ COLUMN_USER_NAME+ " varchar(64)," 
				+ COLUMN_BARCODE + " varchar(32) NOT NULL,"
				+ COLUMN_QTY + " decimal(16, 2)," 
				+ COLUMN_TOTAL_QTY+ " decimal(16, 2)," 
				+ COLUMN_STATUS + " varchar(32),"
				+ " primary key ( " + COLUMN_CHECK_ORDER_ID + " ,"+COLUMN_CHECK_LINE_NO+ " ) "
				+ ")";
		return CREATE_TABLE;
	}
}
