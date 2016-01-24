/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.conf;

import java.io.File;

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.utils.StorageUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;

@Service("ConfigService")
/**
 * @author Cangol
 */
public class ConfigServiceImpl implements ConfigService {
	private final static String TAG="ConfigService";
	private Context mContext = null;
	private ServiceProperty mServiceProperty=null;
	private boolean mDebug=false;
    private boolean mUseInternalStorage=false;
	@Override
	public void onCreate(Context context) {
		mContext=context;
	}
	@Override
	public void setDebug(boolean debug) {
		mDebug=debug;
	}
	@Override
	public void init(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
	}

	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}

	@Override
	public ServiceProperty defaultServiceProperty() {
		ServiceProperty sp=new ServiceProperty(TAG);
		sp.putString(IMAGE_DIR, "image");
		sp.putString(DOWNLOAD_DIR, "download");
		sp.putString(TEMP_DIR,"temp");
		sp.putString(UPGRADE_DIR,"upgrade");
		sp.putString(DATABASE_NAME,"app_db");
		sp.putString(SHARED_NAME,"app_shared");
		return sp;
	}

	@Override
	public String getName() {
		return TAG;
	}

	@Override
	public void onDestory() {
		
	}

    public void setUseInternalStorage(boolean useInternalStorage) {
        this.mUseInternalStorage = useInternalStorage;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public File getFileDir(String name) {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        File file=null;
        if(mUseInternalStorage){
            file=  mContext.getFileStreamPath(name);
        }else{
            if(!Environment.isExternalStorageEmulated())
                file=  mContext.getFileStreamPath(name);
            else
                file= StorageUtils.getExternalFileDir(mContext, name);
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return file;
    }

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
	public File getCacheDir() {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        File file=null;
        if(mUseInternalStorage){
            file=  mContext.getCacheDir();
        }else{
            if(!Environment.isExternalStorageEmulated())
                file=  mContext.getCacheDir();
            else
                file= mContext.getExternalCacheDir();
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return file;
	}

	@Override
	public File getImageDir() {
		return getFileDir(mServiceProperty.getString(ConfigService.IMAGE_DIR));
	}

	@Override
	public File getTempDir() {
		return getFileDir(mServiceProperty.getString(ConfigService.TEMP_DIR));
	}

	@Override
	public File getDownloadDir() {
		return getFileDir(mServiceProperty.getString(ConfigService.DOWNLOAD_DIR));
	}
	
	@Override
	public File getUpgradeDir() {
		return getFileDir(mServiceProperty.getString(ConfigService.UPGRADE_DIR));
	}
	
	@Override
	public String getDatabaseName() {
		return mServiceProperty.getString(ConfigService.DATABASE_NAME);
	}

	@Override
	public String getSharedName() {
		return mServiceProperty.getString(ConfigService.SHARED_NAME);
	}
}
