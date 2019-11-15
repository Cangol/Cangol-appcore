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
package mobi.cangol.mobile.stat.traffic

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import mobi.cangol.mobile.db.CoreSQLiteOpenHelper
import mobi.cangol.mobile.db.DatabaseUtils
import mobi.cangol.mobile.logging.Log

/**
 * Created by weixuewu on 16/1/23.
 */
class StatDatabaseHelper : CoreSQLiteOpenHelper() {

     override fun getDataBaseName(): String {
        return DATABASE_NAME
    }

    override fun getDataBaseVersion(): Int {
        return DATABASE_VERSION
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("onCreate")
        DatabaseUtils.createTable(db, AppTraffic::class.java)
        DatabaseUtils.createTable(db, DateTraffic::class.java)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("onUpgrade $oldVersion->$newVersion")
        if (db.version < DATABASE_VERSION) {
            //do nothings
        }
    }

    companion object {
        private val DATABASE_NAME = "stat_traffic"
        private val DATABASE_VERSION = 1
        private var dataBaseHelper: StatDatabaseHelper? = null

        fun createDataBaseHelper(context: Context): StatDatabaseHelper {
            if (null == dataBaseHelper) {
                dataBaseHelper = StatDatabaseHelper()
                dataBaseHelper!!.open(context)
            }
            return dataBaseHelper!!
        }
    }
}
