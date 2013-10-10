package com.cangol.mobile.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.cangol.mobile.utils.ClassUtils;

public class AppServiceManagerImpl extends AppServiceManager {
	
	private Map<String, AppService> runServiceMap = new Hashtable<String, AppService>();
	private Map<String,Class<? extends AppService>> serviceMap = new Hashtable<String,Class<? extends AppService>>();
	private Context context;
	private boolean useAnnotation=true;
	public AppServiceManagerImpl(Context context){
		this.context=context;
		initServiceMap();
	}
	private void initServiceMap() {	
		
		List<Class> classList=ClassUtils.getAllClassByInterface(AppService.class);
		for(Class<? extends AppService> clazz:classList){
			try {
				if(useAnnotation){
					if(clazz.getClass().isAnnotationPresent(Service.class)){
						Service service = clazz.getClass().getAnnotation(Service.class);
						serviceMap.put(service.value(), clazz);
					}
				}else{
					Method method=clazz.getMethod("getName");
					Object t=clazz.newInstance();
					String name=(String) method.invoke(t);
					serviceMap.put(name, clazz);	
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
		if(runServiceMap.containsKey(name)){
			appService= runServiceMap.get(name);
		}else{
			try {
				if(serviceMap.containsKey(name)){
					appService=serviceMap.get(name).newInstance();
					appService.setContext(context);
				}else{
					throw new IllegalStateException("hasn't appService'name is "+name);
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return appService;
	}

}
