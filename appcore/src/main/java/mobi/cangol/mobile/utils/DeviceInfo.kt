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
package mobi.cangol.mobile.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.WindowManager
import mobi.cangol.mobile.logging.Log
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*

/**
 * 设备信息获取工具类
 */
@SuppressLint("MissingPermission")
object DeviceInfo {
    /**
     * 默认IMEI
     */
    private const val SPECIAL_IMEI = "000000000000000"
    /**
     * 默认ANDROID_ID
     */
    private const val SPECIAL_ANDROID_ID = "9774d56d682e549c"

    /**
     * 默认MAC(小米)
     */
    private const val SPECIAL_MAC = "02:00:00:00:00:00"
    /**
     * 默认CHARSET
     */
    private const val CHARSET = "UTF-8"
    private const val UNKNOWN = "UNKNOWN"
    private const val ANDROID = "android"

    /**
     * 获取操作系统类型
     *
     * @return
     */
    @JvmStatic
    fun getOS(): String {
        return "Android"
    }

    /**
     * 获取操作系统版本号
     *
     * @return
     */
    @JvmStatic
    fun getOSVersion(): String {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    @JvmStatic
    fun getDeviceModel(): String {
        return Build.MODEL
    }

    /**
     * 获取设备品牌
     *
     * @return
     */
    @JvmStatic
    fun getDeviceBrand(): String {
        return Build.BRAND
    }

    /**
     * 获取设备信息
     *
     * @return
     */
    @JvmStatic
    fun getMobileInfo(): String {
        val sb = StringBuilder()
        try {
            val fields = Build::class.java.declaredFields
            for (field in fields) {
                field.isAccessible = true
                val name = field.name
                val value = field.get(null).toString()
                sb.append(name)
                        .append('=')
                        .append(value)
                        .append('\n')
            }
        } catch (e: Exception) {
            Log.d(e.message)
        }
        return sb.toString()
    }

    /**
     * 获取设备mem 总大小 单位B
     *
     * @return
     */
    @JvmStatic
    fun getMemTotalSize(): Long {
        var result: Long
        result = try {
            val process = ProcessBuilder("/system/bin/cat", "/proc/meminfo").start()
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream, CHARSET))
            val str = bufferedReader.readLine()
            val memStr = "MemTotal:"
            val resultStr = str.substring(str.indexOf(memStr) + memStr.length, str.indexOf(" kB"))
            bufferedReader.close()
            java.lang.Long.parseLong(resultStr.trim { it <= ' ' }) * 1024
        } catch (e: IOException) {
            -1
        }
        return result
    }

    /**
     * 获取设备mem 未使用大小 单位B
     *
     * @return
     */
    @JvmStatic
    fun getMemFreeSize(): Long {
        var result: Long
        result = try {
            val process = ProcessBuilder("/system/bin/cat", "/proc/meminfo").start()
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream, CHARSET))
            bufferedReader.readLine()
            val str = bufferedReader.readLine()
            val memStr = "MemFree:"
            val resultStr = str.substring(str.indexOf(memStr) + memStr.length, str.indexOf(" kB"))
            bufferedReader.close()
            java.lang.Long.parseLong(resultStr.trim { it <= ' ' }) * 1024
        } catch (e: IOException) {
            -1
        }
        return result
    }

    /**
     * 获取设备mem信息
     *
     * @return
     */
    @JvmStatic
    fun getMemInfo(): String {
        var result = ""
        try {
            val process = ProcessBuilder("/system/bin/cat", "/proc/meminfo").start()
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream, CHARSET))
            val sb = StringBuilder()
            var end = false
            while (!end) {
                var data = bufferedReader.readLine()
                end = if (data != null) {
                    sb.append("\n" + data)
                    false
                } else {
                    true
                }
            }
            bufferedReader.close()
            result = sb.toString()
        } catch (e: IOException) {
            Log.d(e.message)
        }

        return result
    }

    /**
     * 获取设备cpu信息
     *
     * @return
     */
    @JvmStatic
    fun getCPUInfo(): String {
        var result = ""
        try {
            val process = ProcessBuilder("/system/bin/cat", "/proc/cpuinfo").start()
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream, CHARSET))
            val str = bufferedReader.readLine()
            val title = "Processor\t: "
            result = str.substring(str.indexOf(title) + title.length)
            bufferedReader.close()
            return result
        } catch (e: IOException) {
            Log.d(e.message)
        }
        return result
    }

    /**
     * 获取CPU架构
     *
     * @return
     */
    @JvmStatic
    fun getCPUABI(): String {
        return Build.CPU_ABI
    }

    private const val NETWORK_TYPE_UNAVAILABLE = -1
    private const val NETWORK_TYPE_WIFI = -101
    private const val NETWORK_CLASS_WIFI = -101
    private const val NETWORK_CLASS_UNAVAILABLE = -1
    private const val NETWORK_CLASS_2G = 1
    private const val NETWORK_CLASS_3G = 2
    private const val NETWORK_CLASS_4G = 3
    private const val NETWORK_CLASS_UNKNOWN = 0

    /**
     * 获取设备Locale信息
     *
     * @return
     */
    @JvmStatic
    fun getLocale(): String {
        val locale = Locale.getDefault()
        return locale.language + "_" + locale.country
    }

    /**
     * 获取设备语言
     *
     * @return
     */
    @JvmStatic
    fun getLanguage(): String {
        return Locale.getDefault().language
    }

    /**
     * 获取设备国家
     *
     * @return
     */
    @JvmStatic
    fun getCountry(): String {
        return Locale.getDefault().country
    }

    /**
     * 获取系统文件编码类型
     *
     * @return
     */
    @JvmStatic
    fun getCharset(): String? {
        return System.getProperty("file.encoding")
    }


    @JvmStatic
    fun getSerialNumber(): String {
        return Build.SERIAL
    }

    /**
     * 获取设备分辨率
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getResolution(context: Context): String {
        val dm = DisplayMetrics()
        val wm = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealMetrics(dm)
        } else {
            wm.defaultDisplay.getMetrics(dm)
        }
        return dm.widthPixels.toString() + "x" + dm.heightPixels
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", ANDROID)
        return if (resourceId > 0)
            context.resources.getDimensionPixelSize(resourceId)
        else
            Math.ceil(((if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 24 else 25) * context.resources.displayMetrics.density).toDouble()).toInt()
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getNavigationBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", ANDROID)
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    @JvmStatic
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        return context.resources.displayMetrics
    }

    /**
     * 获取设备Density
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }

    /**
     * 获取设备Density
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getDensityDpi(context: Context): Float {
        return context.resources.displayMetrics.densityDpi.toFloat()
    }

    /**
     * 获取设备Densitydpi 类型
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getDensityDpiStr(context: Context): String {
        return when (context.resources.displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "LDPI"
            DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
            DisplayMetrics.DENSITY_TV -> "TVDPI"
            DisplayMetrics.DENSITY_HIGH -> "HDPI"
            DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
            DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
            DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
            else -> ""
        }
    }

    /**
     * 获取屏幕物理尺寸 单位英寸
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getScreenSize(context: Context): String {
        val dm = context.resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val x = Math.pow(width.toDouble(), 2.0)
        val y = Math.pow(height.toDouble(), 2.0)
        val diagonal = Math.sqrt(x + y)

        val dens = dm.densityDpi
        val screenInches = diagonal / dens.toDouble()
        return String.format("%.2f", screenInches)
    }

    /**
     * 获取设备运营商
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getNetworkOperatorName(context: Context): String {
        var provider = ""
        try {
            val manager = context
                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            provider = manager.networkOperatorName
        } catch (e: Exception) {
            Log.e("getNetworkOperatorName", "" + e.message, e)
        }

        return provider
    }

    @JvmStatic
    fun getNetworkOperator(context: Context): String {
        var provider = ""
        try {
            val manager = context
                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            provider = manager.networkOperator
        } catch (e: Exception) {
            Log.e("getNetworkOperatorName", "" + e.message, e)
        }

        return provider
    }

    @JvmStatic
    fun getNetworkType(context: Context): Int {
        var networkType = TelephonyManager.NETWORK_TYPE_UNKNOWN
        try {
            val network = (context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                    .activeNetworkInfo
            if (network != null && network.isAvailable && network.isConnected) {
                val type = network.type
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    val telephonyManager = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    networkType = telephonyManager.networkType
                }
            } else {
                networkType = NETWORK_TYPE_UNAVAILABLE
            }
        } catch (e: Exception) {
            Log.e("getNetworkType", "" + e.message, e)
        }

        return networkType
    }

    @JvmStatic
    fun getNetworkClass(context: Context): Int {
        return when (val networkType = getNetworkType(context)) {
            NETWORK_TYPE_WIFI -> NETWORK_CLASS_WIFI
            NETWORK_TYPE_UNAVAILABLE -> NETWORK_CLASS_UNAVAILABLE
            else -> when (networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NETWORK_CLASS_2G
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NETWORK_CLASS_3G
                TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_CLASS_4G
                else -> NETWORK_CLASS_UNKNOWN
            }
        }
    }

    @JvmStatic
    fun getNetworkClassName(context: Context): String {
        return when (val networkType = getNetworkType(context)) {
            NETWORK_TYPE_WIFI -> "WIFI"
            NETWORK_TYPE_UNAVAILABLE -> "UNAVAILABLE"
            else -> when (networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
                TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                else -> UNKNOWN
            }
        }
    }

    @JvmStatic
    fun getWifiRssi(context: Context): Int {
        var asu = 85
        try {
            val network = (context.applicationContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                    .activeNetworkInfo
            if (network != null && network.isAvailable && network.isConnected) {
                val type = network.type
                if (type == ConnectivityManager.TYPE_WIFI) {
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

                    val wifiInfo = wifiManager.connectionInfo
                    if (wifiInfo != null) {
                        asu = wifiInfo.rssi
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("getWifiRssi", "" + e.message, e)
        }

        return asu
    }

    @JvmStatic
    fun getWifiRssiString(context: Context): String {
        return getWifiRssi(context).toString() + "dBm"
    }

    /**
     * 获取app版本号
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getAppVersion(context: Context): String {
        var result: String? = null
        try {
            result = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: NameNotFoundException) {
            Log.e(e.message)
        }
        return result ?: UNKNOWN
    }

    /**
     * 获取app版本号
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getAppVersionCode(context: Context): Int {
        var result = -1
        try {
            result = context.packageManager.getPackageInfo(
                    context.packageName, 0).versionCode
        } catch (e: NameNotFoundException) {
            Log.e(e.message)
        }

        return result
    }

    /**
     * 获取Meta数据
     *
     * @param context
     * @param key
     * @return
     */
    @JvmStatic
    fun getAppMetaData(context: Context, key: String): Any? {
        var data: Any? = null
        val appInfo: ApplicationInfo
        try {
            appInfo = context.packageManager.getApplicationInfo(
                    context.packageName, PackageManager.GET_META_DATA)
            if (appInfo?.metaData != null)
                data = appInfo.metaData.get(key)
        } catch (e: NameNotFoundException) {
            Log.e(e.message)
        }

        return data
    }

    /**
     * 获取StringMeta数据
     *
     * @param context
     * @param key
     * @return
     */
    @JvmStatic
    fun getAppStringMetaData(context: Context, key: String): String? {
        var data: String? = null
        val appInfo: ApplicationInfo
        try {
            appInfo = context.packageManager.getApplicationInfo(
                    context.packageName, PackageManager.GET_META_DATA)
            if (appInfo.metaData != null) {
                data = appInfo.metaData.getString(key)
            }
        } catch (e: NameNotFoundException) {
            Log.d(e.message)
        }

        return data
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getMacAddress(context: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val all = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (!nif.name.equals("wlan0", ignoreCase = true)) continue

                    val macBytes = nif.hardwareAddress ?: return ""

                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X:", b))
                    }

                    if (res1.isNotEmpty()) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (e: Exception) {
                Log.e(e.message)
            }

            return "02:00:00:00:00:00"
        } else {
            val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return manager.connectionInfo.macAddress
        }
    }

    /**
     * 获取IP地址
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getIpAddress(context: Context): Int {
        var ipAddress = 0
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        if (wifiInfo == null) {
            return ipAddress
        } else {
            ipAddress = wifiInfo.ipAddress
        }
        return ipAddress
    }

    /**
     * 获取IP地址(%d.%d.%d.%d)
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getIpStr(context: Context): String {
        val ipAddress = getIpAddress(context)
        return String.format("%d.%d.%d.%d", ipAddress and 0xff,
                ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff,
                ipAddress shr 24 and 0xff)
    }

    /**
     * 获取设备openUDID
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getOpenUDID(context: Context): String? {
        var openUDID: String? = null
        var clazz: Class<*>? = try {
            Class.forName("org.OpenUDID.OpenUDID_manager")
        } catch (e: ClassNotFoundException) {
            try {
                Class.forName("org.openudid.OpenUDIDManager")
            } catch (e1: ClassNotFoundException) {
                return null
            }

        }

        var method: Method?
        try {
            method = clazz!!.getDeclaredMethod("getOpenUDID")
            var obj: Any? = method!!.invoke(null)
            if (obj != null) {
                openUDID = obj.toString()
            } else {
                syncOpenUDID(context)
                obj = method.invoke(null)
                openUDID = obj.toString()
            }
        } catch (e: InvocationTargetException) {
            Log.d("InvocationTargetException", e.toString())
        } catch (e: NoSuchMethodException) {
            Log.d("NoSuchMethodException", e.toString())
        } catch (e: IllegalAccessException) {
            Log.d("IllegalAccessException", e.toString())
        }

        return openUDID
    }

    @JvmStatic
    fun syncOpenUDID(context: Context) {
        var clazz: Class<*>? = try {
            Class.forName("org.OpenUDID.OpenUDID_manager")
        } catch (e: ClassNotFoundException) {
            try {
                Class.forName("org.openudid.OpenUDIDManager")
            } catch (e1: ClassNotFoundException) {
                return
            }
        }

        try {
            val method = clazz!!.getDeclaredMethod("sync", Context::class.java)
            method.invoke(null, context)
        } catch (e: InvocationTargetException) {
            Log.d("InvocationTargetException", e.toString())
        } catch (e: NoSuchMethodException) {
            Log.d("NoSuchMethodException", e.toString())
        } catch (e: IllegalAccessException) {
            Log.d("IllegalAccessException", e.toString())
        }

    }

    @JvmStatic
    fun getDeviceId(context: Context): String {
        var did = ""
        val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = manager.connectionInfo
        val macAddress = wifiInfo.macAddress
        Log.i("macAddress did:" + macAddress!!)
        if (null != macAddress && SPECIAL_MAC != macAddress) {
            did = macAddress.replace(".", "").replace(":", "")
                    .replace("-", "").replace("_", "")
            Log.i("macAddress did:$did")
        } else {
            val tm = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var imei: String?= try {
                tm.deviceId
            } catch (e: SecurityException) {
                null
            }

            // no sim: sdk|any pad
            if (null != imei && SPECIAL_IMEI != imei) {
                did = imei
                Log.i("imei did:$did")
            } else {
                val deviceId = Secure.getString(context.applicationContext.contentResolver, Secure.ANDROID_ID)
                // sdk: android_id
                if (null != deviceId && SPECIAL_ANDROID_ID != deviceId) {
                    did = deviceId
                    Log.i("ANDROID_ID did:$did")
                } else {
                    val sp = context.getSharedPreferences(DeviceInfo::class.java.simpleName, Context.MODE_PRIVATE)
                    var uid = sp.getString("uid", null)
                    if (null == uid) {
                        val editor = sp.edit()
                        uid = UUID.randomUUID().toString().replace("-", "")
                        editor.putString("uid", uid)
                        editor.apply()
                        Log.i("uid did:$did")
                    }

                    did = uid
                }
            }
        }
        return did
    }

    /**
     * 判读WIFI网络是否连接
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun isWifiConnection(context: Context): Boolean {
        val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return networkInfo?.isConnected?:false
    }

    /**
     * 判读网络是否连接
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun isConnection(context: Context): Boolean {
        val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting?:false
    }

    /**
     * 判断GPS定位是否开启
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun isGPSLocation(context: Context): Boolean {
        val locationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * 判断网络定位是否开启
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun isNetworkLocation(context: Context): Boolean {
        val locationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * 返回签名证书MD5值
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getMD5Fingerprint(context: Context): String? {
        val info: PackageInfo
        try {
            info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            return StringUtils.md5(info.signatures[0].toByteArray())
        } catch (e1: NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }

        return null
    }

    /**
     * 返回签名证书SHA1值
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getSHA1Fingerprint(context: Context): String? {
        val info: PackageInfo
        try {
            info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            val cert = info.signatures[0].toByteArray()
            val md = MessageDigest.getInstance("SHA1")

            val input = ByteArrayInputStream(cert)
            //证书工厂类，这个类实现了出厂合格证算法的功能
            val cf = CertificateFactory.getInstance("X509")
            val c = cf.generateCertificate(input) as X509Certificate
            //获得公钥
            val publicKey = md.digest(c.encoded)
            //字节到十六进制的格式转换
            val hexArray = "0123456789ABCDEF".toCharArray()
            val hexChars = CharArray(publicKey.size * 2)
            for (j in publicKey.indices) {
                val v = publicKey[j].toInt() and 0xFF
                hexChars[j * 2] = hexArray[v.ushr(4)]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        } catch (e: NameNotFoundException) {
            Log.e("NameNotFoundException", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("NoSuchAlgorithmException", e.toString())
        } catch (e: CertificateEncodingException) {
            Log.e("CertificateEncodingException", e.toString())
        } catch (e: CertificateException) {
            Log.e("CertificateException", e.toString())
        }

        return null
    }

    /**
     * return activity is foreground
     *
     * @param activityName
     * @param context
     * @return
     */
    @JvmStatic
    fun isForegroundActivity(activityName: String, context: Context?): Boolean {
        if (context == null || TextUtils.isEmpty(activityName)) {
            return false
        }
        val am = context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = am.getRunningTasks(1)
        if (list != null && list.isNotEmpty()) {
            val cpn = list[0].topActivity
            return activityName == cpn.className
        }
        return false
    }

    /**
     * return applcation is foreground
     *
     * @param packageName
     * @param context
     * @return
     */
    @JvmStatic
    fun isForegroundApplication(packageName: String, context: Context): Boolean {
        var result = false
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = am.runningAppProcesses
        if (appProcesses != null) {
            for (runningAppProcessInfo in appProcesses) {
                if (runningAppProcessInfo.processName == packageName) {
                    val status = runningAppProcessInfo.importance
                    if (status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE || status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        result = true
                    }
                }
            }
        }
        return result
    }

    /**
     * 获取当前进程name
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getCurProcessName(context: Context): String? {

        val pid = android.os.Process.myPid()

        val activityManager = context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (appProcess in activityManager
                .runningAppProcesses) {

            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }

    /**
     * 判断设备 是否使用代理上网
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun isProxy(context: Context): Boolean {
        val IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
        val proxyAddress: String?
        val proxyPort: Int
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost")
            val portStr = System.getProperty("http.proxyPort")
            proxyPort = Integer.parseInt(portStr ?: "-1")
        } else {
            proxyAddress = android.net.Proxy.getHost(context)
            proxyPort = android.net.Proxy.getPort(context)
        }
        return !TextUtils.isEmpty(proxyAddress) && proxyPort != -1
    }

    /**
     * 是否是app当前进程
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun isAppProcess(context: Context): Boolean {
        return context.applicationInfo.packageName == getCurProcessName(context.applicationContext)
    }

    /**
     * 检测是否具有底部导航栏
     *
     * @return
     */
    @JvmStatic
    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val windowManager = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val realDisplayMetrics = DisplayMetrics()
            display.getRealMetrics(realDisplayMetrics)
            val realHeight = realDisplayMetrics.heightPixels
            val realWidth = realDisplayMetrics.widthPixels
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            val displayHeight = displayMetrics.heightPixels
            val displayWidth = displayMetrics.widthPixels
            return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
        } else {
            var hasNavigationBar = false
            val resources = context.resources
            val id = resources.getIdentifier("config_showNavigationBar", "bool", ANDROID)
            if (id > 0) {
                hasNavigationBar = resources.getBoolean(id)
            }
            try {
                val systemPropertiesClass = Class.forName("android.os.SystemProperties")
                val m = systemPropertiesClass.getMethod("get", String::class.java)
                val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
                if ("1" == navBarOverride) {
                    hasNavigationBar = false
                } else if ("0" == navBarOverride) {
                    hasNavigationBar = true
                }
            } catch (e: Exception) {
                Log.d(e.message)
            }

            return hasNavigationBar
        }
    }
}
