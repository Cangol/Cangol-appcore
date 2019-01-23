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

package mobi.cangol.mobile.service.cache;

import java.io.Serializable;

/**
 * Created by xuewu.wei on 2016/3/14.
 */
public class CacheObject implements Serializable {
    public static final int TIME_SEC = 1000;
    public static final int TIME_MIN = 60 * 1000;
    public static final int TIME_HOUR = 60 * 60 * 1000;
    public static final int TIME_DAY = 24 * 60 * 60 * 1000;
    public static final int TIME_WEEK = 7 * 24 * 60 * 60 * 1000;
    public static final int TIME_MONTH = 30 * 24 * 60 * 60 * 1000;
    private String id;
    private String group;
    private long timestamp;
    private long period;
    private Serializable object;

    public CacheObject() {
    }

    public CacheObject(String group, String id, Serializable object) {
        this.group = group;
        this.id = id;
        this.object = object;
        this.timestamp = System.currentTimeMillis();
        this.period = -1;
    }

    public CacheObject(String group, String id, Serializable object, long period) {
        this.group = group;
        this.id = id;
        this.object = object;
        this.period = period;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public Serializable getObject() {
        return object;
    }

    public void setObject(Serializable object) {
        this.object = object;
    }

    public boolean isExpired() {
        if (period == -1) {
            return false;
        } else return timestamp + period <= System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "CacheObject{" +
                "id='" + id + '\'' +
                ", group='" + group + '\'' +
                ", timestamp=" + timestamp +
                ", period=" + period +
                ", object=" + object +
                '}';
    }
}
