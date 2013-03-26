package com.netsdl.android.main.view.sale;

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

public class PayDialog extends Dialog {

	private BigDecimal payable;
	private Map<String, Integer> payMap;
	private List<Map<String, Object>> payList;
	private List<Map> payMethodList;
	private SimpleAdapter payMethodAdapter;
	Context context;
	private EditText moneyEditText;
	private List payResultList;// 支付结果
	private boolean isPay;// 是否支付

	Handler mHandler;

	public PayDialog(Context context) {
		super(context);
		setOwnerActivity((Activity) context);
		setTitle(R.string.Payment);
		this.context = context;
	}

	public PayDialog(Context context, int theme) {
		super(context, theme);
		setOwnerActivity((Activity) context);
		setTitle(R.string.Payment);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.view_pay);
		init();
	}

	//提供对话框的模式支持
	public boolean showDialog(int width,int height) {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message mesg) {
				// process incoming messages here
				// super.handleMessage(msg);
				throw new RuntimeException();
			}
		};
		isPay = false;
		super.show();
		//设置窗口大小
		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		 params.width = (int) (width*0.8);
		 params.height =(int) (height) ;
		 this.getWindow().setAttributes(params);
		try {
			Looper.getMainLooper().loop();
		} catch (RuntimeException e2) {
		}
		return isPay;
	}

	private void init() {
		// 初始化数字按钮
		int[] buttons = new int[] { R.id.button0, R.id.button1, R.id.button2,
				R.id.button3, R.id.button4, R.id.button5, R.id.button6,
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
		buttonPoint.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String t = moneyEditText.getText().toString().trim();
				if (t.indexOf(".") > 0)
					return;
				if (t.length() == 0)
					buttonInput("0");
				buttonInput(".");
			}
		});
		// 结账
		final Button buttonConfirm = (Button) this
				.findViewById(R.id.buttonConfirm);
		buttonConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pay();
			}
		});
		if (payMap == null)
			payMap = new HashMap(10, 1);
		if (payList == null)
			payList = new ArrayList(10);
		if (payResultList == null)
			payResultList = new ArrayList(10);
		payResultList.clear();
		final ListView payMethodListView = (ListView) this
				.findViewById(R.id.payMethodListView);
		if (payMethodAdapter == null) {
			payMethodAdapter = new SimpleAdapter(
					payMethodListView.getContext(), payList,
					R.layout.view_pay_method, new String[] { "payMethod",
							"money" }, new int[] { R.id.payMethodTextView,
							R.id.moneyTextView });
			payMethodListView.setAdapter(payMethodAdapter);
		}
		if (moneyEditText == null)
			moneyEditText = ((EditTextButton) this.findViewById(R.id.moneyEditText)).getInputEditText();
		// 显示应付金额
		((TextView) this.findViewById(R.id.payableTextView)).setText(Util
				.round(payable).toString());
		computeMoney(null, null);
		initPayMethod();
	}

	// 初始化支付方式
	public void initPayMethod() {
		int[] buttons = new int[] { R.id.pay1Button, R.id.pay2Button,
				R.id.pay3Button, R.id.pay4Button, R.id.pay5Button,
				R.id.pay6Button, R.id.pay7Button, R.id.pay8Button };
		if (payMethodList == null || payMethodList.size() == 0) {
			// 未设置的按钮隐藏
			for (int i = 1; i < buttons.length; i++) {
				final Button button = (Button) this.findViewById(buttons[i]);
				button.setVisibility(View.INVISIBLE);
			}
			final Button button = (Button) this.findViewById(R.id.pay1Button);
			button.setVisibility(View.VISIBLE);
			button.setText("现金");
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String s = ((EditText) PayDialog.this
							.findViewById(R.id.moneyEditText)).getText()
							.toString().trim();
					if (s.length() == 0)
						return;
					computeMoney("现金", "1");
				}
			});
		} else {
			int size = payMethodList.size();
			if (size > buttons.length)
				size = buttons.length;
			for (int i = 0; i < size; i++) {
				final Button button = (Button) this.findViewById(buttons[i]);
				button.setVisibility(View.VISIBLE);
				Map m = payMethodList.get(i);
				final String name = (String) m.get(PaymentMaster.COLUMN_NAME);
				button.setText(name);
				final String id = (String) m.get(PaymentMaster.COLUMN_ID);
				button.setText(name);
				button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						computeMoney(id, name);
					}
				});

			}
			// 未设置的按钮隐藏
			if (size < buttons.length) {
				for (int i = size; i < buttons.length; i++) {
					final Button button = (Button) this
							.findViewById(buttons[i]);
					button.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	// button输入
	private void buttonInput(String str) {
		try {
			if (str == null)
				return;
			String t = moneyEditText.getText().toString().trim();
			if(t.equals("0")&&!str.equals("."))
				t = str;
			else
				t = t + str.trim();
			//保证小数位数
			moneyEditText.setText(t);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 设置应收金额
	public void setPayable(BigDecimal payable) {
		this.payable = payable;
	}

	// 设置支付方式
	public void setPayMethodList(List<Map> payMethodList) {
		this.payMethodList = payMethodList;
	}

	// 计算已经支付金额
	private void computeMoney(String id, String payMethod) {
		String c = moneyEditText.getText().toString().trim();
		if (c.length() == 0)
			c = "0";
		BigDecimal money = new BigDecimal(c);
		// 把支付的金额放到payList中，payMap保存payMethod在payList中的位置
		if (payMethod != null && payMethod.trim().length() > 0
				&& money.doubleValue() != 0) {
			Integer s = payMap.get(payMethod);
			if (s == null) {
				Map m = new HashMap();
				m.put("payMethod", payMethod);
				m.put("money", Util.round(money));
				m.put("id", id);
				payList.add(m);
				s = payList.size() - 1;
				payMap.put(payMethod, s);
			} else {
				Map m = payList.get(s);
				BigDecimal a = (BigDecimal) m.get("money");
				if (a != null)
					a = a.add(money);
				else
					a = money;
				m.put("money", Util.round(a));
			}
		}
		// 计算已付金额
		BigDecimal paid = new BigDecimal(0);
		for (Map m : payList) {
			BigDecimal a = (BigDecimal) m.get("money");
			if (a != null)
				paid = paid.add(a);
		}
		// 支付方式内容改变
		payMethodAdapter.notifyDataSetChanged();
		// 计算未付及找零
		BigDecimal diff = this.payable.subtract(paid);
		if (diff.doubleValue() < 0)
			diff = new BigDecimal(0);
		BigDecimal change = paid.subtract(this.payable);
		if (change.doubleValue() < 0)
			change = new BigDecimal(0);
		((TextView) this.findViewById(R.id.paidTextView)).setText(Util.round(
				paid).toString());
		((TextView) this.findViewById(R.id.payableDiffTextView)).setText(Util
				.round(diff).toString());
		((TextView) this.findViewById(R.id.payChangeTextView)).setText(Util
				.round(change).toString());
		moneyEditText.setText("0");
	}

	// 确认支付
	private void pay() {
		//检查是否支付完成
		String diff = ((TextView) this.findViewById(R.id.payableDiffTextView)).getText().toString();
		if(new BigDecimal(diff).doubleValue()!=0)
		{
			Toast.makeText(this.context, R.string.pay_msg2,
					Toast.LENGTH_LONG).show();
			return;
		}
		payResultList.clear();
		isPay = true;
		payResultList.addAll(payList);
		String c = ((TextView) this.findViewById(R.id.payChangeTextView))
				.getText().toString().trim();
		if (c.length() > 0) {
			BigDecimal cc = new BigDecimal(c);
			if (cc.doubleValue() > 0) {
				Map m = new HashMap();
				m.put("id", "99");
				m.put("payMethod", "找零");
				m.put("money", cc);
				payResultList.add(m);
			}
		}
		this.dismiss();
		//向Handler发送消息，这样主Activity才能得到执行结果
        Message m = mHandler.obtainMessage();  
        mHandler.sendMessage(m);  
	}

	public void clear() {
		((TextView) this.findViewById(R.id.paidTextView)).setText("0");
		((TextView) this.findViewById(R.id.payableDiffTextView)).setText("0");
		((TextView) this.findViewById(R.id.payChangeTextView)).setText("0");
		((TextView) this.findViewById(R.id.payableTextView)).setText("0");
		payMap.clear();
		payList.clear();
		payMethodAdapter.notifyDataSetChanged();
		moneyEditText.setText("");
		isPay = false;
		payResultList.clear();
	}

	public boolean isPay() {
		return isPay;
	}

	public List getPayResultList() {
		return payResultList;
	}
}
