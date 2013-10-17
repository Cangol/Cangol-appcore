package mobi.cangol.mobile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * TheadPool manager by name
 * @author xuewu.wei
 *
 */
public class PoolManager {
	private static ConcurrentHashMap<String,Pool> poolMap=null;
	private static ExecutorService generateExecutorService(final String name,int max){
		ExecutorService executorService= Executors.newFixedThreadPool(max,new ThreadFactory() {
	        private final AtomicInteger mCount = new AtomicInteger(1);

	        public Thread newThread(final Runnable r) {
	            return new Thread(r, name+"$workThread #" + mCount.getAndIncrement());
	        }
	    });
	    return executorService;
	} 
	public  static Pool buildPool(String name,int max){
		if(null==poolMap)poolMap=new ConcurrentHashMap<String,Pool>();
		if(!poolMap.containsKey(name)){
			poolMap.put(name, new Pool(name,max));
		}
		return poolMap.get(name);
	}
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
