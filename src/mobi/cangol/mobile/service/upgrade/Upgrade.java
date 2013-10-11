package mobi.cangol.mobile.service.upgrade;

import mobi.cangol.mobile.service.AppService;


public interface Upgrade extends AppService{
	
	boolean isUpgrade(String version);
	
	void getUpgrade(String version);
	
	void downloadUpgradeApk(String url,String savePath);
}
