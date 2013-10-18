package mobi.cangol.mobile.service.status;

import android.content.Context;

public interface StatusListener {
	
	void networkConnect(Context context);
	
	void networkDisconnect(Context context);
	
	void networkTo3G(Context context);
	
	void storageRemove(Context context);
	
	void storageMount(Context context);
	
	void apkInstall(Context context,String packageName);
	
	void apkUninstall(Context context,String packageName);
}
