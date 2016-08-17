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
package mobi.cangol.mobile.stat.session;

import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

public class StatsSession {
    public static final String TAG = "StatsSession";
    private static StatsSession instance;
    private Timer mTimer;
    private ConnectionQueue mQueue;
    private long mLastTime;
    private long unSentSessionLength = 0;
    private OnSessionListener onSessionListener;
    private Context mContext;
    private StatsSession(Context context) {
        this.mContext=context;
        mLastTime = System.currentTimeMillis() / 1000;
        mQueue = new ConnectionQueue(onSessionListener);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimer();
            }
        }, 30 * 1000, 30 * 1000);
        unSentSessionLength = 0;
    }

    static public StatsSession getInstance(Context context) {
        if (instance == null) {
            instance = new StatsSession(context);
        }
        return instance;
    }

    public void onDestroy() {
        mTimer.cancel();
    }

    public void setOnSessionListener(OnSessionListener onSessionListener) {
        this.onSessionListener = onSessionListener;
    }

    public void onStart(String page) {
        mQueue.beginSession(page);
    }

    public void onStop(String page) {
        long currTime = System.currentTimeMillis() / 1000;
        unSentSessionLength += currTime - mLastTime;

        int duration = (int) unSentSessionLength;
        mQueue.endSession(page, duration);
        unSentSessionLength -= duration;
    }

    private void onTimer() {
        long currTime = System.currentTimeMillis() / 1000;
        unSentSessionLength += currTime - mLastTime;
        mLastTime = currTime;

        int duration = (int) unSentSessionLength;
        mQueue.updateSession(duration);
        unSentSessionLength -= duration;
    }

    public interface OnSessionListener {
        void onTick(String sessionId
                , String beginSession
                , String sessionDuration
                , String endSession
                , String activityId);
    }
}

