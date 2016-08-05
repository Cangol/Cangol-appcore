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
package mobi.cangol.mobile.parser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.logging.Log;


/**
 * @author Cangol
 */
public class JsonUtils extends Converter {
    private final static String TAG = "JsonUtils";

    /**
     * 转换Object到JSONObject
     * @param <T>
     * @param obj
     * @return
     */
    public static <T> JSONObject toJSONObject(T obj, boolean useAnnotation) {
        if (obj == null) {
            return null;
        }
        JSONObject json = new JSONObject();
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isEnumConstant() || Modifier.isFinal(field.getModifiers()) || Modifier.isTransient(field.getModifiers())){
                    continue;
                }
                String filedName = getFieldName(field, useAnnotation);
                if (!List.class.isAssignableFrom(field.getType())) {
                    //非集合类型
                    if (isBaseClass(field.getType())) {
                        json.put(filedName, field.get(obj));
                    } else {
                        json.put(filedName, toJSONObject(field.get(obj), useAnnotation));
                    }
                } else {
                    //集合类型
                    if (field.getGenericType() instanceof ParameterizedType) {
                        List<?> list = (List<?>) field.get(obj);
                        JSONArray jsonArray = new JSONArray();
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                if (isBaseClass(list.get(i).getClass())) {
                                    jsonArray.put(list.get(i));
                                } else {
                                    jsonArray.put(toJSONObject(list.get(i), useAnnotation));
                                }
                            }
                        }
                        json.put(filedName, jsonArray);
                    } else {
                        Log.d(TAG, field.getName() + " require have generic");
                    }
                }

            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.d(TAG, e.getMessage());
        }
        return json;
    }

    /**
     * 解析JSONObject格式字符串到 Object
     * @param c
     * @param str
     * @param useAnnotation
     * @param <T>
     * @return
     * @throws JSONParserException
     */
    public static <T> T parserToObject(Class<T> c, String str, boolean useAnnotation) throws JSONParserException {
        if (null == str || "".equals(str)) {
            throw new IllegalArgumentException("str=null");
        }
        String json = formatJson(str);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            throw new JSONParserException(e.getMessage(), e);
        }
        return parserToObject(c, jsonObject, useAnnotation);
    }

    /**
     * 解析JSONArray格式字符串到 List
     * @param c
     * @param str
     * @param useAnnotation
     * @param <T>
     * @return
     * @throws JSONParserException
     */
    public static <T> ArrayList<T> parserToList(Class<T> c, String str, boolean useAnnotation) throws JSONParserException {
        if (null == str || "".equals(str)) {
            throw new IllegalArgumentException("str=null");
        }
        String json = formatJson(str);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            throw new JSONParserException(e.getMessage(), e);
        }
        return parserToList(c, jsonArray, useAnnotation);
    }

    /**
     *
     * @param c
     * @param urlStr
     * @param <T>
     * @return
     * @throws JSONParserException
     */
    public static <T> T parserToObjectByUrl(Class<T> c, String urlStr) throws JSONParserException {
        if (null == urlStr || "".equals(urlStr)) {
            throw new IllegalArgumentException("urlStr=null");
        }
        URL url = null;
        HttpURLConnection urlConnection = null;
        String json = null;
        JSONObject jsonObject = null;
        try {
            url = new URL(urlStr);
            urlConnection = (HttpURLConnection) url.openConnection();
            json = formatJson(inputStreamTOString(urlConnection.getInputStream()));
            jsonObject = new JSONObject(json);
        } catch (Exception e) {
            throw new JSONParserException(e.getMessage(), e);
        }
        return parserToObject(c, jsonObject, true);
    }

    private static String inputStreamTOString(InputStream in) throws Exception {
        int BUFFER_SIZE = 4096;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1){
            outStream.write(data, 0, count);
        }
        data = null;
        return new String(outStream.toByteArray(), "utf-8");
    }

    public static <T> T parserToObject(Class<T> c, JSONObject jsonObject, boolean useAnnotation) throws JSONParserException {
        if (jsonObject == null) {
            return null;
        }
        T t = null;
        try {
            Constructor constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            t = (T) constructor.newInstance();
            Field[] fields = c.getDeclaredFields();
            String filedName = null;
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isEnumConstant() || Modifier.isFinal(field.getModifiers()) || Modifier.isTransient(field.getModifiers())){
                    continue;
                }
                filedName = getFieldName(field, useAnnotation);
                if (!List.class.isAssignableFrom(field.getType())) {
                    setField(t, field, filedName, jsonObject, false);
                } else {
                    if (field.getGenericType() instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) field.getGenericType();
                        Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
                        List<?> list = parserToList(genericClazz, getJSONArray(jsonObject, filedName), false);
                        try {
                            field.set(t, list);
                        } catch (IllegalArgumentException e) {
                            throw new JSONParserException(c, field, "", e);
                        } catch (IllegalAccessException e) {
                            throw new JSONParserException(c, field, "", e);
                        }
                    } else {
                        Log.i(TAG, field.getName() + " require have generic");
                    }
                }
            }
        } catch (InstantiationException e) {
            throw new JSONParserException(c, "must have zero-argument constructor", e);
        } catch (IllegalAccessException e) {
            throw new JSONParserException(c, "constructor is not accessible", e);
        } catch (NoSuchMethodException e) {
            throw new JSONParserException(c, "must have zero-argument constructor", e);
        } catch (InvocationTargetException e) {
            throw new JSONParserException(c, "must have zero-argument constructor", e);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> parserToList(Class<T> c, JSONArray jsonArray, boolean useAnnotation) throws JSONParserException {
        if (jsonArray == null) {
            return null;
        }
        ArrayList<T> list = new ArrayList<T>();
        T t = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.get(i) instanceof JSONObject) {
                    t = parserToObject(c, jsonArray.getJSONObject(i), useAnnotation);
                } else {
                    t = (T) jsonArray.get(i);
                }
                list.add(t);
            } catch (JSONException e) {
                throw new JSONParserException("Illegal Argument value=", e);
            }
        }
        return list;
    }

    private static <T> void setField(T t, Field field, String key, JSONObject jsonObject, boolean useAnnotation) throws JSONParserException {
        field.setAccessible(true);
        Object value = null;
        try {
            if (isBaseClass(field.getType())) {
                if (field.getType() == String.class) {
                    value = getString(jsonObject, key);
                } else if (field.getType() == Integer.class || field.getType() == int.class) {
                    value = getInt(jsonObject, key, 0);
                } else if (field.getType() == Long.class || field.getType() == long.class) {
                    value = getLong(jsonObject, key, 0L);
                } else if (field.getType() == Double.class || field.getType() == double.class) {
                    value = getDouble(jsonObject, key, 0.0d);
                } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                    value = getBoolean(jsonObject, key, false);
                } else if (field.getType() == Float.class || field.getType() == float.class) {
                    value = getFloat(jsonObject, key, 0.0f);
                }
            } else {
                value = parserToObject(field.getType(), getJSONObject(jsonObject, key), useAnnotation);
            }
            field.set(t, value);
        } catch (IllegalArgumentException e) {
            throw new JSONParserException(t.getClass(), field, "Illegal Argument value=" + value, e);
        } catch (IllegalAccessException e) {
            throw new JSONParserException(t.getClass(), field, "Illegal Access " + t, e);
        }


    }

    public static String formatJson(String json) {
        if (null != json && json.startsWith("{\"\"")) {
            json = json.replaceFirst("\"\"", "\"");
        }
        return json;
    }

    public static JSONObject formatJSONObject(String json) throws JSONException {
        if (null != json && json.startsWith("{\"\"")) {
            json = json.replaceFirst("\"\"", "\"");
        }
        return new JSONObject(json);
    }

    public static int getInt(JSONObject obj, String key, int defaultValue) {
        try {
            if (obj.isNull(key)) {
                return defaultValue;
            } else{
                return obj.getInt(key);
            }
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static long getLong(JSONObject obj, String key, long defaultValue) {
        try {
            if (obj.isNull(key)) {
                return defaultValue;
            } else{
                return obj.getLong(key);
            }
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(JSONObject obj, String key, boolean defaultValue) {
        try {
            if (obj.isNull(key)) {
                return defaultValue;
            } else{
                return obj.getBoolean(key);
            }
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static float getFloat(JSONObject obj, String key, float defaultValue) {
        String floatStr = null;
        try {
            floatStr = "" + obj.get(key);
            return Float.valueOf(floatStr);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static double getDouble(JSONObject obj, String key, double defaultValue) {
        try {
            if (obj.isNull(key)) {
                return defaultValue;
            } else{
                return obj.getDouble(key);
            }
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static String getString(JSONObject obj, String key) {
        try {
            if (obj.isNull(key)) {
                return null;
            } else {
                return obj.getString(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getString(JSONObject obj, String key, String defaultValue) {
        try {
            if (obj.isNull(key)) {
                return defaultValue;
            } else {
                return obj.getString(key);
            }
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static Object getObject(JSONObject obj, String key) {
        try {
            if (obj.isNull(key)) {
                return null;
            } else{
                return obj.get(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getObject(String json) {
        try {
            return new JSONObject(formatJson(json));
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONObject obj, String key) {
        try {
            if (obj.isNull(key)) {
                return null;
            } else{
                return obj.getJSONObject(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONArray getJSONArray(JSONObject obj, String key) {
        try {
            if (obj.isNull(key)) {
                return null;
            } else{
                return obj.getJSONArray(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }
}
