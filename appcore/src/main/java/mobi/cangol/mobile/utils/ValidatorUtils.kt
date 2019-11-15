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

@file:JvmName("ValidatorUtils")
package mobi.cangol.mobile.utils

import android.util.Patterns
import okio.JvmStatic
import java.util.regex.Pattern

/**
 * @author Cangol
 */
object ValidatorUtils {

    /**
     * 验证字符串是否不为空
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validateNull(str: String?): Boolean {
        return str != null && "" != str
    }

    /**
     * 验证消息字符串大小(5-140)
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validateContent(str: String?): Boolean {
        return if (str == null || "" == str) {
            false
        } else
            str.length >= 5 && str.length <= 140
    }

    /**
     * 验证昵称格式是否正确(长度5-20,所有单词字符，包括中文，中文算2个字符)
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validateNickname(str: String?): Boolean {
        if (str == null || "" == str) {
            return false
        }

        val rx = "[a-zA-Z0-9]"
        val rx2 = "[\u4e00-\u9fa5]"
        var num = 0
        var flag = false
        for (i in 0 until str.length) {
            val c = str[i]
            if (Pattern.compile(rx).matcher(c + "").matches()) {
                num += 1
            } else if (Pattern.compile(rx2).matcher(c + "").matches()) {
                num += 2
            } else {
                break
            }
        }

        if (num <= 20 && num > 5) {
            flag = true
        }

        return flag
    }

    /**
     * 验证帐号(手机号码或邮箱)格式是否正确
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validateAccount(str: String): Boolean {
        return validateMobile(str) || validateEmail(str)
    }

    /**
     * 验证密码格式是否正确(可包含大小写字母、数字、下划线、小数点) ^[a-zA-Z0-9_.]{5,20}$
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validatePassword(str: String?): Boolean {
        if (str == null || "" == str) {
            return false
        }
        val p = Pattern.compile("^[a-zA-Z0-9_.]{5,20}$")
        val m = p.matcher(str)
        return m.matches()
    }

    /**
     * 验证电话号码格式是否正确
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validatePhone(str: String?): Boolean {
        if (str == null || "" == str) {
            return false
        }
        val p = Patterns.PHONE
        val m = p.matcher(str)
        return m.matches()
    }

    /**
     * 验证手机号码格式是否正确
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validateMobile(str: String?): Boolean {
        if (str == null || "" == str) {
            return false
        }
        val p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$") // 验证手机号
        val m = p.matcher(str)
        return m.matches()
    }

    /**
     * 验证email地址格式是否正确
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validateEmail(str: String?): Boolean {
        if (str == null || "" == str) {
            return false
        }
        val p = Patterns.EMAIL_ADDRESS
        val m = p.matcher(str)
        return m.matches()
    }

    /**
     * 验证web url地址格式是否正确
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validateURL(str: String?): Boolean {
        if (str == null || "" == str) {
            return false
        }
        val p = Patterns.WEB_URL
        val m = p.matcher(str)
        return m.matches()
    }

    /**
     * 验证IP地址格式正确 255.255.255.255
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun validateIP(str: String?): Boolean {
        if (str == null || "" == str) {
            return false
        }
        val p = Patterns.IP_ADDRESS
        val m = p.matcher(str)
        return m.matches()
    }
}
