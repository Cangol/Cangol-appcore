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

package mobi.cangol.mobile.parser

import mobi.cangol.mobile.utils.StringUtils
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by xuewu.wei on 2016/5/31.
 */
object Converter {

    @Throws(XMLParserException::class)
    @JvmStatic fun <T> parserXml(c: Class<T>, str: String?, useAnnotation: Boolean): T? {
        return XmlUtils.parserToObject(c, str, useAnnotation)
    }

    @Throws(XMLParserException::class)
    @JvmStatic fun <T> parserXmlList(c: Class<T>, str: String, useAnnotation: Boolean): List<T>? {
        return XmlUtils.parserToList(c, str, useAnnotation)
    }

    @Throws(JSONParserException::class)
    @JvmStatic fun <T> parserJson(c: Class<T>, str: String?, useAnnotation: Boolean): T ?{
        return JsonUtils.parserToObject(c, str, useAnnotation)
    }

    @Throws(JSONParserException::class)
    @JvmStatic fun <T> parserJsonList(c: Class<T>, str: String?, useAnnotation: Boolean): List<T> {
        return JsonUtils.parserToList(c, str, useAnnotation)
    }

    /**
     * 解析Int
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @JvmStatic fun parseInt(str: String?, defaultValue: Int): Int {
        return try {
            Integer.parseInt(str)
        } catch (e: Exception) {
            defaultValue
        }

    }

    /**
     * 解析Double
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @JvmStatic fun parseDouble(str: String?, defaultValue: Double): Double {
        return try {
            java.lang.Double.parseDouble(str)
        } catch (e: Exception) {
            defaultValue
        }

    }

    /**
     * 解析Boolean
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @JvmStatic fun parseBoolean(str: String?, defaultValue: Boolean): Boolean {
        return try {
            java.lang.Boolean.parseBoolean(str)
        } catch (e: Exception) {
            defaultValue
        }

    }

    /**
     * 解析Long
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @JvmStatic fun parseLong(str: String?, defaultValue: Long): Long {
        return try {
            java.lang.Long.parseLong(str)
        } catch (e: Exception) {
            defaultValue
        }

    }

    /**
     * 解析Float
     *
     * @param str
     * @param defaultValue
     * @return
     */
    @JvmStatic fun parseFloat(str: String?, defaultValue: Float): Float {
        return try {
            java.lang.Float.parseFloat(str)
        } catch (e: Exception) {
            defaultValue
        }

    }

     @JvmStatic fun isTransient(clz: Class<*>): Boolean {
        return Modifier.isTransient(clz.modifiers)
    }

    /**
     * 判断是否是基类
     *
     * @param clz
     * @return
     */
     @JvmStatic fun isBaseClass(clz: Class<*>): Boolean {
        return isWrapClass(clz) || clz.isPrimitive || clz == String::class.java
    }

    /**
     * 判断是否是包装类
     *
     * @param clz
     * @return
     */
     @JvmStatic fun isWrapClass(clz: Class<*>): Boolean {
        return try {
            (clz.getField("TYPE").get(null) as Class<*>).isPrimitive
        } catch (e: Exception) {
            false
        }

    }

    /**
     * 获取字段要解析|转换的名称
     *
     * @param field
     * @param useAnnotation
     * @return
     */
     @JvmStatic fun getFieldName(field: Field, useAnnotation: Boolean): String {
        var filedName: String? = null
        if (useAnnotation) {
            filedName = if (field.isAnnotationPresent(Attribute::class.java)) {
                val attr = field.getAnnotation(Attribute::class.java)
                if (StringUtils.isEmpty(attr.value)) field.name else attr.value
            } else if (field.isAnnotationPresent(Element::class.java)) {
                val element = field.getAnnotation(Element::class.java)
                if (StringUtils.isEmpty(element.value)) field.name else element.value
            } else if (field.isAnnotationPresent(ElementList::class.java)) {
                val elementList = field.getAnnotation(ElementList::class.java)
                if (StringUtils.isEmpty(elementList.value)) field.name else elementList.value
            } else {
                field.name
            }
        } else {
            filedName = field.name
        }
        return filedName
    }
}
