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
package mobi.cangol.mobile.service.cache

import android.app.Application
import android.text.TextUtils
import mobi.cangol.mobile.CoreApplication
import mobi.cangol.mobile.Task
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.AppService
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import mobi.cangol.mobile.service.conf.ConfigService
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * @author Cangol
 */

@Service("CacheManager")
internal class CacheManagerImpl : CacheManager {
    private val mDiskCacheLock = Object()
    private var mDebug: Boolean = false
    private var mDiskLruCache: DiskLruCache? = null
    private val mContextMaps = HashMap<String, HashMap<String, CacheObject>>()
    private var mDiskCacheStarting = true
    private var mDiskCacheDir: File? = null
    private var mDiskCacheSize: Long = 0
    private var mServiceProperty = ServiceProperty(TAG)
    private var mApplication: CoreApplication? = null

    override fun onCreate(context: Application) {
        this.mApplication = context as CoreApplication
        if (mDebug) Log.d(TAG, "onCreate")
    }

    override fun init(serviceProperty: ServiceProperty) {
        if (mDebug) Log.d(TAG, "init $serviceProperty")
        this.mServiceProperty = serviceProperty
        val dir = mServiceProperty!!.getString(CacheManager.CACHE_DIR)
        val size = mServiceProperty!!.getLong(CacheManager.CACHE_SIZE)
        val configService = mApplication!!.getAppService(AppService.CONFIG_SERVICE) as ConfigService?
        val cacheDir = configService!!.getCacheDir().absolutePath + File.separator + if (!TextUtils.isEmpty(dir)) dir else "contentCache"
        setDiskCache(File(cacheDir), if (size > 0) size else DEFAULT_DISK_CACHE_SIZE)
    }

    /**
     * 设置磁盘缓存位置，并初始化
     *
     * @param cacheDir
     * @param cacheSize
     */
    private fun setDiskCache(cacheDir: File, cacheSize: Long) {
        if (mDebug) Log.d(TAG, "setDiskCache dir=$cacheDir,size=$cacheSize")
        this.mDiskCacheDir = cacheDir
        this.mDiskCacheSize = cacheSize
        this.mDiskCacheStarting = true
        initDiskCache(mDiskCacheDir, mDiskCacheSize)
    }

    /**
     * 初始化磁盘缓存
     *
     * @param diskCacheDir
     * @param diskCacheSize
     */
    private fun initDiskCache(diskCacheDir: File?, diskCacheSize: Long) {
        if (mDebug) Log.d(TAG, "initDiskCache dir=$diskCacheDir,size=$diskCacheSize")
        // Set up disk cache
        synchronized(mDiskCacheLock) {
            if (mDiskLruCache == null || mDiskLruCache!!.isClosed) {
                if (diskCacheDir != null) {
                    if (!diskCacheDir.exists()) {
                        diskCacheDir.mkdirs()
                    }
                    if (getUsableSpace(diskCacheDir) > diskCacheSize) {
                        try {
                            mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, diskCacheSize)
                            if (mDebug) {
                                Log.d(TAG, "Disk cache initialized")
                            }
                        } catch (e: IOException) {
                            Log.e(TAG, "initDiskCache - $e")
                        }

                    }
                } else {
                    //
                }
            }
            mDiskCacheStarting = false
            mDiskCacheLock.notifyAll()
        }
    }

    override fun getContent(context: String, id: String): Serializable? {
        if (mDebug) Log.d(TAG, "getContent context=$context,id=$id")
        var contextMap = mContextMaps[context]
        if (null == contextMap) {
            contextMap = HashMap()
            mContextMaps[context] = contextMap
        }
        var cacheObject = contextMap[id]
        if (cacheObject == null) {
            cacheObject = getContentFromDiskCache(id)
            if (cacheObject != null) {
                contextMap[id] = cacheObject
            }
        }
        return if (cacheObject != null) {
            if (cacheObject.isExpired) {
                Log.e(TAG, "expired:" + cacheObject.expired + ",it's expired & removed ")
                removeContent(context, id)
                null
            } else {
                cacheObject.getObject()!!
            }
        } else {
            null
        }
    }

    override fun getContent(context: String, id: String, cacheLoader: CacheLoader) {
        if (mDebug)
            Log.d(TAG, "getContent context=$context,id=$id,cacheLoader=$cacheLoader")
        cacheLoader?.loading()
        var contextMap = mContextMaps[context]
        if (null == contextMap) {
            contextMap = HashMap()
            mContextMaps[context] = contextMap
        }
        val cacheObject = contextMap[id]
        if (cacheObject == null) {
            mApplication!!.post(object : Task<CacheObject?>() {

                override fun call(): CacheObject? {
                    return getContentFromDiskCache(id)
                }

                override fun result(cacheObject: CacheObject?) {
                    if (cacheObject != null) {
                        if (cacheObject.isExpired) {
                            Log.e(TAG, "expired:" + cacheObject.expired + ",it's expired & removed ")
                            removeContent(context, id)
                            cacheLoader?.returnContent(null!!)
                        } else {
                            addContentToMem(context, id, cacheObject.getObject(), cacheObject.period)
                            cacheLoader?.returnContent(cacheObject.getObject()!!)
                        }
                    } else {
                        cacheLoader?.returnContent(null)
                    }
                }
            })
        } else {
            if (cacheObject.isExpired) {
                Log.e(TAG, "expired:" + cacheObject.expired + ",it's expired & removed ")
                removeContent(context, id)
                cacheLoader?.returnContent(null!!)
            } else {
                cacheLoader?.returnContent(cacheObject.getObject()!!)
            }
        }
    }

    override fun hasContent(context: String, id: String): Boolean {
        if (mDebug) Log.d(TAG, "hasContent context=$context,id=$id")
        var contextMap = mContextMaps[context]
        if (null == contextMap) {
            contextMap = HashMap()
            mContextMaps[context] = contextMap
        }
        val cacheObject = contextMap[id]
        if (cacheObject == null) {
            return hasContentFromDiskCache(id)
        } else {
            if (cacheObject.isExpired) {
                Log.e(TAG, "expired:" + cacheObject.expired + ",it's expired & removed ")
                removeContent(context, id)
                return false
            } else {
                return true
            }
        }
    }

    /**
     * 判断磁盘缓存是否含有
     *
     * @param id
     * @return
     */
    private fun hasContentFromDiskCache(id: String): Boolean {
        if (mDebug) Log.d(TAG, "hasContentFromDiskCache id=$id")
        val key = hashKeyForDisk(id)
        synchronized(mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait()
                } catch (e: InterruptedException) {
                    Log.d(e.message)
                }

            }
            if (mDiskLruCache != null && !mDiskLruCache!!.isClosed) {
                var inputStream: InputStream? = null
                try {
                    val snapshot = mDiskLruCache!!.get(key)
                    if (snapshot != null) {
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX)
                        if (inputStream != null) {
                            val cacheObject = readObject(inputStream) as CacheObject?
                            if (cacheObject != null && cacheObject.isExpired) {
                                Log.e(TAG, "expired:" + cacheObject.expired + ",it's expired & removed ")
                                mDiskLruCache!!.remove(hashKeyForDisk(id))
                                return false
                            } else {
                                return true
                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "getContentFromDiskCache - $e")
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: IOException) {
                        Log.d(e.message)
                    }

                }
            }
            return false
        }
    }

    /**
     * 从磁盘缓存获取
     *
     * @param id
     * @return
     */
    private fun getContentFromDiskCache(id: String): CacheObject? {
        if (mDebug) Log.d(TAG, "getContentFromDiskCache id=$id")
        val key = hashKeyForDisk(id)
        synchronized(mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait()
                } catch (e: InterruptedException) {
                    Log.d(e.message)
                }

            }
            if (mDiskLruCache != null && !mDiskLruCache!!.isClosed) {
                var inputStream: InputStream? = null
                try {
                    val snapshot = mDiskLruCache!!.get(key)
                    if (snapshot != null) {
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX)
                        if (inputStream != null) {
                            return readObject(inputStream) as CacheObject?
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "getContentFromDiskCache - $e")
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: IOException) {
                        Log.d(e.message)
                    }

                }
            }
            return null
        }

    }

    /**
     * 添加到内存缓存
     *
     * @param context
     * @param id
     * @param data
     */
    private fun addContentToMem(context: String, id: String, data: Serializable) {
        var contextMap = mContextMaps[context]
        if (null == contextMap) {
            contextMap = HashMap()
        }
        contextMap[id] = CacheObject(context, id, data)
        mContextMaps[context] = contextMap
    }

    /**
     * 添加到内存缓存
     *
     * @param context
     * @param id
     * @param data
     * @param period
     */
    private fun addContentToMem(context: String, id: String, data: Serializable?, period: Long) {
        var contextMap = mContextMaps[context]
        if (null == contextMap) {
            contextMap = HashMap()
        }
        contextMap[id] = CacheObject(context, id, data!!, period)
        mContextMaps[context] = contextMap
    }

    /**
     * 添加到磁盘缓存（也添加到内存缓存）
     */
    override fun addContent(context: String, id: String, data: Serializable) {
        if (mDebug) Log.d(TAG, "addContent:$id,$data")
        removeContent(context, id)
        addContentToMem(context, id, data)
        asyncAddContentToDiskCache(id, CacheObject(context, id, data))
    }

    override fun addContent(context: String, id: String, data: Serializable, period: Long) {
        if (mDebug) Log.d(TAG, "addContent:$id,$data,$period")
        removeContent(context, id)
        addContentToMem(context, id, data, period)
        asyncAddContentToDiskCache(id, CacheObject(context, id, data, period))
    }

    /**
     * 异步添加到磁盘缓存
     *
     * @param id
     * @param cacheObject
     */
    private fun asyncAddContentToDiskCache(id: String, cacheObject: CacheObject) {
        mApplication!!.post(Runnable { addContentToDiskCache(id, cacheObject) })
    }

    /**
     * 添加到磁盘缓存
     *
     * @param id
     * @param cacheObject
     */
    private fun addContentToDiskCache(id: String, cacheObject: CacheObject) {

        synchronized(mDiskCacheLock) {
            // Add to disk cache
            if (mDiskLruCache != null && !mDiskLruCache!!.isClosed) {
                val key = hashKeyForDisk(id)
                var out: OutputStream? = null
                try {
                    val snapshot = mDiskLruCache!!.get(key)
                    if (snapshot == null) {
                        val editor = mDiskLruCache!!.edit(key)
                        if (editor != null) {
                            out = editor.newOutputStream(DISK_CACHE_INDEX)
                            // 写入out流
                            writeObject(cacheObject, out)
                            editor.commit()
                            out!!.close()
                            flush()
                        }
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX)?.close()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "addContentToCache - $e")
                } finally {
                    try {
                        out?.close()
                    } catch (e: IOException) {
                        Log.d(e.message)
                    }

                }
            }
        }
    }

    override fun removeContext(context: String) {
        val contextMap = mContextMaps[context]
        if (null == contextMap || contextMap.isEmpty()) {
            return
        }
        val iterator = contextMap.keys.iterator()
        var id: String? = null
        while (iterator.hasNext()) {
            id = iterator.next()
            val key = hashKeyForDisk(id)
            try {
                if (mDiskLruCache != null && !mDiskLruCache!!.isClosed) {
                    mDiskLruCache!!.remove(key)
                }
            } catch (e: IOException) {
                if (mDebug) {
                    Log.d(TAG, "cache remove$key", e)
                }
            }

        }
        contextMap.clear()
        mContextMaps.remove(context)
    }

    override fun removeContent(context: String, id: String) {
        val contextMap = mContextMaps[context]
        if (null == contextMap || contextMap.isEmpty()) {
            return
        }
        contextMap.remove(id)
        val key = hashKeyForDisk(id)
        try {
            if (mDiskLruCache != null && !mDiskLruCache!!.isClosed) {
                mDiskLruCache!!.remove(key)
            }
        } catch (e: IOException) {
            if (mDebug) {
                Log.d(TAG, "cache remove$key", e)
            }
        }

    }

    override fun size(): Long {
        var size: Long = 0
        synchronized(mDiskCacheLock) {
            if (mDiskLruCache != null) {
                size = mDiskLruCache!!.size()
            }
        }
        return size
    }

    override fun clearCache() {
        if (mContextMaps != null) {
            mContextMaps.clear()
            if (mDebug) {
                Log.d(TAG, "Memory cache cleared")
            }
        }

        synchronized(mDiskCacheLock) {
            mDiskCacheStarting = true
            if (mDiskLruCache != null && !mDiskLruCache!!.isClosed) {
                try {
                    mDiskLruCache!!.delete()
                    if (mDebug) {
                        Log.d(TAG, "Disk cache cleared")
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "clearCache - $e")
                }

                mDiskLruCache = null
                initDiskCache(mDiskCacheDir, mDiskCacheSize)
            }
        }
    }

    override fun flush() {
        synchronized(mDiskCacheLock) {
            if (mDiskLruCache != null && !mDiskLruCache!!.isClosed) {
                try {
                    mDiskLruCache!!.flush()
                    if (mDebug) {
                        Log.d(TAG, "Disk cache flushed")
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "flush - $e")
                }

            }
        }
    }

    override fun close() {
        synchronized(mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    if (!mDiskLruCache!!.isClosed) {
                        mDiskLruCache!!.close()
                        mDiskLruCache = null
                        if (mDebug) {
                            Log.d(TAG, "Disk cache closed")
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "close - $e")
                }

            }
        }
    }

    private fun hashKeyForDisk(key: String): String {
        var cacheKey: String? = null
        cacheKey = try {
            val mDigest = MessageDigest.getInstance("MD5")
            mDigest.update(key.toByteArray(charset("UTF-8")))
            bytesToHexString(mDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            key.hashCode().toString()
        } catch (e: UnsupportedEncodingException) {
            key.hashCode().toString()
        }

        return cacheKey
    }

    private fun readObject(inputStream: InputStream): Serializable? {
        var obj: Any? = null
        try {
            obj = ObjectInputStream(BufferedInputStream(inputStream)).readObject()
        } catch (e: Exception) {
            Log.e(TAG, "readObject", e)
        }

        return obj as Serializable?
    }

    private fun writeObject(obj: Serializable, out: OutputStream) {
        var bos: BufferedOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            bos = BufferedOutputStream(out)
            oos = ObjectOutputStream(bos)
            oos.writeObject(obj)
        } catch (e: Exception) {
            Log.e(TAG, "writeObject", e)
        } finally {
            try {
                oos?.close()
            } catch (e: IOException) {
                Log.e(TAG, "writeObject close", e)
            }

            try {
                bos?.close()
            } catch (e: IOException) {
                Log.e(TAG, "writeObject close", e)
            }

        }
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        // http://stackoverflow.com/questions/332079
        val sb = StringBuilder()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString()
    }

    private fun getUsableSpace(path: File): Long {
        return path.usableSpace
    }

    override fun getName(): String {
        return TAG
    }

    override fun onDestroy() {
        this.close()
    }

    override fun setDebug(mDebug: Boolean) {
        this.mDebug = mDebug
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun defaultServiceProperty(): ServiceProperty {
        val sp = ServiceProperty(TAG)
        sp.putString(CacheManager.CACHE_DIR, "contentCache")
        sp.putInt(CacheManager.CACHE_SIZE, 20971520)
        return sp
    }

    companion object {
        private const val TAG = "CacheManager"
        private const val DISK_CACHE_INDEX = 0
        private const val DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 20L // 20MB
    }

}
