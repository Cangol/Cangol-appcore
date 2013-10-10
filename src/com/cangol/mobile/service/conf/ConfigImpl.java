package com.cangol.mobile.service.conf;

import android.content.Context;

import com.cangol.mobile.utils.StorageUtils;

public class ConfigImpl implements Config {
	private Context mContext = null;
	@Override
	public String getAppDir() {
		
		return null;
	}

	@Override
	public String getCacheDir() {
		return StorageUtils.getExternalCacheDir(mContext).getAbsolutePath();
	}

	@Override
	public String getImageDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTempDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDownloadDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatabaseName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContext(Context ctx) {
		mContext=ctx;
	}

	@Override
	public String getName() {
		return "config";
	}


}
