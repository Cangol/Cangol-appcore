package mobi.cangol.mobile.service.download;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import mobi.cangol.mobile.service.PoolManager.Pool;
import android.os.Handler;
import android.os.Message;

public class DownloadTask {
	private Pool pool;
	private DownloadResource downloadResource;
	private DownloadHttpClient downloadHttpClient;
	private Future<?> future;
	private Handler handler;
	private DownloadNotification downloadNotification;
	private DownloadResponseHandler responseHandler=new DownloadResponseHandler(){
		@Override
		public void onWait() {
			super.onWait();
			if(downloadNotification!=null)downloadNotification.createNotification();
		}
		
		@Override
		public void onStart(long length) {
			super.onStart(length);
			downloadResource.setStatus(Download.STATUS_START);
			downloadResource.setFileLength(length);
			downloadResource.save();
			DownloadTask.this.sendMessage(Download.TYPE_DOWNLOAD_START,downloadResource);
		}

		@Override
		public void onStop(long end) {
			super.onStop(end);
			downloadResource.setCompleteSize(end);
			downloadResource.save();
			DownloadTask.this.sendMessage(Download.TYPE_DOWNLOAD_STOP,downloadResource);
			if(downloadNotification!=null)downloadNotification.cancelNotification();
			
		}
		
		@Override
		public void onFinish(long end) {
			super.onFinish(end);
			downloadResource.setStatus(Download.STATUS_FINISH);
			downloadResource.setCompleteSize(end);
			downloadResource.save();
			DownloadTask.this.sendMessage(Download.TYPE_DOWNLOAD_FINISH,downloadResource);
			if(downloadNotification!=null)downloadNotification.finishNotification();
		}

		@Override
		public void onProgressUpdate(long end, int progress, int speed) {
			super.onProgressUpdate(end, progress, speed);
			downloadResource.setSpeed(speed);
			downloadResource.setProgress(progress);
			downloadResource.setCompleteSize(end);
			DownloadTask.this.sendMessage(Download.TYPE_DOWNLOAD_UPDATE,downloadResource);
			if(downloadNotification!=null)downloadNotification.updateNotification(progress,speed);//speed 转换
		}
		
		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
			downloadResource.setException(content);
			downloadResource.setStatus(Download.STATUS_FAILURE);
			downloadResource.save();
			DownloadTask.this.sendMessage(Download.TYPE_DOWNLOAD_FAILED,downloadResource);
			if(downloadNotification!=null)downloadNotification.failureNotification();
		}
		
	};
	
	public DownloadTask(DownloadResource downloadResource,Pool pool,Handler handler) {
		this.downloadResource = downloadResource;
		this.pool=pool;
		this.handler=handler;
		downloadHttpClient=new DownloadHttpClient();
		downloadHttpClient.setThreadool((ThreadPoolExecutor) pool.getExecutorService());
	}
	public void setDownloadNotification(DownloadNotification downloadNotification) {
		this.downloadNotification = downloadNotification;
	}

	protected Future<?> exec(DownloadResource downloadResource,DownloadResponseHandler responseHandler){
		return downloadHttpClient.send(null, downloadResource.getUrl(), responseHandler, downloadResource.getCompleteSize(), downloadResource.getSourceFile());
	}
	
	protected void start(){
		downloadResource.setStatus(Download.STATUS_WAIT);
		future=exec(downloadResource,responseHandler);
		pool.getFutureTasks().add(future);
	}

	protected void stop(){
		future.cancel(true);
		future=null;
		downloadResource.setStatus(Download.STATUS_STOP);
		downloadResource.save();
		sendMessage(Download.TYPE_DOWNLOAD_STOP,downloadResource);
	}
	public void interrupt() {
		future.cancel(true);
		future=null;
		downloadResource.setStatus(Download.STATUS_RERUN);
		downloadResource.save();
		sendMessage(Download.TYPE_DOWNLOAD_STOP,downloadResource);
		
	}
	protected void restart(){
		future.cancel(true);
		downloadResource.reset();
		start();
		sendMessage(Download.TYPE_DOWNLOAD_CONTINUE,downloadResource);
	}
	
	public void resume() {
		downloadResource.setStatus(Download.STATUS_WAIT);
		future=exec(downloadResource,responseHandler);
		pool.getFutureTasks().add(future);
		sendMessage(Download.TYPE_DOWNLOAD_CONTINUE,downloadResource);
	}
	
	protected void remove() {
		future.cancel(true);
		sendMessage(Download.TYPE_DOWNLOAD_DELETE,downloadResource);
		
	}
	
	public void sendMessage(int what,DownloadResource obj){
		  Message msg =handler.obtainMessage(what);
		  msg.obj = obj;
		  msg.sendToTarget();
	}
	
}
