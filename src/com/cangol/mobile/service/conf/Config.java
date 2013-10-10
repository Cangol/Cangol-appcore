package com.cangol.mobile.service.conf;

import com.cangol.mobile.service.AppService;

public interface Config extends AppService{
	
	String getAppDir();
	
	String getCacheDir();
	
	String getImageDir();
	
	String getTempDir();
	
	String getDownloadDir();
	
	String getDatabaseName();
	
}
