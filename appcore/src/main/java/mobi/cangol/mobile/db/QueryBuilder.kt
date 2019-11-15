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


class QueryBuilder(clazz: Class<*>) {
    private var distinctValue: Boolean = false
    private var orderByValue: String? = null
    private var groupByValue: String? = null
    private var havingValue: String? = null

    private var limitValue: Long? = null
    private var offsetValue: Long? = null

    private var table: String = clazz.getAnnotation(DatabaseTable::class.java).value
    private val paraKey = mutableListOf<String>()
    private val paraValue = mutableListOf<Any>()
    private val condList = mutableListOf<String>()


    /**
     * 添加查询条件
     *
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
    fun addQuery(pName: String?, pValue: Any?, pType: String, isOr: Boolean = false) {
        var pType = pType
        if (pName != null && "" != pName && pValue != null) {
            if (pType == "is") {
                paraKey!!.add("$pName is ?")
                paraValue!!.add(pValue)
            } else if (pType == "isnot") {
                paraKey!!.add("$pName is not ?")
                paraValue!!.add(pValue)
            } else if (pType == "like") {
                paraKey!!.add("$pName like ?")
                paraValue!!.add("%$pValue%")
            } else if (pType == "blike") {
                paraKey!!.add("$pName like ?")
                paraValue!!.add("%$pValue")
            } else if (pType == "elike") {
                paraKey!!.add("$pName like ?")
                paraValue!!.add("$pValue%")
            } else if (pType == "in") {
                //in查询无法用占位符，只能直接拼成sql
                paraKey!!.add("$pName in($pValue)")
            } else if (pType == "=") {
                paraKey!!.add("$pName=?")
                paraValue!!.add(pValue)
            } else if (pType == ">") {
                paraKey!!.add("$pName>?")
                paraValue!!.add(pValue)
            } else if (pType == "<") {
                paraKey!!.add("$pName<?")
                paraValue!!.add(pValue)
            } else if (pType == "<>") {
                paraKey!!.add("$pName<>?")
                paraValue!!.add(pValue)
            } else if (pType == "!=") {
                paraKey!!.add("$pName!=?")
                paraValue!!.add(pValue)
            } else if (pType == ">=") {
                paraKey!!.add("$pName>=?")
                paraValue!!.add(pValue)
            } else if (pType == "<=") {
                paraKey!!.add("$pName<=?")
                paraValue!!.add(pValue)
            } else {
                if (pType.indexOf('?') == -1) {
                    pType += "?"
                }
                paraKey!!.add(pName + pType)
                paraValue!!.add(pValue)
            }
            condList.add(if (isOr) " or " else " and ")
        }//&& !"".equals(String.valueOf(pValue))
    }

    /**
     * 添加between条件
     *
     * @param pName
     * @param pValue1
     * @param pValue2
     * @param pType
     * @param isOr
     */
    fun addQuery(pName: String?, pValue1: Any, pValue2: Any, pType: String, isOr: Boolean) {
        if (pName != null && "" != pName && "" != pValue1.toString() && "" != pValue2.toString()) {
            if (pType == "between") {
                paraKey!!.add("$pName between $pValue1 and $pValue2")
            }
            condList.add(if (isOr) " or " else " and ")
        }
    }

    fun getSelection(): String? {
        val sql = StringBuilder()
        for (i in paraKey.indices) {
            if (i > 0) {
                sql.append(condList[i])
            }
            sql.append(paraKey[i])
        }
        return sql.toString()
    }

    fun getSelectionArgs(): Array<String?> {
        val args = arrayOfNulls<String>(paraValue.size)
        for (i in paraValue.indices) {
            paraKey[i]
            args[i] = paraValue[i].toString()
        }
        return args

    }

    fun getWhere(): String? {
        val sql = StringBuilder()
        var str: String? = null
        for (i in paraKey.indices) {
            if (i > 0) {
                sql.append(condList[i])
            }
            str = paraKey[i]
            sql.append(str.replace("?", paraValue[i].toString()))
        }
        return sql.toString()
    }

    fun isDistinctValue(): Boolean {
        return distinctValue
    }

    /**
     * 设置distinct
     *
     * @param distinct
     */
    fun distinct(distinct: Boolean) {
        this.distinctValue = distinct
    }

    fun getTable(): String {
        return table
    }

    fun getOrderByValue(): String? {
        return this.orderByValue
    }

    /**
     * 设置 orderByValue
     *
     * @param orderBy
     */
    fun orderBy(orderBy: String) {
        this.orderByValue = orderBy
    }

    fun getGroupByValue(): String? {
        return groupByValue
    }

    /**
     * 设置groupBy
     *
     * @param groupBy
     */
    fun groupBy(groupBy: String) {
        this.groupByValue = groupBy
    }

    fun getHavingValue(): String? {
        return havingValue
    }

    /**
     * 设置having
     *
     * @param having
     */
    fun having(having: String) {
        this.havingValue = having
    }

    fun getLimitValue(): String? {
        return if (limitValue != null) {
            if (offsetValue != null) {
                offsetValue.toString() + "," + limitValue
            } else "" + limitValue!!
        } else {
            null
        }
    }

    /**
     * 设置limit
     *
     * @param limit
     */
    fun limit(limit: Long) {
        this.limitValue = limit
    }

    /**
     * 设置offset
     *
     * @param offset
     */
    fun offset(offset: Long) {
        this.offsetValue = offset
    }

}