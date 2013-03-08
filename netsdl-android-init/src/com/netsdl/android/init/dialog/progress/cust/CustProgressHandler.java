package com.netsdl.android.init.dialog.progress.cust;

import com.netsdl.android.common.dialog.progress.AbstractProgressThread;

import android.os.Handler;
import android.os.Message;

public class CustProgressHandler extends Handler {
	CustProgressDialog custProgressDialog;
	CustProgressThread custProgressThread;
	
	

	public CustProgressThread getCustProgressThread() {
		return custProgressThread;
	}

	public void setCustProgressThread(CustProgressThread custProgressThread) {
		this.custProgressThread = custProgressThread;
	}

	public CustProgressHandler(
			CustProgressDialog custProgressDialog) {
		this.custProgressDialog = custProgressDialog;
	}

	@Override
	public void handleMessage(Message msg) {

		int progress = msg.arg1;
		custProgressDialog.progressDialog.setProgress(progress);

		if (progress >= custProgressDialog.progressDialog.getMax()) {
			custProgressDialog.activity.dismissDialog(custProgressDialog.hashCode());
			custProgressThread.setState(AbstractProgressThread.STATE_DONE);
		}

		super.handleMessage(msg);
	}

}
