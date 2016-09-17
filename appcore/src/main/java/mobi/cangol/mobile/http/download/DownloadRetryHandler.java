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
package mobi.cangol.mobile.http.download;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

public class DownloadRetryHandler{
    private static final long RETRY_SLEEP_TIME_MILLIS = 1500L;
    private static HashSet<Class<?>> exceptionWhitelist = new HashSet<Class<?>>();
    private static HashSet<Class<?>> exceptionBlacklist = new HashSet<Class<?>>();

    static {
        // Retry if the server dropped connection on us
        // retry-this, since it may happens as part of a Wi-Fi to 3G failover
        exceptionWhitelist.add(UnknownHostException.class);
        // retry-this, since it may happens as part of a Wi-Fi to 3G failover
        exceptionWhitelist.add(SocketException.class);
        // retry-this,
        exceptionWhitelist.add(IOException.class);

        // never retry timeouts
        exceptionBlacklist.add(InterruptedIOException.class);
        // never retry SSL handshake failures
        exceptionBlacklist.add(SSLHandshakeException.class);
    }

    private int maxRetries;

    public DownloadRetryHandler(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean retryRequest(IOException exception, int executionCount) {
        Log.d("download retryRequest", "exception:" + exception.getClass() + " executionCount=" + executionCount);
        boolean retry = true;
        if (executionCount > maxRetries) {
            // Do not retry if over max retry count
            retry = false;
        } else if (exceptionBlacklist.contains(exception.getClass())) {
            // immediately cancel retry if the error is blacklisted
            retry = false;
        } else if (exceptionWhitelist.contains(exception.getClass())) {
            // immediately retry if error is whitelisted
            retry = true;
        }

        if (retry) {
            SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
        } else {
            Log.d(getClass().getName(), exception.getMessage());
        }

        return retry;
    }
}

