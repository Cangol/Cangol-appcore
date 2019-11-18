/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package mobi.cangol.mobile.http

import java.io.File
import java.util.concurrent.ConcurrentHashMap

class RequestParams {

    private val urlParams = ConcurrentHashMap<String, String>()
    private val fileParams = ConcurrentHashMap<String, File>()

    constructor()

    constructor(source: Map<String, String>) {
        for ((key, value) in source) {
            put(key, value)
        }
    }

    constructor(key: String, value: String) {
        put(key, value)
    }

    constructor(vararg keysAndValues: Any) {
        val len = keysAndValues.size
        require(len % 2 == 0) { "Supplied arguments must be even" }
        var i = 0
        while (i < len) {
            put(keysAndValues[i].toString(), keysAndValues[i + 1].toString())
            i += 2
        }
    }

    fun put(key: String?, value: String?) {
        if (key != null && value != null) {
            urlParams[key] = value
        }
    }

    fun put(key: String?, value: File?) {
        if (key != null && value != null) {
            fileParams[key] = value
        }
    }

    fun remove(key: String) {
        urlParams.remove(key)
        fileParams.remove(key)
    }

    fun toDebugString(): String {
        val result = StringBuilder()
        for ((key, value) in urlParams) {
            result.append('\t')
                    .append(key)
                    .append('=')
                    .append(value)
                    .append('\n')
        }

        for ((key) in fileParams) {
            result.append(key)
                    .append("=FILE\n")
        }

        return result.toString()
    }

    override fun toString(): String {
        val result = StringBuilder()
        for ((key, value) in urlParams) {
            if (result.isNotEmpty()) {
                result.append('&')
            }

            result.append(key)
                    .append('=')
                    .append(value)
        }

        for ((key) in fileParams) {
            if (result.isNotEmpty()) {
                result.append('&')
            }

            result.append(key)
                    .append("=FILE")
        }

        return result.toString()
    }

    fun isEmpty(): Boolean {
        return urlParams.isEmpty() && fileParams.isEmpty()
    }

    fun getUrlParams(): ConcurrentHashMap<String, String> {
        return urlParams
    }

    fun getFileParams(): ConcurrentHashMap<String, File> {
        return fileParams
    }
}