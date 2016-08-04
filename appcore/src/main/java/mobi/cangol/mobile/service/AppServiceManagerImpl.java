/**
 * Copyright (c) 2013 Cangol
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.ClassUtils;

public class AppServiceManagerImpl extends AppServiceManager {
    private final static String TAG = "AppServiceManager";
    public Application mContext;
    private Map<String, AppService> mRunServiceMap = new Hashtable<String, AppService>();
    private Map<String, Class<? extends AppService>> mServiceMap = new Hashtable<String, Class<? extends AppService>>();
    private boolean mUseAnnotation = true;
    private List<String> mPackageNames = new ArrayList<String>();
    private Map<String, ServiceProperty> mProperties = new HashMap<String, ServiceProperty>();
    private boolean debug = false;
    private AsyncClassScan mAsyncClassScan;

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
        {
            classList = new ArrayList<Class<? extends AppService>>();
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.analytics.AnalyticsServiceImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.cache.CacheManagerImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.conf.ConfigServiceImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.crash.CrashServiceImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.download.DownloadManagerImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.location.LocationServiceImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.session.SessionServiceImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.status.StatusServiceImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.upgrade.UpgradeServiceImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.plugin.PluginManagerImpl"));
            classList.add(ClassUtils.loadClass(mContext, "mobi.cangol.mobile.service.event.ObserverManagerImpl"));
        }
        Log.d(TAG, "classList size=" + classList.size());
        for (int i = 0; i < classList.size(); i++) {
            Log.d(TAG, "classname=" + classList.get(i).getSimpleName());
        }
        //System.gc();
        initServiceMap(classList);
        //initServiceProperties();
    }

    private void initServiceMap(List<Class<? extends AppService>> classList) {
        for (Class<? extends AppService> clazz : classList) {
            registeService(clazz);
        }
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public AppService getAppService(String name) {
        AppService appService = null;
        if (mRunServiceMap.containsKey(name)) {
            appService = mRunServiceMap.get(name);
        } else {
            try {
                if (mServiceMap.containsKey(name)) {
                    Constructor<? extends AppService> c = mServiceMap.get(name).getDeclaredConstructor();
                    c.setAccessible(true);
                    appService = (AppService) c.newInstance();
                    appService.onCreate(mContext);
                    appService.init(mProperties.get(name) != null ? mProperties.get(name) : appService.defaultServiceProperty());
                    appService.setDebug(debug);
                    mRunServiceMap.put(name, appService);
                } else {
                    throw new IllegalStateException("hasn't appService'name is " + name);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return appService;
    }

    @Override
    public void registeService(Class<? extends AppService> clazz) {
        try {
            if (mUseAnnotation) {
                if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    mServiceMap.put(service.value(), clazz);
                } else {
                    if (debug) Log.d(TAG, clazz + " no Service Annotation");
                }
            } else {
                Method method = clazz.getMethod("getName");
                Object t = clazz.newInstance();
                String name = (String) method.invoke(t);
                mServiceMap.put(name, clazz);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * @deprecated
     * @param appService
     * @param serviceProperty
     */
    private void init(AppService appService, ServiceProperty serviceProperty) {
        Field filed = null;
        try {
            filed = appService.getClass().getDeclaredField("mServiceProperty");
            if (filed == null) {
                filed = appService.getClass().getDeclaredField("serviceProperty");
            } else {
                for (Field filed1 : appService.getClass().getDeclaredFields()) {
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
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
            if (debug) Log.d(TAG, name + " Service is not running");
        }
    }

    @Override
    public void destroyAllService() {
        if (debug) Log.d(TAG, "destoryAllService");
        AppService appService = null;
        for (String name : mRunServiceMap.keySet()) {
            appService = mRunServiceMap.get(name);
            appService.onDestroy();
        }
        mRunServiceMap.clear();

    }

    @Override
    public void destroy() {
        if (debug) Log.d(TAG, "destroy");
        if (mAsyncClassScan != null) mAsyncClassScan.cancel(true);
        destroyAllService();
        mProperties.clear();
        mServiceMap.clear();
        mPackageNames.clear();
    }

    @Override
    public void setScanPackage(String... packageName) {
        if (packageName.length > 0) {
            List<Class<? extends AppService>> classList = new ArrayList<Class<? extends AppService>>();
            for (String name : packageName) {
                mPackageNames.add(name);
                classList.addAll(ClassUtils.getAllClassByInterface(AppService.class, mContext, name));
                // 2.2-2.3 版本 Process terminated by signal (11) 堆栈溢出
                //System.gc();
            }
            initServiceMap(classList);
        }
    }

    @Deprecated
    public void initServiceProperties() {
        if (Build.VERSION.SDK_INT >= 9) {
            // Temporarily disable logging of disk reads on the Looper thread
            StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
            InputStream is = this.getClass().getResourceAsStream("properties.xml");
            initSource(is);
            StrictMode.setThreadPolicy(oldPolicy);
        } else {
            InputStream is = this.getClass().getResourceAsStream("properties.xml");
            initSource(is);
        }
    }

    @Override
    public void initSource(InputStream is) {
        try {
            parser(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parser(InputStream is) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);
        Element root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                String name = element.getAttribute("name");
                NodeList nodeList2 = element.getChildNodes();
                ServiceProperty properties = new ServiceProperty(name);
                for (int j = 0; j < nodeList2.getLength(); j++) {
                    Node node2 = nodeList2.item(j);
                    if (node2 instanceof Element) {
                        Element element2 = (Element) node2;
                        properties.putString(element2.getAttribute("name"), element2.getTextContent());
                    }
                }
                mProperties.put(name, properties);
            }
        }
    }

    static class AsyncClassScan extends AsyncTask<String, Void, List<Class<? extends AppService>>> {
        Context context;

        AsyncClassScan(Context context) {
            this.context = context;
        }

        @Override
        protected List<Class<? extends AppService>> doInBackground(String... params) {
            return ClassUtils.getAllClassByInterface(AppService.class, context, params[0]);
        }

    }

}
