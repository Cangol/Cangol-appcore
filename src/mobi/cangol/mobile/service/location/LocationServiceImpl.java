package mobi.cangol.mobile.service.location;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.conf.Config;
import mobi.cangol.mobile.service.conf.ServiceConfig;
import mobi.cangol.mobile.utils.LocationUtils;
import mobi.cangol.mobile.utils.TimeUtils;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
@Service("location")
public class LocationServiceImpl implements LocationService{
	private final static String TAG="LocationService";
	private final static int TIMEOUT=1;
	private final static int BETTER_LOCATION=2;
	private boolean debug=false;
	private int mBetterTime = 1000 * 60 * 2;
	private int mTimeOut = 1000 * 60 * 5;
	private Context mContext = null;
	private Config mConfigService;
	private ServiceConfig mServiceConfig=null;
	private LocationListener mLocationListener;
	private LocationManager mLocationManager;
	private Location mLocation;
	private boolean isRemove;
	private BetterLocationListener mMyLocationListener;
	private String mAddress;
	private volatile ServiceHandler mServiceHandler;
	private volatile Looper mServiceLooper;
	private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what){
        		case TIMEOUT:
        			removeLocationUpdates();
    				if(mMyLocationListener!=null)
    					mMyLocationListener.timeout(mLocation);
        		break;
        		case BETTER_LOCATION:
        			handleBetterLocation();
            	break;
        	}
        }
    }
	@Override
	public void create(Context context) {
		HandlerThread thread = new HandlerThread("LocationService");
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
		CoreApplication app=(CoreApplication) mContext.getApplicationContext();
		mConfigService=(Config) app.getAppService("config");
		mServiceConfig=mConfigService.getServiceConfig("location");
		mBetterTime=mServiceConfig.getInt(Config.LOCATIONSERVICE_BETTERTIME);
		mTimeOut=mServiceConfig.getInt(Config.LOCATIONSERVICE_TIMEOUT);
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		mLocation=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(!isBetterLocation(mLocation)){
			requestLocationUpdates();
		}
	}

	@Override
	public String getName() {
		return "location";
	}

	@Override
	public void destory() {
		removeLocationUpdates();
		mServiceLooper.quit();
	}
	private void handleBetterLocation(){
		removeLocationUpdates();
		if(mMyLocationListener!=null)
			mMyLocationListener.onBetterLocation(mLocation);
		getLocationAddress(mLocation);
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
					mServiceHandler.sendEmptyMessage(BETTER_LOCATION);
				}else{
					Log.d(TAG, "location "+location.toString());
				}
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.d(TAG, "onStatusChanged provider "+provider);
			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.d(TAG, "onProviderEnabled provider "+provider);
			}

			@Override
			public void onProviderDisabled(String provider) {
				Log.d(TAG, "onProviderDisabled provider "+provider);
			}
			
		};
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				mServiceConfig.getInt(Config.LOCATIONSERVICE_GPS_MINTIME),
				mServiceConfig.getInt(Config.LOCATIONSERVICE_GPS_MINDISTANCE),
				mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				mServiceConfig.getInt(Config.LOCATIONSERVICE_NETWORK_MINTIME),
				mServiceConfig.getInt(Config.LOCATIONSERVICE_NETWORK_MINDISTANCE),
				mLocationListener);
		mServiceHandler.sendEmptyMessageDelayed(TIMEOUT, mTimeOut);
	}

	protected void getLocationAddress(Location location) {
		final double lat=location.getLatitude();
		final double lng=location.getLongitude();
		//执行网络请求反查地址（百度地图API|Google地图API）
		mAddress=LocationUtils.getAddressByBaidu(lat, lng, mServiceConfig.getString(Config.LOCATIONSERVICE_BAIDU_AK));
		//LocationUtils.getAddressByGoogle(lat, lng);
		
	}

	@Override
	public void removeLocationUpdates() {
		if(mLocationListener!=null&&!isRemove){
			mLocationManager.removeUpdates(mLocationListener);
			isRemove=true;
		}
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
		return (timeDelta<mBetterTime);
	}
	@Override
	public String getAddress() {
		return mAddress;
	}
	@Override
	public void setBetterLocationListener(BetterLocationListener locationListener) {
		this.mMyLocationListener=locationListener;
		if(mLocation!=null&&!isBetterLocation(mLocation)){
			if(mMyLocationListener!=null)mMyLocationListener.onBetterLocation(mLocation);
		}
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug=debug;
	}


}
