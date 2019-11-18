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
package mobi.cangol.mobile.stat.traffic

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.TrafficStats
import android.os.SystemClock
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.utils.TimeUtils
import java.util.*

/**
 * Created by weixuewu on 16/1/22.
 */
object StatsTraffic {
    private const val BOOT_ACTION = "android.intent.action.BOOT_COMPLETED"
    private const val DATE_ACTION = "android.intent.action.DATE_CHANGE"
    private const val NETWORK_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"

    private var instance: StatsTraffic? = null
    private var context: Application? = null
    private var trafficDbService: TrafficDbService? = null

    @JvmStatic
    fun getInstance(context: Context): StatsTraffic {
        if (instance == null) {
            instance = StatsTraffic
            instance?.onCreated(context.applicationContext as Application)
        }
        return instance!!
    }

    private val statsTrafficReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.v("getAction=" + intent.action!!)
            if (BOOT_ACTION == intent.action) {
                // 开机启动
                resetAppTraffic()
            } else if (NETWORK_ACTION == intent.action) {
                var wifiState: NetworkInfo.State? = null
                var mobileState: NetworkInfo.State? = null
                val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                @SuppressLint("MissingPermission") val networkInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                if (networkInfoWifi != null) {
                    wifiState = networkInfoWifi.state
                }
                @SuppressLint("MissingPermission") val networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                if (networkInfo != null) {
                    mobileState = networkInfo.state
                }

                if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
                    // 无线网络连接
                    Log.d("wifi connected calcTraffic")
                    calcAppTraffic(TimeUtils.getCurrentDate(), true)
                } else if (wifiState != null && NetworkInfo.State.DISCONNECTED == wifiState) {
                    // 无线网络中断
                    Log.d("wifi disconnect calcTraffic")
                    calcAppTraffic(TimeUtils.getCurrentDate(), true)
                } else if (mobileState != null && NetworkInfo.State.CONNECTED == mobileState) {
                    // 手机网络连接
                    Log.d("mobile connected calcTraffic")
                    calcAppTraffic(TimeUtils.getCurrentDate(), false)
                } else if (mobileState != null && NetworkInfo.State.DISCONNECTED == mobileState) {
                    // 手机网络中断
                    Log.d("mobile disconnect calcTraffic")
                    calcAppTraffic(TimeUtils.getCurrentDate(), false)
                } else {
                    //
                }

            } else {
                //day end
                Log.d("day end calcTraffic")
                endCalcAppTraffic()
            }
        }
    }


    fun onCreated(context: Application) {
        this.context = context
        trafficDbService = TrafficDbService(context)
        registerAlarmForDateTraffic()

        val intentFileter = IntentFilter(BOOT_ACTION)
        intentFileter.addAction(DATE_ACTION)
        intentFileter.addAction(NETWORK_ACTION)
        context.registerReceiver(statsTrafficReceiver, intentFileter)
        if (trafficDbService!!.getAppTraffic(context.applicationInfo.uid) == null) {
            addAppTrafficStats(context.applicationInfo.uid, context.applicationInfo.packageName)
        }
    }

    fun onDestroy() {
        endCalcAppTraffic()
        context?.unregisterReceiver(statsTrafficReceiver)
    }

    private fun addAppTrafficStats(uid: Int, packageName: String) {
        val appTraffic = AppTraffic()
        appTraffic.uid = uid
        appTraffic.packageName = packageName
        appTraffic.totalRx = TrafficStats.getUidRxBytes(uid)
        appTraffic.totalTx = TrafficStats.getUidTxBytes(uid)
        appTraffic.mobileRx = 0
        appTraffic.mobileTx = 0
        appTraffic.wifiRx = 0
        appTraffic.wifiTx = 0
        trafficDbService!!.saveAppTraffic(appTraffic)
    }

    private fun calcDateTraffic(appTraffic: AppTraffic, date: String, wifi: Boolean) {
        var dateTraffic = trafficDbService!!.getDateTrafficByDate(appTraffic.uid, date)
        if (dateTraffic == null) {
            dateTraffic = DateTraffic()
            dateTraffic.uid = appTraffic.uid
            dateTraffic.date = date
            dateTraffic.totalRx = 0
            dateTraffic.totalTx = 0
            dateTraffic.mobileRx = 0
            dateTraffic.mobileTx = 0
            dateTraffic.wifiRx = 0
            dateTraffic.wifiTx = 0
            dateTraffic.status = 0
        }
        val rx = TrafficStats.getUidRxBytes(appTraffic.uid)
        val tx = TrafficStats.getUidTxBytes(appTraffic.uid)
        val rxDelta = rx - appTraffic.totalRx
        val txDelta = tx - appTraffic.totalTx
        dateTraffic.totalRx = dateTraffic.totalRx + rxDelta
        dateTraffic.totalTx = dateTraffic.totalTx + txDelta
        if (!wifi) {
            dateTraffic.mobileRx = dateTraffic.mobileRx + rxDelta
            dateTraffic.mobileTx = dateTraffic.mobileTx + txDelta
        } else {
            dateTraffic.wifiRx = dateTraffic.wifiRx + rxDelta
            dateTraffic.wifiTx = dateTraffic.wifiTx + txDelta
        }
        trafficDbService!!.saveDateTraffic(dateTraffic)
    }

    fun calcAppTraffic(date: String, wifi: Boolean) {
        val list = trafficDbService!!.getAppTrafficList()
        var appTraffic: AppTraffic? = null
        for (i in list.indices) {
            appTraffic = list[i]
            calcDateTraffic(appTraffic, date, wifi)
            val rx = TrafficStats.getUidRxBytes(appTraffic.uid)
            val tx = TrafficStats.getUidTxBytes(appTraffic.uid)
            val rxDelta = rx - appTraffic.totalRx
            val txDelta = tx - appTraffic.totalTx
            appTraffic.totalRx = rx
            appTraffic.totalTx = tx
            if (!wifi) {
                appTraffic.mobileRx = appTraffic.mobileRx + rxDelta
                appTraffic.mobileTx = appTraffic.mobileTx + txDelta
            } else {
                appTraffic.wifiRx = appTraffic.wifiRx + rxDelta
                appTraffic.wifiTx = appTraffic.wifiTx + txDelta
            }
            trafficDbService!!.saveAppTraffic(appTraffic)
        }
    }

    fun resetAppTraffic() {
        val list = trafficDbService!!.getAppTrafficList()
        var appTraffic: AppTraffic? = null
        for (i in list.indices) {
            appTraffic = list[i]
            appTraffic.totalRx = TrafficStats.getUidRxBytes(appTraffic.uid)
            appTraffic.totalTx = TrafficStats.getUidTxBytes(appTraffic.uid)
            trafficDbService!!.saveAppTraffic(appTraffic)
        }
    }

    fun getUnPostDateTraffic(uid: Int, date: String): List<Map<String, String>> {
        val list = trafficDbService!!.getDateTrafficByStatus(uid, date, 0)
        var map: Map<String, String>? = null
        var dateTraffic: DateTraffic? = null
        val maps = ArrayList<Map<String, String>>()
        for (i in list.indices) {
            dateTraffic = list[i]
            map = HashMap()
            map.put("date", dateTraffic.date.toString())
            map.put("totalRx", dateTraffic.totalRx.toString())
            map.put("totalTx", dateTraffic.totalTx.toString())
            map.put("mobileRx", dateTraffic.mobileRx.toString())
            map.put("mobileTx", dateTraffic.mobileTx.toString())
            map.put("wifiRx", dateTraffic.wifiRx.toString())
            map.put("wifiTx", dateTraffic.wifiTx.toString())
            maps.add(map)
        }
        return maps
    }

    fun saveUnPostDateTraffic(uid: Int, currentDate: String) {
        val list = trafficDbService!!.getDateTrafficByStatus(uid, currentDate, 0)
        for (i in list.indices) {
            val traffic = list[i]
            traffic.status = 1
            trafficDbService!!.saveDateTraffic(traffic)
        }

    }

    //day end|| app exit
    private fun endCalcAppTraffic() {
        val date = TimeUtils.getCurrentDate()
        var wifiState: NetworkInfo.State? = null
        var mobileState: NetworkInfo.State? = null
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @SuppressLint("MissingPermission") val networkInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (networkInfoWifi != null) {
            wifiState = networkInfoWifi.state
        }
        @SuppressLint("MissingPermission") val networkInfoMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (networkInfoMobile != null) {
            mobileState = networkInfoMobile.state
        }
        if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
            // 无线网络连接
            calcAppTraffic(date, true)
        } else if (mobileState != null && NetworkInfo.State.CONNECTED == mobileState) {
            // 手机网络连接
            calcAppTraffic(date, false)
        }
    }

    fun registerAlarmForDateTraffic() {
        val day = 24 * 60 * 60 * 1000
        val intent = Intent(DATE_ACTION)
        val sender = PendingIntent.getBroadcast(this.context, 1, intent, 0)

        // 开机之后到现在的运行时间(包括睡眠时间)
        var firstTime = SystemClock.elapsedRealtime()
        val systemTime = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.timeZone = TimeZone.getTimeZone("GMT+8")
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        // 选择的定时时间
        var selectTime = calendar.timeInMillis
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            selectTime = calendar.timeInMillis
        }
        // 计算现在时间到设定时间的时间差
        val time = selectTime - systemTime
        firstTime += time
        // 进行闹铃注册
        val manager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, day.toLong(), sender)

    }
}

