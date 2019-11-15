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

import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * @author Cangol
 */
open class SoapResponseHandler {

    private var handler: Handler? = null

    init {
        if (Looper.myLooper() != null) {
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    this@SoapResponseHandler.handleMessage(msg)
                }
            }
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

    open fun onFailure(error: String) {
        //do nothings
    }

     fun sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null))
    }

     fun sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null))
    }

     fun sendSuccessMessage(responseBody: String?) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, responseBody))
    }

     fun sendFailureMessage(responseBody: String?) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, responseBody))
    }

     fun handleMessage(msg: Message) {
        val response: Any
        when (msg.what) {
            SUCCESS_MESSAGE -> {
                response = msg.obj
                onSuccess(response as String)
            }
            FAILURE_MESSAGE -> {
                response = msg.obj
                onFailure(response as String)
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

     fun obtainMessage(responseMessage: Int, response: Any?): Message? {
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

    fun sendResponseMessage(response: String) {
        sendSuccessMessage(response)
    }

    companion object {
        protected const val SUCCESS_MESSAGE = 0
        protected const val FAILURE_MESSAGE = 1
        protected const val START_MESSAGE = 2
        protected const val FINISH_MESSAGE = 3
    }
}
