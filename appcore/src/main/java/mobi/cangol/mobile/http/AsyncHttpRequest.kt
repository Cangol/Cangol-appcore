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

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException

internal class AsyncHttpRequest(private val client: AsyncHttpClient, private val content: OkHttpClient, private val request: Request, private val responseHandler: AsyncHttpResponseHandler?) : Runnable {
    private var isBinaryRequest: Boolean = false
    private var executionCount: Int = 0

    init {
        if (responseHandler is BinaryHttpResponseHandler) {
            this.isBinaryRequest = true
        }
    }

    override fun run() {
        try {
            responseHandler?.sendStartMessage()

            makeRequestWithRetries()

            responseHandler?.sendFinishMessage()
        } catch (e: IOException) {
            if (responseHandler != null) {
                responseHandler.sendFinishMessage()
                if (this.isBinaryRequest) {
                    responseHandler.sendFailureMessage(e, null)
                } else {
                    responseHandler.sendFailureMessage(e, null)
                }
            }
        }

    }

    @Throws(IOException::class)
    private fun makeRequest() {
        if (!Thread.currentThread().isInterrupted) {
            val response = content.newCall(request).execute()
            if (!Thread.currentThread().isInterrupted) {
                responseHandler?.sendResponseMessage(response)
            } else {
                Log.d("AsyncHttpRequest", "Thread.isInterrupted")
            }
        }
    }

    @Throws(ConnectException::class)
    private fun makeRequestWithRetries() {
        // This is an additional layer of retry logic lifted from droid-fu
        // See: https://github.com/kaeppler/droid-fu/blob/master/src/main/java/com/github/droidfu/http/BetterHttpRequestBase.java
        var retry = true
        var cause: IOException? = null
        val retryHandler = client.retryHandler
        while (retry) {
            try {
                makeRequest()
                return
            } catch (e: UnknownHostException) {
                responseHandler!!.sendFailureMessage(e, "can't resolve host")
                return
            } catch (e: SocketException) {
                // Added to detect host unreachable
                responseHandler!!.sendFailureMessage(e, "can't resolve host")
                return
            } catch (e: IOException) {
                cause = e
                retry = retryHandler!!.retryRequest(cause, ++executionCount)
            } catch (e: NullPointerException) {
                // there's a bug in HttpClient 4.0.x that on some occasions causes
                // DefaultRequestExecutor to throw an NPE, see
                // http://code.google.com/p/android/issues/detail?id=5255
                cause = IOException("NPE in HttpClient" + e.message)
                retry = retryHandler!!.retryRequest(cause, ++executionCount)
            }

        }

        // no retries left, crap out with exception
        val ex = ConnectException()
        ex.initCause(cause)
        throw ex
    }
}
