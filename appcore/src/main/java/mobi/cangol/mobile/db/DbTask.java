/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.db;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import mobi.cangol.mobile.service.PoolManager;

public abstract class DbTask<P, R> {

    private static final String TAG = "DbTask";


    private static final InternalHandler sHandler = new InternalHandler();

    private static final ExecutorService sExecutor = PoolManager.buildPool(TAG, 2).getExecutorService();

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_CANCEL = 0x3;

    private static final Map<String, List<WeakReference<Future<?>>>> futureMap = new WeakHashMap<String, List<WeakReference<Future<?>>>>();
    private String tag;

    public DbTask(String tag) {
        this.tag = tag;
    }

    public static void cancel(String tag) {
        cancel(tag, true);
    }

    public static void cancel(String tag, boolean mayInterruptIfRunning) {
        List<WeakReference<Future<?>>> requestList = futureMap.get(tag);
        if (requestList != null) {
            for (WeakReference<Future<?>> requestRef : requestList) {
                Future<?> request = requestRef.get();
                if (request != null) {
                    request.cancel(mayInterruptIfRunning);
                }
            }
        }
        futureMap.remove(tag);
    }

    public void onPreExecute() {

    }

    public abstract R doInBackground(P... mP);

    protected void onPostExecute(R result) {

    }

    public void execute(P... params) {
        onPreExecute();
        Future<?> future = sExecutor.submit(new DbTaskRunnable(sHandler, params));
        if (tag != null) {
            // Add request to request map
            List<WeakReference<Future<?>>> requestList = futureMap.get(tag);
            if (requestList == null) {
                requestList = new LinkedList<WeakReference<Future<?>>>();
                futureMap.put(tag, requestList);
            }
            requestList.add(new WeakReference<Future<?>>(future));
        }
    }

    public void onCancelled() {

    }

    public void onFinish(R result) {
        onPostExecute(result);
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            AsyncTaskR result = (AsyncTaskR) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    result.mTask.onFinish(result.mData[0]);
                    break;
                case MESSAGE_POST_CANCEL:
                    result.mTask.onCancelled();
                    break;
            }
        }
    }

    private static class AsyncTaskR<Data> {
        final DbTask mTask;
        final Data[] mData;

        AsyncTaskR(DbTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }

    private class DbTaskRunnable implements Runnable {
        private InternalHandler mHandler;
        private P[] mP;

        DbTaskRunnable(InternalHandler handler, P[] params) {
            this.mHandler = handler;
            this.mP = params;
        }

        @Override
        public void run() {
            Message message;
            R result = null;
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            if (!Thread.currentThread().isInterrupted()) {
                try {
                    result = doInBackground(mP);
                } catch (CancellationException e) {
                    message = mHandler.obtainMessage(MESSAGE_POST_CANCEL, new AsyncTaskR<R>(DbTask.this, (R[]) null));
                    message.sendToTarget();
                    return;
                }
                message = mHandler.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskR<R>(DbTask.this, result));
                message.sendToTarget();
            } else {
                message = mHandler.obtainMessage(MESSAGE_POST_CANCEL, new AsyncTaskR<R>(DbTask.this, (R[]) null));
                message.sendToTarget();
            }
        }
    }

}
