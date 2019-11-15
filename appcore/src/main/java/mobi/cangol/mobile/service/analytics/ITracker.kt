/**
 * Copyright (c) 2013 Cangol.
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
package mobi.cangol.mobile.service.analytics

import mobi.cangol.mobile.logging.Log

/**
 * @author Cangol
 */
class ITracker(val trackingId: String, private val mHandler: ITrackerHandler) {
    var isClosed: Boolean = false

    fun send(statBuilder: IMapBuilder?): Boolean {
        if (!isClosed) {
            if (statBuilder != null) {
                statBuilder.getUrl()?.let { mHandler.send(this, it, statBuilder.getParams()) }
                return true
            }
            Log.e("statBuilder is null ")
            return false
        } else {
            Log.e("ITracker is closed ")
            return false
        }
    }

    fun send(url: String?, params: Map<String, String>): Boolean {
        if (!isClosed) {
            if (url != null) {
                mHandler.send(this, url, params)
                return true
            }
            Log.e("url is null ")
            return false
        } else {
            Log.e("ITracker is closed ")
            return false
        }
    }
}
