/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.service.plugin;

import android.app.Application;

import java.util.HashMap;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.utils.UrlUtils;

/**
 * Created by xuewu.wei on 2016/4/14.
 */
@Service("PluginManager")
class PluginManagerImpl implements PluginManager {
    private final static String TAG = "PluginManager";
    private Application context = null;
    private boolean debug = false;
    private HashMap<String, PluginInfo> pluginMap = new HashMap<String, PluginInfo>();
    private ServiceProperty serviceProperty = null;

    @Override
    public void onCreate(Application context) {
        this.context = context;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroyAllPlugin");
        PluginInfo pluginInfo = null;
        for (String name : pluginMap.keySet()) {
            pluginInfo = pluginMap.get(name);
            pluginInfo.onDestroy();
        }
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
        PluginInfo pluginInfo = null;
        for (String name : pluginMap.keySet()) {
            pluginInfo = pluginMap.get(name);
            pluginInfo.setDebug(debug);
        }
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.serviceProperty = serviceProperty;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return serviceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        ServiceProperty sp = new ServiceProperty(TAG);
        return sp;
    }

    private PluginInfo initPlugin(String name, String path) {
        PluginInfo pluginInfo = new PluginInfo(name, path);
        pluginInfo.setContext(context);
        pluginInfo.setDebug(debug);
        return pluginInfo;
    }

    @Override
    public void loadPlugin(String name) throws IllegalAccessException {
        if (pluginMap.containsKey(name)) {
            PluginInfo pluginInfo = pluginMap.get(name);
            pluginInfo.launch();
        } else {
            throw new IllegalAccessException("Plugin " + name + " is exist!");
        }
    }

    @Override
    public void addPlugin(String name, String path, int flag) {
        PluginInfo pluginInfo = null;
        if (pluginMap.containsKey(name)) {
            if (flag == 1) {
                if (UrlUtils.isUrl(path)) {
                    pluginInfo = downloadPlugin(path);
                } else {
                    pluginInfo = initPlugin(name, path);
                }
            } else {
                pluginInfo = initPlugin(name, path);
            }
        } else {
            pluginInfo = initPlugin(name, path);
        }
        if (pluginInfo != null) {
            pluginMap.put(name, pluginInfo);
        }
    }

    private PluginInfo downloadPlugin(String url) {
        return null;
    }

    @Override
    public void removePlugin(String name) {
        if (pluginMap.containsKey(name)) {
            PluginInfo pluginInfo = pluginMap.get(name);
            pluginInfo.onDestroy();
            pluginMap.remove(name);
        }
    }

    @Override
    public PluginInfo getPlugin(String name) {
        if (pluginMap.containsKey(name)) {
            return pluginMap.get(name);
        }
        return null;
    }
}
