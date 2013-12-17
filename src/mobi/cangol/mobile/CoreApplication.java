package mobi.cangol.mobile;

import java.util.List;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.AppServiceManager;
import mobi.cangol.mobile.service.AppServiceManagerImpl;
import mobi.cangol.mobile.service.conf.ConfigService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.StrictMode;

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
