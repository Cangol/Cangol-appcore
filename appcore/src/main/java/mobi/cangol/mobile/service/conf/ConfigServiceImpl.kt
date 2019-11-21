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
package mobi.cangol.mobile.service.conf

import android.annotation.TargetApi
import android.app.Application
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import mobi.cangol.mobile.utils.StorageUtils
import java.io.File

@Service("ConfigService")
internal class ConfigServiceImpl : ConfigService {
    private var mContext: Application? = null
    private var mServiceProperty = ServiceProperty(TAG)
    private var mDebug = false
    private var mUseInternalStorage = false
    private var mIsCustomAppDir = false
    private var mAppDir: File? = null

    override fun onCreate(context: Application) {
        mContext = context
        mAppDir = initAppDir()
    }

    override fun setDebug(mDebug: Boolean) {
        this.mDebug = mDebug
    }

    override fun init(serviceProperty: ServiceProperty) {
        this.mServiceProperty = serviceProperty
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun defaultServiceProperty(): ServiceProperty {
        val sp = ServiceProperty(TAG)
        sp.putString(ConfigService.IMAGE_DIR, "image")
        sp.putString(ConfigService.DOWNLOAD_DIR, "download")
        sp.putString(ConfigService.TEMP_DIR, "temp")
        sp.putString(ConfigService.UPGRADE_DIR, "upgrade")
        sp.putString(ConfigService.DATABASE_NAME, "app_db")
        sp.putString(ConfigService.SHARED_NAME, "app_shared")
        return sp
    }

    override fun getName(): String {
        return TAG
    }

    override fun onDestroy() {
        //do nothings
    }

    override fun isUseInternalStorage(): Boolean {
        return mUseInternalStorage
    }

    override fun setUseInternalStorage(useInternalStorage: Boolean) {
        if (!mIsCustomAppDir) {
            this.mUseInternalStorage = useInternalStorage
            mAppDir = initAppDir()
        }
    }

    override fun getAppDir(): File? {
        return mAppDir
    }

    override fun setCustomAppDir(path: String) {
        if (mDebug) Log.d(TAG, "setCustomAppDir $path")
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        val file = File(path)
        if (file.exists()) {
            StrictMode.setThreadPolicy(oldPolicy)
            mAppDir = file
            mIsCustomAppDir = true
        } else {
            val mkdirs = file.mkdirs()
            StrictMode.setThreadPolicy(oldPolicy)
            if (mkdirs) {
                mAppDir = file
                mIsCustomAppDir = true
            } else {
                throw IllegalArgumentException("mkdirs fail. path=$path")
            }
        }
    }

    override fun isCustomAppDir(): Boolean {
        return mIsCustomAppDir
    }

    override fun resetAppDir() {
        mIsCustomAppDir = false
        mAppDir = initAppDir()
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun initAppDir(): File? {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        var file: File? = if (mUseInternalStorage) {
            mContext!!.filesDir.parentFile
        } else {
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && !StorageUtils.isExternalStorageRemovable()) {
                File(StorageUtils.getExternalStorageDir(mContext!!, mContext!!.packageName))
            } else {
                mContext!!.filesDir.parentFile
            }
        }
        StrictMode.setThreadPolicy(oldPolicy)
        return file
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun getFileDir(name: String?): File {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        var file: File? = if (mIsCustomAppDir) {
            File(mAppDir, name)
        } else {
            if (mUseInternalStorage) {
                mContext!!.getFileStreamPath(name)
            } else {
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && !StorageUtils.isExternalStorageRemovable()) {
                    StorageUtils.getExternalFileDir(mContext!!, name!!)
                } else {
                    mContext!!.getFileStreamPath(name)
                }

            }
        }
        if (!file!!.exists()) {
            file.mkdirs()
        }
        StrictMode.setThreadPolicy(oldPolicy)
        return file
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun getCacheDir(): File {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        var file: File?
        file = if (mIsCustomAppDir) {
            File(mAppDir, "cache")
        } else {
            if (mUseInternalStorage) {
                mContext!!.cacheDir
            } else {
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && !StorageUtils.isExternalStorageRemovable()) {
                    StorageUtils.getExternalCacheDir(mContext!!)
                } else {
                    mContext!!.cacheDir
                }
            }
        }
        if (!file!!.exists()) {
            file.mkdirs()
        }
        StrictMode.setThreadPolicy(oldPolicy)
        return file
    }

    override fun getImageDir(): File {
        return getFileDir(mServiceProperty.getString(ConfigService.IMAGE_DIR))
    }

    override fun getTempDir(): File {
        return getFileDir(mServiceProperty.getString(ConfigService.TEMP_DIR))
    }

    override fun getDownloadDir(): File {
        return getFileDir(mServiceProperty.getString(ConfigService.DOWNLOAD_DIR))
    }

    override fun getUpgradeDir(): File {
        return getFileDir(mServiceProperty.getString(ConfigService.UPGRADE_DIR))
    }

    override fun getDatabaseName(): String? {
        return mServiceProperty.getString(ConfigService.DATABASE_NAME)
    }

    override fun getSharedName(): String? {
        return mServiceProperty.getString(ConfigService.SHARED_NAME)
    }

    companion object {
        private const val TAG = "ConfigService"
    }
}