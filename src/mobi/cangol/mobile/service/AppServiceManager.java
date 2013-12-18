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

import java.io.InputStream;

public abstract class AppServiceManager {
	
	public static final String CONFIG_SERVICE = "config";
	
	public static final String UPGRADE_SERVICE = "upgrade";
	
	public static final String DOWNLOAD_SERVICE = "download";
	
	public static final String STAT_SERVICE = "stat";
	
	public abstract AppService getAppService(String name);
	
	public abstract void destoryService(String name);
	
	public abstract void destoryAllService();
	
	public abstract void destory();
	
	public abstract void setScanPackage(String ... packageName);
	
	public abstract void setServicePropertySource(InputStream is);
}
