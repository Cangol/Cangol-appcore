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

package mobi.cangol.mobile.socket

import android.os.SystemClock
import mobi.cangol.mobile.logging.Log
import java.io.IOException
import java.io.InterruptedIOException
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.IllegalBlockingModeException
import java.util.*
import javax.net.ssl.SSLHandshakeException

class SocketRetryHandler(private val maxRetries: Int) {

    fun retryRequest(exception: Exception, executionCount: Int, socket: Socket): Boolean {
        Log.d(TAG, "exception:" + exception.javaClass + " executionCount=" + executionCount)
        var retry = true
        when {
            executionCount >= maxRetries -> // Do not retry if over max retry count
                retry = false
            exceptionBlacklist.contains(exception.javaClass) -> // immediately cancel retry if the error is blacklisted
                retry = false
            exceptionWhitelist.contains(exception.javaClass) -> // immediately retry if error is whitelisted
                retry = true
        }

        if (retry) {
            SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS)
        } else {
            Log.e(TAG, exception.message)
        }

        return retry
    }

    companion object {
        private const val TAG = "SocketRetryHandler"
        private const val RETRY_SLEEP_TIME_MILLIS = 1500L
        private val exceptionWhitelist = HashSet<Class<*>>()
        private val exceptionBlacklist = HashSet<Class<*>>()

        init {
            // 为之主机异常
            exceptionWhitelist.add(UnknownHostException::class.java)
            // 如果端点为 null 或者此套接字不支持 SocketAddress 子类
            exceptionWhitelist.add(IllegalArgumentException::class.java)
            // 如果此套接字具有关联的通道并且该通道处于非阻塞模式
            exceptionWhitelist.add(IllegalBlockingModeException::class.java)
            // 如果在连接之前超时期满
            exceptionWhitelist.add(SocketTimeoutException::class.java)
            // 在连接之前发生
            exceptionWhitelist.add(SocketException::class.java)
            // 如果在连接期间发生错误
            exceptionWhitelist.add(IOException::class.java)

            // never retry Interrupted
            exceptionBlacklist.add(InterruptedIOException::class.java)
            // never retry class not found
            exceptionBlacklist.add(ClassNotFoundException::class.java)
            // never retry SSL handshake failures
            exceptionBlacklist.add(SSLHandshakeException::class.java)
        }
    }
}
