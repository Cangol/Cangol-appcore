package mobi.cangol.mobile.api;

import android.os.Handler;
import android.os.Message;
import android.os.Process;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DbTask<Params, Result> {

	private static final String TAG = "DbTask";

	private static final int CORE_POOL_SIZE = 1;
	private static final int MAXIMUM_POOL_SIZE = 10;
	private static final int KEEP_ALIVE = 10;

	private static final InternalHandler sHandler = new InternalHandler();
	
	private static final BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue<Runnable>(
			MAXIMUM_POOL_SIZE);

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "DbTask #" + mCount.getAndIncrement());
		}
	};

	private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			sWorkQueue, sThreadFactory);
	
    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_CANCEL = 0x3;
    
    private static final Map<String, List<WeakReference<Future<?>>>> futureMap= new WeakHashMap<String, List<WeakReference<Future<?>>>>();
    private String tag;
    public DbTask(String tag ){
    	this.tag=tag;
    }
    
	public void onPreExecute() {

	}
	public abstract Result doInBackground(Params... mParams);

	protected void onPostExecute(Result result) {

	}
	public void execute(Params... params) {
		 onPreExecute();
		 Future<?> future=sExecutor.submit(new DbTaskRunnable(sHandler,params));
		 if(tag != null) {
	            // Add request to request map
	            List<WeakReference<Future<?>>> requestList = futureMap.get(tag);
	            if(requestList == null) {
	                requestList = new LinkedList<WeakReference<Future<?>>>();
	                futureMap.put(tag, requestList);
	            }
	            requestList.add(new WeakReference<Future<?>>(future));
	     }
	}
	public static void cancel(String tag){
		cancel(tag,true);
	}
    public static void cancel(String tag, boolean mayInterruptIfRunning) {
        List<WeakReference<Future<?>>> requestList = futureMap.get(tag);
        if(requestList != null) {
            for(WeakReference<Future<?>> requestRef : requestList) {
            	Future<?> request = requestRef.get();
                if(request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
        futureMap.remove(tag);
    }
    
    public void onCancelled() {
		
	}

	public void onFinish(Result result) {
		onPostExecute(result);
	}
	
	private class DbTaskRunnable implements Runnable {
		private InternalHandler mHandler;
		private Params[] mParams;
		DbTaskRunnable(InternalHandler handler, Params[] params) {
			this.mHandler = handler;
			this.mParams=params;
		}
		
		@Override
		public void run() {
			Message message;
            Result result = null;
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			if (!Thread.currentThread().isInterrupted()) {
			 	try {
			 		result=doInBackground(mParams);
                }catch (CancellationException e) {
                	message = mHandler.obtainMessage(MESSAGE_POST_CANCEL, new AsyncTaskResult<Result>(DbTask.this, (Result[]) null));
                     message.sendToTarget();
                     return;
                }
			 	message = mHandler.obtainMessage(MESSAGE_POST_RESULT,new AsyncTaskResult<Result>(DbTask.this, result));
                message.sendToTarget();
			}else{
				message = mHandler.obtainMessage(MESSAGE_POST_CANCEL, new AsyncTaskResult<Result>(DbTask.this, (Result[]) null));
                message.sendToTarget();
			}
		}
	}

	private static class InternalHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			 AsyncTaskResult result = (AsyncTaskResult) msg.obj;
			switch (msg.what) {
				 case MESSAGE_POST_RESULT:
					 result.mTask.onFinish(result.mData[0]);
                    break;
	             case MESSAGE_POST_CANCEL:
	            	 result.mTask.onCancelled();
                    break;
			}
		}
	}

	 private static class AsyncTaskResult<Data> {
	        final DbTask mTask;
	        final Data[] mData;

	        AsyncTaskResult(DbTask task, Data... data) {
	            mTask = task;
	            mData = data;
	        }
	    }
	
}
