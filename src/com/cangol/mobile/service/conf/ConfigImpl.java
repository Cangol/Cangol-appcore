package com.cangol.mobile.service.conf;

import android.content.Context;

import com.cangol.mobile.service.Service;
import com.cangol.mobile.utils.StorageUtils;
@Service("config")
public class ConfigImpl implements Config {
	private Context mContext = null;
	@Override
	public String getAppDir() {
		return StorageUtils.getExternalStorageDir(mContext, "app_ext");
	}

	@Override
	public String getCacheDir() {
		return StorageUtils.getExternalCacheDir(mContext).getAbsolutePath();
	}

	@Override
	public String getImageDir() {
		return getAppDir()+"/image/";
	}

	@Override
	public String getTempDir() {
		return getAppDir()+"/temp/";
	}

	@Override
	public String getDownloadDir() {
		return getAppDir()+"/download/";
	}

	@Override
	public String getDatabaseName() {
		return "app_db";
	}

	@Override
	public void setContext(Context ctx) {
		mContext=ctx;
	}

	@Override
	public String getName() {
		return "config";
	}

	@Override
	public void destory() {
		
	}


}
