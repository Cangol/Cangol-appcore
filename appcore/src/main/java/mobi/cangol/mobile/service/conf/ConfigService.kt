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

import java.io.File

import mobi.cangol.mobile.service.AppService

interface ConfigService : AppService {

    /**
     * 获取缓存目录
     *
     * @return
     */
    fun getCacheDir(): File

    /**
     * 获取图片目录
     *
     * @return
     */
    fun getImageDir(): File

    /**
     * 获取临时目录
     *
     * @return
     */
    fun getTempDir(): File

    /**
     * 获取下载目录
     *
     * @return
     */
    fun getDownloadDir(): File

    /**
     * 获取更新目录
     *
     * @return
     */
    fun getUpgradeDir(): File

    /**
     * 获取数据库名称
     *
     * @return
     */
    fun getDatabaseName(): String?

    /**
     * 获取共享文件名称
     *
     * @return
     */
    fun getSharedName(): String?

    /**
     * 返回是否使用内置存储(只对非自定义AppDir有效)
     *
     * @return
     */
    fun isUseInternalStorage(): Boolean

    /**
     * 使用内部存储(只对非自定义AppDir有效) 默认为false
     *
     * @param useInternalStorage
     */
    fun setUseInternalStorage(useInternalStorage: Boolean)

    /**
     * 获取app目录
     *
     * @return
     */
    fun getAppDir(): File?

    /**
     * 自定义设置AppDir
     *
     */
    fun setCustomAppDir(path: String)

    /**
     * 返回是否使用自定义AppDir
     *
     * @return 是否使用自定义AppDir
     */
    fun isCustomAppDir(): Boolean

    /**
     * 恢复AppDir为默认
     */
    fun resetAppDir()

    companion object {
        const val APP_DIR = "app_dir"
        const val IMAGE_DIR = "image_dir"
        const val DOWNLOAD_DIR = "download_dir"
        const val TEMP_DIR = "temp_dir"
        const val UPGRADE_DIR = "upgrade_dir"
        const val DATABASE_NAME = "database_name"
        const val SHARED_NAME = "shared_name"
    }
}
