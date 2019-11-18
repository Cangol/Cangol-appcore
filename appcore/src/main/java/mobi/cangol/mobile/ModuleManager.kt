/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile

import java.util.*

/**
 * @author Cangol
 */

class ModuleManager {
    private var mCoreApplication: CoreApplication? = null
    private val mModuleApplications = ArrayList<ModuleApplication>()

    fun setApplication(coreApplication: CoreApplication) {
        this.mCoreApplication = coreApplication
    }

    fun addModule(moduleApplication: ModuleApplication) {
        if (!mModuleApplications.contains(moduleApplication)) {
            mModuleApplications.add(moduleApplication)
        }
    }

    fun removeModule(moduleApplication: ModuleApplication) {
        if (mModuleApplications.contains(moduleApplication)) {
            mModuleApplications.remove(moduleApplication)
        }
    }

    fun clear() {
        mModuleApplications.clear()
    }

    fun init() {
        for (moduleApplication in mModuleApplications) {
            moduleApplication.init()
        }
    }

    fun onCreate() {
        for (moduleApplication in mModuleApplications) {
            moduleApplication.setCoreApplication(mCoreApplication!!)
            moduleApplication.onCreate()
        }
    }

    fun onTerminate() {
        for (moduleApplication in mModuleApplications) {
            moduleApplication.onTerminate()
        }
    }

    fun onLowMemory() {
        for (moduleApplication in mModuleApplications) {
            moduleApplication.onLowMemory()
        }
    }

    fun onTrimMemory(level: Int) {
        for (moduleApplication in mModuleApplications) {
            moduleApplication.onTrimMemory(level)
        }
    }

    fun onExit() {
        for (moduleApplication in mModuleApplications) {
            moduleApplication.onExit()
        }
    }
}
