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

import mobi.cangol.mobile.http.HttpClientFactory;
import mobi.cangol.mobile.service.PoolManager;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DownloadHttpClient {
    public static final  String TAG = "DownloadHttpClient";
    private static final  int DEFAULT_RETRYTIMES = 10;
    private static final  int DEFAULT_MAX = 3;
    private static PoolManager.Pool threadPool;
    private final Map<Object, List<WeakReference<Future<?>>>> requestMap;
    private OkHttpClient httpClient;
    private DownloadRetryHandler downloadRetryHandler;
    private String group;

    protected DownloadHttpClient(final String group, boolean safe) {
        this.group = group;
        this.httpClient = HttpClientFactory.createDefaultHttpClient();
        threadPool = PoolManager.buildPool(group, DEFAULT_MAX);
        this.requestMap = new WeakHashMap<>();
        this.downloadRetryHandler = new DownloadRetryHandler(DEFAULT_RETRYTIMES);

    }

    /**
     * 构造实例
     *
     * @param group
     * @return
     */
    public static DownloadHttpClient build(String group) {
        return build(group, true);
    }

    /**
     * 构造实例
     *
     * @param group
     * @param safe
     * @return
     */
    public static DownloadHttpClient build(String group, boolean safe) {
        return new DownloadHttpClient(group, safe);
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
    public Future send(Object tag, String url, DownloadResponseHandler responseHandler, long from, String saveFile) {
        final Request request = new Request.Builder()
                .tag(tag)
                .addHeader("Range", "bytes=" + from + "-")
                .url(url)
                .build();
        return sendRequest(request, responseHandler, saveFile);
    }

    protected Future sendRequest(Request urlRequest, DownloadResponseHandler responseHandler, String saveFile) {
        final Future<?> request = threadPool.submit(new DownloadThread(this, httpClient, urlRequest, responseHandler, saveFile));
        if (urlRequest.tag() != null) {
            // Add request to request map
            List<WeakReference<Future<?>>> requestList = requestMap.get(urlRequest.tag());
            if (requestList == null) {
                requestList = new LinkedList<>();
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

        final List<WeakReference<Future<?>>> requestList = requestMap.get(tag);
        if (requestList != null) {
            for (final WeakReference<Future<?>> requestRef : requestList) {
                final Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
        requestMap.remove(tag);

        for (final Call call : httpClient.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(group)) {
                call.cancel();
            }
        }
        for (final Call call : httpClient.dispatcher().runningCalls()) {
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
    }

    public void close() {
        threadPool.close(false);
    }
}