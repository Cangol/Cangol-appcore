package com.cangol.mobile.stat;

import java.util.Locale;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeviceInfo {
	public static final String SPECIAL_IMEI="000000000000000";
	public static final String SPECIAL_ANDROID_ID="9774d56d682e549c";

	public static String getOS() {
		return "Android";
	}

	public static String getOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	public static String getDevice() {
		return android.os.Build.MODEL;
	}

	public static String getResolution(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		Display display = wm.getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		return metrics.heightPixels + "x" + metrics.widthPixels;
	}

	public static String getCarrier(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getNetworkOperatorName();
	}

	public static String getLocale() {
		Locale locale = Locale.getDefault();
		return locale.getLanguage() + "_" + locale.getCountry();
	}

	public static String getLanguage() {
		Locale locale = Locale.getDefault();
		return locale.getLanguage();
	}

	public static String getCountry() {
		Locale locale = Locale.getDefault();
		return locale.getCountry();
	}

	public static String getAppVersion(Context context) {
		String result = "UNKNOWN";
		try {
			result = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return result;
	}
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
	public static String getMacAddress(Context context) {
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		String macAddress = wifiInfo.getMacAddress();
		return macAddress;
	}
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
	public static String getIpStr(Context context) {
		int ipAddress = getIpAddress(context);
		return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
	}
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
	public static String getNetworkType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
	    if(networkInfo!=null){
	    	return networkInfo.getTypeName();
	    }else{
	    	return "UNKNOWN";
	    }
	}
	public static boolean isWifiConnection(Context context){
		 final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	     final NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	     if(networkInfo!=null&&networkInfo.isConnected()){
	    	 return true;
	     }
	     return false;
	}
	public static boolean isConnection(Context context){
		 final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	     final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	     if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
	        	return false;
	      }
	     return true;
	}
}
