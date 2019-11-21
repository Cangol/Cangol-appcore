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

import android.app.Application
import mobi.cangol.mobile.CoreApplication
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.AppService
import mobi.cangol.mobile.service.PoolManager
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import mobi.cangol.mobile.service.conf.ConfigService
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Cangol
 */
@Service("DownloadManager")
internal open class DownloadManagerImpl : DownloadManager {
    private var debug = false
    private var executorMap = ConcurrentHashMap<String, DownloadExecutor<*>>()
    private var mContext: Application? = null
    private var mServiceProperty = ServiceProperty(TAG)
    private var mConfigService: ConfigService? = null

    override fun onCreate(context: Application) {
        this.mContext = context
        this.mConfigService = (mContext as CoreApplication).getAppService(AppService.CONFIG_SERVICE) as ConfigService?
    }

    override fun init(serviceProperty: ServiceProperty) {
        this.mServiceProperty = serviceProperty
    }

    @Synchronized
    override fun getDownloadExecutor(name: String): DownloadExecutor<*>? {
        var downloadExecutor: DownloadExecutor<*>? = null
        if (executorMap.containsKey(name)) {
            downloadExecutor = executorMap[name]
        }
        return downloadExecutor
    }

    //提前注册各个下载器 减少需要用时再初始化造成的时间消耗（初始化扫描耗时较多）
    override fun registerExecutor(name: String, clazz: Class<out DownloadExecutor<*>>, max: Int) {
        if (!executorMap.containsKey(name)) {
            executorMap[name] = createDownloadExecutor(name, clazz, max)
        }
    }

    private fun createDownloadExecutor(name: String, clazz: Class<out DownloadExecutor<*>>, max: Int): DownloadExecutor<*> {
        var downloadExecutor: DownloadExecutor<*>? = null
        try {
            val c = clazz.getDeclaredConstructor(String::class.java)
            c.isAccessible = true
            downloadExecutor = c.newInstance(name)
            downloadExecutor!!.setContext(mContext!!)
            downloadExecutor.setPool(PoolManager.buildPool(name, max))
            downloadExecutor.setDownloadDir(File(mConfigService!!.getDownloadDir(), name))
            downloadExecutor.init()
        } catch (e: Exception) {
            Log.d(e.message)
        }

        return downloadExecutor!!
    }

    override fun recoverAllAllDownloadExecutor() {
        val en = executorMap.elements()
        var downloadExecutor: DownloadExecutor<*>?
        while (en.hasMoreElements()) {
            downloadExecutor = en.nextElement()
            downloadExecutor!!.recoverAll()
        }
    }

    override fun interruptAllDownloadExecutor() {
        val en = executorMap.elements()
        var downloadExecutor: DownloadExecutor<*>?
        while (en.hasMoreElements()) {
            downloadExecutor = en.nextElement()
            downloadExecutor!!.interruptAll()
        }
    }

    override fun onDestroy() {
        val en = executorMap.elements()
        while (en.hasMoreElements()) {
            en.nextElement().close()
        }
        executorMap.clear()
        PoolManager.clear()
    }

    override fun getName(): String {
        return TAG
    }

    override fun setDebug(mDebug: Boolean) {
        this.debug = mDebug
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun defaultServiceProperty(): ServiceProperty {
        val sp = ServiceProperty(TAG)
        sp.putString(DownloadManager.DOWNLOADSERVICE_THREADPOOL_NAME, TAG)
        sp.putInt(DownloadManager.DOWNLOADSERVICE_THREAD_MAX, DEFAULT_MAX_THREAD)
        return sp
    }

    companion object {
        protected const val DEFAULT_MAX_THREAD = 2
        private const val TAG = "DownloadManager"
    }
}
