/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.http.polling

import android.util.Log
import mobi.cangol.mobile.service.PoolManager
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class PollingHttpClient
/**
 * 构造实例
 */
(group: String) {
    private val requestMap: MutableMap<Any, MutableList<WeakReference<Future<*>>>>
    private val httpClient: OkHttpClient
    private val threadPool: PoolManager.Pool

    init {
        httpClient = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .build()
        threadPool = PoolManager.buildPool(group, DEFAULT_MAX)
        requestMap = WeakHashMap()
    }

    /**
     * 发送轮询请求(get请求)
     *
     * @param tag
     * @param url
     * @param params
     * @param responseHandler
     * @param retryTimes
     * @param sleeptimes
     */
    fun send(tag: Any, url: String, params: Map<String, String>?, responseHandler: PollingResponseHandler, retryTimes: Int, sleeptimes: Long) {

        var request: Request? = null
        if (params != null) {
            val requestBodyBuilder = FormBody.Builder()
            for ((key, value) in params) {
                requestBodyBuilder.add(key, value)
            }
            request = Request.Builder()
                    .tag(tag)
                    .url(url)
                    .post(requestBodyBuilder.build())
                    .build()
        } else {
            request = Request.Builder()
                    .tag(tag)
                    .url(url)
                    .build()
        }
        sendRequest(httpClient, request, responseHandler, tag, retryTimes, sleeptimes)
    }

    fun send(tag: Any, url: String, requestBody: RequestBody, responseHandler: PollingResponseHandler, retryTimes: Int, sleeptimes: Long) {
        val request = Request.Builder()
                .tag(tag)
                .url(url)
                .post(requestBody)
                .build()
        sendRequest(httpClient, request, responseHandler, tag, retryTimes, sleeptimes)
    }

    protected fun sendRequest(client: OkHttpClient, uriRequest: Request, responseHandler: PollingResponseHandler, context: Any?, retryTimes: Int, sleeptimes: Long) {

        val request = threadPool.submit(HttpRequestTask(client, uriRequest, responseHandler, retryTimes, sleeptimes))
        if (context != null) {
            // Add request to request map
            var requestList: MutableList<WeakReference<Future<*>>>? = requestMap[context]
            if (requestList == null) {
                requestList = LinkedList()
                requestMap[context] = requestList
            }
            requestList.add(WeakReference(request))
        }
    }

    /**
     * 取消请求
     *
     * @param tag
     * @param mayInterruptIfRunning
     */
    fun cancelRequests(tag: Any, mayInterruptIfRunning: Boolean) {
        val requestList = requestMap[tag]
        if (requestList != null) {
            for (requestRef in requestList) {
                val request = requestRef.get()
                request?.cancel(mayInterruptIfRunning)
            }
        }
        requestMap.remove(tag)

        for (call in httpClient.dispatcher().queuedCalls()) {
            if (call.request().tag() == tag) {
                call.cancel()
            }
        }
        for (call in httpClient.dispatcher().runningCalls()) {
            if (call.request().tag() == tag) {
                call.cancel()
            }
        }
    }

    fun shutdown() {
        threadPool.executorService!!.shutdownNow()
        PoolManager.clear()

    }

    internal inner class HttpRequestTask(private val client: OkHttpClient, private val request: Request, private val responseHandler: PollingResponseHandler?, retryTimes: Int, sleepTimes: Long) : Runnable {
        private var retryTimes = 3
        private var sleepTimes = 20000L

        init {
            this.retryTimes = retryTimes
            this.sleepTimes = sleepTimes
        }

        override fun run() {
            if (!Thread.currentThread().isInterrupted) {
                responseHandler!!.sendStartMessage()
                var exec = 0
                var isSuccess = false
                var isInterrupted = false
                while (exec < retryTimes) {
                    try {
                        exec++
                        val response = client.newCall(request).execute()
                        if (!Thread.currentThread().isInterrupted) {
                            if (responseHandler != null) {
                                isSuccess = responseHandler.sendResponseMessage(response)
                                if (isSuccess) {

                                } else {
                                    break
                                }
                            }
                        } else {
                            Log.d(TAG, "Thread.isInterrupted")
                            break
                        }
                        if (!Thread.currentThread().isInterrupted) {
                            Thread.sleep(sleepTimes)
                        } else {
                            break
                        }
                        Log.d(TAG, "Thread sleeptimes end")
                    } catch (e: IOException) {
                        responseHandler.sendFailureMessage(e, "IOException")
                        if (exec >= retryTimes) {
                            break
                        }
                    } catch (e: InterruptedException) {
                        isInterrupted = true
                        break
                    }

                }
                if (!isSuccess && !isInterrupted) {
                    responseHandler.sendFinishMessage(exec)
                }
            }
        }
    }

    companion object {
        private const val TAG = "PollingHttpClient"
        private const val DEFAULT_CONNECT_TIMEOUT = 30 * 1000
        private const val DEFAULT_READ_TIMEOUT = 30 * 1000
        private const val DEFAULT_WRITE_TIMEOUT = 30 * 1000
        private const val DEFAULT_MAX = 3

        @JvmStatic
        fun build(group: String): PollingHttpClient {
            return PollingHttpClient(group)
        }
    }
}

