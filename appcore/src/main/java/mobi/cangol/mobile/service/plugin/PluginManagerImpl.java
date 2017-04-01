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
import android.content.Context;

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
    private HashMap<String, AbstractPlugin> mPluginHashMap = null;

    public PluginManagerImpl(Application context) {
        this.context=context;
    }

    public void registerPlugin(String pluginName, AbstractPlugin plugin) {
        mPluginHashMap.put(pluginName, plugin);
    }

    @Override
    public void onCreate(Application context) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setDebug(boolean debug) {

    }

    @Override
    public void init(ServiceProperty serviceProperty) {

    }

    @Override
    public ServiceProperty getServiceProperty() {
        return null;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        return null;
    }
}
