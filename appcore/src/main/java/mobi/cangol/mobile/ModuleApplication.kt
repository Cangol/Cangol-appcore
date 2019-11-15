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

import mobi.cangol.mobile.service.AppService
import mobi.cangol.mobile.service.PoolManager
import mobi.cangol.mobile.service.session.SessionService
import java.util.concurrent.Callable
import java.util.concurrent.Future


/**
 * @author Cangol
 */

open class ModuleApplication {
    private var mCoreApplication: CoreApplication? = null

    fun getApplication(): CoreApplication {
        return mCoreApplication!!
    }

    fun setCoreApplication(application: CoreApplication) {
        this.mCoreApplication = application
    }

    open fun onCreate() {

    }

    fun getAppService(name: String): AppService? {
        return getApplication().getAppService(name)
    }

    /**
     * 获取Session
     *
     * @return
     */
    fun getSession(): SessionService {
        return getAppService(AppService.SESSION_SERVICE) as SessionService
    }

    /**
     * 获取共享线程池
     *
     * @return
     */
    fun getSharePool(): PoolManager.Pool {
        return getApplication().getSharePool()
    }

    /**
     * 提交一个后台线程任务
     *
     * @param runnable
     */
    fun post(runnable: Runnable): Future<*> {
        return getSharePool().submit(runnable)
    }

    /**
     * 提交一个后台线程任务
     *
     * @param runnable
     */
    fun <T> post(runnable: Runnable, result: T): Future<T> {
        return getSharePool().submit(runnable, result)
    }

    /**
     * 提交一个后台回调任务
     *
     * @param callable
     */
    fun <T> post(callable: Callable<T>): Future<T> {
        return getSharePool().submit(callable)
    }

    /**
     * 提交一个后台回调任务
     *
     * @param task
     */
    fun post(task: Task<*>): Future<*> {
        return getSharePool().submit(task)
    }

    fun init() {}

    fun onTerminate() {}

    fun onLowMemory() {}

    fun onTrimMemory(level: Int) {}

    fun onExit() {

    }

}
