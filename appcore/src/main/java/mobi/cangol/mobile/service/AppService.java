/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service;

import android.content.Context;

public interface AppService {
	/**
	 * 状态监听服务
	 */
	public final static String STATUS_SERVICE = "StatusService";
	/**
	 * 更新服务
	 */
	public final static String UPGRADE_SERVICE = "UpgradeService";
	/**
	 * 统计服务
	 */
	public final static String ANALYTICS_SERVICE = "AnalyticsService";
	/**
	 * 位置服务
	 */
	public final static String LOCATION_SERVICE = "LocationService";
	/**
	 * 缓存服务
	 */
	public final static String CACHE_MANAGER = "CacheManager";
	/**
	 * 应用配置服务
	 */
	public final static String CONFIG_SERVICE = "ConfigService";
	/**
	 * 异常监听服务
	 */
	public final static String CRASH_SERVICE = "CrashService";
	/**
	 * 下载服务
	 */
	public final static String DOWNLOAD_MANAGER = "DownloadManager";
	/**
	 * Session服务
	 */
	public final static String SESSION_SERVICE = "SessionService";
	/**
	 * PluginManager
	 */
	//public final static String PLUGIN_MANAGER = "PluginManager";

	/**
	 * 当创建时
	 * 
	 * @param context
	 */
	void onCreate(Context context);

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
	 * @return
	 */
	ServiceProperty defaultServiceProperty();

}