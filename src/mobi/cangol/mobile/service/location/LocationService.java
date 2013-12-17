package mobi.cangol.mobile.service.location;

import mobi.cangol.mobile.service.AppService;
import android.location.Location;

public interface LocationService extends AppService{
	public final static String LOCATIONSERVICE_BETTERTIME="better_time";
	public final static String LOCATIONSERVICE_TIMEOUT="timeout";
	public final static String LOCATIONSERVICE_BAIDU_AK="baidu_ak";
	public final static String LOCATIONSERVICE_GPS_MINTIME="gps_min_time";
	public final static String LOCATIONSERVICE_GPS_MINDISTANCE="gps_min_distance";
	public final static String LOCATIONSERVICE_NETWORK_MINTIME="network_min_time";
	public final static String LOCATIONSERVICE_NETWORK_MINDISTANCE="network_min_distance";
	
	void requestLocationUpdates();
	
	void removeLocationUpdates();
	
	Location getLastKnownLocation();
	
	boolean isBetterLocation(Location mLocation);

	void setBetterLocationListener(BetterLocationListener locationListener);
	
	String getAddress();
}

