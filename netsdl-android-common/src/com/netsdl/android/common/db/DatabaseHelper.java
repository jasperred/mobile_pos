package com.netsdl.android.common.db;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.netsdl.android.common.Constant;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DatabaseHelper {

	public static Map<String, Object> parserCSV(String[] datas, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String[] COLUMNS = (String[]) clazz.getField(Constant.COLUMNS).get(
				clazz);
		Class<?>[] TYPES = (Class<?>[]) clazz.getField(Constant.TYPES).get(
				clazz);
		return parserCSV(datas, COLUMNS, TYPES);
	}

	public static Map<String, Object> parserCSV(String[] datas,
			String[] COLUMNS, Class<?>[] TYPES) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < COLUMNS.length; i++) {
			map.put(COLUMNS[i],
					convertStringToOjbect(TYPES[i], i >= datas.length ? null
							: datas[i]));
		}
		return map;
	}

	public static Object convertStringToOjbect(Class<?> type, String str) {
		if (str == null)
			return null;
		str = str.trim();
		if (type.equals(String.class)) {
			return str;
		} else if (type.equals(Integer.class)) {
			if ("NULL".equals(str)) {
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
			return new BigDecimal(str);
		}
		return str;
	}

	// Getting single
	public static Object[] getSingleColumn(ContentResolver contentResolver,
			Class<?> clazz) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		return getSingleColumn(contentResolver, null, clazz);
	}

	// Getting single
	public static Object[] getSingleColumn(ContentResolver contentResolver,
			Object[] selectionArgs, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		return getSingleColumn(contentResolver, selectionArgs, null, clazz);
	}

	// Getting single
	public static Object[] getSingleColumn(ContentResolver contentResolver,
			Object[] selectionArgs, String[] whereClause, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String[] strs = new String[selectionArgs.length];

		for (int i = 0; i < selectionArgs.length; i++) {
			strs[i] = selectionArgs[i].toString();
		}
		return getSingleColumn(contentResolver, strs, whereClause, clazz);
	}

	// Getting single
	public static Object[] getSingleColumn(ContentResolver contentResolver,
			String[] selectionArgs, String[] whereClause, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {

		String tableName = (String) clazz.getField(Constant.TABLE_NAME).get(
				clazz);
		Uri uri = Uri.parse(Constant.PROVIDER_URI + tableName);

		String[] COLUMNS = (String[]) clazz.getField(Constant.COLUMNS).get(
				clazz);
		String[] KEYS = (String[]) clazz.getField(Constant.KEYS).get(clazz);

		Cursor cursor = null;
		try {
			cursor = contentResolver.query(uri, COLUMNS,
					getWhereClause(whereClause, KEYS), selectionArgs, null);

			return getSingleColumn(cursor, clazz);

		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	// Getting single
	public static Object[] getSingleColumn(SQLiteDatabase db, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		return getSingleColumn(db, null, clazz);
	}

	// Getting single
	public static Object[] getSingleColumn(SQLiteDatabase db,
			Object[] selectionArgs, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		return getSingleColumn(db, selectionArgs, null, clazz);
	}

	// Getting single
	public static Object[] getSingleColumn(SQLiteDatabase db,
			Object[] selectionArgs, String[] whereClause, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String[] strs = new String[selectionArgs.length];

		for (int i = 0; i < selectionArgs.length; i++) {
			strs[i] = selectionArgs[i].toString();
		}
		return getSingleColumn(db, strs, whereClause, clazz);
	}

	// Getting single
	public static Object[] getSingleColumn(SQLiteDatabase db,
			String[] selectionArgs, String[] whereClause, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String tableName = (String) clazz.getField(Constant.TABLE_NAME).get(
				clazz);
		String[] COLUMNS = (String[]) clazz.getField(Constant.COLUMNS).get(
				clazz);
		String[] KEYS = (String[]) clazz.getField(Constant.KEYS).get(clazz);

		Cursor cursor = null;
		try {
			cursor = db.query(tableName, COLUMNS,
					getWhereClause(whereClause, KEYS), selectionArgs, null,
					null, null, null);
			return getSingleColumn(cursor, clazz);

		} finally {
			if (cursor != null)
				cursor.close();
			if (db != null)
				db.close();
		}

	}

	// Getting single
	public static Object[] getSingleColumn(Cursor cursor, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String[] COLUMNS = (String[]) clazz.getField(Constant.COLUMNS).get(
				clazz);
		Class<?>[] TYPES = (Class<?>[]) clazz.getField(Constant.TYPES).get(
				clazz);

		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		else {
			return null;
		}

		Object[] objs = new Object[COLUMNS.length];
		for (int i = 0; i < COLUMNS.length; i++) {
			objs[i] = convertStringToOjbect(TYPES[i], cursor.getString(i));
		}
		return objs;
	}

	public static void insert(SQLiteDatabase db, String[] datas, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		Map<String, Object> mapData = DatabaseHelper.parserCSV(datas, clazz);
		insert(db, mapData, clazz);
	}

	public static void insert(SQLiteDatabase db, Map<String, Object> mapData,
			Class<?> clazz) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String tableName = (String) clazz.getField(Constant.TABLE_NAME).get(
				clazz);
		String[] COLUMNS = (String[]) clazz.getField(Constant.COLUMNS).get(
				clazz);

		ContentValues values = new ContentValues();

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

		db.replace(tableName, null, values);

	}

	public static void insert(ContentResolver contentResolver, String[] datas,
			Class<?> clazz) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		Map<String, Object> mapData = DatabaseHelper.parserCSV(datas, clazz);
		insert(contentResolver, mapData, clazz);

	}

	// Getting insert

	public static void insert(ContentResolver contentResolver,
			Map<String, Object> mapData, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {

		String tableName = (String) clazz.getField(Constant.TABLE_NAME).get(
				clazz);

		Uri uri = Uri.parse(Constant.PROVIDER_URI + tableName);

		String[] COLUMNS = (String[]) clazz.getField(Constant.COLUMNS).get(
				clazz);

		ContentValues values = new ContentValues();

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

		contentResolver.insert(uri, values);

	}
	
	//delete
	public static void delete(ContentResolver contentResolver,
			String where,String[] selectionArgs, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {

		String tableName = (String) clazz.getField(Constant.TABLE_NAME).get(
				clazz);
		Uri uri = Uri.parse(Constant.PROVIDER_URI + tableName);
		contentResolver.delete(uri, where, selectionArgs);

	}
	
	public static int update(SQLiteDatabase db, Map<String, Object> mapData,String[] whereArgs, String[] whereClause,
			Class<?> clazz) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String tableName = (String) clazz.getField(Constant.TABLE_NAME).get(
				clazz);
		String[] COLUMNS = (String[]) clazz.getField(Constant.COLUMNS).get(
				clazz);

		ContentValues values = new ContentValues();

		for (int i = 0; i < COLUMNS.length; i++) {
			Object obj = mapData.get(COLUMNS[i]);
			if (obj == null) {
				continue;
			} else if (obj instanceof String) {
				values.put(COLUMNS[i], obj.toString());
			} else if (obj instanceof Integer) {
				values.put(COLUMNS[i], (Integer) obj);
			} else if (obj instanceof BigDecimal) {
				values.put(COLUMNS[i], ((BigDecimal) obj).toString());
			}
		}
		String[] KEYS = (String[]) clazz.getField(Constant.KEYS).get(clazz);
		return db.update(tableName, values, getWhereClause(whereClause, KEYS), whereArgs);

	}

	// Getting Multi
	public static Object[] getMultiColumn(ContentResolver contentResolver,
			Class<?> clazz) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		return getMultiColumn(contentResolver, null, clazz);
	}

	// Getting Multi
	public static Object[] getMultiColumn(ContentResolver contentResolver,
			Object[] selectionArgs, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		return getMultiColumn(contentResolver, selectionArgs, null, clazz);
	}

	// Getting Multi
	public static Object[] getMultiColumn(ContentResolver contentResolver,
			Object[] selectionArgs, String[] whereClause, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String[] strs = new String[selectionArgs.length];

		for (int i = 0; i < selectionArgs.length; i++) {
			strs[i] = selectionArgs[i].toString();
		}
		// return getMultiColumn(contentResolver, strs, whereClause, clazz);
		return getMultiColumn(contentResolver, strs, whereClause, null, null,
				null, null, true, clazz);
	}

	// Getting Multi
	public static Object[][] getMultiColumn(ContentResolver contentResolver,
			String[] selectionArgs, String[] whereClause, String[] groupBy,
			String having, String[] orderBy, String limit, boolean isASC,
			Class<?> clazz) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {

		String tableName = (String) clazz.getField(Constant.TABLE_NAME).get(
				clazz);
		Uri uri = Uri.parse(Constant.PROVIDER_URI + tableName);
		String[] COLUMNS = (String[]) clazz.getField(Constant.COLUMNS).get(
				clazz);
		String[] KEYS = (String[]) clazz.getField(Constant.KEYS).get(clazz);

		Log.d("getWhereClause(whereClause, KEYS)",
				getWhereClause(whereClause, KEYS));
		Cursor cursor = null;
		try {
			cursor = contentResolver.query(
					uri,
					COLUMNS,
					getWhereClause(whereClause, KEYS),
					selectionArgs,
					convertContentResolverQueryString(groupBy, having, orderBy,
							limit, isASC));

			return getMultiColumn(cursor, clazz);

		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public static Object[][] getMultiColumn(Cursor cursor, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {

		String[] COLUMNS = (String[]) clazz.getField(Constant.COLUMNS).get(
				clazz);
		Class<?>[] TYPES = (Class<?>[]) clazz.getField(Constant.TYPES).get(
				clazz);

		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		else {
			return null;
		}

		Object[][] objss = new Object[cursor.getCount()][COLUMNS.length];
		int i = 0;
		do {
			objss[i] = new Object[COLUMNS.length];
			for (int j = 0; j < COLUMNS.length; j++) {
				objss[i][j] = convertStringToOjbect(TYPES[j],
						cursor.getString(j));
			}
			i++;
		} while (cursor.moveToNext());
		return objss;
	}
	
	public static int count(SQLiteDatabase db, String[] whereArgs, String[] whereClause,
			Class<?> clazz) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		String tableName = (String) clazz.getField(Constant.TABLE_NAME).get(
				clazz);
		Cursor mCount= db.rawQuery("select count(*) from "+tableName+" "+getWhereClause(whereClause), whereArgs);
		mCount.moveToFirst();
		int count= mCount.getInt(0);
		mCount.close();
		return count;

	}

	public static String getWhereClause(String[] KEYS) {
		return getWhereClause(null, KEYS);
	}

	public static String getWhereClause(String[] strs, String[] KEYS) {
		StringBuffer sb = new StringBuffer();
		if (strs != null)
			KEYS = strs;
		for (int i = 0; i < KEYS.length; i++) {
			if (i > 0)
				sb.append(" and ");
			sb.append(KEYS[i]);
			sb.append(" = ?");
		}
		return sb.toString();
	}

	public static String getOrderByString(String[] strs) {
		return getOrderByString(strs, true);
	}

	public static String getOrderByString(String[] strs, boolean isASC) {
		if (strs == null || strs.length == 0)
			return null;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			if (i > 0)
				sb.append(" , ");
			sb.append(strs[i]);
		}

		sb.append(isASC ? " ASC" : " DESC");
		return sb.toString();
	}

	public static String getGroupByString(String[] strs) {
		if (strs == null || strs.length == 0)
			return null;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			if (i > 0)
				sb.append(" , ");
			sb.append(strs[i]);
		}
		return sb.toString();
	}

	public static int getColumnIndex(String str, String[] COLUMNS) {
		for (int i = 0; i < COLUMNS.length; i++) {
			if (COLUMNS[i].equals(str))
				return i;
		}
		return -1;
	}

	public static Class<?> getColumnType(String column, String[] COLUMNS,
			Class<?>[] TYPES) {
		int index = getColumnIndex(column, COLUMNS);
		if (index < 0)
			return null;
		return TYPES[index];
	}

	public static Object getColumnValue(Object[] objs, String column,
			String[] COLUMNS) {
		if (objs == null)
			return null;
		int index = getColumnIndex(column, COLUMNS);
		if (index < 0)
			return null;
		if (index >= objs.length)
			return null;
		return objs[index];
	}

	public static String convertContentResolverQueryString(String[] groupBy,
			String having, String[] orderBy, String limit, boolean isASC) {
		String strGroupBy = getGroupByString(groupBy);
		if (strGroupBy == null)
			strGroupBy = " ";
		if (having == null)
			having = " ";
		String strOrderBy = getOrderByString(orderBy, isASC);
		if (strOrderBy == null)
			strOrderBy = " ";
		if (limit == null)
			limit = " ";
		return strGroupBy + Constant.SEMICOLON + having + Constant.SEMICOLON
				+ strOrderBy + Constant.SEMICOLON + limit;
	}
}
