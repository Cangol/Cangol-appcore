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
    private static int level = android.util.Log.VERBOSE;

    private static boolean format = false;

    public static String makeLogTag(Class<?> cls) {
        String tag = cls.getSimpleName();
        return (tag.length() > 50) ? tag.substring(0, 50) : tag;
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
                Field field = clazz.getDeclaredField(name);
                return field;
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
        StackTraceElement stackTrace = new Throwable().getStackTrace()[2];
        String classname = stackTrace.getClassName();
        String filename = stackTrace.getFileName();
        String methodname = stackTrace.getMethodName();
        int linenumber = stackTrace.getLineNumber();
        String output = null;
        if (format) {
            output = String.format("%s.%s(%s:%d)-->%s", classname, methodname, filename, linenumber, msg);
        } else {
            output = msg;
        }
        if (null == tag) {
            tag = (filename != null && filename.contains(".java")) ? filename.replace(".java", "") : "";
        }
        if (output == null) {
            output = "" + null;
        }
        switch (logLevel) {
            case android.util.Log.VERBOSE:
                if (error == null) {
                    android.util.Log.v(tag, output);
                } else {
                    android.util.Log.v(tag, output, error);
                }
                break;
            case android.util.Log.DEBUG:
                if (error == null) {
                    android.util.Log.d(tag, output);
                } else {
                    android.util.Log.d(tag, output, error);
                }
                break;
            case android.util.Log.INFO:
                if (error == null) {
                    android.util.Log.i(tag, output);
                } else {
                    android.util.Log.i(tag, output, error);
                }
                break;
            case android.util.Log.WARN:
                if (error == null) {
                    android.util.Log.w(tag, output);
                } else {
                    android.util.Log.w(tag, output, error);
                }
                break;
            case android.util.Log.ERROR:
                if (error == null) {
                    android.util.Log.e(tag, output);
                } else {
                    android.util.Log.e(tag, output, error);
                }
                break;
            default:
                break;
        }
    }
}
