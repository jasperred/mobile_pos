package com.netsdl.android.db;

import java.math.BigDecimal;

import android.content.Context;

public class SkuMaster extends DatabaseHandler {

	// Contacts Table Columns names
	public static final String COLUMN_SKU_ID = "sku_id"; // SKUId
	public static final String COLUMN_CAT2 = "cat2"; // 分类2（季节）
	public static final String COLUMN_CAT2_NAME = "cat2_name"; // 分类2名称（季节名称）
	public static final String COLUMN_CAT3 = "cat3"; // 分类3（品牌）
	public static final String COLUMN_CAT3_NAME = "cat3_name"; // 分类3名称（品牌名称）
	public static final String COLUMN_ITEM_CD = "item_cd"; // 商品编码
	public static final String COLUMN_ORIG_ITEM_CD = "orig_item_cd"; // 原编码
	public static final String COLUMN_ITEM_NAME = "item_name"; // 商品名称
	public static final String COLUMN_ITEM_CAT = "item_cat"; // 商品分类
	public static final String COLUMN_ITEM_CAT_NAME = "item_cat_name"; // 商品分类名称
	public static final String COLUMN_SKU_PROP_1 = "sku_prop_1"; // SKU属性1（color）
	public static final String COLUMN_SKU_PROP_1_NAME = "sku_prop_1_name"; // SKU属性1名称（color名称）
	public static final String COLUMN_PL1_DISP_INDEX = "pl1_disp_index"; // SKU属性1序号（color序号）
	public static final String COLUMN_SKU_PROP_2 = "sku_prop_2"; // SKU属性2（SIZE）
	public static final String COLUMN_SKU_PROP_2_NAME = "sku_prop_2_name"; // SKU属性2名称（SIZE名称）
	public static final String COLUMN_PL2_DISP_INDEX = "pl2_disp_index"; // SKU属性2序号（SIZE序号）
	public static final String COLUMN_SKU_PROP_3 = "sku_prop_3"; // SKU属性3
	public static final String COLUMN_SKU_PROP_3_NAME = "sku_prop_3_name"; // SKU属性3名称
	public static final String COLUMN_PL3_DISP_INDEX = "pl3_disp_index"; // SKU属性3序号
	public static final String COLUMN_SKU_CD = "sku_cd"; // SKU编码
	public static final String COLUMN_SKU_NAME = "sku_name"; // SKU名称
	public static final String COLUMN_BAR_CODE = "bar_code"; // 条形码
	public static final String COLUMN_BAR_CODE_2 = "bar_code_2"; // 条形码2
	public static final String COLUMN_ITEM_COST = "item_cost"; // 成本价
	public static final String COLUMN_ITEM_P_COST = "item_p_cost"; // 采购价
	public static final String COLUMN_ITEM_STD_PRICE = "item_std_price"; // 标准价
	public static final String COLUMN_ITEM_PRICE = "item_price"; // 销售价（Pos销售时的商品单价）

	public static final String[] COLUMNS = { COLUMN_SKU_ID, COLUMN_CAT2,
			COLUMN_CAT2_NAME, COLUMN_CAT3, COLUMN_CAT3_NAME, COLUMN_ITEM_CD,
			COLUMN_ORIG_ITEM_CD, COLUMN_ITEM_NAME, COLUMN_ITEM_CAT,
			COLUMN_ITEM_CAT_NAME, COLUMN_SKU_PROP_1, COLUMN_SKU_PROP_1_NAME,
			COLUMN_PL1_DISP_INDEX, COLUMN_SKU_PROP_2, COLUMN_SKU_PROP_2_NAME,
			COLUMN_PL2_DISP_INDEX, COLUMN_SKU_PROP_3, COLUMN_SKU_PROP_3_NAME,
			COLUMN_PL3_DISP_INDEX, COLUMN_SKU_CD, COLUMN_SKU_NAME,
			COLUMN_BAR_CODE, COLUMN_BAR_CODE_2, COLUMN_ITEM_COST,
			COLUMN_ITEM_P_COST, COLUMN_ITEM_STD_PRICE, COLUMN_ITEM_PRICE };

	public static final String[] KEYS = { COLUMN_SKU_ID };
	public static final Class<Integer> TYPE_SKU_ID = Integer.class;
	public static final Class<String> TYPE_CAT2 = String.class;
	public static final Class<String> TYPE_CAT2_NAME = String.class;
	public static final Class<String> TYPE_CAT3 = String.class;
	public static final Class<String> TYPE_CAT3_NAME = String.class;
	public static final Class<String> TYPE_ITEM_CD = String.class;
	public static final Class<String> TYPE_ORIG_ITEM_CD = String.class;
	public static final Class<String> TYPE_ITEM_NAME = String.class;
	public static final Class<String> TYPE_ITEM_CAT = String.class;
	public static final Class<String> TYPE_ITEM_CAT_NAME = String.class;
	public static final Class<String> TYPE_SKU_PROP_1 = String.class;
	public static final Class<String> TYPE_SKU_PROP_1_NAME = String.class;
	public static final Class<Integer> TYPE_PL1_DISP_INDEX = Integer.class;
	public static final Class<String> TYPE_SKU_PROP_2 = String.class;
	public static final Class<String> TYPE_SKU_PROP_2_NAME = String.class;
	public static final Class<Integer> TYPE_PL2_DISP_INDEX = Integer.class;
	public static final Class<String> TYPE_SKU_PROP_3 = String.class;
	public static final Class<String> TYPE_SKU_PROP_3_NAME = String.class;
	public static final Class<Integer> TYPE_PL3_DISP_INDEX = Integer.class;
	public static final Class<String> TYPE_SKU_CD = String.class;
	public static final Class<String> TYPE_SKU_NAME = String.class;
	public static final Class<String> TYPE_BAR_CODE = String.class;
	public static final Class<String> TYPE_BAR_CODE_2 = String.class;
	public static final Class<BigDecimal> TYPE_ITEM_COST = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_ITEM_P_COST = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_ITEM_STD_PRICE = BigDecimal.class;
	public static final Class<BigDecimal> TYPE_ITEM_PRICE = BigDecimal.class;

	public static final Class<?>[] TYPES = { TYPE_SKU_ID, TYPE_CAT2,
			TYPE_CAT2_NAME, TYPE_CAT3, TYPE_CAT3_NAME, TYPE_ITEM_CD,
			TYPE_ORIG_ITEM_CD, TYPE_ITEM_NAME, TYPE_ITEM_CAT,
			TYPE_ITEM_CAT_NAME, TYPE_SKU_PROP_1, TYPE_SKU_PROP_1_NAME,
			TYPE_PL1_DISP_INDEX, TYPE_SKU_PROP_2, TYPE_SKU_PROP_2_NAME,
			TYPE_PL2_DISP_INDEX, TYPE_SKU_PROP_3, TYPE_SKU_PROP_3_NAME,
			TYPE_PL3_DISP_INDEX, TYPE_SKU_CD, TYPE_SKU_NAME, TYPE_BAR_CODE,
			TYPE_BAR_CODE_2, TYPE_ITEM_COST, TYPE_ITEM_P_COST,
			TYPE_ITEM_STD_PRICE, TYPE_ITEM_PRICE };

	public static final Class<?>[] KEY_TYPES = { TYPE_SKU_ID };

	public SkuMaster(Context context) {
		super(context);
	}

	// Contacts table name
	public static final String TABLE_NAME = "sku_master";

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
		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
				+ COLUMN_SKU_ID + " varchar(32) PRIMARY KEY ," + COLUMN_CAT2 + " varchar(32) ,"
				+ COLUMN_CAT2_NAME + " varchar(256) ," + COLUMN_CAT3
				+ " varchar(32) ," + COLUMN_CAT3_NAME + " varchar(256) ,"
				+ COLUMN_ITEM_CD + " varchar(32) ," + COLUMN_ORIG_ITEM_CD
				+ " varchar(32) ," + COLUMN_ITEM_NAME + " varchar(128) ,"
				+ COLUMN_ITEM_CAT + " varchar(32) ," + COLUMN_ITEM_CAT_NAME
				+ " varchar(256) ," + COLUMN_SKU_PROP_1 + " varchar(16) ,"
				+ COLUMN_SKU_PROP_1_NAME + " varchar(64) ,"
				+ COLUMN_PL1_DISP_INDEX + " int ," + COLUMN_SKU_PROP_2
				+ " varchar(16) ," + COLUMN_SKU_PROP_2_NAME + " varchar(64) ,"
				+ COLUMN_PL2_DISP_INDEX + " int ," + COLUMN_SKU_PROP_3
				+ " varchar(16) ," + COLUMN_SKU_PROP_3_NAME + " varchar(64) ,"
				+ COLUMN_PL3_DISP_INDEX + " int ," + COLUMN_SKU_CD
				+ " varchar(32) ," + COLUMN_SKU_NAME + " varchar(128) ,"
				+ COLUMN_BAR_CODE + " varchar(32) ," + COLUMN_BAR_CODE_2
				+ " varchar(32) ," + COLUMN_ITEM_COST + " decimal(16, 2) ,"
				+ COLUMN_ITEM_P_COST + " decimal(16, 2) ,"
				+ COLUMN_ITEM_STD_PRICE + " decimal(16, 2) ,"
				+ COLUMN_ITEM_PRICE + " decimal(16, 2) " + ")";
		return CREATE_TABLE;
	}

}
