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
package mobi.cangol.mobile

import android.app.Activity
import android.app.Application
import android.os.StrictMode
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.AppService
import mobi.cangol.mobile.service.AppServiceManager
import mobi.cangol.mobile.service.AppServiceManagerImpl
import mobi.cangol.mobile.service.PoolManager
import mobi.cangol.mobile.service.session.SessionService
import mobi.cangol.mobile.utils.Constants
import mobi.cangol.mobile.utils.DeviceInfo
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Future
import kotlin.system.exitProcess


/**
 * @author Cangol
 */

open class CoreApplication : Application() {
    private var mDevMode = false
    private var mStrictMode = false
    private var mAsyncInit = false
    private var mAppServiceManager: AppServiceManager? = null
    private val mSharePool = PoolManager.getPool("share")
    private var mModuleManager: ModuleManager = ModuleManager()
    private val mActivityManager: MutableList<WeakReference<Activity>> = ArrayList()
    override fun onCreate() {
        super.onCreate()
        mModuleManager.setApplication(this)
        if (DeviceInfo.isAppProcess(this)) {
            if (mStrictMode) {
                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
                StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
            }
            if (mDevMode) {
                Log.setLogLevelFormat(android.util.Log.VERBOSE, false)
            } else {
                Log.setLogLevelFormat(android.util.Log.WARN, true)
            }
            initAppServiceManager()
            mModuleManager.onCreate()
            if (mAsyncInit) {
                post(Runnable {
                    init()
                    mModuleManager.init()
                })
            } else {
                init()
                mModuleManager.init()
            }
        } else {
            mModuleManager.onCreate()
            Log.i("cur process is not app' process")
        }
    }

    fun getModuleManager(): ModuleManager {
        return mModuleManager
    }

    override fun onTerminate() {
        super.onTerminate()
        mModuleManager.onTerminate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mModuleManager.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        mModuleManager.onTrimMemory(level)
    }

    /**
     * 设置是否异步初始化(在super.onCreate方法之前调用才有效)
     *
     * @param asyncInit
     */
    protected fun setAsyncInit(asyncInit: Boolean) {
        this.mAsyncInit = asyncInit
    }

    /**
     * 获取是否异步初始化
     *
     * @return
     */
    protected fun isAsyncInit(): Boolean {
        return mAsyncInit
    }

    /**
     * 初始化方法，当setAsyncInit为true 此处执行异步init
     */
    protected open fun init() {
        //do nothings
    }

    /**
     * 获取当前是否研发模式 研发模式log级别为VERBOSE，非研发模式log级别为WARN
     *
     * @return
     */
    fun isDevMode(): Boolean {
        return mDevMode
    }

    /**
     * 设置研发模式 (在super.onCreate方法之前调用才有效)
     *
     * @param devMode
     */
    protected fun setDevMode(devMode: Boolean) {
        this.mDevMode = devMode
    }

    /**
     * 是否严格模式
     *
     * @return
     */
    protected fun isStrictMode(): Boolean {
        return mStrictMode
    }

    /**
     * 设置是否严格模式 (在super.onCreate方法之前调用才有效)
     *
     * @param mStrictMode
     */
    protected fun setStrictMode(mStrictMode: Boolean) {
        this.mStrictMode = mStrictMode
    }

    /**
     * 初始化应用服务管理器
     */
    private fun initAppServiceManager() {
        mAppServiceManager = AppServiceManagerImpl(this)
    }

    /**
     * 获取应用服务
     *
     * @param name
     * @return
     */
    fun getAppService(name: String): AppService? {
        return mAppServiceManager?.getAppService(name)
    }

    /**
     * 获取共享线程池
     *
     * @return
     */
    fun getSharePool(): PoolManager.Pool {
        return mSharePool
    }

    /**
     * 提交一个后台线程任务
     *
     * @param runnable
     */
    fun post(runnable: Runnable): Future<*> {
        return mSharePool.submit(runnable)
    }

    /**
     * 提交一个后台线程任务
     *
     * @param runnable
     */
    fun <T> post(runnable: Runnable, result: T): Future<T> {
        return mSharePool.submit(runnable, result)
    }

    /**
     * 提交一个后台回调任务
     *
     * @param callable
     */
    fun <T> post(callable: Callable<T>): Future<T> {
        return mSharePool.submit(callable)
    }

    /**
     * 提交一个后台回调任务
     *
     * @param task
     */
    fun post(task: Task<*>): Future<*> {
        return mSharePool.submit(task)
    }


    /**
     * 添加一个activity到管理列表里
     *
     * @param activity
     */
    fun addActivityToManager(activity: Activity) {
        for (activityReference in mActivityManager) {
            if (activity != activityReference.get()) {
                mActivityManager.add(WeakReference(activity))
            }
        }
    }

    /**
     * 关闭所有activity
     */
    fun closeAllActivities() {
        for (activityReference in mActivityManager) {
            if (activityReference.get() != null) {
                activityReference.get()!!.finish()
            }
        }
    }

    /**
     * 删除activity从管理列表里
     *
     * @param activity
     */
    fun delActivityFromManager(activity: Activity) {
        for (activityReference in mActivityManager) {
            if (activity == activityReference.get()) {
                mActivityManager.remove(activityReference)
            }
        }
    }

    /**
     * 获取所有应用的所有activity
     *
     * @return
     */

    fun getActivityManager(): List<WeakReference<Activity>> {
        return mActivityManager
    }

    /**
     * 获取session
     *
     * @return
     */
    fun getSession(): SessionService {
        return getAppService(AppService.SESSION_SERVICE) as SessionService
    }

    /**
     * 退出应用
     */
    fun exit() {
        mModuleManager.onExit()
        mModuleManager.clear()
        getSession().saveString(Constants.KEY_EXIT_CODE, "0")
        getSession().saveString(Constants.KEY_EXIT_VERSION, DeviceInfo.getAppVersion(this))
        mAppServiceManager?.destroy()
        PoolManager.closeAll()
        // 0 正常推退出
        exitProcess(0)
    }


}
