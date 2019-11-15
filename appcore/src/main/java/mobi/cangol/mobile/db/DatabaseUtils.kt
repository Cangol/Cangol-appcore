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

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import mobi.cangol.mobile.logging.Log
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.util.*

object DatabaseUtils {

    /**
     * 创建表索引
     *
     * @param db
     * @param clazz
     */
    @JvmStatic fun createIndex(db: SQLiteDatabase, clazz: Class<*>, indexName: String, vararg fieldNames: String) {
        if (clazz.isAnnotationPresent(DatabaseTable::class.java)) {
            val table = clazz.getAnnotation(DatabaseTable::class.java)
            val tableName = if ("" == table!!.value) clazz.simpleName else table.value
            val sql = StringBuilder("CREATE INDEX ")
            sql.append(indexName).append(" on ").append(tableName).append('(')
            var field: Field? = null
            var columnName: String? = null
            for (i in fieldNames.indices) {
                try {
                    field = clazz.getDeclaredField(fieldNames[i])
                    field!!.isAccessible = true
                    if (field.isEnumConstant || Modifier.isFinal(field.modifiers) || Modifier.isTransient(field.modifiers)) {
                        continue
                    } else if (field.isAnnotationPresent(DatabaseField::class.java)) {
                        val dbField = field.getAnnotation(DatabaseField::class.java)
                        columnName = if ("" == dbField.value) field.name else dbField.value
                        sql.append(columnName)
                        if (i < fieldNames.size - 1)
                            sql.append(',')
                    }
                } catch (e: NoSuchFieldException) {
                    Log.e(e.message)
                }

            }
            sql.append(')')
            db.execSQL(sql.toString())
        } else {
            throw IllegalStateException("$clazz not DatabaseTable Annotation")
        }
    }

    /**
     * 创建表
     *
     * @param db
     * @param clazz
     */
    @JvmStatic fun createTable(db: SQLiteDatabase, clazz: Class<*>) {
        if (clazz.isAnnotationPresent(DatabaseTable::class.java)) {
            val sql = StringBuilder("CREATE TABLE IF NOT EXISTS ")
            val table = clazz.getAnnotation(DatabaseTable::class.java)
            val tableName = if ("" == table!!.value) clazz.simpleName else table.value
            sql.append(tableName).append('(')
            val fields = clazz.declaredFields
            var filedName: String? = null
            var isFirst = true
            for (field in fields) {
                field.isAccessible = true
                if (field.isEnumConstant || Modifier.isFinal(field.modifiers) || Modifier.isTransient(field.modifiers)) {
                    continue
                }
                if (field.isAnnotationPresent(DatabaseField::class.java)) {

                    if (!isFirst) {
                        sql.append(',')
                    } else {
                        isFirst = false
                    }

                    val dbField = field.getAnnotation(DatabaseField::class.java)
                    filedName = if ("" == dbField.value) field.name else dbField.value
                    sql.append(filedName)
                    sql.append(' ').append(getDbType(field.type))
                    if (dbField.primaryKey) {
                        sql.append(" PRIMARY KEY AUTOINCREMENT")
                    }
                    if (dbField.unique) {
                        sql.append(" UNIQUE")
                    }
                    if (dbField.notNull) {
                        sql.append(" NOT NULL")
                    }
                }
            }
            sql.append(')')
            db.execSQL(sql.toString())
        } else {
            throw IllegalStateException("$clazz not DatabaseTable Annotation")
        }
    }

    /**
     * 获取sqlite对应的数据类型
     *
     * @param clazz
     * @return
     */
    @JvmStatic fun getDbType(clazz: Class<*>): String {
        return if (clazz == String::class.java
                || clazz == Char::class.java || clazz == Char::class.javaPrimitiveType
                || clazz == Boolean::class.java || clazz == Boolean::class.javaPrimitiveType) {
            "TEXT"
        } else if (clazz == Int::class.java || clazz == Int::class.javaPrimitiveType
                || clazz == Long::class.java || clazz == Long::class.javaPrimitiveType
                || clazz == Short::class.java || clazz == Short::class.javaPrimitiveType) {
            "INTEGER"
        } else if (clazz == Double::class.java || clazz == Double::class.javaPrimitiveType) {
            "Double"
        } else if (clazz == Float::class.java || clazz == Float::class.javaPrimitiveType) {
            "FLOAT"
        } else {
            "BLOB"
        }
    }

    /**
     * 删除表
     *
     * @param db
     * @param clazz
     */
    @JvmStatic fun dropTable(db: SQLiteDatabase, clazz: Class<*>) {
        if (clazz.isAnnotationPresent(DatabaseTable::class.java)) {
            val sql = StringBuilder("DROP TABLE IF EXISTS ")
            val table = clazz.getAnnotation(DatabaseTable::class.java)
            val tableName = if ("" == table!!.value) clazz.simpleName else table.value
            sql.append(tableName)
            db.execSQL(sql.toString())
        } else {
            throw IllegalStateException("$clazz not DatabaseTable Annotation")
        }
    }

    /**
     * 删除表
     *
     * @param db
     * @param table
     */
    @JvmStatic fun dropTable(db: SQLiteDatabase, table: String?) {
        if (table != null && "" != table.trim { it <= ' ' }) {
            val sql = StringBuilder("DROP TABLE IF EXISTS ")
            sql.append(table)
            db.execSQL(sql.toString())
        } else {
            throw IllegalStateException(table!! + " not DatabaseTable Annotation")
        }
    }

    /**
     * 表添加列
     *
     * @param db
     * @param clazz
     */
    @JvmStatic fun addColumn(db: SQLiteDatabase, clazz: Class<*>, vararg columns: String) {
        Log.d("addColumn ")
        if (clazz.isAnnotationPresent(DatabaseTable::class.java)) {
            val table = clazz.getAnnotation(DatabaseTable::class.java)
            val tableName = if ("" == table!!.value) clazz.simpleName else table.value

            val map = getColumnNames(clazz)
            for (i in columns.indices) {
                if (!TextUtils.isEmpty(columns[i]) && map.containsKey(columns[i])) {
                    val sql = StringBuilder("ALTER TABLE ")
                            .append(tableName)
                            .append(" ADD COLUMN ")
                            .append(columns[i])
                            .append("　")
                            .append(getDbType(map[columns[i]]!!.type))
                            .append(";")
                    db.execSQL(sql.toString())
                    Log.d("" + sql.toString())
                } else {
                    throw IllegalStateException("column " + columns[i] + " is exist!")
                }
            }
        } else {
            throw IllegalStateException("$clazz not DatabaseTable Annotation")
        }
    }

    /**
     * 获取所有要数据库化的列名
     *
     * @param clazz
     * @return
     */
    @JvmStatic fun getColumnNames(clazz: Class<*>): Map<String, Field> {
        val map = HashMap<String, Field>()
        for (field in clazz.declaredFields) {
            field.isAccessible = true
            if (field.isAnnotationPresent(DatabaseField::class.java)) {
                val dbField = field.getAnnotation(DatabaseField::class.java)
                map[if ("" == dbField.value) field.name else dbField.value] = field
            }
        }
        return map
    }

    @JvmStatic fun getIdColumnName(clazz: Class<*>): String? {
        var columnName: String? = null
        for (field in clazz.declaredFields) {
            field.isAccessible = true
            if (field.isEnumConstant || Modifier.isFinal(field.modifiers) || Modifier.isTransient(field.modifiers)) {
                continue
            }
            if (field.isAnnotationPresent(DatabaseField::class.java)) {
                val dbField = field.getAnnotation(DatabaseField::class.java)
                if (dbField.primaryKey) {
                    columnName = if ("" == dbField.value) field.name else dbField.value
                    break
                }
            }
        }
        return columnName
    }

    /**
     * 获取主键的值
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    @Throws(IllegalAccessException::class)
    @JvmStatic fun getIdValue(obj: Any): Any? {
        var value: Any? = null
        for (field in obj.javaClass.declaredFields) {
            field.isAccessible = true
            if (field.isEnumConstant || Modifier.isFinal(field.modifiers) || Modifier.isTransient(field.modifiers)) {
                continue
            }

            if (field.isAnnotationPresent(DatabaseField::class.java)) {
                val dbField = field.getAnnotation(DatabaseField::class.java)
                if (dbField.primaryKey) {
                    value = field.get(obj)
                    break
                }
            }
        }
        return value
    }

    /**
     * 获取键值对象
     *
     * @param object
     * @return
     * @throws IllegalAccessException
     */
    @Throws(IllegalAccessException::class)
    @JvmStatic fun getContentValues(obj: Any): ContentValues {
        val v = ContentValues()
        var filedName: String? = null
        for (field in obj.javaClass.declaredFields) {
            field.isAccessible = true
            if (field.isAnnotationPresent(DatabaseField::class.java)) {
                val dbField = field.getAnnotation(DatabaseField::class.java)
                if (!dbField.primaryKey) {
                    filedName = if ("" == dbField.value) field.name else dbField.value
                    v.put(filedName, field.get(obj).toString())
                }
            }
        }
        return v
    }

    /**
     * 获取键值对象
     *
     * @param object
     * @param columns
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @Throws(IllegalAccessException::class)
    @JvmStatic fun getContentValues(obj: Any, columns: Array<out String>?): ContentValues {
        val v = ContentValues()
        var filedName: String? = null
        val set = if (columns == null) HashSet() else HashSet(listOf(*columns))
        for (field in obj.javaClass.declaredFields) {
            field.isAccessible = true
            if (field.isAnnotationPresent(DatabaseField::class.java)) {
                val dbField = field.getAnnotation(DatabaseField::class.java)
                filedName = if ("" == dbField.value) field.name else dbField.value
                if (!dbField.primaryKey && (set.isEmpty() || set.contains(filedName))) {
                    v.put(filedName, field.get(obj).toString())
                }
            }
        }
        return v
    }

    /**
     * 查询记录的值，赋值给obj
     *
     * @param obj
     * @param cursor
     * @param <T>
     * @return
    </T> */
    @JvmStatic fun <T> cursorToObject(obj: T, cursor: Cursor, columns: Array<out String>?): T {
        val fields = (obj as Any).javaClass.declaredFields
        val set = if (columns == null) HashSet() else HashSet(listOf(*columns))
        var columnName: String? = null
        for (field in fields) {
            field.isAccessible = true
            if (field.isEnumConstant || Modifier.isFinal(field.modifiers) || Modifier.isTransient(field.modifiers)) {
                continue
            }
            if (field.isAnnotationPresent(DatabaseField::class.java)) {
                val dbField = field.getAnnotation(DatabaseField::class.java)
                columnName = if ("" == dbField.value) field.name else dbField.value
                if (set.isEmpty() || set.contains(columnName))
                    setFieldValue(obj, field, columnName, cursor)
            }
        }
        return obj
    }

    /**
     * 查询记录的值，赋值给clazz的实例obj
     *
     * @param clazz
     * @param cursor
     * @param <T>
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
    </T> */
    @Throws(InstantiationException::class, IllegalAccessException::class, NoSuchMethodException::class, InvocationTargetException::class)
    @JvmStatic fun <T> cursorToClassObject(clazz: Class<T>, cursor: Cursor, columns: Array<out String>?): T {
        val constructor = clazz.getDeclaredConstructor()
        constructor.isAccessible = true
        val obj = constructor.newInstance() as T
        return cursorToObject(obj, cursor, columns)
    }

    /**
     * field 赋值
     *
     * @param t
     * @param field
     * @param columnName
     * @param cursor
     * @param <T>
    </T> */
    @JvmStatic fun <T> setFieldValue(t: T, field: Field, columnName: String?, cursor: Cursor) {
        try {
            if (field.type == String::class.java) {
                field.set(t, cursor.getString(cursor.getColumnIndex(columnName)))
            } else if (field.type == Int::class.java || field.type == Int::class.javaPrimitiveType) {
                field.set(t, cursor.getInt(cursor.getColumnIndex(columnName)))
            } else if (field.type == Long::class.java || field.type == Long::class.javaPrimitiveType) {
                field.set(t, cursor.getLong(cursor.getColumnIndex(columnName)))
            } else if (field.type == Double::class.java || field.type == Double::class.javaPrimitiveType) {
                field.set(t, cursor.getDouble(cursor.getColumnIndex(columnName)))
            } else if (field.type == Boolean::class.java || field.type == Boolean::class.javaPrimitiveType) {
                field.set(t, cursor.getInt(cursor.getColumnIndex(columnName)) == 1)
            } else if (field.type == Float::class.java || field.type == Float::class.javaPrimitiveType) {
                field.set(t, cursor.getFloat(cursor.getColumnIndex(columnName)))
            }
        } catch (e: Exception) {
            Log.e(e.message)
        }

    }

}
