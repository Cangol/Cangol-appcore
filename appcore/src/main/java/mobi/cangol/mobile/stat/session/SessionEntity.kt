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
package mobi.cangol.mobile.stat.session

import mobi.cangol.mobile.logging.Log

/**
 * Created by weixuewu on 16/1/23.
 */
internal class SessionEntity : Cloneable {
    var sessionId: String? = null
    var beginSession: Long = 0
    var sessionDuration: Long = 0
    var endSession: Long = 0
    var activityId: String? = null

    public override fun clone(): Any {
        var o: Any? = null
        try {
            o = super.clone()
        } catch (e: CloneNotSupportedException) {
            Log.e(e.message)
        }

        return o!!
    }

}
