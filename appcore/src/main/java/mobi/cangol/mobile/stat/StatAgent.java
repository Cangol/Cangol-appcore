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
import android.os.Build;

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
import mobi.cangol.mobile.service.session.Session;
import mobi.cangol.mobile.service.session.SessionService;
import mobi.cangol.mobile.stat.session.StatsSession;
import mobi.cangol.mobile.stat.traffic.StatsTraffic;
import mobi.cangol.mobile.utils.Constants;
import mobi.cangol.mobile.utils.TimeUtils;

public class StatAgent {
    private static final  String STAT_SERVER_URL = "https://www.cangol.mobi/";
    private static final  String STAT_ACTION_EXCEPTION = "api/countly/crash";
    private static final  String STAT_ACTION_EVENT = "api/countly/event";
    private static final  String STAT_ACTION_TIMING = "api/countly/qos";
    private static final  String STAT_ACTION_LAUNCH = "api/countly/launch";
    private static final  String STAT_ACTION_SESSION = "api/countly/session";
    private static final  String STAT_ACTION_TRAFFIC = "api/countly/traffic";
    private static final  String STAT_TRACKING_ID = "stat";
    private static final  String TIMESTAMP = "timestamp";
    private static StatAgent instance;
    private CoreApplication context;
    private ITracker itracker;
    private AnalyticsService analyticsService;
    private Session session;
    private CrashService crashService;
    private String statServerURL=null;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    protected StatAgent(CoreApplication coreApplication) {
        this.context = coreApplication;
        session = ((SessionService) coreApplication.getAppService(AppService.SESSION_SERVICE)).getSession();
        analyticsService = (AnalyticsService) coreApplication.getAppService(AppService.ANALYTICS_SERVICE);
        crashService = (CrashService) coreApplication.getAppService(AppService.CRASH_SERVICE);
        itracker = analyticsService.getTracker(STAT_TRACKING_ID);
    }
    public static StatAgent getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Please invoke initInstance in first!");
        }
        return instance;
    }
    public static void initInstance(CoreApplication coreApplication) {
        if (instance == null) {
            instance = new StatAgent(coreApplication);
            instance.init();
        }
    }
    public void setStatServerURL(String statServerURL) {
        this.statServerURL = statServerURL;
    }

    private String getStatHostUrl(){
        if(statServerURL==null||"".equals(statServerURL)){
            return STAT_SERVER_URL;
        }else{
            return statServerURL;
        }
    }
    private void init() {
        sendLaunch();
        StatsSession.getInstance().setOnSessionListener(new StatsSession.OnSessionListener() {
            @Override
            public void onTick(String sessionId, String beginSession, String sessionDuration, String endSession, String activityId) {
                send(Builder.createSession(sessionId, beginSession, sessionDuration, endSession, activityId));
            }
        });
        StatsTraffic.getInstance(context).onCreated();
        crashService.setReport(getStatHostUrl() + STAT_ACTION_EXCEPTION, null);
    }

    public void destroy() {
        analyticsService.closeTracker(STAT_TRACKING_ID);
        StatsSession.getInstance().onDestroy();
        StatsTraffic.getInstance(context).onDestroy();
    }

    public void setDebug(boolean debug) {
        analyticsService.setDebug(debug);
    }

    public void send(Builder eventBuilder) {
        final IMapBuilder builder = IMapBuilder.build();
        builder.setAll(eventBuilder.build());
        switch (eventBuilder.type) {
            case EVENT:
                builder.setUrl(getStatHostUrl() + STAT_ACTION_EVENT);
                break;
            case TIMING:
                builder.setUrl(getStatHostUrl() + STAT_ACTION_TIMING);
                break;
            case EXCEPTION:
                builder.setUrl(getStatHostUrl() + STAT_ACTION_EXCEPTION);
                break;
            case LAUNCHER:
                builder.setUrl(getStatHostUrl() + STAT_ACTION_LAUNCH);
                break;
            case SESSION:
                builder.setUrl(getStatHostUrl() + STAT_ACTION_SESSION);
                break;
            case TRAFFIC:
                builder.setUrl(getStatHostUrl() + STAT_ACTION_TRAFFIC);
                break;
        }
        itracker.send(builder);
    }

    public void sendLaunch() {
        String exitCode = "";
        String exitVersion = "";
        boolean isnew = true;
        if (session.containsKey(Constants.KEY_IS_NEW_USER)) {
            session.saveBoolean(Constants.KEY_IS_NEW_USER, false);
        } else {
            isnew = session.getBoolean(Constants.KEY_IS_NEW_USER, false);
        }
        if (session.getString(Constants.KEY_EXIT_CODE, null) != null) {
            exitCode = session.getString(Constants.KEY_EXIT_CODE, null);
        }
        if (session.getString(Constants.KEY_EXIT_VERSION, null) != null) {
            exitVersion = session.getString(Constants.KEY_EXIT_VERSION, null);
        }
        send(Builder.createLaunch(exitCode, exitVersion, isnew, TimeUtils.getCurrentTime()));
    }

    public void sendTraffic() {
        final StatsTraffic statsTraffic = StatsTraffic.getInstance(context);
        final List<Map> list = statsTraffic.getUnPostDateTraffic(context.getApplicationInfo().uid, TimeUtils.getCurrentDate());
        for (int i = 0; i < list.size(); i++) {
            send(Builder.createTraffic(list.get(i)));
        }
        statsTraffic.saveUnPostDateTraffic(context.getApplicationInfo().uid, TimeUtils.getCurrentDate());
    }

    public void onActivityResume(String pageName) {
        StatsSession.getInstance().onStart(pageName);
    }

    public void onActivityPause(String pageName) {
        StatsSession.getInstance().onStop(pageName);
    }

    public void onFragmentResume(String pageName) {
        StatsSession.getInstance().onStart(pageName);
    }

    public void onFragmentPause(String pageName) {
        StatsSession.getInstance().onStop(pageName);
    }

    public static class Builder {
        protected  Type type;
        private Map<String, String> map = new HashMap<>();

        public static Builder createAppView(String view) {
            Builder builder = new Builder();
            builder.set("view", view);
            builder.set("action", "visit");
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.type = Type.EVENT;
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
            builder.type = Type.EVENT;
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
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.type = Type.TIMING;
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
            builder.type = Type.EXCEPTION;
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
        protected static Builder createLaunch(String exitCode, String exitVersion, boolean isNew, String launchTime) {
            final Builder builder = new Builder();
            builder.set("exitCode", exitCode);
            builder.set("exitVersion", exitVersion);
            builder.set("launchTime", launchTime);
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.set("isNew", isNew ? "1" : "0");
            builder.type = Type.LAUNCHER;
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
            final Builder builder = new Builder();
            builder.set("sessionId", sessionId);
            builder.set("beginSession", beginSession);
            builder.set("sessionDuration", sessionDuration);
            builder.set("endSession", endSession);
            builder.set("activityId", activityId);
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.type = Type.SESSION;
            return builder;

        }

        /**
         * 流量统计
         *
         * @param map
         * @return
         */
        protected static Builder createTraffic(Map<String, String> map) {
            final Builder builder = new Builder();
            builder.set("date", map.get("date"));
            builder.set("totalRx", map.get("totalRx"));
            builder.set("totalTx", map.get("totalTx"));
            builder.set("mobileRx", map.get("mobileRx"));
            builder.set("mobileTx", map.get("mobileTx"));
            builder.set("wifiRx", map.get("wifiRx"));
            builder.set("wifiTx", map.get("wifiTx"));
            builder.set(TIMESTAMP, TimeUtils.getCurrentTime());
            builder.type = Type.TRAFFIC;
            return builder;

        }

        public Type getType() {
            return type;
        }

        private Builder set(String paramName, String paramValue) {
            if (paramName != null) {
                this.map.put(paramName, paramValue);
            } else {
                Log.w(" EventBuilder.set() called with a null paramName.");
            }
            return this;
        }

        public String get(String paramName) {
            return this.map.get(paramName);
        }

        public Map<String, String> build() {
            return new HashMap<>(this.map);
        }

        enum Type {
            EVENT,
            TIMING,
            EXCEPTION,
            LAUNCHER,
            SESSION,
            TRAFFIC
        }
    }
}
