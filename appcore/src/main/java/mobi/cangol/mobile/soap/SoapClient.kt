/**
 * Copyright (c) 2013 Cangol.
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
package mobi.cangol.mobile.soap

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import mobi.cangol.mobile.Task
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.PoolManager
import mobi.cangol.mobile.socket.SocketClient
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import org.kxml2.kdom.Element
import org.kxml2.kdom.Node
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Future

/**
 * SoapClient.java 使用此类需要ksoap2-android-assembly-3.0.0-jar-with-dependencies.jar
 *
 * @author Cangol
 */
class SoapClient {
    private val requestMap= mutableMapOf<Context, MutableList<WeakReference<Future<*>>>>()
    private val envelope=SoapSerializationEnvelope(SoapEnvelope.VER12)
    private val threadPool: PoolManager.Pool = PoolManager.buildPool("SoapRequest", 3)

    init {
        envelope.dotNet = true
    }

    /**
     * 添加header
     *
     * @param namespace
     * @param authheader
     * @param headers
     */
    fun addHeader(namespace: String, authHeader: String?, headers: Map<String, String>?) {
        if (authHeader != null && headers != null) {
            envelope.headerOut = arrayOfNulls(1)
            envelope.headerOut[0] = buildAuthHeader(namespace, authHeader, headers)
        }
    }

    /**
     * 执行请求
     *
     * @param context
     * @param url
     * @param namespace
     * @param action
     * @param params
     * @param responseHandler
     */
    fun send(context: Context, url: String, namespace: String, action: String, params: Map<String, String>?, responseHandler: SoapResponseHandler) {

        if (params != null) {
            val paramsStr = StringBuilder(url)
            paramsStr.append('/')
                    .append(action)
            if (params.isNotEmpty()) {
                paramsStr.append('?')
            }
            val rpc = SoapObject(namespace, action)
            for ((key, value) in params) {
                rpc.addProperty(key, value)
                paramsStr.append(key)
                        .append('=')
                        .append(value)
                        .append('&')
            }
            Log.d("sendRequest $paramsStr")
            envelope.bodyOut = rpc
        }
        sendRequest(HttpTransportSE(url, 20 * 1000), envelope, namespace, responseHandler, context)
    }

    /**
     * 构建auth header
     *
     * @param namespace
     * @param authHeader
     * @param params
     * @return
     */
    private fun buildAuthHeader(namespace: String, authHeader: String, params: Map<String, String>): Element {
        val header = Element().createElement(namespace, authHeader)
        for ((key, value) in params) {
            val element = Element().createElement(namespace, key)
            element.addChild(Node.TEXT, value)
            header.addChild(Node.ELEMENT, element)
        }
        return header
    }

    /**
     * 取消请求
     *
     * @param context
     * @param mayInterruptIfRunning
     */
    fun cancelRequests(context: Context, mayInterruptIfRunning: Boolean) {
        val requestList = requestMap[context]
        if (requestList != null) {
            for (requestRef in requestList) {
                val request = requestRef.get()
                request?.cancel(mayInterruptIfRunning)
            }
        }
        requestMap.remove(context)
    }

    /**
     * 发生请求
     *
     * @param ht
     * @param envelope
     * @param namespace
     * @param responseHandler
     * @param context
     */
    private fun sendRequest(ht: HttpTransportSE, envelope: SoapSerializationEnvelope,
                              namespace: String, responseHandler: SoapResponseHandler, context: Context?) {

        val request = threadPool.submit(SoapRequest(ht, envelope, namespace, responseHandler))

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
}
