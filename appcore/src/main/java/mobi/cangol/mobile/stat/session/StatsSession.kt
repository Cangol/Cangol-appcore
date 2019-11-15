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

import java.util.Timer
import java.util.TimerTask

object StatsSession {
    private var instance: StatsSession? = null
    private var mTimer: Timer?= null
    private var mQueue: ConnectionQueue?= null
    private var mLastTime: Long = 0
    private var unSentSessionLength: Long = 0
    private var onSessionListener: OnSessionListener? = null

    fun init() {
        mLastTime = System.currentTimeMillis() / 1000
        mQueue = ConnectionQueue(onSessionListener)
        mTimer = Timer()
        mTimer?.schedule(object : TimerTask() {
            override fun run() {
                onTimer()
            }
        }, 30 * 1000L, 30 * 1000L)
        unSentSessionLength = 0
    }

    fun onDestroy() {
        mTimer?.cancel()
    }

    fun setOnSessionListener(onSessionListener: OnSessionListener) {
        this.onSessionListener = onSessionListener
    }

    fun onStart(page: String) {
        mQueue?.beginSession(page)
    }

    fun onStop(page: String) {
        val currTime = System.currentTimeMillis() / 1000
        unSentSessionLength += currTime - mLastTime

        val duration = unSentSessionLength.toInt()
        mQueue?.endSession(page, duration.toLong())
        unSentSessionLength -= duration.toLong()
    }

    private fun onTimer() {
        val currTime = System.currentTimeMillis() / 1000
        unSentSessionLength += currTime - mLastTime
        mLastTime = currTime

        val duration = unSentSessionLength.toInt()
        mQueue?.updateSession(duration.toLong())
        unSentSessionLength -= duration.toLong()
    }

    interface OnSessionListener {
        fun onTick(sessionId: String?, beginSession: String?, sessionDuration: String?, endSession: String?, activityId: String?)
    }

    @JvmStatic fun getInstance(): StatsSession {
        if (instance == null) {
            instance = StatsSession
            instance?.init()
        }
        return instance!!
    }
}

