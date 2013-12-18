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
package mobi.cangol.mobile.parser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



/**
 * @Description: Customer VO annotation
 * @version $Revision: 1.0 $ 
 * @author xuewu.wei
 * @date: 2012-6-5
 */
public class JsonUtils {
	private final static boolean DEBUG=false;
	private final static  String TAG = "JsonUtils";
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
			if(field.isEnumConstant())continue;
			if (!List.class.isAssignableFrom(field.getType())) {
				//非集合类型
				if (isBaseClass(field.getType())) {
					json.put(field.getName(), field.get(obj));
				}else{
					json.put(field.getName(), toJSONObject(field.get(obj)));
				}
			}else{
				//集合类型
				if(field.getGenericType() instanceof ParameterizedType){
					List<?> list=(List<?>) field.get(obj);
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
					if(DEBUG)Log.i(TAG,field.getName()+ " require have generic");
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
			if(field.isEnumConstant())continue;
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
					List<?> list=(List<?>) field.get(obj);
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
					if(DEBUG)Log.i(TAG,field.getName()+ " require have generic");
				}
			}else{
				if(DEBUG)Log.i(TAG,"Field:" + field.getName()+ " no Annotation");
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
	public static <T> T parserToObjectByUrl(Class<T> c,String urlStr) throws Exception{
		if(null==urlStr||"".equals(urlStr)){
			throw new IllegalArgumentException("urlStr=null");
		}
		URL url = new URL(urlStr);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		String json=formatJson(inputStreamTOString(urlConnection.getInputStream()));
		JSONObject jsonObject=new JSONObject(json);
		return parserToObject(c,jsonObject);
	}
	public static String inputStreamTOString(InputStream in) throws Exception{  
		int BUFFER_SIZE = 4096;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] data = new byte[BUFFER_SIZE];  
        int count = -1;  
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
            outStream.write(data, 0, count);  
          
        data = null;  
        return new String(outStream.toByteArray(),"utf-8");  
    } 
	
	public static <T> T parserToObjectByAnnotation(Class<T> c,JSONObject jsonObject) throws JSONParserException{
		if(null==jsonObject)return null;
		T t=null;
		try {
			t = c.newInstance();
		} catch (InstantiationException e) {
			throw new JSONParserException(c,"must have zero-argument constructor",e);
		} catch (IllegalAccessException e) {
			throw new JSONParserException(c,"constructor is not accessible",e);
		}
		Field[] fields = c.getDeclaredFields();
		String filedName=null;
		for (Field field : fields) {
			if(field.isEnumConstant())continue;
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
					Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
					List<?> list=parserToList(genericClazz, getJSONArray(jsonObject, filedName),true);
					try{
						field.set(t,list);
					}catch (IllegalArgumentException e) {
						throw new JSONParserException(c,field,"",e);
					} catch (IllegalAccessException e) {
						throw new JSONParserException(c,field,"",e);
					}
				}else{
					if(DEBUG)Log.i(TAG,field.getName()+ " require have generic");
				}
			}else{
				if(DEBUG)Log.i(TAG,"Field:" + field.getName()+ " no Annotation");
			}
		}
		return t;
		
	}
	
	public static <T> T parserToObject(Class<T> c,JSONObject jsonObject) throws JSONParserException{
		if(jsonObject==null)return null;
		T t=null;
		try {
			t = c.newInstance();
		} catch (InstantiationException e) {
			throw new JSONParserException(c,"must have zero-argument constructor",e);
		} catch (IllegalAccessException e) {
			throw new JSONParserException(c,"constructor is not accessible",e);
		}
		Field[] fields = c.getDeclaredFields();
		String filedName=null;
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (!List.class.isAssignableFrom(field.getType())) {
				filedName=field.getName();
				setField(t,field,filedName,jsonObject,false);
			}else{
				filedName=field.getName();
				if(field.getGenericType() instanceof ParameterizedType){
					ParameterizedType pt = (ParameterizedType) field.getGenericType();  
					Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
					List<?> list=parserToList(genericClazz, getJSONArray(jsonObject, filedName),false);
					try{
						field.set(t,list);
					}catch (IllegalArgumentException e) {
						throw new JSONParserException(c,field,"",e);
					} catch (IllegalAccessException e) {
						throw new JSONParserException(c,field,"",e);
					}
				}else{
					if(DEBUG)Log.i(TAG,field.getName()+ " require have generic");
				}
			}
		}
		return t;
	}
	@SuppressWarnings("unchecked")
	public  static <T> ArrayList<T> parserToList(Class<T> c,JSONArray jsonArray,boolean useAnnotation) throws JSONParserException{
		if(jsonArray==null)return  null;
		ArrayList<T> list=new ArrayList<T>();
		T t=null;
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
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
			} catch (JSONException e) {
				throw new JSONParserException("Illegal Argument value=",e);
			}
		}
		return list;
	}
	
	
	private  static <T> void setField(T t,Field field,String key,JSONObject jsonObject,boolean useAnnotation)throws JSONParserException{
		field.setAccessible(true);
		if(isBaseClass(field.getType())){
			setValue(t,field,key,jsonObject);
		}else{	
			Object obj=null;
			if(useAnnotation){
				obj=parserToObjectByAnnotation(field.getType(), getJSONObject(jsonObject,key));
			}else{
				obj=parserToObject(field.getType(), getJSONObject(jsonObject,key));
			}
			try{
				field.set(t,obj);
			}catch (IllegalArgumentException e) {
				throw new JSONParserException(t.getClass(),field,"Illegal Argument value="+obj,e);
			} catch (IllegalAccessException e) {
				throw new JSONParserException(t.getClass(),field,"Illegal Access "+t,e);
			}
		}
	}
	public static <T> void setValue(T t,Field field,String key,JSONObject jsonObject) throws JSONParserException{
		try{
			if(field.getType()==String.class){
				field.set(t, getString(jsonObject,key,null));
			}else if(field.getType()==Integer.class||field.getType()==int.class){
				field.set(t, getInt(jsonObject,key, 0));
			}else if(field.getType()==Long.class||field.getType()==long.class){
				field.set(t, getLong(jsonObject, key, 0L));
			}else if(field.getType()==Double.class||field.getType()==double.class){
				field.set(t, getDouble(jsonObject, key,0.0d));
			}else if(field.getType()==Boolean.class||field.getType()==boolean.class){
				field.set(t, getBoolean(jsonObject, key, false));
			}else if(field.getType()==Float.class||field.getType()==float.class){
				field.set(t, getFloat(jsonObject, key, 0.0f));
			}
		} catch (IllegalArgumentException e) {
			throw new JSONParserException(t.getClass(),field,"Illegal Argument value=",e);
		} catch (IllegalAccessException e) {
			throw new JSONParserException(t.getClass(),field,"Illegal Access "+t,e);
		}
	}
	private static String formatJson(String json) {
		if(null != json && json.startsWith("{\"\"")) {
			json = json.replaceFirst("\"\"", "\"");
		}
		return json;
	}	
	private static boolean isBaseClass(Class<?> clz){  
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
	public static int getInt(JSONObject obj, String key, int defaultValue) {
		try {
			return obj.getInt(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	public static long getLong(JSONObject obj, String key, long defaultValue) {
		try {
			return obj.getLong(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	public static boolean getBoolean(JSONObject obj, String key, boolean defaultValue) {
		try {
			return obj.getBoolean(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	public static float getFloat(JSONObject obj, String key, float defaultValue) {
		String floatStr=null;
		try {
			floatStr=""+obj.get(key);
			return Float.valueOf(floatStr);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	public static double getDouble(JSONObject obj, String key, double defaultValue) {
		try {
			return obj.getDouble(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	
	public static String getString(JSONObject obj, String key, String defaultValue) {
		try {
			return obj.getString(key);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	public static Object getbject(JSONObject obj, String key) {
		try {
			return obj.get(key);
		} catch (JSONException e) {
			return null;
		}
	}
	public static JSONObject getbject(String json) {
		try {
			return new JSONObject(formatJson(json));
		} catch (JSONException e) {
			return null;
		}
	}
	public static JSONObject getJSONObject(JSONObject obj, String key) {
		try {
			return obj.getJSONObject(key);
		} catch (JSONException e) {
			return null;
		}
	}
	public static JSONArray getJSONArray(JSONObject obj, String key) {
		try {
			return obj.getJSONArray(key);
		} catch (JSONException e) {
			return null;
		}
	}
}
