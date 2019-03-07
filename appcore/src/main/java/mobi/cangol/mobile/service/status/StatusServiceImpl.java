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
package mobi.cangol.mobile.service.status;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.ArrayList;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.utils.DeviceInfo;

/**
 * @author Cangol
 */
@Service("StatusService")
@SuppressLint("MissingPermission")
class StatusServiceImpl implements StatusService {
    private static final String TAG = "StatusService";
    protected ArrayList<StatusListener> mListeners = new ArrayList<>();
    private boolean mDebug = false;
    private Application mContext = null;
    private TelephonyManager mTelephonyManager;
    private boolean mCallingState = true;
    private ServiceProperty mServiceProperty = null;
    private BroadcastReceiver networkStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            State wifiState = null;
            State mobileState = null;
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfoWifi != null) {
                wifiState = networkInfoWifi.getState();
            }

            final NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                mobileState = networkInfo.getState();
            }

            if (wifiState != null && mobileState != null && State.CONNECTED != wifiState
                    && State.CONNECTED == mobileState) {
                // 手机网络连接成功
                if (mDebug) Log.d(TAG, "手机网络连接成功 ");
                if (!mListeners.isEmpty()) {
                    notifyNetworkTo3G(context);
                }
            } else if (wifiState != null && mobileState != null && State.CONNECTED != wifiState
                    && State.CONNECTED != mobileState) {
                // 手机没有任何的网络
                if (mDebug) Log.d(TAG, "手机没有任何的网络,网络中断 ");
                if (!mListeners.isEmpty()) {
                    notifyNetworkDisconnect(context);
                }
            } else if (wifiState != null && State.CONNECTED == wifiState) {
                // 无线网络连接成功
                if (mDebug) Log.d(TAG, " 无线网络连接成功");
                if (!mListeners.isEmpty()) {
                    notifyNetworkConnect(context);
                }
            }

        }

    };
    /**
     * ACTION_MEDIA_MOUNTED 扩展介质被插入，而且已经被挂载。 ACTION_MEDIA_UNMOUNTED
     * 扩展介质存在，但是还没有被挂载 (mount)。
     * <p/>
     * ACTION_MEDIA_EJECT 用户想要移除扩展介质(拔掉扩展卡)。 ACTION_MEDIA_SHARED 扩展介质的挂载被解除
     * (unmount)，因为它已经作为 USB 大容量存储被共享。 ACTION_MEDIA_BAD_REMOVAL 扩展介质(扩展卡)已经从 SD
     * 卡插槽拔出，但是挂载点 (mount point) 还没解除 (unmount)。 ACTION_MEDIA_REMOVED 扩展介质被移除。
     */
    private BroadcastReceiver storageStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Intent.ACTION_MEDIA_EJECT) {
                //do nothings
            } else if (intent.getAction() == Intent.ACTION_MEDIA_SHARED) {
                //do nothings
            } else if (intent.getAction() == Intent.ACTION_MEDIA_BAD_REMOVAL) {
                //do nothings
            } else if (intent.getAction() == Intent.ACTION_MEDIA_REMOVED) {
                notifyStorageRemove(context);
            } else if (intent.getAction() == Intent.ACTION_MEDIA_MOUNTED) {
                notifyStorageMount(context);
            }

        }
    };
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {


        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    // 闲置 挂起
                    if (mDebug) Log.d(TAG, "CALL_STATE_IDLE");
                    mCallingState = false;
                    notifyCallStateIdle();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // 摘机
                    if (mDebug) Log.d(TAG, "CALL_STATE_OFFHOOK");
                    mCallingState = true;
                    notifyCallStateOffhook();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    // 响铃
                    if (mDebug) Log.d(TAG, "CALL_STATE_RINGING");
                    mCallingState = true;
                    notifyCallStateRinging();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onCreate(Application context) {
        mContext = context;

        final IntentFilter intentFileter1 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.registerReceiver(networkStatusReceiver, intentFileter1);

        final IntentFilter intentFileter2 = new IntentFilter("android.intent.action.ACTION_MEDIA_MOUNTED");
        intentFileter2.addAction("android.intent.action.ACTION_MEDIA_REMOVED");
        mContext.registerReceiver(storageStatusReceiver, intentFileter2);

        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        return new ServiceProperty(TAG);
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(networkStatusReceiver);
        mContext.unregisterReceiver(storageStatusReceiver);
        mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void setDebug(boolean mDebug) {
        this.mDebug = mDebug;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public boolean isConnection() {
        return DeviceInfo.isConnection(mContext);
    }

    @Override
    public boolean isWifiConnection() {
        return DeviceInfo.isWifiConnection(mContext);
    }

    @Override
    public boolean isGPSLocation() {
        return DeviceInfo.isGPSLocation(mContext);
    }

    @Override
    public boolean isNetworkLocation() {
        return DeviceInfo.isNetworkLocation(mContext);
    }

    @Override
    public void registerStatusListener(StatusListener statusListener) {
        if (statusListener == null) {
            throw new IllegalArgumentException("The StatusListener is null.");
        }
        synchronized (mListeners) {
            if (mListeners.contains(statusListener)) {
                throw new IllegalStateException("StatusListener " + statusListener + " is already registered.");
            }
            mListeners.add(statusListener);

        }
    }

    @Override
    public void unregisterStatusListener(StatusListener statusListener) {
        if (statusListener == null) {
            throw new IllegalArgumentException("The StatusListener is null.");
        }
        synchronized (mListeners) {
            if (mListeners.contains(statusListener)) {
                mListeners.remove(statusListener);
            } else {
                throw new IllegalStateException("StatusListener " + statusListener + " is not exist.");
            }
        }
    }

    private void notifyNetworkConnect(Context context) {
        for (final StatusListener listener : mListeners) {
            if (listener != null) {
                listener.networkConnect(context);
            }
        }
    }

    private void notifyNetworkDisconnect(Context context) {
        for (final StatusListener listener : mListeners) {
            if (listener != null) {
                listener.networkDisconnect(context);
            } else {
                Log.e("null=" + listener);
            }
        }
    }

    private void notifyNetworkTo3G(Context context) {
        for (final StatusListener listener : mListeners) {
            if (listener != null) {
                listener.networkTo3G(context);
            }
        }
    }

    private void notifyStorageRemove(Context context) {
        for (final StatusListener listener : mListeners) {
            if (listener != null) {
                listener.storageRemove(context);
            }
        }
    }

    private void notifyStorageMount(Context context) {
        for (final StatusListener listener : mListeners) {
            if (listener != null) {
                listener.storageMount(context);
            }
        }
    }

    private void notifyCallStateIdle() {
        for (final StatusListener listener : mListeners) {
            if (listener != null) {
                listener.callStateIdle();
            }
        }
    }

    private void notifyCallStateOffhook() {
        for (final StatusListener listener : mListeners) {
            if (listener != null) {
                listener.callStateOffhook();
            }
        }
    }

    private void notifyCallStateRinging() {
        for (final StatusListener listener : mListeners) {
            if (listener != null) {
                listener.callStateRinging();
            }
        }
    }

    @Override
    public boolean isCallingState() {
        return mCallingState;
    }
}
