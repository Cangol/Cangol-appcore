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
package mobi.cangol.mobile.service.status

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo.State
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import mobi.cangol.mobile.utils.DeviceInfo
import java.util.*

/**
 * @author Cangol
 */
@Service("StatusService")
@SuppressLint("MissingPermission")
internal class StatusServiceImpl : StatusService {
    private var mListeners = ArrayList<StatusListener>()
    private var mDebug = false
    private var mContext: Application? = null
    private var mTelephonyManager: TelephonyManager? = null
    private var mCallingState = true
    private var mServiceProperty: ServiceProperty? = null
    private val networkStatusReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            var wifiState: State? = null
            var mobileState: State? = null
            val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (networkInfoWifi != null) {
                wifiState = networkInfoWifi.state
            }

            val networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (networkInfo != null) {
                mobileState = networkInfo.state
            }

            if (wifiState != null && mobileState != null && State.CONNECTED != wifiState
                    && State.CONNECTED == mobileState) {
                // 手机网络连接成功
                if (mDebug) Log.d(TAG, "手机网络连接成功 ")
                if (!mListeners.isEmpty()) {
                    notifyNetworkTo3G(context)
                }
            } else if (wifiState != null && mobileState != null && State.CONNECTED != wifiState
                    && State.CONNECTED != mobileState) {
                // 手机没有任何的网络
                if (mDebug) Log.d(TAG, "手机没有任何的网络,网络中断 ")
                if (!mListeners.isEmpty()) {
                    notifyNetworkDisconnect(context)
                }
            } else if (wifiState != null && State.CONNECTED == wifiState) {
                // 无线网络连接成功
                if (mDebug) Log.d(TAG, " 无线网络连接成功")
                if (!mListeners.isEmpty()) {
                    notifyNetworkConnect(context)
                }
            }

        }

    }
    /**
     * ACTION_MEDIA_MOUNTED 扩展介质被插入，而且已经被挂载。 ACTION_MEDIA_UNMOUNTED
     * 扩展介质存在，但是还没有被挂载 (mount)。
     *
     *
     * ACTION_MEDIA_EJECT 用户想要移除扩展介质(拔掉扩展卡)。 ACTION_MEDIA_SHARED 扩展介质的挂载被解除
     * (unmount)，因为它已经作为 USB 大容量存储被共享。 ACTION_MEDIA_BAD_REMOVAL 扩展介质(扩展卡)已经从 SD
     * 卡插槽拔出，但是挂载点 (mount point) 还没解除 (unmount)。 ACTION_MEDIA_REMOVED 扩展介质被移除。
     */
    private val storageStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action === Intent.ACTION_MEDIA_EJECT) {
                //do nothings
            } else if (intent.action === Intent.ACTION_MEDIA_SHARED) {
                //do nothings
            } else if (intent.action === Intent.ACTION_MEDIA_BAD_REMOVAL) {
                //do nothings
            } else if (intent.action === Intent.ACTION_MEDIA_REMOVED) {
                notifyStorageRemove(context)
            } else if (intent.action === Intent.ACTION_MEDIA_MOUNTED) {
                notifyStorageMount(context)
            }

        }
    }
    private val phoneStateListener = object : PhoneStateListener() {


        override fun onCallStateChanged(state: Int, incomingNumber: String) {

            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                    // 闲置 挂起
                    if (mDebug) Log.d(TAG, "CALL_STATE_IDLE")
                    mCallingState = false
                    notifyCallStateIdle()
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    // 摘机
                    if (mDebug) Log.d(TAG, "CALL_STATE_OFFHOOK")
                    mCallingState = true
                    notifyCallStateOffhook()
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    // 响铃
                    if (mDebug) Log.d(TAG, "CALL_STATE_RINGING")
                    mCallingState = true
                    notifyCallStateRinging()
                }
                else -> {
                }
            }
        }

    }

    override fun onCreate(context: Application) {
        mContext = context

        val intentFileter1 = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        mContext!!.registerReceiver(networkStatusReceiver, intentFileter1)

        val intentFileter2 = IntentFilter("android.intent.action.ACTION_MEDIA_MOUNTED")
        intentFileter2.addAction("android.intent.action.ACTION_MEDIA_REMOVED")
        mContext!!.registerReceiver(storageStatusReceiver, intentFileter2)

        mTelephonyManager = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        mTelephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun init(serviceProperty: ServiceProperty) {
        this.mServiceProperty = serviceProperty
    }

    override fun defaultServiceProperty(): ServiceProperty {
        return ServiceProperty(TAG)
    }

    override fun onDestroy() {
        mContext!!.unregisterReceiver(networkStatusReceiver)
        mContext!!.unregisterReceiver(storageStatusReceiver)
        mTelephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }

    override fun getName(): String {
        return TAG
    }

    override fun setDebug(mDebug: Boolean) {
        this.mDebug = mDebug
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty!!
    }

    override fun isConnection(): Boolean {
        return DeviceInfo.isConnection(mContext!!)
    }

    override fun isWifiConnection(): Boolean {
        return DeviceInfo.isWifiConnection(mContext!!)
    }

    override fun isGPSLocation(): Boolean {
        return DeviceInfo.isGPSLocation(mContext!!)
    }

    override fun isNetworkLocation(): Boolean {
        return DeviceInfo.isNetworkLocation(mContext!!)
    }

    override fun registerStatusListener(statusListener: StatusListener) {
        requireNotNull(statusListener) { "The StatusListener is null." }
        synchronized(mListeners) {
            check(!mListeners.contains(statusListener)) { "StatusListener $statusListener is already registered." }
            mListeners.add(statusListener)

        }
    }

    override fun unregisterStatusListener(statusListener: StatusListener) {
        requireNotNull(statusListener) { "The StatusListener is null." }
        synchronized(mListeners) {
            if (mListeners.contains(statusListener)) {
                mListeners.remove(statusListener)
            } else {
                throw IllegalStateException("StatusListener $statusListener is not exist.")
            }
        }
    }

    private fun notifyNetworkConnect(context: Context) {
        for (listener in mListeners) {
            listener?.networkConnect(context)
        }
    }

    private fun notifyNetworkDisconnect(context: Context) {
        for (listener in mListeners) {
            if (listener != null) {
                listener.networkDisconnect(context)
            } else {
                Log.e("null=$listener")
            }
        }
    }

    private fun notifyNetworkTo3G(context: Context) {
        for (listener in mListeners) {
            listener?.networkTo3G(context)
        }
    }

    private fun notifyStorageRemove(context: Context) {
        for (listener in mListeners) {
            listener?.storageRemove(context)
        }
    }

    private fun notifyStorageMount(context: Context) {
        for (listener in mListeners) {
            listener?.storageMount(context)
        }
    }

    private fun notifyCallStateIdle() {
        for (listener in mListeners) {
            listener?.callStateIdle()
        }
    }

    private fun notifyCallStateOffhook() {
        for (listener in mListeners) {
            listener?.callStateOffhook()
        }
    }

    private fun notifyCallStateRinging() {
        for (listener in mListeners) {
            listener?.callStateRinging()
        }
    }

    override fun isCallingState(): Boolean {
        return mCallingState
    }

    companion object {
        private const val TAG = "StatusService"
    }
}
