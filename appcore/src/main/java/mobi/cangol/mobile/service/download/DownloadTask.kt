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
package mobi.cangol.mobile.service.download

import android.os.Handler
import mobi.cangol.mobile.http.download.DownloadHttpClient
import mobi.cangol.mobile.http.download.DownloadResponseHandler
import mobi.cangol.mobile.service.PoolManager.Pool
import java.util.concurrent.Future

class DownloadTask constructor(private val downloadResource: DownloadResource, pool: Pool, private val handler: Handler, safe: Boolean = true) {
    private val downloadHttpClient: DownloadHttpClient
    private var future: Future<*>? = null
    var isRunning: Boolean = false
        private set
    private var downloadNotification: DownloadNotification? = null
    private val responseHandler = object : DownloadResponseHandler() {
        override fun onWait() {
            super.onWait()
            if (downloadNotification != null) {
                downloadNotification!!.createNotification()
            }
        }

        override fun onStart(start: Long, length: Long) {
            super.onStart(start, length)
            downloadResource.status = Download.STATUS_START
            downloadResource.fileLength = length
            sendDownloadMessage(Download.ACTION_DOWNLOAD_START, downloadResource)
        }

        override fun onStop(end: Long) {
            super.onStop(end)
            downloadResource.completeSize = end
            sendDownloadMessage(Download.ACTION_DOWNLOAD_STOP, downloadResource)
            if (downloadNotification != null) {
                downloadNotification!!.cancelNotification()
            }

        }

        override fun onFinish(end: Long) {
            super.onFinish(end)
            downloadResource.status = Download.STATUS_FINISH
            downloadResource.completeSize = end
            sendDownloadMessage(Download.ACTION_DOWNLOAD_FINISH, downloadResource)
            if (downloadNotification != null) {
                downloadNotification!!.finishNotification()
            }

        }

        override fun onProgressUpdate(end: Long, progress: Int, speed: Int) {
            super.onProgressUpdate(end, progress, speed)
            downloadResource.speed = speed.toLong()
            downloadResource.progress = progress
            downloadResource.completeSize = end
            sendDownloadMessage(Download.ACTION_DOWNLOAD_UPDATE, downloadResource)
            if (downloadNotification != null) {
                downloadNotification!!.updateNotification(progress, speed)//speed 转换
            }
        }

        override fun onFailure(error: Throwable, content: String) {
            super.onFailure(error, content)
            downloadResource.exception = content
            downloadResource.status = Download.STATUS_FAILURE
            sendDownloadMessage(Download.ACTION_DOWNLOAD_FAILED, downloadResource)
            if (downloadNotification != null) {
                downloadNotification!!.failureNotification()
            }

        }

    }

    init {
        downloadHttpClient = DownloadHttpClient.build(pool.name!!, safe)
        DownloadHttpClient.setThreadPool(pool)
    }

    fun setDownloadNotification(downloadNotification: DownloadNotification) {
        this.downloadNotification = downloadNotification
    }

    protected fun exec(downloadResource: DownloadResource, responseHandler: DownloadResponseHandler): Future<*> {
        return downloadHttpClient.send(downloadResource.key!!, downloadResource.url!!, responseHandler, downloadResource.completeSize, downloadResource.sourceFile!!)
    }

    fun start() {
        downloadResource.status = Download.STATUS_WAIT
        future = exec(downloadResource, responseHandler)
        isRunning = true
    }

    fun restart() {
        if (future != null && !future!!.isCancelled) {
            future!!.cancel(true)
        }
        downloadResource.reset()
        start()
        sendDownloadMessage(Download.ACTION_DOWNLOAD_CONTINUE, downloadResource)
    }

    fun resume() {
        downloadResource.status = Download.STATUS_WAIT
        future = exec(downloadResource, responseHandler)
        sendDownloadMessage(Download.ACTION_DOWNLOAD_CONTINUE, downloadResource)
        isRunning = true
    }

    fun stop() {
        if (future != null && !future!!.isCancelled) {
            future!!.cancel(true)
        }
        future = null
        downloadResource.status = Download.STATUS_STOP
        sendDownloadMessage(Download.ACTION_DOWNLOAD_STOP, downloadResource)
        isRunning = false
    }

    fun interrupt() {
        if (future != null && !future!!.isCancelled) {
            future!!.cancel(true)
        }
        future = null
        downloadResource.status = Download.STATUS_RERUN
        sendDownloadMessage(Download.ACTION_DOWNLOAD_STOP, downloadResource)
        isRunning = false
    }

    fun remove() {
        if (future != null && !future!!.isCancelled) {
            future!!.cancel(true)
        }
        future = null
        sendDownloadMessage(Download.ACTION_DOWNLOAD_DELETE, downloadResource)
        isRunning = false
    }

    fun sendDownloadMessage(what: Int, obj: DownloadResource) {
        val msg = handler.obtainMessage(what)
        msg.obj = obj
        msg.sendToTarget()
    }

}
