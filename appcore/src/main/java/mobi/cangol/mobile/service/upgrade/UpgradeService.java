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
package mobi.cangol.mobile.service.upgrade;

import mobi.cangol.mobile.service.AppService;

/**
 * @author Cangol
 *
 */
public interface UpgradeService extends AppService {
//	/**
//	 * 更新资源
//	 *
//	 * @param filename 文件名 包含后缀，且是唯一的
//	 * @param url 下载地址
//	 * @param notification 是否通知栏显示
//	 * @param load
//	 */
//	void upgradeRes(String filename, String url,boolean notification, boolean load);
//
//	/**
//	 * 更新dex
//	 *
//	 * @param filename 文件名 包含后缀，且是唯一的
//	 * @param url 下载地址
//	 * @param notification 是否通知栏显示
//	 * @param load
//	 */
//	void upgradeDex(String filename, String url,boolean notification, boolean load);
//
//	/**
//	 * 更新so
//	 *
//	 * @param filename 文件名 包含后缀，且是唯一的
//	 * @param url 下载地址
//	 * @param notification 是否通知栏显示
//	 * @param load
//	 */
//	 void upgradeSo(String filename, String url,boolean notification, boolean load);
//
//	/**
//	 * 更新插件apk
//	 *
//	 * @param filename 文件名 包含后缀，且是唯一的
//	 * @param url 下载地址
//	 * @param notification 是否通知栏显示
//	 * @param install
//	 * @hide
//	 */
//	void upgradeApk(String filename, String url,boolean notification, boolean install);

    /**
     * 更新资源
     *
     * @param filename 文件名 包含后缀，且是唯一的
     * @param url 下载地址
     * @param notification 是否通知栏显示
     */
    void upgrade(String filename, String url, boolean notification);

    /**
     * 取消更新
     * @param filename 文件名 包含后缀，且是唯一的
     */
    void cancel(String filename);

    /**
     * 设置更新监听接口
     * @param filename 文件名 包含后缀，且是唯一的
     * @param upgradeListener
     */
    void setOnUpgradeListener(String filename, OnUpgradeListener upgradeListener);
}
