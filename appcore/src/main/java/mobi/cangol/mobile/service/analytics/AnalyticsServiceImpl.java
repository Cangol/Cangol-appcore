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
import mobi.cangol.mobile.parser.JsonUtils;
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
    private final static String TAG = "AnalyticsService";
    private boolean mDebug = false;
    private Application mContext = null;
    private AsyncHttpClient mAsyncHttpClient = null;
    private ServiceProperty mServiceProperty = null;
    private Map<String, ITracker> mTrackers = new HashMap<String, ITracker>();
    private HashMap<String, String> commonParams;
    private HashMap<String, String> deviceParams;
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
    public void send(final ITracker iTracker, String url,Map<String, String> params) {
        RequestParams requestParams = new RequestParams(params);
        if (mDebug) Log.v(TAG, "send " + url+"?"+requestParams.toString());
        if (mDebug) Log.v(TAG, "params: \n" + requestParams.toDebugString());

        HashMap<String, String> maps=new HashMap<>();
        maps.putAll(commonParams);
        HashMap<String, String> headers=new HashMap<>();
        headers.put("device",new JSONObject(maps).toString());
        mAsyncHttpClient.get(mContext, url, headers, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
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
    protected HashMap<String, String> initCommonParams() {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        HashMap<String, String> params = new HashMap<String, String>();
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
    public HashMap<String, String> getCommonParams() {
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
    public HashMap<String, String> initDeviceParams() {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("os", DeviceInfo.getOS());
        params.put("osVersion", DeviceInfo.getOSVersion());
        params.put("model", DeviceInfo.getDeviceModel());
        params.put("brand", DeviceInfo.getDeviceBrand());
        params.put("carrier", DeviceInfo.getNetworkOperatorName(mContext));
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
        params.put("cpuInfo", DeviceInfo.getCPUInfo());
        //params.put("memInfo", DeviceInfo.getMemInfo());
        StrictMode.setThreadPolicy(oldPolicy);
        return params;
    }

    @Override
    public HashMap<String, String> getDeviceParams() {
        return deviceParams;
    }
    private String getDeviceId(Context context) {
        String deviceId = DeviceInfo.getOpenUDID(context);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = DeviceInfo.getDeviceId(context);
        }
        return deviceId;
    }

    private String getChannelID(Context context) {
        String channel_id = DeviceInfo.getAppStringMetaData(context, "CHANNEL_ID");
        if (TextUtils.isEmpty(channel_id)) {
            channel_id = "UNKNOWN";
        }
        return channel_id;
    }

    private String getAppID(Context context) {
        String app_id = DeviceInfo.getAppStringMetaData(context, "APP_ID");
        if (TextUtils.isEmpty(app_id)) {
            app_id = context.getPackageName();
        }
        return app_id;
    }
}
