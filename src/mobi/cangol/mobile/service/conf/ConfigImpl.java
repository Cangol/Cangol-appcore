package mobi.cangol.mobile.service.conf;

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.utils.StorageUtils;
import android.content.Context;
@Service("config")
public class ConfigImpl implements Config {
	private Context mContext = null;
	@Override
	public void init() {
		
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
	public String getSharedName() {
		return "app_shared";
	}


}
