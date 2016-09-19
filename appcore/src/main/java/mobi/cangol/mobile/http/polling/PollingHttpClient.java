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
package mobi.cangol.mobile.http.polling;

import android.util.Log;

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
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PollingHttpClient {
    public final static boolean DEBUG = true;
    public final static String TAG = "PollingHttpClient";
    private final static int DEFAULT_RETRYTIMES = 10;
    private final static int DEFAULT_CONNECT_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_READ_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_WRITE_TIMEOUT = 30 * 1000;
    private final static int DEFAULT_MAX = 3;
    private final Map<Object, List<WeakReference<Future<?>>>> requestMap;
    private OkHttpClient httpClient;
    private PoolManager.Pool threadPool;

    /**
     * 构造实例
     */
    public PollingHttpClient() {
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

    /**
     * 发送轮询请求(get请求)
     * @param tag
     * @param url
     * @param params
     * @param responseHandler
     * @param retryTimes
     * @param sleeptimes
     */
    public void send(Object tag, String url, HashMap<String, String> params, PollingResponseHandler responseHandler, int retryTimes, long sleeptimes) {

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
        sendRequest(httpClient, request, responseHandler, tag, retryTimes, sleeptimes);
    }

    protected void sendRequest(OkHttpClient client, Request uriRequest, PollingResponseHandler responseHandler, Object context, int retryTimes, long sleeptimes) {

        Future<?> request = threadPool.submit(new HttpRequestTask(client, uriRequest, responseHandler, retryTimes, sleeptimes));
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
            if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
        for (Call call : httpClient.dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
    }

    class HttpRequestTask implements Runnable {
        private final PollingResponseHandler responseHandler;
        private OkHttpClient client;
        private Request request;
        private int retryTimes = 3;
        private long sleepTimes = 20000L;

        public HttpRequestTask(OkHttpClient client, Request request, PollingResponseHandler responseHandler, int retryTimes, long sleepTimes) {
            this.client = client;
            this.request = request;
            this.responseHandler = responseHandler;
            this.retryTimes = retryTimes;
            this.sleepTimes = sleepTimes;
        }

        @Override
        public void run() {
            if (!Thread.currentThread().isInterrupted()) {
                responseHandler.sendStartMessage();
                int exec = 0;
                boolean isSuccess = false;
                boolean isInterrupted = false;
                while (exec < retryTimes) {
                    try {
                        exec++;
                        Response response = client.newCall(request).execute();
                        if (!Thread.currentThread().isInterrupted()) {
                            if (responseHandler != null) {
                                if (isSuccess = responseHandler.sendResponseMessage(response)) {
                                    break;
                                } else {
                                    //
                                }
                            }
                        } else {
                            Log.d(TAG, "Thread.isInterrupted");
                            break;
                        }
                        if (!Thread.currentThread().isInterrupted()) {
                            Thread.sleep(sleepTimes);
                        } else {
                            break;
                        }
                        Log.d(TAG, "Thread sleeptimes end");
                    } catch (IOException e) {
                        responseHandler.sendFailureMessage(e, "IOException");
                        if (exec >= retryTimes) {
                            break;
                        }
                        continue;
                    } catch (InterruptedException e) {
                        isInterrupted = true;
                        break;
                    }
                }
                if (!isSuccess && !isInterrupted) {
                    responseHandler.sendFinishMessage(exec);
                }
            }
        }
    }
}

