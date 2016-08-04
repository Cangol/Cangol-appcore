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
    public static final int TIME_HOUR = 60 * 60 * 1000;
    public static final int TIME_DAY = 24 * 60 * 60 * 1000;
    public static final int TIME_WEEK = 7 * 24 * 60 * 60 * 1000;
    public static final int TIME_MONTH = 30 * 24 * 60 * 60 * 1000;
    private String id;
    private String tag;
    private String timestamp;
    private Period period;
    private String deadline;
    private Serializable object;
    public CacheObject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Serializable getObject() {
        return object;
    }

    public void setObject(Serializable object) {
        this.object = object;
    }

    public enum Period {
        HOUR,
        DAY,
        WEEK,
        MONTH,
        ANY
    }

}
