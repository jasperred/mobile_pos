package com.netsdl.android.wms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.netsdl.android.Util;
import com.netsdl.android.db.DeviceMaster;
import com.netsdl.android.db.LoginLog;
import com.netsdl.android.db.UserMaster;
import com.netsdl.android.service.CommonServices;
import com.netsdl.android.service.instantiate.InstanceFactory;
import com.netsdl.android.service.login.LoginServices;

public class ModuleActivity extends Activity {

	private Map loginInfo;
	private String whNo;
	private String whName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_module);
		init();
		initControl();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void init() {

		((Button) this.findViewById(R.id.initButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							Intent gotoIntent = new Intent(ModuleActivity.this,
									InitActivity.class);
							Bundle bundle = new Bundle();
							if (loginInfo != null)
								bundle.putString("userNo",
										(String) loginInfo.get(UserMaster.COLUMN_USER_NO));
							gotoIntent.putExtras(bundle);
							ModuleActivity.this.startActivity(gotoIntent);
						} catch (RuntimeException e) {
							Toast.makeText(ModuleActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
		((Button) this.findViewById(R.id.refundButton))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						try {
							toRefund();
						} catch (RuntimeException e) {
							Toast.makeText(ModuleActivity.this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});
	}

	private void initControl() {
		loginInfo = CommonServices.getLoginUserInfo(this);
		// 出库、入库、其它入库功能未开发
		((Button) this.findViewById(R.id.inButton)).setEnabled(false);
		((Button) this.findViewById(R.id.outButton)).setEnabled(false);
		((Button) this.findViewById(R.id.otherOutButton)).setEnabled(false);
		if (loginInfo == null) {
			((Button) this.findViewById(R.id.refundButton)).setEnabled(false);
			((Button) this.findViewById(R.id.loginButton))
					.setText(R.string.login);
			((Button) this.findViewById(R.id.loginButton))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							try {
								Intent gotoIntent = new Intent(
										ModuleActivity.this,
										LoginActivity.class);
								ModuleActivity.this.startActivity(gotoIntent);
							} catch (Exception e) {
								Toast.makeText(ModuleActivity.this,
										e.getMessage(), Toast.LENGTH_SHORT)
										.show();
							}
						}

					});
		} else {
			((Button) this.findViewById(R.id.refundButton)).setEnabled(true);
			((Button) this.findViewById(R.id.loginButton))
					.setText(R.string.logout);
			// 登出
			((Button) this.findViewById(R.id.loginButton))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							try {
								LoginServices loginService = (LoginServices) InstanceFactory
										.getInstance("LoginServices");
								LoginLog ll = new LoginLog(ModuleActivity.this);
								Map<String, Object> param = new HashMap();
								param.put("LoginLog", ll);
								Map result = loginService.logout(param);
								Intent gotoIntent = new Intent(
										ModuleActivity.this,
										LoginActivity.class);
								ModuleActivity.this.startActivity(gotoIntent);
							} catch (Exception e) {
								Toast.makeText(ModuleActivity.this,
										e.getMessage(), Toast.LENGTH_SHORT)
										.show();
							}
						}

					});
			// 查询仓库编号和名称，数据上传地址
			DeviceMaster dm = new DeviceMaster(this);
			List<Map> dl = dm.find(
					new String[] { DeviceMaster.COLUMN_FIELD_04 },
					DeviceMaster.COLUMN_DEVICE_ID + " = ? and "
							+ DeviceMaster.COLUMN_INIT_ID + " = ?",
					new String[] { Util.getLocalDeviceId(this), "10" });
			if (dl != null && dl.size() > 0) {
				Map d = dl.get(0);
				String str = (String) d.get(DeviceMaster.COLUMN_FIELD_04);
				if (str != null && str.trim().length() > 0) {
					String[] ss = str.split(":");
					if (ss.length == 2) {
						whNo = ss[0];
						whName = ss[1];
					}
				}
			}
		}
	}

	private void toRefund() {
		if (whNo == null) {
			Toast.makeText(this, R.string.messageNoWh, Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (loginInfo == null) {
			Toast.makeText(this, R.string.messageNoLogin, Toast.LENGTH_LONG)
					.show();
			return;
		}
		Intent gotoIntent = new Intent(ModuleActivity.this,
				RefundMainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("whNo", whNo);
		bundle.putString("whName", whName);
		bundle.putString("userNo",
				(String) loginInfo.get(UserMaster.COLUMN_USER_NO));
		bundle.putString("userName",
				(String) loginInfo.get(UserMaster.COLUMN_NAME));
		gotoIntent.putExtras(bundle);
		ModuleActivity.this.startActivity(gotoIntent);
	}
}
