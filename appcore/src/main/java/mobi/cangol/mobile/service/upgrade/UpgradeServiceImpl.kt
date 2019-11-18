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
package mobi.cangol.mobile.service.upgrade

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import mobi.cangol.mobile.CoreApplication
import mobi.cangol.mobile.http.download.DownloadHttpClient
import mobi.cangol.mobile.http.download.DownloadResponseHandler
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.AppService
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import mobi.cangol.mobile.service.conf.ConfigService
import mobi.cangol.mobile.service.download.DownloadNotification
import mobi.cangol.mobile.utils.AppUtils
import java.io.File
import java.io.IOException
import java.util.*


/**
 * @author Cangol
 */
@Service("UpgradeService")
internal class UpgradeServiceImpl : UpgradeService {
    private var debug = false
    private var mContext: Application? = null
    private var mServiceProperty: ServiceProperty? = null
    private var mConfigService: ConfigService? = null
    private val mIds = ArrayList<Int>()
    private var mOnUpgradeListeners: MutableMap<String, OnUpgradeListener>? = null
    private var mDownloadHttpClient: DownloadHttpClient? = null

    override fun onCreate(context: Application) {
        mContext = context
        mConfigService = (mContext as CoreApplication).getAppService(AppService.CONFIG_SERVICE) as ConfigService?
        mOnUpgradeListeners = HashMap()
    }

    override fun init(serviceProperty: ServiceProperty) {
        this.mServiceProperty = serviceProperty
    }

    override fun getName(): String {
        return TAG
    }

    override fun onDestroy() {
        if (debug) Log.d("onDestory")
        if (mDownloadHttpClient != null)
            mDownloadHttpClient!!.cancelAll()
        val notificationManager = mContext!!.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        for (id in mIds) {
            notificationManager.cancel(id)
            if (debug) Log.d("notification cancel $id")
        }
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty!!
    }

    override fun defaultServiceProperty(): ServiceProperty {
        return ServiceProperty(TAG)
    }

    override fun setDebug(mDebug: Boolean) {
        this.debug = mDebug
    }

    override fun upgrade(filename: String, url: String, notification: Boolean) {
        upgrade(filename, url, notification, UpgradeType.APK, false, true)
    }

    override fun upgrade(filename: String, url: String, notification: Boolean, install: Boolean) {
        upgrade(filename, url, notification, UpgradeType.APK, install, true)
    }

    override fun upgrade(filename: String, url: String, notification: Boolean, install: Boolean, safe: Boolean) {
        upgrade(filename, url, notification, UpgradeType.APK, install, safe)
    }

    private fun upgrade(filename: String, url: String, notification: Boolean, upgradeType: UpgradeType, install: Boolean, safe: Boolean) {
        val savePath = mConfigService!!.getUpgradeDir().toString() + File.separator + filename
        val saveFile = File(savePath)
        if (debug) Log.d("upgrade savePath:$savePath")
        if (saveFile.exists()) {
            val result = saveFile.delete()
            if (!result) Log.d("delete oldFile fail:$savePath")
        } else {
            try {
                val result = saveFile.createNewFile()
                if (!result) Log.d("createNewFile  fail:$savePath")
            } catch (e: IOException) {
                Log.e(e.message)
            }

        }
        var downloadNotification: DownloadNotification? = null
        if (notification) {
            var intent: Intent? = null
            try {
                intent = createFinishIntent(savePath, upgradeType)
            } catch (e: Exception) {
                Log.e(TAG, "createFinishIntent fail!")
                return
            }

            if (intent == null) {
                Log.e(TAG, "createFinishIntent fail!")
                return
            }
            downloadNotification = DownloadNotification(mContext!!, filename, savePath, intent)
            mIds.add(downloadNotification.id)
        }
        mDownloadHttpClient = DownloadHttpClient.build(TAG, safe)

        val finalDownloadNotification = downloadNotification
        mDownloadHttpClient!!.send(filename, url, object : DownloadResponseHandler() {
            override fun onWait() {
                super.onWait()
                Log.d(UpgradeServiceImpl.TAG,"onWait ")
                if (notification) {
                    finalDownloadNotification?.createNotification()
                }
            }

            override fun onStart(start: Long, length: Long) {
                Log.d(UpgradeServiceImpl.TAG, "onStart  start=$start,length=$length")
            }

            override fun onStop(end: Long) {
                super.onStop(end)
                Log.d(UpgradeServiceImpl.TAG, "onStop  end=$end")
                if (notification) {
                    finalDownloadNotification?.cancelNotification()
                    mIds.remove(Integer.valueOf(finalDownloadNotification?.id!!))
                }
                notifyUpgradeFailure(filename, "stop")
            }

            override fun onFinish(end: Long) {
                super.onFinish(end)
                Log.d(UpgradeServiceImpl.TAG, "onFinish  end=$end")
                if (notification) {
                    finalDownloadNotification?.finishNotification()
                }
                if (install) {
                    makeLoad(savePath, upgradeType)
                }

                notifyUpgradeFinish(filename, savePath)
            }

            override fun onProgressUpdate(end: Long, progress: Int, speed: Int) {
                super.onProgressUpdate(end, progress, speed)
                Log.d(UpgradeServiceImpl.TAG, "onProgressUpdate  end=$end progress=$progress speed=$speed")
                if (notification) {
                    finalDownloadNotification?.updateNotification(progress, speed)
                }

                notifyUpgradeProgress(filename, speed, progress)
            }

            override fun onFailure(error: Throwable, content: String) {
                super.onFailure(error, content)
                Log.e(UpgradeServiceImpl.TAG, "onFailure error=${error.cause}")
                if (notification) {
                    finalDownloadNotification?.failureNotification()
                }
                notifyUpgradeFailure(filename, content)
            }

        }, saveFile.length(), savePath)
    }

    private fun makeLoad(savePath: String, upgradeType: UpgradeType) {
        when (upgradeType) {
            UpgradeType.APK -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val authority = mContext!!.packageName + ".fileprovider"
                if (debug) Log.e("authority=$authority")
                val contentUri = FileProvider.getUriForFile(mContext!!, authority, File(savePath))
                AppUtils.install(mContext!!, contentUri)
            } else {
                AppUtils.install(mContext!!, savePath)
            }
            UpgradeType.RES -> {
            }
            UpgradeType.DEX -> {
            }
            UpgradeType.SO -> System.load(savePath)
            UpgradeType.OTHER -> Intent()
            else -> {
            }
        }
        /**
         * DexClassLoader dexClassLoader = new DexClassLoader(savePath, mConfigService.getTempDir().getAbsolutePath(), null, mContext.getClassLoader());
         * try {
         * Class clazz = dexClassLoader.loadClass("className");
         * } catch (ClassNotFoundException e) {
         * Log.e(e.getMessage());
         * } */
    }

    private fun createFinishIntent(savePath: String, upgradeType: UpgradeType): Intent? {
        var intent: Intent? = null
        val file = File(savePath)
        when (upgradeType) {
            UpgradeType.APK -> {
                intent = Intent(Intent.ACTION_VIEW)
                //判断是否是AndroidN以及更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val authority = mContext!!.packageName + ".fileprovider"
                    if (debug) Log.e("authority=$authority")
                    val contentUri = FileProvider.getUriForFile(mContext!!, authority, file)
                    if (debug) Log.e("uri=$contentUri")
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
            UpgradeType.RES -> {
            }
            UpgradeType.DEX -> {
            }
            UpgradeType.SO -> {
            }
            UpgradeType.OTHER -> {
            }
            else -> {
            }
        }
        return intent
    }

    override fun cancel(filename: String) {
        if (mDownloadHttpClient != null)
            mDownloadHttpClient!!.cancelRequests(filename, true)
    }

    fun notifyUpgradeFinish(filename: String, filepath: String) {
        if (mOnUpgradeListeners!!.containsKey(filename)) {
            mOnUpgradeListeners!![filename]!!.onFinish(filepath)
        }
    }

    fun notifyUpgradeProgress(filename: String, speed: Int, progress: Int) {
        if (mOnUpgradeListeners!!.containsKey(filename)) {
            mOnUpgradeListeners!![filename]!!.progress(speed, progress)
        }
    }

    fun notifyUpgradeFailure(filename: String, error: String) {
        if (mOnUpgradeListeners!!.containsKey(filename)) {
            mOnUpgradeListeners!![filename]!!.onFailure(error)
        }
    }

    override fun setOnUpgradeListener(filename: String, onUpgradeListener: OnUpgradeListener) {
        if (!mOnUpgradeListeners!!.containsKey(filename)) {
            mOnUpgradeListeners!![filename] = onUpgradeListener
        }
    }

    companion object {
         const val TAG = "UpgradeService"
    }
}
