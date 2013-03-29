/**
 * 
 */
package com.netsdl.android.main.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.netsdl.android.common.Constant;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.DeviceMaster;
import com.netsdl.android.common.db.StoreMaster;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.sale.SaleActivity;

/**
 * @author jasper
 *
 */
public class LoginActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setTitle(R.string.longin_title);
		init();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub

	}

	private void init()
	{
		//初始化数字按钮
		int[] buttons = new int[] { R.id.button0, R.id.button1, R.id.button2,
				R.id.button3, R.id.button4, R.id.button5, R.id.button6,
				R.id.button7, R.id.button8, R.id.button9 };

		int c = 0;
		for (int i : buttons) {
			final Button button = (Button) this.findViewById(i);
			final int cc = c;
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					buttonInput(""+cc);
				}
			});
			c++;
		}

		final Button loginButton = (Button) this.findViewById(R.id.loginButton);

		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				login();
			}
		});

		final Button buttonClear = (Button) this
				.findViewById(R.id.buttonClear);
		buttonClear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buttonInput("BACK");
			}
		});

		final Button buttonReturn = (Button) this
				.findViewById(R.id.buttonReturn);
		buttonReturn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buttonInput("ENTER");
			}
		});
	}
	
	//button输入
	//BACK--消除
	//ENTER--回车
	private void buttonInput(String str)
	{
		if(str==null)
			return;
		int state = 0;
		final EditText userNameText = (EditText) this.findViewById(R.id.userNameEditText);
		final EditText passText = (EditText) this.findViewById(R.id.passwordEditText);
		if(userNameText.isFocused())
			state = 1;
		if(passText.isFocused())
			state = 2;
		switch(state)
		{
		case 0://无焦点，不输入
			break;
		case 1://用户名输入
			String t = userNameText.getText().toString().trim();
			if(str.equals("BACK"))
			{
				if(t.length()>0)
					t = t.substring(0,t.length()-1);
				userNameText.setText(t);
			}
			else if(str.equals("ENTER"))
			{
				passText.requestFocus();
			}
			else
			{
				t = t+str.trim();
				userNameText.setText(t);				
			}
			break;
		case 2://密码输入
			String p = passText.getText().toString().trim();
			if(str.equals("BACK"))
			{
				if(p.length()>0)
					p = p.substring(0,p.length()-1);
				passText.setText(p);
			}
			else if(str.equals("ENTER"))
			{
				login();
			}
			else
			{
				p = p+str.trim();
				passText.setText(p);				
			}
			break;
		}
	}
	
	//登录
	private void login()
	{
		final EditText userNameText = (EditText) this.findViewById(R.id.userNameEditText);
		final EditText passText = (EditText) this.findViewById(R.id.passwordEditText);
		String userName = userNameText.getText().toString().trim();
		String password = passText.getText().toString();
		try {
			Object[] obj = DatabaseHelper.getSingleColumn(
					this.getContentResolver(),
					new Object[] { userName }, StoreMaster.class);
			//未找到用户
			if(obj==null||obj.length==0)
			{
				Toast.makeText(this, R.string.msg_no_and_pass,
						Toast.LENGTH_LONG).show();
				return;
			}
			String pass = (String) DatabaseHelper.getColumnValue(
					obj, StoreMaster.COLUMN_MD5,
					StoreMaster.COLUMNS);
			//原密码为空不允许登录
			if(pass==null)
			{
				Toast.makeText(this, R.string.msg_no_and_pass,
						Toast.LENGTH_LONG).show();
				return;
			}
			//密码比对
			if(!Util.getMD5(password).equals(pass))
			{
				Toast.makeText(this, R.string.msg_no_and_pass,
						Toast.LENGTH_LONG).show();
				return;
			}
			//查用户所在店铺
			String localShopCode = null,localShopName = null;
			Object[] ss = DatabaseHelper.getSingleColumn(getContentResolver(), new String[]{"1",Util.getLocalDeviceId(this.getApplicationContext())}, new String[]{DeviceMaster.COLUMN_INIT_ID,DeviceMaster.COLUMN_DEVICE_ID}, DeviceMaster.class);
			if(ss!=null&&ss.length>0)
			{
				String sss = (String)ss[DatabaseHelper.getColumnIndex(DeviceMaster.COLUMN_FIELD_01, DeviceMaster.COLUMNS)];
				if(sss!=null)
				{
					String[] sps = sss.split(";");
					if(sps.length==2)
					{
						localShopCode = sps[0];
						localShopName = sps[1];
					}
				}
				
			}
			final Switch loginRoleSwitch = (Switch) this
					.findViewById(R.id.loginRoleSwitch);
			String role = (String) DatabaseHelper.getColumnValue(
					obj, StoreMaster.COLUMN_ROLE,
					StoreMaster.COLUMNS);
			String name = (String) DatabaseHelper.getColumnValue(
					obj, StoreMaster.COLUMN_NAME,
					StoreMaster.COLUMNS);
			//收银登录
			if(!loginRoleSwitch.isChecked())
			{
				//转向销售页面
				toSale(userName,name,role,localShopCode,localShopName);
			}
			//店长登录
			else 
			{
				if(role!=null&&role.equals(Constant.SHOP_ROLE_MANAGER))
				{
					//转向菜单页面
					toFunction(userName,name,role,localShopCode,localShopName);
				}
				else
				{
					Toast.makeText(this,R.string.messageNoPermission,
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (IllegalArgumentException e) {
			Toast.makeText(this,e.getMessage(),
					Toast.LENGTH_LONG).show();
		} catch (SecurityException e) {
			Toast.makeText(this,e.getMessage(),
					Toast.LENGTH_LONG).show();
		} catch (IllegalAccessException e) {
			Toast.makeText(this,e.getMessage(),
					Toast.LENGTH_LONG).show();
		} catch (NoSuchFieldException e) {
			Toast.makeText(this,e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		
	}

	//转向销售页面
	private void toSale(String userId,String userName,String role,String shopCode,String shopName) {
		Intent gotoIntent = new Intent(
				LoginActivity.this,
				SaleActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("userId", userId);
		bundle.putString("userName", userName);
		bundle.putString("role", role);
		bundle.putString("localShopName", shopName);
		bundle.putString("localShopCode", shopCode);
		gotoIntent.putExtras(bundle);
		LoginActivity.this
				.startActivity(gotoIntent);
	}
	//转向菜单页面
	private void toFunction(String userId,String userName,String role,String shopCode,String shopName) {
		Intent gotoIntent = new Intent(
				LoginActivity.this,
				FunctionActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("userId", userId);
		bundle.putString("userName", userName);
		bundle.putString("role", role);
		bundle.putString("localShopName", shopName);
		bundle.putString("localShopCode", shopCode);
		gotoIntent.putExtras(bundle);
		LoginActivity.this
				.startActivity(gotoIntent);
	}
}
