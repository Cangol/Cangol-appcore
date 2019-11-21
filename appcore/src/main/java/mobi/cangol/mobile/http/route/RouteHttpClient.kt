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
package mobi.cangol.mobile.http.route

import mobi.cangol.mobile.service.PoolManager
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class RouteHttpClient {
    private val httpClient: OkHttpClient
    private val threadPool: PoolManager.Pool

    /**
     * 构造实例
     */
    init {

        httpClient = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .build()
        threadPool = PoolManager.buildPool(TAG, DEFAULT_MAX)

    }

    /**
     * 发起请求
     *
     * @param tag
     * @param url
     * @param params
     * @param responseHandler
     * @param host
     */
    fun send(tag: Any, url: String, params: Map<String, String>?, responseHandler: RouteResponseHandler, vararg host: String) {
        var request: Request?
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
        sendRequest(httpClient, request, responseHandler, tag, *host)
    }

    private fun getNewRequest(request: Request, host: String): Request {
        val hostStr = request.url().url().host
        val urlStr = request.url().url().toString().replace(hostStr, host)

        return Request.Builder()
                .tag(request.tag())
                .url(urlStr)
                .build()
    }

    protected fun sendRequest(client: OkHttpClient, uriRequest: Request?, responseHandler: RouteResponseHandler, context: Any?, vararg host: String) {

        val request = threadPool.submit(HttpRequestTask(client, uriRequest, responseHandler, *host))
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

    internal inner class HttpRequestTask(private val client: OkHttpClient, private var request: Request?, private val responseHandler: RouteResponseHandler?, vararg host: String) : Runnable {
        private val host: Array<out String>?

        init {
            this.host = host
        }

        override fun run() {
            if (!Thread.currentThread().isInterrupted) {
                responseHandler?.sendStartMessage()
                var exec = 0
                while (exec < host!!.size) {
                    try {
                        request = getNewRequest(request!!, host[exec])
                        exec++
                        val response = client.newCall(request).execute()
                        if (!Thread.currentThread().isInterrupted) {
                                if (responseHandler?.sendResponseMessage(response)!!) {
                                    break
                                } else {
                                    //
                                }
                        } else {
                            break
                        }
                    } catch (e: IOException) {
                        if (exec >= host.size) {
                            responseHandler?.sendFailureMessage(e, "IOException")
                            break
                        }
                    }

                }
            }
        }
    }

    companion object {
        private const val TAG = "RouteHttpClient"
        private const val DEFAULT_CONNECT_TIMEOUT = 30 * 1000
        private const val DEFAULT_READ_TIMEOUT = 30 * 1000
        private const val DEFAULT_WRITE_TIMEOUT = 30 * 1000
        private const val DEFAULT_MAX = 3
        private val requestMap = WeakHashMap<Any, MutableList<WeakReference<Future<*>>>>()
    }
}

