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

import android.os.Message
import mobi.cangol.mobile.logging.Log
import java.io.*

/**
 * Created by weixuewu on 15/11/11.
 */
abstract class SocketSerializableHandler : SocketHandler() {
    private val TAG = "SocketSerializableHandler"
    private val DEBUG = false
    private val RECEIVE_MESSAGE = 0

    abstract fun onReceive(msg: Serializable)


    fun sendReceiveMessage(obj: Serializable) {
        sendMessage(obtainMessage(RECEIVE_MESSAGE, obj))
    }

    fun handleReceiveMessage(response: Serializable) {
        onReceive(response)
    }


    @Throws(IOException::class)
    protected fun write(os: OutputStream, obj: Serializable) {
        val oos = ObjectOutputStream(BufferedOutputStream(os))
        oos.writeObject(obj)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    protected fun read(inputStream: DataInputStream): Serializable {
        val ois = ObjectInputStream(BufferedInputStream(inputStream))
        val obj = ois.readObject()
        return obj as Serializable
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        if (msg.what == RECEIVE_MESSAGE) {
            handleReceiveMessage(msg.obj as Serializable)
        }
    }

    @Throws(IOException::class)
    override fun handleSocketWrite(outputStream: DataOutputStream): Boolean {
        val sendMsg = getSend() as Serializable
        if (sendMsg == null || outputStream == null) return false
        if (DEBUG) Log.d(TAG, "sendMsg=$sendMsg")
        write(outputStream, sendMsg)
        return true
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    override fun handleSocketRead(inputStream: DataInputStream): Boolean {
        if (inputStream == null) return false
        val receivedMsg = read(inputStream)
        if (DEBUG) Log.d(TAG, "receivedMsg=$receivedMsg")
        sendReceiveMessage(receivedMsg)
        return true
    }
}
