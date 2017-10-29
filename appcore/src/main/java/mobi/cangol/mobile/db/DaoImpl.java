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
package mobi.cangol.mobile.db;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.StrictMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mobi.cangol.mobile.logging.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class DaoImpl<T, ID> implements Dao<T, ID> {
    private CoreSQLiteOpenHelper mDatabaseHelper;
    private String mTableName;
    private Class<T> mClazz;

    public DaoImpl(CoreSQLiteOpenHelper databaseHelper, Class<T> clazz) {
        this.mDatabaseHelper = databaseHelper;
        this.mClazz = clazz;
        DatabaseTable dbTable = clazz.getAnnotation(DatabaseTable.class);
        if (dbTable != null) {
            this.mTableName = "".equals(dbTable.value()) ? clazz.getSimpleName() : dbTable.value();
        } else {
            Log.e("has no Annotation DatabaseTable clazz=" + clazz.getSimpleName());
        }
    }

    @Override
    public int create(T paramT) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long result = -1;
        try {
            db.beginTransaction();
            result = db.insert(mTableName, null, DatabaseUtils.getContentValues(paramT));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return (int) result;
    }

    @Override
    public int create(Collection<T> paramTs) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long result = -1;
        try {
            db.beginTransaction();
            for (T paramT : paramTs) {
                result = result + db.insert(mTableName, null, DatabaseUtils.getContentValues(paramT));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return (int) result;
    }

    private Cursor query(SQLiteDatabase db, QueryBuilder queryBuilder,String... columns) {
        return db.query(queryBuilder.isDistinctValue(),
                queryBuilder.getTable(),
                columns,
                queryBuilder.getSelection(),
                queryBuilder.getSelectionArgs(),
                queryBuilder.getGroupByValue(),
                queryBuilder.getHavingValue(),
                queryBuilder.getOrderByValue(),
                queryBuilder.getLimitValue());
    }
    @Override
    public List<T> query(QueryBuilder queryBuilder,String... columns) {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        ArrayList<T> list = new ArrayList<T>();
        try {
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor cursor = query(db, queryBuilder,columns);
            T obj = null;
            while (cursor.moveToNext()) {
                obj = DatabaseUtils.cursorToClassObject(mClazz,cursor,columns);
                list.add(obj);
            }
            cursor.close();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return list;
    }
    @Override
    public T queryForId(ID paramID,String... columns) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        T obj = null;
        try {
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            QueryBuilder queryBuilder = new QueryBuilder(mClazz);
            queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz), paramID, "=");
            Cursor cursor = query(db, queryBuilder);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                obj =  DatabaseUtils.cursorToClassObject(mClazz, cursor,columns);
            }
            cursor.close();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return obj;
    }

    @Override
    public List<T> queryForAll(String... columns) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        ArrayList<T> list = new ArrayList<T>();
        try {
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            QueryBuilder queryBuilder = new QueryBuilder(mClazz);
            Cursor cursor = query(db, queryBuilder);
            T obj = null;
            while (cursor.moveToNext()) {
                obj = DatabaseUtils.cursorToClassObject(mClazz, cursor,columns);
                list.add(obj);
            }
            cursor.close();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return list;
    }
    @Override
    public T refresh(T paramT,String... columns) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        T result = null;
        try {
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            QueryBuilder queryBuilder = new QueryBuilder(mClazz);
            queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz), DatabaseUtils.getIdValue(paramT), "=");
            Cursor cursor = query(db, queryBuilder);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = DatabaseUtils.cursorToObject(paramT, cursor,columns);
            }
            cursor.close();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }
    @Override
    public int update(UpdateBuilder updateBuilder) throws SQLException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = db.update(mTableName,updateBuilder.getContentValues(), updateBuilder.getWhere(), updateBuilder.getWhereArgs());
        return result;
    }

    @Override
    public int update(T paramT,String... columns) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = -1;
        try {
            result = db.update(mTableName, DatabaseUtils.getContentValues(paramT,columns), DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + DatabaseUtils.getIdValue(paramT)});
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int update(Collection<T> paramTs,String... columns) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = -1;
        try {
            db.beginTransaction();
            for (T paramT : paramTs) {
                result = result + db.update(mTableName, DatabaseUtils.getContentValues(paramT,columns), DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + DatabaseUtils.getIdValue(paramT)});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }
    @Override
    public int updateById(T paramT, ID paramID,String... columns) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = -1;
        try {
            result = db.update(mTableName, DatabaseUtils.getContentValues(paramT,columns), DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + paramID});
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int delete(T paramT) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = -1;
        try {
            result = db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + DatabaseUtils.getIdValue(paramT)});
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int delete(Collection<T> paramCollection) throws SQLException {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();
            for (T t : paramCollection) {
                result = result + db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + DatabaseUtils.getIdValue(t)});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int delete(DeleteBuilder deleteBuilder) throws SQLException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = db.delete(mTableName, deleteBuilder.getWhere(), deleteBuilder.getWhereArgs());
        return result;
    }

    @Override
    public int deleteById(ID paramID) throws SQLException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + paramID});
        return result;
    }

    @Override
    public int deleteByIds(Collection<ID> paramCollection) throws SQLException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = 0;
        try {
            for (ID id : paramCollection) {
                result = result + db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + id});
            }
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        }
        return result;
    }

    @Override
    public int deleteAll() throws SQLException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();
            result = db.delete(mTableName, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }

    @Override
    public Class<T> getEntityClass() {
        return mClazz;
    }
}
