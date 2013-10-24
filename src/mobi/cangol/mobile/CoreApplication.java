package mobi.cangol.mobile;

import java.util.HashMap;
import java.util.Map;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.AppServiceManager;
import mobi.cangol.mobile.service.AppServiceManagerImpl;
import mobi.cangol.mobile.service.conf.Config;
import android.app.Application;
import android.content.res.Resources.NotFoundException;

public class CoreApplication extends Application {
	
	private AppServiceManager serviceManager;
	public Map<String,Object> session=new HashMap<String,Object>();
	@Override
	public void onCreate() {
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
}
