package com.netsdl.android.common.db;

import java.math.BigDecimal;

import android.content.Context;

public class PosTable extends DatabaseHandler {
	public static final String COLUMN_ORDER_NO = "order_no"; // 单据号
	public static final String COLUMN_ORDER_DATE = "order_date"; // 单据日期
	public static final String COLUMN_CREATE_DATE = "create_date"; // 登录日期
	public static final String COLUMN_ORDER_TYPE = "order_type"; // 单据类型
	public static final String COLUMN_RTN = "rtn"; // 退货标记
	public static final String COLUMN_WH_NO = "wh_no"; // 仓库NO
	public static final String COLUMN_WH_NAME = "wh_name"; // 仓库名称
	public static final String COLUMN_CUST_NO = "cust_no"; // 客户NO
	public static final String COLUMN_CUST_NAME = "cust_name"; // 客户名称
	public static final String COLUMN_USER_NO = "user_no"; // 营业员NO
	public static final String COLUMN_USER_NAME = "user_name"; // 营业员名称
	public static final String COLUMN_FLAG = "flag"; // 明细标记（销售、付款）
	public static final String COLUMN_SKU_CD = "sku_cd"; // SKU编码
	public static final String COLUMN_ITEM_NAME = "item_name"; // 商品名称
	public static final String COLUMN_ITEM_COST = "item_cost"; // 成本价
	public static final String COLUMN_S_PRICE = "s_price"; // 标准单价
	public static final String COLUMN_P_PRICE = "p_price"; // 销售单价
	public static final String COLUMN_QTY = "qty"; // 数量
	public static final String COLUMN_S_AMT = "s_amt"; // 标准金额
	public static final String COLUMN_P_AMT = "p_amt"; // 销售金额
	public static final String COLUMN_END_DAY = "end_day"; // 日结标记
	public static final String COLUMN_OFF_RATE = "off_rate"; // 扣率
	public static final String COLUMN_P_STD_PRICE = "p_std_price"; // 建议销售价
	public static final String COLUMN_P_DISCOUNT = "p_discount"; // 折扣率
	public static final String COLUMN_BO_TYPE = "bo_type"; //

	public static final String[] COLUMNS = { COLUMN_ORDER_NO,
			COLUMN_ORDER_DATE, COLUMN_CREATE_DATE, COLUMN_ORDER_TYPE,
			COLUMN_RTN, COLUMN_WH_NO, COLUMN_WH_NAME, COLUMN_CUST_NO,
			COLUMN_CUST_NAME, COLUMN_USER_NO, COLUMN_USER_NAME, COLUMN_FLAG,
			COLUMN_SKU_CD, COLUMN_ITEM_NAME, COLUMN_ITEM_COST, COLUMN_S_PRICE,
			COLUMN_P_PRICE, COLUMN_QTY, COLUMN_S_AMT, COLUMN_P_AMT,COLUMN_END_DAY,
			COLUMN_OFF_RATE, COLUMN_P_STD_PRICE,COLUMN_P_DISCOUNT,COLUMN_BO_TYPE};

	public static final String[] KEYS = { COLUMN_ORDER_NO, COLUMN_SKU_CD };

	public static final Class<String> TYPE_ORDER_NO = String.class;
	public static final Class<String> TYPE_ORDER_DATE = String.class;
	public static final Class<String> TYPE_CREATE_DATE = String.class;
	public static final Class<String> TYPE_ORDER_TYPE = String.class;
	public static final Class<Integer> TYPE_RTN = Integer.class;
	public static final Class<String> TYPE_WH_NO = String.class;
	public static final Class<String> TYPE_WH_NAME = String.class;
	public static final Class<String> TYPE_CUST_NO = String.class;
	public static final Class<String> TYPE_CUST_NAME = String.class;
	public static final Class<String> TYPE_USER_NO = String.class;
	public static final Class<String> TYPE_USER_NAME = String.class;
	public static final Class<String> TYPE_FLAG = String.class;
	public static final Class<String> TYPE_SKU_CD = String.class;
	public static final Class<String> TYPE_ITEM_NAME = String.class;
	public static final Class<BigDecimal> TYPE_ITEM_COST = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_S_PRICE = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_P_PRICE = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_QTY = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_S_AMT = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_P_AMT = BigDecimal.class;
	public static final Class<Integer> TYPE_END_DAY = Integer.class;
	public static final Class<BigDecimal> TYPE_OFF_RATE = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_P_STD_PRICE = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_P_DISCOUNT = BigDecimal.class;
	public static final Class<String> TYPE_BO_TYPE = String.class;

	public static final Class<?>[] TYPES = { TYPE_ORDER_NO, TYPE_ORDER_DATE,
			TYPE_CREATE_DATE, TYPE_ORDER_TYPE, TYPE_RTN, TYPE_WH_NO,
			TYPE_WH_NAME, TYPE_CUST_NO, TYPE_CUST_NAME, TYPE_USER_NO,
			TYPE_USER_NAME, TYPE_FLAG, TYPE_SKU_CD, TYPE_ITEM_NAME,
			TYPE_ITEM_COST, TYPE_S_PRICE, TYPE_P_PRICE, TYPE_QTY, TYPE_S_AMT,
			TYPE_P_AMT ,
			TYPE_END_DAY,TYPE_OFF_RATE,TYPE_P_STD_PRICE,TYPE_P_DISCOUNT,TYPE_BO_TYPE};

	public static final Class<?>[] KEY_TYPES = { TYPE_ORDER_NO, TYPE_SKU_CD };

	public PosTable(Context context) {
		super(context);
	}

	// Contacts table name
	public static final String TABLE_NAME = "pos_table";

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
				+ COLUMN_ORDER_DATE + " datetime(8)," + COLUMN_CREATE_DATE
				+ " datetime(8)," + COLUMN_ORDER_TYPE + " varchar(32),"
				+ COLUMN_RTN + " int," + COLUMN_WH_NO + " varchar(32),"
				+ COLUMN_WH_NAME + " varchar(64)," + COLUMN_CUST_NO
				+ " varchar(32)," + COLUMN_CUST_NAME + " varchar(64),"
				+ COLUMN_USER_NO + " varchar(32)," + COLUMN_USER_NAME
				+ " varchar(64)," + COLUMN_FLAG + " varchar(32),"
				+ COLUMN_SKU_CD + " varchar(32) NOT NULL," + COLUMN_ITEM_NAME
				+ " varchar(128)," + COLUMN_ITEM_COST + " decimal(16, 2),"
				+ COLUMN_S_PRICE + " decimal(16, 2)," + COLUMN_P_PRICE
				+ " decimal(16, 2)," + COLUMN_QTY + " decimal(16, 2),"
				+ COLUMN_S_AMT + " decimal(16, 2)," + COLUMN_P_AMT
				+ " decimal(16, 2),"+ COLUMN_END_DAY + " int,"
				+ COLUMN_OFF_RATE + " decimal(16, 2),"
				+ COLUMN_P_STD_PRICE + " decimal(16, 2),"
				+ COLUMN_P_DISCOUNT + " decimal(16, 2),"
				+ COLUMN_BO_TYPE + " varchar(32),"
				+ " primary key ( " + COLUMN_ORDER_NO
				+ " ," + COLUMN_SKU_CD + " ) " + ")";
		return CREATE_TABLE;
	}
}
