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

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import java.util.*

object AppUtils {

    /**
     * 修改locale ,修改后只对新生成activity有效，旧的activity需要重启
     *
     * @param context
     * @param locale
     */
    @JvmStatic
    fun changeLocale(context: Context, locale: Locale) {
        val config = context.resources.configuration
        val dm = context.resources.displayMetrics
        config.setLocale(locale)
        context.resources.updateConfiguration(config, dm)

        val app = context.applicationContext as Application
        app.onConfigurationChanged(config)

    }

    /**
     * 安装apk文件
     *
     * @param context
     * @param apkUri
     */
    @JvmStatic
    fun install(context: Context, apkUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    /**
     * 安装apk文件
     *
     * @param context
     * @param apkPath
     */
    @JvmStatic
    fun install(context: Context, apkPath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(Uri.parse("file://$apkPath"),
                "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    /**
     * 卸载应用
     *
     * @param context
     * @param packageName
     */
    @JvmStatic
    fun unInstall(context: Context, packageName: String) {
        val uri = Uri.parse("package:$packageName")
        val intent = Intent(Intent.ACTION_DELETE, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 启动应用
     *
     * @param context
     * @param packageName
     */
    @JvmStatic
    fun launch(context: Context, packageName: String) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
    }

    /**
     * 判断是否安装应用
     *
     * @param context
     * @param packageName
     * @return
     */
    @JvmStatic
    fun isInstalled(context: Context, packageName: String): Boolean {
        val pManager = context.packageManager
        val apps = pManager.getInstalledPackages(0)
        for (i in apps.indices) {
            if (apps[i].packageName == packageName) {
                return true
            }
        }
        return false
    }

    /**
     * 获取apk应用包名
     *
     * @param context
     * @param apkPath
     * @return
     */
    @JvmStatic
    fun getPackageName(context: Context, apkPath: String): String? {
        val appInfo = getApplicationInfo(context, apkPath)
        return appInfo?.packageName
    }

    /**
     * 获取apk应用icon
     *
     * @param context
     * @param apkPath
     * @return
     */
    @JvmStatic
    fun getApplicationIcon(context: Context, apkPath: String): Drawable? {
        val packageManager = context.packageManager
        val appInfo = getApplicationInfo(context, apkPath)
        if (appInfo != null) {
            appInfo.sourceDir = apkPath
            appInfo.publicSourceDir = apkPath
            return packageManager.getApplicationIcon(appInfo)
        }
        return null
    }

    /**
     * 获取应用信息
     *
     * @param context
     * @param apkPath
     * @return
     */
    @JvmStatic
    fun getApplicationInfo(context: Context, apkPath: String): ApplicationInfo? {
        val packageManager = context.packageManager
        val info = packageManager.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES)
        return info?.applicationInfo
    }

    /**
     * 获取所有非系统应用
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getAllApps(context: Context): List<PackageInfo> {
        val apps = ArrayList<PackageInfo>()
        val pManager = context.packageManager
        val pkgList = pManager.getInstalledPackages(0)
        for (i in pkgList.indices) {
            val pak = pkgList[i]
            //not system app
            if (pak.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM <= 0) {
                apps.add(pak)
            }
        }
        return apps
    }
}
