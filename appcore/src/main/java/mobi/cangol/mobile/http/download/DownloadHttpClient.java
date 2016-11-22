/**
 * Copyright (c) 2013 Cangol
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.http.download;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import mobi.cangol.mobile.service.PoolManager;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DownloadHttpClient {
    public final static String TAG = "DownloadHttpClient";
    private final static boolean DEBUG = false;
    private final static int DEFAULT_RETRYTIMES = 10;
    private final static int DEFAULT_CONNECT_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_READ_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_WRITE_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_MAX = 3;
    private static PoolManager.Pool threadPool;
    private final Map<Object, List<WeakReference<Future<?>>>> requestMap;
    private OkHttpClient httpClient;
    private DownloadRetryHandler downloadRetryHandler;
    private String group;

    protected DownloadHttpClient(final String group) {
        this.group = group;
        httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
        threadPool = PoolManager.buildPool(group, DEFAULT_MAX);
        requestMap = new WeakHashMap<Object, List<WeakReference<Future<?>>>>();
        this.downloadRetryHandler = new DownloadRetryHandler(DEFAULT_RETRYTIMES);

    }

    /**
     * 构造实例
     *
     * @param group
     * @return
     */
    public static DownloadHttpClient build(String group) {
        DownloadHttpClient asyncHttpClient = new DownloadHttpClient(group);
        return asyncHttpClient;
    }

    /**
     * 设置线程池
     *
     * @param pool
     */
    public static void setThreadPool(PoolManager.Pool pool) {
        threadPool = pool;
    }

    protected DownloadRetryHandler getDownloadRetryHandler() {
        return downloadRetryHandler;
    }

    /**
     * 发起请求
     *
     * @param tag
     * @param url
     * @param responseHandler
     * @param from
     * @param saveFile
     * @return
     */
    public Future<?> send(Object tag, String url, DownloadResponseHandler responseHandler, long from, String saveFile) {
        Request request = new Request.Builder()
                .tag(tag)
                .addHeader("Range", "bytes=" + from + "-")
                .url(url)
                .build();
        return sendRequest(request, responseHandler, saveFile);
    }

    protected Future<?> sendRequest(Request urlRequest, DownloadResponseHandler responseHandler, String saveFile) {
        Future<?> request = threadPool.submit(new DownloadThread(this, httpClient, urlRequest, responseHandler, saveFile));
        if (urlRequest.tag() != null) {
            // Add request to request map
            List<WeakReference<Future<?>>> requestList = requestMap.get(urlRequest.tag());
            if (requestList == null) {
                requestList = new LinkedList<WeakReference<Future<?>>>();
                requestMap.put(urlRequest.tag(), requestList);
            }
            requestList.add(new WeakReference<Future<?>>(request));
        }

        return request;
    }

    /**
     * 取消请求
     *
     * @param tag
     * @param mayInterruptIfRunning
     */
    public void cancelRequests(Object tag, boolean mayInterruptIfRunning) {

        List<WeakReference<Future<?>>> requestList = requestMap.get(tag);
        if (requestList != null) {
            for (WeakReference<Future<?>> requestRef : requestList) {
                Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
        requestMap.remove(tag);

        for (Call call : httpClient.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(group)) {
                call.cancel();
            }
        }
        for (Call call : httpClient.dispatcher().runningCalls()) {
            if (call.request().tag().equals(group)) {
                call.cancel();
            }
        }
    }

    /**
     * 取消所有
     */
    public void cancelAll() {
        httpClient.dispatcher().cancelAll();
        threadPool.cancle(true);
    }
}