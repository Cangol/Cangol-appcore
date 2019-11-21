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

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * Created by weixuewu on 15/11/11.
 */
abstract class SocketHandler {
    private var handler: InternalHandler? = null
    private var isInterrupted = false
    private var readLocker = Any()
    private var writeLocker = Any()

    fun getReadLocker(): Any {
        return readLocker
    }

    fun getWriteLocker(): Any {
        return writeLocker
    }

    init {
        if (Looper.myLooper() != null) {
            handler = InternalHandler(this)
        }
    }

    internal class InternalHandler(socketHandler: SocketHandler) : Handler() {
        private val reference: WeakReference<SocketHandler> = WeakReference(socketHandler)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            reference.get()?.handleMessage(msg)
        }
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

    protected fun obtainMessage(responseMessage: Int, arg1: Int, arg2: Int, response: Any): Message? {
        var msg: Message?
        if (handler != null) {
            msg = this.handler!!.obtainMessage(responseMessage, arg1, arg2, response)
        } else {
            msg = Message()
            msg.what = responseMessage
            msg.arg1 = arg1
            msg.arg2 = arg2
            msg.obj = response
        }
        return msg
    }

    open fun handleMessage(msg: Message) {
        when (msg.what) {
            FAIL_MESSAGE -> handleFailMessage((msg.obj as Array<Any>)[0], Exception((msg.obj as Array<Any>)[1] as Throwable))
            START_MESSAGE -> handleStartMessage()
            CONNECTED_MESSAGE -> handleConnectedMessage()
            DISCONNECTED_MESSAGE -> handleDisconnectedMessage()
            else -> {
            }
        }
    }

    @Throws(IOException::class)
    abstract fun handleSocketWrite(outputStream: DataOutputStream): Boolean

    @Throws(IOException::class, ClassNotFoundException::class)
    abstract fun handleSocketRead(inputStream: DataInputStream): Boolean

    abstract fun getSend(): Any

    abstract fun onFail(obj: Any, e: Exception)

    open fun onStart() {}

    open fun onConnected() {}

    open fun onDisconnected() {}


    fun sendFailMessage(obj: Any) {
        sendMessage(obtainMessage(FAIL_MESSAGE, obj))
    }

    fun sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null))
    }

    fun sendConnectedMessage() {
        sendMessage(obtainMessage(CONNECTED_MESSAGE, null))
    }

    fun sendDisconnectedMessage() {
        sendMessage(obtainMessage(DISCONNECTED_MESSAGE, null))
    }

    private fun handleFailMessage(obj: Any, exception: Exception) {
        onFail(obj, exception)
    }

    private fun handleStartMessage() {
        onStart()
    }

    private fun handleConnectedMessage() {
        onConnected()
    }

    private fun handleDisconnectedMessage() {
        onDisconnected()
    }

    fun isInterrupted(): Boolean {
        return isInterrupted
    }

    fun interrupted() {
        isInterrupted = true
    }

    companion object {
        protected const val FAIL_MESSAGE = -1
        protected const val START_MESSAGE = -4
        protected const val CONNECTED_MESSAGE = -2
        protected const val DISCONNECTED_MESSAGE = -3
    }
}
