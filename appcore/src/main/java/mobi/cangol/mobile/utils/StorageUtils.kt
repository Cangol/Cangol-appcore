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

import android.content.Context
import android.os.Environment

import java.io.File

object StorageUtils {

    @JvmStatic
    fun isExternalStorageRemovable(): Boolean {
        return Environment.isExternalStorageRemovable()
    }

    @JvmStatic
    fun getExternalStorageDir(context: Context, appName: String): String {
        return if (Environment.MEDIA_MOUNTED == Environment
                        .getExternalStorageState() && !isExternalStorageRemovable()) {
            (Environment.getExternalStorageDirectory().path
                    + File.separator + appName)
        } else {
            context.cacheDir.path
        }
    }

    @JvmStatic
    fun getFileDir(context: Context, uniqueName: String): File {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        val cachePath = if (Environment.MEDIA_MOUNTED == Environment
                        .getExternalStorageState() && !isExternalStorageRemovable())
            getExternalCacheDir(
                    context).path
        else
            context.cacheDir.path

        return File(cachePath + File.separator + uniqueName)
    }

    @JvmStatic
    fun getExternalFileDir(context: Context, uniqueName: String): File? {
        return if (context.getExternalFilesDir(uniqueName) != null) {
            context.getExternalFilesDir(uniqueName)
        } else File(Environment.getExternalStorageDirectory().path + uniqueName)
        // Before Froyo we need to construct the external cache dir ourselves
    }

    @JvmStatic
    fun getExternalCacheDir(context: Context): File {
        if (context.externalCacheDir != null) {
            return context.externalCacheDir
        }
        // Before Froyo we need to construct the external cache dir ourselves
        val cacheDir = ("/Android/data/" + context.packageName
                + "/cache/")
        return File(Environment.getExternalStorageDirectory().path + cacheDir)
    }

    @JvmStatic
    fun getUsableSpace(path: File): Long {
        return path.usableSpace
    }
}
