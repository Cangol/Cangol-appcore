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
package mobi.cangol.mobile.parser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.cangol.mobile.logging.Log;


/**
 * @author Cangol
 */
public class JsonUtils extends Converter {
    private static final  String TAG = "JsonUtils";
    public static final String UTF_8 = "utf-8";

    private JsonUtils() {
    }

    /**
     * 转换Object到JSONArray
     *
     * @param <T>
     * @param obj
     * @return
     */
    public static <T> JSONArray toJSONArray(List<T> obj, boolean useAnnotation) {
        JSONArray jsonArray = new JSONArray();
        if (obj != null) {
            for (int i = 0; i < obj.size(); i++) {
                if (isBaseClass(obj.get(i).getClass())) {
                    jsonArray.put(obj.get(i));
                } else {
                    jsonArray.put(toJSONObject(obj.get(i), useAnnotation));
                }
            }
        }
        return jsonArray;
    }

    /**
     * 转换Object到JSONObject
     *
     * @param <T>
     * @param obj
     * @return
     */
    public static <T> JSONObject toJSONObject(T obj, boolean useAnnotation) {
        if (obj == null) {
            return null;
        }
        if (List.class.isAssignableFrom(obj.getClass())) {
            return null;
        }

        JSONObject json = new JSONObject();
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isEnumConstant() || Modifier.isFinal(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
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
        }  catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return json;
    }

    /**
     * 解析JSONObject格式字符串到 Object
     *
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
     *
     * @param c
     * @param str
     * @param useAnnotation
     * @param <T>
     * @return
     * @throws JSONParserException
     */
    public static <T> List<T> parserToList(Class<T> c, String str, boolean useAnnotation) throws JSONParserException {
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
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int count = -1;
        while ((count = in.read(data, 0, 4096)) != -1) {
            outStream.write(data, 0, count);
        }
        return new String(outStream.toByteArray(), UTF_8);
    }

    public static <T> T parserToObject(Class<T> c, JSONObject jsonObject, boolean useAnnotation) throws JSONParserException {
        if (jsonObject == null) {
            return null;
        }
        T t = null;
        try {
            Map<String, Class> typeMap = new HashMap<>();
            Constructor constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            t = (T) constructor.newInstance();
            Field[] fields = c.getDeclaredFields();
            String filedName = null;
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isEnumConstant() || Modifier.isFinal(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                filedName = getFieldName(field, useAnnotation);
                if (!List.class.isAssignableFrom(field.getType())) {
                    Class<?> filedClass = null;

                    if (field.getGenericType() instanceof TypeVariable) {
                        TypeVariable aType = (TypeVariable) field.getGenericType();
                        filedClass = typeMap.get(aType.getName());
                    } else {
                        filedClass = field.getType();
                    }
                    if(jsonObject.has(filedName))
                        field.set(t, getValueFromJson(t, filedClass, filedName, jsonObject, useAnnotation));
                } else {
                    if (field.getGenericType() instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) field.getGenericType();
                        Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
                        List<?> list = parserToList(genericClazz, getJSONArray(jsonObject, filedName), useAnnotation);
                            field.set(t, list);
                    } else {
                        Log.i(TAG, field.getName() + " require have generic");
                    }
                }
            }
        }catch (Exception e) {
            throw new JSONParserException(c, "constructor is not accessible,must have zero-argument constructor", e);
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> parserToList(Class<T> c, JSONArray jsonArray, boolean useAnnotation) throws JSONParserException {
        if (jsonArray == null) {
            return new ArrayList<>();
        }
        List<T> list = new ArrayList<>();
        T t = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.get(i) instanceof JSONObject) {
                    t = parserToObject(c, jsonArray.getJSONObject(i), useAnnotation);
                } else if (jsonArray.get(i) != null) {
                    t = (T) jsonArray.get(i);
                } else {
                    continue;
                }
                list.add(t);
            } catch (JSONException e) {
                throw new JSONParserException("Illegal Argument value=", e);
            }
        }
        return list;
    }

    private static <T> Object getValueFromJson(T t, Class<?> fieldClass, String key, JSONObject jsonObject, boolean useAnnotation) {
        Object value = null;
        try {
            if (isBaseClass(fieldClass)) {
                if (fieldClass == String.class) {
                    value = getString(jsonObject, key);
                } else if (fieldClass == Integer.class || fieldClass == int.class) {
                    value = getInt(jsonObject, key, 0);
                } else if (fieldClass == Long.class || fieldClass == long.class) {
                    value = getLong(jsonObject, key, 0L);
                } else if (fieldClass == Double.class || fieldClass == double.class) {
                    value = getDouble(jsonObject, key, 0.0d);
                } else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
                    value = getBoolean(jsonObject, key, false);
                } else if (fieldClass == Float.class || fieldClass == float.class) {
                    value = getFloat(jsonObject, key, 0.0f);
                }
            } else {
                value = parserToObject(fieldClass, getJSONObject(jsonObject, key), useAnnotation);
            }
        } catch (IllegalArgumentException e) {
            throw new JSONParserException(t.getClass(), "Illegal Argument value=" + value, e);
        } finally {
            return value;
        }
    }

    public static ParameterizedType getType(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
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
            } else {
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
            } else {
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
            } else {
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
            } else {
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
            } else {
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
            } else {
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
            } else {
                return obj.getJSONArray(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }
}
