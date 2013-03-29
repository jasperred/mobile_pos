package com.netsdl.android.wms;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.netsdl.android.Util;
import com.netsdl.android.db.LoginLog;
import com.netsdl.android.db.UserMaster;
import com.netsdl.android.service.instantiate.InstanceFactory;
import com.netsdl.android.service.login.LoginServices;

public class LoginActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void init() {
		//pj暂时不显示
		((TextView) this.findViewById(R.id.pjNoTextView)).setVisibility(View.GONE);
		((EditText) this.findViewById(R.id.pjNoEditText)).setVisibility(View.GONE);
		((Button) this.findViewById(R.id.loginButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							login();
						} catch (RuntimeException e) {
							Toast.makeText(LoginActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
		((Button) this.findViewById(R.id.cancelButton))
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					noLogin();
				} catch (RuntimeException e) {
					Toast.makeText(LoginActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});

	}
	
	private void login()
	{
//		String pjNo = ((EditText) this.findViewById(R.id.pjNoEditText)).getText().toString().trim();
//		if(pjNo.length()==0)
//		{
//			Toast.makeText(this, R.string.pjNoMessage, Toast.LENGTH_LONG)
//			.show();
//			return;
//		}
		String userNo = ((EditText) this.findViewById(R.id.userNoEditText)).getText().toString().trim();
		if(userNo.length()==0)
		{
			Toast.makeText(this, R.string.userNoMessage, Toast.LENGTH_LONG)
			.show();
			return;
		}
		String password = ((EditText) this.findViewById(R.id.passwordEditText)).getText().toString().trim();
		if(password.length()==0)
		{
			Toast.makeText(this, R.string.passwordMessage, Toast.LENGTH_LONG)
			.show();
			return;
		}
		LoginServices loginService =(LoginServices) InstanceFactory.getInstance("LoginServices");
		UserMaster um = new UserMaster(this);
		LoginLog ll = new LoginLog(this);
		Map<String, Object> param = new HashMap();
		param.put("UserMaster", um);
		param.put("LoginLog", ll);
		//param.put("pjNo", pjNo);
		param.put("userNo", userNo);
		param.put("password", Util.getMD5(password));
		Map result = loginService.login(param);
		if(result.get("flag").equals("error"))
		{
			Toast.makeText(this, (String)result.get("message"), Toast.LENGTH_LONG)
			.show();
			return;
		}
		else
		{
			Intent gotoIntent = new Intent(LoginActivity.this,
					ModuleActivity.class);
			LoginActivity.this.startActivity(gotoIntent);
		}		
	}

	private void noLogin() {
		LoginLog ll = new LoginLog(this);
		ll.delete("1=1", null);
		Intent gotoIntent = new Intent(LoginActivity.this,
				ModuleActivity.class);
		LoginActivity.this.startActivity(gotoIntent);
	}
}
