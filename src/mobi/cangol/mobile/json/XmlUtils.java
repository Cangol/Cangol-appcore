package mobi.cangol.mobile.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
	private final static boolean DEBUG=false;
	private final static  String TAG = "XmlUtils";
	public static String toXml(Object obj){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		try {
			toXml(obj,baos,false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toString();
	}
	public static String toXmlByAnnotation(Object obj){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		try {
			toXml(obj,baos,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toString();
	}
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
	public static void toXml(XmlSerializer serializer,Object obj)throws Exception{
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
	public static void toXmlByAnnotation(XmlSerializer serializer,Object obj)throws Exception{
	    serializer.startTag(null, obj.getClass().getSimpleName()); 
	  	Field[] fields = obj.getClass().getDeclaredFields();
	  	String filedName=null;
	  	//先处理属性
	  	for (Field field : fields) {
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (field.isAnnotationPresent(Attribute.class)){
				//非集合类型 属性s
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

	public static <T> T  parserToObject(Class<T> c,String str) throws Exception{
		InputStream inputSteam= new ByteArrayInputStream(str.getBytes());   
		return parserToObject(c,inputSteam);	
	}
	public static <T> T  parserToObject(Class<T> c,InputStream inputSteam) throws Exception{
		DocumentParser documentParser=new DocumentParser(inputSteam);
		return parserToObject(c,documentParser.getRoot());
	}
	public static <T> T  parserToObjectByAnnotation(Class<T> c,String str) throws Exception{
		InputStream inputSteam= new ByteArrayInputStream(str.getBytes());   
		return parserToObjectByAnnotation(c,inputSteam);	
	}
	public static <T> T  parserToObjectByAnnotation(Class<T> c,InputStream inputSteam) throws Exception{
		DocumentParser documentParser=new DocumentParser(inputSteam);
		return parserToObjectByAnnotation(c,documentParser.getRoot());
	}
	public static <T> T  parserToObject(Class<T> c,Node node) throws Exception{
		if(null==node)return null;
		T t  = c.newInstance();
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
					field.set(t,list);
				}else{
					if(DEBUG)Log.i(TAG,field.getName()+ " require have generic");
				}
			}
		}
		return t;
	}
	public static <T> T parserToObjectByAnnotation(Class<T> c,Node node) throws Exception{
		if(null==node)return null;
		T t  = c.newInstance();
		Field[] fields = c.getDeclaredFields();
		String filedName=null;
		for (Field field : fields) {
			if(field.isEnumConstant())continue;
			field.setAccessible(true);
			if (field.isAnnotationPresent(Attribute.class)) {
				Attribute attr = field.getAnnotation(Attribute.class);
				filedName="".equals(attr.value())?field.getName():attr.value();
				field.set(t, field.getType().cast(getNodeAttr(node,filedName)));
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
					field.set(t,list);
				}else{
					if(DEBUG)Log.i(TAG,field.getName()+ " require have generic");
				}
			}else{
				if(DEBUG)Log.i(TAG,"Field:" + field.getName()+ " no Annotation");
			}
		}
		return t;
		
	}
	public  static <T> ArrayList<T> parserToList(Class<T> c,NodeList nodeList,boolean useAnnotation) throws Exception{
		if(null==nodeList)return null;
		ArrayList<T> list=new ArrayList<T>();
		T t=null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			t=parserToObject(c,nodeList.item(i));
			list.add(t);
		}
		return list;
	}
	private  static <T> void setField(T t,Field field,Node node,boolean useAnnotation,String filedName)throws Exception{
		field.setAccessible(true);
		if(field.getType()==String.class){
			field.set(t, getString(node,filedName));
		}else if(field.getType()==Integer.class||field.getType()==int.class){
			field.set(t, getInt(node, 0,filedName));
		}else if(field.getType()==Long.class||field.getType()==long.class){
			field.set(t, getLong(node, 0L, filedName));
		}else if(field.getType()==Double.class||field.getType()==double.class){
			field.set(t, getDouble(node,0.0d, filedName));
		}else if(field.getType()==Boolean.class||field.getType()==boolean.class){
			field.set(t, getBoolean(node, false, filedName));
		}else if(field.getType()==Float.class||field.getType()==float.class){
			field.set(t, getFloat(node, 0.0f, filedName));
		}else{	
			Object obj=null;
			if(useAnnotation){
				obj=parserToObjectByAnnotation(field.getType(), getNode(node,filedName));
			}else{
				obj=parserToObject(field.getType(), getNode(node,filedName));
			}
			field.set(t,obj);
		}
	}
	public static <T> void setValue(T t,Field field,String value)throws Exception{
		if(field.getType()==String.class){
			field.set(t, value);
		}else if(field.getType()==Integer.class||field.getType()==int.class){
			field.set(t, Integer.parseInt(value));
		}else if(field.getType()==Long.class||field.getType()==long.class){
			field.set(t, Long.parseLong(value));
		}else if(field.getType()==Double.class||field.getType()==double.class){
			field.set(t, Double.parseDouble(value));
		}else if(field.getType()==Boolean.class||field.getType()==boolean.class){
			field.set(t, Boolean.parseBoolean(value));
		}else if(field.getType()==Float.class||field.getType()==float.class){
			field.set(t, Float.parseFloat(value));
		}
	}
	public static Node getNode(Node node,String nodeName){
		return DocumentParser.getNode(node, nodeName);
	}
	
	public static NodeList getNodeList(Node node,String nodeName) {
		return DocumentParser.getNodeList(node, nodeName);
	}
	
	public static String getNodeAttr(Node node,String attrName){
		return DocumentParser.getNodeAttr(node, attrName);
	}
	
	public static String getString(Node node,String nodeName) {
		return DocumentParser.getNodeValue(node,nodeName);
	}
	
	public static int getInt(Node node,int defaultValue,String nodeName) {
		String str=DocumentParser.getNodeValue(node,nodeName);
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static double getDouble(Node node,double defaultValue,String nodeName) {
		String str=DocumentParser.getNodeValue(node,nodeName);
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static boolean getBoolean(Node node,boolean defaultValue,String nodeName) {
		String str=DocumentParser.getNodeValue(node,nodeName);
		try {
			return Boolean.parseBoolean(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static long getLong(Node node,long defaultValue,String nodeName) {
		String str=DocumentParser.getNodeValue(node,nodeName);
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static float getFloat(Node node,float defaultValue,String nodeName) {
		String str=DocumentParser.getNodeValue(node,nodeName);
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
