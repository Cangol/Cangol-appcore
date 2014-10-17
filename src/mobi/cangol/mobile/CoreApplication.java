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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.AppServiceManager;
import mobi.cangol.mobile.service.AppServiceManagerImpl;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Build;
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
	public List<WeakReference<Activity>> activityManager;
	@Override
	public void onCreate() {
		if(mDevMode&&Build.VERSION.SDK_INT>=9){
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
			 .detectAll()
	        .penaltyLog() 
	        .build()); 
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	        .detectAll()
	        .penaltyLog()
	        .build());
		}
		super.onCreate();
		if(mDevMode){
			Log.setLogLevelFormat(android.util.Log.VERBOSE,false);
		}else{
			Log.setLogLevelFormat(android.util.Log.WARN,true);
		}
		mSession=new Session();
		initAppServiceManager();
		activityManager = new ArrayList<WeakReference<Activity>>();
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
	public void addActivityToManager(Activity act) {
		for (final WeakReference<Activity> actR : activityManager) {
			if (actR != null&&!act.equals(actR.get())) {
				activityManager.add(new WeakReference<Activity>(act));
			}
		}
	}

	public void closeAllActivities() {
		for (final WeakReference<Activity> actR : activityManager) {
			if (actR != null&&actR.get()!=null) {
				actR.get().finish();
			}
		}
	}

	public void delActivityFromManager(Activity act) {
		for (final WeakReference<Activity> actR : activityManager) {
			if (actR != null&&act.equals(actR.get())) {
				activityManager.remove(actR);
			}
		}
	}
	public void exit() {
		Log.d("exit");
		mSession.clear();
		if(mAppServiceManager!=null){
			mAppServiceManager.destory();
		}
		//0 正常推退出
		System.exit(0);
	}
	public void setDevMode(boolean devMode) {
		this.mDevMode = devMode;
	}
	
	public boolean isDevMode() {
		return mDevMode;
	}

	/**
	 * @return the mSession
	 */
	public Session getSession() {
		return mSession;
	}
	
}
