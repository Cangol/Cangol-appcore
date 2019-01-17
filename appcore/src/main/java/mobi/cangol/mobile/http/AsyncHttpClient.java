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
package mobi.cangol.mobile.http;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.PoolManager.Pool;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 异步http请求client
 * 基于okio,okhttp3实现
 */
public class AsyncHttpClient {
    private final static String TAG = "AsyncHttpClient";
    private final static int DEFAULT_RETRY_TIMES = 3;
    private final OkHttpClient httpClient;
    private final Map<Object, List<WeakReference<Future<?>>>> requestMap;

    private Pool threadPool;
    private RetryHandler retryHandler;
    private String group;

    private AsyncHttpClient(String group) {
        this.group = group;
        this.httpClient = HttpClientFactory.createDefaultHttpClient();
        this.threadPool = PoolManager.getPool(group);
        this.requestMap = new WeakHashMap<Object, List<WeakReference<Future<?>>>>();
        this.retryHandler = new RetryHandler(DEFAULT_RETRY_TIMES);
    }

    private AsyncHttpClient(String group, OkHttpClient client) {
        this.group = group;
        this.httpClient = client;
        this.threadPool = PoolManager.getPool(group);
        this.requestMap = new WeakHashMap<Object, List<WeakReference<Future<?>>>>();
        this.retryHandler = new RetryHandler(DEFAULT_RETRY_TIMES);
    }

    /**
     * 构造一个实例
     *
     * @param group
     * @return
     */
    public static AsyncHttpClient build(String group) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(group);
        return asyncHttpClient;
    }

    /**
     * 构造一个实例
     *
     * @param group
     * @param client
     * @return
     */
    public static AsyncHttpClient build(String group, OkHttpClient client) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(group, client);
        return asyncHttpClient;
    }

    /**
     * 设置线程池
     *
     * @param pool
     */
    public void setThreadPool(Pool pool) {
        this.threadPool = pool;
    }

    /**
     * 获取RetryHandler
     *
     * @return
     */
    protected RetryHandler getRetryHandler() {
        return retryHandler;
    }

    /**
     * 执行get方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    public void get(Object context, String url, AsyncHttpResponseHandler responseHandler) {
        get(context, url, null, null, responseHandler);
    }

    /**
     * 执行get方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    public void get(Object context, String url, HashMap<String, String> headers, HashMap<String, String> params, AsyncHttpResponseHandler responseHandler) {

        StringBuilder sb = new StringBuilder(url.contains("?") ? "" : "?");
        if (params != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey())
                        .append('&')
                        .append(entry.getValue());
            }
        }
        execMethod("GET", context, url + sb.toString(), headers, (HashMap<String, String>) null, responseHandler);
    }

    /**
     * 执行get方法
     *
     * @param context
     * @param url
     * @param requestParams
     * @param responseHandler
     */
    public void get(Object context, String url, RequestParams requestParams, AsyncHttpResponseHandler responseHandler) {
        StringBuilder sb = new StringBuilder(url.contains("?") ? "" : "?");
        if (requestParams.urlParams != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : requestParams.urlParams.entrySet()) {
                sb.append(entry.getKey())
                        .append('&')
                        .append(entry.getValue());
            }
        }

        execMethod("GET", context, url, null, (HashMap<String, String>) null, responseHandler);
    }

    /**
     * 执行patch方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    public void patch(Object context, String url, AsyncHttpResponseHandler responseHandler) {
        patch(context, url, null, null, responseHandler);
    }

    /**
     * 执行patch方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    public void patch(Object context, String url, HashMap<String, String> headers, HashMap<String, String> params, AsyncHttpResponseHandler responseHandler) {
        execMethod("PATCH", context, url, headers, params, responseHandler);
    }

    /**
     * 执行post方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    public void post(Object context, String url, AsyncHttpResponseHandler responseHandler) {
        post(context, url, null, null, responseHandler);
    }

    /**
     * 执行post方法
     *
     * @param context
     * @param url
     * @param json
     * @param responseHandler
     */
    public void post(Object context, String url, JSONObject json, AsyncHttpResponseHandler responseHandler) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        execMethod("POST", context, url, null, requestBody, responseHandler);
    }

    /**
     * 执行post方法
     *
     * @param context
     * @param url
     * @param requestParams
     * @param responseHandler
     */
    public void post(Object context, String url, RequestParams requestParams, AsyncHttpResponseHandler responseHandler) {
        execMethod("POST", context, url, null, requestParams, responseHandler);
    }

    /**
     * 执行post方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    public void post(Object context, String url, HashMap<String, String> headers, HashMap<String, String> params, AsyncHttpResponseHandler responseHandler) {
        execMethod("POST", context, url, headers, params, responseHandler);
    }

    /**
     * 执行put方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    public void put(Object context, String url, AsyncHttpResponseHandler responseHandler) {
        put(context, url, null, null, responseHandler);
    }

    /**
     * 执行put方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    public void put(Object context, String url, HashMap<String, String> headers, HashMap<String, String> params, AsyncHttpResponseHandler responseHandler) {
        execMethod("PUT", context, url, headers, params, responseHandler);
    }

    /**
     * 执行delete方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    public void delete(Object context, String url, AsyncHttpResponseHandler responseHandler) {
        delete(context, url, null, null, responseHandler);
    }

    /**
     * 执行delete方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    public void delete(Object context, String url, HashMap<String, String> headers, HashMap<String, String> params, AsyncHttpResponseHandler responseHandler) {
        execMethod("DELETE", context, url, headers, params, responseHandler);
    }

    /**
     * 执行方法
     *
     * @param method
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    public void execMethod(String method, Object context, String url, HashMap<String, String> headers, HashMap<String, String> params, AsyncHttpResponseHandler responseHandler) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        if (params != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : params.entrySet()) {
                requestBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = null;
        if ("GET".equals(method.toUpperCase())) {
            request = new Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .get()
                    .build();
        } else {
            request = new Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .method(method, requestBodyBuilder.build())
                    .build();
        }
        sendRequest(httpClient, request, responseHandler, context);
    }

    /**
     * 执行方法
     *
     * @param method
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    public void execMethod(String method, Object context, String url, HashMap<String, String> headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (params.fileParams != null) {
            for (ConcurrentHashMap.Entry<String, File> entry : params.fileParams.entrySet()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), entry.getValue());
                requestBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue().getName(), fileBody);
            }
        }

        if (params.urlParams != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : params.urlParams.entrySet()) {
                requestBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        Request request = null;
        if ("GET".equals(method.toUpperCase())) {
            request = new Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .get()
                    .build();
        } else {
            request = new Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .method(method, requestBodyBuilder.build())
                    .build();
        }

        sendRequest(httpClient, request, responseHandler, context);
    }

    /**
     * @param method
     * @param context
     * @param url
     * @param headers
     * @param requestBody
     * @param responseHandler
     */
    public void execMethod(String method, Object context, String url, HashMap<String, String> headers, RequestBody requestBody, AsyncHttpResponseHandler responseHandler) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        Request request = null;
        if ("GET".equals(method.toUpperCase())) {
            request = new Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .get()
                    .build();
        } else {
            request = new Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .method(method, requestBody)
                    .build();
        }

        sendRequest(httpClient, request, responseHandler, context);
    }

    protected void sendRequest(OkHttpClient client, Request uriRequest, AsyncHttpResponseHandler responseHandler, Object context) {
        Future<?> request = threadPool.submit(new AsyncHttpRequest(this, client, uriRequest, responseHandler));
        if (context != null) {
            // Add request to request map
            List<WeakReference<Future<?>>> requestList = requestMap.get(context);
            if (requestList == null) {
                requestList = new LinkedList<WeakReference<Future<?>>>();
                requestMap.put(context, requestList);
            }
            requestList.add(new WeakReference<Future<?>>(request));
        }
    }

    /**
     * 取消请求
     *
     * @param context
     * @param mayInterruptIfRunning
     */
    public void cancelRequests(Object context, boolean mayInterruptIfRunning) {

        List<WeakReference<Future<?>>> requestList = requestMap.get(context);
        if (requestList != null) {
            for (WeakReference<Future<?>> requestRef : requestList) {
                Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
        requestMap.remove(context);

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
    }

    public void close() {
        threadPool.close(false);
    }
}
