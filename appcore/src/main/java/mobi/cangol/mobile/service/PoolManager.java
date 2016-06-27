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

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * TheadPool manager by name
 * @author Cangol
 *
 */
public class PoolManager {
	private static ConcurrentHashMap<String,Pool> poolMap=null;
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	private static final int KEEP_ALIVE = 1;
	private static ExecutorService generateExecutorService(final String name,int max){

		ExecutorService executorService=new ThreadPoolExecutor(CORE_POOL_SIZE, max, KEEP_ALIVE,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128), new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);

			public Thread newThread(final Runnable r) {
				return new Thread(r, name+"$WorkThread #" + mCount.getAndIncrement());
			}
		});
	    return executorService;
	}
	/**
	 * 获取一个线程池
	 * @param name
	 * @return
	 */
	public  static Pool getPool(String name){
		if(null==poolMap)poolMap=new ConcurrentHashMap<String,Pool>();
		if(!poolMap.containsKey(name)){
			poolMap.put(name, new Pool(name,MAXIMUM_POOL_SIZE));
		}
		return poolMap.get(name);
	}
	/**
	 * 创建一个线程池
	 */
	public  static Pool buildPool(String name,int max){
		if(null==poolMap)poolMap=new ConcurrentHashMap<String,Pool>();
		if(!poolMap.containsKey(name)){
			poolMap.put(name, new Pool(name,max));
		}
		return poolMap.get(name);
	}
	/**
	 * 清除线程池
	 */
	public static void clear() {
		if(null!=poolMap){
			poolMap.clear();	
		}
		poolMap=null;
	}
	
	public static class Pool{
		private ArrayList<Future<?>> futureTasks= null;
		private ExecutorService executorService =null;
		private boolean isThreadPoolClose= false;
		private String name=null;
		Pool(String name,int max){
			this.name=name;
			this.executorService=PoolManager.generateExecutorService(name,max);
			
			this.futureTasks=new ArrayList<Future<?>>();
			this.isThreadPoolClose=false;
		}
		public void close(){
			this.executorService.shutdownNow();
			this.futureTasks.clear();
			this.isThreadPoolClose=true;
			this.executorService=null;
		}
		public void cancle(boolean mayInterruptIfRunning){
			for(Future<?> future:futureTasks){
				future.cancel(mayInterruptIfRunning);
			}
		}
		public Future<?> submit(Runnable task){
			Future<?> future=this.executorService.submit(task);
			futureTasks.add(future);
			return future;
		}
		public boolean isTerminated(){
			return this.executorService.isTerminated();
		}
		public boolean isShutdown(){
			return this.executorService.isShutdown();
		}
		public ArrayList<Future<?>> getFutureTasks() {
			return futureTasks;
		}
		public void setFutureTasks(ArrayList<Future<?>> futureTasks) {
			this.futureTasks = futureTasks;
		}
		public ExecutorService getExecutorService() {
			return executorService;
		}
		public void setExecutorService(ExecutorService executorService) {
			this.executorService = executorService;
		}
		public boolean isThreadPoolClose() {
			return isThreadPoolClose;
		}
		public void setThreadPoolClose(boolean isThreadPoolClose) {
			this.isThreadPoolClose = isThreadPoolClose;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
}
