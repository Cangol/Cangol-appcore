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
package mobi.cangol.mobile.service.download;

import mobi.cangol.mobile.service.AppService;

public interface DownloadManager extends AppService {
	public final static String DOWNLOADSERVICE_THREAD_MAX="thread_max";
	public final static String DOWNLOADSERVICE_THREADPOOL_NAME="threadpool_name";
	
	DownloadExecutor getDownloadExecutor(String name);
	
	void registerExecutor(String name,Class<? extends DownloadExecutor> clazz,int max);
	
	void recoverAllAllDownloadExecutor();
	
	void interruptAllDownloadExecutor();
}
