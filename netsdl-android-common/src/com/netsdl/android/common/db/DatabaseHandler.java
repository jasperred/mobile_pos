package com.netsdl.android.common.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("NewApi")
public abstract class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "netsdlDB";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("DatabaseHandler", "new Database:" + DATABASE_NAME + " version:"
				+ DATABASE_VERSION);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		db.execSQL(getCreateTableSql());
		super.onOpen(db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(getTableName(), "onCreate");
		db.execSQL(getCreateTableSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(getTableName(), "onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS " + getTableName());
		onCreate(db);
	}

	public void clear() {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			db.execSQL("DROP TABLE IF EXISTS " + getTableName());
			db.execSQL(getCreateTableSql());
		} finally {
			if (db != null)
				db.close();
		}
	}

	public Map<String, Object> parserCSV(String[] datas) {
		return DatabaseHelper.parserCSV(datas, getColumns(), getTypes());
	}

	public Map<String, Object> parserCSV(String data) {
		String[] datas = data.split(",");
		return parserCSV(datas);
	}

	public void insert(Map<String, Object> mapData)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			DatabaseHelper.insert(db, mapData, getClass());
		} finally {
			if (db != null)
				db.close();
		}

	}

	public void insert(String data) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		Map<String, Object> mapData = parserCSV(data);
		insert(mapData);
	}

	public void insert(String[] datas) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		Map<String, Object> mapData = parserCSV(datas);
		insert(mapData);
	}
	
	public int update(Map<String, Object> mapData,String[] whereArgs, String[] whereClause)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			int r = DatabaseHelper.update(db, mapData,whereArgs,whereClause, getClass());
			return r;
		} finally {
			if (db != null)
				db.close();
		}

	}

	public void deleteByKey(String data) {
		ContentValues values = new ContentValues();
		Map<String, Object> mapData = parserCSV(data);

		String[] COLUMNS = getColumns();

		for (int i = 0; i < COLUMNS.length; i++) {
			Object obj = mapData.get(COLUMNS[i]);
			if (obj == null) {
				values.put(COLUMNS[i], "");
			} else if (obj instanceof String) {
				values.put(COLUMNS[i], obj.toString());
			} else if (obj instanceof Integer) {
				values.put(COLUMNS[i], (Integer) obj);
			} else if (obj instanceof BigDecimal) {
				values.put(COLUMNS[i], ((BigDecimal) obj).toString());
			}
		}

		String[] KEYS = getKeys();
		String[] whereArgs = new String[KEYS.length];
		for (int i = 0; i < KEYS.length; i++) {
			whereArgs[i] = values.getAsString(KEYS[i]);
		}
		deleteByKey(KEYS,whereArgs);
	}

	//增加whereConditions条件参数
	public void deleteByKey(String[] whereConditions,String[] whereArgs) {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			if(whereConditions==null)
				whereConditions = getKeys();
			db.delete(getTableName(), DatabaseHelper.getWhereClause(whereConditions),
					whereArgs);
		} finally {
			if (db != null)
				db.close();
		}
	}

	public Object[] getSingleColumn() throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		return getSingleColumn(null);
	}

	public Object[] getSingleColumn(Object[] selectionArgs)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		return getSingleColumn(selectionArgs, null);
	}

	public Object[] getSingleColumn(Object[] selectionArgs, String[] whereClause)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		SQLiteDatabase db = null;
		try {
			db = getReadableDatabase();
			return DatabaseHelper.getSingleColumn(db, selectionArgs,
					whereClause, getClass());
		} finally {
			if (db != null)
				db.close();
		}
	}

	// Getting multi
	public Object[][] getMultiColumn(String[] groupBy, String having,
			String[] orderBy, String limit) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		return getMultiColumn(new String[] {}, new String[] {}, groupBy,
				having, orderBy, limit, true);
	}

	// Getting multi
	public Object[][] getMultiColumn(String[] selectionArgs,
			String[] whereClause, String[] groupBy, String having,
			String[] orderBy, String limit) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		return getMultiColumn(selectionArgs, whereClause, groupBy, having,
				orderBy, limit, true);
	}

	// Getting multi
	public Object[][] getMultiColumn(String[] selectionArgs,
			String[] whereClause, String[] groupBy, String having,
			String[] orderBy, String limit, boolean isASC)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String[] COLUMNS = getColumns();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			cursor = db.query(getTableName(), COLUMNS,
					DatabaseHelper.getWhereClause(whereClause, getKeys()),
					selectionArgs, DatabaseHelper.getGroupByString(groupBy),
					having, DatabaseHelper.getOrderByString(orderBy, isASC),
					limit);
			return DatabaseHelper.getMultiColumn(cursor, getClass());
		} finally {
			if (cursor != null)
				cursor.close();
			if (db != null)
				db.close();
		}

	}
	
	public int getCount(String[] selectionArgs,
			String[] whereClause)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		SQLiteDatabase db = null;
		try {
			db = this.getReadableDatabase();
			int c = DatabaseHelper.count(db, selectionArgs, whereClause, getClass());
			return c;
		} finally {
			if (db != null)
				db.close();
		}

	}
	
	/**
	 * 
	 * @param whereArgs
	 * @param whereClause
	 * @return
	 */
	public int count(String whereClause,String[] whereArgs)
	{
		SQLiteDatabase db = null;
		try {
			db = this.getReadableDatabase();
			Cursor mCount= null;
			if(whereClause==null)
			{
				mCount = db.rawQuery("select count(*) from "+getTableName(),null);
			}
			else
			{
				mCount = db.rawQuery("select count(*) from "+getTableName()+" "+whereClause, whereArgs);
			}
			mCount.moveToFirst();
			int count= mCount.getInt(0);
			mCount.close();
			return count;
		} finally {
			if (db != null)
				db.close();
		}
	}

    /**
     * 
     * @param whereClause
     * @param whereArgs
     * @return
     */
	public void delete(String whereClause,String[] whereArgs)
    {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			//无查询条件建议使用1=1
			if(whereClause==null)
			{
				return;
			}
			else if(whereArgs==null)
			{
				db.execSQL("delete from "+getTableName()+" where "+whereClause);
			}
			else
			{
				db.execSQL("delete from "+getTableName()+" where "+whereClause, whereArgs);
			}
			
		} finally {
			if (db != null)
				db.close();
		}
	}

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Official_article_temp
     *
     * @mbggenerated
     */
	public long insert(Map data,String nullColumnHack)
    {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			String[] columns = getColumns();
			ContentValues values = new ContentValues();

			for (int i = 0; i < columns.length; i++) {
				Object obj = data.get(columns[i]);
				if (obj == null) {
					values.put(columns[i], "");
				} else if (obj instanceof String) {
					values.put(columns[i], obj.toString());
				} else if (obj instanceof Integer) {
					values.put(columns[i], (Integer) obj);
				} else if (obj instanceof BigDecimal) {
					values.put(columns[i], ((BigDecimal) obj).toString());
				}
			}
			long l = db.insert(getTableName(), nullColumnHack, values);
			return l;
		} finally {
			if (db != null)
				db.close();
		}
	}
	
	public List<Map> findAll()
	{
		return find (false,getColumns(),null,null,null,null,null,null);
	}
	
	public List<Map> find(String[] columns,String selection,String[] selectionArgs)
	{
		return find (false,columns,selection,selectionArgs,null,null,null,null);
	}

    /**
     *
     * @mbggenerated
     */
	public List<Map> find(boolean distinct,String[] columns,String selection,String[] selectionArgs,String groupBy,String having,String orderBy,String limit){
		SQLiteDatabase db = null;
		try {
			db = this.getReadableDatabase();
			if(columns==null)
			{
				columns = getColumns();
			}
			Class<?>[] types = getTypes();
			Cursor cursor = db.query(distinct, getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
			if(cursor.isAfterLast())
				return null;
			if(cursor.isBeforeFirst())
				cursor.moveToFirst();
			List l = new ArrayList();
			do {
				Map m = new HashMap();
				for (int j = 0; j < columns.length; j++) {
					int p = getPosition(columns[j]);
					if(p==-1)
					{
						m.put(columns[j], cursor.getString(j));
					}
					else
						m.put(columns[j], convertStringToOjbect(types[p],cursor.getString(j)));
				}
				l.add(m);
			} while (cursor.moveToNext());
			return l;
		} finally {
			if (db != null)
				db.close();
		}
	}
	
	//得到行的位置
	private int getPosition(String column)
	{
		String[] cls = getColumns();
		for(int i=0;i<cls.length;i++)
		{
			if(column.equals(cls[i]))
				return i;
		}
		return -1;
	}
    
    /**
     * 
     * @param record
     * @param example
     * @return
     */
	public long update(Map data,String whereClause,String[] whereArgs)
    {
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			String[] columns = getColumns();
			ContentValues values = new ContentValues();

			for (int i = 0; i < columns.length; i++) {
				Object obj = data.get(columns[i]);
				if (obj == null) {
					continue;
				} else if (obj instanceof String) {
					values.put(columns[i], obj.toString());
				} else if (obj instanceof Integer) {
					values.put(columns[i], (Integer) obj);
				} else if (obj instanceof BigDecimal) {
					values.put(columns[i], ((BigDecimal) obj).toString());
				}
			}
			long l = db.update(getTableName(), values, whereClause, whereArgs);
			return l;
		} finally {
			if (db != null)
				db.close();
		}
	}

  
    
    private Object convertStringToOjbect(Class<?> type, String str) {
		if (str == null)
			return null;
		str = str.trim();
		if (type.equals(String.class)) {
			return str;
		} else if (type.equals(Integer.class)) {
			if ("NULL".equals(str)||str.trim().length()==0) {
				return null;
			} else {
				try {
					byte[] ss = str.getBytes();
					byte[] ss2 = new byte[ss.length];
					int c = 0;
					for(int i=0;i<ss.length;i++)
					{
						if(ss[i]<0)
							continue;
						ss2[c] = ss[i];
						c++;
					}
					byte[] ss3 = new byte[c];
					System.arraycopy(ss2, 0, ss3, 0, c);
					String str2 = new String(ss3);
					Integer iTemp = Integer.valueOf(str2);
					return iTemp;
				} catch (NumberFormatException nfe) {
					return null;
				}
			}
		} else if (type.equals(BigDecimal.class)) {
			if ("NULL".equals(str)||str.trim().length()==0) {
				return null;
			}
			return new BigDecimal(str);
		}
		return str;
	}

	public abstract String getTableName();

	public abstract String[] getColumns();

	public abstract String[] getKeys();

	public abstract Class<?>[] getTypes();

	public abstract Class<?>[] getKeyTypes();

	public abstract String getCreateTableSql();

}
