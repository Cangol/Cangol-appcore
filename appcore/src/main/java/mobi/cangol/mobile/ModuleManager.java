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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cangol
 */

public class ModuleManager {
    private CoreApplication mCoreApplication;
    private final List<ModuleApplication> mModuleApplications = new ArrayList<>();

    public ModuleManager(CoreApplication coreApplication) {
        this.mCoreApplication=coreApplication;
    }

    protected final void setCoreApplication(CoreApplication coreApplication) {
        this.mCoreApplication = coreApplication;
    }

    public final CoreApplication getApplication() {
        return mCoreApplication;
    }

    public void onCreate() {
        for (ModuleApplication moduleApplication : mModuleApplications) {
            moduleApplication.onCreate();
        }
    }

    public void onTerminate() {
        for (ModuleApplication moduleApplication : mModuleApplications) {
            moduleApplication.onTerminate();
        }
    }

    public void onLowMemory() {
        for (ModuleApplication moduleApplication : mModuleApplications) {
            moduleApplication.onLowMemory();
        }
    }

    public void onTrimMemory(int level) {
        for (ModuleApplication moduleApplication : mModuleApplications) {
            moduleApplication.onTrimMemory(level);
        }
    }

    public void init() {
        for (ModuleApplication moduleApplication : mModuleApplications) {
            moduleApplication.init();
        }
    }

    public void add(ModuleApplication moduleApplication) {
        if (!mModuleApplications.contains(moduleApplication)) {
            mModuleApplications.add(moduleApplication);
            moduleApplication.setCoreApplication(mCoreApplication);
        }
    }

    public void onExit() {
        for (ModuleApplication moduleApplication : mModuleApplications) {
            moduleApplication.onExit();
        }
    }
}
