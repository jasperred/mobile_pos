package com.netsdl.android.common.dialog.progress;

public interface ProgressThreadable {
	void before() throws Exception;

	void proc() throws Exception;

	void after() throws Exception;

}
