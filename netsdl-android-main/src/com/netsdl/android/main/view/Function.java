package com.netsdl.android.main.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.netsdl.android.common.Structs;
import com.netsdl.android.common.Structs.Type;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.manage.ReturnToWhActivity;
import com.netsdl.android.main.view.manage.ShopTransferActivity;

public class Function{
	public static final int LAYOUT_COMMON33 = R.layout.common33;
	final View view;
	final LayoutInflater inflater;
	final LinearLayout linearLayoutType;
	final FrameLayout coreLayout;
	MainActivity parent;

	int[] ids = new int[] { R.id.checkButton, R.id.buttonType2,
			R.id.buttonType3,R.id.buttonType4, R.id.searchFunctionButton };
	Type[] types = new Type[] { Type.type1, Type.type2, Type.type3 };

	int currentID = R.id.checkButton;

	public Function(MainActivity parent) {
		this.parent = parent;
		inflater = LayoutInflater.from(parent);
		view = inflater.inflate(LAYOUT_COMMON33, null);

		linearLayoutType = (LinearLayout) inflater.inflate(R.layout.function,
				null);
		coreLayout = (FrameLayout) view.findViewById(R.id.core);
	}

	public void init() {
		parent.status = MainActivity.Status.Function;
		parent.setContentView(view);
		coreLayout.removeAllViews();
		coreLayout.addView(linearLayoutType);

		//setFunction();
		//initFunction();
		
		final Button buttonBack = (Button) parent.findViewById(R.id.buttonBack);
		buttonBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				parent.login.data = new Structs().new LoginViewData();
				parent.login.init();
			}
		});
		
		//销售
		((Button) parent.findViewById( R.id.checkButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				currentID =  R.id.checkButton;
				parent.type = types[getIndex(currentID)];
				parent.preMain.init();
			}
		});
		//销售退货
		((Button) parent.findViewById( R.id.buttonType2))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				currentID =  R.id.buttonType2;
				parent.type = types[getIndex(currentID)];
				parent.preMain.init();
			}
		});
		//返品处理
		((Button) parent.findViewById( R.id.buttonType3))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//parent.type = types[getIndex( R.id.buttonType3)];
				//此处使用整合后的画面，不分主表和明细
				Intent gotoIntent = new Intent(parent,
						ReturnToWhActivity.class);
				Bundle bundle = new Bundle();
			
				gotoIntent.putExtras(bundle);
				parent.startActivity(gotoIntent);
			}
		});
		//移动处理
		((Button) parent.findViewById( R.id.buttonType4))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//parent.type = types[getIndex( R.id.buttonType4)];
				Intent gotoIntent = new Intent(parent,
						ShopTransferActivity.class);
				Bundle bundle = new Bundle();
			
				gotoIntent.putExtras(bundle);
				parent.startActivity(gotoIntent);
			}
		});

		//查询
		((Button) parent.findViewById( R.id.searchFunctionButton))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//parent.type = types[getIndex( R.id.buttonType4)];
				Intent gotoIntent = new Intent(parent,
						ShopTransferActivity.class);
				Bundle bundle = new Bundle();
			
				gotoIntent.putExtras(bundle);
				parent.startActivity(gotoIntent);
			}
		});
	}

	//@sunmw 2013.2.20 此方法取消，没太大作用
	private void setFunction() {
		//设计功能区按钮的背景颜色，已选择的功能设为浅灰色
		for (int id : ids) {
			((Button) parent.findViewById(id))
					.setBackgroundColor(Color.DKGRAY);
		}
		((Button) parent.findViewById(currentID))
				.setBackgroundColor(Color.LTGRAY);

		parent.type = types[getIndex(currentID)];

		// ((Button) parent.findViewById(ids[getIndex(currentID)]))
		// .setBackgroundColor(Color.CYAN);

	}

	//@sunmw 2013.2.20 此方法已经不适合初始化Button事件了
	private void initFunction() {
		for (final int id : ids) {
			((Button) parent.findViewById(id))
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							currentID = id;
							setFunction();
							parent.preMain.init();
						}
					});
		}

	}

	private int getIndex(int id) {
		for (int i = 0; i < ids.length; i++)
			if (ids[i] == id)
				return i;
		return -1;
	}

}
