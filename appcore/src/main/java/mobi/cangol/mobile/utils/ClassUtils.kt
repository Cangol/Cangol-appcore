/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils

import android.content.Context
import dalvik.system.DexFile
import mobi.cangol.mobile.logging.Log
import java.io.IOException
import java.util.*

object ClassUtils {

    /**
     * 加载类
     *
     * @param context
     * @param className
     * @return
     */
    @JvmStatic
    fun loadClass(context: Context, className: String): Class<*>? {
        var clazz: Class<*>? = null
        try {
            clazz = context.classLoader.loadClass(className)
        } catch (e: ClassNotFoundException) {
            Log.d(e.message)
        }

        return clazz
    }

    /**
     * 获取接口的所有实现类
     *
     * @param c
     * @param context
     * @param packageName
     * @return
     */
    @JvmStatic
    fun <T> getAllClassByInterface(c: Class<T>, context: Context, packageName: String): List<Class<out T>> {
        val returnClassList = ArrayList<Class<out T>>()
        if (c.isInterface) {
            val allClass = getAllClassByPackage(packageName, context)
            for (i in allClass.indices) {
                if (c.isAssignableFrom(allClass[i]) && !allClass[i].isInterface) {
                    returnClassList.add(allClass[i] as Class<out T>)
                }
            }
        } else {
            Log.e("class $c is not Interface")
        }
        return returnClassList
    }

    /**
     * 获取包内所有类
     *
     * @param packageName
     * @param context
     * @return
     */
    @JvmStatic
    fun getAllClassByPackage(packageName: String, context: Context): List<Class<*>> {
        val classList = ArrayList<Class<*>>()
        val list = getAllClassNameFromDexFile(context, packageName)
        var clazz: Class<*>
        try {
            for (className in list) {
                if (className.startsWith(packageName)) {
                    clazz = context.classLoader.loadClass(className)
                    classList.add(clazz)
                }
            }
        } catch (e: ClassNotFoundException) {
            Log.e("ClassNotFoundException " + e.message)
        }

        return classList
    }

    /**
     * 获取dexFile所有类
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getAllClassNameFromDexFile(context: Context, packageName: String?): List<String> {
        val classList = ArrayList<String>()
        try {
            val df = DexFile(context.packageCodePath)
            var str: String
            val iter = df.entries()
            while (iter.hasMoreElements()) {
                str = iter.nextElement()
                if (packageName != null && str.startsWith(packageName) || packageName == null || "" == packageName) {
                    classList.add(str)
                }
            }
            df.close()
        } catch (e: IOException) {
            Log.e("IOException " + e.message)
        }

        return classList
    }

    /**
     * 获取dexFile所有类
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getAllClassFromDexFile(context: Context, packageName: String?): List<Class<*>> {
        val classList = ArrayList<Class<*>>()
        try {
            val df = DexFile(context.packageCodePath)
            var clazz: Class<*>
            var str: String
            val iter = df.entries()
            while (iter.hasMoreElements()) {
                str = iter.nextElement()
                if (packageName != null && str.startsWith(packageName) || packageName == null || "" == packageName) {
                    clazz = context.classLoader.loadClass(str)
                    classList.add(clazz)
                }
            }
            df.close()
        } catch (e: IOException) {
            Log.e("IOException " + e.message)
        } catch (e: ClassNotFoundException) {
            Log.e("ClassNotFoundException " + e.message)
        }

        return classList
    }
}