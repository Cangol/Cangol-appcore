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
package mobi.cangol.mobile.service.download;

import mobi.cangol.mobile.service.AppService;

public interface DownloadManager extends AppService {
    /**
     * 并发线程数
     */
    public final static String DOWNLOADSERVICE_THREAD_MAX = "thread_max";
    /**
     * 线程池名称
     */
    public final static String DOWNLOADSERVICE_THREADPOOL_NAME = "threadpool_name";

    /**
     * 获取一个下载执行器
     *
     * @param name
     * @return
     */
    DownloadExecutor<?> getDownloadExecutor(String name);

    /**
     * 注册一个下载执行器
     *
     * @param name
     * @param clazz
     * @param max   同时之心的下载数
     */
    void registerExecutor(String name, Class<? extends DownloadExecutor<?>> clazz, int max);

    /**
     * 恢复所有下载执行器
     */
    void recoverAllAllDownloadExecutor();

    /**
     * 中断所有下载执行器
     */
    void interruptAllDownloadExecutor();
}
