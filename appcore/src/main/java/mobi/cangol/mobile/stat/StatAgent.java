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
package mobi.cangol.mobile.stat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.analytics.AnalyticsService;
import mobi.cangol.mobile.service.analytics.IMapBuilder;
import mobi.cangol.mobile.service.analytics.ITracker;
import mobi.cangol.mobile.service.crash.CrashService;
import mobi.cangol.mobile.service.session.SessionService;
import mobi.cangol.mobile.stat.session.StatsSession;
import mobi.cangol.mobile.stat.traffic.StatsTraffic;
import mobi.cangol.mobile.utils.Constants;
import mobi.cangol.mobile.utils.DeviceInfo;
import mobi.cangol.mobile.utils.TimeUtils;

public class StatAgent {
    private final static String SDK_VERSION = "1.0.5";
    private final static String STAT_HOST_URL = "http://www.cangol.mobi/cmweb/";
    private final static String STAT_ACTION_EXCEPTION = "api/countly/crash.do";
    private final static String STAT_ACTION_EVENT = "api/countly/event.do";
    private final static String STAT_ACTION_TIMING = "api/countly/qos.do";
    private final static String STAT_ACTION_LAUNCH = "api/countly/launch.do";
    private final static String STAT_ACTION_DEVICE = "api/countly/device.do";
    private final static String STAT_ACTION_SESSION = "api/countly/session.do";
    private final static String STAT_ACTION_TRAFFIC = "api/countly/traffic.do";
    private final static String STAT_TRACKING_ID = "stat";
    private final static String TIMESTAMP = "timestamp";
    private static StatAgent instance;
    private CoreApplication context;
    private ITracker itracker;
    private HashMap<String, String> commonParams;
    private AnalyticsService analyticsService;
    private SessionService sessionService;
    private CrashService crashService;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    protected StatAgent(CoreApplication coreApplication) {
        this.context = coreApplication;
        sessionService = (SessionService) coreApplication.getAppService(AppService.SESSION_SERVICE);
        analyticsService = (AnalyticsService)coreApplication.getAppService(AppService.ANALYTICS_SERVICE);
        crashService= (CrashService)coreApplication.getAppService(AppService.CRASH_SERVICE);
        itracker = analyticsService.getTracker(STAT_TRACKING_ID);

        commonParams = this.getCommonParams();
    }
    public static StatAgent getInstance(Context context){
        if (instance == null) {
            instance=new StatAgent((CoreApplication) context.getApplicationContext());
            instance.init();
        }
        return instance;
    }
    public static void initInstance(CoreApplication coreApplication){
        if (instance == null) {
            instance=new StatAgent(coreApplication);
            instance.init();
        }
    }
    protected void init() {
        sendDevice();
        sendLaunch();
        StatsSession.getInstance(context).setOnSessionListener(new StatsSession.OnSessionListener() {
            @Override
            public void onTick(String sessionId, String beginSession, String sessionDuration, String endSession, String activityId) {
                send(Builder.createSession(sessionId, beginSession, sessionDuration, endSession, activityId));
            }
        });
        StatsTraffic.getInstance(context).onCreated();
        crashService.setReport(STAT_HOST_URL+STAT_ACTION_EXCEPTION,getCommonParams());
    }

    public void destroy() {
        analyticsService.closeTracker(STAT_TRACKING_ID);
        StatsSession.getInstance(context).onDestroy();
        StatsTraffic.getInstance(context).onDestroy();
    }

    /**
     * 公共参数 osVersion 操作系统版本号 4.2.2 deviceId 设备唯一ID Android|IOS均为open-uuid
     * platform 平台 平台控制使用IOS|Android channelId 渠道
     * 渠道控制使用(Google|baidu|91|appstore…) appId App ID appVersion App版本号 1.1.0
     * sdkVersion 统计SDK版本号
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private HashMap<String, String> getCommonParams() {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("osVersion", DeviceInfo.getOSVersion());
        params.put("deviceId", getDeviceId(context));
        params.put("platform", DeviceInfo.getOS());
        params.put("channelId", getChannelID(context));
        params.put("appId", getAppID(context));
        params.put("appVersion", DeviceInfo.getAppVersion(context));
        params.put("sdkVersion", SDK_VERSION);
        params.put(TIMESTAMP, TimeUtils.getCurrentTime());
        StrictMode.setThreadPolicy(oldPolicy);
        return params;
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
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private HashMap<String, String> getDeviceParams() {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("os", DeviceInfo.getOS());
        params.put("osVersion", DeviceInfo.getOSVersion());
        params.put("model", DeviceInfo.getDeviceModel());
        params.put("brand", DeviceInfo.getDeviceBrand());
        params.put("carrier", DeviceInfo.getOperator(context));
        params.put("screenSize", DeviceInfo.getScreenSize(context));
        params.put("density", "" + DeviceInfo.getDensity(context));
        params.put("densityDpi", DeviceInfo.getDensityDpiStr(context));
        params.put("resolution", DeviceInfo.getResolution(context));
        params.put("locale", DeviceInfo.getLocale());
        params.put("country", DeviceInfo.getCountry());
        params.put("language", DeviceInfo.getLanguage());
        params.put("charset", DeviceInfo.getCharset());
        params.put("ip", DeviceInfo.getIpStr(context));
        params.put("mac", DeviceInfo.getMacAddress(context));
        params.put("cpuInfo", DeviceInfo.getCPUInfo());
        params.put("memInfo", DeviceInfo.getMemInfo());
        StrictMode.setThreadPolicy(oldPolicy);
        return params;
    }

    private String getDeviceId(Context context) {
        String deviceId = DeviceInfo.getOpenUDID(context);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = DeviceInfo.getDeviceId(context);
        }
        return deviceId;
    }

    private String getChannelID(Context context) {
        String channel_id = sessionService.getString("CHANNEL_ID", null);
        if (TextUtils.isEmpty(channel_id)) {
            channel_id = DeviceInfo.getAppStringMetaData(context, "CHANNEL_ID");
            if (TextUtils.isEmpty(channel_id)) {
                channel_id = "UNKNOWN";
            } else {
                sessionService.saveString("CHANNEL_ID", channel_id);
            }
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

    public void setDebug(boolean debug) {
        analyticsService.setDebug(debug);
    }

    public void send(Builder eventBuilder) {
        IMapBuilder builder = IMapBuilder.build();
        builder.setAll(commonParams);
        builder.setAll(eventBuilder.build());
        switch (eventBuilder.type) {
            case Device:
                builder.setAll(getDeviceParams());
                builder.setUrl(STAT_HOST_URL + STAT_ACTION_DEVICE);
                break;
            case Event:
                builder.setUrl(STAT_HOST_URL + STAT_ACTION_EVENT);
                break;
            case Timing:
                builder.setUrl(STAT_HOST_URL + STAT_ACTION_TIMING);
                break;
            case Exception:
                builder.setUrl(STAT_HOST_URL + STAT_ACTION_EXCEPTION);
                break;
            case Launcher:
                builder.setUrl(STAT_HOST_URL + STAT_ACTION_LAUNCH);
                break;
            case Session:
                if ("1".equals(eventBuilder.get("beginSession"))) {
                    builder.setAll(getDeviceParams());
                }
                builder.setUrl(STAT_HOST_URL + STAT_ACTION_SESSION);
                break;
            case Traffic:
                builder.setUrl(STAT_HOST_URL + STAT_ACTION_TRAFFIC);
                break;
        }
        itracker.send(builder);
    }

    public void sendLaunch() {
        String exitCode = "";
        String exitVersion = "";
        boolean isnew = true;
        if (sessionService.containsKey(Constants.KEY_IS_NEW_USER)) {
            sessionService.saveBoolean(Constants.KEY_IS_NEW_USER, false);
        } else {
            isnew = sessionService.getBoolean(Constants.KEY_IS_NEW_USER, false);
        }
        if (sessionService.getString(Constants.KEY_EXIT_CODE, null) != null) {
            exitCode = sessionService.getString(Constants.KEY_EXIT_CODE, null);
        }
        if (sessionService.getString(Constants.KEY_EXIT_VERSION, null) != null) {
            exitVersion = sessionService.getString(Constants.KEY_EXIT_VERSION, null);
        }
        send(Builder.createLaunch(exitCode, exitVersion, isnew, TimeUtils.getCurrentTime()));
    }

    public void sendDevice() {
        send(Builder.createDevice());
    }

    public void sendTraffic() {
        StatsTraffic statsTraffic = StatsTraffic.getInstance(context);
        List<Map> list = statsTraffic.getUnPostDateTraffic(context.getApplicationInfo().uid, TimeUtils.getCurrentDate());
        for (int i = 0; i < list.size(); i++) {
            send(Builder.createTraffic(list.get(i)));
        }
        statsTraffic.saveUnPostDateTraffic(context.getApplicationInfo().uid, TimeUtils.getCurrentDate());
    }

    public void onActivityResume(String pageName) {
        StatsSession.getInstance(context).onStart(pageName);
    }

    public void onActivityPause(String pageName) {
        StatsSession.getInstance(context).onStop(pageName);
    }

    public void onFragmentResume(String pageName) {
        StatsSession.getInstance(context).onStart(pageName);
    }

    public void onFragmentPause(String pageName) {
        StatsSession.getInstance(context).onStop(pageName);
    }

    public static class Builder {
        Type type;
        private Map<String, String> map = new HashMap<String, String>();

        protected static Builder createDevice() {
            Builder builder = new Builder();
            builder.type = Type.Device;
            return builder;
        }

        public static Builder createAppView(String view) {
            Builder builder = new Builder();
            builder.set("view", view);
            builder.set("action", "Vistor");
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.type = Type.Event;
            return builder;
        }

        /**
         * 事件统计
         *
         * @param user   用户
         * @param action 动作
         * @param view   页面
         * @param target 目标
         * @param result 结果
         * @return
         */
        public static Builder createEvent(String user, String view, String action, String target, Long result) {

            Builder builder = new Builder();
            builder.set("userId", user);
            builder.set("view", view);
            builder.set("action", action);
            builder.set("target", target);
            builder.set("result", result == null ? null : Long.toString(result.longValue()));
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.type = Type.Event;
            return builder;
        }

        /**
         * 时间统计
         *
         * @param view
         * @param idletime
         * @return
         */
        public static Builder createTiming(String view, Long idletime) {
            Builder builder = new Builder();
            builder.set("view", view);
            builder.set("idleTime", idletime == null ? null : Long.toString(idletime.longValue()));
            builder.type = Type.Timing;
            return builder;
        }

        /**
         * 异常统计
         *
         * @param error    异常类型
         * @param position 位置
         * @param content  详细log
         * @param fatal    是否奔溃
         * @return
         */
        public static Builder createException(String error, String position, String content, String timestamp, String fatal) {
            Builder builder = new Builder();
            builder.set("error", error);
            builder.set("position", position);
            builder.set("content", content);
            builder.set(TIMESTAMP, timestamp);
            builder.set("fatal", fatal);
            builder.type = Type.Event;
            return builder;

        }

        /**
         * 启动
         *
         * @param exitCode    上次退出编码
         * @param exitVersion 上次退出版本
         * @param isNew       是否新用户
         * @return
         */
        protected static Builder createLaunch(String exitCode, String exitVersion, Boolean isNew, String launchTime) {
            Builder builder = new Builder();
            builder.set("exitCode", exitCode);
            builder.set("exitVersion", exitVersion);
            builder.set("launchTime", launchTime);
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.set("isNew", isNew == null ? null : isNew ? "1" : "0");
            builder.type = Type.Launcher;
            return builder;

        }

        /**
         * 会话统计
         *
         * @param sessionId
         * @param beginSession
         * @param sessionDuration
         * @param endSession
         * @param activityId
         * @return
         */
        protected static Builder createSession(String sessionId
                , String beginSession
                , String sessionDuration
                , String endSession
                , String activityId) {
            Builder builder = new Builder();
            builder.set("sessionId", sessionId);
            builder.set("beginSession", beginSession);
            builder.set("sessionDuration", sessionDuration);
            builder.set("endSession", endSession);
            builder.set("activityId", activityId);
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.type = Type.Session;
            return builder;

        }

        /**
         * 流量统计
         * @param map
         * @return
         */
        protected static Builder createTraffic(Map<String, String> map) {
            Builder builder = new Builder();
            builder.set("date", map.get("date"));
            builder.set("totalRx", map.get("totalRx"));
            builder.set("totalTx", map.get("totalTx"));
            builder.set("mobileRx", map.get("mobileRx"));
            builder.set("mobileTx", map.get("mobileTx"));
            builder.set("wifiRx", map.get("wifiRx"));
            builder.set("wifiTx", map.get("wifiTx"));
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.type = Type.Traffic;
            return builder;

        }

        public Type getType() {
            return type;
        }

        public Builder set(String paramName, String paramValue) {
            if (paramName != null) {
                this.map.put(paramName, paramValue);
            } else {
                Log.w(" EventBuilder.set() called with a null paramName.");
            }
            return this;
        }

        public Builder setAll(Map<String, String> params) {
            if (params == null) {
                return this;
            }
            this.map.putAll(new HashMap<String, String>(params));
            return this;
        }

        public String get(String paramName) {
            return (String) this.map.get(paramName);
        }

        public Map<String, String> build() {
            return new HashMap<String, String>(this.map);
        }

        enum Type {
            Device,
            Event,
            Timing,
            Exception,
            Launcher,
            Session,
            Traffic
        }
    }
}
