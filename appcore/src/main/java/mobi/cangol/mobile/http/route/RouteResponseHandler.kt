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

import android.os.Handler
import android.os.Looper
import android.os.Message
import okhttp3.Response
import java.io.IOException

open class RouteResponseHandler {
    private var handler: Handler? = null

    init {
        if (Looper.myLooper() != null) {
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    this@RouteResponseHandler.handleMessage(msg)
                }
            }
        }
    }

    /**
     * 开始请求
     */
    open fun onStart() {
        //do nothings
    }

    /**
     * 请求成功
     *
     * @param statusCode
     * @param content
     */
    open fun onSuccess(statusCode: Int, content: String) {
        //do nothings
    }

    /**
     * 请求失败
     *
     * @param error
     * @param content
     */
    open fun onFailure(error: Throwable, content: String) {
        //do nothings
    }

    fun sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null))
    }

    protected fun sendSuccessMessage(statusCode: Int, responseBody: String?) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, responseBody?.let { arrayOf<Any>(statusCode, it) }))
    }

    fun sendFailureMessage(e: IOException, responseBody: String?) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, responseBody?.let { arrayOf<Any>(e, it) }))
    }

    fun sendResponseMessage(response: Response): Boolean {
        var result:Boolean
        val responseBody = response.body()
        var content: String? = null
        if (response.isSuccessful) {
            if (responseBody != null) {
                content = responseBody.toString()
            }
            sendSuccessMessage(response.code(), content)
            result = true
        } else {
            sendFailureMessage(IOException("code=" + response.code()), content)
            result = false
        }

        return result
    }

    protected fun handleMessage(msg: Message) {
        val response: Array<Any>
        when (msg.what) {
            SUCCESS_MESSAGE -> {
                response = msg.obj as Array<Any>
                handleSuccessMessage((response[0] as Int).toInt(), response[1] as String)
            }
            FAILURE_MESSAGE -> {
                response = msg.obj as Array<Any>
                handleFailureMessage(response[0] as Throwable, response[1] as String)
            }
            START_MESSAGE -> handleStartMessage()
            else -> {
            }
        }
    }

    protected fun handleStartMessage() {
        onStart()
    }

    protected fun handleSuccessMessage(statusCode: Int, responseBody: String) {
        onSuccess(statusCode, responseBody)
    }

    protected fun handleFailureMessage(e: Throwable, responseBody: String) {
        onFailure(e, responseBody)
    }

    protected fun sendMessage(msg: Message?) {
        if (handler != null) {
            handler!!.sendMessage(msg)
        } else {
            handleMessage(msg!!)
        }
    }

    protected fun obtainMessage(responseMessage: Int, response: Any?): Message? {
        var msg: Message?
        if (handler != null) {
            msg = this.handler!!.obtainMessage(responseMessage, response)
        } else {
            msg = Message()
            msg.what = responseMessage
            msg.obj = response
        }
        return msg
    }

    companion object {
        protected val START_MESSAGE = 1
        protected val SUCCESS_MESSAGE = 2
        protected val FAILURE_MESSAGE = 3
    }
}