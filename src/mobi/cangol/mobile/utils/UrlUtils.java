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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description URLUtils.java 
 * @author Cangol
 * @date 2012-8-27
 */
public class UrlUtils {
	
	/**
	 * 判断是否是url
	 * @param value
	 * @return
	 */
	public static boolean isUrl(String value) {
		if (value!=null){
			return value.matches("(((http|ftp|https|file)://)?([\\w\\-]+\\.)+[\\w\\-]+(/[\\w\\u4e00-\\u9fa5\\-\\./?\\@\\%\\!\\&=\\+\\~\\:\\#\\;\\,]*)?)");
		}else 
			return false;
	}
	/**
	 * url encode
	 * @param url
	 * @return
	 */
	public static String encode(String url) {
		return URLEncoder.encode(url);
	}
	/**
	 * url decode
	 * @param url
	 * @return
	 */
	public static String decode(String url) {
		return URLDecoder.decode(url);
	}
	/**
	 * 主机和method组合url
	 * @param host
	 * @param method
	 * @return
	 */
	public static String getUrl(String host, String method) {
		
		try {
			URL u = new URL(host);
			return new URL(u.getProtocol(), u.getHost(), method).toString();
		} catch (MalformedURLException e) {
			return "";
		} catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * 从url获取主机
	 * @param url
	 * @return
	 */
	public static String getHost(String url) {
		
		try {
			return new URL(url).getHost();
		} catch(MalformedURLException e) {
			return "";
		} catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * 从url获取域名
	 * @param url
	 * @return
	 */
	public static String getDomain(String url) {
		
		String host = getHost(url);
		int index = host.lastIndexOf('.');
		if(index == -1) {
			return "";
		}
		String str = host.substring(0, index);
		index = str.lastIndexOf('.');
		if(index == -1) {
			return "";
		}
		str = host.substring(index + 1);
		if(str.endsWith("/")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
	/**
	 * 从url获取参数map
	 * @param url
	 * @return Map
	 */
	public static Map<String, String> getParams(String url) {
		
		String query = "";
		try {
			query = new URL(url).getQuery();
		} catch(MalformedURLException e) {
			query = "";
		}
		
		Map<String, String> queries = new HashMap<String, String>();
		if(query == null) {
			return queries;
		}
		
		for(String entry: query.split("&")) {
			String[] keyvalue = entry.split("=");
			if(keyvalue.length != 2) {
				continue;
			}
			queries.put(keyvalue[0], keyvalue[1]);
		}
		return queries;
	}
}
