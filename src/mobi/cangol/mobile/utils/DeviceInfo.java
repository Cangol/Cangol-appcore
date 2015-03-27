/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import mobi.cangol.mobile.logging.Log;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeviceInfo {
	public static final String SPECIAL_IMEI="000000000000000";
	public static final String SPECIAL_ANDROID_ID="9774d56d682e549c";

	/**
	 * 获取操作系统类型
	 * @return
	 */
	public static String getOS() {
		return "Android";
	}
	/**
	 * 获取操作系统版本号
	 * @return
	 */
	public static String getOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}
	/**
	 * 获取设备型号
	 * @return
	 */
	public static String getDeviceModel() {
		return android.os.Build.MODEL;
	}
	/**
	 * 获取设备品牌
	 * @return
	 */
	public static String getDeviceBrand() {
		return android.os.Build.BRAND;
	}
	/**
	 * 获取设备信息
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
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	/**
	 * 获取设备cpu信息
	 * @return
	 */
	public static String getCPUInfo() {
		try
		{
			byte[] bs = new byte[1024];
			RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
			reader.read(bs);
			String ret = new String(bs);
			int index = ret.indexOf(0);
			if(index != -1) {
				return ret.substring(0, index);
			} else {
				return ret;
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 获取设备分辨率
	 * @param context
	 * @return
	 */
	public static String getResolution(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		Display display = wm.getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		return metrics.heightPixels + "x" + metrics.widthPixels;
	}
	/**
	 * 获取状态栏高度
	 * @param activity
	 * @return
	 */
	public static int getStatusBarHeight(Activity activity){
		Rect frame = new Rect();  
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		return frame.top; 
	}
	public static DisplayMetrics getDisplayMetrics(Context context) {
		return context.getResources().getDisplayMetrics();
	}
	/**
	 * 获取设备Density
	 * @param context
	 * @return
	 */
	public static float getFloatDensity(Context context) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics(); 
		return displayMetrics.density;
	}
	/**
	 * 获取设备Density类型
	 * @param context
	 * @return
	 */
    public static String getDensity(Context context) {
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
     * 获取设备运营商
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
     * @return
     */
	public static String getLocale() {
		Locale locale = Locale.getDefault();
		return locale.getLanguage() + "_" + locale.getCountry();
	}
	/**
     * 获取设备语言
     * @return
     */
	public static String getLanguage() {
		Locale locale = Locale.getDefault();
		return locale.getLanguage();
	}
	/**
     * 获取设备国家
     * @return
     */
	public static String getCountry() {
		Locale locale = Locale.getDefault();
		return locale.getCountry();
	}
	/**
	 * 获取app版本号
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
	 * 获取Meta数据
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
	 * 获取mac地址
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
	 * @param context
	 * @return
	 */
	public static int getIpAddress(Context context) {
		int ipAddress = 0;
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
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
	 * @return
	 */
	public static String getCharset() {
		return System.getProperty("file.encoding");
	}
	public static String getDeviceId(Context context) {
		String did="";
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		String macAddress = wifiInfo.getMacAddress();
		if (null != macAddress) {
			did=macAddress.replace(".", "").replace(":", "")
					.replace("-", "").replace("_", "");
		} else {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			// no sim: sdk|any pad
			if (null != imei && !SPECIAL_IMEI.equals(imei)) {
				did=imei;
			} else {
				String deviceId = Secure.getString(context.getContentResolver(),
						Secure.ANDROID_ID);
				// sdk: android_id
				if (null != deviceId
						&& !SPECIAL_ANDROID_ID.equals(deviceId)) {
					did=deviceId;
				} else {
					SharedPreferences sp = context.getSharedPreferences(DeviceInfo.class.getSimpleName(), Context.MODE_PRIVATE);
					String uid = sp.getString("uid", null);
					if (null == uid) {
						SharedPreferences.Editor editor = sp.edit();
						uid = UUID.randomUUID().toString().replace("-", "");
						editor.putString("uid", uid);
						editor.commit();
					}
		
					did=uid;
				}
			}
		}
		return did;
	}
	/**
	 * 获取网络类型
	 * @param context
	 * @return
	 */
	public static String getNetworkType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
	    if(networkInfo!=null){
	    	return networkInfo.getTypeName();
	    }else{
	    	return "UNKNOWN";
	    }
	}
	/**
	 * 判读WIFI网络是否连接
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnection(Context context){
		 final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	     final NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	     if(networkInfo!=null&&networkInfo.isConnected()){
	    	 return true;
	     }
	     return false;
	}
	/**
	 * 判读网络是否连接
	 * @param context
	 * @return
	 */
	public static boolean isConnection(Context context){
		 final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	     final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	     if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
	        	return false;
	      }
	     return true;
	}
	/**
	 * 判断GPS定位是否开启
	 * @param context
	 * @return
	 */
	public static boolean isGPSLocation(Context context){
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	/**
	 * 判断网络定位是否开启
	 * @param context
	 * @return
	 */
	public static boolean isNetworkLocation(Context context){
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	/**
	 * 返回签名证书MD5值
	 * @param context
	 * @return
	 */
	public static String getMD5Fingerprint(Context context) {  
	    PackageInfo info;
	    try {
	        info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	        	String something=StringUtils.md5(signature.toByteArray());
	            return something;
	        }
	    } catch (NameNotFoundException e1) {
	        Log.e("name not found", e1.toString());
	    } catch (Exception e) {
	        Log.e("exception", e.toString());
	    }
		return null;
	}
	/**
	 * 返回签名证书SHA1值
	 * @param context
	 * @return
	 */
	public static String getSHA1Fingerprint(Context context) {  
	    PackageInfo info;
	    try {
	        info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	        	 MessageDigest md=MessageDigest.getInstance("SHA1");
		         md.update(signature.toByteArray());
		         String something = new String(md.digest());
		         return something;
	        }
	    } catch (NameNotFoundException e1) {
	        Log.e("name not found", e1.toString());
	    } catch (Exception e) {
	        Log.e("exception", e.toString());
	    }
		return null;
	}
	/**
	* return application is background
	* @param context
	* @return
	*/
	public static boolean isBackground(Context context) {
	
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
}
