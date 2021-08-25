/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.logging;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Cangol
 */
public class Log {
    private Log() {
    }
    private static final int MAX_LENGTH=3000;
    private static int level = android.util.Log.VERBOSE;

    private static boolean format = false;

    public static String makeLogTag(Class<?> cls) {
        final String tag = cls.getSimpleName();
        return (tag.length() > 50) ? tag.substring(0, 50) : tag;
    }
    public static String getStackTraceString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }
    /**
     * 设置类对象TAG
     *
     * @param obj
     */
    public static void setLogTag(Object obj) {
        setLogTag(obj.getClass(), obj);
    }

    private static Field findField(Class clazz, String name) {
        if (clazz != Object.class) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                return findField(clazz.getSuperclass(), name);
            }
        }
        return null;
    }

    /**
     * 设置类的TAG
     *
     * @param clazz
     * @param obj
     */
    public static void setLogTag(Class clazz, Object obj) {
        if (clazz != Object.class) {
            Field field = null;
            try {
                field = findField(clazz, "TAG");
                if (field != null) {
                    if (!Modifier.isPrivate(field.getModifiers())) {
                        field.setAccessible(true);
                        field.set(obj, makeLogTag(clazz));
                    } else {
                        Log.e("field TAG is private!");
                    }
                } else {
                    Log.e("not field is TAG");
                }
            } catch (IllegalAccessException e) {
                //
            }
        }
    }

    public static void setLogLevelFormat(int level, boolean format) {
        Log.level = level;
        Log.format = format;
    }

    public static int getLevel() {
        return level;
    }

    public static boolean isFormat() {
        return format;
    }
    // VERBOSE log

    public static void v(String msg) {
        formatLog(android.util.Log.VERBOSE, null, msg, null);
    }

    public static void v(String tag, String msg) {
        formatLog(android.util.Log.VERBOSE, tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable t) {
        formatLog(android.util.Log.VERBOSE, tag, msg, t);
    }

    // INFO log

    public static void i(String msg) {
        formatLog(android.util.Log.INFO, null, msg, null);
    }

    public static void i(String tag, String msg) {
        formatLog(android.util.Log.INFO, tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable t) {
        formatLog(android.util.Log.INFO, tag, msg, t);
    }

    // DEBUG log

    public static void d(String msg) {
        formatLog(android.util.Log.DEBUG, null, msg, null);
    }

    public static void d(String tag, String msg) {
        formatLog(android.util.Log.DEBUG, tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable t) {
        formatLog(android.util.Log.DEBUG, tag, msg, t);
    }

    // WARN log
    public static void w(String msg) {
        formatLog(android.util.Log.WARN, null, msg, null);
    }

    public static void w(String tag, String msg) {
        formatLog(android.util.Log.WARN, tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable t) {
        formatLog(android.util.Log.WARN, tag, msg, t);
    }

    // ERROR log

    public static void e(String msg) {
        formatLog(android.util.Log.ERROR, null, msg, null);
    }

    public static void e(String tag, String msg) {
        formatLog(android.util.Log.ERROR, tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable t) {
        formatLog(android.util.Log.ERROR, tag, msg, t);
    }
    private static void formatLog(int logLevel, String tag, String msg, Throwable error) {
        if (level > logLevel) {
            return;
        }
        final StackTraceElement stackTrace = new Throwable().getStackTrace()[2];
        final String classname = stackTrace.getClassName();
        final String filename = stackTrace.getFileName();
        final String methodName = stackTrace.getMethodName();
        final int linenumber = stackTrace.getLineNumber();
        String output = null;
        if (format) {
            output = String.format("%s.%s(%s:%d)-->%s", classname, methodName, filename, linenumber, msg);
        } else {
            output = msg;
        }
        if (null == tag) {
            tag = (filename != null && filename.contains(".java")) ? filename.replace(".java", "") : "";
        }
        if (output == null) {
            output = "" + null;
        }
        if (error == null) {
            println(logLevel,tag, output);
        } else {
            println(logLevel,tag,output + '\n' + android.util.Log.getStackTraceString(error));
        }
    }
    private static void println(int logLevel,String tag,String msg){
        int strLength = msg.length();
        int start = 0;
        int end = MAX_LENGTH;
        int line=strLength/MAX_LENGTH+1;
        for (int i = 0; i < line; i++) {
            if (strLength > end) {
                android.util.Log.println(logLevel,tag + i, msg.substring(start, end));
                start = end;
                end = end + MAX_LENGTH;
            } else {
                android.util.Log.println(logLevel,tag, msg.substring(start, strLength));
                break;
            }
        }
    }
}
