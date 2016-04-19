/** 
 * Copyright (c) 2013 Cangol.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.analytics;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cangol
 */
public class ITracker {
	private final String mTrackingId;
	private final Map<String, String> mParams = new HashMap<String, String>();
	private long mStartTime;
	private ITrackerHandler mHandler;

	public ITracker(String trackingId, ITrackerHandler handler) {
		super();
		this.mTrackingId = trackingId;
		this.mHandler = handler;
	}

	public void sendTiming(String url) {
		mHandler.send(url, mParams);
		mStartTime = 0;
		mParams.clear();
	}

	public void send(IMapBuilder statBuilder) {
		if (statBuilder != null) {
			mHandler.send(statBuilder.getUrl(), statBuilder.getParams());
		}
	}

	public void send(String url, Map<String, String> params) {
		if (params != null) {
			mHandler.send(url, params);
		}
	}

	public String get(String key) {
		return mParams.get(key);
	}

	public void set(String key, String value) {
		mParams.put(key, value);
	}

	public void setAll(Map<String, String> params) {
		if (params != null) {
			mParams.putAll(params);
		}
	}

	public String getTrackingId() {
		return mTrackingId;
	}

	public long start() {
		mStartTime = System.currentTimeMillis();
		return mStartTime;
	}

	public long end() {
		return System.currentTimeMillis() - mStartTime;
	}
}
