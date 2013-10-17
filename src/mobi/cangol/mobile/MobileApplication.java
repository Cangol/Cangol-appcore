package mobi.cangol.mobile;

import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.AppServiceManager;
import mobi.cangol.mobile.service.AppServiceManagerImpl;
import android.app.Application;

public class MobileApplication extends Application {
	private AppServiceManager serviceManager;
	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}
	private void init() {
		serviceManager=new AppServiceManagerImpl(this);
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
