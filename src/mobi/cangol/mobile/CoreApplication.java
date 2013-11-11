package mobi.cangol.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.AppServiceManager;
import mobi.cangol.mobile.service.AppServiceManagerImpl;
import mobi.cangol.mobile.service.conf.Config;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.StrictMode;

public class CoreApplication extends Application {
	
	private AppServiceManager serviceManager;
	public Map<String,Object> session=new HashMap<String,Object>();
	private boolean devMode=true;
	@Override
	public void onCreate() {
		if(devMode){
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
	        .detectDiskReads()  
	        .detectDiskWrites()  
	        .detectNetwork()  
	        .penaltyLog() 
	        .penaltyDialog()
	        .build()); 
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	        .detectLeakedSqlLiteObjects()
	        .detectLeakedClosableObjects()
	        .penaltyLog()
	        .penaltyDeath()
	        .build());
		}
		super.onCreate();
		init();
	}
	private void init() {
		serviceManager=new AppServiceManagerImpl(this);
		Config config=(Config) serviceManager.getAppService("config");
		try{
			int id=this.getResources().getIdentifier("config", "raw", this.getPackageName());
			config.setConfigSource(this.getResources().openRawResource(id));
		}catch(NotFoundException e){
			Log.d("Application","not found config.xml");
		}
		Log.d("Application",""+config.getAppDir());
	}	
	
	public AppService getAppService(String name){
		if(serviceManager!=null){
			return serviceManager.getAppService(name);
		}
		return null;
	}
	
	public void exit() {
		if(serviceManager!=null){
			serviceManager.destoryAllService();
		}
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
