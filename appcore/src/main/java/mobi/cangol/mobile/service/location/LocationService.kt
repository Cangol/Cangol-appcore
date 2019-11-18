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

import android.app.Activity
import android.location.Location

import mobi.cangol.mobile.service.AppService

interface LocationService : AppService {

    /**
     * 获取最后记录的位置
     *
     * @return
     */
    fun getLastKnownLocation(): Location?

    /**
     * 请求位置更新
     */
    fun requestLocationUpdates(activity: Activity)

    /**
     * 停止位置更新
     */
    fun removeLocationUpdates()

    /**
     * 判断位置是否是有效地（据当前时间差小于最佳时间better_time）
     *
     * @param mLocation
     * @return
     */
    fun isBetterLocation(mLocation: Location): Boolean

    /**
     * 设置位置更新监听接口
     *
     * @param locationListener
     */
    fun setBetterLocationListener(locationListener: BetterLocationListener)

    companion object {
        /**
         * 最佳定位时间间隔,单位豪秒
         */
        const val LOCATIONSERVICE_BETTERTIME = "better_time"
        /**
         * 定位超市时间,单位豪秒
         */
        const val LOCATIONSERVICE_TIMEOUT = "timeout"
        /**
         * GPS定位 最小时间间隔,单位豪秒
         */
        const val LOCATIONSERVICE_GPS_MINTIME = "gps_min_time"
        /**
         * GPS定位 最小距离间隔,单位米
         */
        const val LOCATIONSERVICE_GPS_MINDISTANCE = "gps_min_distance"
        /**
         * 网络定位 最小时间间隔,单位豪秒
         */
        const val LOCATIONSERVICE_NETWORK_MINTIME = "network_min_time"
        /**
         * 网络定位 最小位置间,单位米
         */
        const val LOCATIONSERVICE_NETWORK_MINDISTANCE = "network_min_distance"
    }
}
