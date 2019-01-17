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

/**
 * @author Cangol
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import mobi.cangol.mobile.logging.Log;

public class StringUtils {
    public static final int INDEX_NOT_FOUND = -1;

    private StringUtils() {
    }

    /**
     * bytes to String
     *
     * @param value
     * @return
     */
    public static String byte2String(byte[] value) {
        String result = null;
        try {
            result = (value == null) ? "" : new String(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(e.getMessage());
        }
        return result;
    }

    /**
     * byte to hex
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0').append(stmp);
            } else {
                hs.append(stmp);
            }

        }
        return hs.toString().toUpperCase();
    }

    /**
     * 反转
     *
     * @param value
     * @return
     */
    public static String reverse(String value) {
        StringBuilder sb = new StringBuilder();
        for (int i = value.length() - 1; i >= 0; --i) {
            sb.append(value.charAt(i));
        }
        return sb.toString();
    }

    /**
     * 格式化为中文数量
     *
     * @param num
     * @return
     */
    public static String formatZhNum(long num) {
        long yi = num / (10000 * 10000);
        long qianwan = (num % (10000 * 10000)) / (1000 * 10000);
        long wan = num / (10000);
        long qian = (num % (10000)) / (1000);

        if (yi > 0) {
            if (qianwan > 0) {
                return yi + "." + qianwan + "亿";
            } else {
                return yi + "亿";
            }
        } else if (wan > 0) {
            if (qian > 0) {
                return wan + "." + qian + "万";
            } else {
                return wan + "万";
            }
        } else {
            return "" + num;
        }
    }

    /**
     * format speed   /s
     *
     * @param value
     * @return
     */
    public static String formatSpeed(long value) {
        return formatSize(value) + "/s";
    }

    /**
     * format size
     *
     * @param value
     * @return
     */
    public static String formatSize(long value) {

        double k = (double) value / 1024;
        if (k == 0) {
            return String.format("%dB", value);
        }

        double m = k / 1024;
        if (m < 1) {
            return String.format("%.1fK", k);
        }

        double g = m / 1024;
        if (g < 1) {
            return String.format("%.1fM", m);
        }

        return String.format("%.1fG", g);
    }

    /**
     * format time
     *
     * @param second
     * @return
     */
    public static String formatTime(int second) {

        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;

        if (0 != hh) {
            return String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            return String.format("%02d:%02d", mm, ss);
        }
    }

    /**
     * MD5 s
     *
     * @param s
     * @return
     */
    public static String md5(String s) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = s.getBytes("UTF-8");
            //MessageDigest md5
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            Log.d(e.getMessage());
            return null;
        }
    }

    /**
     * MD5 byte[]
     *
     * @param bytes
     * @return
     */
    public static String md5(byte[] bytes) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            //MessageDigest md5
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(bytes);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte b = md[i];
                str[k++] = hexDigits[b >> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            Log.d(e.getMessage());
            return null;
        }
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns false if the string is null or 0-length.
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

    /**
     * Returns true if the string is null or ''.
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }

    /**
     * Returns string if the string is null or ''.
     *
     * @return
     */
    public static String null2Zero(String str) {
        if (str == null || str.equals("")) {
            return "0";
        } else {
            return str;
        }
    }

    /**
     * remove TableSpace
     *
     * @param str
     * @return
     */
    public static String replaceWhiteSpace(String str) {
        return str.replaceAll("\n", "");
    }

    /**
     * remove TableSpace
     *
     * @param str
     * @return
     */
    public static String replaceTableSpace(String str) {
        return str.replaceAll(" ", "").replaceAll("\t", "");
    }

    /**
     * remove front Whitespace
     *
     * @param str
     * @return
     */
    public static String trimForFront(String str) {
        StringBuilder sb = new StringBuilder();
        boolean first = false;
        char aa;
        for (int i = 0, length = str.length(); i < length; i++) {
            aa = str.charAt(i);
            if (!(!first && aa == '\t')) {
                first = true;
                sb.append(aa);
            }
        }

        return sb.toString();
    }

    /**
     * str  the String to be trimmed, may be null
     *
     * @param str
     * @return
     */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * repalce all Whitespace
     *
     * @param str
     * @return
     */
    public static String trimAllWhitespace(String str) {
        return str.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "");
    }

    /**
     * strip
     *
     * @param str
     * @return
     */
    public static String strip(String str) {
        return strip(str, null);
    }

    /**
     * strip Start
     *
     * @param str
     * @param stripChars
     * @return
     */
    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while ((start != strLen) && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                start++;
            }
        }
        return str.substring(start);
    }

    /**
     * @param str
     * @param stripChars
     * @return
     */
    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND)) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    /**
     * strip start|end
     *
     * @param str
     * @param stripChars
     * @return
     */
    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    /**
     * isNumeric
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * isNumericSpace
     *
     * @param str
     * @return
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i)) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * byte[] xor int
     *
     * @param data
     * @param xor
     * @return
     */
    public static byte[] byteXorInt(byte[] data, int xor) {
        byte[] data2 = new byte[4];
        for (int i = 0; i < data.length; i = i + 4) {
            data2[0] = data[i / 4 + 0];
            data2[1] = data[i / 4 + 1];
            data2[2] = data[i / 4 + 2];
            data2[3] = data[i / 4 + 3];
            data2 = int2byteArray(byteArray2int(data2) ^ xor);
            data[i / 4 + 0] = data2[0];
            data[i / 4 + 1] = data2[1];
            data[i / 4 + 2] = data2[2];
            data[i / 4 + 3] = data2[3];
        }
        return data;
    }

    /**
     * byte[] to int
     *
     * @param b
     * @return
     */
    public static int byteArray2int(byte[] b) {
        byte[] a = new byte[4];
        int i = a.length - 1;
        int j = b.length - 1;
        for (; i >= 0; i--, j--) {//从b的尾部(即int值的低位)开始copy数据
            if (j >= 0) {
                a[i] = b[j];
            } else {
                a[i] = 0;//如果b.length不足4,则将高位补0
            }

        }
        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff);
        return v0 + v1 + v2 + v3;
    }

    /**
     * int to byte[]
     *
     * @param num
     * @return
     */
    public static byte[] int2byteArray(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) (num >>> 24);
        result[1] = (byte) (num >>> 16);
        result[2] = (byte) (num >>> 8);
        result[3] = (byte) (num);
        return result;
    }

    /**
     * @param str
     * @return
     */
    public static String null2Empty(String str) {
        return str == null ? "" : str;
    }
}
