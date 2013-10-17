package mobi.cangol.mobile.service.download;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import mobi.cangol.mobile.service.PoolManager.Pool;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class DownloadExecutor {
	private ArrayList<DownloadResource> mDownloadRes=new ArrayList<DownloadResource>();
	private ArrayList<WeakReference<DownloadStatusListener>> listeners=new  ArrayList<WeakReference<DownloadStatusListener>>();
	private Pool mPool;
	private Context context;
	private DownloadEvent mDownloadEvent;
	private ExecutorHandler mHandler;
	public DownloadExecutor(String name) {
		this.mHandler=new ExecutorHandler(this);
		mDownloadRes.addAll(scanResource());
	}
	public void setPool(Pool pool) {
		this.mPool = pool;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public void setDownloadEvent(DownloadEvent downloadEvent) {
		this.mDownloadEvent = downloadEvent;
	}
	
	public abstract ArrayList<DownloadResource> scanResource();
	
	public abstract DownloadNotification notification(Context context,DownloadResource resource);
	
	public abstract DownloadResource createResource(String url,String name);


	public DownloadResource getDownloadResource(String key) {
		for(DownloadResource resource:mDownloadRes){
			if(key!=null&&key.equals(resource.getKey())){
				return resource;
			}
		}
		return null;
	}
	public void add(DownloadResource resource) {
		if(mDownloadRes.contains(resource)){
			return;
		}else{
			DownloadTask downloadTask=new DownloadTask(resource,mPool,mHandler);
			resource.setDownloadTask(downloadTask);
			downloadTask.setDownloadNotification(notification(context,resource));
			downloadTask.start();
			synchronized(mDownloadRes){
				mDownloadRes.add(resource);
			}
		}
		
	}
	public void start(DownloadResource resource) {
		if(mDownloadRes.contains(resource)){
			DownloadTask downloadTask=resource.getDownloadTask();
			downloadTask.start();
		}
	}
	public void stop(DownloadResource resource) {
		if(mDownloadRes.contains(resource)){
			DownloadTask downloadTask=resource.getDownloadTask();
			downloadTask.stop();
		}
	}
	
	public  void resume(DownloadResource resourceInfo) {
		if(mDownloadRes.contains(resourceInfo)){
			DownloadTask downloadTask=resourceInfo.getDownloadTask();
			downloadTask.resume();
		}
	}
	
	public void restart(DownloadResource resource) {
		if(mDownloadRes.contains(resource)){
			DownloadTask downloadTask=resource.getDownloadTask();
			downloadTask.restart();
		}
	}

	public void remove(DownloadResource resource) {
		synchronized(mDownloadRes){
			if(mDownloadRes.contains(resource)){
				DownloadTask downloadTask=resource.getDownloadTask();
				downloadTask.remove();
				mDownloadRes.remove(resource);
			}
		}
	}

	public void recoverAll() {
		synchronized(mDownloadRes){
			DownloadTask downloadTask=null;
			for(DownloadResource resource:mDownloadRes){
				downloadTask=resource.getDownloadTask();
				if(resource.getStatus()==Download.STATUS_RERUN){
					downloadTask.resume();
				}
			}
		}
	}

	public void interruptAll() {
		synchronized(mDownloadRes){
			DownloadTask downloadTask=null;
			for(DownloadResource resource:mDownloadRes){
				downloadTask=resource.getDownloadTask();
				if(resource.getStatus()<Download.STATUS_STOP){
					downloadTask.interrupt();
				}
			}
		}
	}
	
	public void close() {
		synchronized(mDownloadRes){
			DownloadTask downloadTask=null;
			for(DownloadResource resource:mDownloadRes){
				downloadTask=resource.getDownloadTask();
				if(downloadTask!=null)
					downloadTask.stop();
			}
		}	
		mDownloadRes.clear();
		mPool.close();
	}
	
	public void registerDownloadStatusListener(DownloadStatusListener downloadStatusListener){
		if(null==downloadStatusListener)
			throw new IllegalArgumentException("downloadStatusListener is null!");
		boolean isExist=false;
		for(WeakReference<DownloadStatusListener> listener:listeners){
			if(downloadStatusListener.equals(listener.get())){
				isExist=true;
				break;
			}
		}
		if(!isExist)listeners.add(new WeakReference<DownloadStatusListener>(downloadStatusListener));
	}
	
	public void unregisterDownloadStatusListener(DownloadStatusListener downloadStatusListener){
		if(null==downloadStatusListener)
			throw new IllegalArgumentException("downloadStatusListener is null!");
		for(WeakReference<DownloadStatusListener> listener:listeners){
			if(downloadStatusListener.equals(listener.get())){
				listeners.remove(listener);
				break;
			}
		}
	}
	
	private void notifyUpdateStatus(DownloadResource resource,int type){
		for (WeakReference<DownloadStatusListener> listener:listeners) {
			if(null!=listener.get())listener.get().onStatusChange(resource,type);
		}
	}
	
	private void _handleMessage(Message msg) {
		switch (msg.what) {
			case Download.TYPE_DOWNLOAD_START:
				if(null!=mDownloadEvent)mDownloadEvent.onStart((DownloadResource) msg.obj);
			case Download.TYPE_DOWNLOAD_FINISH:
				if(null!=mDownloadEvent)mDownloadEvent.onFinish((DownloadResource) msg.obj);
			case Download.TYPE_DOWNLOAD_FAILED:
				if(null!=mDownloadEvent)mDownloadEvent.onFailure((DownloadResource) msg.obj);
			default:
				notifyUpdateStatus((DownloadResource) msg.obj,msg.what);
				break;
		}
	}
	
	final static class ExecutorHandler extends Handler {
		private final WeakReference<DownloadExecutor> mDownloadExecutor;
		public ExecutorHandler(DownloadExecutor downloadExecutor) {
			mDownloadExecutor = new WeakReference<DownloadExecutor>(downloadExecutor);
		}

		public void handleMessage(Message msg) {
			DownloadExecutor downloadExecutor = mDownloadExecutor.get();
		   if (downloadExecutor != null) {
			   downloadExecutor._handleMessage(msg);
		   }
       }
	}
}
