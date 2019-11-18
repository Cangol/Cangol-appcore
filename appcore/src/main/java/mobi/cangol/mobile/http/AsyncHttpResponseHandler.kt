/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package mobi.cangol.mobile.http

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import okhttp3.Response
import java.io.IOException
import java.lang.ref.WeakReference

open class AsyncHttpResponseHandler {
    protected val SUCCESS_MESSAGE = 0
    protected val FAILURE_MESSAGE = 1
    private val START_MESSAGE = 2
    private val FINISH_MESSAGE = 3
    private var handler: Handler? = null

    constructor() {
        // Set up a handler to post events back to the correct thread if possible
        if (Looper.myLooper() != null) {
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    this@AsyncHttpResponseHandler.handleMessage(msg)
                }
            }
        }
    }

    constructor(context: Context) {
        // Set up a handler to post events back to the correct thread if possible
        if (Looper.myLooper() != null) {
            handler = InternalHandler(context)
        }
    }

    open fun onStart() {
        //do nothings
    }

    fun onFinish() {
        //do nothings
    }

    open fun onSuccess(content: String) {
        //do nothings
    }

    fun onSuccess(statusCode: Int, content: String) {
        onSuccess(content)
    }

    fun onFailure(error: Throwable) {
        //do nothings
    }

    open fun onFailure(error: Throwable, content: String) {
        // By default, call the deprecated onFailure(Throwable) for compatibility
        onFailure(error)
    }

    open fun sendSuccessMessage(statusCode: Int, responseBody: String) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, arrayOf(statusCode, responseBody)))
    }

    fun sendFailureMessage(e: Throwable, responseBody: String?) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, arrayOf(e, responseBody)))
    }

    fun sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null))
    }

    fun sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null))
    }

    fun handleSuccessMessage(statusCode: Int, responseBody: String) {
        onSuccess(statusCode, responseBody)
    }

    open fun handleFailureMessage(e: Throwable, responseBody: String) {
        onFailure(e, responseBody)
    }

    // Methods which emulate android's Handler and Message methods
    protected open fun handleMessage(msg: Message) {
        val response: Array<Any?>
        when (msg.what) {
            SUCCESS_MESSAGE -> {
                response = msg.obj as Array<Any?>
                handleSuccessMessage((response[0] as Int).toInt(), response[1].toString())
            }
            FAILURE_MESSAGE -> {
                response = msg.obj as Array<Any?>
                handleFailureMessage(response[0] as Throwable, response[1].toString())
            }
            START_MESSAGE -> onStart()
            FINISH_MESSAGE -> onFinish()
            else -> {
            }
        }
    }

    fun sendMessage(msg: Message?) {
        if (handler != null) {
            handler!!.sendMessage(msg)
        } else {
            handleMessage(msg!!)
        }
    }

    protected fun obtainMessage(responseMessage: Int, response: Any?): Message? {
        var msg: Message? = null
        if (handler != null) {
            msg = this.handler!!.obtainMessage(responseMessage, response)
        } else {
            msg = Message()
            msg.what = responseMessage
            msg.obj = response
        }
        return msg
    }

    internal open fun sendResponseMessage(response: Response) {
        val requestBody = response.body()
        if (response.isSuccessful) {
            try {
                sendSuccessMessage(response.code(), requestBody!!.string())
            } catch (e: IOException) {
                sendFailureMessage(e, response.message())
            }

        } else {
            sendFailureMessage(IOException("code=" + response.code()), response.message())
        }
        response.close()
    }

    internal class InternalHandler(context: Context) : Handler() {
        private val mContext: WeakReference<Context> = WeakReference(context)

        override fun handleMessage(msg: Message) {
            val context = mContext.get()
            if (context != null) {
                handleMessage(msg)
            }
        }
    }

}