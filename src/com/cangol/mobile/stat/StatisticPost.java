package com.cangol.mobile.stat;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * @Description StatisticPost.java 
 * @author xuewu.wei
 * @date 2013-3-3
 */
public class StatisticPost {
	private final static boolean debug=true;
	public final static  String TAG = "StatisticPost";
    private final AndroidHttpClient httpClient;
    private final HttpContext httpContext;
    private ThreadPoolExecutor threadPool;
    private final Map<Object, List<WeakReference<Future<?>>>> requestMap;
    public StatisticPost() {
        
    	httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		httpClient =AndroidHttpClient.newInstance("android");

        threadPool = (ThreadPoolExecutor)Executors.newCachedThreadPool();

        requestMap = new WeakHashMap<Object, List<WeakReference<Future<?>>>>();
    }
    protected void send(Object context,String url, HashMap<String,String> params){
    	send(context,url,params,null);
    }
    protected void send(Object context,String url, HashMap<String,String> params,ResponseHandler responseHandler) {
    	List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for(ConcurrentHashMap.Entry<String, String> entry : params.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        String paramString=URLEncodedUtils.format(lparams, "UTF-8");
        HttpUriRequest request = new HttpGet(url+"?"+paramString);
        if(debug)Log.d(TAG, "url:"+request.getURI().toString());
        sendRequest(httpClient,httpContext,request,null,responseHandler,context);
    }

    protected void sendRequest(HttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType,ResponseHandler responseHandler, Object context) {
        if(contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        Future<?> request = threadPool.submit(new HttpRequestTask(client, httpContext, uriRequest, responseHandler));

        if(context != null) {
            // Add request to request map
            List<WeakReference<Future<?>>> requestList = requestMap.get(context);
            if(requestList == null) {
                requestList = new LinkedList<WeakReference<Future<?>>>();
                requestMap.put(context, requestList);
            }
            requestList.add(new WeakReference<Future<?>>(request));
        }
    }
    protected void cancelRequests(Object context, boolean mayInterruptIfRunning) {
        List<WeakReference<Future<?>>> requestList = requestMap.get(context);
        if(requestList != null) {
            for(WeakReference<Future<?>> requestRef : requestList) {
            	Future<?> request = requestRef.get();
                if(request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
        requestMap.remove(context);
    }
    class HttpRequestTask implements Runnable{
    	private HttpClient client;
    	private HttpContext context;
    	private HttpUriRequest request;
    	private final ResponseHandler responseHandler;
		public HttpRequestTask(HttpClient client,
				HttpContext context, HttpUriRequest request,ResponseHandler responseHandler) {
			this.client=client;
			this.context=context;
			this.request=request;
			this.responseHandler=responseHandler;
		}

		@Override
		public void run() {
			 if(!Thread.currentThread().isInterrupted()) {
		            try {
						HttpResponse response = client.execute(request, context);
						if(debug)Log.d("HttpRequestTask", "response:"+EntityUtils.toString(response.getEntity(), HTTP.UTF_8));
						if(!Thread.currentThread().isInterrupted()) {
			                if(responseHandler != null) {
			                    responseHandler.sendResponseMessage(response);
			                }else{
			                }
			            } else{
			            }
					} catch (ClientProtocolException e) {	
						//e.printStackTrace();
						Log.d(TAG, "ClientProtocolException");
					} catch (IOException e) {
						//e.printStackTrace();
						Log.d(TAG, "IOException");
					}
		      }
		}
    	
    }
}
class ResponseHandler{
	protected static final int SUCCESS_MESSAGE = 0;
	protected static final int FAILURE_MESSAGE = 1;
	private Handler handler;
    public ResponseHandler() {
        if(Looper.myLooper() != null) {
            handler = new Handler(){
                public void handleMessage(Message msg){
                	ResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }
    public void onSuccess(int statusCode, String content) {
    	Log.d("ResponseHandler", "statusCode="+statusCode+" content:"+content);
    }
    public void onFailure(Throwable error, String content) {
    	Log.d("ResponseHandler", "error="+error+" content:"+content);
    }
    public void sendSuccessMessage(int statusCode, String responseBody) {
    	sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{Integer.valueOf(statusCode), responseBody}));
	}
	public void sendFailureMessage(IOException e, String responseBody) {
		 sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
	}
	void sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        String responseBody = null;
        try {
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if(temp != null) {
                entity = new BufferedHttpEntity(temp);
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
        } catch(IOException e) {
            sendFailureMessage(e, (String) null);
        }

        if(status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
        } else {
            sendSuccessMessage(status.getStatusCode(), responseBody);
        }
    }
	protected void handleMessage(Message msg) {
		Object[] response;
    	 switch(msg.what) {
    	 case SUCCESS_MESSAGE:
             response = (Object[])msg.obj;
             handleSuccessMessage(((Integer) response[0]).intValue(), (String) response[1]);
             break;
         case FAILURE_MESSAGE:
             response = (Object[])msg.obj;
             handleFailureMessage((Throwable)response[0], (String)response[1]);
             break;
    	 }
    }
    protected void handleSuccessMessage(int statusCode, String responseBody) {
        onSuccess(statusCode, responseBody);
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
