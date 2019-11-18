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
package mobi.cangol.mobile.stat

import mobi.cangol.mobile.CoreApplication
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.AppService
import mobi.cangol.mobile.service.analytics.AnalyticsService
import mobi.cangol.mobile.service.analytics.IMapBuilder
import mobi.cangol.mobile.service.analytics.ITracker
import mobi.cangol.mobile.service.crash.CrashService
import mobi.cangol.mobile.service.session.SessionService
import mobi.cangol.mobile.stat.session.StatsSession
import mobi.cangol.mobile.stat.traffic.StatsTraffic
import mobi.cangol.mobile.utils.Constants
import mobi.cangol.mobile.utils.TimeUtils
import java.util.*

object StatAgent {
    private const val STAT_SERVER_URL = "https://www.cangol.mobi/"
    private const val STAT_ACTION_EXCEPTION = "api/countly/crash"
    private const val STAT_ACTION_EVENT = "api/countly/event"
    private const val STAT_ACTION_TIMING = "api/countly/qos"
    private const val STAT_ACTION_LAUNCH = "api/countly/launch"
    private const val STAT_ACTION_SESSION = "api/countly/session"
    private const val STAT_ACTION_TRAFFIC = "api/countly/traffic"
    private const val STAT_TRACKING_ID = "stat"
    private const val TIMESTAMP = "timestamp"

    private var itracker: ITracker? = null
    private var analyticsService: AnalyticsService? = null
    private var sessionService: SessionService? = null
    private var crashService: CrashService? = null
    private var statServerURL: String? = null
    private var instance: StatAgent? = null
    private var context: CoreApplication? = null
    private var statHostUrl: String? = null

    @JvmStatic
    fun initInstance(coreApplication: CoreApplication) {
        if (instance == null) {
            instance = StatAgent
            instance!!.init(coreApplication)
        }
    }

    @JvmStatic
    fun getInstance(): StatAgent {
        checkNotNull(instance) { "Please invoke initInstance in first!" }
        return instance!!
    }

    fun setStatServerURL(statServerURL: String) {
        this.statServerURL = statServerURL
    }

    private fun getStatHostUrl(): String {
        return if (statServerURL == null || "" == statServerURL) {
            STAT_SERVER_URL
        } else {
            statHostUrl!!
        }
    }

    private fun init(context: CoreApplication) {
        this.context = context
        sessionService = context.getAppService(AppService.SESSION_SERVICE) as SessionService
        analyticsService = context.getAppService(AppService.ANALYTICS_SERVICE) as AnalyticsService
        crashService = context.getAppService(AppService.CRASH_SERVICE) as CrashService
        itracker = analyticsService?.getTracker(STAT_TRACKING_ID)

        sendLaunch()
        StatsSession.getInstance().setOnSessionListener(object : StatsSession.OnSessionListener {
            override fun onTick(sessionId: String?, beginSession: String?, sessionDuration: String?, endSession: String?, activityId: String?) {
                send(Builder.createSession(sessionId, beginSession, sessionDuration, endSession, activityId))
            }
        })
        StatsTraffic.getInstance(context).onCreated(context!!)
        crashService?.setReport(getStatHostUrl() + STAT_ACTION_EXCEPTION, null)
    }

    fun destroy() {
        analyticsService?.closeTracker(STAT_TRACKING_ID)
        StatsSession.getInstance().onDestroy()
        StatsTraffic.getInstance(context!!).onDestroy()
    }

    fun setDebug(debug: Boolean) {
        analyticsService?.setDebug(debug)
    }

    fun send(eventBuilder: Builder) {
        val builder = IMapBuilder.build()
        builder.setAll(eventBuilder.build())
        when (eventBuilder.type) {
            Builder.Type.EVENT -> builder.setUrl(getStatHostUrl() + STAT_ACTION_EVENT)
            Builder.Type.TIMING -> builder.setUrl(getStatHostUrl() + STAT_ACTION_TIMING)
            Builder.Type.EXCEPTION -> builder.setUrl(getStatHostUrl() + STAT_ACTION_EXCEPTION)
            Builder.Type.LAUNCHER -> builder.setUrl(getStatHostUrl() + STAT_ACTION_LAUNCH)
            Builder.Type.SESSION -> builder.setUrl(getStatHostUrl() + STAT_ACTION_SESSION)
            Builder.Type.TRAFFIC -> builder.setUrl(getStatHostUrl() + STAT_ACTION_TRAFFIC)
        }
        itracker?.send(builder)
    }

    fun sendLaunch() {
        var exitCode: String? = ""
        var exitVersion: String? = ""
        var isnew: Boolean? = true
        if (sessionService!!.containsKey(Constants.KEY_IS_NEW_USER)) {
            sessionService?.saveBoolean(Constants.KEY_IS_NEW_USER, false)
        } else {
            isnew = sessionService?.getBoolean(Constants.KEY_IS_NEW_USER, false)
        }
        if (sessionService?.getString(Constants.KEY_EXIT_CODE, null) != null) {
            exitCode = sessionService?.getString(Constants.KEY_EXIT_CODE, null)
        }
        if (sessionService?.getString(Constants.KEY_EXIT_VERSION, null) != null) {
            exitVersion = sessionService?.getString(Constants.KEY_EXIT_VERSION, null)
        }
        send(Builder.createLaunch(exitCode, exitVersion, isnew, TimeUtils.getCurrentTime()))
    }

    fun sendTraffic() {
        val statsTraffic = StatsTraffic.getInstance(context!!.applicationContext)
        val list = statsTraffic.getUnPostDateTraffic(context?.applicationInfo!!.uid, TimeUtils.getCurrentDate())
        for (i in list.indices) {
            send(Builder.createTraffic(list[i]))
        }
        statsTraffic.saveUnPostDateTraffic(context?.applicationInfo!!.uid, TimeUtils.getCurrentDate())
    }

    fun onActivityResume(pageName: String) {
        StatsSession.getInstance().onStart(pageName)
    }

    fun onActivityPause(pageName: String) {
        StatsSession.getInstance().onStop(pageName)
    }

    fun onFragmentResume(pageName: String) {
        StatsSession.getInstance().onStart(pageName)
    }

    fun onFragmentPause(pageName: String) {
        StatsSession.getInstance().onStop(pageName)
    }

    object Builder {
        internal var type: Type? = null
        private val map = HashMap<String, String>()

        operator fun set(paramName: String?, paramValue: String?): Builder {
            if (paramName != null) {
                this.map[paramName] = paramValue!!
            } else {
                Log.w(" EventBuilder.set() called with a null paramName.")
            }
            return this
        }

        operator fun get(paramName: String): String? {
            return this.map[paramName]
        }

        @JvmStatic
        fun build(): Map<String, String> {
            return HashMap(this.map)
        }

        enum class Type {
            EVENT,
            TIMING,
            EXCEPTION,
            LAUNCHER,
            SESSION,
            TRAFFIC
        }


        @JvmStatic
        fun createAppView(view: String): Builder {
            val builder = Builder
            builder["view"] = view
            builder["action"] = "visit"
            builder[TIMESTAMP] = TimeUtils.getCurrentTime()
            builder.type = Type.EVENT
            return builder
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
        @JvmStatic
        fun createEvent(user: String?, view: String?, action: String?, target: String?, result: Long?): Builder {

            val builder = Builder
            builder["userId"] = user ?: ""
            builder["view"] = view ?: ""
            builder["action"] = action ?: ""
            builder["target"] = target ?: ""
            builder["result"] = result?.toString() ?: ""
            builder[TIMESTAMP] = TimeUtils.getCurrentTime()
            builder.type = Type.EVENT
            return builder
        }

        /**
         * 时间统计
         *
         * @param view
         * @param idleTime
         * @return
         */
        @JvmStatic
        fun createTiming(view: String?, idleTime: Long?): Builder {
            val builder = Builder
            builder["view"] = view ?: ""
            builder["idleTime"] = idleTime?.toString() ?: ""
            builder[TIMESTAMP] = TimeUtils.getCurrentTime()
            builder.type = Type.TIMING
            return builder
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
        @JvmStatic
        fun createException(error: String?, position: String?, content: String?, timestamp: String?, fatal: String?): Builder {
            val builder = Builder
            builder["error"] = error ?: ""
            builder["position"] = position ?: ""
            builder["content"] = content ?: ""
            builder[TIMESTAMP] = timestamp ?: ""
            builder["fatal"] = fatal ?: ""
            builder.type = Type.EXCEPTION
            return builder

        }

        /**
         * 启动
         *
         * @param exitCode    上次退出编码
         * @param exitVersion 上次退出版本
         * @param isNew       是否新用户
         * @return
         */
        @JvmStatic
        fun createLaunch(exitCode: String?, exitVersion: String?, isNew: Boolean?, launchTime: String?): Builder {
            val builder = Builder
            builder["exitCode"] = exitCode ?: ""
            builder["exitVersion"] = exitVersion ?: ""
            builder["launchTime"] = launchTime ?: ""
            builder[TIMESTAMP] = TimeUtils.getCurrentTime()
            builder["isNew"] = if (isNew!!) "1" else "0"
            builder.type = Type.LAUNCHER
            return builder

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
        @JvmStatic
        fun createSession(sessionId: String?, beginSession: String?, sessionDuration: String?, endSession: String?, activityId: String?): Builder {
            val builder = Builder
            builder["sessionId"] = sessionId ?: ""
            builder["beginSession"] = beginSession ?: ""
            builder["sessionDuration"] = sessionDuration ?: ""
            builder["endSession"] = endSession ?: ""
            builder["activityId"] = activityId ?: ""
            builder[TIMESTAMP] = TimeUtils.getCurrentTime()
            builder.type = Type.SESSION
            return builder

        }

        /**
         * 流量统计
         *
         * @param map
         * @return
         */
        @JvmStatic
        fun createTraffic(map: Map<String, String>): Builder {
            val builder = Builder
            builder["date"] = map["date"]
            builder["totalRx"] = map["totalRx"]
            builder["totalTx"] = map["totalTx"]
            builder["mobileRx"] = map["mobileRx"]
            builder["mobileTx"] = map["mobileTx"]
            builder["wifiRx"] = map["wifiRx"]
            builder["wifiTx"] = map["wifiTx"]
            builder[TIMESTAMP] = TimeUtils.getCurrentTime()
            builder.type = Type.TRAFFIC
            return builder

        }
    }
}
