package mobi.cangol.mobile.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mobi.cangol.mobile.utils.ClassUtils;
import android.content.Context;
import android.util.Log;

public class AppServiceManagerImpl extends AppServiceManager {
	private final static String  TAG=" AppServiceManager";
	private Map<String, AppService> runServiceMap = new Hashtable<String, AppService>();
	private Map<String,Class<? extends AppService>> serviceMap = new Hashtable<String,Class<? extends AppService>>();
	private Context context;
	private boolean useAnnotation=true;
	private List<String> packageNames=new ArrayList<String>();
	public AppServiceManagerImpl(Context context){
		this.context=context;
		initServiceMap(ClassUtils.getAllClassByInterface(AppService.class, context, context.getPackageName()));
	}
	private void initServiceMap(List<Class<? extends AppService>> classList) {	
		for(Class<? extends AppService> clazz:classList){
			try {
				if(useAnnotation){
					if(clazz.isAnnotationPresent(Service.class)){
						Service service = clazz.getAnnotation(Service.class);
						serviceMap.put(service.value(), clazz);
					}else{
						Log.d(TAG, clazz+" no Service Annotation");
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
					appService.create(context);
					runServiceMap.put(name, appService);
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
	@Override
	public void destoryService(String name) {
		AppService appService=null;
		if(runServiceMap.containsKey(name)){
			appService= runServiceMap.get(name);
			appService.destory();
			runServiceMap.remove(name);
		}else{
			Log.d(TAG, name+" Service is not running");
		}
	}
	@Override
	public void destoryAllService() {
		AppService appService=null;
		for(String name:runServiceMap.keySet()){
			appService= runServiceMap.get(name);
			appService.destory();
		}
		runServiceMap.clear();
	}
	@Override
	public void setScanPackage(String... packageName) {
		if(packageName.length>0){
			List<Class<? extends AppService>> classList= new ArrayList<Class<? extends AppService>>(); 
			for(String name:packageName){
				packageNames.add(name);
				classList.addAll(ClassUtils.getAllClassByInterface(AppService.class, context, name));
			}
			initServiceMap(classList);
		}
	}

}
