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
	
	public final static String STATUS_SERVICE="StatusService";
	
	public final static String UPGRADE_SERVICE="UpgradeService";
	
	public final static String ANALYTICS_SERVICE="AnalyticsService";
	
	public final static String LOCATION_SERVICE="LocationService";
	
	public final static String CACHE_MANAGER="CacheManager";
	
	public final static String CONFIG_SERVICE="ConfigService";
	
	public final static String CRASH_SERVICE="CrashService";
	
	public final static String DOWNLOAD_MANAGER="DownloadManager";
	
	public final static String GLOBAL_DATA="GlobalData";
	
	void onCreate(Context context);

	String getName();
	
	void onDestory();	
	
	void setDebug(boolean debug);
	
	void init(ServiceProperty serviceProperty);
	
	ServiceProperty getServiceProperty();
}