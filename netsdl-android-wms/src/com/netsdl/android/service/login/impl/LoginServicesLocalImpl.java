package com.netsdl.android.service.login.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netsdl.android.Util;
import com.netsdl.android.db.LoginLog;
import com.netsdl.android.db.UserMaster;
import com.netsdl.android.service.login.LoginServices;

public class LoginServicesLocalImpl implements LoginServices {

	@Override
	public Map login(Map param) {
		Map result = new HashMap();
		//清空登录信息
		LoginLog ll = (LoginLog)param.get("LoginLog");
		UserMaster um = (UserMaster) param.get("UserMaster");
		if(ll==null||um==null)
		{
			result.put("message", "DB信息错误");
			result.put("flag", "error");
			return result;
		}
		ll.delete("1=1", null);
		List<Map> ulList = um.find(UserMaster.COLUMNS, UserMaster.COLUMN_USER_NO+" = ? and "+UserMaster.COLUMN_MD5+" = ?", new String[]{(String)param.get("userNo"),(String)param.get("password")});
		if(ulList==null||ulList.size()==0)
		{
			result.put("message", "登录信息错误");
			result.put("flag", "error");
			return result;
		}
		result.put("flag", "success");
		//登录成功后记录登录信息（LoginLog），作为其它地方获得登录信息的凭据
		Map m = new HashMap();
		m.put(LoginLog.COLUMN_ID, Util.getUUID());
		m.put(LoginLog.COLUMN_LOGIN_TIME, Util.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		m.put(LoginLog.COLUMN_USER_NO, (String)param.get("userNo"));
		m.put(LoginLog.COLUMN_STATUS, "login");
		ll.insert(m, null);
		return result;
	}

	@Override
	public Map logout(Map param) {
		Map result = new HashMap();
		//清空登录信息
		LoginLog ll = (LoginLog)param.get("LoginLog");
		if(ll==null)
		{
			result.put("message", "DB信息错误");
			result.put("flag", "error");
			return result;
		}
		ll.delete("1=1", null);
		result.put("flag", "success");
		return result;
	}

}
