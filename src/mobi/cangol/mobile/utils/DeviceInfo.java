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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
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

	public static String getDeviceModel() {
		return android.os.Build.MODEL;
	}
	
	public static String getDeviceBrand() {
		return android.os.Build.BRAND;
	}
	
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
	public static String getResolution(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		Display display = wm.getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		return metrics.heightPixels + "x" + metrics.widthPixels;
	}

	public static String getOperator(Context context) {
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
	
	public static boolean isGPSLocation(Context context){
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	public static boolean isNetworkLocation(Context context){
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	public static String getSignHashkey(Context context) {  
	    PackageInfo info;
	    try {
	        info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md;
	            md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            String something = new String(Base64.encode(md.digest(), 0));
	            return something;
	        }
	    } catch (NameNotFoundException e1) {
	        Log.e("name not found", e1.toString());
	    } catch (NoSuchAlgorithmException e) {
	        Log.e("no such an algorithm", e.toString());
	    } catch (Exception e) {
	        Log.e("exception", e.toString());
	    }
		return null;
	}  
}
