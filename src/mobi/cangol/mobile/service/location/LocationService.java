package mobi.cangol.mobile.service.location;

import mobi.cangol.mobile.service.AppService;
import android.location.Location;

public interface LocationService extends AppService{
	
	void requestLocationUpdates();
	
	void removeLocationUpdates();
	
	Location getLastKnownLocation();
	
	boolean isBetterLocation(Location mLocation);
}

