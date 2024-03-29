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
package mobi.cangol.mobile.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import mobi.cangol.mobile.logging.Log;

/**
 * 设备信息获取工具类
 */
@SuppressLint("MissingPermission")
public final class DeviceInfo {
    /**
     * 默认IMEI
     */
    private static final String SPECIAL_IMEI = "000000000000000";
    /**
     * 默认ANDROID_ID
     */
    private static final String SPECIAL_ANDROID_ID = "9774d56d682e549c";

    /**
     * 默认MAC(小米)
     */
    private static final String SPECIAL_MAC = "02:00:00:00:00:00";
    /**
     * 默认CHARSET
     */
    private static final String CHARSET = "UTF-8";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String ANDROID = "android";

    private DeviceInfo() {
    }

    /**
     * 获取操作系统类型
     *
     * @return
     */
    public static String getOS() {
        return "Android";
    }

    /**
     * 获取操作系统版本号
     *
     * @return
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取设备品牌
     *
     * @return
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取设备信息
     *
     * @return
     */
    public static String getMobileInfo() {
        final StringBuilder sb = new StringBuilder();
        try {

            final Field[] fields = Build.class.getDeclaredFields();
            for (final Field field : fields) {
                field.setAccessible(true);
                final String name = field.getName();
                final String value = field.get(null).toString();
                sb.append(name)
                        .append('=')
                        .append(value)
                        .append('\n');
            }
        } catch (Exception e) {
            Log.d(e.getMessage());
        }
        return sb.toString();
    }

    /**
     * 获取设备mem 总大小 单位B
     *
     * @return
     */
    public static long getMemTotalSize() {
        long result = 0;
        try {
            final Process process = new ProcessBuilder("/system/bin/cat", "/proc/meminfo").start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), CHARSET));
            String str = bufferedReader.readLine();
            final String memStr = "MemTotal:";
            final String resultStr = str.substring(str.indexOf(memStr) + memStr.length(), str.indexOf(" kB"));
            bufferedReader.close();
            result = Long.parseLong(resultStr.trim()) * 1024;
        } catch (IOException e) {
            result = -1;
        }
        return result;
    }

    /**
     * 获取设备mem 未使用大小 单位B
     *
     * @return
     */
    public static long getMemFreeSize() {
        long result = 0;
        try {
            final Process process = new ProcessBuilder("/system/bin/cat", "/proc/meminfo").start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), CHARSET));
            bufferedReader.readLine();
            final String str = bufferedReader.readLine();
            final String memStr = "MemFree:";
            final String resultStr = str.substring(str.indexOf(memStr) + memStr.length(), str.indexOf(" kB"));
            bufferedReader.close();
            result = Long.parseLong(resultStr.trim()) * 1024;
        } catch (IOException e) {
            result = -1;
        }
        return result;
    }

    /**
     * 获取设备mem信息
     *
     * @return
     */
    public static String getMemInfo() {
        String result = "";
        try {
            final Process process = new ProcessBuilder("/system/bin/cat", "/proc/meminfo").start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), CHARSET));
            String data = null;
            final StringBuilder sb = new StringBuilder();
            while ((data = bufferedReader.readLine()) != null) {
                sb.append("\n" + data);
            }
            bufferedReader.close();
            result = sb.toString();
        } catch (IOException e) {
            Log.d(e.getMessage());
        }
        return result;
    }

    /**
     * 获取设备cpu信息
     *
     * @return
     */
    public static String getCPUInfo() {
        String result = "";
        try {
            final  Process process = new ProcessBuilder("/system/bin/cat", "/proc/cpuinfo").start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), CHARSET));
            final String str = bufferedReader.readLine();
            final String title = "Processor\t: ";
            result = str.substring(str.indexOf(title) + title.length());
            bufferedReader.close();
            return result;
        } catch (IOException e) {
            Log.d(e.getMessage());
        }
        return result;
    }

    /**
     * 获取CPU架构
     *
     * @return
     */
    public static String getCPUABI() {
        return android.os.Build.CPU_ABI;
    }

    /**
     * 获取设备分辨率
     *
     * @param context
     * @return
     */
    public static String getResolution(Context context) {
        final DisplayMetrics dm = new DisplayMetrics();
        final  WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealMetrics(dm);
        } else {
            wm.getDefaultDisplay().getMetrics(dm);
        }
        return dm.widthPixels + "x" + dm.heightPixels;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        final int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", ANDROID);
        if (resourceId > 0)
            return context.getResources().getDimensionPixelSize(resourceId);
        else
            return (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * context.getResources().getDisplayMetrics().density);
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        final int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", ANDROID);
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取设备Density
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取设备Density
     *
     * @param context
     * @return
     */
    public static float getDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * 获取设备Densitydpi 类型
     *
     * @param context
     * @return
     */
    public static String getDensityDpiStr(Context context) {
        switch (context.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "LDPI";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "MDPI";
            case DisplayMetrics.DENSITY_TV:
                return "TVDPI";
            case DisplayMetrics.DENSITY_HIGH:
                return "HDPI";
            case DisplayMetrics.DENSITY_XHIGH:
                return "XHDPI";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "XXHDPI";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "XXXHDPI";
            default:
                return "";
        }
    }

    /**
     * 获取屏幕物理尺寸 单位英寸
     *
     * @param context
     * @return
     */
    public static String getScreenSize(Context context) {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        final int width = dm.widthPixels;
        final int height = dm.heightPixels;
        final double x = Math.pow(width, 2);
        final double y = Math.pow(height, 2);
        final double diagonal = Math.sqrt(x + y);

        final int dens = dm.densityDpi;
        final double screenInches = diagonal / (double) dens;
        return String.format("%.2f", screenInches);
    }

    /**
     * 获取设备运营商
     *
     * @param context
     * @return
     */
    public static String getNetworkOperatorName(Context context) {
        String provider = "";
        try {
            final TelephonyManager manager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            provider = manager.getNetworkOperatorName();
        } catch (Exception e) {
            Log.e("getNetworkOperatorName", "" + e.getMessage(), e);
        }
        return provider;
    }
    public static String getNetworkOperator(Context context) {
        String provider = "";
        try {
            final TelephonyManager manager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            provider = manager.getNetworkOperator();
        } catch (Exception e) {
            Log.e("getNetworkOperatorName", "" + e.getMessage(), e);
        }
        return provider;
    }
    public static final int NETWORK_TYPE_UNAVAILABLE = -1;
    public static final int NETWORK_TYPE_WIFI = -101;

    public static int getNetworkType(Context context) {
        int networkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
        try {
            final NetworkInfo network = ((ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable() && network.isConnected()) {
                final int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    final TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    networkType = telephonyManager.getNetworkType();
                }
            } else {
                networkType = NETWORK_TYPE_UNAVAILABLE;
            }
        } catch (Exception e) {
            Log.e("getNetworkType", "" + e.getMessage(), e);
        }

        return networkType;
    }
    public static final int NETWORK_CLASS_WIFI = -101;
    public static final int NETWORK_CLASS_UNAVAILABLE = -1;
    public static final int NETWORK_CLASS_2G = 1;
    public static final int NETWORK_CLASS_3G = 2;
    public static final int NETWORK_CLASS_4G = 3;
    public static final int NETWORK_CLASS_UNKNOWN=0;
    public static int getNetworkClass(Context context) {
        int networkType= getNetworkType(context);
        if (networkType == NETWORK_TYPE_WIFI) {
            return NETWORK_CLASS_WIFI;
        } else if (networkType == NETWORK_TYPE_UNAVAILABLE) {
            return NETWORK_CLASS_UNAVAILABLE;
        }else{
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NETWORK_CLASS_2G;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NETWORK_CLASS_3G;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NETWORK_CLASS_4G;
                default:
                    return NETWORK_CLASS_UNKNOWN;
            }
        }
    }
    public static String getNetworkClassName(Context context) {
        int networkType= getNetworkType(context);
        if (networkType == NETWORK_TYPE_WIFI) {
            return "WIFI";
        } else if (networkType == NETWORK_TYPE_UNAVAILABLE) {
            return "UNAVAILABLE";
        }else{
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G";
                default:
                    return UNKNOWN;
            }
        }
    }

    public static int getWifiRssi(Context context) {
        int asu = 85;
        try {
            final NetworkInfo network = ((ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable() && network.isConnected()) {
                final int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        asu = wifiInfo.getRssi();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("getWifiRssi", "" + e.getMessage(), e);
        }
        return asu;
    }

    public static String getWifiRssiString(Context context) {
        return getWifiRssi(context) + "dBm";
    }

    /**
     * 获取设备Locale信息
     *
     * @return
     */
    public static String getLocale() {
        final Locale locale = Locale.getDefault();
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    /**
     * 获取设备语言
     *
     * @return
     */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取设备国家
     *
     * @return
     */
    public static String getCountry() {
        return Locale.getDefault().getCountry();
    }

    /**
     * 获取app版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        String result = UNKNOWN;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            result = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            Log.e(e.getMessage());
        }
        return result;
    }

    /**
     * 获取app版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int result = -1;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                result = (int) packageInfo.getLongVersionCode();
            } else {
                result = packageInfo.versionCode;
            }
        } catch (NameNotFoundException e) {
            Log.e(e.getMessage());
        }
        return result;
    }

    /**
     * 获取Meta数据
     *
     * @param context
     * @param key
     * @return
     */
    public static Object getAppMetaData(Context context, String key) {
        Object data = null;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getApplicationContext().getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            data = appInfo.metaData.get(key);
        } catch (NameNotFoundException e) {
            Log.e(e.getMessage());
        }
        return data;
    }

    /**
     * 获取StringMeta数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getAppStringMetaData(Context context, String key) {
        String data = null;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getApplicationContext().getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                data = appInfo.metaData.getString(key);
            }
        } catch (NameNotFoundException e) {
            Log.d(e.getMessage());
        }
        return data;
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getMacAddress(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                final List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (final NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                    final byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    final StringBuilder res1 = new StringBuilder();
                    for (final byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            } catch (Exception e) {
                Log.e(e.getMessage());
            }
            return "02:00:00:00:00:00";
        } else {
            final WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return manager.getConnectionInfo().getMacAddress();
        }
    }

    /**
     * 获取IP地址
     *
     * @param context
     * @return
     */
    public static int getIpAddress(Context context) {
        int ipAddress = 0;
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null || wifiInfo.equals("")) {
            return ipAddress;
        } else {
            ipAddress = wifiInfo.getIpAddress();
        }
        return ipAddress;
    }

    /**
     * 获取IP地址(%d.%d.%d.%d)
     *
     * @param context
     * @return
     */
    public static String getIpStr(Context context) {
        final int ipAddress = getIpAddress(context);
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }

    /**
     * 获取系统文件编码类型
     *
     * @return
     */
    public static String getCharset() {
        return System.getProperty("file.encoding");
    }

    public static String getSerialNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        }else{
            return  android.os.Build.SERIAL;
        }
    }

    public static String getDeviceId(Context context) {
        String did = "";
        final  WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = manager.getConnectionInfo();
        final String macAddress = wifiInfo.getMacAddress();
        Log.i("macAddress did:" + macAddress);
        if (null != macAddress && !SPECIAL_MAC.equals(macAddress)) {
            did = macAddress.replace(".", "").replace(":", "")
                    .replace("-", "").replace("_", "");
            Log.i("macAddress did:" + did);
        } else {
            final TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String imei = null;
            try {
                imei = tm.getDeviceId();
            } catch (SecurityException e) {
                //
            }
            // no sim: sdk|any pad
            if (null != imei && !SPECIAL_IMEI.equals(imei)) {
                did = imei;
                Log.i("imei did:" + did);
            } else {
                final String deviceId = Secure.getString(context.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
                // sdk: android_id
                if (null != deviceId
                        && !SPECIAL_ANDROID_ID.equals(deviceId)) {
                    did = deviceId;
                    Log.i("ANDROID_ID did:" + did);
                } else {
                    final SharedPreferences sp = context.getSharedPreferences(DeviceInfo.class.getSimpleName(), Context.MODE_PRIVATE);
                    String uid = sp.getString("uid", null);
                    if (null == uid) {
                        final SharedPreferences.Editor editor = sp.edit();
                        uid = UUID.randomUUID().toString().replace("-", "");
                        editor.putString("uid", uid);
                        editor.apply();
                        Log.i("uid did:" + did);
                    }

                    did = uid;
                }
            }
        }
        return did;
    }

    /**
     * 判读WIFI网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnection(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 判读网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnection(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * 判断GPS定位是否开启
     *
     * @param context
     * @return
     */
    public static boolean isGPSLocation(Context context) {
        final LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断网络定位是否开启
     *
     * @param context
     * @return
     */
    public static boolean isNetworkLocation(Context context) {
        final  LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * 返回签名证书MD5值
     *
     * @param context
     * @return
     */
    public static String getMD5Fingerprint(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            return StringUtils.md5(info.signatures[0].toByteArray());
        } catch (NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        return null;
    }

    /**
     * 返回签名证书SHA1值
     *
     * @param context
     * @return
     */
    public static String getSHA1Fingerprint(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            final byte[] cert = info.signatures[0].toByteArray();
            final MessageDigest md = MessageDigest.getInstance("SHA1");

            final InputStream input = new ByteArrayInputStream(cert);
            //证书工厂类，这个类实现了出厂合格证算法的功能
            final  CertificateFactory cf = CertificateFactory.getInstance("X509");
            final X509Certificate c = (X509Certificate) cf.generateCertificate(input);
            //获得公钥
            final byte[] publicKey = md.digest(c.getEncoded());
            //字节到十六进制的格式转换
            final char[] hexArray = "0123456789ABCDEF".toCharArray();
            char[] hexChars = new char[publicKey.length * 2];
            for (int j = 0; j < publicKey.length; j++) {
                final int v = publicKey[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } catch (NameNotFoundException e) {
            Log.e("NameNotFoundException", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("NoSuchAlgorithmException", e.toString());
        } catch (CertificateEncodingException e) {
            Log.e("CertificateEncodingException", e.toString());
        } catch (CertificateException e) {
            Log.e("CertificateException", e.toString());
        }
        return null;
    }

    /**
     * return activity is foreground
     *
     * @param activityName
     * @param context
     * @return
     */
    public static boolean isForegroundActivity(String activityName, Context context) {
        if (context == null || TextUtils.isEmpty(activityName)) {
            return false;
        }
        final ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && !list.isEmpty()) {
            final ComponentName cpn = list.get(0).topActivity;
            return activityName.equals(cpn.getClassName());
        }
        return false;
    }

    /**
     * return applcation is foreground
     *
     * @param packageName
     * @param context
     * @return
     */
    public static boolean isForegroundApplication(String packageName, Context context) {
        final ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses != null) {
            for (final ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                if (runningAppProcessInfo.processName.equals(packageName)) {
                    final int status = runningAppProcessInfo.importance;
                    if (status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                            || status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            // App is foreground, but screen is locked, so show notification
            return km.inKeyguardRestrictedInputMode();
        }
        return false;
    }

    /**
     * 获取当前进程name
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {

        final int pid = android.os.Process.myPid();

        final ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (final ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 判断设备 是否使用代理上网
     *
     * @param context
     * @return
     */
    public static boolean isProxy(Context context) {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            final String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

    /**
     * 是否是app当前进程
     *
     * @param context
     * @return
     */
    public static boolean isAppProcess(Context context) {
        return context.getApplicationInfo().packageName.equals(getCurProcessName(context));
    }
    /**
     * 是否是app当前进程
     *
     * @param context
     * @return
     */
    public static boolean isAppProcessByFile(Context context) {
        return context.getApplicationInfo().packageName.equals(getCurProcessNameByFile());
    }

    public static String getCurProcessNameByFile() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "这里写你的App的进程名"; //TODO 有可能获取失败，需要写默认的进程名
        }
    }
    /**
     * 检测是否具有底部导航栏
     *
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
           final WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
           final Display display = windowManager.getDefaultDisplay();
            final DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            display.getRealMetrics(realDisplayMetrics);
            final int realHeight = realDisplayMetrics.heightPixels;
            final int realWidth = realDisplayMetrics.widthPixels;
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            final int displayHeight = displayMetrics.heightPixels;
            final int displayWidth = displayMetrics.widthPixels;
            return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else {
            boolean hasNavigationBar = false;
            final Resources resources = context.getResources();
            final int id = resources.getIdentifier("config_showNavigationBar", "bool", ANDROID);
            if (id > 0) {
                hasNavigationBar = resources.getBoolean(id);
            }
            try {
                final Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
                final Method m = systemPropertiesClass.getMethod("get", String.class);
                final String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
                if ("1".equals(navBarOverride)) {
                    hasNavigationBar = false;
                } else if ("0".equals(navBarOverride)) {
                    hasNavigationBar = true;
                }
            } catch (Exception e) {
                Log.d(e.getMessage());
            }
            return hasNavigationBar;
        }
    }
}
