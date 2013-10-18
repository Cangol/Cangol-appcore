package mobi.cangol.mobile.service.status;

import mobi.cangol.mobile.service.AppService;

public interface SystemStatus extends AppService {
	
	boolean isConnection();

	boolean isWifiConnection();
	
	void setStatusListner(StatusListener statusListener);
}
