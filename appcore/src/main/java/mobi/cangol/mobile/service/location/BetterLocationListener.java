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

public interface BetterLocationListener {
    /**
     * 需要权限
     */
    void needPermission(String[] permissions);

    /**
     * provider 无效
     * @param provider
     */
    void providerDisabled(String provider);
    /**
     * 获取最新有效地位置
     *
     * @param location
     */
    void onBetterLocation(Location location);

    /**
     * 更新位置超时,返回最后的有效位置
     *
     * @param location
     */
    void timeout(Location location);

    /**
     * 定位中
     */
    void positioning();
}
