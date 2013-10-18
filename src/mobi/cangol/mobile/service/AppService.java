package mobi.cangol.mobile.service;

import android.content.Context;


public interface AppService {
	
	void init();	
	
	void setContext(Context context);

	String getName();
	
	void destory();	
}