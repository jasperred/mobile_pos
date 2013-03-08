package com.netsdl.android.common.view.dialog;

import android.app.Dialog;

public interface Dialogable {
	void onPrepareDialog(int id, Dialog dialog);

	Dialog onCreateDialog(int id);

}
