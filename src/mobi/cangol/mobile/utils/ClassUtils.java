package mobi.cangol.mobile.utils;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

import android.content.Context;
import android.util.Log;

@SuppressWarnings("all")
public class ClassUtils {
	
	/**
	 * 获取接口的所有实现类
	 * @param c
	 * @param context
	 * @return
	 */
	public static List<Class> getAllClassByInterface(Class c,Context context) {
		List<Class> returnClassList = new ArrayList<Class>(); 
		if (c.isInterface()) {
			String packageName = c.getPackage().getName(); 
			List<Class> allClass = getAllClassByPackage(packageName,context); 
			for (int i = 0; i < allClass.size(); i++) {
				if (c.isAssignableFrom(allClass.get(i))&&!allClass.get(i).isInterface()) { 
					returnClassList.add(allClass.get(i));
				}
			}
		}
		return returnClassList;
	}
	
	/**
	 * 获取包内所有类
	 * @param packageName
	 * @param context
	 * @return
	 */
	public  static List<Class> getAllClassByPackage(String packageName,Context context){
		List<Class> classList = new ArrayList<Class>(); 
		try {
	        DexFile df = new DexFile(context.getPackageCodePath());
	        for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
	            String s = iter.nextElement();
	            if(s.startsWith(packageName)){
	            	Class<?> clazz=context.getClassLoader().loadClass(s);
	            	classList.add(clazz);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classList;
	}
}