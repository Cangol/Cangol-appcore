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

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppUtils {
    private AppUtils() {
    }

    /**
     * 修改locale ,修改后只对新生成activity有效，旧的activity需要重启
     *
     * @param context
     * @param locale
     */
    public static void changeLocale(Context context, Locale locale) {
        Configuration config = context.getResources().getConfiguration();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        config.locale = locale;
        context.getResources().updateConfiguration(config, dm);

        Application app = (Application) context.getApplicationContext();
        app.onConfigurationChanged(config);

    }

    /**
     * 安装apk文件
     *
     * @param context
     * @param apkUri
     */
    public static void install(Context context, Uri apkUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri,
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 安装apk文件
     *
     * @param context
     * @param apkPath
     */
    public static void install(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://"
                        + apkPath),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 卸载应用
     *
     * @param context
     * @param packageName
     */
    public static void unInstall(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 启动应用
     *
     * @param context
     * @param packageName
     */
    public static void launch(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 判断是否安装应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstalled(Context context, String packageName) {
        PackageManager pManager = context.getPackageManager();
        List<PackageInfo> apps = pManager.getInstalledPackages(0);
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取apk应用包名
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static String getPackageName(Context context, String apkPath) {
        ApplicationInfo appInfo = getApplicationInfo(context, apkPath);
        if (appInfo != null) {
            return appInfo.packageName;
        }
        return null;
    }

    /**
     * 获取apk应用icon
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static Drawable getApplicationIcon(Context context, String apkPath) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo appInfo = getApplicationInfo(context, apkPath);
        if (appInfo != null) {
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            return packageManager.getApplicationIcon(appInfo);
        }
        return null;
    }

    /**
     * 获取应用信息
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context, String apkPath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = packageManager.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            return info.applicationInfo;
        }
        return null;
    }

    /**
     * 获取所有非系统应用
     *
     * @param context
     * @return
     */
    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<>();
        PackageManager pManager = context.getPackageManager();
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            //not system app
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                apps.add(pak);
            }
        }
        return apps;
    }
}
