package mobi.cangol.mobile.service.download;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

public class DownloadThread extends Thread{
	private final static boolean DEBUG=false;
	public final static  String TAG = "DownloadThread";
	private AbstractHttpClient client;
	private HttpContext context;
	private HttpUriRequest request;
	private final DownloadResponseHandler responseHandler;
	private int executionCount;
	private long from;
	private String saveFile;
	public DownloadThread(AbstractHttpClient client,
			HttpContext context, HttpUriRequest request,DownloadResponseHandler responseHandler,long from,String saveFile) {
		this.client=client;
		this.context=context;
		this.request=request;
		this.responseHandler=responseHandler;
		this.from=from;
		this.saveFile=saveFile;
	}

	public void run() {
        try {
        	 if(responseHandler != null) {
	              responseHandler.sendWaitMessage();
	            }
            makeRequestWithRetries();
        } catch (IOException e) {
            if(responseHandler != null) {
              responseHandler.sendFailureMessage(e, (String) null);
            }
        }
    }

    private void makeRequest() throws IOException {
        if(!Thread.currentThread().isInterrupted()) {
        	request.addHeader("Range", "bytes="+from+"-");
            HttpResponse response = client.execute(request, context);
            if(!Thread.currentThread().isInterrupted()) {
                if(responseHandler != null) {
                    responseHandler.sendResponseMessage(response,from,saveFile);
                }
            } else{
            	if(DEBUG)Log.d(TAG, "Thread.isInterrupted");
                //TODO: should raise InterruptedException? this block is reached whenever the request is cancelled before its response is received
            }
        }else{
        	if(DEBUG)Log.d(TAG, "Thread.isInterrupted");
        }
    }

    private void makeRequestWithRetries() throws ConnectException {
        // This is an additional layer of retry logic lifted from droid-fu
        // See: https://github.com/kaeppler/droid-fu/blob/master/src/main/java/com/github/droidfu/http/BetterHttpRequestBase.java
        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = this.client.getHttpRequestRetryHandler();
        while (retry&&!Thread.currentThread().isInterrupted()) {
            try {
                makeRequest();
                return;
            } catch (UnknownHostException e) {
		        if(responseHandler != null) {
		            responseHandler.sendFailureMessage(e, "can't resolve host");
		        }
	        	return;
            } catch (SocketException e){
                // Added to detect host unreachable
                if(responseHandler != null) {
                    responseHandler.sendFailureMessage(e, "can't resolve host");
                }
                return;
            } catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            } catch (NullPointerException e) {
                // there's a bug in HttpClient 4.0.x that on some occasions causes
                // DefaultRequestExecutor to throw an NPE, see
                // http://code.google.com/p/android/issues/detail?id=5255
                cause = new IOException("NPE in HttpClient" + e.getMessage());
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            }
        }

        // no retries left, crap out with exception
        ConnectException ex = new ConnectException();
        ex.initCause(cause);
        throw ex;
    }
	
}
