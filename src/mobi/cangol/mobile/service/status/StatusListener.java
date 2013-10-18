package mobi.cangol.mobile.service.status;

public interface StatusListener {
	
	void networkConnect();
	
	void networkDissconnect();
	
	void networkTo3G();
	
	void storageRemove();
	
	void storageMount();
	
	void apkInstall(String packageName);
	
	void apkUninstall(String packageName);
}
