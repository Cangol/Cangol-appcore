package mobi.cangol.mobile.service;

import android.content.Context;


public interface AppService {
	
	void create(Context context);

	String getName();
	
	void destory();	
	
	void setDebug(boolean debug);
}