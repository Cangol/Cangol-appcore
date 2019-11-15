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
package mobi.cangol.mobile.stat.traffic

import mobi.cangol.mobile.db.DatabaseField
import mobi.cangol.mobile.db.DatabaseTable

/**
 * Created by weixuewu on 16/1/23.
 */

@DatabaseTable("DATE_TRAFFIC")
class DateTraffic {
    @DatabaseField(primaryKey = true, notNull = true, value = "id")
    var id: Int = 0
    @DatabaseField(value = "status", notNull = true)
    var status: Int = 0
    @DatabaseField(value = "date", notNull = true)
    var date: String? = null
    @DatabaseField(value = "uid", notNull = true)
    var uid: Int = 0
    @DatabaseField(value = "total_rx", notNull = true)
    var totalRx: Long = 0
    @DatabaseField(value = "total_tx", notNull = true)
    var totalTx: Long = 0
    @DatabaseField(value = "mobile_rx", notNull = true)
    var mobileRx: Long = 0
    @DatabaseField(value = "mobile_tx", notNull = true)
    var mobileTx: Long = 0
    @DatabaseField(value = "wifi_rx", notNull = true)
    var wifiRx: Long = 0
    @DatabaseField(value = "wifi_tx", notNull = true)
    var wifiTx: Long = 0

    override fun toString(): String {
        return "DateTraffic{" +
                "id=" + id +
                ", status=" + status +
                ", date='" + date + '\''.toString() +
                ", uid=" + uid +
                ", totalRx=" + totalRx +
                ", totalTx=" + totalTx +
                ", mobileRx=" + mobileRx +
                ", mobileTx=" + mobileTx +
                ", wifiRx=" + wifiRx +
                ", wifiTx=" + wifiTx +
                '}'.toString()
    }
}