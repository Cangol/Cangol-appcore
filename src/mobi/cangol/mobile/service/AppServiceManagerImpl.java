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
package mobi.cangol.mobile.service;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.ClassUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

public class AppServiceManagerImpl extends AppServiceManager {
	private final static String  TAG=" AppServiceManager";
	private Map<String, AppService> mRunServiceMap = new Hashtable<String, AppService>();
	private Map<String,Class<? extends AppService>> mServiceMap = new Hashtable<String,Class<? extends AppService>>();
	private Context mContext;
	private boolean mUseAnnotation=true;
	private List<String> mPackageNames=new ArrayList<String>();
	private Map<String,ServiceProperty> mProperties=new HashMap<String,ServiceProperty>();
	public AppServiceManagerImpl(Context context){
		this.mContext=context;
    	initClass();
	}
	private void initClass(){
		List<Class<? extends AppService>> classList=	ClassUtils.getAllClassByInterface(AppService.class, mContext, this.getClass().getPackage().getName());
		classList.addAll(ClassUtils.getAllClassByInterface(AppService.class, mContext, mContext.getPackageName()));
		initServiceMap(classList);
		initServiceProperties();
	}
	private void initServiceMap(List<Class<? extends AppService>> classList) {	
		for(Class<? extends AppService> clazz:classList){
			try {
				if(mUseAnnotation){
					if(clazz.isAnnotationPresent(Service.class)){
						Service service = clazz.getAnnotation(Service.class);
						mServiceMap.put(service.value(), clazz);
					}else{
						Log.d(TAG, clazz+" no Service Annotation");
					}
				}else{
					Method method=clazz.getMethod("getName");
					Object t=clazz.newInstance();
					String name=(String) method.invoke(t);
					mServiceMap.put(name, clazz);	
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public AppService getAppService(String name) {
		AppService appService=null;
		if(mRunServiceMap.containsKey(name)){
			appService= mRunServiceMap.get(name);
		}else{
			try {
				if(mServiceMap.containsKey(name)){
					Constructor<AppService> c =(Constructor<AppService>) mServiceMap.get(name).getDeclaredConstructor();
					c.setAccessible(true);
					appService=c.newInstance();
					appService.onCreate(mContext);
					appService.init(mProperties.get(name)!=null?mProperties.get(name):new ServiceProperty(name));
					mRunServiceMap.put(name, appService);
				}else{
					throw new IllegalStateException("hasn't appService'name is "+name);
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} 
		}
		return appService;
	}
	/**
	 * @deprecated
	 * @param appService
	 * @param serviceProperty
	 */
	private void init(AppService appService,ServiceProperty serviceProperty){
		Field filed=null;
		try {
			filed = appService.getClass().getDeclaredField("mServiceProperty");
			if(filed==null){
				filed = appService.getClass().getDeclaredField("serviceProperty");
			}else{
				for(Field filed1:appService.getClass().getDeclaredFields()){
					filed1.setAccessible(true);
					if(filed1.getType() ==ServiceProperty.class){
						filed=filed1;
						break;
					}
				}
			}
			if(filed!=null){
				filed.setAccessible(true);
				filed.set(appService,serviceProperty);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void destoryService(String name) {
		AppService appService=null;
		if(mRunServiceMap.containsKey(name)){
			appService= mRunServiceMap.get(name);
			appService.onDestory();
			mRunServiceMap.remove(name);
		}else{
			Log.d(TAG, name+" Service is not running");
		}
	}
	@Override
	public void destoryAllService() {
		AppService appService=null;
		for(String name:mRunServiceMap.keySet()){
			appService= mRunServiceMap.get(name);
			appService.onDestory();
		}
		mRunServiceMap.clear();
		
	}
	@Override
	public void destory() {
		destoryAllService() ;
		mProperties.clear();
		mServiceMap.clear();
		mPackageNames.clear();
	}
	@Override
	public void setScanPackage(String... packageName) {
		if(packageName.length>0){
			List<Class<? extends AppService>> classList= new ArrayList<Class<? extends AppService>>(); 
			for(String name:packageName){
				mPackageNames.add(name);
				classList.addAll(ClassUtils.getAllClassByInterface(AppService.class, mContext, name));
			}
			initServiceMap(classList);
		}
	}
	public void initServiceProperties(){
		if(Build.VERSION.SDK_INT>=9){
			// Temporarily disable logging of disk reads on the Looper thread
			StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
	        InputStream is= this.getClass().getResourceAsStream("properties.xml");
	        initSource(is);
	        StrictMode.setThreadPolicy(oldPolicy);
	   }else{
	   	 	InputStream is= this.getClass().getResourceAsStream("properties.xml");
	   	 	initSource(is);
	   }
	}
	@Override
	public void initSource(InputStream is) {
		try {
			parser(is);
			is.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void parser(InputStream is) throws Exception{
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document=builder.parse(is);
		Element root=document.getDocumentElement();
		NodeList nodeList=root.getChildNodes();
		for(int i=0;i<nodeList.getLength();i++){
			Node node=nodeList.item(i);
			if(node instanceof Element){
				Element element=(Element)node;
				String name=element.getAttribute("name");
				NodeList nodeList2=element.getChildNodes();
				ServiceProperty properties=new ServiceProperty(name);
				for(int j=0;j<nodeList2.getLength();j++){
					Node node2=nodeList2.item(j);
					if(node2 instanceof Element){
						Element element2=(Element)node2;	
						properties.putString(element2.getAttribute("name"), element2.getTextContent());
					}
				}
				mProperties.put(name, properties);
			}
		}
	}
	
}
