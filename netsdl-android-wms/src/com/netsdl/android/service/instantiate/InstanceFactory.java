package com.netsdl.android.service.instantiate;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * ʵ��������������ҵ�����ʵ�����Ӵ˲���
 * ʵ�����������Կ��Ƶ��ñ���ҵ�����ǵ���WebService����
 * @author jasper
 *
 */
public class InstanceFactory {
	
	//�������ͣ�local--����,ws--WebService
	private static String connectType = "local";
	//Bean��������Ӧ,Beanʹ�ýӿ�����
	private static Map<String,Map> classMap;
	
	private static void initClassMap()
	{
		if(classMap==null)
			classMap = new HashMap();
		classMap.clear();
		Map local = new HashMap();
		local.put("LoginServices", "com.netsdl.android.service.login.impl.LoginServicesLocalImpl");
		Map ws = new HashMap();
		ws.put("LoginServices", "com.netsdl.android.service.login.impl.LoginServicesWebServiceImpl");
		classMap.put("local", local);
		classMap.put("ws", ws);
	}
	
	/**
	 * ���ҵ�����ʵ��
	 * @param bean
	 * @return
	 */
	public static Object getInstance(String bean)
	{
		if(classMap==null)
			initClassMap();
		Map<String,String> m = classMap.get(connectType);
		if(m==null)
			return null;
		String className = m.get(bean);
		if(className==null)
			return null;
		try {
			Class c = Class.forName(className);
			Object o = c.newInstance();
			return o;
		} catch (ClassNotFoundException e) {
			Log.d("instance", e.getMessage());
		} catch (InstantiationException e) {
			Log.d("instance", e.getMessage());
		} catch (IllegalAccessException e) {
			Log.d("instance", e.getMessage());
		}
		return null;
	}

}
