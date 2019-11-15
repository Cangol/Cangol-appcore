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
package mobi.cangol.mobile.service.status

import mobi.cangol.mobile.service.AppService

interface StatusService : AppService {
    /**
     * 网络是否连接
     *
     * @return
     */
    fun isConnection(): Boolean

    /**
     * Wifi是否连接
     *
     * @return
     */
    fun isWifiConnection(): Boolean

    /**
     * gps定位是否开启
     *
     * @return
     */
    fun isGPSLocation(): Boolean

    /**
     * network定位是否开启
     *
     * @return
     */
    fun isNetworkLocation(): Boolean

    /**
     * 是否在呼叫状态
     *
     * @return
     */
    fun isCallingState(): Boolean

    /**
     * 注册状态监听
     */
    fun registerStatusListener(statusListener: StatusListener)

    /**
     * 移除状态监听
     */
    fun unregisterStatusListener(statusListener: StatusListener)
}
