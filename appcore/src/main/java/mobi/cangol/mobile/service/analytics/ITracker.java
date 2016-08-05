/**
 * Copyright (c) 2013 Cangol.
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
package mobi.cangol.mobile.service.analytics;

import java.util.Map;

import mobi.cangol.mobile.logging.Log;

/**
 * @author Cangol
 */
public class ITracker {
    private final String mTrackingId;
    private ITrackerHandler mHandler;
    private boolean mClosed;

    public ITracker(String trackingId, ITrackerHandler handler) {
        super();
        this.mTrackingId = trackingId;
        this.mHandler = handler;
    }

    public boolean send(IMapBuilder statBuilder) {
        if (!mClosed) {
            if (statBuilder != null) {
                mHandler.send(this, statBuilder.getUrl(), statBuilder.getParams());
                return true;
            }
            Log.e("statBuilder is null ");
            return false;
        } else {
            Log.e("ITracker is closed ");
            return false;
        }
    }

    public boolean send(String url, Map<String, String> params) {
        if (!mClosed) {
            if (url != null) {
                mHandler.send(this, url, params);
                return true;
            }
            Log.e("url is null ");
            return false;
        } else {
            Log.e("ITracker is closed ");
            return false;
        }
    }

    public String getTrackingId() {
        return mTrackingId;
    }

    public boolean isClosed() {
        return mClosed;
    }

    protected void setClosed(boolean mClosed) {
        this.mClosed = mClosed;
    }
}
