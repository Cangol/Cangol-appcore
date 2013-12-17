package mobi.cangol.mobile.service;

import android.content.Context;


public interface AppService {
	
	void onCreate(Context context);

	String getName();
	
	void onDestory();	
	
	void setDebug(boolean debug);
	
	void setServiceProperty(ServiceProperty serviceProperty);
	
	ServiceProperty getServiceProperty();
}