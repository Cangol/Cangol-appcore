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
package mobi.cangol.mobile.service.status;

import mobi.cangol.mobile.service.AppService;

public interface StatusService extends AppService {
    /**
     * 网络是否连接
     *
     * @return
     */
    boolean isConnection();

    /**
     * Wifi是否连接
     *
     * @return
     */
    boolean isWifiConnection();

    /**
     * gps定位是否开启
     *
     * @return
     */
    boolean isGPSLocation();

    /**
     * network定位是否开启
     *
     * @return
     */
    boolean isNetworkLocation();

    /**
     * 是否在呼叫状态
     *
     * @return
     */
    boolean isCallingState();

    /**
     * 注册状态监听
     */
    void registerStatusListener(StatusListener statusListener);

    /**
     * 移除状态监听
     */
    void unregisterStatusListener(StatusListener statusListener);
}
