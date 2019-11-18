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

package mobi.cangol.mobile.service.cache

import java.io.Serializable

/**
 * Created by xuewu.wei on 2016/3/14.
 */
class CacheObject : Serializable {
    var id: String? = null
    var group: String? = null
    var timestamp: Long = 0
    var period: Long = 0
    var obj: Serializable? = null

    val isExpired: Boolean
        get() = if (period == -1L) {
            false
        } else
            timestamp + period <= System.currentTimeMillis()
    val expired: Long
        get() = if (period == -1L)
            -1
        else
            timestamp + period

    constructor() {}

    constructor(group: String, id: String, obj: Serializable) {
        this.group = group
        this.id = id
        this.obj = obj
        this.timestamp = System.currentTimeMillis()
        this.period = -1
        DiskLruCache
    }

    constructor(group: String, id: String, obj: Serializable, period: Long) {
        this.group = group
        this.id = id
        this.obj = obj
        this.period = period
        this.timestamp = System.currentTimeMillis()
    }

    override fun toString(): String {
        return "CacheObject{" +
                "id='" + id + '\''.toString() +
                ", group='" + group + '\''.toString() +
                ", timestamp=" + timestamp +
                ", period=" + period +
                ", obj=" + obj +
                '}'.toString()
    }

    fun getObject(): Serializable? {
        return obj
    }

    companion object {
        private const val serialVersionUID = 0L
        const val TIME_SEC = 1000
        const val TIME_MIN = 60 * 1000
        const val TIME_HOUR = 60 * 60 * 1000
        const val TIME_DAY = 24 * 60 * 60 * 1000
        const val TIME_WEEK = 7 * 24 * 60 * 60 * 1000
        const val TIME_MONTH = 30 * 24 * 60 * 60 * 1000
    }
}
