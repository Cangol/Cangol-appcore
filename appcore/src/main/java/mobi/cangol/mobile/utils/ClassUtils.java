/**
 * Copyright (c) 2013 Cangol
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;
import mobi.cangol.mobile.logging.Log;

public class ClassUtils {

    /**
     * 加载类
     * @param context
     * @param className
     * @return
     */
    public static Class loadClass(Context context, String className) {
        Class clazz = null;
        try {
            clazz = context.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
           Log.d(e.getMessage());
        }
        return clazz;
    }

    /**
     *  获取接口的所有实现类
     * @param c
     * @param context
     * @param packageName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<Class<? extends T>> getAllClassByInterface(Class<T> c, Context context, String packageName) {
        List<Class<? extends T>> returnClassList = new ArrayList<Class<? extends T>>();
        if (c.isInterface()) {
            List<Class<?>> allClass = getAllClassByPackage(packageName, context);
            for (int i = 0; i < allClass.size(); i++) {
                if (c.isAssignableFrom(allClass.get(i)) && !allClass.get(i).isInterface()) {
                    returnClassList.add((Class<? extends T>) allClass.get(i));
                }
            }
            allClass = null;
            //System.gc();
        } else {
            Log.e("class " + c + " is not Interface");
        }
        return returnClassList;
    }

    /**
     * 获取包内所有类
     * @param packageName
     * @param context
     * @return
     */
    public static List<Class<?>> getAllClassByPackage(String packageName, Context context) {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        List<String> list = getAllClassNameFromDexFile(context, packageName);
        Class<?> clazz = null;
        try {
            for (String classNane : list) {
                if (classNane.startsWith(packageName)) {
                    clazz = (Class<?>) context.getClassLoader().loadClass(classNane);
                    classList.add(clazz);
                    clazz = null;
                }
            }
            list = null;
            //System.gc();
        } catch (ClassNotFoundException e) {
            Log.e("ClassNotFoundException " + e.getMessage());
        }
        return classList;
    }

    /**
     * 获取dexFile所有类
     * @param context
     * @return
     */
    public static List<String> getAllClassNameFromDexFile(Context context, String packageName) {
        List<String> classList = new ArrayList<String>();
        try {
            DexFile df = new DexFile(context.getPackageCodePath());
            String str = null;
            if (df != null)
                for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
                    str = iter.nextElement();
                    if (packageName != null && str.startsWith(packageName)) {
                        classList.add(str);
                    } else if (packageName == null || "".equals(packageName)) {
                        classList.add(str);
                    }
                }
            else {
                Log.e("DexFile " + context.getPackageCodePath() + " is null");
            }
            df.close();
            //System.gc();
        } catch (IOException e) {
            Log.e("IOException " + e.getMessage());
        }
        return classList;
    }

    /**
     * 获取dexFile所有类
     * @param context
     * @return
     */
    public static List<Class<?>> getAllClassFromDexFile(Context context, String packageName) {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
            DexFile df = new DexFile(context.getPackageCodePath());
            Class<?> clazz = null;
            String str = null;
            if (df != null)
                for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
                    str = iter.nextElement();
                    if (packageName != null && str.startsWith(packageName)) {
                        clazz = (Class<?>) context.getClassLoader().loadClass(str);
                        classList.add(clazz);
                    } else if (packageName == null || "".equals(packageName)) {
                        clazz = (Class<?>) context.getClassLoader().loadClass(str);
                        classList.add(clazz);
                    }
                    clazz = null;
                }
            else {
                Log.e("DexFile " + context.getPackageCodePath() + " is null");
            }
            df.close();
            //System.gc();
        } catch (IOException e) {
            Log.e("IOException " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ClassNotFoundException " + e.getMessage());
        }
        return classList;
    }
}