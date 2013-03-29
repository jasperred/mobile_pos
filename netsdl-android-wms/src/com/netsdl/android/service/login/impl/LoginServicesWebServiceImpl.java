package com.netsdl.android.service.login.impl;

import java.util.HashMap;
import java.util.Map;

import com.netsdl.android.service.login.LoginServices;

public class LoginServicesWebServiceImpl implements LoginServices {

	@Override
	public Map login(Map param) {
		Map result = new HashMap();
		result.put("flag", "success");
		return result;
	}

	@Override
	public Map logout(Map param) {
		// TODO Auto-generated method stub
		return null;
	}

}
