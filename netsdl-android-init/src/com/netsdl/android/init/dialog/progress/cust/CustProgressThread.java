package com.netsdl.android.init.dialog.progress.cust;

import java.io.BufferedReader;

import android.os.Handler;

import com.netsdl.android.common.Constant;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.CustMaster;
import com.netsdl.android.common.db.DeviceMaster;
import com.netsdl.android.common.dialog.progress.AbstractProgressThread;

public class CustProgressThread extends AbstractProgressThread {
	private CustMaster custMaster;
	private String url;
	BufferedReader in;
	String line;

	public CustProgressThread(Handler handler) {
		super(handler);
	}

	public void before() throws Exception {
		custMaster.clear();
		if (url == null) {
			setState(STATE_CANCEL);
			return;
		}

		in = Util.getBufferedReaderFromURI(url,Constant.UTF_8);
		line = in.readLine();

	}

	public void proc() throws Exception {
		if (line == null) {
			setState(STATE_DONE);
			return;
		}

		if (line.trim().length() == 0)
			return;

		custMaster.insert(line);

		line = in.readLine();

	}

	public void after() throws Exception {
		if (in != null)
			in.close();
	}

	public void setCustMaster(CustMaster custMaster) {
		this.custMaster = custMaster;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
