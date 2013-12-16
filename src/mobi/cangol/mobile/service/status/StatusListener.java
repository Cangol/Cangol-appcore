package mobi.cangol.mobile.service.status;

import android.content.Context;

public interface StatusListener {
	
void networkConnect(Context context);
	
	void networkDisconnect(Context context);
	
	void networkTo3G(Context context);
	
	void storageRemove(Context context);
	
	void storageMount(Context context);
	
	void callStateIdle();
	
	void callStateOffhook();
	
	void callStateRinging();
}
