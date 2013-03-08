/**
 * 
 */
package com.netsdl.android.main.view.manage;

import com.netsdl.android.common.Constant;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author jasper
 *
 */
public class ShopTransferMainActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_main);
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
		//移动主表确认事件
		((Button) this.findViewById(R.id.confirmButton))
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent gotoIntent = new Intent(ShopTransferMainActivity.this,
							ShopTransferDetailActivity.class);
					//需要把主界面输入信息传给明细界面
					Bundle bundle = new Bundle();
				
					gotoIntent.putExtras(bundle);
					ShopTransferMainActivity.this.startActivity(gotoIntent);
				} catch (RuntimeException e) {
					Toast.makeText(ShopTransferMainActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		//移动主界面取消事件
		((Button) this.findViewById(R.id.backButton))
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent gotoIntent = new Intent(ShopTransferMainActivity.this,
							MainActivity.class);
					Bundle bundle = new Bundle();
					//临时方法，IS_LOGIN为true表示已经是登录状态，在MainActivity的onCreate处理中直接进到function界面
					bundle.putBoolean(Constant.IS_LOGIN, true);
					gotoIntent.putExtras(bundle);
					ShopTransferMainActivity.this.startActivity(gotoIntent);
				} catch (RuntimeException e) {
					Toast.makeText(ShopTransferMainActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});
	}

}
