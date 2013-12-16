package mobi.cangol.mobile.service.status;

import mobi.cangol.mobile.service.AppService;

public interface StatusService extends AppService {
	
	boolean isConnection();

	boolean isWifiConnection();

	boolean isGPSLocation();
	
	boolean isNetworkLocation();
	
	boolean isCallingState();
	
	void registerStatusListener(StatusListener statusListener);
	
	void unregisterStatusListener(StatusListener statusListener);
}
