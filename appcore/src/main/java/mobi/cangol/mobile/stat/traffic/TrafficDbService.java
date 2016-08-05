/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.stat.traffic;

import android.content.Context;
import android.database.SQLException;

import java.util.List;

import mobi.cangol.mobile.db.Dao;
import mobi.cangol.mobile.db.QueryBuilder;
import mobi.cangol.mobile.logging.Log;

/**
 * Created by weixuewu on 16/1/23.
 */
class TrafficDbService {
    private Dao<DateTraffic, Integer> dateTrafficDao;
    private Dao<AppTraffic, Integer> appTrafficDao;

    public TrafficDbService(Context context) {
        try {
            StatDatabaseHelper dbHelper = StatDatabaseHelper
                    .createDataBaseHelper(context);
            dateTrafficDao = dbHelper.getDao(DateTraffic.class);
            appTrafficDao = dbHelper.getDao(AppTraffic.class);
        } catch (SQLException e) {
            Log.e("TrafficDbService init fail!" + e.getMessage());
        }
    }

    public int saveAppTraffic(AppTraffic obj) {
        int result = -1;
        try {
            if (obj._id > 0 && obj._id != -1) {
                result = appTrafficDao.update(obj);
                if (result > 0) {
                    result = obj._id;
                }
            } else {
                result = appTrafficDao.create(obj);
            }
        } catch (SQLException e) {
            Log.e("TrafficDbService saveAppTraffic fail! " + e.getMessage());
        }
        return result;
    }

    public int saveDateTraffic(DateTraffic obj) {
        int result = -1;
        try {
            if (obj._id > 0 && obj._id != -1) {
                result = dateTrafficDao.update(obj);
                if (result > 0) {
                    result = obj._id;
                }
            } else {
                result = dateTrafficDao.create(obj);
            }
        } catch (SQLException e) {
            Log.e("TrafficDbService saveDateTraffic fail! " + e.getMessage());
        }
        return result;
    }

    public DateTraffic getDateTrafficByDate(int uid, String date) {
        QueryBuilder queryBuilder = new QueryBuilder(DateTraffic.class);
        queryBuilder.addQuery("uid", uid, "=");
        queryBuilder.addQuery("date", date, "=");
        List<DateTraffic> list = dateTrafficDao.query(queryBuilder);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }

    }

    public AppTraffic getAppTraffic(int uid) {
        QueryBuilder queryBuilder = new QueryBuilder(AppTraffic.class);
        queryBuilder.addQuery("uid", uid, "=");
        List<AppTraffic> list = appTrafficDao.query(queryBuilder);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }

    }

    public List<AppTraffic> getAppTrafficList() {
        return appTrafficDao.queryForAll();
    }

    public List<DateTraffic> getDateTrafficByStatus(int uid, String date, int status) {
        QueryBuilder queryBuilder = new QueryBuilder(DateTraffic.class);
        queryBuilder.addQuery("uid", uid, "=");
        queryBuilder.addQuery("date", date, "<");
        queryBuilder.addQuery("status", status, "=");
        queryBuilder.orderBy("date asc");
        return dateTrafficDao.query(queryBuilder);
    }
}
