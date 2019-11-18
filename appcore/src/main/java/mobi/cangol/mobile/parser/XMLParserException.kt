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

import org.w3c.dom.Node

import java.lang.reflect.Field

class XMLParserException : ParserException {
    @Transient
    private var mNode: Node? = null

    constructor(throwable: Throwable) : super(throwable) {
        mNode = null
    }

    constructor(message: String, throwable: Throwable) : super(message, throwable) {}

    constructor(clazz: Class<*>, message: String, throwable: Throwable) : super(clazz, message, throwable) {}

    constructor(clazz: Class<*>, field: Field, message: String, throwable: Throwable) : super(clazz, field, message, throwable) {}

    constructor(node: Node, message: String, throwable: Throwable) : super(message, throwable) {
        mNode = node
    }

    override val message: String?
        get() {
            return if (mNode != null) {
                if (mNode!!.textContent != null) {
                    "\nError '" + super.message + "' occurred in \n" + mNode!!.textContent + "\n" + cause?.message
                } else {
                    "\nError '" + super.message + "' occurred in " + mNode!!.nodeName + "\n" + cause?.message
                }
            } else {
                super.message
            }
        }
}
