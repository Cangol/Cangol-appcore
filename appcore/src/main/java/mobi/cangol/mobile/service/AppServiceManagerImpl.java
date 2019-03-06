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
package mobi.cangol.mobile.service;

import android.os.Build;
import android.os.StrictMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.Task;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.ClassUtils;

public class AppServiceManagerImpl extends AppServiceManager {
    private static final String TAG = "AppServiceManager";
    private CoreApplication mContext;
    private Map<String, AppService> mRunServiceMap = new HashMap<>();
    private Map<String, Class<? extends AppService>> mServiceMap = new HashMap<>();
    private boolean mUseAnnotation = true;
    private Map<String, ServiceProperty> mProperties = new HashMap<>();
    private boolean mDebug = false;

    public AppServiceManagerImpl(CoreApplication context) {
        this.mContext = context;
        initClass();
    }

    private void initClass() {
        List<Class<? extends AppService>> classList = null;
        Log.d(TAG, "SDK_INT=" + Build.VERSION.SDK_INT);
        /** mulit dex not used
         if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
         Log.d(TAG,"Class Scan");
         classList=ClassUtils.getAllClassByInterface(AppService.class, mContext, this.getClass().getPackage().getName());
         classList.addAll(ClassUtils.getAllClassByInterface(AppService.class, mContext, mContext.getPackageName()));
         //2.2-2.3 版本 Process terminated by signal (11) 堆栈溢出
         }else**/

        classList = new ArrayList<>();
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.analytics.AnalyticsServiceImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.cache.CacheManagerImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.conf.ConfigServiceImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.crash.CrashServiceImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.download.DownloadManagerImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.location.LocationServiceImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.session.SessionServiceImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.status.StatusServiceImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.upgrade.UpgradeServiceImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.event.ObserverManagerImpl"));
        classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.route.RouteServiceImpl"));

        Log.d(TAG, "classList size=" + classList.size());
        for (int i = 0; i < classList.size(); i++) {
            Log.d(TAG, "classname=" + classList.get(i).getSimpleName());
        }
        initServiceMap(classList);
    }

    private void initServiceMap(List<Class<? extends AppService>> classList) {
        for (final Class<? extends AppService> clazz : classList) {
            registerService(clazz);
        }
    }

    @Override
    public void setDebug(boolean debug) {
        this.mDebug = debug;
    }

    @Override
    public AppService getAppService(String name) {
        AppService appService = null;
        if (mRunServiceMap.containsKey(name)) {
            appService = mRunServiceMap.get(name);
        } else {
            try {
                if (mServiceMap.containsKey(name)) {
                    final Constructor<? extends AppService> c = mServiceMap.get(name).getDeclaredConstructor();
                    c.setAccessible(true);
                    appService = c.newInstance();
                    appService.onCreate(mContext);
                    appService.init(mProperties.get(name) != null ? mProperties.get(name) : appService.defaultServiceProperty());
                    appService.setDebug(mDebug);
                    mRunServiceMap.put(name, appService);
                } else {
                    throw new IllegalStateException("hasn't appService'name is " + name);
                }
            } catch (Exception e) {
                Log.d(e.getMessage());
            }
        }
        return appService;
    }

    @Override
    public void registerService(Class<? extends AppService> clazz) {
        try {
            if (mUseAnnotation) {
                if (clazz.isAnnotationPresent(Service.class)) {
                    final Service service = clazz.getAnnotation(Service.class);
                    mServiceMap.put(service.value(), clazz);
                } else {
                    Log.d(TAG, clazz + " no Service Annotation");
                }
            } else {
                final Method method = clazz.getMethod("getName");
                final Object t = clazz.newInstance();
                final String name = (String) method.invoke(t);
                mServiceMap.put(name, clazz);
            }
        } catch (Exception e) {
            Log.d(e.getMessage());
        }
    }

    /**
     * @param appService
     * @param serviceProperty
     * @deprecated
     */
    @Deprecated
    private void init(AppService appService, ServiceProperty serviceProperty) {
        Field filed = null;
        try {
            filed = appService.getClass().getDeclaredField("mServiceProperty");
            if (filed == null) {
                filed = appService.getClass().getDeclaredField("serviceProperty");
            } else {
                for (final Field filed1 : appService.getClass().getDeclaredFields()) {
                    filed1.setAccessible(true);
                    if (filed1.getType() == ServiceProperty.class) {
                        filed = filed1;
                        break;
                    }
                }
            }
            if (filed != null) {
                filed.setAccessible(true);
                filed.set(appService, serviceProperty);
            }
        } catch (Exception e) {
            Log.d(e.getMessage());
        }
    }

    @Override
    public void destroyService(String name) {
        AppService appService = null;
        if (mRunServiceMap.containsKey(name)) {
            appService = mRunServiceMap.get(name);
            appService.onDestroy();
            mRunServiceMap.remove(name);
        } else {
            Log.d(TAG, name + " Service is not running");
        }
    }

    @Override
    public void destroyAllService() {
        Log.d(TAG, "destroyAllService");
        AppService appService = null;
        for (final String name : mRunServiceMap.keySet()) {
            appService = mRunServiceMap.get(name);
            appService.onDestroy();
        }
        mRunServiceMap.clear();

    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        destroyAllService();
        mProperties.clear();
        mServiceMap.clear();
    }

    @Override
    public void setScanPackage(final String... packageName) {
        if (packageName.length > 0) {
            mContext.post(new Task<List<Class<? extends AppService>>>() {

                @Override
                public List<Class<? extends AppService>> call() {
                    final List<Class<? extends AppService>> classList = new ArrayList<>();
                    for (final String name : packageName) {
                        classList.addAll(ClassUtils.getAllClassByInterface(AppService.class, mContext, name));
                    }
                    return classList;
                }

                @Override
                public void result(List<Class<? extends AppService>> list) {
                    initServiceMap(list);
                }
            });
        }
    }

    /**
     *  @deprecated
     */
    @Deprecated
    public void initServiceProperties() {
        if (Build.VERSION.SDK_INT >= 9) {
            // Temporarily disable logging of disk reads on the Looper thread
            final  StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
            final InputStream is = this.getClass().getResourceAsStream("properties.xml");
            initSource(is);
            StrictMode.setThreadPolicy(oldPolicy);
        } else {
            final  InputStream is = this.getClass().getResourceAsStream("properties.xml");
            initSource(is);
        }
    }

    @Override
    public void initSource(InputStream is) {
        try {
            parser(is);
            is.close();
        } catch (Exception e) {
            Log.d(e.getMessage());
        }
    }

    private void parser(InputStream is) throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final  DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.parse(is);
        final Element root = document.getDocumentElement();
        final  NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node instanceof Element) {
                final Element element = (Element) node;
                final String name = element.getAttribute("name");
                final  NodeList nodeList2 = element.getChildNodes();
                final ServiceProperty properties = new ServiceProperty(name);
                for (int j = 0; j < nodeList2.getLength(); j++) {
                    final Node node2 = nodeList2.item(j);
                    if (node2 instanceof Element) {
                        final  Element element2 = (Element) node2;
                        properties.putString(element2.getAttribute("name"), element2.getTextContent());
                    }
                }
                mProperties.put(name, properties);
            }
        }
    }
}
