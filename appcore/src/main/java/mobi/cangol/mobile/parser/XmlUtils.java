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

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils extends Converter {
    private static final String TAG = "XmlUtils";
    public static final String UTF_8 = "UTF-8";

    private XmlUtils() {
    }

    /**
     * 转换Object到xml
     *
     * @param obj
     * @param useAnnotation
     * @return
     */
    public static String toXml(Object obj, boolean useAnnotation) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final XmlSerializer serializer = Xml.newSerializer();
        String result = null;
        try {
            serializer.setOutput(baos, UTF_8);
            serializer.startDocument(UTF_8, true);
            toXml(serializer, obj, useAnnotation);
            serializer.endDocument();
            baos.close();
            result = baos.toString(UTF_8);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return result;
    }

    private static void toXml(XmlSerializer serializer, Object obj, boolean useAnnotation) {
        try {
            serializer.startTag(null, obj.getClass().getSimpleName());
            final Field[] fields = obj.getClass().getDeclaredFields();
            for (final Field field : fields) {
                field.setAccessible(true);
                if (field.isEnumConstant() || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
               final String filedName = getFieldName(field, useAnnotation);
                if (!List.class.isAssignableFrom(field.getType())) {
                    //非集合类型
                    if (isBaseClass(field.getType())) {
                        if (field.isAnnotationPresent(Attribute.class)) {
                            serializer.attribute(null, filedName, String.valueOf(field.get(obj) == null ? "" : field.get(obj)));
                        } else {
                            serializer.startTag(null, filedName);
                            serializer.text(String.valueOf(field.get(obj) == null ? "" : field.get(obj)));
                            serializer.endTag(null, filedName);
                        }
                    } else {
                        toXml(serializer, field.get(obj), useAnnotation);
                    }
                } else {
                    //集合类型
                    if (field.getGenericType() instanceof ParameterizedType) {
                        final List<?> list = (List<?>) field.get(obj);
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                toXml(serializer, list.get(i), useAnnotation);
                            }
                        }
                    } else {
                        Log.i(TAG, field.getName() + " require have generic");
                    }
                }
            }
            serializer.endTag(null, obj.getClass().getSimpleName());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }


    /*************以下开始是解析XML**************/
    /**
     * 解析xml流串到c的实例
     *
     * @param c             解析目标类
     * @param str           解析字符串
     * @param useAnnotation 是否使用注解
     * @param <T>
     * @return
     * @throws XMLParserException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static <T> T parserToObject(Class<T> c, String str, boolean useAnnotation) throws XMLParserException {
        return parserToObject(c, new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), useAnnotation);
    }

    /**
     * 解析xml流串到c的实例
     *
     * @param c             解析目标类
     * @param inputSteam    输入流
     * @param useAnnotation 是否使用注解
     * @param <T>
     * @return
     * @throws XMLParserException
     */
    public static <T> T parserToObject(Class<T> c, InputStream inputSteam, boolean useAnnotation) throws XMLParserException {
        final DocumentParser documentParser = new DocumentParser(inputSteam);
        documentParser.parserDom();
        return parserToObject(c, documentParser.getRoot(), useAnnotation);
    }

    /**
     * 解析xml流串到c的实例list
     *
     * @param c             解析目标类
     * @param str           解析字符串
     * @param useAnnotation 是否使用注解
     * @param <T>
     * @return
     * @throws XMLParserException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static <T> List<T> parserToList(Class<T> c, String str, boolean useAnnotation) throws XMLParserException {
        return parserToList(c, new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), useAnnotation);
    }

    /**
     * 解析xml流串到c的实例list
     *
     * @param c             解析目标类
     * @param inputSteam    输入流
     * @param useAnnotation 是否使用注解
     * @param <T>
     * @return
     * @throws XMLParserException
     */
    public static <T> List<T> parserToList(Class<T> c, InputStream inputSteam, boolean useAnnotation) throws XMLParserException {
        final DocumentParser documentParser = new DocumentParser(inputSteam);
        documentParser.parserDom();
        return parserToList(c, (NodeList) documentParser.getRoot(), useAnnotation);
    }


    private static <T> T parserToObject(Class<T> c, Node node, boolean useAnnotation) throws XMLParserException {
        if (null == node) {
            return null;
        }
        T t = null;
        try {
            final Constructor constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            t = (T) constructor.newInstance();
            final Field[] fields = c.getDeclaredFields();
            String filedName = null;
            for (final Field field : fields) {
                field.setAccessible(true);
                if (field.isEnumConstant() || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }


                filedName = getFieldName(field, useAnnotation);

                if (!List.class.isAssignableFrom(field.getType())) {
                    setField(t, field, node, useAnnotation, filedName);
                } else {
                    if (field.getGenericType() instanceof ParameterizedType) {
                        final ParameterizedType pt = (ParameterizedType) field.getGenericType();
                        final Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
                        final List<?> list = parserToList(genericClazz, getNodeList(node, filedName), useAnnotation);
                        field.set(t, list);
                    } else {
                        Log.i(TAG, field.getName() + " require have generic");
                    }
                }
            }
        } catch (Exception e) {
            throw new XMLParserException(c, "constructor is not accessible,must have zero-argument constructor", e);
        }
        return t;

    }

    private static <T> List<T> parserToList(Class<T> c, NodeList nodeList, boolean useAnnotation) throws XMLParserException {
        if (null == nodeList) {
            return new ArrayList<>();
        }
        final List<T> list = new ArrayList<>();
        T t = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            t = parserToObject(c, nodeList.item(i), useAnnotation);
            if (t != null)
                list.add(t);
        }
        return list;
    }

    private static <T> void setField(T t, Field field, Node node, boolean useAnnotation, String filedName) throws XMLParserException {
        field.setAccessible(true);
        Object value = null;
        try {
            if (isBaseClass(field.getType())) {
                String valueStr = null;
                if (field.isAnnotationPresent(Attribute.class)) {
                    valueStr = getNodeAttr(node, filedName);
                } else {
                    valueStr = getNodeValue(node, filedName);
                }


                if (field.getType() == String.class) {
                    value = valueStr;
                } else if (field.getType() == Integer.class || field.getType() == int.class) {
                    value = parseInt(valueStr, 0);
                } else if (field.getType() == Long.class || field.getType() == long.class) {
                    value = parseLong(valueStr, 0L);
                } else if (field.getType() == Double.class || field.getType() == double.class) {
                    value = parseDouble(valueStr, 0.0d);
                } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                    value = parseBoolean(valueStr, false);
                } else if (field.getType() == Float.class || field.getType() == float.class) {
                    value = parseFloat(valueStr, 0.0f);
                }
            } else {
                value = parserToObject(field.getType(), getNode(node, filedName), useAnnotation);
            }
            field.set(t, value);
        } catch (IllegalArgumentException e) {
            throw new XMLParserException(t.getClass(), field, "Illegal Argument value=" + value, e);
        } catch (IllegalAccessException e) {
            throw new XMLParserException(t.getClass(), field, "Illegal Access " + t, e);
        }
    }


    private static Node getNode(Node node, String nodeName) {
        return DocumentParser.getNode(node, nodeName);
    }

    private static NodeList getNodeList(Node node, String nodeName) {
        return DocumentParser.getNodeList(node, nodeName);
    }

    private static String getNodeAttr(Node node, String attrName) {
        return DocumentParser.getNodeAttr(node, attrName);
    }

    public static String getNodeValue(Node node, String nodeName) {
        return DocumentParser.getNodeValue(node, nodeName);
    }
}
