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
package mobi.cangol.mobile.db


open class DeleteBuilder(clazz: Class<*>) {
    private var table: String = clazz.getAnnotation(DatabaseTable::class.java).value
    private val paraKey = mutableListOf<String>()
    private val paraValue = mutableListOf<Any>()
    private val condList = mutableListOf<String>()


    /**
     * @param pName  字段名称
     * @param pValue 字段值
     * @param pType  查询类型{}
     */
     fun addQuery(pName: String, pValue: Any, pType: String) {
        addQuery(pName, pValue, pType, false)
    }

    /**
     * 添加查询条件
     *
     * @param pName  字段名称
     * @param pValue 字段值
     * @param pType  查询类型{}
     * @param isOr
     */
     fun addQuery(pName: String, pValue: Any, pType: String, isOr: Boolean = false) {
        var pType = pType
        if ("" != pName) {
            when (pType) {
                "is" -> {
                    paraKey.add("$pName is ?")
                    paraValue.add(pValue)
                }
                "isnot" -> {
                    paraKey.add("$pName is not ?")
                    paraValue.add(pValue)
                }
                "like" -> {
                    paraKey.add("$pName like ?")
                    paraValue.add("%$pValue%")
                }
                "blike" -> {
                    paraKey.add("$pName like ?")
                    paraValue.add("%$pValue")
                }
                "elike" -> {
                    paraKey.add("$pName like ?")
                    paraValue.add("$pValue%")
                }
                "in" -> //in查询无法用占位符，只能直接拼成sql
                    paraKey.add("$pName in($pValue)")
                "=" -> {
                    paraKey.add("$pName=?")
                    paraValue.add(pValue)
                }
                ">" -> {
                    paraKey.add("$pName>?")
                    paraValue.add(pValue)
                }
                "<" -> {
                    paraKey.add("$pName<?")
                    paraValue.add(pValue)
                }
                "<>" -> {
                    paraKey.add("$pName<>?")
                    paraValue.add(pValue)
                }
                "!=" -> {
                    paraKey.add("$pName!=?")
                    paraValue.add(pValue)
                }
                ">=" -> {
                    paraKey.add("$pName>=?")
                    paraValue.add(pValue)
                }
                "<=" -> {
                    paraKey.add("$pName<=?")
                    paraValue.add(pValue)
                }
                else -> {
                    if (pType.indexOf('?') == -1) {
                        pType += "?"
                    }
                    paraKey.add(pName + pType)
                    paraValue.add(pValue)
                }
            }
            condList.add(if (isOr) " or " else " and ")
        }//&& !"".equals(String.valueOf(pValue))
    }

    /**
     * 添加查询条件
     *
     * @param pName   字段名称
     * @param pValue1 字段值
     * @param pValue2 字段值
     * @param pType   查询类型{}
     * @param isOr
     */
    fun addQuery(pName: String, pValue1: Any, pValue2: Any, pType: String, isOr: Boolean) {
        if ("" != pName && "" != pValue1.toString() && "" != pValue2.toString()) {
            if (pType == "between") {
                paraKey.add("$pName between $pValue1 and $pValue2")
            }
            condList.add(if (isOr) " or " else " and ")
        }
    }

    fun getWhere(): String? {
        val sql = StringBuilder()
        for (i in paraKey.indices) {
            if (i > 0) {
                sql.append(condList[i])
            }

            sql.append(paraKey[i])
        }
        return sql.toString()
    }

    fun getWhereArgs(): Array<String?> {
        val args = arrayOfNulls<String>(paraValue.size)
        for (i in paraValue.indices) {
            paraKey[i]
            args[i] = paraValue[i].toString()
        }
        return args
    }

    fun getTable(): String {
        return table
    }
}