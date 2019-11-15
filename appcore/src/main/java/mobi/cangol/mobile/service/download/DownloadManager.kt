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
package mobi.cangol.mobile.service.download

import mobi.cangol.mobile.service.AppService

interface DownloadManager : AppService {

    /**
     * 获取一个下载执行器
     *
     * @param name
     * @return
     */
    fun getDownloadExecutor(name: String): DownloadExecutor<*>?

    /**
     * 注册一个下载执行器
     *
     * @param name
     * @param clazz
     * @param max   同时下载数
     */
    fun registerExecutor(name: String, clazz: Class<out DownloadExecutor<*>>, max: Int)

    /**
     * 恢复所有下载执行器
     */
    fun recoverAllAllDownloadExecutor()

    /**
     * 中断所有下载执行器
     */
    fun interruptAllDownloadExecutor()

    companion object {
        /**
         * 并发线程数
         */
        const val DOWNLOADSERVICE_THREAD_MAX = "thread_max"
        /**
         * 线程池名称
         */
        const val DOWNLOADSERVICE_THREADPOOL_NAME = "threadpool_name"
    }
}
