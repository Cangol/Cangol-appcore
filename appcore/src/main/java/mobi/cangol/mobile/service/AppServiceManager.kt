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
package mobi.cangol.mobile.service

import java.io.InputStream

abstract class AppServiceManager {

    /**
     * 获取一个应用服务
     *
     * @param name
     * @return
     */
    abstract fun getAppService(name: String): AppService?

    /**
     * 销毁一个应用服务
     */
    abstract fun destroyService(name: String)

    /**
     * 注册一个自定义AppService
     *
     * @param clazz
     */
    abstract fun registerService(clazz: Class<out AppService>)

    /**
     * 销毁所有服务
     */
    abstract fun destroyAllService()

    /**
     * 销毁
     */
    abstract fun destroy()

    /**
     * 设置服务扫描包
     *
     * @param packageName
     */
    abstract fun setScanPackage(vararg packageName: String)

    /**
     * 设置初始化源
     *
     * @param is
     */
    abstract fun initSource(`is`: InputStream)

    /**
     * 设置debug
     *
     * @param mDebug
     */
    abstract fun setDebug(mDebug: Boolean)
}
