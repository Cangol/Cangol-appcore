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

import android.util.Log

import java.io.IOException
import java.io.InterruptedIOException

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class DownloadThread(private val context: DownloadHttpClient, private val client: OkHttpClient, private val request: Request, private val responseHandler: DownloadResponseHandler?, private val saveFile: String) : Runnable {
    private var executionCount: Int = 0
    private var from: Long = 0

    override fun run() {
        try {
            from = parserFrom(request)
            responseHandler!!.sendWaitMessage()
            makeRequestWithRetries()
        } catch (e: Exception) {
            responseHandler!!.sendFailureMessage(e, e.message!!)
        }

    }

    @Throws(IOException::class)
    private fun makeRequest() {
        if (!Thread.currentThread().isInterrupted) {

            val response = client.newCall(request).execute()
            if (!Thread.currentThread().isInterrupted) {
                responseHandler?.sendResponseMessage(response, saveFile)
            } else {
                Log.d(TAG, "Thread.isInterrupted")
                responseHandler!!.sendStopMessage(from)
            }
        } else {
            Log.d(TAG, "Thread.isInterrupted")
            responseHandler!!.sendStopMessage(from)
        }
    }

    private fun parserFrom(request: Request): Long {
        var value = request.header("Range")
        if (value != null && value.contains("bytes=")) {
            value = value.substring(value.indexOf("bytes=") + "bytes=".length, value.indexOf('-'))
        } else {
            value = "0"
        }
        return java.lang.Long.parseLong(value)
    }

    @Throws(Exception::class)
    private fun makeRequestWithRetries() {
        var retry = true
        var cause: Exception? = null
        val retryHandler = this.context.downloadRetryHandler
        while (retry) {
            try {
                makeRequest()
                return
            } catch (e: InterruptedIOException) {
                responseHandler!!.sendStopMessage(from)
                return
            } catch (e: IOException) {
                cause = e
                retry = retryHandler!!.retryRequest(e, ++executionCount)
            } catch (e: Exception) {
                cause = e
                retry = false
            }

        }

        throw Exception(cause)
    }

    companion object {
        val TAG = "DownloadThread"
    }
}
