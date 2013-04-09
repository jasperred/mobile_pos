package com.netsdl.android.main.view;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netsdl.android.common.Structs;
import com.netsdl.android.common.Structs.LoginStatus;
import com.netsdl.android.common.Structs.LoginViewData;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.StoreMaster;
import com.netsdl.android.main.R;

public class Login {
	public static final int LAYOUT_COMMON33 = R.layout.common33;
	final LayoutInflater inflater;
	final View view;
	final LinearLayout linearLayoutLogin;
	final FrameLayout coreLayout;
	MainActivity parent;
	public LoginViewData data;

	public Login(MainActivity parent) {
		this.parent = parent;
		data = new Structs().new LoginViewData();

		inflater = LayoutInflater.from(parent);
		view = inflater.inflate(LAYOUT_COMMON33, null);
		linearLayoutLogin = (LinearLayout) inflater.inflate(R.layout.login,
				null);

		LinearLayout row1 = (LinearLayout) view.findViewById(R.id.row1);
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) row1
				.getLayoutParams();
		params.weight = 3;
		row1.setLayoutParams(params);

		LinearLayout row2 = (LinearLayout) view.findViewById(R.id.row2);
		params = (android.widget.LinearLayout.LayoutParams) row2
				.getLayoutParams();
		params.weight = 0.5f;
		row2.setLayoutParams(params);

		LinearLayout row3 = (LinearLayout) view.findViewById(R.id.row3);
		params = (android.widget.LinearLayout.LayoutParams) row3
				.getLayoutParams();
		params.weight = 1;
		row3.setLayoutParams(params);

		LinearLayout column1 = (LinearLayout) view.findViewById(R.id.column1);
		params = (android.widget.LinearLayout.LayoutParams) column1
				.getLayoutParams();
		params.weight = 3;
		column1.setLayoutParams(params);

		LinearLayout column2 = (LinearLayout) view.findViewById(R.id.column2);
		column2.getLayoutParams();
		params = (android.widget.LinearLayout.LayoutParams) column2
				.getLayoutParams();
		params.weight = 1;
		column2.setLayoutParams(params);

		LinearLayout column3 = (LinearLayout) view.findViewById(R.id.column3);
		params = (android.widget.LinearLayout.LayoutParams) column3
				.getLayoutParams();
		params.weight = 3;
		column3.setLayoutParams(params);

		coreLayout = (FrameLayout) view.findViewById(R.id.core);
	}

	public void init() {

		parent.status = MainActivity.Status.Login;

		parent.setContentView(view);
		coreLayout.removeAllViews();
		coreLayout.addView(linearLayoutLogin);

		final EditText editText = (EditText) parent.findViewById(R.id.userNameEditText);

		editText.setText(data.text);

		if (data.status == LoginStatus.operaterID) {
			setOperaterID(editText);
		} else {
			editText.setTransformationMethod(new PasswordTransformationMethod());
		}

		int[] buttons = new int[] { R.id.button0, R.id.checkUploadButton, R.id.uploadCheckFileButton,
				R.id.uploadAllCheckFileButton, R.id.deleteCheckFileButton, R.id.button5, R.id.button6,
				R.id.button7, R.id.button8, R.id.button9 };

		for (int i : buttons) {
			final Button button = (Button) parent.findViewById(i);

			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					data.text = editText.getText().toString()
							+ Integer.parseInt(button.getText().toString());
					editText.setText(data.text);
				}
			});
		}

		final Button buttonBack = (Button) parent.findViewById(R.id.buttonBack);

		buttonBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (editText.getText().length() <= 0) {
					if (data.status == LoginStatus.operaterID)
						return;
					data.status = LoginStatus.operaterID;
					setOperaterID(editText);
				} else {
					data.text = editText
							.getText()
							.delete(editText.getText().length() - 1,
									editText.getText().length()).toString();
					editText.setText(data.text);
				}
			}
		});

		final Button buttonClear = (Button) parent
				.findViewById(R.id.buttonClear);
		buttonClear.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (editText.getText().length() <= 0)
					return;
				data.text = "";
				editText.setText(data.text);
			}
		});

		final Button buttonReturn = (Button) parent
				.findViewById(R.id.buttonReturn);
		buttonReturn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (data.status == LoginStatus.operaterID) {
					try {
						int iTemp = Integer.parseInt(editText.getText()
								.toString());

						try {
							data.storeObjs = DatabaseHelper.getSingleColumn(
									parent.getContentResolver(),
									new Object[] { iTemp }, StoreMaster.class);
						} catch (IllegalArgumentException e1) {
						} catch (SecurityException e1) {
						} catch (IllegalAccessException e1) {
						} catch (NoSuchFieldException e1) {
						}

						if (data.storeObjs == null) {
							Toast.makeText(parent, R.string.msg_no_id,
									Toast.LENGTH_SHORT).show();
							data.text = "";
							editText.setText(data.text);

						} else {

							data.status = LoginStatus.password;

							String name = (String) DatabaseHelper
									.getColumnValue(data.storeObjs,
											StoreMaster.COLUMN_NAME,
											StoreMaster.COLUMNS);

							((TextView) parent
									.findViewById(R.id.textViewUsername))
									.setText(name);

							((TextView) parent
									.findViewById(R.id.textViewUsernameFixed))
									.setText(R.string.username);

							((TextView) parent.findViewById(R.id.status))
									.setText(R.string.password);

							data.text = "";
							editText.setText(data.text);
							editText.setTransformationMethod(new PasswordTransformationMethod());

						}

					} catch (NumberFormatException nfe) {
						Toast.makeText(parent, R.string.msg_id_must_be_number,
								Toast.LENGTH_SHORT).show();
						data.text = "";
						editText.setText(data.text);
					}

				} else {
					if (editText.getText().toString().length() > 0) {
						String str1 = Util
								.getMD5(editText.getText().toString());
						String str2 = (String) DatabaseHelper.getColumnValue(
								data.storeObjs, StoreMaster.COLUMN_MD5,
								StoreMaster.COLUMNS);
						if (str1.equals(str2)) {
							// parent.main.init();
							// parent.type.init();
							parent.function.init();
						} else {
							Toast.makeText(parent, R.string.msg_password_wrong,
									Toast.LENGTH_SHORT).show();
							data.text = "";
							editText.setText(data.text);

						}

					}

				}
			}
		});

	}

	private void setOperaterID(EditText editText) {
		((TextView) parent.findViewById(R.id.status))
				.setText(R.string.username);
		((TextView) parent.findViewById(R.id.textViewUsername)).setText("");
		((TextView) parent.findViewById(R.id.textViewUsernameFixed))
				.setText("");
		editText.setTransformationMethod(null);

	}

}
