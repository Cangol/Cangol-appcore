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

/**
 * @author Cangol
 */

import android.os.Build
import android.support.annotation.RequiresApi
import mobi.cangol.mobile.logging.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object StringUtils {
    /**
     * bytes to String
     *
     * @param value
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @JvmStatic
    fun byte2String(value: ByteArray?): String {
        return if (value == null) "" else String(value, StandardCharsets.UTF_8)
    }

    /**
     * byte to hex
     *
     * @param b
     * @return
     */
    @JvmStatic
    fun byte2hex(b: ByteArray): String {
        val hs = StringBuilder()
        var stmp = ""
        for (n in b.indices) {
            stmp = Integer.toHexString(b[n].toInt() and 0XFF)
            if (stmp.length == 1) {
                hs.append('0').append(stmp)
            } else {
                hs.append(stmp)
            }

        }
        return hs.toString().toUpperCase()
    }

    /**
     * 反转
     *
     * @param value
     * @return
     */
    @JvmStatic
    fun reverse(value: String): String {
        val sb = StringBuilder()
        for (i in value.length - 1 downTo 0) {
            sb.append(value[i])
        }
        return sb.toString()
    }

    /**
     * 格式化为中文数量
     *
     * @param num
     * @return
     */
    @JvmStatic
    fun formatZhNum(num: Long): String {
        val yi = num / (10000 * 10000)
        val qianwan = num % (10000 * 10000) / (1000 * 10000)
        val wan = num / 10000
        val qian = num % 10000 / 1000

        return if (yi > 0) {
            if (qianwan > 0) {
                yi.toString() + "." + qianwan + "亿"
            } else {
                yi.toString() + "亿"
            }
        } else if (wan > 0) {
            if (qian > 0) {
                wan.toString() + "." + qian + "万"
            } else {
                wan.toString() + "万"
            }
        } else {
            "" + num
        }
    }

    /**
     * format speed   /s
     *
     * @param value
     * @return
     */
    @JvmStatic
    fun formatSpeed(value: Long): String {
        return formatSize(value) + "/s"
    }

    /**
     * format size
     *
     * @param value
     * @return
     */
    @JvmStatic
    fun formatSize(value: Long): String {

        val k = value.toDouble() / 1024
        if (k == 0.0) {
            return String.format("%dB", value)
        }

        val m = k / 1024
        if (m < 1) {
            return String.format("%.1fK", k)
        }

        val g = m / 1024
        return if (g < 1) {
            String.format("%.1fM", m)
        } else String.format("%.1fG", g)

    }

    /**
     * format time
     *
     * @param second
     * @return
     */
    @JvmStatic
    fun formatTime(second: Int): String {

        val hh = second / 3600
        val mm = second % 3600 / 60
        val ss = second % 60

        return if (0 != hh) {
            String.format("%02d:%02d:%02d", hh, mm, ss)
        } else {
            String.format("%02d:%02d", mm, ss)
        }
    }

    /**
     * MD5 s
     *
     * @param s
     * @return
     */
    @JvmStatic
    fun md5(s: String): String? {
        val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
        try {
            val strTemp = s.toByteArray()
            //MessageDigest md5
            val mdTemp = MessageDigest.getInstance("MD5")
            mdTemp.update(strTemp)
            val md = mdTemp.digest()
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {
                val b = md[i]
                str[k++] = hexDigits[b.toInt() shr 4 and 0xf]
                str[k++] = hexDigits[b.toInt() and 0xf]
            }
            return String(str)
        } catch (e: Exception) {
            Log.d(e.message)
            return null
        }

    }

    /**
     * MD5 byte[]
     *
     * @param bytes
     * @return
     */
    @JvmStatic
    fun md5(bytes: ByteArray): String? {
        val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
        try {
            //MessageDigest md5
            val mdTemp = MessageDigest.getInstance("MD5")
            mdTemp.update(bytes)
            val md = mdTemp.digest()
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {
                val b = md[i]
                str[k++] = hexDigits[b.toInt() shr 4 and 0xf]
                str[k++] = hexDigits[b.toInt() and 0xf]
            }
            return String(str)
        } catch (e: Exception) {
            Log.d(e.message)
            return null
        }

    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun isBlank(str: String?): Boolean {
        if (str == null || str.isEmpty()) {
            return true
        }
        for (element in str) {
            if (!Character.isWhitespace(element)) {
                return false
            }
        }
        return true
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun isEmpty(str: String?): Boolean {
        return str == null || str.isEmpty()
    }

    /**
     * Returns false if the string is null or 0-length.
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun isNotEmpty(str: String): Boolean {
        return !StringUtils.isEmpty(str)
    }

    /**
     * Returns true if the string is null or ''.
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun isNotBlank(str: String): Boolean {
        return !StringUtils.isBlank(str)
    }

    /**
     * Returns string if the string is null or ''.
     *
     * @return
     */
    @JvmStatic
    fun null2Zero(str: String?): String {
        return if (str == null || str == "") {
            "0"
        } else {
            str
        }
    }

    /**
     * remove TableSpace
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun replaceWhiteSpace(str: String): String {
        return str.replace("\n".toRegex(), "")
    }

    /**
     * remove TableSpace
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun replaceTableSpace(str: String): String {
        return str.replace(" ".toRegex(), "").replace("\t".toRegex(), "")
    }

    /**
     * remove front Whitespace
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun trimForFront(str: String): String {
        val sb = StringBuilder()
        var first = false
        var aa: Char
        val length = str.length
        for (i in 0 until length) {
            aa = str[i]
            if (!(!first && aa == '\t')) {
                first = true
                sb.append(aa)
            }
        }

        return sb.toString()
    }

    /**
     * str  the String to be trimmed, may be null
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun trimToEmpty(str: String?): String {
        return str?.trim { it <= ' ' } ?: ""
    }

    /**
     * repalce all Whitespace
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun trimAllWhitespace(str: String): String {
        return str.replace(" ".toRegex(), "").replace("\n".toRegex(), "").replace("\t".toRegex(), "")
    }

    /**
     * strip Start
     *
     * @param str
     * @param stripChars
     * @return
     */
    @JvmStatic
    fun stripStart(str: String?, stripChars: String?): String? {
        if (str == null || str.isEmpty()) {
            return str
        }
        var start = 0
        if (stripChars == null) {
            while (start != str.length && Character.isWhitespace(str[start])) {
                start++
            }
        } else if (stripChars.isEmpty()) {
            return str
        } else {
            while (start != str.length && stripChars.indexOf(str[start], 0, true) != -1) {
                start++
            }
        }
        return str.substring(start)
    }

    /**
     * @param str
     * @param stripChars
     * @return
     */
    @JvmStatic
    fun stripEnd(str: String?, stripChars: String?): String? {
        if (str == null || str.isEmpty()) {
            return str
        }
        var end = str.length
        if (stripChars == null) {
            while (end != 0 && Character.isWhitespace(str[end - 1])) {
                end--
            }
        } else if (stripChars.isEmpty()) {
            return str
        } else {
            while (end != 0 && stripChars.indexOf(str[end - 1], 0, true) != -1) {
                end--
            }
        }
        return str.substring(0, end)
    }

    /**
     * strip start|end
     *
     * @param str
     * @param stripChars
     * @return
     */
    @JvmStatic
    fun strip(str: String?, stripChars: String? = null): String? {
        var str = str
        if (isEmpty(str)) {
            return str
        }
        str = stripStart(str, stripChars)
        return stripEnd(str, stripChars)
    }

    /**
     * strip
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun strip(str: String): String ?{
        return strip(str, null)
    }
    /**
     * isNumeric
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun isNumeric(str: String?): Boolean {
        if (str == null) {
            return false
        }
        val sz = str.length
        for (i in 0 until sz) {
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    }

    /**
     * isNumericSpace
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun isNumericSpace(str: String?): Boolean {
        if (str == null) {
            return false
        }
        val sz = str.length
        for (i in 0 until sz) {
            if (!Character.isDigit(str[i]) && str[i] != ' ') {
                return false
            }
        }
        return true
    }

    /**
     * byte[] xor int
     *
     * @param data
     * @param xor
     * @return
     */
    @JvmStatic
    fun byteXorInt(data: ByteArray, xor: Int): ByteArray {
        var data2 = ByteArray(4)
        var i = 0
        while (i < data.size) {
            data2[0] = data[i / 4 + 0]
            data2[1] = data[i / 4 + 1]
            data2[2] = data[i / 4 + 2]
            data2[3] = data[i / 4 + 3]
            data2 = int2byteArray(byteArray2int(data2) xor xor)
            data[i / 4 + 0] = data2[0]
            data[i / 4 + 1] = data2[1]
            data[i / 4 + 2] = data2[2]
            data[i / 4 + 3] = data2[3]
            i = i + 4
        }
        return data
    }

    /**
     * byte[] to int
     *
     * @param b
     * @return
     */
    @JvmStatic
    fun byteArray2int(b: ByteArray): Int {
        val a = ByteArray(4)
        var i = a.size - 1
        var j = b.size - 1
        while (i >= 0) {//从b的尾部(即int值的低位)开始copy数据
            if (j >= 0) {
                a[i] = b[j]
            } else {
                a[i] = 0//如果b.length不足4,则将高位补0
            }
            i--
            j--

        }
        val v0 = a[0].toInt() and 0xff shl 24//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        val v1 = a[1].toInt() and 0xff shl 16
        val v2 = a[2].toInt() and 0xff shl 8
        val v3 = a[3].toInt() and 0xff
        return v0 + v1 + v2 + v3
    }

    /**
     * int to byte[]
     *
     * @param num
     * @return
     */
    @JvmStatic
    fun int2byteArray(num: Int): ByteArray {
        val result = ByteArray(4)
        result[0] = num.ushr(24).toByte()
        result[1] = num.ushr(16).toByte()
        result[2] = num.ushr(8).toByte()
        result[3] = num.toByte()
        return result
    }

    /**
     * @param str
     * @return
     */
    @JvmStatic
    fun null2Empty(str: String?): String {
        return str ?: ""
    }
}