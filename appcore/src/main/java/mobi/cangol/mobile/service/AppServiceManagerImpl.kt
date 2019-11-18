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
package mobi.cangol.mobile.service

import android.os.Build
import android.os.StrictMode
import mobi.cangol.mobile.CoreApplication
import mobi.cangol.mobile.Task
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.utils.ClassUtils
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Field
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class AppServiceManagerImpl(private val mContext: CoreApplication) : AppServiceManager() {
    private val mRunServiceMap = HashMap<String, AppService>()
    private val mServiceMap = HashMap<String, Class<out AppService>>()
    private val mUseAnnotation = true
    private val mProperties = HashMap<String, ServiceProperty>()
    private var mDebug = false

    init {
        initClass()
    }

    private fun initClass() {
        var classList: MutableList<Class<out AppService>>? = null
        Log.d(TAG, "SDK_INT=" + Build.VERSION.SDK_INT)
        /** mulit dex not used
         * if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
         * Log.d(TAG,"Class Scan");
         * classList=ClassUtils.getAllClassByInterface(AppService.class, mContext, this.getClass().getPackage().getName());
         * classList.addAll(ClassUtils.getAllClassByInterface(AppService.class, mContext, mContext.getPackageName()));
         * //2.2-2.3 版本 Process terminated by signal (11) 堆栈溢出
         * }else */

        classList = ArrayList()
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.analytics.AnalyticsServiceImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.cache.CacheManagerImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.conf.ConfigServiceImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.crash.CrashServiceImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.download.DownloadManagerImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.location.LocationServiceImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.session.SessionServiceImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.status.StatusServiceImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.upgrade.UpgradeServiceImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.event.ObserverManagerImpl") as Class<AppService>)
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.route.RouteServiceImpl") as Class<AppService>)

        Log.d(TAG, "classList size=" + classList.size)
        for (i in classList.indices) {
            Log.d(TAG, "classname=" + classList[i].simpleName)
        }
        initServiceMap(classList)
    }

    private fun initServiceMap(classList: List<Class<out AppService>>) {
        for (clazz in classList) {
            registerService(clazz)
        }
    }

    override fun setDebug(debug: Boolean) {
        this.mDebug = debug
    }

    override fun getAppService(name: String): AppService? {
        var appService: AppService? = null
        if (mRunServiceMap.containsKey(name)) {
            appService = mRunServiceMap[name]
        } else {
            try {
                if (mServiceMap.containsKey(name)) {
                    val c = mServiceMap[name]!!.getDeclaredConstructor()
                    c.isAccessible = true
                    appService = c.newInstance()
                    appService!!.onCreate(mContext)
                    appService.init(if (mProperties[name] != null) mProperties[name]!! else appService.defaultServiceProperty())
                    appService.setDebug(mDebug)
                    mRunServiceMap[name] = appService
                } else {
                    throw IllegalStateException("hasn't appService'name is $name")
                }
            } catch (e: Exception) {
                Log.d(e.message)
            }
        }
        return appService
    }

    override fun registerService(clazz: Class<out AppService>) {
        try {
            if (mUseAnnotation) {
                if (clazz.isAnnotationPresent(Service::class.java)) {
                    val service = clazz.getAnnotation(Service::class.java)
                    this.mServiceMap[service!!.value] = clazz
                } else {
                    Log.d(TAG, "$clazz no Service Annotation")
                }
            } else {
                val method = clazz.getMethod("getName")
                val t = clazz.newInstance()
                val name = method.invoke(t) as String
                this.mServiceMap[name] = clazz
            }
        } catch (e: Exception) {
            Log.d(TAG,"registerService "+e.message)
        }

    }

    /**
     * @param appService
     * @param serviceProperty
     */
    @Deprecated("")
    private fun init(appService: AppService, serviceProperty: ServiceProperty) {
        var filed: Field? = null
        try {
            filed = appService.javaClass.getDeclaredField("mServiceProperty")
            if (filed == null) {
                filed = appService.javaClass.getDeclaredField("serviceProperty")
            } else {
                for (filed1 in appService.javaClass.declaredFields) {
                    filed1.isAccessible = true
                    if (filed1.type == ServiceProperty::class.java) {
                        filed = filed1
                        break
                    }
                }
            }
            if (filed != null) {
                filed.isAccessible = true
                filed.set(appService, serviceProperty)
            }
        } catch (e: Exception) {
            Log.d(e.message)
        }

    }

    override fun destroyService(name: String) {
        var appService: AppService? = null
        if (mRunServiceMap.containsKey(name)) {
            appService = mRunServiceMap[name]
            appService!!.onDestroy()
            mRunServiceMap.remove(name)
        } else {
            Log.d(TAG, "$name Service is not running")
        }
    }

    override fun destroyAllService() {
        Log.d(TAG, "destroyAllService")
        for ((_, value) in mRunServiceMap) {
            value.onDestroy()
        }
        mRunServiceMap.clear()

    }

    override fun destroy() {
        Log.d(TAG, "destroy")
        destroyAllService()
        mProperties.clear()
        mServiceMap.clear()
    }

    override fun setScanPackage(vararg packageName: String) {
        if (packageName.isNotEmpty()) {
            mContext.post(object : Task<List<Class<out AppService>>>() {

                override fun call(): List<Class<out AppService>> {
                    val classList = ArrayList<Class<out AppService>>()
                    for (name in packageName) {
                        classList.addAll(ClassUtils.getAllClassByInterface(AppService::class.java, mContext, name))
                    }
                    return classList
                }

                override fun result(list: List<Class<out AppService>>) {
                    initServiceMap(list)
                }
            })
        }
    }


    @Deprecated("")
    fun initServiceProperties() {
        if (Build.VERSION.SDK_INT >= 14) {
            // Temporarily disable logging of disk reads on the Looper thread
            val oldPolicy = StrictMode.allowThreadDiskReads()
            val inputStream = this.javaClass.getResourceAsStream("properties.xml")
            initSource(inputStream!!)
            StrictMode.setThreadPolicy(oldPolicy)
        } else {
            val inputStream = this.javaClass.getResourceAsStream("properties.xml")
            initSource(inputStream!!)
        }
    }

    override fun initSource(inputStream: InputStream) {
        try {
            parser(inputStream)
            inputStream.close()
        } catch (e: Exception) {
            Log.d(e.message)
        }

    }

    @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
    private fun parser(inputStream: InputStream) {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(inputStream)
        val root = document.documentElement
        val nodeList = root.childNodes
        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)
            if (node is Element) {
                val name = node.getAttribute("name")
                val nodeList2 = node.childNodes
                val properties = ServiceProperty(name)
                for (j in 0 until nodeList2.length) {
                    val node2 = nodeList2.item(j)
                    if (node2 is Element) {
                        properties.putString(node2.getAttribute("name"), node2.textContent)
                    }
                }
                mProperties[name] = properties
            }
        }
    }

    companion object {
        private const val TAG = "AppServiceManager"
    }
}
