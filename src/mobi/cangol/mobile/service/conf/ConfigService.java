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
package mobi.cangol.mobile.service.conf;

import mobi.cangol.mobile.service.AppService;

public interface ConfigService extends AppService{
	
	public final static String APP_DIR="app_dir";
	public final static String IMAGE_DIR="image_dir";
	public final static String DOWNLOAD_DIR="download_dir";
	public final static String TEMP_DIR="temp_dir";
	public final static String UPGRADE_DIR="upgrade_dir";
	public final static String DATABASE_NAME="database_name";
	public final static String SHARED_NAME="shared_name";
	
	String getAppDir();
	
	String getCacheDir();
	
	String getImageDir();
	
	String getTempDir();
	
	String getDownloadDir();
	
	String getUpgradeDir();
	
	String getDatabaseName();
	
	String getSharedName();
}
