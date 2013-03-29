package com.netsdl.android.service;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.netsdl.android.db.LoginLog;

public class CommonServices {
	
	//用户检查
	public static Map getLoginUserInfo(Context c)
	{
		LoginLog ll = new LoginLog(c);
		List<Map> ul = ll.findAll();
		if(ul==null||ul.size()==0)
			return null;
		return ul.get(0);
	}

}
