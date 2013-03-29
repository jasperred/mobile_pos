package com.netsdl.android.service.instantiate;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * 实例化工厂，所有业务类的实例化从此产生
 * 实例化工厂可以控制调用本地业务处理还是调用WebService处理
 * @author jasper
 *
 */
public class InstanceFactory {
	
	//连接类型，local--本地,ws--WebService
	private static String connectType = "local";
	//Bean和类名对应,Bean使用接口名称
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
	 * 获得业务类的实例
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
