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
package com.cangol.mobile.json;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpResult<T> implements Serializable{
	/**
	 * 
	 */	
	private static final long serialVersionUID = 1L;
	private boolean success;
	private String source;
	private String error;
	private T object;
	private List<T> list;
	public final static  String SUCCESS = "success";
	public final static  String ERROR = "error";
	public final static  String RESULT = "result";
	private HttpResult(){}
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public T getObject() {
		return object;
	}
	public void setObject(T object) {
		this.object = object;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public static <T> HttpResult<T> parserObject(Class<T> c,JSONObject json) {
		HttpResult<T> result = new HttpResult<T>();
		try {
			result.setSource(json.toString());
			result.setSuccess(json.getInt(SUCCESS) == 1 ? true : false);
			if(result.isSuccess()&&c!=null) {
				Object resultObject=json.get(RESULT);
				if(resultObject instanceof JSONObject){	
					result.setObject(JsonUtils.parserToObjectByAnnotation(c, json.getJSONObject(RESULT)));
				}else{
					result.setList(JsonUtils.parserToList(c, json.getJSONArray(RESULT),true));
				}
			}else{
				String error = json.getString(ERROR);
				result.setError(error);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


}
