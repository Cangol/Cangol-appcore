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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description: Customer VO annotation
 * @version $Revision: 1.0 $ 
 * @author xuewu.wei
 * @date: 2012-6-5
 */
public class JsonUtils {
	private final static boolean debug=false;
	public final static  String TAG = "JsonUtils";
	/**
	 * 转换Object到JSONObject
	 * @param obj
	 * @return
	 * @throws JSONException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static JSONObject toJSONObject(Object obj) throws JSONException, IllegalArgumentException, IllegalAccessException{
		JSONObject json=new JSONObject();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (!field.getType().isAssignableFrom(List.class)){
				//非集合类型
				if (isBaseClass(field.getType())) {
					json.put(field.getName(), field.get(obj));
				}else{
					json.put(field.getName(), toJSONObject(field.get(obj)));
				}
			}else{
				//集合类型
				if(field.getGenericType() instanceof ParameterizedType){
					List list=(List) field.get(obj);
					JSONArray jsonArray=new JSONArray();
					if(list!=null){
						for (int i = 0; i < list.size(); i++) {	
							if (isBaseClass(list.get(i).getClass())) {
								jsonArray.put(list.get(i));
							}else{
								jsonArray.put(toJSONObject(list.get(i)));
							}
						}
					}
					json.put(field.getName(), jsonArray);
				}else{
					throw new IllegalStateException(field.getName()+ " require have generic");
				}
			}
			
		}
		return json;
	}
	/**
	 * 转换Object到JSONObject 依赖注解（无注解不转换）
	 * @param obj
	 * @return
	 * @throws JSONException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static JSONObject toJSONObjectByAnnotation(Object obj) throws JSONException, IllegalArgumentException, IllegalAccessException{
		JSONObject json=new JSONObject();
		Field[] fields = obj.getClass().getDeclaredFields();
		String filedName=null;
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Element.class)){
				//非集合类型
				Element element = field.getAnnotation(Element.class);
				filedName="".equals(element.value())?field.getName():element.value();
				if (isBaseClass(field.getType())) {
					json.put(filedName, field.get(obj));
				}else{
					json.put(filedName, toJSONObjectByAnnotation(field.get(obj)));
				}
			}else if (field.isAnnotationPresent(ElementList.class)) {
				//集合类型
				ElementList elementList = field.getAnnotation(ElementList.class);
				filedName="".equals(elementList.value())?field.getName():elementList.value();
				if(field.getGenericType() instanceof ParameterizedType){
					List list=(List) field.get(obj);
					JSONArray jsonArray=new JSONArray();
					if(list!=null){
						for (int i = 0; i < list.size(); i++) {	
							if (isBaseClass(list.get(i).getClass())) {
								jsonArray.put(list.get(i));
							}else{
								jsonArray.put(toJSONObjectByAnnotation(list.get(i)));
							}
						}
					}
					json.put(filedName, jsonArray);
				}else{
					throw new IllegalStateException(field.getName()+ " require have generic");
				}
			}
		}
		return json;
	}
	/**
	 * 解析JSON格式字符串到  Object依赖注解（无注解不解析）
	 * @param c
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static <T> T parserToObjectByAnnotation(Class<T> c,String str) throws Exception{
		if(null==str||"".equals(str)){
			throw new IllegalArgumentException("str=null");
		}
		String json=formatJson(str);
		JSONObject jsonObject=new JSONObject(json);
		return parserToObjectByAnnotation(c,jsonObject);
	}
	/**
	 * 解析JSON格式字符串到 Object
	 * @param c
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static <T> T parserToObject(Class<T> c,String str) throws Exception{
		if(null==str||"".equals(str)){
			throw new IllegalArgumentException("str=null");
		}
		String json=formatJson(str);
		JSONObject jsonObject=new JSONObject(json);
		return parserToObject(c,jsonObject);
	}
	
	public static <T> T parserToObjectByAnnotation(Class<T> c,JSONObject jsonObject) throws Exception{
		T t  = c.newInstance();
		Field[] fields = c.getDeclaredFields();
		String filedName=null;
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Element.class)) {
				Element element = field.getAnnotation(Element.class);
				filedName="".equals(element.value())?field.getName():element.value();
				setField(t,field,filedName,jsonObject,true);
			}else if (field.isAnnotationPresent(ElementList.class)) {
				ElementList elementList = field.getAnnotation(ElementList.class);
				filedName="".equals(elementList.value())?field.getName():elementList.value();
				if(field.getGenericType() instanceof ParameterizedType){
					ParameterizedType pt = (ParameterizedType) field.getGenericType();  
					Class genericClazz = (Class)pt.getActualTypeArguments()[0];
					List list=parserToList(genericClazz, getJSONArray(jsonObject, filedName),true);
					field.set(t,list);
				}else{
					throw new IllegalStateException(field.getName()+ " require have generic");
				}
			}
		}
		return t;
		
	}
	
	public static <T> T parserToObject(Class<T> c,JSONObject jsonObject) throws Exception{
		if(jsonObject==null)return null;
		T t  = c.newInstance();
		Field[] fields = c.getDeclaredFields();
		String filedName=null;
		for (Field field : fields) {
			field.setAccessible(true);
			if (!List.class.isAssignableFrom(field.getType())) {
				filedName=field.getName();
				setField(t,field,filedName,jsonObject,false);
			}else{
				filedName=field.getName();
				if(field.getGenericType() instanceof ParameterizedType){
					ParameterizedType pt = (ParameterizedType) field.getGenericType();  
					Class genericClazz = (Class)pt.getActualTypeArguments()[0];
					List list=parserToList(genericClazz, getJSONArray(jsonObject, filedName),false);
					field.set(t,list);
				}else{
					throw new IllegalStateException(field.getName()+ " require have generic");
				}
			}
		}
		return t;
	}
	public  static <T> List<T> parserToList(Class<T> c,JSONArray jsonArray,boolean useAnnotation) throws Exception{
		if(jsonArray==null)return  null;
		List<T> list=new ArrayList<T>();
		T t=null;
		for (int i = 0; i < jsonArray.length(); i++) {
			if(jsonArray.get(i) instanceof JSONObject){
				if(useAnnotation){
					t=parserToObjectByAnnotation(c,jsonArray.getJSONObject(i));
				}else{
					t=parserToObject(c,jsonArray.getJSONObject(i));
				}
			}else{
				t=(T) jsonArray.get(i);
			}
			list.add(t);
		}
		return list;
	}
	
	
	private  static <T> void setField(T t,Field field,String filedName,JSONObject jsonObject,boolean useAnnotation)throws Exception{
		field.setAccessible(true);
		if(field.getType()==String.class){
			field.set(t, getString(jsonObject,filedName,null));
		}else if(field.getType()==Integer.class||field.getType()==int.class){
			field.set(t, getInt(jsonObject,filedName, 0));
		}else if(field.getType()==Long.class||field.getType()==long.class){
			field.set(t, getLong(jsonObject, filedName, 0L));
		}else if(field.getType()==Double.class||field.getType()==double.class){
			field.set(t, getDouble(jsonObject, filedName,0.0d));
		}else if(field.getType()==Boolean.class||field.getType()==boolean.class){
			field.set(t, getBoolean(jsonObject, filedName, false));
		}else if(field.getType()==Float.class||field.getType()==float.class){
			field.set(t, (float)getDouble(jsonObject, filedName, 0.0d));
		}else{	
			Object obj=null;
			if(useAnnotation){
				obj=parserToObjectByAnnotation(field.getType(), getJSONObject(jsonObject,filedName));
			}else{
				obj=parserToObject(field.getType(), getJSONObject(jsonObject,filedName));
			}
			field.set(t,obj);
		}
	}
	private static String formatJson(String json) {
		if(null != json && json.startsWith("{\"\"")) {
			json = json.replaceFirst("\"\"", "\"");
		}
		return json;
	}
	private static boolean isBaseClass(Class clz){  
	    return isWrapClass(clz)||clz.isPrimitive()||clz==String.class;
	} 
	
	@SuppressWarnings("rawtypes")
	private static boolean isWrapClass(Class clz){  
	    try {  
	        return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
	    } catch (Exception e) {  
	        return false;  
	    }  
	} 
	private static int getInt(JSONObject obj, String key, int defaultValue) {
		try {
			return obj.getInt(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	private static long getLong(JSONObject obj, String key, long defaultValue) {
		try {
			return obj.getLong(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	private static boolean getBoolean(JSONObject obj, String key, boolean defaultValue) {
		try {
			return obj.getBoolean(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	private static double getDouble(JSONObject obj, String key, double defaultValue) {
		try {
			return obj.getInt(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	
	private static String getString(JSONObject obj, String key, String defaultValue) {
		try {
			return obj.getString(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	private static JSONObject getJSONObject(JSONObject obj, String key) {
		try {
			return obj.getJSONObject(key);
		} catch (JSONException e) {
			return null;
		}
	}
	private static JSONArray getJSONArray(JSONObject obj, String key) {
		try {
			return obj.getJSONArray(key);
		} catch (JSONException e) {
			return null;
		}
	}
}
