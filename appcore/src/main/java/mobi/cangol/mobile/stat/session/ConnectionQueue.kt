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
import mobi.cangol.mobile.utils.StringUtils
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by weixuewu on 16/1/23.
 */
internal class ConnectionQueue(private val mSessionListener: StatsSession.OnSessionListener?) {
    private val queue = ConcurrentLinkedQueue<SessionEntity>()
    private val entices = HashMap<String, SessionEntity>()

    private var mThread: Thread? = null

    fun beginSession(page: String) {
        val data = SessionEntity()
        data.sessionId = StringUtils.md5(page.hashCode().toString())
        data.beginSession = 1//currTime
        data.endSession = 0
        data.activityId = page

        entices[page] = data

        queue.offer(data)

        tick()

    }

    fun updateSession(duration: Long) {
        var data: SessionEntity? = null
        try {
            val itr = entices.keys.iterator()
            while (itr.hasNext()) {
                val page = itr.next()
                data = entices[page]!!.clone() as SessionEntity
                data.beginSession = 0
                data.sessionDuration = duration
                data.endSession = 0
                queue.offer(data)
                tick()
            }
        } catch (e: Exception) {
            Log.e(e.message)
        }

    }

    fun endSession(page: String, duration: Long) {
        var data: SessionEntity? = null
        try {
            if (entices.containsKey(page)) {
                data = entices[page]!!.clone() as SessionEntity
                data.beginSession = 0
                data.sessionDuration = duration
                data.endSession = 1//currTime
                queue.offer(data)
            }
            tick()
        } catch (e: Exception) {
            Log.e(e.message)
        }

    }


    private fun tick() {
        if (mThread != null && mThread!!.isAlive) {
            return
        }
        if (queue.isEmpty()) {
            return
        }

        mThread = object : Thread() {
            override fun run() {
                while (true) {
                    val data = queue.peek() ?: break

                    try {
                        //提交
                        mSessionListener?.onTick(
                                data.sessionId,
                                data.beginSession.toString(),
                                data.sessionDuration.toString(),
                                data.endSession.toString(),
                                data.activityId)
                        queue.poll()
                    } catch (e: Exception) {
                        Log.e("StatAgent", e.toString())
                        break
                    }

                }
            }
        }
        mThread!!.start()
    }
}
