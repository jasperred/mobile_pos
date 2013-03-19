package com.netsdl.android.main.view.customer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.netsdl.android.main.R;


public class EditTextButton extends LinearLayout// implements EdtInterface 
{

	private Button deleteButton;
	private EditText inputEditText;

	public EditTextButton(Context context) {
		super(context);

	}

	public EditTextButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_edittext_button,
				this, true);
		init();

	}

	private void init() {
		deleteButton = (Button) findViewById(R.id.deleteButton);
		inputEditText = (EditText) findViewById(R.id.inputEditText);
//		inputEditText.addTextChangedListener(tw);// 为输入框绑定一个监听文字变化的监听器
		// 添加按钮点击事件
		deleteButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//hideBtn();// 隐藏按钮
				String str = inputEditText.getText().toString();
				if(str.length()>0)
					inputEditText.setText(str.substring(0,str.length()-1));// 设置输入框内容为空
			}
		});

	}
	
	public EditText getInputEditText()
	{
		return inputEditText;
	}
	
	public Button getDeleteButton()
	{
		return deleteButton;
	}

//	// 当输入框状态改变时，会调用相应的方法
//	TextWatcher tw = new TextWatcher() {
//
//		public void onTextChanged(CharSequence s, int start, int before,
//				int count) {
//			// TODO Auto-generated method stub
//
//		}
//
//		public void beforeTextChanged(CharSequence s, int start, int count,
//				int after) {
//			// TODO Auto-generated method stub
//
//		}
//
//		// 在文字改变后调用
//		public void afterTextChanged(Editable s) {
//			if (s.length() == 0) {
//				hideBtn();// 隐藏按钮
//			} else {
//				showBtn();// 显示按钮
//			}
//
//		}
//
//	};
//
//	public void hideBtn() {
//		// 设置按钮不可见
//		if (deleteButton.isShown())
//			deleteButton.setVisibility(View.GONE);
//
//	}
//
//	public void showBtn() {
//		// 设置按钮可见
//		if (!deleteButton.isShown())
//			deleteButton.setVisibility(View.VISIBLE);
//
//	}

}

//interface EdtInterface {
//
//	public void hideBtn();
//
//	public void showBtn();
//
//}