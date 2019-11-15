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

import java.lang.reflect.Field

open class ParserException : Exception {
    private var field: Field? = null
    private var clazz: Class<*>? = null

    constructor(message: String) : super(message) {}

    constructor(throwable: Throwable) : super(throwable) {}

    constructor(message: String, throwable: Throwable) : super(message, throwable) {}

    constructor(clazz: Class<*>, message: String, throwable: Throwable) : super(message, throwable) {
        this.field = null
        this.clazz = clazz
    }

    constructor(clazz: Class<*>, field: Field, message: String, throwable: Throwable) : super(message, throwable) {
        this.field = field
        this.clazz = clazz
    }

    override val message: String? = null
        get() {
            return if (clazz != null) {
                if (field != null) {
                    "\nError '" + super.message + "' occurred in " + clazz!!.name + "@" + field + "\n" + cause?.message
                } else {
                    "\nError '" + super.message + "' occurred in " + clazz!!.name + "\n" + cause?.message
                }
            } else {
                super.message
            }
        }
}
