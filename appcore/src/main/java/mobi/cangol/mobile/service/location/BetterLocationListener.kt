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
package mobi.cangol.mobile.service.location

import android.location.Location

interface BetterLocationListener {
    /**
     * 需要权限
     */
    fun needPermission(permissions: Array<String?>)

    /**
     * provider 无效
     * @param provider
     */
    fun providerDisabled(provider: String)

    /**
     * 获取最新有效地位置
     *
     * @param location
     */
    fun onBetterLocation(location: Location)

    /**
     * 更新位置超时,返回最后的有效位置
     *
     * @param location
     */
    fun timeout(location: Location)

    /**
     * 定位中
     */
    fun positioning()
}
