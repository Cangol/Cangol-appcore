package mobi.cangol.mobile.service.conf;

import mobi.cangol.mobile.service.AppService;

public interface Config extends AppService{
	
	String getAppDir();
	
	String getCacheDir();
	
	String getImageDir();
	
	String getTempDir();
	
	String getDownloadDir();
	
	String getDatabaseName();
	
	String getSharedName();
}
