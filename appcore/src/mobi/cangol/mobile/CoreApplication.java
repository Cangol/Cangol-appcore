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
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

/**
 * 
 * @author Cangol
 * @date 2011-11-28
 */

public class CoreApplication extends Application {

	private AppServiceManager mAppServiceManager;
	public Session mSession;
	private boolean mDevMode = false;
	public List<WeakReference<Activity>> mActivityManager;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() {
		if (mDevMode && Build.VERSION.SDK_INT >= 9) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
		}
		super.onCreate();
		if (mDevMode) {
			Log.setLogLevelFormat(android.util.Log.VERBOSE, false);
		} else {
			Log.setLogLevelFormat(android.util.Log.WARN, true);
		}
		mSession = new Session();
		initAppServiceManager();
		mActivityManager = new ArrayList<WeakReference<Activity>>();
	}

	/**
	 * 初始化应用服务管理器
	 */
	private void initAppServiceManager() {
		mAppServiceManager = new AppServiceManagerImpl(this);
	}

	/**
	 * 获取应用服务管理器
	 * 
	 * @return
	 */
	public AppServiceManager getAppServiceManager() {
		return mAppServiceManager;
	}

	/**
	 * 获取应用服务
	 * 
	 * @param name
	 * @return
	 */
	public AppService getAppService(String name) {
		if (mAppServiceManager != null) {
			return mAppServiceManager.getAppService(name);
		}
		return null;
	}

	/**
	 * 添加一个activity到管理列表里
	 * 
	 * @param act
	 */
	public void addActivityToManager(Activity act) {
		for (final WeakReference<Activity> actR : mActivityManager) {
			if (actR != null && !act.equals(actR.get())) {
				mActivityManager.add(new WeakReference<Activity>(act));
			}
		}
	}

	/**
	 * 关闭所有activity
	 */
	public void closeAllActivities() {
		for (final WeakReference<Activity> actR : mActivityManager) {
			if (actR != null && actR.get() != null) {
				actR.get().finish();
			}
		}
	}

	/**
	 * 删除activity从管理列表里
	 * 
	 * @param act
	 */
	public void delActivityFromManager(Activity act) {
		for (final WeakReference<Activity> actR : mActivityManager) {
			if (actR != null && act.equals(actR.get())) {
				mActivityManager.remove(actR);
			}
		}
	}

	/**
	 * 获取所有应用的所有activity
	 * 
	 * @return
	 */
	public List<WeakReference<Activity>> getActivityManager() {
		return mActivityManager;
	}

	/**
	 * 获取session
	 * 
	 * @return
	 */
	public Session getSession() {
		return mSession;
	}

	/**
	 * 退出应用
	 */
	public void exit() {
		mSession.clear();
		if (mAppServiceManager != null) {
			mAppServiceManager.destory();
		}
		// 0 正常推退出
		System.exit(0);
	}

	/**
	 * 设置研发模式
	 * 
	 * @param devMode
	 */
	public void setDevMode(boolean devMode) {
		this.mDevMode = devMode;
	}

	/**
	 * 获取当前是否研发模式 研发模式log级别为VERBOSE，非研发模式log级别为WARN
	 * 
	 * @return
	 */
	public boolean isDevMode() {
		return mDevMode;
	}
}
