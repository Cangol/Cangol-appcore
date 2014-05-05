/** 
 * Copyright (c) 2013 Cangol
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

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.TimeUtils;

final public class IMapBuilder {

	private String mUrl;
	private Map<String, String> mParams;

	private IMapBuilder() {
		mParams = new HashMap<String, String> ();
	}

	public String getUrl() {
		return mUrl;
	}

	public IMapBuilder setUrl(String url) {
		this.mUrl = url;
		return this;
	}

	public IMapBuilder set(String paramName, String paramValue) {
		if (paramName != null)
			mParams.put(paramName, paramValue);
		else {
			Log.w(" StatBuilder.set() called with a null paramName.");
		}
		return this;
	}

	public IMapBuilder setAll(Map<String, String> params) {
		if (params == null) {
			return this;
		}
		mParams.putAll(params);
		return this;
	}

	public String get(String paramName) {
		return mParams.get(paramName);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("url=" + mUrl);
		builder.append("\n");
		for (String key : mParams.keySet()) {
			builder.append(key + "=" + mParams.get(key));
		}
		return builder.toString();
	}
	public  Map<String, String> getParams(){
		return mParams;
	}
	public static IMapBuilder build() {
		return new IMapBuilder();
	}

}
