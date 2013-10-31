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
package mobi.cangol.mobile.service.conf;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description Serv.java 
 * @author Cangol
 * @date 2013-10-31
 */
public class ServiceConfig {
	private String name;
	private Map<String,String> properties;
	
	public ServiceConfig(String name){
		this.name=name;
		properties=new HashMap<String,String>();
	}
	public void put(String key,String value){
		properties.put(key, value);
	}
	public String getName() {
		return name;
	}
	public  String getString(String key) {
	  return properties.get(key);
	}
	public  int getInt(String key) {
		try {
			String str=properties.get(key);
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public  double getDouble(String key) {
		try {
			String str=properties.get(key);
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return 0.0d;
		}
	}

	public  boolean getBoolean(String key) {
		try {
			String str=properties.get(key);
			return Boolean.parseBoolean(str);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public  long getLong(String key) {
		try {
			String str=properties.get(key);
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			return 0L;
		}
	}
	
	public  float getFloat(String key) {
		try {
			String str=properties.get(key);
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			return 0f;
		}
	}
}
