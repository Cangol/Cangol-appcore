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


import android.os.Process;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import mobi.cangol.mobile.logging.Log;

/**
 * Created by weixuewu on 15/11/11.
 * 链接异常后开始重试（重试次数RETRY_TIMES次）
 * 重试次数用完后，发消息断开，由使用者决定是否继续重试
 * 参考 http://www.cnblogs.com/jerrychoi/archive/2010/04/15/1712931.html
 */
public class SocketThread extends Thread {
    private static final String TAG = "SocketThread";
    private static final int BUFFER_SIZE = 8192;
    private static final int WRITE_DELAY_TIME = 1;
    private static final int LINGER_TIME = 5;//阻塞时间 单位秒
    private static final int CONNECT_TIME_OUT = 20 * 1000;
    private static final int RETRY_TIMES_SHORT = 1;//3
    private static final int RETRY_TIMES_LONG = 1;//5
    private String host;
    private int port;
    private boolean isLong;
    private int timeout = CONNECT_TIME_OUT;
    private SocketHandler socketHandler;
    private SocketRetryHandler retryHandler;
    private Socket socket;
    private ExecutorService executorService;
    private int executionCount;
    protected boolean isConnecting;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public SocketThread(String host, int port, boolean isLong, int timeout, ExecutorService executorService, SocketHandler socketHandler) {
        this.port = port;
        this.host = host;
        this.isLong = isLong;
        if (timeout > 0) this.timeout = timeout;
        this.executorService = executorService;
        this.socketHandler = socketHandler;
        this.retryHandler = new SocketRetryHandler(isLong ? RETRY_TIMES_LONG : RETRY_TIMES_SHORT);
        this.setPriority(Thread.MAX_PRIORITY); // 10
    }

    private void makeRequest() throws IOException {
        Log.d(TAG, "socket connect executionCount=" + executionCount);
        if (!Thread.currentThread().isInterrupted()) {
            socket = new Socket();
            socket.setTrafficClass(0x04);//低成本0x02;高可靠性0x04;最高吞吐量;0x08;小延迟0x10
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(isLong ? CONNECT_TIME_OUT * 3 : timeout);
            socket.setKeepAlive(isLong);
            socket.setPerformancePreferences(3, 2, 1);//相对重要性 (connectionTime:表示用最少时间建立连接;latency:表示最小延迟;bandwidth:表示最高带宽)
            socket.setReuseAddress(false);
            socket.setSoLinger(true, LINGER_TIME);//linger=0:当close时立即关闭,丢弃剩余数据
            Log.d(TAG, "socket " + host + ":" + port + " connect...");
            socket.connect(new InetSocketAddress(host, port), CONNECT_TIME_OUT);
            isConnecting = true;
            Log.d(TAG, "socket is connected. " + socket.getLocalPort());
            socketHandler.sendConnectedMessage();

            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            Log.d(TAG, "socket is " + (isLong ? "isLong" : "") + " connect.");
            if (!isLong) {
                handleSocketWrite();
                handleSocketRead();
                disconnect();
            } else {
                executorService.submit(new SocketWriteThread(socketHandler, outputStream));
                executorService.submit(new SocketReadThread(socketHandler, inputStream));
            }
        } else {
            isConnecting = false;
            Log.d(TAG, "Thread.isInterrupted");
        }
    }

    public boolean handleSocketWrite() {
        boolean result = false;
        try {
            result = socketHandler.handleSocketWrite(outputStream);
        } catch (Exception e) {
            Log.d(TAG, "handleSocketWrite " + e);
            socketHandler.sendFailMessage(new Object[]{null, e});
            disconnect();
        }
        return result;
    }

    public boolean handleSocketRead() {
        boolean result = false;
        try {
            result = socketHandler.handleSocketRead(inputStream);
        } catch (Exception e) {
            Log.d(TAG, "handleSocketRead " + e);
            socketHandler.sendFailMessage(new Object[]{null, e});
            disconnect();
        }
        return result;
    }

    public synchronized void disconnect() {
        Log.d(TAG, "disconnect");
        isConnecting = false;
        socketHandler.sendDisconnectedMessage();
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (socket != null) {
                socket.close();
            }
            Log.d(TAG, "socket close.");
        } catch (IOException e) {
            Log.d(TAG, "disconnect Exception " + e.getMessage());
        }
    }

    private void makeRequestWithRetries() throws Exception {
        boolean retry = true;
        Exception cause = null;
        while (retry && !Thread.currentThread().isInterrupted()) {
            try {
                makeRequest();
                return;
            } catch (Exception e) {
                cause = e;
                retry = retryHandler.retryRequest(e, ++executionCount, socket);
            }
        }
        throw new Exception("Retry count exceeded, exception " + cause, cause);
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        try {
            socketHandler.sendStartMessage();
            makeRequestWithRetries();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            isConnecting = false;
            Log.d(TAG, "InterruptedException " + e.getMessage());
            disconnect();
        } catch (Exception e) {
            isConnecting = false;
            Log.d(TAG, "" + e.getCause());
            Object[] result = {null, e.getCause()};
            socketHandler.sendFailMessage(result);
            disconnect();
        }
    }

    class SocketWriteThread extends Thread {
        SocketHandler socketHandler;
        DataOutputStream outputStream;

        SocketWriteThread(SocketHandler socketHandler, DataOutputStream outputStream) {
            super();
            this.socketHandler = socketHandler;
            this.outputStream = outputStream;
            this.setPriority(Thread.MAX_PRIORITY); // 10
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            while (isConnecting && !socketHandler.isInterrupted()) {
                synchronized (socketHandler.writeLocker) {
                    if (!handleSocketWrite()) {
                        try {
                            socketHandler.writeLocker.wait(WRITE_DELAY_TIME);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            Log.d(TAG, "InterruptedException " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    class SocketReadThread extends Thread {
        SocketHandler socketHandler;
        DataInputStream inputStream;

        SocketReadThread(SocketHandler socketHandler, DataInputStream inputStream) {
            this.socketHandler = socketHandler;
            this.inputStream = inputStream;
            this.setPriority(Thread.MAX_PRIORITY); // 10
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            while (isConnecting && !socketHandler.isInterrupted()) {
                synchronized (socketHandler.readLocker) {
                    handleSocketRead();
                }
            }
        }
    }
}
