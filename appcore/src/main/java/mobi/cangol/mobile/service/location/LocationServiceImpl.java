/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.location;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * @author Cangol
 */
@Service("LocationService")
class LocationServiceImpl implements LocationService {
    private static final String TAG = "LocationService";
    private static final int FLAG_TIMEOUT = 1;
    private static final int FLAG_BETTER_LOCATION = 2;
    private boolean mDebug = false;
    private int mBetterTime = 1000 * 60 * 2;
    private int mTimeOut = 1000 * 60 * 5;
    private Application mContext = null;
    private ServiceProperty mServiceProperty = null;
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private Location mLocation;
    private boolean isRemove;
    private BetterLocationListener mMyLocationListener;
    private volatile ServiceHandler mServiceHandler;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Application context) {
        this.mContext = context;
        mServiceHandler = new ServiceHandler(Looper.getMainLooper());
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
        mBetterTime = mServiceProperty.getInt(LOCATIONSERVICE_BETTERTIME);
        mTimeOut = mServiceProperty.getInt(LOCATIONSERVICE_TIMEOUT);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        removeLocationUpdates();
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        ServiceProperty sp = new ServiceProperty(TAG);
        sp.putInt(LOCATIONSERVICE_BETTERTIME, 120000);
        sp.putInt(LOCATIONSERVICE_TIMEOUT, 300000);
        sp.putInt(LOCATIONSERVICE_GPS_MINTIME, 1000);
        sp.putInt(LOCATIONSERVICE_GPS_MINDISTANCE, 50);
        sp.putInt(LOCATIONSERVICE_NETWORK_MINTIME, 1000);
        sp.putInt(LOCATIONSERVICE_NETWORK_MINDISTANCE, 50);
        return sp;
    }

    private void handleBetterLocation() {
        removeLocationUpdates();
        if (mMyLocationListener != null) {
            mMyLocationListener.onBetterLocation(mLocation);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestLocationUpdates() {
        if (null != mLocationListener) {
            return;
        }

        mLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "location " + location.getProvider() + ":" + location.getLatitude() + "," + location.getLongitude());
                if (isBetterLocation(location)) {
                    mLocation = location;
                    mServiceHandler.sendEmptyMessage(FLAG_BETTER_LOCATION);
                } else {
                    Log.d(TAG, "location " + location.toString());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.d(TAG, "onStatusChanged provider " + provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled provider " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled provider " + provider);
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

    @SuppressLint("MissingPermission")
    @Override
    public void removeLocationUpdates() {
        if (mLocationListener != null && !isRemove) {
            mLocationManager.removeUpdates(mLocationListener);
            isRemove = true;
        }
        mLocationListener = null;
    }

    @Override
    public Location getLastKnownLocation() {
        return mLocation;
    }

    @Override
    public boolean isBetterLocation(Location location) {
        if (null == location) {
            return false;
        }
        final long timeDelta = System.currentTimeMillis() - location.getTime();
        Log.d(TAG, "location time :" + TimeUtils.formatYmdHms(location.getTime()));
        return (timeDelta < mBetterTime);
    }

    @Override
    public void setBetterLocationListener(BetterLocationListener locationListener) {
        this.mMyLocationListener = locationListener;
        if (mLocation != null && !isBetterLocation(mLocation)) {
            if (mMyLocationListener != null) {
                mMyLocationListener.onBetterLocation(mLocation);
            } else {
                //
            }
        }
    }

    @Override
    public void setDebug(boolean mDebug) {
        this.mDebug = mDebug;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FLAG_TIMEOUT:
                    removeLocationUpdates();
                    if (mMyLocationListener != null) {
                        mMyLocationListener.timeout(mLocation);
                    }
                    break;
                case FLAG_BETTER_LOCATION:
                    handleBetterLocation();
                    break;
                default:
                    break;
            }
        }
    }
}
