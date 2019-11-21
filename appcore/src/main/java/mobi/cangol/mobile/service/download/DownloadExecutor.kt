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

import android.content.Context
import android.os.Handler
import android.os.Message
import mobi.cangol.mobile.CoreApplication
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.parser.JsonUtils
import mobi.cangol.mobile.service.PoolManager.Pool
import mobi.cangol.mobile.utils.FileUtils
import mobi.cangol.mobile.utils.Object2FileUtils
import java.io.File
import java.lang.ref.SoftReference
import java.util.*


abstract class DownloadExecutor<T>(private val mName: String) {
    private var mDownloadRes = ArrayList<DownloadResource>()
    private var mTag = "DownloadExecutor"
    private val mListeners = ArrayList<SoftReference<DownloadStatusListener>>()
    private var mPool: Pool? = null
    private var mContext: Context? = null
    private var mDownloadDir: File? = null
    private var mDownloadEvent: DownloadEvent? = null
    private val mHandler: ExecutorHandler
    private var mHttpSafe = true

    init {
        this.mHandler = ExecutorHandler(this)
        this.mTag = "DownloadExecutor_$mName"
    }

    fun setHttpSafe(safe: Boolean) {
        this.mHttpSafe = safe
    }

    fun setContext(context: Context) {
        this.mContext = context
    }

    fun getDownloadDir(): File {
        return mDownloadDir!!
    }

    fun setDownloadDir(directory: File) {
        mDownloadDir = directory
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    fun setPool(pool: Pool) {
        this.mPool = pool
    }

    fun init() {
        mDownloadRes.addAll(scanResource(mDownloadDir))
    }

    fun setDownloadEvent(downloadEvent: DownloadEvent) {
        this.mDownloadEvent = downloadEvent
    }

    /**
     * 下载对象转换为DownloadResource
     *
     * @param t
     * @return
     */
    protected abstract fun getDownloadResource(t: T): DownloadResource

    /**
     * DownloadResource转换为下载对象
     *
     * @param resource
     * @return
     */
    protected abstract fun getDownloadModel(resource: DownloadResource): T

    /**
     * 创建按一个状态栏通知
     *
     * @param context
     * @param resource
     * @return
     */
    abstract fun notification(context: Context?, resource: DownloadResource): DownloadNotification

    /**
     * 扫描本地的下载任务资源
     *
     * @return
     */
    protected fun scanResource(scanDir: File?): ArrayList<DownloadResource> {
        val list = ArrayList<DownloadResource>()
        val fileList = ArrayList<File>()
        //耗时操作
        FileUtils.searchBySuffix(scanDir!!, fileList, Download.SUFFIX_CONFIG)
        for (i in fileList.indices) {
            readResource(fileList[i].absolutePath)?.let { list.add(it) }
        }
        return list
    }

    /**
     * 读取文件下载资源
     *
     * @param filePath
     * @return
     */
    protected fun readResource(filePath: String): DownloadResource? {
        Log.d(mTag, "read DownloadResource <$filePath")
        //使用json格式存储
        var downloadResource: DownloadResource? = null
        try {
            //downloadResource= (DownloadResource) Object2FileUtils.readObject(new File(filePath));
            val jsonObject = Object2FileUtils.readFile2JSONObject(File(filePath))
            downloadResource = JsonUtils.parserToObject(DownloadResource::class.java, jsonObject, false, true)

        } catch (e: Exception) {
            Log.d(mTag, e.message)
        }

        return downloadResource
    }

    /**
     * 存除下载资源到本地
     *
     * @param resource
     */
    protected fun writeResource(resource: DownloadResource) {
        Log.d(mTag, "write DownloadResource >" + resource.confFile!!)
        if (mContext != null)
            (mContext!!.applicationContext as CoreApplication).post(Runnable {
                //使用json格式存储
                val jsonObject = JsonUtils.toJSONObject(resource, false, true)
                Object2FileUtils.writeJSONObject2File(jsonObject!!, resource.confFile!!)
            })
    }

    /**
     * 通过唯一识别符获取下载资源
     *
     * @param key
     * @return
     */
    fun getDownloadResource(key: String?): DownloadResource? {
        for (resource in mDownloadRes) {
            if (key != null && key == resource.key) {
                return resource
            }
        }
        return null
    }

    /**
     * 开始下载
     *
     * @param resource
     */
    fun start(resource: DownloadResource?) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null")
            return
        }
        if (mDownloadRes.contains(resource)) {
            var downloadTask = resource.downloadTask
            if (downloadTask == null) {
                downloadTask = DownloadTask(resource, mPool!!, mHandler, true)
                resource.downloadTask = downloadTask
                downloadTask.setDownloadNotification(notification(mContext, resource))
            }
            if (!downloadTask.isRunning) {
                downloadTask.start()
            }
        } else {
            val downloadTask = DownloadTask(resource, mPool!!, mHandler, true)
            resource.downloadTask = downloadTask
            downloadTask.setDownloadNotification(notification(mContext, resource))
            downloadTask.start()
            synchronized(mDownloadRes) {
                mDownloadRes.add(resource)
            }
        }
    }

    /**
     * 停止下载
     *
     * @param resource
     */
    fun stop(resource: DownloadResource?) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null")
            return
        }
        if (mDownloadRes.contains(resource)) {
            val downloadTask = resource.downloadTask
            if (downloadTask!!.isRunning) {
                downloadTask.stop()
            }
        } else {
            Log.e(mTag, "resource isn't exist")
        }
    }

    /**
     * 恢复下载
     *
     * @param resource
     */
    fun resume(resource: DownloadResource?) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null")
            return
        }
        if (mDownloadRes.contains(resource)) {
            val downloadTask = resource.downloadTask
            downloadTask!!.resume()
        }
    }

    /**
     * 重启下载
     *
     * @param resource
     */
    fun restart(resource: DownloadResource?) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null")
            return
        }
        if (mDownloadRes.contains(resource)) {
            val downloadTask = resource.downloadTask
            downloadTask!!.restart()
        }
    }

    /**
     * 添加下载任务
     *
     * @param resource
     */
    fun add(resource: DownloadResource?) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null")
            return
        }
        if (!mDownloadRes.contains(resource)) {
            val downloadTask = DownloadTask(resource, mPool!!, mHandler, mHttpSafe)
            resource.downloadTask = downloadTask
            downloadTask.setDownloadNotification(notification(mContext, resource))
            downloadTask.start()
            synchronized(mDownloadRes) {
                mDownloadRes.add(resource)
            }
        } else if (resource.status != Download.STATUS_FINISH) {
            val downloadTask = DownloadTask(resource, mPool!!, mHandler, mHttpSafe)
            resource.downloadTask = downloadTask
            downloadTask.setDownloadNotification(notification(mContext, resource))
            downloadTask.start()
        }
    }

    /**
     * 移除下载任务
     *
     * @param resource
     */
    fun remove(resource: DownloadResource?) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null")
            return
        }
        synchronized(mDownloadRes) {
            if (mDownloadRes.contains(resource)) {
                val downloadTask = resource.downloadTask
                downloadTask!!.remove()
                mDownloadRes.remove(resource)
            } else {
                Log.e(mTag, "resource isn't exist")
            }
        }
    }

    /**
     * 恢复所有下载
     */
    fun recoverAll() {
        synchronized(mDownloadRes) {
            var downloadTask: DownloadTask?
            for (resource in mDownloadRes) {
                downloadTask = resource.downloadTask
                if (resource.status == Download.STATUS_RERUN) {
                    downloadTask!!.resume()
                }
            }
        }
    }

    /**
     * 中断所有下载
     */
    fun interruptAll() {
        synchronized(mDownloadRes) {
            var downloadTask: DownloadTask?
            for (resource in mDownloadRes) {
                downloadTask = resource.downloadTask
                if (resource.status < Download.STATUS_STOP) {
                    downloadTask!!.interrupt()
                }
            }
        }
    }

    /**
     * 关闭所有下载
     */
    fun close() {
        synchronized(mDownloadRes) {
            var downloadTask: DownloadTask?
            for (resource in mDownloadRes) {
                downloadTask = resource.downloadTask
                downloadTask?.stop()
            }
        }
        mDownloadRes.clear()
        mPool!!.close(false)
    }

    /**
     * 注册下载状态监听
     */
    fun registerDownloadStatusListener(downloadStatusListener: DownloadStatusListener?) {
        requireNotNull(downloadStatusListener) { "downloadStatusListener is null!" }
        var isExist = false
        for (listener in mListeners) {
            if (downloadStatusListener == listener.get()) {
                isExist = true
                break
            }
        }
        if (!isExist) {
            mListeners.add(SoftReference(downloadStatusListener))
        }
    }

    /**
     * 移除下载状态监听
     */
    fun unregisterDownloadStatusListener(downloadStatusListener: DownloadStatusListener?) {
        requireNotNull(downloadStatusListener) { "downloadStatusListener is null!" }
        for (listener in mListeners) {
            if (downloadStatusListener == listener.get()) {
                mListeners.remove(listener)
                break
            }
        }
    }

    private fun notifyUpdateStatus(resource: DownloadResource, action: Int) {
        for (listener in mListeners) {
            if (null != listener.get()) {
                listener.get()!!.onStatusChange(resource, action)
            }
        }
    }

    private fun handleMessage(msg: Message) {
        val resource = msg.obj as DownloadResource
        when (msg.what) {
            Download.ACTION_DOWNLOAD_START -> {
                if (null != mDownloadEvent) {
                    mDownloadEvent!!.onStart(resource)
                }
                writeResource(resource)
            }
            Download.ACTION_DOWNLOAD_STOP -> writeResource(resource)
            Download.ACTION_DOWNLOAD_FINISH -> {
                if (null != mDownloadEvent) {
                    mDownloadEvent!!.onFinish(resource)
                }
                writeResource(rename(resource))
            }
            Download.ACTION_DOWNLOAD_FAILED -> {
                if (null != mDownloadEvent) {
                    mDownloadEvent!!.onFailure(resource)
                }
                writeResource(resource)
            }
            else -> {
            }
        }
        notifyUpdateStatus(resource, msg.what)
    }

    private fun rename(resource: DownloadResource): DownloadResource {
        if (resource.status == Download.STATUS_FINISH) {
            val file = File(resource.sourceFile)
            val result = file.renameTo(File(resource.sourceFile!!.replace(Download.SUFFIX_SOURCE, "")))
            if (result) resource.sourceFile = resource.sourceFile!!.replace(Download.SUFFIX_SOURCE, "")
        }
        return resource
    }

    internal class ExecutorHandler(downloadExecutor: DownloadExecutor<*>) : Handler() {
        private val reference: SoftReference<DownloadExecutor<*>> = SoftReference(downloadExecutor)

        override fun handleMessage(msg: Message) {
             reference.get()?.handleMessage(msg)
        }
    }
}
