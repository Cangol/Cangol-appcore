/**
 * Copyright (c) 2013 Cangol.
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
package mobi.cangol.mobile.service

/**
 * @author Cangol
 */
class ServiceProperty(val name: String) {
    private val mMap= HashMap<String, Any>()

    fun putString(key: String, value: String) {
        mMap[key] = value
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        val o = mMap[key] ?: return defaultValue
        return try {
            o as String
        } catch (e: ClassCastException) {
            defaultValue
        }
    }

    fun putInt(key: String, value: Int) {
        mMap[key] = value
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        val o = mMap[key] ?: return defaultValue
        return try {
            Integer.parseInt(o.toString())
        } catch (e: ClassCastException) {
            defaultValue
        }

    }

    fun putDouble(key: String, value: Double) {
        mMap[key] = value
    }

    fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        val o = mMap[key] ?: return defaultValue
        return try {
            java.lang.Double.valueOf(o.toString())
        } catch (e: ClassCastException) {
            defaultValue
        }

    }

    fun putBoolean(key: String, value: Boolean) {
        mMap[key] = value
    }

    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val o = mMap[key] ?: return defaultValue
        return try {
            java.lang.Boolean.valueOf(o.toString())
        } catch (e: ClassCastException) {
            defaultValue
        }

    }

    fun putLong(key: String, value: Long?) {
        mMap[key] = value!!
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        val o = mMap[key] ?: return defaultValue
        return try {
            java.lang.Long.parseLong(o.toString())
        } catch (e: ClassCastException) {
            defaultValue
        }

    }

    fun putFloat(key: String, value: Float?) {
        mMap[key] = value!!
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        val o = mMap[key] ?: return defaultValue
        return try {
            java.lang.Float.valueOf(o.toString())
        } catch (e: ClassCastException) {
            defaultValue
        }

    }

    override fun toString(): String {
        return "ServiceProperty{" +
                "mName='" + name + '\''.toString() +
                ", mMap=" + mMap.toString() +
                '}'.toString()
    }
}
