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

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.AppServiceManager;
import mobi.cangol.mobile.service.AppServiceManagerImpl;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.session.SessionService;
import mobi.cangol.mobile.utils.Constants;
import mobi.cangol.mobile.utils.DeviceInfo;

/**
 * @author Cangol
 */

public class CoreApplication extends Application {
    private boolean mDevMode = false;
    private boolean mStrictMode = false;
    private boolean mAsyncInit = false;
    private AppServiceManager mAppServiceManager;
    private PoolManager.Pool mSharePool;
    private ModuleManager mModuleManager;
    public final List<WeakReference<Activity>> mActivityManager = new ArrayList<>();

    public CoreApplication() {
        super();
        mModuleManager = new ModuleManager(this);
    }

    public ModuleManager getModuleManager() {
        return mModuleManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DeviceInfo.isAppProcess(this)) {
            if (mStrictMode && Build.VERSION.SDK_INT >= 14) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
            }
            if (mDevMode) {
                Log.setLogLevelFormat(android.util.Log.VERBOSE, false);
            } else {
                Log.setLogLevelFormat(android.util.Log.WARN, true);
            }
            mSharePool = PoolManager.getPool("share");
            initAppServiceManager();
            mModuleManager.onCreate();
            if (mAsyncInit) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        init();
                        mModuleManager.init();
                    }
                });
            } else {
                init();
                mModuleManager.init();
            }
        } else {
            mModuleManager.onCreate();
            Log.i("cur process is not app' process");
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mModuleManager.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mModuleManager.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        mModuleManager.onTrimMemory(level);
    }

    /**
     * 设置是否异步初始化(在super.onCreate方法之前调用才有效)
     *
     * @param asyncInit
     */
    protected final void setAsyncInit(boolean asyncInit) {
        this.mAsyncInit = asyncInit;
    }

    /**
     * 获取是否异步初始化
     *
     * @return
     */
    protected final boolean isAsyncInit() {
        return mAsyncInit;
    }

    /**
     * 初始化方法，当setAsyncInit为true 此处执行异步init
     */
    protected void init() {
        //do nothings
    }

    /**
     * 获取当前是否研发模式 研发模式log级别为VERBOSE，非研发模式log级别为WARN
     *
     * @return
     */
    public final boolean isDevMode() {
        return mDevMode;
    }

    /**
     * 设置研发模式 (在super.onCreate方法之前调用才有效)
     *
     * @param devMode
     */
    protected final void setDevMode(boolean devMode) {
        this.mDevMode = devMode;
    }

    /**
     * 是否严格模式
     *
     * @return
     */
    protected final boolean isStrictMode() {
        return mStrictMode;
    }

    /**
     * 设置是否严格模式 (在super.onCreate方法之前调用才有效)
     *
     * @param mStrictMode
     */
    protected final void setStrictMode(boolean mStrictMode) {
        this.mStrictMode = mStrictMode;
    }

    /**
     * 初始化应用服务管理器
     */
    private final void initAppServiceManager() {
        mAppServiceManager = new AppServiceManagerImpl(this);
    }

    /**
     * 获取应用服务管理器
     *
     * @return
     */
    protected final AppServiceManager getAppServiceManager() {
        return mAppServiceManager;
    }

    /**
     * 获取应用服务
     *
     * @param name
     * @return
     */
    public final AppService getAppService(String name) {
        if (mAppServiceManager != null) {
            return mAppServiceManager.getAppService(name);
        }
        return null;
    }

    /**
     * 获取共享线程池
     *
     * @return
     */
    public final PoolManager.Pool getSharePool() {
        return mSharePool;
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


    /**
     * 添加一个activity到管理列表里
     *
     * @param activity
     */
    public final void addActivityToManager(Activity activity) {
        for (final WeakReference<Activity> activityReference : getActivityManager()) {
            if (activityReference != null && !activity.equals(activityReference.get())) {
                getActivityManager().add(new WeakReference<Activity>(activity));
            }
        }
    }

    /**
     * 关闭所有activity
     */
    public final void closeAllActivities() {
        for (final WeakReference<Activity> activityReference : getActivityManager()) {
            if (activityReference != null && activityReference.get() != null) {
                activityReference.get().finish();
            }
        }
    }

    /**
     * 删除activity从管理列表里
     *
     * @param activity
     */
    public final void delActivityFromManager(Activity activity) {
        for (final WeakReference<Activity> activityReference : getActivityManager()) {
            if (activityReference != null && activity.equals(activityReference.get())) {
                getActivityManager().remove(activityReference);
            }
        }
    }

    /**
     * 获取所有应用的所有activity
     *
     * @return
     */

    public final List<WeakReference<Activity>> getActivityManager() {
        return mActivityManager;
    }

    /**
     * 获取session
     *
     * @return
     */
    public final SessionService getSession() {
        return (SessionService) getAppService(AppService.SESSION_SERVICE);
    }

    /**
     * 退出应用
     */
    public void exit() {
        mModuleManager.onExit();
        getSession().saveString(Constants.KEY_EXIT_CODE, "0");
        getSession().saveString(Constants.KEY_EXIT_VERSION, DeviceInfo.getAppVersion(this));
        if (mAppServiceManager != null) {
            mAppServiceManager.destroy();
        }
        PoolManager.closeAll();
        // 0 正常推退出
        System.exit(0);
    }


}
