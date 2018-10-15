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
package mobi.cangol.mobile.service;

import android.app.Application;

public interface AppService {
    /**
     * 路由服务
     */
    String ROUTE_SERVICE = "RouteService";
    /**
     * 状态监听服务
     */
    String STATUS_SERVICE = "StatusService";
    /**
     * 更新服务
     */
    String UPGRADE_SERVICE = "UpgradeService";
    /**
     * 统计服务
     */
    String ANALYTICS_SERVICE = "AnalyticsService";
    /**
     * 位置服务
     */
    String LOCATION_SERVICE = "LocationService";
    /**
     * 缓存服务
     */
    String CACHE_MANAGER = "CacheManager";
    /**
     * 应用配置服务
     */
    String CONFIG_SERVICE = "ConfigService";
    /**
     * 异常监听服务
     */
    String CRASH_SERVICE = "CrashService";
    /**
     * 下载服务
     */
    String DOWNLOAD_MANAGER = "DownloadManager";
    /**
     * Session服务
     */
    String SESSION_SERVICE = "SessionService";
    /**
     * PluginManager
     */
    //String PLUGIN_MANAGER = "PluginManager";

    /**
     * ObserverManager
     */
    String OBSERVER_MANAGER = "ObserverManager";

    /**
     * 当创建时
     *
     * @param context
     */
    void onCreate(Application context);

    /**
     * 获取应用服务名称
     *
     * @return
     */
    String getName();

    /**
     * 当销毁时
     */
    void onDestroy();

    /**
     * 设置debug模式
     *
     * @param debug
     */
    void setDebug(boolean debug);

    /**
     * 初始化应用服务属性
     *
     * @param serviceProperty
     */
    void init(ServiceProperty serviceProperty);

    /**
     * 获取应用服务属性
     *
     * @return
     */
    ServiceProperty getServiceProperty();

    /**
     * 生存一个默认的服务属性
     *
     * @return
     */
    ServiceProperty defaultServiceProperty();

}