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
package mobi.cangol.mobile.http.extras;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import android.content.Context;
import android.util.Log;

public class RouteHttpClient {
	private final static boolean DEBUG=true;
	public final static  String TAG = "RouteHttpClient";
    private DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private ThreadPoolExecutor threadPool;
    private final Map<Context, List<WeakReference<Future<?>>>> requestMap;
    public RouteHttpClient() {
        
    	httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		httpClient = new DefaultHttpClient();
		ClientConnectionManager mgr = httpClient.getConnectionManager();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, false);
		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
      
		threadPool = (ThreadPoolExecutor)Executors.newCachedThreadPool();

        requestMap = new WeakHashMap<Context, List<WeakReference<Future<?>>>>();
    }
	public void send(Context context,String url, HashMap<String,String> params,RouteResponseHandler responseHandler,String... host) {
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
    	
    	if(params!=null)
        for(ConcurrentHashMap.Entry<String, String> entry : params.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
    	
        String paramString=URLEncodedUtils.format(lparams, "UTF-8");
        
        HttpUriRequest request=new HttpGet(url+"?"+paramString);
        sendRequest(httpClient,httpContext,request,null,responseHandler,context,host);
    }
	private HttpUriRequest getHttpUriRequest(URL url,String host){
		
		String hostStr = url.getHost();
		String urlStr=url.toString().replace(hostStr, host);
		HttpUriRequest request=new HttpGet(urlStr);
		
		return request;
	}
    protected void sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType,RouteResponseHandler responseHandler, Context context,String... host) {
        if(contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        Future<?> request = threadPool.submit(new HttpRequestTask(client, httpContext, uriRequest,responseHandler,host));
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
    public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
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
    	private final RouteResponseHandler responseHandler;
    	private String[] host; 
		public HttpRequestTask(DefaultHttpClient client,
				HttpContext context, HttpUriRequest request,RouteResponseHandler responseHandler,String... host) {
			this.client=client;
			this.context=context;
			this.request=request;
			this.responseHandler=responseHandler;
			this.host=host;
		}

		@Override
		public void run() {
			 if(!Thread.currentThread().isInterrupted()) {
				 responseHandler.sendStartMessage();
				 int exec=0;
				 while(exec<host.length){
					 try {
						 	request=getHttpUriRequest(request.getURI().toURL(),host[exec]);
						 	Log.d(TAG, ""+request.getURI().toURL());
						 	exec++;
						 	client.execute(request, context);
							HttpResponse response = client.execute(request, context);
							if(!Thread.currentThread().isInterrupted()) {
				                if(responseHandler != null) {
				                    if(responseHandler.sendResponseMessage(response))
				                    	break;
				                }
				            }else{
				            	if(DEBUG)Log.d(TAG, "Thread.isInterrupted");
				            	break;
				            }
						} catch (IOException e) {
							if(exec>=host.length){
								responseHandler.sendFailureMessage(e, "IOException");
								break;
							}continue;
						}
				 }
		      }
		}
    }
}

