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
package mobi.cangol.mobile.service.global;

import mobi.cangol.mobile.service.AppService;

/**
 * 适合小型数据缓存
 * @author xuewu.wei
 *
 */
public interface GlobalData extends AppService{
	
	/**
	 * value 的class类型,仅限float,int,boolean,long,String,Set<String>(SDK>=11),JSONObject,JSONArray,实现了Serializable
	 * 本地化存储
	 * @param name
	 * @param value
	 */
	void save(String name,Object value);
	
	/**
	 * 内存缓存
	 * @param name
	 * @param value
	 */
	void put(String name,Object value);
	
	/**
	 * 获取本地缓存和内存缓存
	 * @param name
	 * @return
	 */
	Object get(String name);
	
	/**
	 * 刷新本地缓存和内存缓存
	 * @param name
	 * @return
	 */
	void refresh();
	
	/**
	 * 清除内存缓存
	 * @return
	 */
	void clear();
	
}
