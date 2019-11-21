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

import android.os.Handler
import android.os.Looper
import android.os.Message
import mobi.cangol.mobile.logging.Log
import okhttp3.Response
import java.io.IOException

open class PollingResponseHandler {
    private var handler: Handler? = null

    init {
        if (Looper.myLooper() != null) {
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    this@PollingResponseHandler.handleMessage(msg)
                }
            }
        }
    }

    /**
     * 是否结束请求
     *
     * @param content
     * @return
     */
    open fun isFailResponse(content: String?): Boolean {
        return false
    }

    /**
     * 启动开始
     */
    open fun onStart() {
        //do nothings
    }

    /**
     * 轮询结束
     *
     * @param execTimes
     * @param content
     */
    open fun onPollingFinish(execTimes: Int, content: String) {
        //do nothings
    }

    /**
     * 轮询成功
     *
     * @param statusCode
     * @param content
     */
    open fun onSuccess(statusCode: Int, content: String) {
        //do nothings
    }

    /**
     * 轮询失败
     *
     * @param error
     * @param content
     */
    open fun onFailure(error: Throwable, content: String) {
        //do nothings
    }

    fun sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, arrayOf(-1, "exec start")))
    }

    fun sendFinishMessage(execTimes: Int) {
        sendMessage(obtainMessage(FINISH_MESSAGE, arrayOf(execTimes, "exec finish")))
    }

    protected fun sendSuccessMessage(statusCode: Int, responseBody: String?) {
        sendMessage(responseBody?.let { arrayOf(statusCode, it) }?.let { obtainMessage(SUCCESS_MESSAGE, it) })
    }

    fun sendFailureMessage(e: IOException, responseBody: String?) {
        sendMessage(responseBody?.let { arrayOf<Any>(e, it) }?.let { obtainMessage(FAILURE_MESSAGE, it) })
    }

    fun sendResponseMessage(response: Response): Boolean {
        var result:Boolean
        val responseBody = response.body()
        var content: String? = null
        if (response.isSuccessful) {
            if (responseBody != null) {
                try {
                    content = responseBody.string()
                } catch (e: IOException) {
                    Log.e(e.message)
                }

            }
            if (isFailResponse(content)) {
                sendFailureMessage(IOException("code=" + response.code()), content)
                result = false
            } else {
                sendSuccessMessage(response.code(), content)
                result = true
            }
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
            FINISH_MESSAGE -> {
                response = msg.obj as Array<Any>
                handleFinishMessage((response[0] as Int).toInt(), response[1] as String)
            }
            START_MESSAGE -> handleStartMessage()
            else -> {
            }
        }
    }

    protected fun handleStartMessage() {
        onStart()
    }

    protected fun handleFinishMessage(execTimes: Int, responseBody: String) {
        onPollingFinish(execTimes, responseBody)
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

    protected fun obtainMessage(responseMessage: Int, response: Any): Message? {
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
        protected const val START_MESSAGE = -1
        protected const val SUCCESS_MESSAGE = 0
        protected const val FAILURE_MESSAGE = 1
        protected const val FINISH_MESSAGE = 2
    }
}