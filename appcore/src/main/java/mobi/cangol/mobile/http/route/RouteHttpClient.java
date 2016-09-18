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
package mobi.cangol.mobile.http.route;

import android.content.Context;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import mobi.cangol.mobile.service.PoolManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RouteHttpClient {
    public final static String TAG = "RouteHttpClient";
    private final static boolean DEBUG = true;
    private final static int DEFAULT_RETRYTIMES = 10;
    private final static int DEFAULT_CONNECT_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_READ_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_WRITE_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_MAX = 3;
    private final Map<Object, List<WeakReference<Future<?>>>> requestMap;
    private OkHttpClient httpClient;
    private PoolManager.Pool threadPool;

    public RouteHttpClient() {

        httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
        threadPool = PoolManager.buildPool(TAG, DEFAULT_MAX);

        requestMap = new WeakHashMap<Object, List<WeakReference<Future<?>>>>();
    }

    public void send(Object tag, String url, HashMap<String, String> params, RouteResponseHandler responseHandler, String... host) {
        Request request = null;
        if (params != null) {
            FormBody.Builder requestBodyBuilder = new FormBody.Builder();
            for (ConcurrentHashMap.Entry<String, String> entry : params.entrySet()) {
                requestBodyBuilder.add(entry.getKey(), entry.getValue());
            }
            request = new Request.Builder()
                    .tag(tag)
                    .url(url)
                    .post(requestBodyBuilder.build())
                    .build();
        } else {
            request = new Request.Builder()
                    .tag(tag)
                    .url(url)
                    .build();
        }
        sendRequest(httpClient, request, responseHandler, tag, host);
    }

    private Request getNewRequest(Request request, String host) {
        String hostStr = request.url().url().getHost();
        String urlStr = request.url().url().toString().replace(hostStr, host);

        Request newRequest = new Request.Builder()
                .tag(request.tag())
                .url(urlStr)
                .build();

        return newRequest;
    }

    protected void sendRequest(OkHttpClient client, Request uriRequest, RouteResponseHandler responseHandler, Object context, String... host) {

        Future<?> request = threadPool.submit(new HttpRequestTask(client, uriRequest, responseHandler, host));
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

    public void cancelRequests(Context context, boolean mayInterruptIfRunning) {
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
    }

    class HttpRequestTask implements Runnable {
        private final RouteResponseHandler responseHandler;
        private OkHttpClient client;
        private Request request;
        private String[] host;

        public HttpRequestTask(OkHttpClient client, Request request, RouteResponseHandler responseHandler, String... host) {
            this.client = client;
            this.request = request;
            this.responseHandler = responseHandler;
            this.host = host;
        }

        @Override
        public void run() {
            if (!Thread.currentThread().isInterrupted()) {
                responseHandler.sendStartMessage();
                int exec = 0;
                while (exec < host.length) {
                    try {
                        request = getNewRequest(request, host[exec]);
                        exec++;
                        Response response = client.newCall(request).execute();
                        if (!Thread.currentThread().isInterrupted()) {
                            if (responseHandler != null) {
                                if (responseHandler.sendResponseMessage(response)) {
                                    break;
                                } else {
                                    //
                                }
                            }
                        } else {
                            break;
                        }
                    } catch (IOException e) {
                        if (exec >= host.length) {
                            responseHandler.sendFailureMessage(e, "IOException");
                            break;
                        }
                        continue;
                    }
                }
            }
        }
    }
}

