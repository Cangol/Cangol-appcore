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
package mobi.cangol.mobile.service.download;

import android.app.Application;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;

/**
 * @author Cangol
 */
@Service("DownloadManager")
class DownloadManagerImpl implements DownloadManager {
    protected static final int DEFAULT_MAX_THREAD = 2;
    private static final String TAG = "DownloadManager";
    protected boolean debug = false;
    protected ConcurrentHashMap<String, DownloadExecutor<?>> executorMap = new ConcurrentHashMap<>();
    private Application mContext = null;
    private ServiceProperty mServiceProperty;
    private ConfigService mConfigService;

    public void onCreate(Application context) {
        this.mContext = context;
        mConfigService = (ConfigService) ((CoreApplication) mContext).getAppService(AppService.CONFIG_SERVICE);

    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
    }

    @Override
    public synchronized DownloadExecutor<?> getDownloadExecutor(String name) {
        DownloadExecutor<?> downloadExecutor = null;
        if (executorMap.containsKey(name)) {
            downloadExecutor = executorMap.get(name);
        }
        return downloadExecutor;
    }

    //提前注册各个下载器 减少需要用时再初始化造成的时间消耗（初始化扫描耗时较多）
    @Override
    public void registerExecutor(String name, Class<? extends DownloadExecutor<?>> clazz, int max) {
        if (!executorMap.containsKey(name)) {
            executorMap.put(name, createDownloadExecutor(name, clazz, max));
        }
    }

    private DownloadExecutor<?> createDownloadExecutor(String name, Class<? extends DownloadExecutor<?>> clazz, int max) {
        DownloadExecutor<?> downloadExecutor = null;
        try {
            final Constructor<? extends DownloadExecutor<?>> c = clazz.getDeclaredConstructor(String.class);
            c.setAccessible(true);
            downloadExecutor = c.newInstance(name);
            downloadExecutor.setContext(mContext);
            downloadExecutor.setPool(PoolManager.buildPool(name, max));
            downloadExecutor.setDownloadDir(new File(mConfigService.getDownloadDir(), name));
            downloadExecutor.init();
        } catch (Exception e) {
            Log.d(e.getMessage());
        }
        return downloadExecutor;
    }

    @Override
    public void recoverAllAllDownloadExecutor() {
        final Enumeration<DownloadExecutor<?>> en = executorMap.elements();
        DownloadExecutor<?> downloadExecutor = null;
        while (en.hasMoreElements()) {
            downloadExecutor = en.nextElement();
            downloadExecutor.recoverAll();
        }
    }

    @Override
    public void interruptAllDownloadExecutor() {
        if (null == executorMap) {
            return;
        }
        final Enumeration<DownloadExecutor<?>> en = executorMap.elements();
        DownloadExecutor<?> downloadExecutor = null;
        while (en.hasMoreElements()) {
            downloadExecutor = en.nextElement();
            downloadExecutor.interruptAll();
        }
    }

    @Override
    public void onDestroy() {
        if (null == executorMap) {
            return;
        }
        final Enumeration<DownloadExecutor<?>> en = executorMap.elements();
        while (en.hasMoreElements()) {
            en.nextElement().close();
        }
        executorMap.clear();
        PoolManager.clear();
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void setDebug(boolean mDebug) {
        this.debug = mDebug;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        final ServiceProperty sp = new ServiceProperty(TAG);
        sp.putString(DOWNLOADSERVICE_THREADPOOL_NAME, TAG);
        sp.putInt(DOWNLOADSERVICE_THREAD_MAX, DEFAULT_MAX_THREAD);
        return sp;
    }
}
