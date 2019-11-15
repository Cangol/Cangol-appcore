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


import android.os.Process

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ExecutorService

import mobi.cangol.mobile.logging.Log

/**
 * Created by weixuewu on 15/11/11.
 * 链接异常后开始重试（重试次数RETRY_TIMES次）
 * 重试次数用完后，发消息断开，由使用者决定是否继续重试
 * 参考 http://www.cnblogs.com/jerrychoi/archive/2010/04/15/1712931.html
 */
class SocketThread(private val host: String, private val port: Int, private val isLong: Boolean, timeout: Int, private val executorService: ExecutorService, private val socketHandler: SocketHandler?) : Thread() {
    private var timeout = CONNECT_TIME_OUT
    private val retryHandler: SocketRetryHandler
    private var socket: Socket? = null
    private var executionCount: Int = 0
    private var isConnecting: Boolean = false
    private var inputStream: DataInputStream? = null
    private var outputStream: DataOutputStream? = null

    init {
        if (timeout > 0) this.timeout = timeout
        this.retryHandler = SocketRetryHandler(if (isLong) RETRY_TIMES_LONG else RETRY_TIMES_SHORT)
        this.priority = MAX_PRIORITY // 10
    }

    @Throws(IOException::class)
    private fun makeRequest() {
        Log.d(TAG, "socket connect executionCount=$executionCount")
        if (!Thread.currentThread().isInterrupted) {
            socket = Socket()
            socket!!.trafficClass = 0x04//低成本0x02;高可靠性0x04;最高吞吐量;0x08;小延迟0x10
            socket!!.tcpNoDelay = true
            socket!!.soTimeout = if (isLong) CONNECT_TIME_OUT * 3 else timeout
            socket!!.keepAlive = isLong
            socket!!.setPerformancePreferences(3, 2, 1)//相对重要性 (connectionTime:表示用最少时间建立连接;latency:表示最小延迟;bandwidth:表示最高带宽)
            socket!!.reuseAddress = false
            socket!!.setSoLinger(true, LINGER_TIME)//linger=0:当close时立即关闭,丢弃剩余数据
            Log.d(TAG, "socket $host:$port connect...")
            socket!!.connect(InetSocketAddress(host, port), CONNECT_TIME_OUT)
            isConnecting = true
            Log.d(TAG, "socket is connected. " + socket!!.localPort)
            socketHandler?.sendConnectedMessage()

            inputStream = DataInputStream(socket!!.getInputStream())
            outputStream = DataOutputStream(socket!!.getOutputStream())
            Log.d(TAG, "socket is " + (if (isLong) "isLong" else "") + " connect.")
            if (!isLong) {
                handleSocketWrite()
                handleSocketRead()
                disconnect()
            } else {
                executorService.submit(socketHandler?.let { SocketWriteThread(it, outputStream!!) })
                executorService.submit(socketHandler?.let { SocketReadThread(it, inputStream!!) })
            }
        } else {
            isConnecting = false
            Log.d(TAG, "Thread.isInterrupted")
        }
    }

    fun handleSocketWrite(): Boolean {
        var result = false
        try {
            result = socketHandler!!.handleSocketWrite(outputStream!!)
        } catch (e: Exception) {
            Log.d(TAG, "handleSocketWrite $e")
            socketHandler!!.sendFailMessage(arrayOf<Any>("", e))
            disconnect()
        }

        return result
    }

    fun handleSocketRead(): Boolean {
        var result = false
        try {
            result = socketHandler!!.handleSocketRead(inputStream!!)
        } catch (e: Exception) {
            Log.d(TAG, "handleSocketRead $e")
            socketHandler!!.sendFailMessage(arrayOf<Any>("", e))
            disconnect()
        }

        return result
    }

    @Synchronized
    fun disconnect() {
        Log.d(TAG, "disconnect")
        isConnecting = false
        socketHandler!!.sendDisconnectedMessage()
        try {
            if (outputStream != null) outputStream!!.close()
            if (inputStream != null) inputStream!!.close()
            if (socket != null) {
                socket!!.close()
            }
            Log.d(TAG, "socket close.")
        } catch (e: IOException) {
            Log.d(TAG, "disconnect Exception " + e.message)
        }

    }

    @Throws(Exception::class)
    private fun makeRequestWithRetries() {
        var retry = true
        var cause: Exception? = null
        while (retry && !Thread.currentThread().isInterrupted) {
            try {
                makeRequest()
                return
            } catch (e: Exception) {
                cause = e
                retry = retryHandler.retryRequest(e, ++executionCount, socket!!)
            }

        }
        throw Exception("Retry count exceeded, exception " + cause!!, cause)
    }


    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
        try {
            socketHandler!!.sendStartMessage()
            makeRequestWithRetries()

        } catch (e: InterruptedException) {
            currentThread().interrupt()
            isConnecting = false
            Log.d(TAG, "InterruptedException " + e.message)
            disconnect()
        } catch (e: Exception) {
            isConnecting = false
            Log.d(TAG, "" + e.cause)
            val result = arrayOf<Any>("",""+e.cause)
            socketHandler!!.sendFailMessage(result)
            disconnect()
        }

    }

    internal inner class SocketWriteThread(private var socketHandler: SocketHandler, var outputStream: DataOutputStream) : Thread() {

        init {
            this.priority = MAX_PRIORITY // 10
        }

        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
            while (isConnecting && !socketHandler.isInterrupted()) {
                synchronized(socketHandler.getWriteLocker()) {
                    if (!handleSocketWrite()) {
                        try {
                            sleep(WRITE_DELAY_TIME)
                            //socketHandler.getWriteLocker().wait(WRITE_DELAY_TIME)
                        } catch (e: InterruptedException) {
                            currentThread().interrupt()
                            Log.d(TAG, "InterruptedException " + e.message)
                        }

                    }
                }
            }
        }
    }

    internal inner class SocketReadThread(private var socketHandler: SocketHandler, var inputStream: DataInputStream) : Thread() {

        init {
            this.priority = MAX_PRIORITY // 10
        }

        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
            while (isConnecting && !socketHandler.isInterrupted()) {
                synchronized(socketHandler.getReadLocker()) {
                    handleSocketRead()
                }
            }
        }
    }

    companion object {
        private const val TAG = "SocketThread"
        private const val WRITE_DELAY_TIME = 1L
        private const val LINGER_TIME = 5//阻塞时间 单位秒
        private const val CONNECT_TIME_OUT = 20 * 1000
        private const val RETRY_TIMES_SHORT = 1//3
        private const val RETRY_TIMES_LONG = 1//5
    }
}
