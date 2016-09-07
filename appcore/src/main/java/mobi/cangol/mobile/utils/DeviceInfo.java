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

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.Rect;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import mobi.cangol.mobile.logging.Log;

public class DeviceInfo {
    public static final String SPECIAL_IMEI = "000000000000000";
    public static final String SPECIAL_ANDROID_ID = "9774d56d682e549c";
    public static final String CHARSET = "UTF-8";

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
        StringBuffer sb = new StringBuffer();
        try {

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
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
     * 获取设备mem 大小 单位B
     *
     * @return
     */
    public static long getMemSize() {
        long result = 0;
        try {
            Process process = new ProcessBuilder(new String[]{"/system/bin/cat", "/proc/meminfo"}).start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), CHARSET));
            String str = bufferedReader.readLine();
            String memStr = "MemTotal:";
            String resultStr = str.substring(str.indexOf(memStr) + memStr.length(), str.indexOf(" kB"));
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
            Process process = new ProcessBuilder(new String[]{"/system/bin/cat", "/proc/meminfo"}).start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), CHARSET));
            String str = bufferedReader.readLine();
            bufferedReader.close();
            String memStr = "MemTotal:";
            result = str.substring(str.indexOf(memStr) + memStr.length()).trim();
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
            Process process = new ProcessBuilder(new String[]{"/system/bin/cat", "/proc/cpuinfo"}).start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), CHARSET));
            String str = bufferedReader.readLine();
            String title = "Processor\t: ";
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
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec("getprop ro.product.cpu.abi");
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), CHARSET);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = bufferedReader.readLine();
            result = str.trim();
        } catch (Exception e) {
            result = "UNKNOWN";
        }
        return result;
    }

    /**
     * 获取设备分辨率
     *
     * @param context
     * @return
     */
    public static String getResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(point);
            return point.y + "x" + point.x;
        } else {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return dm.heightPixels + "x" + dm.widthPixels;
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
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
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    /**
     * 获取设备Density
     *
     * @param context
     * @return
     */
    public static float getDensityDpi(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.densityDpi;
    }

    /**
     * 获取设备Densitydpi 类型
     *
     * @param context
     * @return
     */
    public static String getDensityDpiStr(Context context) {
        int density = context.getResources().getDisplayMetrics().densityDpi;
        switch (density) {
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
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double x = Math.pow(width, 2);
        double y = Math.pow(height, 2);
        double diagonal = Math.sqrt(x + y);

        int dens = dm.densityDpi;
        double screenInches = diagonal / (double) dens;
        return String.format("%.2f", screenInches);
    }

    /**
     * 获取设备运营商
     *
     * @param context
     * @return
     */
    public static String getOperator(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    /**
     * 获取设备Locale信息
     *
     * @return
     */
    public static String getLocale() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    /**
     * 获取设备语言
     *
     * @return
     */
    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    /**
     * 获取设备国家
     *
     * @return
     */
    public static String getCountry() {
        Locale locale = Locale.getDefault();
        return locale.getCountry();
    }

    /**
     * 获取app版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        String result = "UNKNOWN";
        try {
            result = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
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
            result = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
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
            appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            data = appInfo.metaData.get(key);
        } catch (NameNotFoundException e) {
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
            appInfo = context.getPackageManager().getApplicationInfo(
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
    public static String getMacAddress(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        return macAddress;
    }

    /**
     * 获取IP地址
     *
     * @param context
     * @return
     */
    public static int getIpAddress(Context context) {
        int ipAddress = 0;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
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
        int ipAddress = getIpAddress(context);
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

    /**
     * 获取设备openUDID
     *
     * @param context
     * @return
     */
    public static String getOpenUDID(Context context) {
        String openUDID = null;
        Class clazz = null;
        try {
            clazz = Class.forName("org.OpenUDID.OpenUDID_manager");
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName("org.openudid.OpenUDIDManager");
            } catch (ClassNotFoundException e1) {
                return null;
            }
        }
        Method method = null;
        try {
            method = clazz.getDeclaredMethod("getOpenUDID");
            Object obj = method.invoke(null);
            if (obj != null) {
                openUDID = String.valueOf(obj);
            } else {
                syncOpenUDID(context);
                obj = method.invoke(null);
                openUDID = String.valueOf(obj);
            }
        } catch (InvocationTargetException e) {
            Log.d("InvocationTargetException", e.toString());
        } catch (NoSuchMethodException e) {
            Log.d("NoSuchMethodException", e.toString());
        } catch (IllegalAccessException e) {
            Log.d("IllegalAccessException", e.toString());
        }

        return openUDID;
    }

    public static void syncOpenUDID(Context context) {
        Class clazz = null;
        try {
            clazz = Class.forName("org.OpenUDID.OpenUDID_manager");
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName("org.openudid.OpenUDIDManager");
            } catch (ClassNotFoundException e1) {
                return;
            }
        }
        try {
            Method method = clazz.getDeclaredMethod("sync", Context.class);
            method.invoke(null, context);
        } catch (InvocationTargetException e) {
            Log.d("InvocationTargetException", e.toString());
        } catch (NoSuchMethodException e) {
            Log.d("NoSuchMethodException", e.toString());
        } catch (IllegalAccessException e) {
            Log.d("IllegalAccessException", e.toString());
        }
    }

    public static String getDeviceId(Context context) {
        String did = "";
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        if (null != macAddress) {
            did = macAddress.replace(".", "").replace(":", "")
                    .replace("-", "").replace("_", "");
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            // no sim: sdk|any pad
            if (null != imei && !SPECIAL_IMEI.equals(imei)) {
                did = imei;
            } else {
                String deviceId = Secure.getString(context.getContentResolver(),
                        Secure.ANDROID_ID);
                // sdk: android_id
                if (null != deviceId
                        && !SPECIAL_ANDROID_ID.equals(deviceId)) {
                    did = deviceId;
                } else {
                    SharedPreferences sp = context.getSharedPreferences(DeviceInfo.class.getSimpleName(), Context.MODE_PRIVATE);
                    String uid = sp.getString("uid", null);
                    if (null == uid) {
                        SharedPreferences.Editor editor = sp.edit();
                        uid = UUID.randomUUID().toString().replace("-", "");
                        editor.putString("uid", uid);
                        editor.commit();
                    }

                    did = uid;
                }
            }
        }
        return did;
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.getTypeName();
        } else {
            return "UNKNOWN";
        }
    }

    /**
     * 判读WIFI网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnection(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 判读网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnection(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            return false;
        }
        return true;
    }

    /**
     * 判断GPS定位是否开启
     *
     * @param context
     * @return
     */
    public static boolean isGPSLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断网络定位是否开启
     *
     * @param context
     * @return
     */
    public static boolean isNetworkLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");

            InputStream input = new ByteArrayInputStream(cert);
            //证书工厂类，这个类实现了出厂合格证算法的功能
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate c = (X509Certificate) cf.generateCertificate(input);
            //获得公钥
            byte[] publicKey = md.digest(c.getEncoded());
            //字节到十六进制的格式转换
            char[] hexArray = "0123456789ABCDEF".toCharArray();
            char[] hexChars = new char[publicKey.length * 2];
            for ( int j = 0; j < publicKey.length; j++ ) {
                int v = publicKey[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } catch (NameNotFoundException e) {
            Log.e("NameNotFoundException", e.toString());
        }catch (NoSuchAlgorithmException e) {
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
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (activityName.equals(cpn.getClassName())) {
                return true;
            }
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
        boolean result = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                if (runningAppProcessInfo.processName.equals(packageName)) {
                    int status = runningAppProcessInfo.importance;
                    if (status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                            || status == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取当前进程name
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 是否是app当前进程
     * @param context
     * @return
     */
    public static boolean isAppProcess(Context context) {
        if (context.getApplicationInfo().packageName.equals(getCurProcessName(context.getApplicationContext())) ) {
            return true;
        }
        return false;
    }
}
