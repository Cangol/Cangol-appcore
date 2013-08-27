package com.cangol.mobile.utils;

/**
 * @Description: 字符串类
 * @version $Revision: 1.0 $
 * @author xuewu.wei
 * @date: 2010-12-6
 */

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static final int INDEX_NOT_FOUND = -1;
	
	/**
	 * check account valid(email or phone)
	 * @param str
	 * @return
	 */
	public static boolean checkAccount(String str) {
		if(checkEmail(str)){
			return true;
		}else
			return checkPhone(str);
		
	}
	
	/**
	 * check  phone 
	 * @param str
	 * @return
	 */
	public static boolean checkPhone(String str) {
		Pattern p = Pattern.compile("^\\d{3}-?\\d{7}$");
		Matcher m = p.matcher(str);
		return m.matches();
	}
	public static boolean checkZipCode(String str) {
		Pattern p = Pattern.compile("^\\d{5}$");
		Matcher m = p.matcher(str);
		return m.matches();
	}
	/**
	 * check nick valid
	 * @param str
	 * @return
	 */
	public static boolean checkNickName(String str) {
		Pattern p = Pattern.compile("^[\\w+$\u4e00-\u9fa5]+$");
		Matcher m = p.matcher(str);
		return m.matches();
	}
	
	/**
	 * check password 6-14 word
	 * @param pwdStr
	 * @return
	 */
	public static boolean checkPassword(String str) {
		Pattern p = Pattern.compile("^[\\w+$]{6,14}+$");
		Matcher m = p.matcher(str);
		return m.matches();
	}
	
	/**
	 * check email
	 * @param emailStr
	 * @return
	 */
	public static boolean checkEmail(String emailStr) {
		Pattern p = Pattern.compile("\\w+(\\.\\w+)*@\\w+(\\.\\w+)+");
    	Matcher m = p.matcher(emailStr);
    	return m.matches();
	}
	
	/**
	 * MD5
	 * @param s
	 * @return
	 */
	public  static String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			//MessageDigest md5
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				//System.out.println((int)b);    
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 *  Returns true if the string is null or 0-length.
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
	}
	
	/**
	 * Returns true if the string is null or 0-length.
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
	        return str == null || str.length() == 0;
	}
	
	/**
	 * Returns false if the string is null or 0-length.
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str){
	        return !StringUtils.isEmpty(str);
	}
	
	/**
	 * Returns true if the string is null or ''.
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str){
		return !StringUtils.isBlank(str);
	}
	
	/**
	 * Returns string if the string is null or ''.
	 * @return
	 */
	public static String null2Zero(String str){
		if (str == null || str.equals("")){
			return  "0";
		}else{
			return str;
		}
	}
	
	/**
	 * remove TableSpace
	 * @param str
	 * @return
	 */
	public static String replaceWhiteSpace(String str) {
		return str.replaceAll("\n", "");
	}
	
	/**
	 * remove TableSpace
	 * @param str
	 * @return
	 */
	public static String replaceTableSpace(String str) {
		return str.replaceAll(" ", "").replaceAll("\t", "");
	}
	
	/**
	 * remove front Whitespace
	 * @param str
	 * @return
	 */
	public static String trimForFront(String str){
		StringBuffer sb = new StringBuffer();
		boolean first = false;
		char aa;
		for(int i=0,length=str.length();i<length;i++){
			aa = str.charAt(i);
			if(!first&&aa=='\t'){
			}else{
				first = true;
				sb.append(aa);
			}
		}
		
		return sb.toString();
	}
	/**
	 * str  the String to be trimmed, may be null
	 * @param str
	 * @return
	 */
	public static String trimToEmpty(String str) {
		return str == null ? "" : str.trim();
	}
	/**
	 * repalce all Whitespace
	 * @param str
	 * @return
	 */
	public static String trimAllWhitespace(String str) {
		return str.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "").toString();
	}
	public static String strip(String str) {
	        return strip(str, null);
	}
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
	 * 
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
	public static String strip(String str, String stripChars) {
	        if (isEmpty(str)) {
	            return str;
	        }
	        str = stripStart(str, stripChars);
	        return stripEnd(str, stripChars);
	}
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }
}
