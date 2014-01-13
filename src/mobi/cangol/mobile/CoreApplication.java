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
package mobi.cangol.mobile;

import java.util.List;

import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.AppServiceManager;
import mobi.cangol.mobile.service.AppServiceManagerImpl;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
/**
 * 
 * @author Cangol
 * @date 2011-12-18
 */
public class CoreApplication extends Application {
	
	private AppServiceManager mAppServiceManager;
	public Session mSession;
	private boolean mDevMode=true;
	@Override
	public void onCreate() {
		if(mDevMode){
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
	        .detectDiskReads()  
	        .detectDiskWrites()  
	        .detectNetwork()  
	        .penaltyLog() 
	        .build()); 
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	        .detectLeakedSqlLiteObjects()
	        .detectLeakedClosableObjects()
	        .penaltyLog()
	        .build());
		}
		super.onCreate();
		mSession=new Session();
		initAppServiceManager();
	}
	
	private void initAppServiceManager() {
		mAppServiceManager=new AppServiceManagerImpl(this);
	}	

	public AppServiceManager getAppServiceManager() {
		return mAppServiceManager;
	}

	public AppService getAppService(String name){
		if(mAppServiceManager!=null){
			return mAppServiceManager.getAppService(name);
		}
		return null;
	}
	
	public void exit() {
		mSession.clear();
		if(mAppServiceManager!=null){
			mAppServiceManager.destory();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	public void setDevMode(boolean devMode) {
		this.mDevMode = devMode;
	}

	/**
	* return application is background
	* @param context
	* @return
	*/
	public boolean isBackground(Context context) {
	
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
}
