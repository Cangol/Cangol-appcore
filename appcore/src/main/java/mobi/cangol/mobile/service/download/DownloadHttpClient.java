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

import android.content.Context;
import android.util.Log;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Future;

import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.PoolManager.Pool;

public class DownloadHttpClient {
	private final static boolean DEBUG=false;
	public final static  String TAG = "PollingHttpClient";
    private DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private final Map<Context, List<WeakReference<Future<?>>>> requestMap;
    private final static int DEFAULT_RETRYTIMES=10;
    private final static int DEFAULT_SOCKET_TIMEOUT = 50 * 1000;
    private final static int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    private Pool threadPool;
    protected DownloadHttpClient(String group) {

        httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        httpClient = new DefaultHttpClient();
        ClientConnectionManager mgr = httpClient.getConnectionManager();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, DEFAULT_SOCKET_BUFFER_SIZE);
        HttpClientParams.setRedirecting(params, true);
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
        httpClient.setHttpRequestRetryHandler(new DownloadRetryHandler(DEFAULT_RETRYTIMES));
		threadPool = PoolManager.getPool(group);

        requestMap = new WeakHashMap<Context, List<WeakReference<Future<?>>>>();
    }
    public  static DownloadHttpClient build(String group) {
    	DownloadHttpClient asyncHttpClient=new DownloadHttpClient(group);
		return asyncHttpClient;
	}
    public  static void cancel(String group,boolean mayInterruptIfRunning) {
    	PoolManager.getPool(group).cancle(mayInterruptIfRunning);
	}
    public void setRetryHandler(HttpRequestRetryHandler retryHandler) {
    	httpClient.setHttpRequestRetryHandler(retryHandler);
    }
    public void setThreadool(Pool pool) {
        this.threadPool = pool;
    }
    public Future<?> send(Context context, String url, DownloadResponseHandler responseHandler, long from, String saveFile) {
        HttpUriRequest request = new HttpGet(url);
        if (DEBUG) Log.d(TAG, "url:" + request.getURI().toString());
        return sendRequest(httpClient, httpContext, request, null, responseHandler, context, from, saveFile);
    }

    protected Future<?> sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, DownloadResponseHandler responseHandler, Context context, long from, String saveFile) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }
        Future<?> request = threadPool.submit(new DownloadThread(client, httpContext, uriRequest, responseHandler, from, saveFile));
        if (context != null) {
            // Add request to request map
            List<WeakReference<Future<?>>> requestList = requestMap.get(context);
            if (requestList == null) {
                requestList = new LinkedList<WeakReference<Future<?>>>();
                requestMap.put(context, requestList);
            }
            requestList.add(new WeakReference<Future<?>>(request));
        }
        return request;
    }

    public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
        List<WeakReference<Future<?>>> requestList = requestMap.get(context);
        if (requestList != null) {
            for (WeakReference<Future<?>> requestRef : requestList) {
                Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(mayInterruptIfRunning);
                    if (DEBUG) Log.d(TAG, "cancelRequests");
                }
            }
        }
        requestMap.remove(context);
    }

}