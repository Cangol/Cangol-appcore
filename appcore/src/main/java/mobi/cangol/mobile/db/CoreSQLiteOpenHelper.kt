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

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.StrictMode
import mobi.cangol.mobile.logging.Log

abstract class CoreSQLiteOpenHelper {

    /**
     * 数据库创建帮手
     */
    private var mDbHelper: CreateDBHelper? = null

    /**
     * 获得数据库名称
     *
     * @return
     */
    protected abstract fun getDataBaseName(): String

    /**
     * 获得数据库版本，值至少为1。 当数据库结构发生改变的时候，请将此值加1，系统会在初始化时自动调用
     * createDBTables和dropDBTables方法更新数据库结构。
     *
     * @return
     */
    protected abstract fun getDataBaseVersion(): Int

    /**
     * 获取读写数据库
     *
     * @return
     */
    val writableDatabase: SQLiteDatabase
        get() {
            if (mDbHelper != null) {
                var oldPolicy = StrictMode.allowThreadDiskWrites()
                val database = mDbHelper!!.writableDatabase
                StrictMode.setThreadPolicy(oldPolicy)
                return database
            } else {
                throw IllegalStateException("mDbHelper==null,please invoke open method")
            }
        }

    /**
     * 获取只读数据库
     *
     * @return
     */
    val readableDatabase: SQLiteDatabase
        get() {
            if (mDbHelper != null) {
                var oldPolicy = StrictMode.allowThreadDiskReads()
                val database = mDbHelper!!.readableDatabase
                StrictMode.setThreadPolicy(oldPolicy)
                return database
            } else {
                throw IllegalStateException("mDbHelper==null,please invoke open method")
            }
        }

    /**
     * 创建数据库
     *
     * @param db
     */
    abstract fun onCreate(db: SQLiteDatabase)

    /**
     * 升级数据库
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    abstract fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)

    /**
     * 初始化数据库
     *
     * @param context
     */
    fun open(context: Context) {
        Log.i("Open database '${getDataBaseName()}'")
        mDbHelper = CreateDBHelper(context)
    }

    /**
     * 获取对象dao
     *
     * @param clazz
     * @return
     */
    fun <T, I> getDao(clazz: Class<T>): Dao<T, I> {
        return DaoImpl(this, clazz)
    }

    /**
     * 关闭
     */
    fun close() {
        if (mDbHelper != null) {
            Log.i("Close database '${getDataBaseName()}'")
            mDbHelper!!.close()
        }
    }

    /**
     *
     *
     */
    inner class CreateDBHelper(context: Context) : SQLiteOpenHelper(context, this@CoreSQLiteOpenHelper.getDataBaseName(), null, this@CoreSQLiteOpenHelper.getDataBaseVersion()) {

        override fun onCreate(db: SQLiteDatabase) {
            this@CoreSQLiteOpenHelper.onCreate(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            this@CoreSQLiteOpenHelper.onUpgrade(db, oldVersion, newVersion)
        }
    }

}
