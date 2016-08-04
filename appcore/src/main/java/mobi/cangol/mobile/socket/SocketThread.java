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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.concurrent.ExecutorService;

import mobi.cangol.mobile.logging.Log;

/**
 * Created by weixuewu on 15/11/11.
 * 链接异常后开始重试（重试次数RETRY_TIMES次）
 * 重试次数用完后，发消息断开，由使用者决定是否继续重试
 */
public class SocketThread implements Runnable {
    private static final boolean DEBUG = true;
    private static final int BUFFER_SIZE = 8192;
    private static final int KEEPALIVE_TIME = 1 * 60 * 1000;
    private static final int READ_DELAY_TIME = 1000;
    private static final int WRITE_DELAY_TIME = 60;
    private static final int LINGER_TIME = 5;
    private static final int CONNECT_TIME_OUT = 20 * 1000;
    private static final int RETRY_TIMES_SHORT = 1;//3
    private static final int RETRY_TIMES_LONG = 3;//5
    private static final String TAG = "SocketThread";
    protected boolean isConnecting;
    private String host;
    private int port;
    private boolean isLong;
    private int timeout = CONNECT_TIME_OUT;
    private SocketHandler socketHandler;
    private SocketRetryHandler retryHandler;
    private Socket socket;
    private ExecutorService executorService;
    private int executionCount;
    private InputStream inputStream;
    private OutputStream outputStream;

    public SocketThread(String host, int port, boolean isLong, int timeout, ExecutorService executorService, SocketHandler socketHandler) {
        this.port = port;
        this.host = host;
        this.isLong = isLong;
        if (timeout > 0) {
            this.timeout = timeout;
        }
        this.executorService = executorService;
        this.socketHandler = socketHandler;
        this.retryHandler = new SocketRetryHandler(isLong ? RETRY_TIMES_LONG : RETRY_TIMES_SHORT);
    }

    private void makeRequest() throws ClassNotFoundException, IllegalBlockingModeException, IllegalArgumentException, SocketTimeoutException, IOException {
        if (DEBUG){
            Log.d(TAG, "socket connect executionCount=" + executionCount);
        }
//        if(TextUtils.isEmpty(host)||port==0){
//            Object[] response = {null, new IllegalArgumentException(" host or port isn't valid")};
//            socketHandler.sendFailMessage(response);
//            disconnect();
//        }else
        if (!Thread.currentThread().isInterrupted()) {
            socket = new Socket();
            socket.setTrafficClass(0x10);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(CONNECT_TIME_OUT);
            socket.setReceiveBufferSize(BUFFER_SIZE);
            socket.setSendBufferSize(BUFFER_SIZE);
            socket.setKeepAlive(isLong);
            socket.setPerformancePreferences(0, 1, 0);
            socket.setReuseAddress(true);
            socket.setSoLinger(true, LINGER_TIME);
            if (DEBUG){
                Log.d(TAG, "socket " + host + ":" + port + " connect...");
            }
            socket.connect(new InetSocketAddress(host, port), CONNECT_TIME_OUT);
            isConnecting = true;
            if (DEBUG) {
                Log.d(TAG, "socket is connected.");
            }
            socketHandler.sendConnectedMessage();

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            if (DEBUG) {
                Log.d(TAG, "socket is " + (isLong ? "isLong" : "") + " connect.");
            }

            if (!isLong) {
                handleSocketWrite();
                handleSocketRead(timeout);
                disconnect();
            } else {
                //socketHandler.whileRunnable(new SocketWriteThread(socketHandler, outputStream), WRITE_DELAY_TIME);
                //socketHandler.waitWrite();
                executorService.submit(new SocketWriteThread(socketHandler, outputStream));
                executorService.submit(new SocketReadThread(socketHandler, inputStream));
                //new Thread(new SocketWriteThread(socketHandler, outputStream)).start();
                //new Thread(new SocketReadThread(socketHandler, inputStream)).start();
            }
        } else {
            isConnecting = false;
            if (DEBUG) {
                Log.d(TAG, "Thread.isInterrupted");
            }
        }
    }

    public boolean handleSocketWrite() {
        boolean result = false;
        try {
            result = socketHandler.handleSocketWrite(outputStream);
        } catch (Throwable e) {
            if (DEBUG) {
                Log.d(TAG, "handleSocketWrite " + e);
            }

            Object[] response = {null, e};
            socketHandler.sendFailMessage(response);
            if (e instanceof SocketTimeoutException) {
                //socket read timeout,disconnect
            }
            disconnect();
        }
        return result;
    }

    public boolean handleSocketRead(int timeout) {
        boolean result = false;
        try {
            result = socketHandler.handleSocketRead(timeout, inputStream);
        } catch (Throwable e) {
            if (DEBUG) {
                Log.d(TAG, "handleSocketRead " + e);
            }
            Object[] response = {null, e};
            socketHandler.sendFailMessage(response);
            if (e instanceof SocketTimeoutException) {
                //socket read timeout,don't disconnect
            }
            disconnect();
        }
        return result;
    }

    public synchronized void disconnect() {
        if (DEBUG) {
            Log.d(TAG, "disconnect");
        }
        isConnecting = false;
        socketHandler.sendDisconnectedMessage();
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }

            if (socket != null) {
                socket.close();
            }
            if (DEBUG) {
                Log.d(TAG, "socket close.");
            }

        } catch (IOException e) {
            if (DEBUG) {
                Log.d(TAG, "disconnect Exception " + e.getMessage());
            }

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
        Exception ex = new Exception("Retry count exceeded, exception " + cause.getMessage());
        ex.initCause(cause);
        throw ex;
    }


    @Override
    public void run() {
        try {
            socketHandler.sendStartMessage();
            makeRequestWithRetries();
        } catch (InterruptedException e) {
            isConnecting = false;
            if (DEBUG) {
                Log.d(TAG, "InterruptedException " + e.getMessage());
            }
            disconnect();
        } catch (Exception e) {
            Log.d(e.getMessage());
            Object[] result = {null, e.getCause()};
            socketHandler.sendFailMessage(result);
            disconnect();
        }
    }

    class SocketWriteThread implements Runnable {
        SocketHandler socketHandler;
        OutputStream outputStream;

        SocketWriteThread(SocketHandler socketHandler, OutputStream outputStream) {
            super();
            this.socketHandler = socketHandler;
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            while (isConnecting) {
                synchronized (socketHandler.writeLocker) {
                    if (!handleSocketWrite()) {
                        //socketHandler.waitWrite();
                        try {
                            Thread.sleep(WRITE_DELAY_TIME);
                        } catch (InterruptedException e) {
                            Log.d(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    class SocketReadThread implements Runnable {
        SocketHandler socketHandler;
        InputStream inputStream;

        SocketReadThread(SocketHandler socketHandler, InputStream inputStream) {
            this.socketHandler = socketHandler;
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            while (isConnecting) {
                synchronized (socketHandler.readLocker) {
                    handleSocketRead(timeout);
                    //Thread.sleep(READ_DELAY_TIME);
                }
            }
        }
    }
}
