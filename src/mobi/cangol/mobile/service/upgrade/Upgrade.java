package mobi.cangol.mobile.service.upgrade;

import mobi.cangol.mobile.service.AppService;


public interface Upgrade extends AppService{
	
	enum UpgradeType{
		APK,//apk升级
		DEX,//dex升级
		RES,//res升级
		SO//so库升级
	}
	
	boolean isUpgrade(String version);
	
	String getUpgrade(String version);
	
	void downloadUpgrade(String url,String savePath);
}
