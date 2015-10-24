/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.TextView;

/**
 * @author Cangol
 */
public class ValidatorUtils {

	/**
	 * 验证字符串是否不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validateNull(String str) {
		if (TextUtils.isEmpty(str)) {
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
		if (TextUtils.isEmpty(str)) {
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
	 * @param view
	 * @return
	 */
	public static boolean validateNickname(TextView view) {
		String rx = "[\\w]";
		String rx2 = "[\u4e00-\u9fa5]";
		final int MAXCOUNT = 20;
		int num = 0;
		boolean flag = true;
		String str = view.getText().toString();
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

		if (num <= MAXCOUNT && num > 5)
			flag = false;

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
		Pattern p = Patterns.IP_ADDRESS;
		Matcher m = p.matcher(str);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}
}
