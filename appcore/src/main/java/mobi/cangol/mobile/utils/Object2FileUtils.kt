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
package mobi.cangol.mobile.utils

import mobi.cangol.mobile.logging.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.*

/**
 * @author Cangol
 */
object Object2FileUtils {
    /**
     * 写入json对象到文件
     *
     * @param jsonObject
     * @param objPath
     */
    @JvmStatic
    fun writeJSONObject2File(jsonObject: JSONObject, objPath: String) {
        try {
            writeString2File(jsonObject.toString(), FileOutputStream(File(objPath)))
        } catch (e: FileNotFoundException) {
            Log.d(e.message)
        }

    }

    /**
     * 写入json对象到文件
     *
     * @param jsonArray
     * @param objPath
     */
    @JvmStatic
    fun writeJSONArray2File(jsonArray: JSONArray, objPath: String) {
        try {
            writeString2File(jsonArray.toString(), FileOutputStream(File(objPath)))
        } catch (e: FileNotFoundException) {
            Log.d(e.message)
        }

    }

    /**
     * 读取文件到json对象
     *
     * @param jsonFile
     * @return
     */
    @JvmStatic
    fun readFile2JSONArray(jsonFile: File): JSONArray? {
        var jsonArray: JSONArray? = null
        try {
            jsonArray = JSONArray(readString(FileInputStream(jsonFile)))
        } catch (e: Exception) {
            Log.d(e.message)
        }

        return jsonArray
    }

    /**
     * 读取文件到json对象
     *
     * @param jsonFile
     * @return
     */
    @JvmStatic
    fun readFile2JSONObject(jsonFile: File): JSONObject? {
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(readString(FileInputStream(jsonFile)))
        } catch (e: Exception) {
            Log.d(e.message)
        }

        return jsonObject
    }

    /**
     * 写入String到输出流
     *
     * @param str
     * @param os
     */
    @JvmStatic
    fun writeString2File(str: String, os: OutputStream) {
        try {
            val buffer = str.toByteArray()
            os.write(buffer)
            os.flush()
        } catch (e: IOException) {
            Log.d(e.message)
        } finally {
                try {
                    os.close()
                } catch (e: IOException) {
                    Log.d(e.message)
                }
        }
    }

    /**
     * 读取输入流到String
     * @param is
     * @return
     */
    @JvmStatic
    fun readString(inputStream: InputStream?): String? {
        var content: String? = null
        try {
            val buffer = ByteArray(inputStream!!.available())
            if (inputStream.read(buffer) != -1) {
                content = String(buffer)
            }
        } catch (e: Exception) {
            Log.d(e.message)
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    Log.d(e.message)
                }

            }
        }
        return content
    }

    /**
     * 将object对象写入输出流
     *
     * @param obj
     * @param out
     */
    @JvmStatic
    fun writeObject(obj: Serializable, out: OutputStream) {
        var bos: BufferedOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            bos = BufferedOutputStream(out)
            oos = ObjectOutputStream(bos)
            oos.writeObject(obj)
        } catch (e: Exception) {
            Log.d(e.message)
        } finally {
            try {
                oos?.close()
            } catch (e: IOException) {
                Log.d(e.message)
            }

            try {
                bos?.close()
            } catch (e: IOException) {
                Log.d(e.message)
            }

        }
    }

    /**
     * 从输入流读取对象
     *
     * @param is
     * @return
     */
    @JvmStatic
    fun readObject(inputStream: InputStream): Serializable? {
        var obj: Any? = null
        try {
            obj = ObjectInputStream(BufferedInputStream(inputStream)).readObject()
        } catch (e: Exception) {
            Log.d(e.message)
        }

        return obj as Serializable?
    }

    /**
     * 将对象写入文件
     *
     * @param obj
     * @param objPath
     */
    @JvmStatic
    fun writeObject(obj: Serializable, objPath: String) {

        val file = File(objPath)
        if (file.exists()) {
            file.delete()
        }
        try {
            writeObject(obj, FileOutputStream(file))
        } catch (e: FileNotFoundException) {
            Log.d(e.message)
        }

    }

    /**
     * 从文件读取对象
     *
     * @param file
     * @return
     */
    @JvmStatic
    fun readObject(file: File): Serializable? {
        if (!file.exists() || file.length() == 0L) {
            return null
        }
        var obj: Any? = null
        try {
            obj = readObject(FileInputStream(file))
        } catch (e: FileNotFoundException) {
            Log.d(e.message)
        }

        return obj as Serializable?
    }
}
