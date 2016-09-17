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
package mobi.cangol.mobile.service.analytics;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.AsyncHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;

/**
 * @author Cangol
 */
@Service("AnalyticsService")
class AnalyticsServiceImpl extends ITrackerHandler implements AnalyticsService {
    private final static String TAG = "AnalyticsService";
    private boolean mDebug = false;
    private Application mContext = null;
    private AsyncHttpClient mAsyncHttpClient = null;
    private ServiceProperty mServiceProperty = null;
    private Map<String, ITracker> mTrackers = new HashMap<String, ITracker>();

    @Override
    public void onCreate(Application context) {
        mContext = context;
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
        PoolManager.buildPool(mServiceProperty.getString(ANALYTICSSERVICE_THREADPOOL_NAME), mServiceProperty.getInt(ANALYTICSSERVICE_THREAD_MAX));
        mAsyncHttpClient = AsyncHttpClient.build(mServiceProperty.getString(ANALYTICSSERVICE_THREADPOOL_NAME));
    }

    @Override
    public String getName() {
        return "AnalyticsService";
    }

    @Override
    public void onDestroy() {
        mAsyncHttpClient.cancelRequests(mContext, true);
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        ServiceProperty sp = new ServiceProperty(TAG);
        sp.putString(ANALYTICSSERVICE_THREADPOOL_NAME, TAG);
        sp.putInt(ANALYTICSSERVICE_THREAD_MAX, 2);
        return sp;
    }

    @Override
    public void send(final ITracker iTracker, String url, Map<String, String> paramsMap) {
        RequestParams params = new RequestParams(paramsMap);
        if (mDebug) Log.v(TAG, "send " + url);
        if (mDebug) Log.v(TAG, "params: \n" + params.toDebugString());
        mAsyncHttpClient.get(mContext, url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                if (mDebug) Log.d(TAG, iTracker.getTrackingId() + " send Success :" + content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (mDebug) Log.d(TAG, iTracker.getTrackingId() + " send Failure :" + content);
            }

        });
    }

    @Override
    public void setDebug(boolean debug) {
        this.mDebug = debug;
    }

    @Override
    public ITracker getTracker(String trackingId) {
        if (mTrackers.containsKey(trackingId)) {
            return mTrackers.get(trackingId);
        } else {
            ITracker tracker = new ITracker(trackingId, this);
            mTrackers.put(trackingId, tracker);
            return tracker;
        }
    }

    @Override
    public void closeTracker(String trackingId) {
        ITracker tracker = mTrackers.get(trackingId);
        if (tracker != null) {
            mTrackers.remove(trackingId);
            tracker.setClosed(true);
        }
    }
}
