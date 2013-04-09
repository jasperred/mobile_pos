package com.netsdl.android.main.view.customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.PaymentMaster;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.customer.EditTextButton;

public class InputDialog extends Dialog {

	Context context;
	private EditText inputEditText;
	private String inputStr;// 支付结果
	private boolean isConfirm;// 是否支付
	private boolean isInt = false;// 是否是整数
	private int scale = 2;// 小数位数
	private BigDecimal min;//最大值
	private BigDecimal max;//最小值

	Handler mHandler;

	public void setInt(boolean isInt) {
		this.isInt = isInt;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public InputDialog(Context context) {
		super(context);
		setOwnerActivity((Activity) context);
		this.context = context;
	}

	public InputDialog(Context context, int theme) {
		super(context, theme);
		setOwnerActivity((Activity) context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.view_input);
		init();
	}

	// 提供对话框的模式支持
	public boolean showDialog(int width, int height) {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message mesg) {
				// process incoming messages here
				// super.handleMessage(msg);
				throw new RuntimeException();
			}
		};
		isConfirm = false;
		super.show();
		// 设置窗口大小
		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		params.width = (int) (width * 0.4);
		params.height = (int) (height*0.83);
		this.getWindow().setAttributes(params);
		try {
			Looper.getMainLooper().loop();
		} catch (RuntimeException e2) {
		}
		return isConfirm;
	}

	private void init() {
		// 初始化数字按钮
		int[] buttons = new int[] { R.id.button0, R.id.checkUploadButton, R.id.uploadCheckFileButton,
				R.id.uploadAllCheckFileButton, R.id.deleteCheckFileButton, R.id.button5, R.id.button6,
				R.id.button7, R.id.button8, R.id.button9 };
		int c = 0;
		for (int i : buttons) {
			final Button button = (Button) this.findViewById(i);
			final int cc = c;
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					buttonInput("" + cc);
				}
			});
			c++;
		}
		// 小数点
		final Button buttonPoint = (Button) this.findViewById(R.id.buttonPoint);
		if (isInt) {
			buttonPoint.setEnabled(false);
		} else {
			buttonPoint.setEnabled(true);
			buttonPoint.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String t = inputEditText.getText().toString().trim();
					if (t.indexOf(".") > 0)
						return;
					if (t.length() == 0)
						buttonInput("0");
					buttonInput(".");
				}
			});
		}
		// 结账
		final Button buttonConfirm = (Button) this
				.findViewById(R.id.buttonConfirm);
		buttonConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				confirm();
			}
		});

		if (inputEditText == null)
			inputEditText = ((EditTextButton) this
					.findViewById(R.id.inputEditTextButton)).getInputEditText();
	}

	// button输入
	private void buttonInput(String str) {
		try {
			if (str == null)
				return;
			String t = inputEditText.getText().toString().trim();
			if(t.equals("0")&&!str.equals("."))
				t = str;
			else
				t = t + str.trim();
			// 最大值最小值控制
			if(min!=null)
			{
				if(new BigDecimal(t).doubleValue()<min.doubleValue())
					t = min.toString();
			}
			if(max!=null)
			{
				if(new BigDecimal(t).doubleValue()>max.doubleValue())
					t = max.toString();
			}
			inputEditText.setText(t);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 确认支付
	private void confirm() {
		inputStr = this.inputEditText.getText().toString().trim();
		if (inputStr.length() == 0)
			return;
		isConfirm = true;
		this.dismiss();
		// 向Handler发送消息，这样主Activity才能得到执行结果
		Message m = mHandler.obtainMessage();
		mHandler.sendMessage(m);
	}

	public String getInputStr() {
		return inputStr;
	}

}
