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
package mobi.cangol.mobile.parser

import org.json.JSONObject

import java.lang.reflect.Field


class JSONParserException : ParserException {

    private var mNode: JSONObject?=null

    constructor(message: String, throwable: Throwable) : super(throwable) {}

    constructor(clazz: Class<*>, message: String, throwable: Throwable) : super(clazz, message, throwable) {}

    constructor(clazz: Class<*>, field: Field, message: String, throwable: Throwable) : super(clazz, field, message, throwable) {}

    constructor(node: JSONObject, message: String, throwable: Throwable) : super(throwable) {
        mNode = node
    }

    override val message: String?
        get() {
            return if (mNode != null) {
                "\nError '" + super.message + "' occurred in \n" + mNode.toString() + "\n" + cause?.message
            } else {
                super.message
            }
        }
}
