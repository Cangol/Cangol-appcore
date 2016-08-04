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

import mobi.cangol.mobile.plugin.PluginInfo;
import mobi.cangol.mobile.service.AppService;

/**
 * @author Cangol
 */
public interface PluginManager extends AppService {

    /**
     * @param name
     */
    void loadPlugin(String name) throws IllegalAccessException;

    /**
     * @param name
     * @param path
     */
    void addPlugin(String name, String path, int flag);

    /**
     * @param name
     */
    void removePlugin(String name);

    /**
     * @param name
     * @return
     */
    PluginInfo getPlugin(String name);

}
