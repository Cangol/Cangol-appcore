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
package mobi.cangol.mobile.service

import android.app.Application

interface AppService {

    /**
     * 获取应用服务名称
     *
     * @return
     */
    fun getName(): String

    /**
     * 当创建时
     *
     * @param context
     */
    fun onCreate(context: Application)

    /**
     * 当销毁时
     */
    fun onDestroy()

    /**
     * 设置debug模式
     *
     * @param mDebug
     */
    fun setDebug(mDebug: Boolean)

    /**
     * 初始化应用服务属性
     *
     * @param serviceProperty
     */
    fun init(serviceProperty: ServiceProperty)

    /**
     * 生存一个默认的服务属性
     *
     * @return
     */
    fun defaultServiceProperty(): ServiceProperty

    /**
     * 获取应用服务属性
     *
     * @return
     */
    fun getServiceProperty(): ServiceProperty

    companion object {
        /**
         * 状态监听服务
         */
        const val STATUS_SERVICE = "StatusService"
        /**
         * 更新服务
         */
        const val UPGRADE_SERVICE = "UpgradeService"
        /**
         * 统计服务
         */
        const val ANALYTICS_SERVICE = "AnalyticsService"
        /**
         * 位置服务
         */
        const val LOCATION_SERVICE = "LocationService"
        /**
         * 缓存服务
         */
        const val CACHE_MANAGER = "CacheManager"
        /**
         * 应用配置服务
         */
        const val CONFIG_SERVICE = "ConfigService"
        /**
         * 异常监听服务
         */
        const val CRASH_SERVICE = "CrashService"
        /**
         * 下载服务
         */
        const val DOWNLOAD_MANAGER = "DownloadManager"
        /**
         * Session服务
         */
        const val SESSION_SERVICE = "SessionService"
        /**
         * ObserverManager
         */
        const val OBSERVER_MANAGER = "ObserverManager"
        /**
         *RouteService
         */
        const val ROUTE_SERVICE = "RouteService"
    }

}