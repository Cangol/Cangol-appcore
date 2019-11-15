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
package mobi.cangol.mobile.parser

import android.util.Log
import android.util.Xml
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xmlpull.v1.XmlSerializer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.*

object XmlUtils {
    private const val UTF_8 = "UTF-8"
    private const val TAG = "XmlUtils"
    /**
     * 转换Object到xml
     *
     * @param obj
     * @param useAnnotation
     * @return
     */
    @JvmStatic
    fun toXml(obj: Any, useAnnotation: Boolean): String? {
        val baos = ByteArrayOutputStream()
        val serializer = Xml.newSerializer()
        var result: String? = null
        try {
            serializer.setOutput(baos, UTF_8)
            serializer.startDocument(UTF_8, true)
            toXml(serializer, obj, useAnnotation)
            serializer.endDocument()
            baos.close()
            result = baos.toString(UTF_8)
        } catch (e: IOException) {
            Log.d(TAG, e.message)
        }

        return result
    }

    private fun toXml(serializer: XmlSerializer, obj: Any, useAnnotation: Boolean) {
        try {
            serializer.startTag(null, obj.javaClass.simpleName)
            val fields = obj.javaClass.declaredFields
            for (field in fields) {
                field.isAccessible = true
                if (field.isEnumConstant || Modifier.isFinal(field.modifiers)) {
                    continue
                }
                val filedName = Converter.getFieldName(field, useAnnotation)

                if (!field.type.javaClass.isAssignableFrom(List::class.java)) {
                    //非集合类型
                    if (Converter.isBaseClass(field.type)) {
                        if (field.isAnnotationPresent(Attribute::class.java)) {
                            serializer.attribute(null, filedName, (if (field.get(obj) == null) "" else field.get(obj)).toString())
                        } else {
                            serializer.startTag(null, filedName)
                            serializer.text((if (field.get(obj) == null) "" else field.get(obj)).toString())
                            serializer.endTag(null, filedName)
                        }
                    } else {
                        toXml(serializer, field.get(obj), useAnnotation)
                    }
                } else {
                    //集合类型
                    if (field.genericType is ParameterizedType) {
                        val list = field.get(obj) as List<*>
                        if (list != null) {
                            for (i in list.indices) {
                                list[i]?.let { toXml(serializer, it, useAnnotation) }
                            }
                        }
                    } else {
                        Log.i(TAG, field.name + " require have generic")
                    }
                }
            }
            serializer.endTag(null, obj.javaClass.simpleName)
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }

    }


    /*************以下开始是解析XML */
    /**
     * 解析xml流串到c的实例
     *
     * @param c             解析目标类
     * @param str           解析字符串
     * @param useAnnotation 是否使用注解
     * @param <T>
     * @return
     * @throws XMLParserException
    </T> */
    @Throws(XMLParserException::class)
    @JvmStatic
    fun <T> parserToObject(c: Class<T>, str: String?, useAnnotation: Boolean): T? {
        return parserToObject(c, ByteArrayInputStream(str?.toByteArray()), useAnnotation)
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
    </T> */
    @Throws(XMLParserException::class)
    @JvmStatic
    fun <T> parserToObject(c: Class<T>, inputSteam: InputStream, useAnnotation: Boolean): T? {
        return parserToObject(c,  DocumentParser.parserDom(inputSteam), useAnnotation)
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
    </T> */
    @Throws(XMLParserException::class)
    @JvmStatic
    fun <T> parserToList(c: Class<T>, str: String, useAnnotation: Boolean): List<T> {
        return parserToList(c, ByteArrayInputStream(str.toByteArray()), useAnnotation)
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
    </T> */
    @Throws(XMLParserException::class)
    @JvmStatic
    fun <T> parserToList(c: Class<T>, inputSteam: InputStream, useAnnotation: Boolean): List<T> {
        return parserToList(c, DocumentParser.parserDom(inputSteam) as NodeList, useAnnotation)
    }


    @Throws(XMLParserException::class)
    private fun <T> parserToObject(c: Class<T>, node: Node?, useAnnotation: Boolean): T? {
        if (null == node) {
            return null
        }
        var t: T? = null
        try {
            val constructor = c.getDeclaredConstructor()
            constructor.isAccessible = true
            t = constructor.newInstance() as T
            val fields = c.declaredFields
            var filedName: String? = null
            for (field in fields) {
                field.isAccessible = true
                if (field.isEnumConstant || Modifier.isFinal(field.modifiers)) {
                    continue
                }


                filedName = Converter.getFieldName(field, useAnnotation)

                if (!field.type.javaClass.isAssignableFrom(List::class.java)) {
                    setField(t, field, node, useAnnotation, filedName)
                } else {
                    if (field.genericType is ParameterizedType) {
                        val pt = field.genericType as ParameterizedType
                        val genericClazz = pt.actualTypeArguments[0] as Class<*>
                        val list = parserToList(genericClazz, getNodeList(node, filedName), useAnnotation)
                        field.set(t, list)
                    } else {
                        Log.i(TAG, field.name + " require have generic")
                    }
                }
            }
        } catch (e: Exception) {
            throw XMLParserException(c, "constructor is not accessible,must have zero-argument constructor", e)
        }

        return t

    }

    @Throws(XMLParserException::class)
    private fun <T> parserToList(c: Class<T>, nodeList: NodeList?, useAnnotation: Boolean): List<T> {
        if (null == nodeList) {
            return ArrayList()
        }
        val list = ArrayList<T>()
        var t: T? = null
        for (i in 0 until nodeList.length) {
            t = parserToObject(c, nodeList.item(i), useAnnotation)
            if (t != null)
                list.add(t)
        }
        return list
    }

    @Throws(XMLParserException::class)
    private fun <T> setField(t: T, field: Field, node: Node, useAnnotation: Boolean, filedName: String?) {
        field.isAccessible = true
        var value: Any? = null
        try {
            if (Converter.isBaseClass(field.type)) {
                var valueStr: String? = null
                if (field.isAnnotationPresent(Attribute::class.java)) {
                    valueStr = getNodeAttr(node, filedName)
                } else {
                    valueStr = getNodeValue(node, filedName)
                }

                if (field.type == String::class.java) {
                    value = valueStr
                } else if (field.type == Int::class.java || field.type == Int::class.javaPrimitiveType) {
                    value = Converter.parseInt(valueStr, 0)
                } else if (field.type == Long::class.java || field.type == Long::class.javaPrimitiveType) {
                    value = Converter.parseLong(valueStr, 0L)
                } else if (field.type == Double::class.java || field.type == Double::class.javaPrimitiveType) {
                    value = Converter.parseDouble(valueStr, 0.0)
                } else if (field.type == Boolean::class.java || field.type == Boolean::class.javaPrimitiveType) {
                    value = Converter.parseBoolean(valueStr, false)
                } else if (field.type == Float::class.java || field.type == Float::class.javaPrimitiveType) {
                    value = Converter.parseFloat(valueStr, 0.0f)
                }
            } else {
                value = parserToObject(field.type, getNode(node, filedName), useAnnotation)
            }
            field.set(t, value)
        } catch (e: IllegalArgumentException) {
            throw XMLParserException((t as Any).javaClass, field, "Illegal Argument value=" + value!!, e)
        } catch (e: IllegalAccessException) {
            throw XMLParserException((t as Any).javaClass, field, "Illegal Access $t", e)
        }
    }


    private fun getNode(node: Node, nodeName: String?): Node? {
        return DocumentParser.getNode(node, nodeName)
    }

    private fun getNodeList(node: Node, nodeName: String?): NodeList {
        return DocumentParser.getNodeList(node, nodeName)
    }

    private fun getNodeAttr(node: Node, attrName: String?): String {
        return DocumentParser.getNodeAttr(node, attrName)
    }

    @JvmStatic
    fun getNodeValue(node: Node, nodeName: String?): String? {
        return DocumentParser.getNodeValue(node, nodeName!!)
    }
}
