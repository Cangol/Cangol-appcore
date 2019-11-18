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
package mobi.cangol.mobile.http.download

import android.os.Handler
import android.os.Looper
import android.os.Message
import mobi.cangol.mobile.logging.Log
import okhttp3.Response
import java.io.IOException
import java.io.RandomAccessFile

open class DownloadResponseHandler {
    private var handler: Handler? = null

    init {
        if (Looper.myLooper() != null) {
            handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    this@DownloadResponseHandler.handleMessage(msg)
                }
            }
        }
    }

    /**
     * 等候状态
     */
    open fun onWait() {
        if (DEBUG) Log.d(TAG, "onWait")
        //do nothings
    }

    /**
     * 开始状态
     *
     * @param start
     * @param length
     */
    open fun onStart(start: Long, length: Long) {
        if (DEBUG) Log.d(TAG, "onStart start=$start,length=$length")
        //do nothings
    }

    /**
     * 停止状态
     *
     * @param end
     */
    open fun onStop(end: Long) {
        if (DEBUG) Log.d(TAG, "onStop end=$end")
        //do nothings
    }

    /**
     * 完成状态
     *
     * @param end
     */
    open fun onFinish(end: Long) {
        if (DEBUG) Log.d(TAG, "onFinish end=$end")
        //do nothings
    }

    /**
     * 更新状态
     *
     * @param end
     * @param progress
     * @param speed
     */
    open fun onProgressUpdate(end: Long, progress: Int, speed: Int) {
        if (DEBUG) Log.d(TAG, "onProgressUpdate end=$end,progress=$progress,speed=$speed")
        //do nothings
    }

    /**
     * 失败状态
     *
     * @param error
     * @param content
     */
    open fun onFailure(error: Throwable, content: String) {
        if (DEBUG) Log.d(TAG, "onFailure content=$content")
        //do nothings
    }

    fun sendWaitMessage() {
        sendMessage(obtainMessage(WAIT_MESSAGE, null))
    }

    fun sendStartMessage(start: Long, length: Long) {
        sendMessage(obtainMessage(START_MESSAGE, arrayOf<Any>(start, length)))
    }

    fun sendStopMessage(end: Long) {
        sendMessage(obtainMessage(STOP_MESSAGE, arrayOf<Any>(end)))
    }

    fun sendFinishMessage(end: Long) {
        sendMessage(obtainMessage(FINISH_MESSAGE, arrayOf<Any>(end)))
    }

    fun sendProgressMessage(end: Long, progress: Int, speed: Int) {
        sendMessage(obtainMessage(PROGRESS_MESSAGE, arrayOf(end, progress, speed)))
    }

    fun sendFailureMessage(e: Exception, responseBody: String) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, arrayOf<Any>(e, responseBody)))
    }

    @Throws(IOException::class)
    internal fun sendResponseMessage(response: Response, saveFile: String) {
        if (response.isSuccessful) {
            val responseBody = response.body()
            val length = responseBody!!.contentLength()
            val threadfile = RandomAccessFile(saveFile, "rwd")
            val inputStream = responseBody.byteStream()
            var oldLength = threadfile.length()
            sendStartMessage(oldLength, length)
            if (oldLength < length) {
                threadfile.seek(oldLength)
                val block = ByteArray(BUFF_SIZE)
                var starTime = System.currentTimeMillis()
                var startLength: Long = 0
                var readCount = 0
                var end = false
                while (!Thread.currentThread().isInterrupted && !end) {
                    readCount = inputStream.read(block, 0, BUFF_SIZE)
                    end = readCount == -1
                    if (!end) {
                        threadfile.write(block, 0, readCount)
                        oldLength += readCount.toLong()
                        startLength += readCount.toLong()
                        if (System.currentTimeMillis() - starTime > 1000L) {
                            val progress = (oldLength * 1.0f / length * 100).toInt()
                            val speed = (startLength * 1000.0f / (System.currentTimeMillis() - starTime)).toInt()
                            sendProgressMessage(oldLength, progress, speed)
                            starTime = System.currentTimeMillis()
                            startLength = 0
                        }
                    }
                }
                threadfile?.close()
                if (Thread.currentThread().isInterrupted) {
                    sendStopMessage(oldLength)
                } else {
                    if (oldLength == length) {
                        sendProgressMessage(oldLength, 100, 0)
                        sendFinishMessage(length)
                    } else {
                        sendFinishMessage(oldLength)
                    }
                }
            } else if (oldLength == length) {
                sendProgressMessage(oldLength, 100, 0)
                sendFinishMessage(oldLength)
            } else {
                sendFailureMessage(IOException(), "oldfile error oldLength>length")
            }
            responseBody?.close()
        } else {
            sendFailureMessage(IOException(), "StatusCode " + response.code())
        }
    }

    protected fun handleMessage(msg: Message) {
        var response: Array<Any>? = null
        when (msg.what) {
            PROGRESS_MESSAGE -> {
                response = msg.obj as Array<Any>
                handleProgressMessage((response[0] as Long).toLong(), (response[1] as Int).toInt(), (response[2] as Int).toInt())
            }
            FAILURE_MESSAGE -> {
                response = msg.obj as Array<Any>
                handleFailureMessage(response[0] as Throwable, response[1] as String)
            }
            STOP_MESSAGE -> {
                response = msg.obj as Array<Any>
                handleStopMessage((response[0] as Long).toLong())
            }
            START_MESSAGE -> {
                response = msg.obj as Array<Any>
                handleStartMessage((response[0] as Long).toLong(), (response[1] as Long).toLong())
            }
            WAIT_MESSAGE -> handleWaitMessage()
            FINISH_MESSAGE -> {
                response = msg.obj as Array<Any>
                handleFinishMessage((response[0] as Long).toLong())
            }
            else -> {
            }
        }
    }

    protected fun handleWaitMessage() {
        onWait()
    }

    protected fun handleStartMessage(start: Long, length: Long) {
        onStart(start, length)
    }

    protected fun handleFinishMessage(end: Long) {
        onFinish(end)
    }

    protected fun handleStopMessage(end: Long) {
        onStop(end)
    }

    protected fun handleProgressMessage(end: Long, progress: Int, speed: Int) {
        onProgressUpdate(end, progress, speed)
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

    companion object {
        protected const val TAG = "DownloadResponseHandler"
        protected const val WAIT_MESSAGE = 0
        protected const val START_MESSAGE = 1
        protected const val PROGRESS_MESSAGE = 2
        protected const val STOP_MESSAGE = 3
        protected const val FAILURE_MESSAGE = 4
        protected const val FINISH_MESSAGE = 5
        private const val BUFF_SIZE = 8192
        private const val DEBUG = false
    }
}