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

import java.io.InputStream;

public abstract class AppServiceManager {

    /**
     * 获取一个应用服务
     *
     * @param name
     * @return
     */
    public abstract AppService getAppService(String name);

    /**
     * 销毁一个应用服务
     *
     */
    public abstract void destroyService(String name);

    /**
     * 注册一个自定义AppService
     * @param clazz
     */
    public abstract void registeService(Class<? extends AppService> clazz);

    /**
     * 销毁所有服务
     */
    public abstract void destroyAllService();

    /**
     * 销毁
     */
    public abstract void destroy();

    /**
     * 设置服务扫描包
     *
     * @param packageName
     * @deprecated
     */
    public abstract void setScanPackage(String... packageName);

    /**
     * 设置初始化源
     *
     * @param is
     */
    public abstract void initSource(InputStream is);

    /**
     * 设置debug
     *
     * @param debug
     */
    public abstract void setDebug(boolean debug);
}
