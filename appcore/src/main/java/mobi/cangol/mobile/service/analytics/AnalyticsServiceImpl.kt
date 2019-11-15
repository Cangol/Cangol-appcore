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
package mobi.cangol.mobile.service.analytics

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.text.TextUtils
import mobi.cangol.mobile.core.BuildConfig
import mobi.cangol.mobile.http.AsyncHttpClient
import mobi.cangol.mobile.http.AsyncHttpResponseHandler
import mobi.cangol.mobile.http.RequestParams
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.PoolManager
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import mobi.cangol.mobile.utils.DeviceInfo
import mobi.cangol.mobile.utils.TimeUtils
import org.json.JSONObject
import java.util.*

/**
 * @author Cangol
 */
@Service("AnalyticsService")
internal class AnalyticsServiceImpl : ITrackerHandler(), AnalyticsService {
    private var mDebug = false
    private var mContext: Application? = null
    private var mAsyncHttpClient: AsyncHttpClient? = null
    private var mServiceProperty= ServiceProperty(TAG)
    private val mTrackers = HashMap<String, ITracker>()
    private var commonParams= HashMap<String, String>()
    private var deviceParams= HashMap<String, String>()
    override fun onCreate(context: Application) {
        mContext = context
    }

    override fun init(serviceProperty: ServiceProperty) {
        this.mServiceProperty = serviceProperty
        PoolManager.buildPool(mServiceProperty.getString(AnalyticsService.ANALYTICSSERVICE_THREADPOOL_NAME)!!, mServiceProperty!!.getInt(AnalyticsService.ANALYTICSSERVICE_THREAD_MAX))
        mAsyncHttpClient = AsyncHttpClient.build(mServiceProperty.getString(AnalyticsService.ANALYTICSSERVICE_THREADPOOL_NAME)!!)
        try {
            commonParams = initCommonParams()
            deviceParams = initDeviceParams()
        } catch (e: Exception) {
            Log.w(TAG, ":" + e.message)
        }
    }

    override fun getName(): String {
        return TAG
    }

    override fun onDestroy() {
        mAsyncHttpClient!!.cancelRequests(mContext!!, true)
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun defaultServiceProperty(): ServiceProperty {
        val sp = ServiceProperty(TAG)
        sp.putString(AnalyticsService.ANALYTICSSERVICE_THREADPOOL_NAME, TAG)
        sp.putInt(AnalyticsService.ANALYTICSSERVICE_THREAD_MAX, 2)
        return sp
    }

    override fun send(iTracker: ITracker, url: String, params: Map<String, String>) {
        val headers = HashMap<String, String>()
        headers["device"] = JSONObject(deviceParams).toString()
        headers.putAll(commonParams!!)

        mAsyncHttpClient!!.post(mContext!!, url, headers, params, object : AsyncHttpResponseHandler() {

            override fun onStart() {
                super.onStart()
                if (mDebug) {
                    val requestParams = RequestParams(params)
                    val result = StringBuilder()
                    for ((key, value) in headers) {
                        result.append('\t').append(key).append('=').append(value).append('\n')
                    }
                    Log.v(TAG, "url: " + url + "?" + requestParams.toString()
                            + "\nheader: \n" + result.toString()
                            + "params: \n" + requestParams.toDebugString())
                }
            }

            override fun onSuccess(content: String) {
                super.onSuccess(content)
                if (mDebug) Log.d(TAG, iTracker.trackingId + " send Success :" + content)
            }

            override fun onFailure(error: Throwable, content: String) {
                super.onFailure(error, content)
                if (mDebug) Log.d(TAG, iTracker.trackingId + " send Failure :" + content)
            }

        })
    }

    override fun setDebug(debug: Boolean) {
        this.mDebug = debug
    }

    override fun getTracker(trackingId: String): ITracker {
        return if (mTrackers.containsKey(trackingId)) {
            mTrackers[trackingId]!!
        } else {
            val tracker = ITracker(trackingId, this)
            mTrackers[trackingId] = tracker
            tracker
        }
    }

    override fun closeTracker(trackingId: String) {
        val tracker = mTrackers[trackingId]
        if (tracker != null) {
            mTrackers.remove(trackingId)
            tracker.isClosed = true
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
    protected fun initCommonParams(): HashMap<String, String> {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        val params = HashMap<String, String>()
        params["osVersion"] = DeviceInfo.getOSVersion()
        params["deviceId"] = getDeviceId(mContext)
        params["platform"] = DeviceInfo.getOS()
        params["channelId"] = getChannelID(mContext)
        params["appId"] = getAppID(mContext)!!
        params["appVersion"] = DeviceInfo.getAppVersion(mContext!!)
        params["sdkVersion"] = BuildConfig.VERSION_NAME
        params["timestamp"] = TimeUtils.getCurrentTime()
        StrictMode.setThreadPolicy(oldPolicy)
        return params
    }

    override fun getCommonParams(): HashMap<String, String> {
        return commonParams
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
    fun initDeviceParams(): HashMap<String, String> {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        val params = HashMap<String, String>()
        params["os"] = DeviceInfo.getOS()
        params["osVersion"] = DeviceInfo.getOSVersion()
        params["model"] = DeviceInfo.getDeviceModel()
        params["brand"] = DeviceInfo.getDeviceBrand()
        params["carrier"] = DeviceInfo.getNetworkOperator(mContext!!)
        params["screenSize"] = DeviceInfo.getScreenSize(mContext!!)
        params["density"] = "" + DeviceInfo.getDensity(mContext!!)
        params["densityDpi"] = DeviceInfo.getDensityDpiStr(mContext!!)
        params["resolution"] = DeviceInfo.getResolution(mContext!!)
        params["locale"] = DeviceInfo.getLocale()
        params["country"] = DeviceInfo.getCountry()
        params["language"] = DeviceInfo.getLanguage()
        params["charset"] = DeviceInfo.getCharset()!!
        params["ip"] = DeviceInfo.getIpStr(mContext!!)
        params["mac"] = DeviceInfo.getMacAddress(mContext!!)
        params["cpuABI"] = DeviceInfo.getCPUABI()
        params["cpuInfo"] = DeviceInfo.getCPUInfo()
        params["memSize"] = "" + DeviceInfo.getMemTotalSize()
        StrictMode.setThreadPolicy(oldPolicy)
        return params
    }

    override fun getDeviceParams(): Map<String, String> {
        return deviceParams
    }

    private fun getDeviceId(context: Context?): String {
        var deviceId = DeviceInfo.getOpenUDID(context!!)
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = DeviceInfo.getDeviceId(context)
        }
        return deviceId!!
    }

    private fun getChannelID(context: Context?): String {
        var channelId = DeviceInfo.getAppStringMetaData(context!!, "CHANNEL_ID")
        if (TextUtils.isEmpty(channelId)) {
            channelId = "UNKNOWN"
        }
        return channelId!!
    }

    private fun getAppID(context: Context?): String? {
        var appId = DeviceInfo.getAppStringMetaData(context!!, "APP_ID")
        if (TextUtils.isEmpty(appId)) {
            appId = context.packageName
        }
        return appId
    }

    companion object {
        private const val TAG = "AnalyticsService"
    }
}
