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

import android.app.Application;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
/**
 * @author Cangol
 */
@Service("DownloadManager")
 class DownloadManagerImpl implements DownloadManager{
	private final static String TAG="DownloadManager";
	protected static final int DEFAULT_MAX_THREAD = 2;
	protected boolean debug=false;
	protected ConcurrentHashMap<String, DownloadExecutor<?>> executorMap = null;
	private Application mContext = null;
	private ServiceProperty mServiceProperty;
	private DownloadManagerImpl() {
		
	}
	public void  onCreate(Application context){
		this.mContext=context;
	}
	@Override
	public void init(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
		if (executorMap == null) {
			executorMap = new ConcurrentHashMap<String, DownloadExecutor<?>>();
		}
	}
	
	public  synchronized DownloadExecutor<?> getDownloadExecutor(String name) {
		DownloadExecutor<?> downloadExecutor = null;
		if (executorMap == null) {
			executorMap = new ConcurrentHashMap<String, DownloadExecutor<?>>();
		}
		if (executorMap.containsKey(name)) {
			downloadExecutor = executorMap.get(name);
		} 
		return downloadExecutor;
	}
	
	//提前注册各个下载器 减少需要用时再初始化造成的时间消耗（初始化扫描耗时较多）
	public  void registerExecutor(String name,Class<? extends DownloadExecutor<?>> clazz,int max){
		DownloadExecutor<?> downloadExecutor=null;
		if (executorMap == null) {
			executorMap = new ConcurrentHashMap<String, DownloadExecutor<?>>();
		}
		if (executorMap.containsKey(name)) {
			downloadExecutor = executorMap.get(name);
		} else {
			try {
				Constructor<? extends DownloadExecutor<?>> c=clazz.getDeclaredConstructor(String.class);
				c.setAccessible(true);
				downloadExecutor = c.newInstance(name);
				downloadExecutor.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
			downloadExecutor.setContext(mContext);
			downloadExecutor.setPool(PoolManager.buildPool(name, max));
			executorMap.put(name, downloadExecutor);
		}
		
	}
	
	public  void recoverAllAllDownloadExecutor() {
		if(null==executorMap)return;
		Enumeration<DownloadExecutor<?>> en = executorMap.elements();
		DownloadExecutor<?> downloadExecutor=null;
		while (en.hasMoreElements()) {
			downloadExecutor=en.nextElement();
			downloadExecutor.recoverAll();
		}
	}
	
	public  void interruptAllDownloadExecutor() {
		if(null==executorMap)return;
		Enumeration<DownloadExecutor<?>> en = executorMap.elements();
		DownloadExecutor<?> downloadExecutor=null;
		while (en.hasMoreElements()) {
			downloadExecutor=en.nextElement();
			downloadExecutor.interruptAll();
		}
	}
	
	public  void onDestroy() {
		if(null==executorMap)return;
		Enumeration<DownloadExecutor<?>> en = executorMap.elements();
		while (en.hasMoreElements()) {
			en.nextElement().close();
		}
		executorMap.clear();
		executorMap=null;
		PoolManager.clear();
	}

	@Override
	public String getName() {
		return "DownloadManager";
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug=debug;
	}

	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}
	@Override
	public ServiceProperty defaultServiceProperty() {
		ServiceProperty sp=new ServiceProperty(TAG);
		sp.putString(DOWNLOADSERVICE_THREADPOOL_NAME, TAG);
		sp.putInt(DOWNLOADSERVICE_THREAD_MAX, DEFAULT_MAX_THREAD);
		return sp;
	}
}
