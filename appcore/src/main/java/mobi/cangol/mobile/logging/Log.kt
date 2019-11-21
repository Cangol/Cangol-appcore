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
package mobi.cangol.mobile.logging

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * @author Cangol
 */
object Log {
    private var level = android.util.Log.VERBOSE
    private var format = false

    @JvmStatic
    fun makeLogTag(cls: Class<*>): String {
        val tag = cls.simpleName
        return if (tag.length > 50) tag.substring(0, 50) else tag
    }

    /**
     * 设置类对象TAG
     *
     * @param obj
     */
    @JvmStatic
    fun setLogTag(obj: Any) {
        setLogTag(obj.javaClass, obj)
    }

    private fun findField(clazz: Class<*>?, name: String): Field? {
        if (clazz != Any::class.java) {
            return try {
                clazz!!.getDeclaredField(name)
            } catch (e: NoSuchFieldException) {
                findField(clazz!!.superclass, name)
            }
        }
        return null
    }

    /**
     * 设置类的TAG
     *
     * @param clazz
     * @param obj
     */
    @JvmStatic
    fun setLogTag(clazz: Class<*>, obj: Any) {
        if (clazz != Any::class.java) {
            var field: Field?
            try {
                field = findField(clazz, "TAG")
                if (field != null) {
                    if (!Modifier.isPrivate(field.modifiers)) {
                        field.isAccessible = true
                        field.set(obj, makeLogTag(clazz))
                    } else {
                        e("field TAG is private!")
                    }
                } else {
                    e("not field is TAG")
                }
            } catch (e: IllegalAccessException) {
                //
            }

        }
    }

    @JvmStatic
    fun setLogLevelFormat(level: Int, format: Boolean) {
        this.level = level
        this.format = format
    }

    @JvmStatic
    fun getLevel(): Int {
        return level
    }

    @JvmStatic
    fun isFormat(): Boolean {
        return format
    }


    // VERBOSE log

    @JvmStatic
    fun v(msg: String?) {
        formatLog(android.util.Log.VERBOSE, null, msg, null)
    }

    @JvmStatic
    fun v(tag: String, msg: String?) {
        formatLog(android.util.Log.VERBOSE, tag, msg, null)
    }

    @JvmStatic
    fun v(tag: String, msg: String?, t: Throwable) {
        formatLog(android.util.Log.VERBOSE, tag, msg, t)
    }

    // INFO log

    @JvmStatic
    fun i(msg: String?) {
        formatLog(android.util.Log.INFO, null, msg, null)
    }

    @JvmStatic
    fun i(tag: String, msg: String?) {
        formatLog(android.util.Log.INFO, tag, msg, null)
    }

    @JvmStatic
    fun i(tag: String, msg: String?, t: Throwable) {
        formatLog(android.util.Log.INFO, tag, msg, t)
    }

    // DEBUG log

    @JvmStatic
    fun d(msg: String?) {
        formatLog(android.util.Log.DEBUG, null, msg, null)
    }

    @JvmStatic
    fun d(msg: String?, t: Throwable) {
        formatLog(android.util.Log.DEBUG, null, msg, t)
    }

    @JvmStatic
    fun d(tag: String, msg: String?) {
        formatLog(android.util.Log.DEBUG, tag, msg, null)
    }

    @JvmStatic
    fun d(tag: String, msg: String?, t: Throwable) {
        formatLog(android.util.Log.DEBUG, tag, msg, t)
    }

    // WARN log
    @JvmStatic
    fun w(msg: String?) {
        formatLog(android.util.Log.WARN, null, msg, null)
    }

    @JvmStatic
    fun w(tag: String, msg: String?) {
        formatLog(android.util.Log.WARN, tag, msg, null)
    }

    @JvmStatic
    fun w(tag: String, msg: String?, t: Throwable) {
        formatLog(android.util.Log.WARN, tag, msg, t)
    }

    // ERROR log

    @JvmStatic
    fun e(msg: String?) {
        formatLog(android.util.Log.ERROR, null, msg, null)
    }

    @JvmStatic
    fun e(tag: String, msg: String?) {
        formatLog(android.util.Log.ERROR, tag, msg, null)
    }

    @JvmStatic
    fun e(tag: String, msg: String?, t: Throwable) {
        formatLog(android.util.Log.ERROR, tag, msg, t)
    }

    private fun formatLog(logLevel: Int, str: String?, msg: String?, error: Throwable?) {
        var tag = str
        if (level > logLevel) {
            return
        }
        val stackTrace = Throwable().stackTrace[2]
        val classname = stackTrace.className
        val filename = stackTrace.fileName
        val methodName = stackTrace.methodName
        val linenumber = stackTrace.lineNumber
        var output: String?
        output = if (format) {
            String.format("%s.%s(%s:%d)-->%s", classname, methodName, filename, linenumber, msg)
        } else {
            msg
        }
        if (null == tag) {
            tag = if (filename != null && filename.contains(".java")) filename.replace(".java", "") else ""
        }
        if (output == null) {
            output = ""
        }
        when (logLevel) {
            android.util.Log.VERBOSE -> if (error == null) {
                android.util.Log.v(tag, output)
            } else {
                android.util.Log.v(tag, output, error)
            }
            android.util.Log.DEBUG -> if (error == null) {
                android.util.Log.d(tag, output)
            } else {
                android.util.Log.d(tag, output, error)
            }
            android.util.Log.INFO -> if (error == null) {
                android.util.Log.i(tag, output)
            } else {
                android.util.Log.i(tag, output, error)
            }
            android.util.Log.WARN -> if (error == null) {
                android.util.Log.w(tag, output)
            } else {
                android.util.Log.w(tag, output, error)
            }
            android.util.Log.ERROR -> if (error == null) {
                android.util.Log.e(tag, output)
            } else {
                android.util.Log.e(tag, output, error)
            }
            else -> {
            }
        }
    }
}
