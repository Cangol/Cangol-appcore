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
package mobi.cangol.mobile.utils;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Cangol
 */
public class ValidatorUtils {
    private ValidatorUtils() {
    }

    /**
     * 验证字符串是否不为空
     *
     * @param str
     * @return
     */
    public static boolean validateNull(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        return true;
    }

    /**
     * 验证消息字符串大小(5-140)
     *
     * @param str
     * @return
     */
    public static boolean validateContent(String str) {
        if (str == null || "".equals(str)) {
            return false;
        } else if (str.length() >= 5 && str.length() <= 140) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证昵称格式是否正确(长度5-20,所有单词字符，包括中文，中文算2个字符)
     *
     * @param str
     * @return
     */
    public static boolean validateNickname(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }

        String rx = "[a-zA-Z0-9]";
        String rx2 = "[\u4e00-\u9fa5]";
        int num = 0;
        boolean flag = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Pattern.compile(rx).matcher(c + "").matches()) {
                num += 1;
            } else if (Pattern.compile(rx2).matcher(c + "").matches()) {
                num += 2;
            } else {
                flag = false;
                break;
            }
        }

        if (num <= 20 && num > 5) {
            flag = true;
        }

        return flag;
    }

    /**
     * 验证帐号(手机号码或邮箱)格式是否正确
     *
     * @param str
     * @return
     */
    public static boolean validateAccount(String str) {
        return validateMobile(str) || validateEmail(str);
    }

    /**
     * 验证密码格式是否正确(可包含大小写字母、数字、下划线、小数点) ^[a-zA-Z0-9_.]{5,20}$
     *
     * @param str
     * @return
     */
    public static boolean validatePassword(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        Pattern p = Pattern.compile("^[a-zA-Z0-9_.]{5,20}$");
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证电话号码格式是否正确
     *
     * @param str
     * @return
     */
    public static boolean validatePhone(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        Pattern p = Patterns.PHONE;
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证手机号码格式是否正确
     *
     * @param str
     * @return
     */
    public static boolean validateMobile(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证email地址格式是否正确
     *
     * @param str
     * @return
     */
    public static boolean validateEmail(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        Pattern p = Patterns.EMAIL_ADDRESS;
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证web url地址格式是否正确
     *
     * @param str
     * @return
     */
    public static boolean validateURL(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证IP地址格式正确 255.255.255.255
     *
     * @param str
     * @return
     */
    public static boolean validateIP(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        Pattern p = Patterns.IP_ADDRESS;
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
