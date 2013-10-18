package mobi.cangol.mobile.service.location;

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.utils.TimeUtils;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
@Service("location")
public class LocationServiceImpl implements LocationService{
	private final static String TAG="LocationService";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private Context mContext = null;
	private LocationListener mLocationListener;
	private LocationManager mLocationManager;
	private Location mLocation;
	private boolean isRemove;
	
	@Override
	public void init() {
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		mLocation=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(!isBetterLocation(mLocation)){
			requestLocationUpdates();
		}
	}
	
	@Override
	public void setContext(Context context) {
		mContext=context;
	}

	@Override
	public String getName() {
		return "location";
	}

	@Override
	public void destory() {
		removeLocationUpdates();
	}

	@Override
	public void requestLocationUpdates() {
		if(null!=mLocationListener)return;
		mLocationListener=new LocationListener(){

			@Override
			public void onLocationChanged(Location location) {
				Log.d(TAG, "location "+location.getProvider()+":"+location.getLatitude()+","+location.getLongitude());
				if(isBetterLocation(location)){
					mLocation=location;
					removeLocationUpdates();
					isRemove=true;
				}
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.d(TAG, "onStatusChanged provider"+provider);
			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.d(TAG, "onProviderEnabled provider"+provider);
			}

			@Override
			public void onProviderDisabled(String provider) {
				Log.d(TAG, "onProviderDisabled provider"+provider);
			}
			
		};
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50,mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 50,mLocationListener);
	}

	@Override
	public void removeLocationUpdates() {
		if(mLocationListener!=null&&!isRemove)
			mLocationManager.removeUpdates(mLocationListener);
		mLocationListener=null;
	}

	@Override
	public Location getLastKnownLocation() {
		return mLocation;
	}

	@Override
	public boolean isBetterLocation(Location location) {
		if(null==location)return false;
		long timeDelta=System.currentTimeMillis()-location.getTime();
		Log.d(TAG, "location time :"+TimeUtils.convert(location.getTime()));
		return (timeDelta<TWO_MINUTES);
	}

}
