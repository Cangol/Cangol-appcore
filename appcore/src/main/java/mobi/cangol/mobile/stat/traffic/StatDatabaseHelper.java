/**
 * Copyright (c) 2013 Cangol
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.stat.traffic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import mobi.cangol.mobile.db.CoreSQLiteOpenHelper;
import mobi.cangol.mobile.db.DatabaseUtils;
import mobi.cangol.mobile.logging.Log;

/**
 * Created by weixuewu on 16/1/23.
 */
class StatDatabaseHelper extends CoreSQLiteOpenHelper {
    private static final String DATABASE_NAME = "stat_traffic";
    private static final int DATABASE_VERSION = 1;
    private static StatDatabaseHelper dataBaseHelper;

    public static StatDatabaseHelper createDataBaseHelper(Context context) {
        if (null == dataBaseHelper) {
            dataBaseHelper = new StatDatabaseHelper();
            dataBaseHelper.open(context);
        }
        return dataBaseHelper;
    }

    @Override
    public String getDataBaseName() {
        return DATABASE_NAME;
    }

    @Override
    public int getDataBaseVersion() {
        return DATABASE_VERSION;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("onCreate");
        DatabaseUtils.createTable(db, AppTraffic.class);
        DatabaseUtils.createTable(db, DateTraffic.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("onUpgrade " + oldVersion + "->" + newVersion);
        if (db.getVersion() < DATABASE_VERSION) {
            //DatabaseUtils.dropTable(db, Goddess.class);
        }
    }
}
