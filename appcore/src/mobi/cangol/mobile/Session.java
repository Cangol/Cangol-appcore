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
package mobi.cangol.mobile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Cangol
 * @date 2011-12-18
 */
public class Session {
	private Map<String, Object> map = null;
	private long createTime;

	public Session() {
		map = new ConcurrentHashMap<String, Object>();
	}

	public Session(Map<String, Object> map) {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		createTime = System.currentTimeMillis();
	}

	public long getCreateTime() {
		return createTime;
	}

	public Object get(String key) {
		return map.get(key);
	}

	public String getString(String key) {
		return String.valueOf(map.get(key));
	}

	public int getInt(String key) {
		try {
			String str = String.valueOf(map.get(key));
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public double getDouble(String key) {
		try {
			String str = String.valueOf(map.get(key));
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return 0.0d;
		}
	}

	public boolean getBoolean(String key) {
		try {
			String str = String.valueOf(map.get(key));
			return Boolean.parseBoolean(str);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public long getLong(String key) {
		try {
			String str = String.valueOf(map.get(key));
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	public float getFloat(String key) {
		try {
			String str = String.valueOf(map.get(key));
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Object remove(String key) {
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

	public void putAll(Map<String, ?> map2) {
		this.map.putAll(map2);
	}
}
