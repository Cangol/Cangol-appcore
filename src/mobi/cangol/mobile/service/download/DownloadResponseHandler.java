package mobi.cangol.mobile.service.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class DownloadResponseHandler{
	public final static  String TAG = "DownloadResponseHandler";
	private final static boolean DEBUG=false;
	protected static final int WAIT_MESSAGE = 0;
	protected static final int START_MESSAGE = 1;
	protected static final int PROGRESS_MESSAGE = 2;
	protected static final int FINISH_MESSAGE = 3;
	protected static final int FAILURE_MESSAGE = 4;
	protected static final int COMPLETE_MESSAGE = 5;
	
	private final int BUFF_SIZE=4*1024;
	private Handler handler;
    public DownloadResponseHandler() {
        if(Looper.myLooper() != null) {
            handler = new Handler(){
                public void handleMessage(Message msg){
                	DownloadResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }

	public void onWait() {
    	if(DEBUG)Log.d(TAG, "onWait...");
    }
    public void onStart(long from) {
    	if(DEBUG)Log.d(TAG, "onStart from="+from);
    }
    public void onFinish(long end) {
    	if(DEBUG)Log.d(TAG, "onFinish end="+end);
    }
    public void onComplete(long end) {
    	if(DEBUG)Log.d(TAG, "onComplete end="+end);
    }
    public void onProgressUpdate(int progress,int speed) {
    	if(DEBUG)Log.d(TAG, "progress="+progress+" speed:"+speed);
    }
    public void onFailure(Throwable error, String content) {
    	if(DEBUG)Log.d(TAG, "error="+error+" content:"+content);
    }
    public void sendWaitMessage() {
    	sendMessage(obtainMessage(WAIT_MESSAGE, null));
    }
    public void sendStartMessage(long from) {
    	sendMessage(obtainMessage(START_MESSAGE, new Object[]{from}));
    }
    public void sendFinishMessage(long end) {
    	sendMessage(obtainMessage(FINISH_MESSAGE, new Object[]{end}));
    }
    public void sendCompleteMessage(long end) {
    	sendMessage(obtainMessage(COMPLETE_MESSAGE, new Object[]{end}));
    }
    public void sendProgressMessage(int progress, int speed) {
    	sendMessage(obtainMessage(PROGRESS_MESSAGE, new Object[]{progress, speed}));
	}
	public void sendFailureMessage(IOException e, String responseBody) {
		 sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
	}
	void sendResponseMessage(HttpResponse response,long from,String saveFile) throws IOException{
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT 
				||response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ) {
			Log.d(TAG, "StatusCode="+response.getStatusLine().getStatusCode()); 
			HttpEntity entity = response.getEntity();
			long length = entity.getContentLength();
			RandomAccessFile threadfile= new RandomAccessFile(saveFile, "rwd");  
			InputStream inputStream = entity.getContent(); 
			long oldLength=threadfile.length();
			sendStartMessage(oldLength);
			if(oldLength<length){
				threadfile.seek(oldLength);  
				byte[] block = new byte[BUFF_SIZE];
				long starTime = System.currentTimeMillis();
				long startLength=0;
				int readCount = 0;
				while (!Thread.currentThread().isInterrupted()&&( readCount = inputStream.read(block, 0, BUFF_SIZE)) != -1) {
					threadfile.write(block, 0, readCount);
					oldLength += readCount;
					startLength+=readCount;
					if((System.currentTimeMillis()-starTime)>1000L) {
						int progres=(int) (oldLength*1.0f/length*100);
						int speed=(int) (startLength*1000.0f/(System.currentTimeMillis()-starTime));
						sendProgressMessage(progres,speed);
						starTime=System.currentTimeMillis();
						startLength=0;
					}
				}
				if(oldLength==length){
					sendProgressMessage(100,0);
					sendCompleteMessage(threadfile.length());
				}else
				sendFinishMessage(threadfile.length());
			}else if(oldLength==length){
				sendProgressMessage(100,0);
				sendCompleteMessage(threadfile.length());
			}else{
				sendFailureMessage(new IOException(),"oldfile error oldLength>length");
			}
		}else{
			sendFailureMessage(new IOException(),"StatusCode "+response.getStatusLine().getStatusCode());
		}
    }
	protected void handleMessage(Message msg) {
		Object[] response = null;	
    	 switch(msg.what) {
	    	 case PROGRESS_MESSAGE:
	             response = (Object[])msg.obj;
	             handleProgressMessage(((Integer) response[0]).intValue(),((Integer) response[1]).intValue());
	             break;
	         case FAILURE_MESSAGE:
	             response = (Object[])msg.obj;
	             handleFailureMessage((Throwable)response[0], (String)response[1]);
	             break;
	         case FINISH_MESSAGE:
	             response = (Object[])msg.obj;
	             handleFinishMessage(((Long) response[0]).longValue());
	             break;
	         case START_MESSAGE:
	        	 response = (Object[])msg.obj;
	             handleStartMessage(((Long) response[0]).longValue());
	             break;
	         case WAIT_MESSAGE:
	             handleWaitMessage();
	             break;
	         case COMPLETE_MESSAGE:
	             response = (Object[])msg.obj;
	             handleCompleteMessage(((Long) response[0]).longValue());
	             break;
    	 }
    }
	
	protected void handleWaitMessage() {
		 onWait();
	}
	
	protected void handleStartMessage(long from) {
		 onStart(from);
	}
	
	protected void handleCompleteMessage(long end) {
        onComplete(end);
    }
	
	protected void handleFinishMessage(long end) {
        onFinish(end);
    }
	
    protected void handleProgressMessage(int progress,int speed) {
        onProgressUpdate(progress, speed);
    }
    
    protected void handleFailureMessage(Throwable e, String responseBody) {
        onFailure(e, responseBody);
    }
    protected void sendMessage(Message msg) {
        if(handler != null){
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }
    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if(handler != null){
            msg = this.handler.obtainMessage(responseMessage, response);
        }else{
            msg = new Message();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }
}