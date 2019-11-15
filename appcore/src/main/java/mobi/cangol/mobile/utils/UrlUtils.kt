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

import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * @author Cangol
 */
object UrlUtils {

    /**
     * 判断是否是url
     *
     * @param value
     * @return
     */
    @JvmStatic
    fun isUrl(value: String?): Boolean {
        return if (value != null && "" != value) {
            value.matches("(((http|ftp|https|file)://)?([\\w\\-]+\\.)+[\\w\\-]+(/[\\w\\u4e00-\\u9fa5\\-\\./?\\@\\%\\!\\&=\\+\\~\\:\\#\\;\\,]*)?)".toRegex())
        } else {
            false
        }
    }

    /**
     * 从url获取主机
     *
     * @param url
     * @return
     */
    @JvmStatic
    fun getHost(url: String): String {
        return try {
            URL(url).host
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 从url获取参数map
     *
     * @param url
     * @return Map
     */
    @JvmStatic
    fun getParams(url: String): Map<String, String> {

        var query = try {
            URL(url).query
        } catch (e: MalformedURLException) {
            ""
        }

        val queries = HashMap<String, String>()
        if (query == null) {
            return queries
        }

        for (entry in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val keyValue = entry.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (keyValue.size != 2) {
                continue
            }
            queries[keyValue[0]] = keyValue[1]
        }
        return queries
    }

    /**
     * @param url
     * @return
     */
    @JvmStatic
    fun getPath(url: String?): String? {
        var command: String? = null
        if (url != null && url.contains("://")) {
            command = if (url.contains("?")) {
                url.substring(url.indexOf("://") + 3, url.indexOf('?'))
            } else {
                url.substring(url.indexOf("://") + 3, url.length)
            }
        }
        return command
    }

    /**
     * @param url
     * @return
     */
    @JvmStatic
    fun getScheme(url: String?): String? {
        var command: String? = null
        if (url != null && url.contains("://")) {
            command = url.substring(0, url.indexOf("://"))
        }
        return command
    }
}
