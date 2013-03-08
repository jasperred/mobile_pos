package com.netsdl.android.common;

public class Constant {
	
	public static final String IS_LOGIN = "IS_LOGIN";//登录状态
	public static final String UTF_8 = "UTF-8";
	public static final String SEMICOLON = ";";
	public static final String URL = "url";
	public static final String VERSION = "version";
	public static final String ROWS = "rows";
	public static final String PROVIDER_AUTHORITY = "com.netsdl.android.init.provider.Provider";
	public static final String PROVIDER_URI = "content://" + PROVIDER_AUTHORITY+ "/";
	//public static final Uri PROVIDER_URI_STORE_MASTER = Uri.parse(PROVIDER_URI + StoreMaster.TABLE_NAME);
	
	public static final String TABLE_NAME = "TABLE_NAME";
	public static final String COLUMNS = "COLUMNS";
	public static final String TYPES = "TYPES";
	public static final String KEYS = "KEYS";
		
	public static final String ORDER_FLAG_SKU = "SKU";
	public static final String ORDER_FLAG_PAY = "PAY";
	public static final String ORDER_PAY_CHANGE_CD = "99";
	public static final String ORDER_PAY_CHANGE_NAME = "找零";
	
	// private static final String INIT_URL = "http://cyr.dip.jp/init.txt";
	public static final String INIT_URI = "file:///mnt/sdcard/netsdl/pos/init.txt";
	
	public static final String CUST_WH = "WH";//Cust的仓库分类
	public static final String CUST_ST = "ST";//Cust的店铺分类
	public static final String CUST_TYPE = "CUST";//CustMaster中的客户类型

	
}
