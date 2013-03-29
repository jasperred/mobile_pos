package com.netsdl.android.service.login;

import java.util.Map;

public interface LoginServices {
	/**
	 * login
	 * @param param
	 * @return
	 */
	public Map login(Map param);
	/**
	 * logout
	 * @param param
	 * @return
	 */
	public Map logout(Map param);
}
