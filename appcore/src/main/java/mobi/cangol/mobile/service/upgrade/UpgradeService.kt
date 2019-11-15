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

import mobi.cangol.mobile.service.AppService

/**
 * @author Cangol
 */
interface UpgradeService : AppService {

    /**
     * 更新资源
     *
     * @param filename     文件名 包含后缀，且是唯一的
     * @param url          下载地址
     * @param notification 是否通知栏显示
     */
    fun upgrade(filename: String, url: String, notification: Boolean)

    /**
     * 更新资源
     *
     * @param filename     文件名 包含后缀，且是唯一的
     * @param url          下载地址
     * @param notification 是否通知栏显示
     * @param install      是否安装
     */
    fun upgrade(filename: String, url: String, notification: Boolean, install: Boolean)

    /**
     * 更新资源
     *
     * @param filename     文件名 包含后缀，且是唯一的
     * @param url          下载地址
     * @param notification 是否通知栏显示
     * @param install      是否安装
     * @param safe         是否安全
     */
    fun upgrade(filename: String, url: String, notification: Boolean, install: Boolean, safe: Boolean)

    /**
     * 取消更新
     *
     * @param filename 文件名 包含后缀，且是唯一的
     */
    fun cancel(filename: String)

    /**
     * 设置更新监听接口
     *
     * @param filename        文件名 包含后缀，且是唯一的
     * @param upgradeListener
     */
    fun setOnUpgradeListener(filename: String, upgradeListener: OnUpgradeListener)
}
