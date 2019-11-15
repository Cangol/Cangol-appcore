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
import android.database.SQLException

import mobi.cangol.mobile.db.Dao
import mobi.cangol.mobile.db.QueryBuilder
import mobi.cangol.mobile.logging.Log

/**
 * Created by weixuewu on 16/1/23.
 */
internal class TrafficDbService(context: Context) {
    private var dateTrafficDao: Dao<DateTraffic, Int>? = null
    private var appTrafficDao: Dao<AppTraffic, Int>? = null

    init {
        try {
            val dbHelper = StatDatabaseHelper
                    .createDataBaseHelper(context.applicationContext)
            dateTrafficDao = dbHelper.getDao(DateTraffic::class.java)
            appTrafficDao = dbHelper.getDao(AppTraffic::class.java)
        } catch (e: SQLException) {
            Log.e("TrafficDbService init fail!" + e.message)
        }

    }

    fun getAppTrafficList(): List<AppTraffic> {
        return appTrafficDao!!.queryForAll()
    }

    fun saveAppTraffic(obj: AppTraffic): Int {
        var result = -1
        try {
            if (obj.id > 0 && obj.id != -1) {
                result = appTrafficDao!!.update(obj)
                if (result > 0) {
                    result = obj.id
                }
            } else {
                result = appTrafficDao!!.create(obj)
            }
        } catch (e: SQLException) {
            Log.e("TrafficDbService saveAppTraffic fail! " + e.message)
        }

        return result
    }

    fun saveDateTraffic(obj: DateTraffic): Int {
        var result = -1
        try {
            if (obj.id > 0 && obj.id != -1) {
                result = dateTrafficDao!!.update(obj)
                if (result > 0) {
                    result = obj.id
                }
            } else {
                result = dateTrafficDao!!.create(obj)
            }
        } catch (e: SQLException) {
            Log.e("TrafficDbService saveDateTraffic fail! " + e.message)
        }

        return result
    }

    fun getDateTrafficByDate(uid: Int, date: String): DateTraffic? {
        val queryBuilder = QueryBuilder(DateTraffic::class.java)
        queryBuilder.addQuery("uid", uid, "=")
        queryBuilder.addQuery("date", date, "=")
        val list = dateTrafficDao!!.query(queryBuilder)
        return if (!list.isEmpty()) {
            list[0]
        } else {
            null
        }

    }

    fun getAppTraffic(uid: Int): AppTraffic? {
        val queryBuilder = QueryBuilder(AppTraffic::class.java)
        queryBuilder.addQuery("uid", uid, "=")
        val list = appTrafficDao!!.query(queryBuilder)
        return if (!list.isEmpty()) {
            list[0]
        } else {
            null
        }

    }

    fun getDateTrafficByStatus(uid: Int, date: String, status: Int): List<DateTraffic> {
        val queryBuilder = QueryBuilder(DateTraffic::class.java)
        queryBuilder.addQuery("uid", uid, "=")
        queryBuilder.addQuery("date", date, "<")
        queryBuilder.addQuery("status", status, "=")
        queryBuilder.orderBy("date asc")
        return dateTrafficDao!!.query(queryBuilder)
    }
}
