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
package mobi.cangol.mobile.http

import org.json.JSONObject

import java.io.File
import java.lang.ref.WeakReference
import java.util.HashMap
import java.util.LinkedList
import java.util.WeakHashMap
import java.util.concurrent.Future

import mobi.cangol.mobile.service.PoolManager
import mobi.cangol.mobile.service.PoolManager.Pool
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * 异步http请求client
 * 基于okio,okhttp3实现
 */
class AsyncHttpClient {
    private val httpClient: OkHttpClient
    private val requestMap: MutableMap<Any, MutableList<WeakReference<Future<*>>>>

    private var threadPool: Pool? = null
    /**
     * 获取RetryHandler
     *
     * @return
     */
    var retryHandler: RetryHandler? = null
        private set
    private var group: String? = null

    private constructor(group: String) {
        this.group = group
        this.httpClient = HttpClientFactory.createDefaultHttpClient()
        this.threadPool = PoolManager.getPool(group)
        this.requestMap = WeakHashMap()
        this.retryHandler = RetryHandler(DEFAULT_RETRY_TIMES)
    }

    private constructor(group: String, client: OkHttpClient) {
        this.group = group
        this.httpClient = client
        this.threadPool = PoolManager.getPool(group)
        this.requestMap = WeakHashMap()
        this.retryHandler = RetryHandler(DEFAULT_RETRY_TIMES)
    }

    /**
     * 设置线程池
     *
     * @param pool
     */
    fun setThreadPool(pool: Pool) {
        this.threadPool = pool
    }

    /**
     * 执行get方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    operator fun get(context: Any, url: String, responseHandler: AsyncHttpResponseHandler) {
        get(context, url, null, null, responseHandler)
    }

    /**
     * 执行get方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    operator fun get(context: Any, url: String, headers: Map<String, String>?, params: Map<String, String>?, responseHandler: AsyncHttpResponseHandler) {

        val sb = StringBuilder(if (url.contains("?")) "" else "?")
        if (params != null) {
            for ((key, value) in params) {
                sb.append(key)
                        .append('&')
                        .append(value)
            }
        }
        execMethod("GET", context, url + sb.toString(), headers, null as Map<String, String>?, responseHandler)
    }

    /**
     * 执行get方法
     *
     * @param context
     * @param url
     * @param requestParams
     * @param responseHandler
     */
    operator fun get(context: Any, url: String, requestParams: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        val sb = StringBuilder(if (url.contains("?")) "" else "?")
        for ((key, value) in requestParams.getUrlParams()) {
            sb.append(key)
                    .append('&')
                    .append(value)
        }

        execMethod("GET", context, url, null, null as HashMap<String, String>?, responseHandler)
    }

    /**
     * 执行patch方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    fun patch(context: Any, url: String, responseHandler: AsyncHttpResponseHandler) {
        patch(context, url, null, null, responseHandler)
    }

    /**
     * 执行patch方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    fun patch(context: Any, url: String, headers: Map<String, String>?, params: Map<String, String>?, responseHandler: AsyncHttpResponseHandler) {
        execMethod("PATCH", context, url, headers, params, responseHandler)
    }

    /**
     * 执行post方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    fun post(context: Any, url: String, responseHandler: AsyncHttpResponseHandler) {
        post(context, url, null, null, responseHandler)
    }

    /**
     * 执行post方法
     *
     * @param context
     * @param url
     * @param json
     * @param responseHandler
     */
    fun post(context: Any, url: String, json: JSONObject, responseHandler: AsyncHttpResponseHandler) {
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString())
        execMethod("POST", context, url, null, requestBody, responseHandler)
    }

    /**
     * 执行post方法
     *
     * @param context
     * @param url
     * @param requestParams
     * @param responseHandler
     */
    fun post(context: Any, url: String, requestParams: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        execMethod("POST", context, url, null, requestParams, responseHandler)
    }

    /**
     * 执行post方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    fun post(context: Any, url: String, headers: Map<String, String>?, params: Map<String, String>?, responseHandler: AsyncHttpResponseHandler) {
        execMethod("POST", context, url, headers, params, responseHandler)
    }

    /**
     * 执行put方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    fun put(context: Any, url: String, responseHandler: AsyncHttpResponseHandler) {
        put(context, url, null, null, responseHandler)
    }

    /**
     * 执行put方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    fun put(context: Any, url: String, headers: Map<String, String>?, params: Map<String, String>?, responseHandler: AsyncHttpResponseHandler) {
        execMethod("PUT", context, url, headers, params, responseHandler)
    }

    /**
     * 执行delete方法
     *
     * @param context
     * @param url
     * @param responseHandler
     */
    fun delete(context: Any, url: String, responseHandler: AsyncHttpResponseHandler) {
        delete(context, url, null, null, responseHandler)
    }

    /**
     * 执行delete方法
     *
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    fun delete(context: Any, url: String, headers: Map<String, String>?, params: Map<String, String>?, responseHandler: AsyncHttpResponseHandler) {
        execMethod("DELETE", context, url, headers, params, responseHandler)
    }

    /**
     * 执行方法
     *
     * @param method
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    fun execMethod(method: String, context: Any, url: String, headers: Map<String, String>?, params: Map<String, String>?, responseHandler: AsyncHttpResponseHandler) {
        val headerBuilder = Headers.Builder()
        if (headers != null) {
            for ((key, value) in headers) {
                headerBuilder.add(key, value)
            }
        }

        val requestBodyBuilder = FormBody.Builder()
        if (params != null) {
            for ((key, value) in params) {
                if (value != null)
                    requestBodyBuilder.add(key, value)
            }
        }
        var request: Request? = null
        if ("GET".equals(method, ignoreCase = true)) {
            request = Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .get()
                    .build()
        } else {
            request = Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .method(method, requestBodyBuilder.build())
                    .build()
        }
        sendRequest(httpClient, request, responseHandler, context)
    }

    /**
     * 执行方法
     *
     * @param method
     * @param context
     * @param url
     * @param headers
     * @param params
     * @param responseHandler
     */
    fun execMethod(method: String, context: Any, url: String, headers: Map<String, String>?, params: RequestParams, responseHandler: AsyncHttpResponseHandler) {
        val headerBuilder = Headers.Builder()
        if (headers != null) {
            for ((key, value) in headers) {
                headerBuilder.add(key, value)
            }
        }
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for ((key, value) in params.getFileParams()) {
            val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), value)
            requestBodyBuilder.addFormDataPart(key, value.name, fileBody)
        }

        for ((key, value) in params.getUrlParams()) {
            requestBodyBuilder.addFormDataPart(key, value)
        }

        var request: Request? = null
        if ("GET".equals(method, ignoreCase = true)) {
            request = Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .get()
                    .build()
        } else {
            request = Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .method(method, requestBodyBuilder.build())
                    .build()
        }

        sendRequest(httpClient, request, responseHandler, context)
    }

    /**
     * @param method
     * @param context
     * @param url
     * @param headers
     * @param requestBody
     * @param responseHandler
     */
    fun execMethod(method: String, context: Any, url: String, headers: Map<String, String>?, requestBody: RequestBody, responseHandler: AsyncHttpResponseHandler) {
        val headerBuilder = Headers.Builder()
        if (headers != null) {
            for ((key, value) in headers) {
                headerBuilder.add(key, value)
            }
        }

        var request: Request? = null
        if ("GET".equals(method, ignoreCase = true)) {
            request = Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .get()
                    .build()
        } else {
            request = Request.Builder()
                    .tag(context)
                    .headers(headerBuilder.build())
                    .url(url)
                    .method(method, requestBody)
                    .build()
        }

        sendRequest(httpClient, request, responseHandler, context)
    }

    protected fun sendRequest(client: OkHttpClient, uriRequest: Request?, responseHandler: AsyncHttpResponseHandler, context: Any?) {
        val request = threadPool!!.submit(AsyncHttpRequest(this, client, uriRequest!!, responseHandler))
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
     * @param context
     * @param mayInterruptIfRunning
     */
    fun cancelRequests(context: Any, mayInterruptIfRunning: Boolean) {

        val requestList = requestMap[context]
        if (requestList != null) {
            for (requestRef in requestList) {
                val request = requestRef.get()
                request?.cancel(mayInterruptIfRunning)
            }
        }
        requestMap.remove(context)

        for (call in httpClient.dispatcher().queuedCalls()) {
            if (call.request().tag() == group) {
                call.cancel()
            }
        }
        for (call in httpClient.dispatcher().runningCalls()) {
            if (call.request().tag() == group) {
                call.cancel()
            }
        }
    }

    /**
     * 取消所有
     */
    fun cancelAll() {
        httpClient.dispatcher().cancelAll()
    }

    fun close() {
        threadPool!!.close(false)
    }

    companion object {
        private const val DEFAULT_RETRY_TIMES = 3

        /**
         * 构造一个实例
         *
         * @param group
         * @return
         */
        @JvmStatic fun build(group: String): AsyncHttpClient {
            return AsyncHttpClient(group)
        }

        /**
         * 构造一个实例
         *
         * @param group
         * @param client
         * @return
         */
        @JvmStatic fun build(group: String, client: OkHttpClient): AsyncHttpClient {
            return AsyncHttpClient(group, client)
        }
    }
}
