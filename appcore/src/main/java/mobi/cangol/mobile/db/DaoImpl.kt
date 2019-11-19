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

import android.annotation.TargetApi
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.os.Build
import android.os.StrictMode
import mobi.cangol.mobile.logging.Log
import java.util.*

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
internal class DaoImpl<T, I>(private val mDatabaseHelper: CoreSQLiteOpenHelper, private val mClazz: Class<T>) : Dao<T, I> {
    private var mTableName: String? = null
    private var mShowSql: Boolean = false

    init {
        val dbTable = mClazz.getAnnotation(DatabaseTable::class.java)
        if (dbTable != null) {
            this.mTableName = if ("" == dbTable.value) mClazz.simpleName else dbTable.value
        } else {
            Log.e("has no Annotation DatabaseTable clazz=" + mClazz.simpleName)
        }
    }

    @Throws(SQLException::class)
    override fun showSql(showSql: Boolean) {
        mShowSql = showSql
    }

    @Throws(SQLException::class)
    override fun create(paramT: T): Int {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        val db = mDatabaseHelper.writableDatabase
        var result: Long = -1
        try {
            db.beginTransaction()
            result = db.insert(mTableName, null, DatabaseUtils.getContentValues(paramT as Any))
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        } finally {
            db.endTransaction()
        }
        StrictMode.setThreadPolicy(oldPolicy)
        return result.toInt()
    }

    @Throws(SQLException::class)
    override fun create(paramTs: Collection<T>): Int {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        val db = mDatabaseHelper.writableDatabase
        var result: Long = -1
        try {
            db.beginTransaction()
            for (paramT in paramTs) {
                result += db.insert(mTableName, null, DatabaseUtils.getContentValues(paramT as Any))
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        } finally {
            db.endTransaction()
        }
        StrictMode.setThreadPolicy(oldPolicy)
        return result.toInt()
    }

    private fun query(db: SQLiteDatabase, queryBuilder: QueryBuilder, vararg columns: String): Cursor {
        if (mShowSql) {
            Log.d(mTableName!!, SQLiteQueryBuilder.buildQueryString(queryBuilder.isDistinctValue(),
                    queryBuilder.getTable(),
                    columns,
                    queryBuilder.getWhere(),
                    queryBuilder.getGroupByValue(),
                    queryBuilder.getHavingValue(),
                    queryBuilder.getOrderByValue(),
                    queryBuilder.getLimitValue()))
        }

        return db.query(queryBuilder.isDistinctValue(),
                queryBuilder.getTable(),
                columns,
                queryBuilder.getSelection(),
                queryBuilder.getSelectionArgs(),
                queryBuilder.getGroupByValue(),
                queryBuilder.getHavingValue(),
                queryBuilder.getOrderByValue(),
                queryBuilder.getLimitValue())
    }

    override fun query(queryBuilder: QueryBuilder, vararg columns: String): List<T> {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        val list = ArrayList<T>()
        try {
            val db = mDatabaseHelper.readableDatabase
            val cursor = query(db, queryBuilder, *columns)
            var obj: T? = null
            while (cursor.moveToNext()) {
                obj = DatabaseUtils.cursorToClassObject(mClazz, cursor, columns)
                list.add(obj)
            }
            cursor.close()
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        }

        StrictMode.setThreadPolicy(oldPolicy)
        return list
    }

    @Throws(SQLException::class)
    override fun queryForId(paramID: I, vararg columns: String): T? {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        var obj: T? = null
        try {
            val db = mDatabaseHelper.readableDatabase
            val queryBuilder = QueryBuilder(mClazz)
            queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz)!!, paramID as Any, "=")
            val cursor = query(db, queryBuilder)
            if (cursor.count > 0 && cursor.moveToFirst()) {
                obj = DatabaseUtils.cursorToClassObject(mClazz, cursor, columns)
            }
            cursor.close()
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        }

        StrictMode.setThreadPolicy(oldPolicy)
        return obj
    }

    @Throws(SQLException::class)
    override fun queryForAll(vararg columns: String): List<T> {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        val list = ArrayList<T>()
        try {
            val db = mDatabaseHelper.readableDatabase
            val queryBuilder = QueryBuilder(mClazz)
            val cursor = query(db, queryBuilder)
            var obj: T
            while (cursor.moveToNext()) {
                obj = DatabaseUtils.cursorToClassObject(mClazz, cursor, columns)
                list.add(obj)
            }
            cursor.close()
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        }

        StrictMode.setThreadPolicy(oldPolicy)
        return list
    }

    @Throws(SQLException::class)
    override fun refresh(paramT: T, vararg columns: String): T? {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        var result: T? = null
        try {
            val db = mDatabaseHelper.readableDatabase
            val queryBuilder = QueryBuilder(mClazz)
            queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz)!!, DatabaseUtils.getIdValue(paramT as Any)!!, "=")
            val cursor = query(db, queryBuilder)
            if (cursor.count > 0 && cursor.moveToFirst()) {
                result = DatabaseUtils.cursorToObject(paramT, cursor, columns)
            }
            cursor.close()
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        }

        StrictMode.setThreadPolicy(oldPolicy)
        return result
    }

    @Throws(SQLException::class)
    override fun update(updateBuilder: UpdateBuilder): Int {
        return mDatabaseHelper.writableDatabase.update(mTableName, updateBuilder.getContentValues(), updateBuilder.getWhere(), updateBuilder.getWhereArgs())
    }

    @Throws(SQLException::class)
    override fun update(paramT: T, vararg columns: String): Int {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        val db = mDatabaseHelper.writableDatabase
        var result = -1
        try {
            result = db.update(mTableName, DatabaseUtils.getContentValues(paramT as Any, columns), DatabaseUtils.getIdColumnName(mClazz)!! + "=?", arrayOf("" + DatabaseUtils.getIdValue(paramT)!!))
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        }

        StrictMode.setThreadPolicy(oldPolicy)
        return result
    }

    @Throws(SQLException::class)
    override fun update(paramTs: Collection<T>, vararg columns: String): Int {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        val db = mDatabaseHelper.writableDatabase
        var result = -1
        try {
            db.beginTransaction()
            for (paramT in paramTs) {
                result += db.update(mTableName, DatabaseUtils.getContentValues(paramT as Any, columns), DatabaseUtils.getIdColumnName(mClazz)!! + "=?", arrayOf("" + DatabaseUtils.getIdValue(paramT)!!))
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        } finally {
            db.endTransaction()
        }
        StrictMode.setThreadPolicy(oldPolicy)
        return result
    }

    @Throws(SQLException::class)
    override fun updateById(paramT: T, paramID: I, vararg columns: String): Int {
        val oldPolicy = StrictMode.allowThreadDiskWrites()

        val db = mDatabaseHelper.writableDatabase
        var result = -1
        try {
            result = db.update(mTableName, DatabaseUtils.getContentValues(paramT as Any, columns), DatabaseUtils.getIdColumnName(mClazz)!! + "=?", arrayOf("" + paramID))
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        }

        StrictMode.setThreadPolicy(oldPolicy)
        return result
    }

    @Throws(SQLException::class)
    override fun delete(paramT: T): Int {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        val db = mDatabaseHelper.writableDatabase
        var result = -1
        try {
            result = db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)!! + "=?", arrayOf("" + DatabaseUtils.getIdValue(paramT as Any)!!))
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        }

        StrictMode.setThreadPolicy(oldPolicy)
        return result
    }

    @Throws(SQLException::class)
    override fun delete(paramCollection: Collection<T>): Int {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        val db = mDatabaseHelper.writableDatabase
        var result = 0
        try {
            db.beginTransaction()
            for (t in paramCollection) {
                result += db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)!! + "=?", arrayOf("" + DatabaseUtils.getIdValue(t as Any)!!))
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            throw SQLException(mTableName, e)
        } finally {
            db.endTransaction()
        }
        StrictMode.setThreadPolicy(oldPolicy)
        return result
    }

    @Throws(SQLException::class)
    override fun delete(deleteBuilder: DeleteBuilder): Int {
        return mDatabaseHelper.writableDatabase.delete(mTableName, deleteBuilder.getWhere(), deleteBuilder.getWhereArgs())
    }

    @Throws(SQLException::class)
    override fun deleteById(paramID: I): Int {
        return mDatabaseHelper.writableDatabase.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)!! + "=?", arrayOf("" + paramID))
    }

    @Throws(SQLException::class)
    override fun deleteByIds(paramCollection: Collection<I>): Int {
        val db = mDatabaseHelper.writableDatabase
        var result = 0
        try {
            db.beginTransaction()
            for (id in paramCollection) {
                result += db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)!! + "=?", arrayOf("" + id))
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            throw SQLException("$mTableName error=$e", e)
        }

        return result
    }

    @Throws(SQLException::class)
    override fun deleteAll(): Int {
        val db = mDatabaseHelper.writableDatabase
        var result = 0
        try {
            db.beginTransaction()
            result = db.delete(mTableName, null, null)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            throw SQLException("$mTableName error=$e", e)
        } finally {
            db.endTransaction()
        }
        return result
    }

    override fun getEntityClass(): Class<T> {
        return mClazz
    }
}
