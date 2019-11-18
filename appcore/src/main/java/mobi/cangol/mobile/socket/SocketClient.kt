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

import mobi.cangol.mobile.service.PoolManager
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Future

/**
 * Created by weixuewu on 15/11/11.
 */
class SocketClient {
    private val requestMap = WeakHashMap<String, MutableList<WeakReference<Future<*>>>>()
    private val handlerMap = WeakHashMap<String, WeakReference<SocketHandler>>()
    private val pool = PoolManager.buildPool("SocketClient", 3)
    private var port: Int = 0
    private var host: String? = null
    private var isLong: Boolean = false


    fun setLong(aLong: Boolean) {
        isLong = aLong
    }

    fun setHost(host: String) {
        this.host = host
    }

    fun setPort(port: Int) {
        this.port = port
    }

    fun connect(tag: String, socketHandler: SocketHandler): Future<*> {
        return connect(tag, this.host!!, this.port, this.isLong, 20 * 1000, socketHandler)
    }

    fun connect(tag: String?, host: String, port: Int, isLong: Boolean, timeout: Int, socketHandler: SocketHandler?): Future<*> {
        val request = pool.submit(SocketThread(host, port, isLong, timeout, pool!!.executorService!!, socketHandler))
        if (tag != null && socketHandler != null) {
            handlerMap[tag] = WeakReference(socketHandler)
        }
        // Add request to request map
        var requestList: MutableList<WeakReference<Future<*>>>? = requestMap[tag]
        if (requestList == null) {
            requestList = LinkedList()
            requestMap[tag!!] = requestList
        }
        requestList.add(WeakReference(request))
        return request
    }

    fun cancelRequests(tag: String) {
        val requestList = requestMap[tag]
        if (requestList != null) {
            for (requestRef in requestList) {
                val request = requestRef.get()
                request?.cancel(true)
            }
        }
        requestMap.remove(tag)

        for ((key, value) in handlerMap) {
            if (key == tag && value.get() != null) {
                value.get()!!.interrupted()
            }
        }
        handlerMap.remove(tag)
    }

    fun cancel(mayInterruptIfRunning: Boolean) {
        for ((_, value) in handlerMap) {
            value.get()!!.interrupted()
        }
        for ((_, value) in requestMap) {
            for (requestRef in value) {
                val request = requestRef.get()
                request?.cancel(mayInterruptIfRunning)
            }
        }
    }

    fun shutdown() {
        pool.executorService!!.shutdown()
    }

    companion object {

        fun build(): SocketClient {
            return SocketClient()
        }
    }
}
