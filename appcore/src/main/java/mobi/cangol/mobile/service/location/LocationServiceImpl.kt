/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.content.PermissionChecker
import android.util.Log
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import mobi.cangol.mobile.utils.TimeUtils
import java.util.*

/**
 * @author Cangol
 */
@Service("LocationService")
@SuppressLint("MissingPermission")
internal class LocationServiceImpl : LocationService {
    private var mDebug = false
    private var mBetterTime = 1000 * 60 * 2
    private var mTimeOut = 1000 * 60 * 5
    private var mServiceProperty = ServiceProperty(TAG)
    private var mLocationListener: LocationListener? = null
    private var mLocationManager: LocationManager? = null
    private var mLocation: Location? = null
    private var mIsRemove: Boolean = false
    private var mBetterLocationListener: BetterLocationListener? = null
    @Volatile
    private var mServiceHandler: ServiceHandler? = null

    override fun onCreate(context: Application) {
        mServiceHandler = ServiceHandler(Looper.getMainLooper())
        mLocationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocation = mLocationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }

    override fun init(serviceProperty: ServiceProperty) {
        Log.d(TAG, "init $serviceProperty")
        this.mServiceProperty = serviceProperty
        mBetterTime = mServiceProperty.getInt(LocationService.LOCATIONSERVICE_BETTERTIME)
        mTimeOut = mServiceProperty.getInt(LocationService.LOCATIONSERVICE_TIMEOUT)
    }

    override fun getName(): String {
        return TAG
    }

    override fun onDestroy() {
        removeLocationUpdates()
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun defaultServiceProperty(): ServiceProperty {
        val sp = ServiceProperty(TAG)
        sp.putInt(LocationService.LOCATIONSERVICE_BETTERTIME, 120000)
        sp.putInt(LocationService.LOCATIONSERVICE_TIMEOUT, 300000)
        sp.putInt(LocationService.LOCATIONSERVICE_GPS_MINTIME, 1000)
        sp.putInt(LocationService.LOCATIONSERVICE_GPS_MINDISTANCE, 50)
        sp.putInt(LocationService.LOCATIONSERVICE_NETWORK_MINTIME, 1000)
        sp.putInt(LocationService.LOCATIONSERVICE_NETWORK_MINDISTANCE, 50)
        return sp
    }

    private fun handleBetterLocation() {
        removeLocationUpdates()
        if (mBetterLocationListener != null) {
            mBetterLocationListener!!.onBetterLocation(mLocation!!)
        }
    }

    private fun checkLocationPermission(activity: Activity): Boolean {
        val list = ArrayList<String>()
        if (PermissionChecker.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            list.add(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Log.e(TAG, "requestLocation need Permission " + Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (PermissionChecker.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            Log.e(TAG, "requestLocation  NETWORK_PROVIDER is disabled ")
        }
        if (list.isNotEmpty()) {
            Log.e(TAG, "requestLocation need Permission $list")
            val permissions = arrayOfNulls<String>(list.size)
            list.toTypedArray()
            mBetterLocationListener!!.needPermission(permissions)
            return false
        }
        return true
    }

    override fun requestLocationUpdates(activity: Activity) {

        if (!checkLocationPermission(activity)) {
            return
        }

        var gpsProvider = false
        if (mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsProvider = true
        } else {
            Log.e(TAG, "requestLocation  GPS_PROVIDER is disabled ")
            mBetterLocationListener!!.providerDisabled(LocationManager.GPS_PROVIDER)
        }

        var networkProvider = false
        if (mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            networkProvider = true
        } else {
            Log.e(TAG, "requestLocation  NETWORK_PROVIDER is disabled ")
            mBetterLocationListener!!.providerDisabled(LocationManager.NETWORK_PROVIDER)
        }

        if (networkProvider || gpsProvider) {
            if (null != mLocationListener) {
                return
            }
            mLocationListener = object : LocationListener {

                override fun onLocationChanged(location: Location) {
                    Log.d(TAG, "location " + location.provider + ":" + location.latitude + "," + location.longitude)
                    if (isBetterLocation(location)) {
                        mLocation = location
                        mServiceHandler!!.sendEmptyMessage(FLAG_BETTER_LOCATION)
                    } else {
                        Log.d(TAG, "location $location")
                    }
                }

                override fun onStatusChanged(provider: String, status: Int,
                                             extras: Bundle) {
                    Log.d(TAG, "onStatusChanged provider $provider")
                }

                override fun onProviderEnabled(provider: String) {
                    Log.d(TAG, "onProviderEnabled provider $provider")
                }

                override fun onProviderDisabled(provider: String) {
                    Log.d(TAG, "onProviderDisabled provider $provider")
                }

            }
            if (gpsProvider) {
                mLocationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        mServiceProperty.getInt(LocationService.LOCATIONSERVICE_GPS_MINTIME).toLong(),
                        mServiceProperty.getInt(LocationService.LOCATIONSERVICE_GPS_MINDISTANCE).toFloat(),
                        mLocationListener)
            }
            if (networkProvider) {
                mLocationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        mServiceProperty.getInt(LocationService.LOCATIONSERVICE_NETWORK_MINTIME).toLong(),
                        mServiceProperty.getInt(LocationService.LOCATIONSERVICE_NETWORK_MINDISTANCE).toFloat(),
                        mLocationListener)
            }
            mServiceHandler!!.sendEmptyMessageDelayed(FLAG_TIMEOUT, mTimeOut.toLong())
            mBetterLocationListener!!.positioning()
        }
    }

    override fun removeLocationUpdates() {
        if (mLocationListener != null && !mIsRemove) {
            mLocationManager!!.removeUpdates(mLocationListener)
            mIsRemove = true
        }
        mLocationListener = null
    }

    override fun getLastKnownLocation(): Location? {
        return mLocation
    }

    override fun isBetterLocation(location: Location): Boolean {
        val timeDelta = System.currentTimeMillis() - location.time
        Log.d(TAG, "location time :" + TimeUtils.formatYmdHms(location.time))
        return timeDelta < mBetterTime
    }

    override fun setBetterLocationListener(locationListener: BetterLocationListener) {
        this.mBetterLocationListener = locationListener
        if (mLocation != null && !isBetterLocation(mLocation!!)) {
            if (mBetterLocationListener != null) {
                mBetterLocationListener!!.onBetterLocation(mLocation!!)
            } else {
                //
            }
        }
    }

    override fun setDebug(mDebug: Boolean) {
        this.mDebug = mDebug
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                FLAG_TIMEOUT -> {
                    removeLocationUpdates()
                    if (mBetterLocationListener != null) {
                        mBetterLocationListener!!.timeout(mLocation!!)
                    }
                }
                FLAG_BETTER_LOCATION -> handleBetterLocation()
                else -> {
                }
            }
        }
    }

    companion object {
        private const val TAG = "LocationService"
        private const val FLAG_TIMEOUT = 1
        private const val FLAG_BETTER_LOCATION = 2
    }
}
