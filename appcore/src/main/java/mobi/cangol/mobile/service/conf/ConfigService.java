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
package mobi.cangol.mobile.service.conf;

import java.io.File;

import mobi.cangol.mobile.service.AppService;

public interface ConfigService extends AppService {

    String APP_DIR = "app_dir";
    String IMAGE_DIR = "image_dir";
    String DOWNLOAD_DIR = "download_dir";
    String TEMP_DIR = "temp_dir";
    String UPGRADE_DIR = "upgrade_dir";
    String DATABASE_NAME = "database_name";
    String SHARED_NAME = "shared_name";

    /**
     * 获取缓存目录
     *
     * @return
     */
    File getCacheDir();

    /**
     * 获取图片目录
     *
     * @return
     */
    File getImageDir();

    /**
     * 获取临时目录
     *
     * @return
     */
    File getTempDir();

    /**
     * 获取下载目录
     *
     * @return
     */
    File getDownloadDir();

    /**
     * 获取更新目录
     *
     * @return
     */
    File getUpgradeDir();

    /**
     * 获取数据库名称
     *
     * @return
     */
    String getDatabaseName();

    /**
     * 获取共享文件名称
     *
     * @return
     */
    String getSharedName();

    /**
     * 返回是否使用内置存储(只对非自定义AppDir有效)
     *
     * @return
     */
    boolean isUseInternalStorage();

    /**
     * 使用内部存储(只对非自定义AppDir有效) 默认为false
     *
     * @param useInternalStorage
     */
    void setUseInternalStorage(boolean useInternalStorage);

    /**
     * 获取app目录
     *
     * @return
     */
    File getAppDir();

    /**
     * 自定义设置AppDir
     *
     */
    void setCustomAppDir(String path);

    /**
     * 返回是否使用自定义AppDir
     *
     * @return 是否使用自定义AppDir
     */
    boolean isCustomAppDir();

    /**
     * 恢复AppDir为默认
     */
    void resetAppDir();
}
