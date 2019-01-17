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

import android.os.SystemClock;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

import mobi.cangol.mobile.logging.Log;

public class SocketRetryHandler {
    private static final String TAG = "SocketRetryHandler";
    private static final long RETRY_SLEEP_TIME_MILLIS = 1500L;
    private static HashSet<Class<?>> exceptionWhitelist = new HashSet<>();
    private static HashSet<Class<?>> exceptionBlacklist = new HashSet<>();

    static {
        // 为之主机异常
        exceptionWhitelist.add(UnknownHostException.class);
        // 如果端点为 null 或者此套接字不支持 SocketAddress 子类
        exceptionWhitelist.add(IllegalArgumentException.class);
        // 如果此套接字具有关联的通道并且该通道处于非阻塞模式
        exceptionWhitelist.add(IllegalBlockingModeException.class);
        // 如果在连接之前超时期满
        exceptionWhitelist.add(SocketTimeoutException.class);
        // 在连接之前发生
        exceptionWhitelist.add(SocketException.class);
        // 如果在连接期间发生错误
        exceptionWhitelist.add(IOException.class);

        // never retry Interrupted
        exceptionBlacklist.add(InterruptedIOException.class);
        // never retry class not found
        exceptionBlacklist.add(ClassNotFoundException.class);
        // never retry SSL handshake failures
        exceptionBlacklist.add(SSLHandshakeException.class);
    }

    private int maxRetries;

    public SocketRetryHandler(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean retryRequest(Exception exception, int executionCount, Socket socket) {
        Log.d(TAG, "exception:" + exception.getClass() + " executionCount=" + executionCount);
        boolean retry = true;
        if (executionCount >= maxRetries) {
            // Do not retry if over max retry count
            retry = false;
        } else if (exceptionBlacklist.contains(exception.getClass())) {
            // immediately cancel retry if the error is blacklisted
            retry = false;
        } else if (exceptionWhitelist.contains(exception.getClass())) {
            // immediately retry if error is whitelisted
            retry = true;
        }
        //socket handle

        if (retry) {
            SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
        } else {
            Log.e(TAG, exception.getMessage());
        }

        return retry;
    }
}
