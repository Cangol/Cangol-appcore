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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import android.content.Context;
@Service("DownloadService")
public class DownloadManagerImpl implements DownloadManager{
	
	protected static final int DEFAULT_MAX_THREAD = 2;
	
	static Map<String,Integer> conf=new HashMap<String,Integer>();
	
	static {
		//配置每个下载执行器的核心线程数
		conf.put("Apk", 1);
		conf.put("Book", 5);
	}
	
	protected static ConcurrentHashMap<String, DownloadExecutor> executorMap = null;
	private Context mContext = null;
	private ServiceProperty mServiceProperty;
	private DownloadManagerImpl() {
		
	}
	public void  onCreate(Context context){
		this.mContext=context;
	}
	@Override
	public void init(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
		if (executorMap == null) {
			executorMap = new ConcurrentHashMap<String, DownloadExecutor>();
		}
		DownloadExecutor downloadExecutor = null;
		for(String name:conf.keySet()){
			downloadExecutor = create(name);
			downloadExecutor.setContext(mContext);
			downloadExecutor.setPool(PoolManager.buildPool(name, conf.containsKey(name)?conf.get(name):DEFAULT_MAX_THREAD));
			executorMap.put(name, downloadExecutor);
		}
	}
	
	public  synchronized DownloadExecutor getDownloadExecutor(String name) {
		DownloadExecutor downloadExecutor = null;
		if (executorMap == null) {
			executorMap = new ConcurrentHashMap<String, DownloadExecutor>();
		}
		if (executorMap.containsKey(name)) {
			downloadExecutor = executorMap.get(name);
		} else {
			downloadExecutor = create(name);
			downloadExecutor.setContext(mContext);
			downloadExecutor.setPool(PoolManager.buildPool(name, conf.containsKey(name)?conf.get(name):DEFAULT_MAX_THREAD));
			executorMap.put(name, downloadExecutor);
		}
		return downloadExecutor;
	}
	
	private  DownloadExecutor create(String name){
		if("Apk".equals(name)){
			return new ApkDownloadExecutor(name);
		}else if("Book".equals(name)){
			return null;
		}
		return null;	
	}
	//提前注册各个下载器 减少需要用时再初始化造成的时间消耗（初始化扫描耗时较多）
	public  void registerExecutor(String name,Class<? extends DownloadExecutor> clazz,int max){
		DownloadExecutor downloadExecutor=null;
		if (executorMap == null) {
			executorMap = new ConcurrentHashMap<String, DownloadExecutor>();
		}
		if (executorMap.containsKey(name)) {
			downloadExecutor = executorMap.get(name);
		} else {
			try {
				downloadExecutor = clazz.getConstructor(String.class).newInstance(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
			conf.put(name, max);
			downloadExecutor.setContext(mContext);
			downloadExecutor.setPool(PoolManager.buildPool(name, max));
			executorMap.put(name, downloadExecutor);
		}
		
	}
	
	public  void recoverAllAllDownloadExecutor() {
		if(null==executorMap)return;
		Enumeration<DownloadExecutor> en = executorMap.elements();
		DownloadExecutor downloadExecutor=null;
		while (en.hasMoreElements()) {
			downloadExecutor=en.nextElement();
			downloadExecutor.recoverAll();
		}
	}
	
	public  void interruptAllDownloadExecutor() {
		if(null==executorMap)return;
		Enumeration<DownloadExecutor> en = executorMap.elements();
		DownloadExecutor downloadExecutor=null;
		while (en.hasMoreElements()) {
			downloadExecutor=en.nextElement();
			downloadExecutor.interruptAll();
		}
	}
	
	public  void onDestory() {
		if(null==executorMap)return;
		Enumeration<DownloadExecutor> en = executorMap.elements();
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
		
	}

	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}
}
