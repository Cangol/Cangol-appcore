/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.socket;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Future;

import mobi.cangol.mobile.service.PoolManager;

/**
 * Created by weixuewu on 15/11/11.
 */
public class SocketClient {
    private static final  String TAG = "SocketClient";
    private final Map<String, List<WeakReference<Future<?>>>> requestMap;
    private final Map<String, WeakReference<SocketHandler>> handlerMap;

    public static SocketClient build() {
        return new SocketClient();
    }

    private int port;
    private String host;
    private boolean isLong;

    private static final int TIME_OUT = 20 * 1000;

    private PoolManager.Pool pool;
    protected SocketClient() {
        handlerMap = new WeakHashMap<>();
        requestMap = new WeakHashMap<>();
        pool = PoolManager.buildPool(TAG,3);
    }

    public void setLong(boolean aLong) {
        isLong = aLong;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Future connect(String tag, SocketHandler socketHandler) {
        return connect(tag, this.host, this.port, this.isLong, TIME_OUT, socketHandler);
    }

    public Future connect(String tag, String host, int port, boolean isLong, int timeout, SocketHandler socketHandler) {
        Future request = pool.submit(new SocketThread(host, port, isLong, timeout, pool.getExecutorService(), socketHandler));
        if (tag != null && socketHandler != null) {
            handlerMap.put(tag, new WeakReference<>(socketHandler));
        }
        // Add request to request map
        List<WeakReference<Future<?>>> requestList = requestMap.get(tag);
        if (requestList == null) {
            requestList = new LinkedList<>();
            requestMap.put(tag, requestList);
        }
        requestList.add(new WeakReference<Future<?>>(request));
        return request;
    }

    public void cancelRequests(String tag) {
        List<WeakReference<Future<?>>> requestList = requestMap.get(tag);
        if (requestList != null) {
            for (WeakReference<Future<?>> requestRef : requestList) {
                Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(true);
                }
            }
        }
        requestMap.remove(tag);

        for (Map.Entry<String, WeakReference<SocketHandler>> entry : handlerMap.entrySet()) {
            if (entry.getKey().equals(tag) && entry.getValue().get() != null) {
                entry.getValue().get().interrupted();
            }
        }
        handlerMap.remove(tag);
    }

    public void cancel(boolean mayInterruptIfRunning) {
        for (Map.Entry<String, WeakReference<SocketHandler>> entry : handlerMap.entrySet()) {
            entry.getValue().get().interrupted();
        }
        for (Map.Entry<String, List<WeakReference<Future<?>>>> entry : requestMap.entrySet()) {
            for (WeakReference<Future<?>> requestRef : entry.getValue()) {
                Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
    }

    public void shutdown() {
        pool.getExecutorService().shutdown();
    }
}
