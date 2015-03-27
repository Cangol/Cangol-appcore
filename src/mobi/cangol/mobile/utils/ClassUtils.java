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

public class ClassUtils {
	
	/**
	 *  获取接口的所有实现类
	 * @param c
	 * @param context
	 * @param packageName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Class<? extends T>> getAllClassByInterface(Class<T> c,Context context,String packageName) {
		List<Class<? extends T>> returnClassList = new ArrayList<Class<? extends T>>(); 
		if (c.isInterface()) {
			List<Class<?>> allClass = getAllClassByPackage(packageName,context); 
			for (int i = 0; i < allClass.size(); i++) {
				if (c.isAssignableFrom(allClass.get(i))&&!allClass.get(i).isInterface()) { 
					returnClassList.add((Class<? extends T>) allClass.get(i));
				}
			}
			allClass=null;
			System.gc();
		}
		return returnClassList;
	}

	/**
	 * 获取包内所有类
	 * @param packageName
	 * @param context
	 * @return
	 */
	public  static  List<Class<?>> getAllClassByPackage(String packageName,Context context){
		List<Class<?>> classList = new ArrayList<Class<?>>(); 
        List<String> list =getAllClassNameFromDexFile(context);
        Class<?> clazz=null;
        try {
	        for (String classNane: list) {
	            if(classNane.startsWith(packageName)){
						clazz = (Class<?>) context.getClassLoader().loadClass(classNane);
						classList.add(clazz);
						clazz=null;
	            }
	        }
	        list=null;
	        System.gc();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classList;
	}
	/**
	 * 获取dexFile所有类
	 * @param context
	 * @return
	 */
	public  static  List<String> getAllClassNameFromDexFile(Context context){
		List<String> classList = new ArrayList<String>(); 
		try {
	        DexFile df = new DexFile(context.getPackageCodePath());
	        if(df!=null) for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
            	classList.add(iter.nextElement());
	        }
	        df.close();
	        System.gc();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return classList;
	}
	/**
	 * 获取dexFile所有类
	 * @param context
	 * @return
	 */
	public  static  List<Class<?>> getAllClassFromDexFile(Context context){
		List<Class<?>> classList = new ArrayList<Class<?>>(); 
		try {
	        DexFile df = new DexFile(context.getPackageCodePath());
	        Class<?> clazz=null;
	        if(df!=null)for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) {
            	clazz=(Class<?>) context.getClassLoader().loadClass(iter.nextElement());
            	classList.add(clazz);
            	clazz=null;
	        }
	        df.close();
	        System.gc();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classList;
	}
}