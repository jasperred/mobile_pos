package com.netsdl.android.common.dialog.progress;


import com.netsdl.android.common.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public class CommonProgressDialog extends AbstractProgressDialog {

	public CommonProgressDialog(Activity activity) {
		super(activity);
		setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		setMessage(activity.getString(R.string.Loading));

		setCancelable(false);
		setButtonWhichButton(DialogInterface.BUTTON_POSITIVE);
		setButtonText(activity.getString(R.string.Cancel));
	}

}
