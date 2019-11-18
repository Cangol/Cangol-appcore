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
package mobi.cangol.mobile.service.analytics

import mobi.cangol.mobile.logging.Log
import java.util.*

class IMapBuilder private constructor() {

    private var mUrl: String? = null
    private val mParams = HashMap<String, String>()

    open fun getParams(): Map<String, String> {
        return mParams
    }

    open fun getUrl(): String? {
        return mUrl
    }

    fun setUrl(url: String): IMapBuilder {
        this.mUrl = url
        return this
    }

    operator fun set(paramName: String?, paramValue: String): IMapBuilder {
        if (paramName != null) {
            mParams[paramName] = paramValue
        } else {
            Log.w(" IMapBuilder.set() called with a null paramName.")
        }
        return this
    }

    fun setAll(params: Map<String, String>?): IMapBuilder {
        if (params != null) {
            mParams.putAll(params)
        }
        return this
    }

    operator fun get(paramName: String): String? {
        return mParams[paramName]
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("url=")
                .append(mUrl)
                .append('\n')
        for ((key, value) in mParams) {
            builder.append(key).append('=').append(value)
        }
        return builder.toString()
    }

    companion object {

        @JvmStatic
        fun build(): IMapBuilder {
            return IMapBuilder()
        }
    }

}
