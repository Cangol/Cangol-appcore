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
package mobi.cangol.mobile.http.download

import mobi.cangol.mobile.http.HttpClientFactory
import mobi.cangol.mobile.service.PoolManager
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Future


object DownloadHttpClient {
    private const val TAG = "DownloadHttpClient"
    private const val DEFAULT_RETRYTIMES = 10
    private const val DEFAULT_MAX = 3
    private var group: String? = null
    private var threadPool: PoolManager.Pool? = null
    private var requestMap: MutableMap<Any, MutableList<WeakReference<Future<*>>>>? = null
    private var httpClient: OkHttpClient? = null
    var downloadRetryHandler: DownloadRetryHandler? = null

    fun init(group: String, safe: Boolean) {
        this.group = group
        this.httpClient = if (safe) HttpClientFactory.createDefaultHttpClient() else HttpClientFactory.createUnSafeHttpClient()
        this.threadPool = PoolManager.buildPool(group, DEFAULT_MAX)
        this.requestMap = WeakHashMap()
        this.downloadRetryHandler = DownloadRetryHandler(DEFAULT_RETRYTIMES)
    }

    /**
     * 发起请求
     *
     * @param tag
     * @param url
     * @param responseHandler
     * @param from
     * @param saveFile
     * @return
     */
    fun send(tag: Any, url: String, responseHandler: DownloadResponseHandler, from: Long, saveFile: String): Future<*> {
        val request = Request.Builder()
                .tag(tag)
                .addHeader("Range", "bytes=$from-")
                .url(url)
                .build()
        return sendRequest(request, responseHandler, saveFile)
    }

    fun sendRequest(urlRequest: Request, responseHandler: DownloadResponseHandler, saveFile: String): Future<*> {
        val request = threadPool!!.submit(DownloadThread(this, httpClient!!, urlRequest, responseHandler, saveFile))
        if (urlRequest.tag() != null) {
            // Add request to request map
            var requestList: MutableList<WeakReference<Future<*>>>? = requestMap!![urlRequest.tag()!!]
            if (requestList == null) {
                requestList = mutableListOf()
                requestMap?.set(urlRequest.tag()!!, requestList)
            }
            requestList.add(WeakReference(request))
        }

        return request
    }

    /**
     * 取消请求
     *
     * @param tag
     * @param mayInterruptIfRunning
     */
    fun cancelRequests(tag: Any, mayInterruptIfRunning: Boolean) {

        val requestList = requestMap!![tag]
        if (requestList != null) {
            for (requestRef in requestList) {
                val request = requestRef.get()
                request?.cancel(mayInterruptIfRunning)
            }
        }
        requestMap!!.remove(tag)

        for (call in httpClient!!.dispatcher().queuedCalls()) {
            if (call.request().tag() == group) {
                call.cancel()
            }
        }
        for (call in httpClient!!.dispatcher().runningCalls()) {
            if (call.request().tag() == group) {
                call.cancel()
            }
        }
    }

    /**
     * 取消所有
     */
    fun cancelAll() {
        httpClient?.dispatcher()?.cancelAll()
    }

    fun close() {
        threadPool!!.close(false)
    }

    /**
     * 构造实例
     *
     * @param group
     * @return
     */
    @JvmStatic
    fun build(group: String): DownloadHttpClient {
        return build(group, true)
    }

    /**
     * 构造实例
     *
     * @param group
     * @param safe
     * @return
     */
    @JvmStatic
    fun build(group: String, safe: Boolean = true): DownloadHttpClient {
        var downHttpClient = DownloadHttpClient
        downHttpClient.init(group, safe)
        return downHttpClient
    }

    /**
     * 设置线程池
     *
     * @param pool
     */
    fun setThreadPool(pool: PoolManager.Pool) {
        this.threadPool = pool
    }
}