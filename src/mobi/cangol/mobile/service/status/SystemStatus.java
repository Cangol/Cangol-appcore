package mobi.cangol.mobile.service.status;

import mobi.cangol.mobile.service.AppService;

public interface SystemStatus extends AppService {
	
	boolean isConnection();

	boolean isWifiConnection();

	boolean isGPSLocation();
	
	boolean isNetworkLocation();
	
	void setStatusListner(StatusListener statusListener);
}
