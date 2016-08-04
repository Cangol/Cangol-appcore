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
package mobi.cangol.mobile.stat.session;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.StringUtils;

/**
 * Created by weixuewu on 16/1/23.
 */
class ConnectionQueue {
    public static final String TAG = "ConnectionQueue";
    private ConcurrentLinkedQueue<SessionEntity> queue_ = new ConcurrentLinkedQueue<SessionEntity>();
    private Hashtable<String, SessionEntity> entitys = new Hashtable<String, SessionEntity>();

    private Thread mThread = null;
    private StatsSession.OnSessionListener mSessionListener;

    public ConnectionQueue(StatsSession.OnSessionListener onSessionListener) {
        mSessionListener = onSessionListener;
    }

    public void beginSession(String page) {
        //long currTime = System.currentTimeMillis() / 1000;
        SessionEntity data = new SessionEntity();
        data.sessionId = StringUtils.md5(String.valueOf(page.hashCode()));
        data.beginSession = 1;//currTime
        data.endSession = 0;
        data.activityId = page;

        entitys.put(page, data);

        queue_.offer(data);

        tick();

    }

    public void updateSession(long duration) {
        SessionEntity data = null;
        try {
            for (Iterator<String> itr = entitys.keySet().iterator(); itr.hasNext(); ) {
                String page = (String) itr.next();
                data = (SessionEntity) entitys.get(page).clone();
                data.beginSession = 0;
                data.sessionDuration = duration;
                data.endSession = 0;
                queue_.offer(data);
                tick();
            }
        } catch (CloneNotSupportedException e) {
            Log.e(e.getMessage());
        }
    }

    public void endSession(String page, long duration) {
        //long currTime = System.currentTimeMillis() / 1000;
        SessionEntity data = null;
        try {
            if (entitys.containsKey(page)) {
                data = (SessionEntity) entitys.get(page).clone();
                data.beginSession = 0;
                data.sessionDuration = duration;
                data.endSession = 1;//currTime
                queue_.offer(data);
            }
            tick();
        } catch (CloneNotSupportedException e) {
            Log.e(e.getMessage());
        }
    }


    private void tick() {
        if (mThread != null && mThread.isAlive()){
            return;
        }
        if (queue_.isEmpty()){
            return;
        }

        mThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    SessionEntity data = queue_.peek();

                    if (data == null){
                        break;
                    }
                    try {
                        //提交
                        if (mSessionListener != null){
                            mSessionListener.onTick(
                                    data.sessionId,
                                    String.valueOf(data.beginSession),
                                    String.valueOf(data.sessionDuration),
                                    String.valueOf(data.endSession),
                                    data.activityId);
                        }
                        queue_.poll();
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                        break;
                    }
                }
            }
        };
        mThread.start();
    }
}
