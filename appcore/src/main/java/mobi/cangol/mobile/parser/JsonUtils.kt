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


import mobi.cangol.mobile.logging.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


/**
 * @author Cangol
 */
object JsonUtils {
    private const val TAG = "JsonUtils"

    /**
     * 转换Object到JSONArray
     *
     * @param <T>
     * @param obj
     * @return
    </T> */
    @JvmStatic
    fun <T> toJSONArray(obj: List<T>?, useAnnotation: Boolean): JSONArray {
        val jsonArray = JSONArray()
        if (obj != null) {
            for (i in obj.indices) {
                if (Converter.isBaseClass((obj[i] as Any).javaClass)) {
                    jsonArray.put(obj[i])
                } else {
                    jsonArray.put(toJSONObject(obj[i], useAnnotation))
                }
            }
        }
        return jsonArray
    }

    /**
     * 转换Object到JSONObject
     *
     * @param <T>
     * @param obj
     * @return
    </T> */
    @JvmStatic
    fun <T> toJSONObject(obj: T, useAnnotation: Boolean): JSONObject? {
        return toJSONObject(obj, useAnnotation, false)
    }

    @JvmStatic
    fun <T> toJSONObject(obj: T, useAnnotation: Boolean, excludeTransient: Boolean): JSONObject? {
        if (obj == null) {
            return null
        }

        if ((obj as Any).javaClass.isAssignableFrom(List::class.java)) {
            return null
        }

        val json = JSONObject()
        val fields = obj.javaClass.declaredFields
        try {
            for (field in fields) {
                field.isAccessible = true
                if (field.isEnumConstant || Modifier.isFinal(field.modifiers)) {
                    continue
                }
                if (excludeTransient && Modifier.isTransient(field.modifiers)) {
                    continue
                }

                val filedName = Converter.getFieldName(field, useAnnotation)
                if (!field.type.javaClass.isAssignableFrom(List::class.java)) {
                    //非集合类型
                    if (Converter.isBaseClass(field.type)) {
                        json.put(filedName, field.get(obj))
                    } else {
                        json.put(filedName, toJSONObject(field.get(obj), useAnnotation))
                    }
                } else {
                    //集合类型
                    if (field.genericType is ParameterizedType) {
                        val list = field.get(obj) as List<*>
                        val jsonArray = JSONArray()
                        if (list != null) {
                            for (i in list.indices) {
                                if (Converter.isBaseClass((list[i] as Any).javaClass)) {
                                    jsonArray.put(list[i])
                                } else {
                                    jsonArray.put(toJSONObject(list[i], useAnnotation))
                                }
                            }
                        }
                        json.put(filedName, jsonArray)
                    } else {
                        Log.d(TAG, field.name + " require have generic")
                    }
                }

            }
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }

        return json
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
    </T> */
    @Throws(JSONParserException::class)
    @JvmStatic
    fun <T> parserToObject(c: Class<T>, str: String?, useAnnotation: Boolean): T? {
        return parserToObject(c, str, useAnnotation, false)
    }

    @Throws(JSONParserException::class)
    @JvmStatic
    fun <T> parserToObject(c: Class<T>, str: String?, useAnnotation: Boolean, excludeTransient: Boolean): T? {
        require(!(null == str || "" == str)) { "str=null" }
        val json = formatJson(str)
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(json)
        } catch (e: JSONException) {
            throw JSONParserException(e.message!!, e)
        }

        return parserToObject(c, jsonObject, useAnnotation, excludeTransient)
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
    </T> */
    @Throws(JSONParserException::class)
    @JvmStatic
    fun <T> parserToList(c: Class<T>, str: String?, useAnnotation: Boolean): List<T> {
        return parserToList(c, str, useAnnotation, false)
    }

    @Throws(JSONParserException::class)
    @JvmStatic
    fun <T> parserToList(c: Class<T>, str: String?, useAnnotation: Boolean, excludeTransient: Boolean): List<T> {
        require(!(null == str || "" == str)) { "str=null" }
        val json = formatJson(str)
        var jsonArray: JSONArray? = null
        try {
            jsonArray = JSONArray(json)
        } catch (e: JSONException) {
            throw JSONParserException(e.message!!, e)
        }

        return parserToList(c, jsonArray, useAnnotation, excludeTransient)
    }

    /**
     * @param c
     * @param urlStr
     * @param <T>
     * @return
     * @throws JSONParserException
    </T> */
    @Throws(JSONParserException::class)
    @JvmStatic
    fun <T> parserToObjectByUrl(c: Class<T>, urlStr: String?): T? {
        require(!(null == urlStr || "" == urlStr)) { "urlStr=null" }
        var url: URL? = null
        var urlConnection: HttpURLConnection? = null
        var json: String? = null
        var jsonObject: JSONObject? = null
        try {
            url = URL(urlStr)
            urlConnection = url.openConnection() as HttpURLConnection
            json = formatJson(inputStreamTOString(urlConnection.inputStream))
            jsonObject = JSONObject(json)
        } catch (e: Exception) {
            throw JSONParserException(e.message!!, e)
        }

        return parserToObject(c, jsonObject, true)
    }

    @Throws(Exception::class)
    private fun inputStreamTOString(inputStream: InputStream): String {
        val outStream = ByteArrayOutputStream()
        val data = ByteArray(4096)
        var end = true
        while (end) {
            var count = inputStream.read(data, 0, 4096)
            end = if (count > 0) {
                outStream.write(data, 0, count)
                true
            } else {
                false
            }
        }
        return String(outStream.toByteArray())
    }

    @Throws(JSONParserException::class)
    @JvmStatic
    fun <T> parserToObject(c: Class<T>, jsonObject: JSONObject?, useAnnotation: Boolean): T? {
        return parserToObject(c, jsonObject, useAnnotation, false)
    }

    @Throws(JSONParserException::class)
    @JvmStatic
    fun <T> parserToObject(c: Class<T>, jsonObject: JSONObject?, useAnnotation: Boolean, excludeTransient: Boolean): T? {
        if (jsonObject == null) {
            return null
        }
        var t: T? = null
        try {
            val typeMap = HashMap<String, Class<*>>()
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
                if (excludeTransient && Modifier.isTransient(field.modifiers)) {
                    continue
                }
                filedName = Converter.getFieldName(field, useAnnotation)
                if (!field.type.javaClass.isAssignableFrom(List::class.java)) {
                    val filedClass = if (field.genericType is TypeVariable<*>) {
                        val aType = field.genericType as TypeVariable<*>
                        typeMap[aType.name]
                    } else {
                        field.type
                    }

                    if (jsonObject.has(filedName))
                        field.set(t, getValueFromJson(t, filedClass, filedName, jsonObject, useAnnotation))
                } else {
                    if (field.genericType is ParameterizedType) {
                        val pt = field.genericType as ParameterizedType
                        val genericClazz = pt.actualTypeArguments[0] as Class<*>
                        val list = parserToList(genericClazz, getJSONArray(jsonObject, filedName), useAnnotation)
                        field.set(t, list)
                    } else {
                        Log.i(TAG, field.name + " require have generic")
                    }
                }
            }
        } catch (e: Exception) {
            throw JSONParserException(c, "constructor is not accessible,must have zero-argument constructor", e)
        }

        return t
    }

    @Throws(JSONParserException::class)
    @JvmStatic
    fun <T> parserToList(c: Class<T>, jsonArray: JSONArray?, useAnnotation: Boolean): List<T> {
        return parserToList(c, jsonArray, useAnnotation, false)
    }

    @Throws(JSONParserException::class)
    @JvmStatic
    fun <T> parserToList(c: Class<T>, jsonArray: JSONArray?, useAnnotation: Boolean, excludeTransient: Boolean): List<T> {
        if (jsonArray == null) {
            return ArrayList()
        }
        val list = ArrayList<T>()
        var t: T? = null
        for (i in 0 until jsonArray.length()) {
            try {
                t = if (jsonArray.get(i) is JSONObject) {
                    parserToObject(c, jsonArray.getJSONObject(i), useAnnotation, excludeTransient)
                } else if (jsonArray.get(i) != null) {
                    jsonArray.get(i) as T
                } else {
                    continue
                }
                list.add(t!!)
            } catch (e: JSONException) {
                throw JSONParserException("Illegal Argument value=", e)
            }

        }
        return list
    }

    private fun <T> getValueFromJson(t: T, fieldClass: Class<*>?, key: String?, jsonObject: JSONObject, useAnnotation: Boolean): Any? {
        var value: Any? = null
        try {
            if (Converter.isBaseClass(fieldClass!!)) {
                if (fieldClass == String::class.java) {
                    value = getString(jsonObject, key)
                } else if (fieldClass == Int::class.java || fieldClass == Int::class.javaPrimitiveType) {
                    value = getInt(jsonObject, key, 0)
                } else if (fieldClass == Long::class.java || fieldClass == Long::class.javaPrimitiveType) {
                    value = getLong(jsonObject, key, 0L)
                } else if (fieldClass == Double::class.java || fieldClass == Double::class.javaPrimitiveType) {
                    value = getDouble(jsonObject, key, 0.0)
                } else if (fieldClass == Boolean::class.java || fieldClass == Boolean::class.javaPrimitiveType) {
                    value = getBoolean(jsonObject, key, false)
                } else if (fieldClass == Float::class.java || fieldClass == Float::class.javaPrimitiveType) {
                    value = getFloat(jsonObject, key, 0.0f)
                }
            } else {
                value = parserToObject(fieldClass, getJSONObject(jsonObject, key), useAnnotation)
            }
        } catch (e: IllegalArgumentException) {
            throw JSONParserException((t as Any).javaClass, "Illegal Argument value=" + value!!, e)
        } finally {
            return value
        }
    }

    @JvmStatic
    fun getType(raw: Class<*>, vararg args: Type): ParameterizedType {
        return object : ParameterizedType {
            override fun getRawType(): Type {
                return raw
            }

            override fun getActualTypeArguments(): Array<out Type> {
                return args
            }

            override fun getOwnerType(): Type? {
                return null
            }
        }
    }

    @JvmStatic
    fun formatJson(json: String?): String? {
        var json = json
        if (null != json && json.startsWith("{\"\"")) {
            json = json.replaceFirst("\"\"".toRegex(), "\"")
        }
        return json
    }

    @Throws(JSONException::class)
    @JvmStatic
    fun formatJSONObject(json: String?): JSONObject {
        var json = json
        if (null != json && json.startsWith("{\"\"")) {
            json = json.replaceFirst("\"\"".toRegex(), "\"")
        }
        return JSONObject(json)
    }

    @JvmStatic
    fun getInt(obj: JSONObject, key: String?, defaultValue: Int): Int {
        return try {
            if (obj.isNull(key)) {
                defaultValue
            } else {
                obj.getInt(key)
            }
        } catch (e: JSONException) {
            defaultValue
        }

    }

    @JvmStatic
    fun getLong(obj: JSONObject, key: String?, defaultValue: Long): Long {
        return try {
            if (obj.isNull(key)) {
                defaultValue
            } else {
                obj.getLong(key)
            }
        } catch (e: JSONException) {
            defaultValue
        }

    }

    @JvmStatic
    fun getBoolean(obj: JSONObject, key: String?, defaultValue: Boolean): Boolean {
        return try {
            if (obj.isNull(key)) {
                defaultValue
            } else {
                obj.getBoolean(key)
            }
        } catch (e: JSONException) {
            defaultValue
        }

    }

    @JvmStatic
    fun getFloat(obj: JSONObject, key: String?, defaultValue: Float): Float {
        var floatStr: String? = null
        return try {
            floatStr = "" + obj.get(key)
            java.lang.Float.valueOf(floatStr)
        } catch (e: JSONException) {
            defaultValue
        }

    }

    @JvmStatic
    fun getDouble(obj: JSONObject, key: String?, defaultValue: Double): Double {
        return try {
            if (obj.isNull(key)) {
                defaultValue
            } else {
                obj.getDouble(key)
            }
        } catch (e: JSONException) {
            defaultValue
        }

    }

    @JvmStatic
    fun getString(obj: JSONObject, key: String?): String? {
        return try {
            if (obj.isNull(key)) {
                null
            } else {
                obj.getString(key)
            }
        } catch (e: JSONException) {
            null
        }

    }

    @JvmStatic
    fun getString(obj: JSONObject, key: String, defaultValue: String): String {
        return try {
            if (obj.isNull(key)) {
                defaultValue
            } else {
                obj.getString(key)
            }
        } catch (e: JSONException) {
            defaultValue
        }

    }

    @JvmStatic
    fun getObject(obj: JSONObject, key: String): Any? {
        return try {
            if (obj.isNull(key)) {
                null
            } else {
                obj.get(key)
            }
        } catch (e: JSONException) {
            null
        }

    }

    @JvmStatic
    fun getObject(json: String): JSONObject? {
        return try {
            JSONObject(formatJson(json))
        } catch (e: JSONException) {
            null
        }

    }

    @JvmStatic
    fun getJSONObject(obj: JSONObject, key: String?): JSONObject? {
        return try {
            if (obj.isNull(key)) {
                null
            } else {
                obj.getJSONObject(key)
            }
        } catch (e: JSONException) {
            null
        }

    }

    @JvmStatic
    fun getJSONArray(obj: JSONObject, key: String?): JSONArray? {
        return try {
            if (obj.isNull(key)) {
                null
            } else {
                obj.getJSONArray(key)
            }
        } catch (e: JSONException) {
            null
        }

    }
}
