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

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class StorageUtils {
    private StorageUtils() {
    }

    public static String getExternalStorageDir(Context context, String appName) {
        String externalDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) && !isExternalStorageRemovable()) {
            externalDir = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + appName;
        } else {
            externalDir = context.getCacheDir().getPath();
        }
        return externalDir;
    }

    public static File getFileDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) && !isExternalStorageRemovable() ? getExternalCacheDir(
                context).getPath()
                : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public static boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }

    public static File getExternalFileDir(Context context, String uniqueName) {
        if (context.getExternalFilesDir(uniqueName) != null) {
            return context.getExternalFilesDir(uniqueName);
        }
        // Before Froyo we need to construct the external cache dir ourselves
        return new File(Environment.getExternalStorageDirectory().getPath()
                + uniqueName);
    }

    public static File getExternalCacheDir(Context context) {
        if (context.getExternalCacheDir() != null) {
            return context.getExternalCacheDir();
        }
        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    public static long getUsableSpace(File path) {
      return path.getUsableSpace();
    }
}
