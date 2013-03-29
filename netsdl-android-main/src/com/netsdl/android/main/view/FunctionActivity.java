/**
 * 
 */
package com.netsdl.android.main.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.netsdl.android.main.R;
import com.netsdl.android.main.view.manage.ReturnToWhActivity;
import com.netsdl.android.main.view.manage.ShopTransferActivity;
import com.netsdl.android.main.view.sale.SaleActivity;
import com.netsdl.android.main.view.sale.SaleReturnActivity;

/**
 * @author jasper
 * 
 */
public class FunctionActivity extends Activity {

	public static String userId;
	public static String userName;
	public static String role;
	public static String localShopName;
	public static String localShopCode;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_function);
		setTitle(R.string.function_title);
		// 得到用户名和角色
		Bundle data = this.getIntent().getExtras();
		if (data != null) {
			if (data.getString("userId") != null)
				userId = data.getString("userId");
			if (data.getString("userName") != null)
				userName = data.getString("userName");
			if (data.getString("role") != null)
				role = data.getString("role");
			if (data.getString("localShopName") != null)
				localShopName = data.getString("localShopName");
			if (data.getString("localShopCode") != null)
				localShopCode = data.getString("localShopCode");
		}
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub

	}

	private void init() {
		final Button buttonBack = (Button) this.findViewById(R.id.buttonBack);
		buttonBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toLogin();
			}
		});

		// 销售
		((Button) this.findViewById(R.id.buttonType1))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						toSale();
					}
				});
		// 销售退货
		((Button) this.findViewById(R.id.buttonType2))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						toSaleReturn();
					}
				});
		// 返品处理
		((Button) this.findViewById(R.id.buttonType3))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						toReturn();
					}

				});
		// 移动处理
		((Button) this.findViewById(R.id.buttonType4))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						toTransfer();
					}

				});

		// 查询
		((Button) this.findViewById(R.id.searchFunctionButton))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						toSearch();
					}

				});
	}

	// 转向登录页面
	private void toLogin() {
		this.userName = null;
		this.role = null;
		Intent gotoIntent = new Intent(FunctionActivity.this,
				LoginActivity.class);
		FunctionActivity.this.startActivity(gotoIntent);
	}

	// 转向销售页面
	private void toSale() {
		Intent gotoIntent = new Intent(FunctionActivity.this,
				SaleActivity.class);
		FunctionActivity.this.startActivity(gotoIntent);
	}

	// 转向销售退货页面
	private void toSaleReturn() {
		Intent gotoIntent = new Intent(FunctionActivity.this,
				SaleReturnActivity.class);
		FunctionActivity.this.startActivity(gotoIntent);
	}

	// 转到退货页面
	private void toReturn() {
		// 此处使用整合后的画面，不分主表和明细
		Intent gotoIntent = new Intent(FunctionActivity.this,
				ReturnToWhActivity.class);
		Bundle bundle = new Bundle();

		gotoIntent.putExtras(bundle);
		FunctionActivity.this.startActivity(gotoIntent);
	}

	// 转到移动页面
	private void toTransfer() {
		Intent gotoIntent = new Intent(FunctionActivity.this,
				ShopTransferActivity.class);
		Bundle bundle = new Bundle();

		gotoIntent.putExtras(bundle);
		FunctionActivity.this.startActivity(gotoIntent);
	}

	// 转到查询页面
	private void toSearch() {
		// Intent gotoIntent = new Intent(FunctionActivity.this,
		// ShopTransferActivity.class);
		// Bundle bundle = new Bundle();
		//
		// gotoIntent.putExtras(bundle);
		// FunctionActivity.this.startActivity(gotoIntent);
	}
}
