/**
 * 
 */
package com.netsdl.android.main.view.manage;

import com.netsdl.android.main.R;

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
public class ShopTransferDetailActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_detail);
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
		//移动明细界面确认事件
		((Button) this.findViewById(R.id.confirmButton))
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					//移动确认，保存到数据，生成文件并上传
					Intent gotoIntent = new Intent(ShopTransferDetailActivity.this,
							ShopTransferMainActivity.class);
					Bundle bundle = new Bundle();
				
					gotoIntent.putExtras(bundle);
					ShopTransferDetailActivity.this.startActivity(gotoIntent);
				} catch (RuntimeException e) {
					Toast.makeText(ShopTransferDetailActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		//移动明细界面取消事件，返回到移动主界面
		((Button) this.findViewById(R.id.backButton))
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					Intent gotoIntent = new Intent(ShopTransferDetailActivity.this,
							ShopTransferMainActivity.class);
					Bundle bundle = new Bundle();
				
					gotoIntent.putExtras(bundle);
					ShopTransferDetailActivity.this.startActivity(gotoIntent);
				} catch (RuntimeException e) {
					Toast.makeText(ShopTransferDetailActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});
	}
}
