/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.session.SessionService;

/**
 * @author Cangol
 */

public class ModuleApplication {
    private CoreApplication mCoreApplication;

    protected void setCoreApplication(CoreApplication application) {
        this.mCoreApplication = application;
    }

    protected final CoreApplication getApplication() {
        return mCoreApplication;
    }

    protected void onCreate() {

    }

    public final AppService getAppService(String name) {
        return getApplication().getAppService(name);
    }

    /**
     * 获取Session
     *
     * @return
     */
    public final SessionService getSession() {
        return ((SessionService) getAppService(AppService.SESSION_SERVICE));
    }

    /**
     * 获取共享线程池
     *
     * @return
     */
    public final PoolManager.Pool getSharePool() {
        return getApplication().getSharePool();
    }

    /**
     * 提交一个后台线程任务
     *
     * @param runnable
     */
    public final Future post(Runnable runnable) {
        return getSharePool().submit(runnable);
    }

    /**
     * 提交一个后台线程任务
     *
     * @param runnable
     */
    public final <T> Future<T> post(Runnable runnable, T result) {
        return getSharePool().submit(runnable, result);
    }

    /**
     * 提交一个后台回调任务
     *
     * @param callable
     */
    public final <T> Future<T> post(Callable<T> callable) {
        return getSharePool().submit(callable);
    }

    /**
     * 提交一个后台回调任务
     *
     * @param task
     */
    public final Future post(Task task) {
        return getSharePool().submit(task);
    }

    protected void init() {
    }

    protected void onTerminate() {
    }

    protected void onLowMemory() {
    }

    protected void onTrimMemory(int level) {
    }

    protected void onExit() {

    }

}
