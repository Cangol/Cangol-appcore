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

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Future;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.PoolManager;

/**
 * Created by weixuewu on 15/11/11.
 */
public class SocketClient {
    private final static String TAG = "SocketClient";
    private final static boolean DEBUG = false;
    private final Map<Context, List<WeakReference<Future<?>>>> requestMap;

    private PoolManager.Pool threadPool;

    public static SocketClient build() {
        return new SocketClient();
    }

    protected SocketClient() {
        threadPool = PoolManager.buildPool(TAG,3);
        requestMap = new WeakHashMap<Context, List<WeakReference<Future<?>>>>();
    }

    public Future<?> connect(Context context, String host, int port, boolean isLong, int timeout, SocketHandler socketHandler) {
        Future<?> request = threadPool.submit(new SocketThread(host, port, isLong,timeout, socketHandler));
        // Add request to request map
        List<WeakReference<Future<?>>> requestList = requestMap.get(context);
        if (requestList == null) {
            requestList = new LinkedList<WeakReference<Future<?>>>();
            requestMap.put(context, requestList);
        }
        requestList.add(new WeakReference<Future<?>>(request));
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

    public void cancel(boolean mayInterruptIfRunning) {
        threadPool.cancle(mayInterruptIfRunning);
    }
}
