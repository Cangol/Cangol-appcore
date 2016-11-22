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
package mobi.cangol.mobile.service.location;

import android.location.Location;

import mobi.cangol.mobile.service.AppService;

public interface LocationService extends AppService {
    /**
     * 最佳定位时间间隔,单位豪秒
     */
    String LOCATIONSERVICE_BETTERTIME = "better_time";
    /**
     * 定位超市时间,单位豪秒
     */
    String LOCATIONSERVICE_TIMEOUT = "timeout";
    /**
     * 百度LBS 的apikey
     */
    String LOCATIONSERVICE_BAIDU_AK = "baidu_ak";
    /**
     * GPS定位 最小时间间隔,单位豪秒
     */
    String LOCATIONSERVICE_GPS_MINTIME = "gps_min_time";
    /**
     * GPS定位 最小距离间隔,单位米
     */
    String LOCATIONSERVICE_GPS_MINDISTANCE = "gps_min_distance";
    /**
     * 网络定位 最小时间间隔,单位豪秒
     */
    String LOCATIONSERVICE_NETWORK_MINTIME = "network_min_time";
    /**
     * 网络定位 最小位置间,单位米
     */
    String LOCATIONSERVICE_NETWORK_MINDISTANCE = "network_min_distance";

    /**
     * 请求位置更新
     */
    void requestLocationUpdates();

    /**
     * 停止位置更新
     */
    void removeLocationUpdates();

    /**
     * 获取最后记录的位置
     *
     * @return
     */
    Location getLastKnownLocation();

    /**
     * 判断位置是否是有效地（据当前时间差小于最佳时间better_time）
     *
     * @param mLocation
     * @return
     */
    boolean isBetterLocation(Location mLocation);

    /**
     * 设置位置更新监听接口
     *
     * @param locationListener
     */
    void setBetterLocationListener(BetterLocationListener locationListener);

    /**
     * 获取地址
     *
     * @return
     */
    String getAddress();
}
