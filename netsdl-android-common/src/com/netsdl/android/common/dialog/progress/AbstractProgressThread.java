package com.netsdl.android.common.dialog.progress;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class AbstractProgressThread extends Thread implements
		ProgressThreadable {
	Handler handler;
	public final static int STATE_DONE = 0;
	public final static int STATE_RUNNING = 1;
	public final static int STATE_CANCEL = 2;
	public int mState;
	int total;

	public AbstractProgressThread(Handler handler) {
		this.handler = handler;
	}

	public void run() {
		mState = STATE_RUNNING;
		total = 0;
		Log.d("Thread", "start");
		try {
			before();
		} catch (Exception e) {
			mState = STATE_CANCEL;
		}
		while (mState == STATE_RUNNING) {
			Message msg = handler.obtainMessage();
			msg.arg1 = total;
			handler.sendMessage(msg);
			total++;
			try {
				proc();
			} catch (Exception e) {
				mState = STATE_CANCEL;
			}
		}
		try {
			after();
		} catch (Exception e) {
			mState = STATE_CANCEL;
		}
		Log.d("Thread", "end");
	}

	public void setState(int state) {
		mState = state;
	}

}
