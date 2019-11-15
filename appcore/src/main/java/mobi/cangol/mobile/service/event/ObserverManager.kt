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

package mobi.cangol.mobile.service.event

import mobi.cangol.mobile.service.AppService

/**
 * Created by xuewu.wei on 2016/8/3.
 */
interface ObserverManager : AppService {
    /**
     * 注册订阅者
     *
     * @param subscriber
     */
    fun register(subscriber: Any)

    /**
     * 解除订阅者
     *
     * @param subscriber
     */
    fun unregister(subscriber: Any)

    /**
     * 触发一个事件
     *
     * @param event 事件名称
     * @param data  事件数据
     */
    fun post(event: String, data: Any)

}
