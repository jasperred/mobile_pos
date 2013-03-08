package com.netsdl.android.init.dialog.progress.commodity;

import java.io.BufferedReader;
import java.util.Map;

import android.os.Handler;

import com.netsdl.android.common.Constant;
import com.netsdl.android.common.Util;
import com.netsdl.android.common.db.SkuMaster;
import com.netsdl.android.common.dialog.progress.AbstractProgressThread;

public class CommodityProgressThread extends AbstractProgressThread {
	private SkuMaster skuMaster;
	private String url;
	BufferedReader in;
	String line;

	public CommodityProgressThread(Handler handler) {
		super(handler);
	}

	public void before() throws Exception {
		//skuMaster.clear();
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

		//skuMaster.delete(line);
		Map<String,Object> skuMap = skuMaster.parserCSV(line);
		skuMap.remove(SkuMaster.COLUMN_SKU_ID);
		int r = skuMaster.update(skuMap, new String[]{(String)skuMap.get(SkuMaster.COLUMN_SKU_CD)}, new String[]{SkuMaster.COLUMN_SKU_CD});
		if(r<=0)
		{
			int c = skuMaster.getCount(null,new String[]{});
			skuMap.put(SkuMaster.COLUMN_SKU_ID, c+2);
			skuMaster.insert(skuMap);
		}

		line = in.readLine();

	}

	public void after() throws Exception {
		if (in != null)
			in.close();
	}

	public void setSkuMaster(SkuMaster skuMaster) {
		this.skuMaster = skuMaster;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
