/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.parser;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by xuewu.wei on 2016/5/31.
 */
public class Converter {


    public static <T> T parserXml(Class<T> c, String str, boolean useAnnotation) throws XMLParserException {
        return XmlUtils.parserToObject(c, str, useAnnotation);
    }

    public static <T> ArrayList<T> parserXmlList(Class<T> c, String str, boolean useAnnotation) throws XMLParserException {
        return XmlUtils.parserToList(c, str, useAnnotation);
    }

    public static <T> T parserJson(Class<T> c, String str, boolean useAnnotation) throws JSONParserException {
        return JsonUtils.parserToObject(c, str, useAnnotation);
    }

    public static <T> ArrayList<T> parserJsonList(Class<T> c, String str, boolean useAnnotation) throws JSONParserException {
        return JsonUtils.parserToList(c, str, useAnnotation);
    }

    /**
     * 解析Int
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static int parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NullPointerException e) {
            return defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 解析Double
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static double parseDouble(String str, double defaultValue) {
        try {
            return Double.parseDouble(str);
        } catch (NullPointerException e) {
            return defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 解析Boolean
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static boolean parseBoolean(String str, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(str);
        } catch (NullPointerException e) {
            return defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 解析Long
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static long parseLong(String str, long defaultValue) {
        try {
            return Long.parseLong(str);
        } catch (NullPointerException e) {
            return defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 解析Float
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static float parseFloat(String str, float defaultValue) {
        try {
            return Float.parseFloat(str);
        } catch (NullPointerException e) {
            return defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    protected static boolean isTransient(Class clz) {
        return Modifier.isTransient(clz.getModifiers());
    }

    /**
     * 判断是否是基类
     *
     * @param clz
     * @return
     */
    protected static boolean isBaseClass(Class<?> clz) {
        return isWrapClass(clz) || clz.isPrimitive() || clz == String.class;
    }

    /**
     * 判断是否是包装类
     *
     * @param clz
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取字段要解析|转换的名称
     *
     * @param field
     * @param useAnnotation
     * @return
     */
    protected static String getFieldName(Field field, boolean useAnnotation) {
        String filedName = null;
        if (useAnnotation) {
            if (field.isAnnotationPresent(Attribute.class)) {
                Attribute attr = field.getAnnotation(Attribute.class);
                filedName = TextUtils.isEmpty(attr.value()) ? field.getName() : attr.value();
            } else if (field.isAnnotationPresent(Element.class)) {
                Element element = field.getAnnotation(Element.class);
                filedName = TextUtils.isEmpty(element.value()) ? field.getName() : element.value();
            } else if (field.isAnnotationPresent(ElementList.class)) {
                ElementList elementList = field.getAnnotation(ElementList.class);
                filedName = TextUtils.isEmpty(elementList.value()) ? field.getName() : elementList.value();
            } else {
                //do nothing
                filedName = field.getName();
            }
        } else {
            filedName = field.getName();
        }
        return filedName;
    }
}
