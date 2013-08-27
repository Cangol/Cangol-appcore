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
package com.cangol.mobile.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

public class AppUtils {
	public static void install(Context context,String apkPath){
		Intent intent = new Intent(Intent.ACTION_VIEW);   
	    intent.setDataAndType(Uri.parse("file://"  
	            + apkPath),   
	            "application/vnd.android.package-archive");   
	    context.startActivity(intent);   
	}
	public static void unInstall(Context context,String packageName){
		Uri uri = Uri.parse("package:"+packageName); 
		Intent intent = new Intent(Intent.ACTION_DELETE, uri); 
		context.startActivity(intent); 
	}
	public static void launch(Context context,String packageName){
		PackageManager packageManager = context.getPackageManager(); 
		Intent intent=new Intent(); 
		intent =packageManager.getLaunchIntentForPackage(packageName); 
		context.startActivity(intent);   
	}
	
	public static boolean isInstalled(Context context,String packageName){
		 PackageManager pManager = context.getPackageManager();  
		 List<PackageInfo> apps = pManager.getInstalledPackages(0);
		 for (int i = 0; i < apps.size(); i++) { 
			 if(apps.get(i).packageName.equals(packageName)){
				 return true;
			 }
		 }
		 return false;
	}
	
	public static String getPackageName(Context context,String apkPath){
		ApplicationInfo appInfo=getApplicationInfo(context,apkPath);
		if (appInfo != null) { 
			return appInfo.packageName;
		}
		return null;
	}
	public static ApplicationInfo getApplicationInfo(Context context,String apkPath){
		PackageManager packageManager = context.getPackageManager(); 
		PackageInfo info = packageManager.getPackageArchiveInfo(apkPath,  
				PackageManager.GET_ACTIVITIES);  
		if (info != null) { 
			return info.applicationInfo;
		}
		return null;
	}

	public static List<PackageInfo> getAllApps(Context context) {  
	    List<PackageInfo> apps = new ArrayList<PackageInfo>();  
	    PackageManager pManager = context.getPackageManager();  
	    List<PackageInfo> paklist = pManager.getInstalledPackages(0);  
	    for (int i = 0; i < paklist.size(); i++) {  
	        PackageInfo pak = (PackageInfo) paklist.get(i);  
	        //not system app
	        if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {  
	            apps.add(pak);  
	        }  
	    }  
	    return apps;  
	} 
}
