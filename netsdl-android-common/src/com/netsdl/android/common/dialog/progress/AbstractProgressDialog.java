package com.netsdl.android.common.dialog.progress;

import com.netsdl.android.common.view.dialog.Dialogable;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Message;

public abstract class AbstractProgressDialog implements Dialogable {
	public Activity activity;
	public ProgressDialog progressDialog;
	AbstractProgressThread progressThread;

	public AbstractProgressDialog(Activity activity) {
		this.activity = activity;
		progressDialog = new ProgressDialog(activity);
		setButtonListener(new ProgressDialogOnClickListener());

	}

	public Dialog onCreateDialog(int id) {
		progressDialog.setProgressStyle(getProgressStyle());
		progressDialog.setMessage(getMessage());
		progressDialog.setCancelable(getCancelable());

		OnClickListener buttonListener = getButtonListener();
		Message buttonMsg = getButtonMsg();

		if (buttonListener != null && buttonMsg == null)
			progressDialog.setButton(getButtonWhichButton(), getButtonText(),
					buttonListener);
		if (buttonListener == null && buttonMsg != null)
			progressDialog.setButton(getButtonWhichButton(), getButtonText(),
					buttonMsg);
		progressDialog.setOnDismissListener(getOnDismissListener());

		return progressDialog;
	}

	public void onPrepareDialog(int id, Dialog dialog) {
		progressDialog.setProgress(getProgressStart());
		progressDialog.setMax(getProgressMax());
		progressThread = getProgressThread();
		progressThread.start();

	}

	private int progressStyle;

	public int getProgressStyle() {
		return progressStyle;
	}

	public void setProgressStyle(int progressStyle) {
		this.progressStyle = progressStyle;
	}

	private CharSequence message;

	public CharSequence getMessage() {
		return message;
	}

	public void setMessage(CharSequence message) {
		this.message = message;
	}

	private int buttonWhichButton;

	public int getButtonWhichButton() {
		return buttonWhichButton;
	}

	public void setButtonWhichButton(int buttonWhichButton) {
		this.buttonWhichButton = buttonWhichButton;
	}

	private CharSequence buttonText;

	public CharSequence getButtonText() {
		return buttonText;
	}

	public void setButtonText(CharSequence buttonText) {
		this.buttonText = buttonText;
	}

	private boolean cancelable = true;

	public boolean getCancelable() {
		return cancelable;
	}

	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
	}

	private OnClickListener buttonListener;

	public OnClickListener getButtonListener() {
		return buttonListener;
	}

	public void setButtonListener(OnClickListener buttonListener) {
		this.buttonListener = buttonListener;
	}

	private OnDismissListener onDismissListener;

	public void setOnDismissListener(OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
	}

	public OnDismissListener getOnDismissListener() {
		return onDismissListener;
	}

	private Message buttonMsg;

	public Message getButtonMsg() {
		return buttonMsg;
	}

	public void setButtonMsg(Message buttonMsg) {
		this.buttonMsg = buttonMsg;
	}

	private int progressStart = 0;

	public int getProgressStart() {
		return progressStart;
	}

	public void setProgressStart(int progressStart) {
		this.progressStart = progressStart;
	}

	private int progressMax;

	public int getProgressMax() {
		return progressMax;
	}

	public void setProgressMax(int progressMax) {
		this.progressMax = progressMax;
	}

	private AbstractProgressThread abstractProgressThread;

	public AbstractProgressThread getProgressThread() {
		return abstractProgressThread;
	}

	public void setProgressThread(AbstractProgressThread abstractProgressThread) {
		this.abstractProgressThread = abstractProgressThread;
	}

	class ProgressDialogOnClickListener implements OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			getProgressThread().setState(AbstractProgressThread.STATE_CANCEL);
			progressDialog.dismiss();
		}

	}

}
