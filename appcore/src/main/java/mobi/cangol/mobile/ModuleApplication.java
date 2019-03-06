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

import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.route.RouteService;

/**
 * @author Cangol
 */

public abstract class ModuleApplication {
    private CoreApplication mCoreApplication;
    private RouteService mRouteService;

    protected final void setCoreApplication(CoreApplication coreApplication) {
        this.mCoreApplication = coreApplication;
    }

    public final CoreApplication getApplication() {
        return mCoreApplication;
    }

    public void onCreate() {
        mRouteService = (RouteService) getApplication().getAppService(AppService.ROUTE_SERVICE);
    }

    public void init() {
    }

    public void onTerminate() {
    }

    public void onLowMemory() {
    }

    public void onTrimMemory(int level) {
    }

    public void onExit() {
    }

    protected void registerRoute(String path, Class clazz) {
        mRouteService.register(path, clazz);
    }

    protected void registerRoute(Class clazz) {
        mRouteService.register(clazz);
    }
}
