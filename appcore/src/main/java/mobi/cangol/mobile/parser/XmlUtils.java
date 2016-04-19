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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class XmlUtils {
	private final static  String TAG = "XmlUtils";
	private final static boolean DEBUG=false;
	/**
	 *  转换Object到xml
	 * @param obj
	 * @return
	 */
	public static String toXml(Object obj){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		try {
			toXml(obj,baos,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toString();
	}
	/**
	 * 转换Object到xml 通过注解（支持属性）
	 * @param obj
	 * @return
	 */
	public static String toXmlByAnnotation(Object obj){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		try {
			toXml(obj,baos,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toString();
	}
	/**
	 * 转换Object到xml流， 通过注解（支持属性）
	 * @param obj
	 * @param outputStream
	 * @param annotation
	 * @throws Exception
	 */
	public static void toXml(Object obj,OutputStream outputStream,boolean annotation)throws Exception{
		  XmlSerializer serializer=Xml.newSerializer();  
	      serializer.setOutput(outputStream, "utf-8"); 
	      serializer.startDocument("utf-8", true);
	      if(annotation){
	    	  toXmlByAnnotation(serializer,obj);
	      }else{
	    	  toXml(serializer,obj);
	      }
	      
	      serializer.endDocument();  
	      outputStream.close();
	}
	private static void toXml(XmlSerializer serializer,Object obj)throws Exception{
	    serializer.startTag(null, obj.getClass().getSimpleName()); 
	  	Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (!List.class.isAssignableFrom(field.getType())) {
				//非集合类型
				if (isBaseClass(field.getType())) {
					serializer.startTag(null, field.getName());  
					serializer.text(String.valueOf(field.get(obj)==null?"":field.get(obj)));  
					serializer.endTag(null, field.getName());
				}else{
					toXml(serializer, field.get(obj));	
				}
			}else{
				//集合类型
				if(field.getGenericType() instanceof ParameterizedType){
					List<?> list=(List<?>) field.get(obj);
					if(list!=null){
						for (int i = 0; i < list.size(); i++) {	
							if (isBaseClass(list.get(i).getClass())) {
								serializer.startTag(null, field.getName());  
								serializer.text(String.valueOf(field.get(obj)==null?"":field.get(obj)));  
								serializer.endTag(null, field.getName());  
							}else{
								toXml(serializer, list.get(i));
							}
						}
					}
				}else{
					if(DEBUG)Log.i(TAG,field.getName()+ " require have generic");
				}
			}
		}
		serializer.endTag(null, obj.getClass().getSimpleName());  
	}
	private static void toXmlByAnnotation(XmlSerializer serializer,Object obj) throws IllegalArgumentException, IllegalStateException, IOException, IllegalAccessException{
	    serializer.startTag(null, obj.getClass().getSimpleName()); 
	  	Field[] fields = obj.getClass().getDeclaredFields();
	  	String filedName=null;
	  	//先处理属性
	  	for (Field field : fields) {
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (field.isAnnotationPresent(Attribute.class)){
				//非集合类型 属性
				Attribute element = field.getAnnotation(Attribute.class);
				filedName="".equals(element.value())?field.getName():element.value();
				if (isBaseClass(field.getType())) {
					serializer.attribute(null, filedName, String.valueOf(field.get(obj)==null?"":field.get(obj)));
				}else{
					
				}
			}
	  	}
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (field.isAnnotationPresent(Element.class)){
				//非集合类型
				Element element = field.getAnnotation(Element.class);
				filedName="".equals(element.value())?field.getName():element.value();
				if (isBaseClass(field.getType())) {
					serializer.startTag(null, filedName);  
					serializer.text(String.valueOf(field.get(obj)==null?"":field.get(obj)));  
					serializer.endTag(null, filedName);
				}else{
					toXmlByAnnotation(serializer, field.get(obj));	
				}
			}else if(field.isAnnotationPresent(ElementList.class)){
				//集合类型
				ElementList elementList = field.getAnnotation(ElementList.class);
				filedName="".equals(elementList.value())?field.getName():elementList.value();
				if(field.getGenericType() instanceof ParameterizedType){
					List<?> list=(List<?>) field.get(obj);
					if(list!=null){
						for (int i = 0; i < list.size(); i++) {	
							if (isBaseClass(list.get(i).getClass())) {
								serializer.startTag(null, filedName);  
								serializer.text(String.valueOf(field.get(obj)==null?"":field.get(obj)));  
								serializer.endTag(null, filedName);  
							}else{
								toXmlByAnnotation(serializer, list.get(i));
							}
						}
					}
				}else{
					if(DEBUG)Log.i(TAG,field.getName()+ " require have generic");
				}
			}
		}
		serializer.endTag(null, obj.getClass().getSimpleName());  
	}
	
	
	/*************以下开始是解析XML**************/
	/**
	 * 解析xml字符串到c的实例
	 * @param c
	 * @param str
	 * @return
	 * @throws XMLParserException
	 */
	public static <T> T  parserToObject(Class<T> c,String str) throws XMLParserException{
		InputStream inputSteam= new ByteArrayInputStream(str.getBytes());   
		return parserToObject(c,inputSteam);
	}
	/**
	 * 解析xml流串到c的实例
	 * @param c
	 * @param inputSteam
	 * @return
	 * @throws XMLParserException
	 */
	public static <T> T  parserToObject(Class<T> c,InputStream inputSteam) throws XMLParserException{
		DocumentParser documentParser=new DocumentParser(inputSteam);
		documentParser.parserDom();
		return parserToObject(c,documentParser.getRoot());
	}
	/**
	 * 解析xml字符串到c的实例（注解方式 :支持属性）
	 * @param c
	 * @param str
	 * @return
	 * @throws XMLParserException
	 */
	public static <T> T  parserToObjectByAnnotation(Class<T> c,String str) throws XMLParserException{
		InputStream inputSteam= new ByteArrayInputStream(str.getBytes());   
		return parserToObjectByAnnotation(c,inputSteam);	
	}
	/**
	 * 解析xml流串到c的实例（注解方式 :支持属性）
	 * @param c
	 * @param inputSteam
	 * @return
	 * @throws XMLParserException
	 */
	public static <T> T  parserToObjectByAnnotation(Class<T> c,InputStream inputSteam) throws XMLParserException{
		DocumentParser documentParser=new DocumentParser(inputSteam);
		documentParser.parserDom();
		return parserToObjectByAnnotation(c,documentParser.getRoot());
	}
	
	private static <T> T  parserToObject(Class<T> c,Node node) throws XMLParserException{
		if(null==node)return null;
		T t=null;
		try {
			t = c.newInstance();
		} catch (InstantiationException e) {
			throw new XMLParserException(c,"must have zero-argument constructor",e);
		} catch (IllegalAccessException e) {
			throw new XMLParserException(c,"constructor is not accessible",e);
		}
		Field[] fields = c.getDeclaredFields();
		String filedName=null;
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (!List.class.isAssignableFrom(field.getType())) {
				filedName=field.getName();
				setField(t,field,node,false,filedName);
			}else{
				filedName=field.getName();
				if(field.getGenericType() instanceof ParameterizedType){
					ParameterizedType pt = (ParameterizedType) field.getGenericType();  
					Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
					List<?> list=parserToList(genericClazz, getNodeList(node,filedName),false);
					try{
						field.set(t,list);
					}catch (IllegalArgumentException e) {
						throw new XMLParserException(c,field,"",e);
					} catch (IllegalAccessException e) {
						throw new XMLParserException(c,field,"",e);
					}
				}else{
					if(DEBUG)Log.i(TAG,field.getName()+ " require have generic");
				}
			}
		}
		return t;
	}
	private static <T> T parserToObjectByAnnotation(Class<T> c,Node node) throws XMLParserException{
		if(null==node)return null;
		T t=null;
		try {
			t = c.newInstance();
		} catch (InstantiationException e) {
			throw new XMLParserException(c,"must have zero-argument constructor",e);
		} catch (IllegalAccessException e) {
			throw new XMLParserException(c,"constructor is not accessible",e);
		}
		Field[] fields = c.getDeclaredFields();
		String filedName=null;
		for (Field field : fields) {
			if(field.isEnumConstant())continue;
			field.setAccessible(true);
			if (field.isAnnotationPresent(Attribute.class)) {
				Attribute attr = field.getAnnotation(Attribute.class);
				filedName="".equals(attr.value())?field.getName():attr.value();
				setValue(t,field,getNodeAttr(node,filedName));
			}else if (field.isAnnotationPresent(Element.class)) {
				Element element = field.getAnnotation(Element.class);
				filedName="".equals(element.value())?field.getName():element.value();
				setField(t,field,node,true,filedName);
			}else if (field.isAnnotationPresent(ElementList.class)) {
				ElementList elementList = field.getAnnotation(ElementList.class);
				filedName="".equals(elementList.value())?field.getName():elementList.value();
				if(field.getGenericType() instanceof ParameterizedType){
					ParameterizedType pt = (ParameterizedType) field.getGenericType();  
					Class<?> genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
					List<?> list=parserToList(genericClazz,getNodeList(node,filedName),true);
					try{
						field.set(t,list);
					}catch (IllegalArgumentException e) {
						throw new XMLParserException(c,field,"",e);
					}catch (IllegalAccessException e) {
						throw new XMLParserException(c,field,"",e);
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
	
	private  static <T> ArrayList<T> parserToList(Class<T> c,NodeList nodeList,boolean useAnnotation)throws XMLParserException{
		if(null==nodeList)return null;
		ArrayList<T> list=new ArrayList<T>();
		T t=null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			if(useAnnotation)
				t=parserToObjectByAnnotation(c, nodeList.item(i));
			else
				t=parserToObject(c,nodeList.item(i));
			list.add(t);
		}
		return list;
	}
	
	private  static <T> void setField(T t,Field field,Node node,boolean useAnnotation,String filedName) throws XMLParserException{
		field.setAccessible(true);
		if(isBaseClass(field.getType())){
			setValue(t,field,getString(node,filedName));
		}else{	
			Object obj=null;
			if(useAnnotation){
				obj=parserToObjectByAnnotation(field.getType(), getNode(node,filedName));
			}else{
				obj=parserToObject(field.getType(), getNode(node,filedName));
			}
			try{
				field.set(t,obj);
			}catch (IllegalArgumentException e) {
				throw new XMLParserException(t.getClass(),field,"Illegal Argument value="+obj,e);
			} catch (IllegalAccessException e) {
				throw new XMLParserException(t.getClass(),field,"Illegal Access "+t,e);
			}
		}
	}
	
	private static <T> void setValue(T t,Field field,String value) throws XMLParserException{
		
		try{
			if(field.getType()==String.class){
				field.set(t, value);
			}else if(field.getType()==Integer.class||field.getType()==int.class){
				field.set(t, parseInt(value, 0));
			}else if(field.getType()==Long.class||field.getType()==long.class){
				field.set(t, parseLong(value, 0L));
			}else if(field.getType()==Double.class||field.getType()==double.class){
				field.set(t, parseDouble(value,0.0d));	
			}else if(field.getType()==Boolean.class||field.getType()==boolean.class){
				field.set(t, parseBoolean(value,false));
			}else if(field.getType()==Float.class||field.getType()==float.class){
				field.set(t, parseFloat(value, 0.0f));
			}
		} catch (IllegalArgumentException e) {
			throw new XMLParserException(t.getClass(),field,"Illegal Argument value="+value,e);
		} catch (IllegalAccessException e) {
			throw new XMLParserException(t.getClass(),field,"Illegal Access "+t,e);
		}
		
	}
	
	private static Node getNode(Node node,String nodeName){
		return DocumentParser.getNode(node, nodeName);
	}
	
	private static NodeList getNodeList(Node node,String nodeName) {
		return DocumentParser.getNodeList(node, nodeName);
	}
	
	private static String getNodeAttr(Node node,String attrName){
		return DocumentParser.getNodeAttr(node, attrName);
	}
	
	public static String getString(Node node,String nodeName) {
		return DocumentParser.getNodeValue(node,nodeName);
	}
	
	public static int parseInt(String str,int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static double parseDouble(String str,double defaultValue) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static boolean parseBoolean(String str,boolean defaultValue) {
		try {
			return Boolean.parseBoolean(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static long parseLong(String str,long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static float parseFloat(String str,float defaultValue) {
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static boolean isBaseClass(Class<?> clz){  
	    return isWrapClass(clz)||clz.isPrimitive()||clz==String.class;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isWrapClass(Class clz){  
	    try {  
	        return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
	    } catch (Exception e) {  
	        return false;  
	    }  
	} 
}
