/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.location;

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
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
/**
 * @author Cangol
 */
@Service("LocationService")
public class LocationServiceImpl implements LocationService{
	private final static String TAG="LocationService";
	private final static int FLAG_TIMEOUT=1;
	private final static int FLAG_BETTER_LOCATION=2;
	private boolean mDebug=false;
	private int mBetterTime = 1000 * 60 * 2;
	private int mTimeOut = 1000 * 60 * 5;
	private Context mContext = null;
	private ServiceProperty mServiceProperty=null;
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
        		case FLAG_TIMEOUT:
        			removeLocationUpdates();
    				if(mMyLocationListener!=null)
    					mMyLocationListener.timeout(mLocation);
        		break;
        		case FLAG_BETTER_LOCATION:
        			handleBetterLocation();
            	break;
        	}
        }
    }
	@Override
	public void onCreate(Context context) {
		this.mContext=context;
		HandlerThread thread = new HandlerThread("LocationService");
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		mLocation=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}
	public void init(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
		mBetterTime=mServiceProperty.getInt(LOCATIONSERVICE_BETTERTIME);
		mTimeOut=mServiceProperty.getInt(LOCATIONSERVICE_TIMEOUT);
	}
	@Override
	public String getName() {
		return TAG;
	}

	@Override
	public void onDestory() {
		removeLocationUpdates();
		mServiceLooper.quit();
	}

	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}

	@Override
	public ServiceProperty defaultServiceProperty() {
		ServiceProperty sp=new ServiceProperty(TAG);
		sp.putString(LOCATIONSERVICE_BAIDU_AK, "694639beed8fa216ffae5d78d8cd51e0");
		sp.putInt(LOCATIONSERVICE_BETTERTIME, 120000);
		sp.putInt(LOCATIONSERVICE_TIMEOUT, 300000);
		sp.putInt(LOCATIONSERVICE_GPS_MINTIME, 1000);
		sp.putInt(LOCATIONSERVICE_GPS_MINDISTANCE, 50);
		sp.putInt(LOCATIONSERVICE_NETWORK_MINTIME, 1000);
		sp.putInt(LOCATIONSERVICE_NETWORK_MINDISTANCE, 50);
		return sp;
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
					mServiceHandler.sendEmptyMessage(FLAG_BETTER_LOCATION);
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
				mServiceProperty.getInt(LOCATIONSERVICE_GPS_MINTIME),
				mServiceProperty.getInt(LOCATIONSERVICE_GPS_MINDISTANCE),
				mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				mServiceProperty.getInt(LOCATIONSERVICE_NETWORK_MINTIME),
				mServiceProperty.getInt(LOCATIONSERVICE_NETWORK_MINDISTANCE),
				mLocationListener);
		mServiceHandler.sendEmptyMessageDelayed(FLAG_TIMEOUT, mTimeOut);
	}

	private void getLocationAddress(Location location) {
		final double lat=location.getLatitude();
		final double lng=location.getLongitude();
		//执行网络请求反查地址（百度地图API|Google地图API）
		mAddress=LocationUtils.getAddressByBaidu(lat, lng, mServiceProperty.getString(LOCATIONSERVICE_BAIDU_AK));
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
		if(mDebug)Log.d(TAG, "location time :"+TimeUtils.convert(location.getTime()));
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
		this.mDebug=debug;
	}
}
