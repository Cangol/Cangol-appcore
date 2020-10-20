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
package mobi.cangol.mobile.service.analytics;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mobi.cangol.mobile.core.BuildConfig;
import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.AsyncHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.utils.DeviceInfo;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * @author Cangol
 */
@Service("AnalyticsService")
class AnalyticsServiceImpl extends ITrackerHandler implements AnalyticsService {
    private static final String TAG = "AnalyticsService";
    private boolean mDebug = false;
    private Application mContext = null;
    private AsyncHttpClient mAsyncHttpClient = null;
    private ServiceProperty mServiceProperty = null;
    private Map<String, ITracker> mTrackers = new HashMap<>();
    private Map<String, String> commonParams;
    private Map<String, String> deviceParams;
    @Override
    public void onCreate(Application context) {
        mContext = context;
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
        PoolManager.buildPool(mServiceProperty.getString(ANALYTICSSERVICE_THREADPOOL_NAME), mServiceProperty.getInt(ANALYTICSSERVICE_THREAD_MAX));
        mAsyncHttpClient = AsyncHttpClient.build(mServiceProperty.getString(ANALYTICSSERVICE_THREADPOOL_NAME));
        try {
            commonParams = initCommonParams();
            deviceParams = initDeviceParams();
        }catch (Exception e){
            Log.w(TAG, ":" + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        mAsyncHttpClient.cancelRequests(mContext, true);
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        final ServiceProperty sp = new ServiceProperty(TAG);
        sp.putString(ANALYTICSSERVICE_THREADPOOL_NAME, TAG);
        sp.putInt(ANALYTICSSERVICE_THREAD_MAX, 2);
        return sp;
    }

    @Override
    public void send(final ITracker iTracker, final String url,final Map<String, String> params) {
        final HashMap<String, String> headers=new HashMap<>();
        headers.put("device",new JSONObject(deviceParams).toString());
        headers.putAll(commonParams);

        mAsyncHttpClient.post(mContext, url, headers, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                if (mDebug) {
                    final RequestParams requestParams = new RequestParams(params);
                    final StringBuilder result = new StringBuilder();
                    for(final Map.Entry<String, String> entry : headers.entrySet()) {
                        result.append('\t').append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
                    }
                    Log.v(TAG, "url: " + url + "?" + requestParams.toString()
                            + "\nheader: \n" + result.toString()
                            + "params: \n" + requestParams.toDebugString());
                }
            }

            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                if (mDebug) Log.d(TAG, iTracker.getTrackingId() + " send Success :" + content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (mDebug) Log.d(TAG, iTracker.getTrackingId() + " send Failure :" + content);
            }

        });
    }

    @Override
    public void setDebug(boolean debug) {
        this.mDebug = debug;
    }

    @Override
    public ITracker getTracker(String trackingId) {
        if (mTrackers.containsKey(trackingId)) {
            return mTrackers.get(trackingId);
        } else {
           final ITracker tracker = new ITracker(trackingId, this);
            mTrackers.put(trackingId, tracker);
            return tracker;
        }
    }

    @Override
    public void closeTracker(String trackingId) {
        final ITracker tracker = mTrackers.get(trackingId);
        if (tracker != null) {
            mTrackers.remove(trackingId);
            tracker.setClosed(true);
        }
    }

    /**
     * 公共参数
     * osVersion 操作系统版本号 4.2.2
     * deviceId 设备唯一ID Android|IOS均为open-uuid
     * platform 平台 平台控制使用IOS|Android
     * channelId 渠道 渠道控制使用(Google|baidu|91|appstore…)
     * appId AppID
     * appVersion App版本号 1.1.0
     * sdkVersion 统计SDK版本号
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    protected Map<String, String> initCommonParams() {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        Map<String, String> params = new HashMap<>();
        params.put("osVersion", DeviceInfo.getOSVersion());
        params.put("deviceId", getDeviceId(mContext));
        params.put("platform", DeviceInfo.getOS());
        params.put("channelId", getChannelID(mContext));
        params.put("appId", getAppID(mContext));
        params.put("appVersion", DeviceInfo.getAppVersion(mContext));
        params.put("sdkVersion", BuildConfig.VERSION_NAME);
        params.put("timestamp", TimeUtils.getCurrentTime());
        StrictMode.setThreadPolicy(oldPolicy);
        return params;
    }
    @Override
    public Map<String, String> getCommonParams() {
        return commonParams;
    }
    /**
     * 设备参数
     * os API版本号 版本控制使用
     * osVersion 操作系统版本号 4.2.2
     * model 设备类型 Note2
     * brand 设备制造商 Samsung
     * carrier 设备制造商
     * screenSize 屏幕物理尺寸
     * density density
     * densityDpi DPI
     * resolution 设备分辨率 800*480
     * locale locale
     * language 设备语言 Zh
     * country 设备国家 Cn
     * charset 设备字符集 (utf-8|gbk...)
     * ip 设备网络地址 (8.8.8.8)
     * mac mac地址
     * cpuInfo cpuInfo
     * cpuAbi  cpuAbi
     * mem 内存大小
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public Map<String, String> initDeviceParams() {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        Map<String, String> params = new HashMap<>();
        params.put("os", DeviceInfo.getOS());
        params.put("osVersion", DeviceInfo.getOSVersion());
        params.put("model", DeviceInfo.getDeviceModel());
        params.put("brand", DeviceInfo.getDeviceBrand());
        params.put("carrier", DeviceInfo.getNetworkOperator(mContext));
        params.put("screenSize", DeviceInfo.getScreenSize(mContext));
        params.put("density", "" + DeviceInfo.getDensity(mContext));
        params.put("densityDpi", DeviceInfo.getDensityDpiStr(mContext));
        params.put("resolution", DeviceInfo.getResolution(mContext));
        params.put("locale", DeviceInfo.getLocale());
        params.put("country", DeviceInfo.getCountry());
        params.put("language", DeviceInfo.getLanguage());
        params.put("charset", DeviceInfo.getCharset());
        params.put("ip", DeviceInfo.getIpStr(mContext));
        params.put("mac", DeviceInfo.getMacAddress(mContext));
        params.put("cpuABI", DeviceInfo.getCPUABI());
        params.put("cpuInfo", DeviceInfo.getCPUInfo());
        params.put("memSize", ""+DeviceInfo.getMemTotalSize());
        StrictMode.setThreadPolicy(oldPolicy);
        return params;
    }

    @Override
    public Map<String, String> getDeviceParams() {
        return deviceParams;
    }
    private String getDeviceId(Context context) {
        return DeviceInfo.getDeviceId(context);
    }

    private String getChannelID(Context context) {
        String channelId = DeviceInfo.getAppStringMetaData(context, "CHANNEL_ID");
        if (TextUtils.isEmpty(channelId)) {
            channelId = "UNKNOWN";
        }
        return channelId;
    }

    private String getAppID(Context context) {
        String appId = DeviceInfo.getAppStringMetaData(context, "APP_ID");
        if (TextUtils.isEmpty(appId)) {
            appId = context.getPackageName();
        }
        return appId;
    }
}
