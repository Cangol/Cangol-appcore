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
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;
import android.os.StrictMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mobi.cangol.mobile.logging.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class DaoImpl<T, I> implements Dao<T, I> {
    private CoreSQLiteOpenHelper mDatabaseHelper;
    private String mTableName;
    private Class<T> mClazz;
    private boolean mShowSql;

    public DaoImpl(CoreSQLiteOpenHelper databaseHelper, Class<T> clazz) {
        this.mDatabaseHelper = databaseHelper;
        this.mClazz = clazz;
        final DatabaseTable dbTable = clazz.getAnnotation(DatabaseTable.class);
        if (dbTable != null) {
            this.mTableName = "".equals(dbTable.value()) ? clazz.getSimpleName() : dbTable.value();
        } else {
            Log.e("has no Annotation DatabaseTable clazz=" + clazz.getSimpleName());
        }
    }

    @Override
    public void showSql(boolean showSql) throws SQLException {
        mShowSql = showSql;
    }

    @Override
    public int create(T paramT) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long result = -1;
        try {
            db.beginTransaction();
            result = db.insert(mTableName, null, DatabaseUtils.getContentValues(paramT));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        } finally {
            db.endTransaction();
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return (int) result;
    }

    @Override
    public int create(Collection<T> paramTs) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long result = -1;
        try {
            db.beginTransaction();
            for (final T paramT : paramTs) {
                result = result + db.insert(mTableName, null, DatabaseUtils.getContentValues(paramT));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        } finally {
            db.endTransaction();
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return (int) result;
    }

    private Cursor query(SQLiteDatabase db, QueryBuilder queryBuilder, String... columns) {
        if (mShowSql) {
            Log.d(mTableName, SQLiteQueryBuilder.buildQueryString(queryBuilder.isDistinctValue(),
                    queryBuilder.getTable(),
                    columns,
                    queryBuilder.getWhere(),
                    queryBuilder.getGroupByValue(),
                    queryBuilder.getHavingValue(),
                    queryBuilder.getOrderByValue(),
                    queryBuilder.getLimitValue()));
        }

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
    public List<T> query(QueryBuilder queryBuilder, String... columns) {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        final ArrayList<T> list = new ArrayList<>();
        try {
           final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            final Cursor cursor = query(db, queryBuilder, columns);
            T obj = null;
            while (cursor.moveToNext()) {
                obj = DatabaseUtils.cursorToClassObject(mClazz, cursor, columns);
                list.add(obj);
            }
            cursor.close();
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return list;
    }

    @Override
    public T queryForId(I paramID, String... columns) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        T obj = null;
        try {
            final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            final QueryBuilder queryBuilder = new QueryBuilder(mClazz);
            queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz), paramID, "=");
            final Cursor cursor = query(db, queryBuilder);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                obj = DatabaseUtils.cursorToClassObject(mClazz, cursor, columns);
            }
            cursor.close();
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return obj;
    }

    @Override
    public List<T> queryForAll(String... columns) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        final ArrayList<T> list = new ArrayList<>();
        try {
            final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            final QueryBuilder queryBuilder = new QueryBuilder(mClazz);
            final Cursor cursor = query(db, queryBuilder);
            T obj;
            while (cursor.moveToNext()) {
                obj = DatabaseUtils.cursorToClassObject(mClazz, cursor, columns);
                list.add(obj);
            }
            cursor.close();
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return list;
    }

    @Override
    public T refresh(T paramT, String... columns) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        T result = null;
        try {
            final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            final QueryBuilder queryBuilder = new QueryBuilder(mClazz);
            queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz), DatabaseUtils.getIdValue(paramT), "=");
            final Cursor cursor = query(db, queryBuilder);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                result = DatabaseUtils.cursorToObject(paramT, cursor, columns);
            }
            cursor.close();
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int update(UpdateBuilder updateBuilder) throws SQLException {
        return mDatabaseHelper.getWritableDatabase().update(mTableName, updateBuilder.getContentValues(), updateBuilder.getWhere(), updateBuilder.getWhereArgs());
    }

    @Override
    public int update(T paramT, String... columns) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = -1;
        try {
            result = db.update(mTableName, DatabaseUtils.getContentValues(paramT, columns), DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + DatabaseUtils.getIdValue(paramT)});
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int update(Collection<T> paramTs, String... columns) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = -1;
        try {
            db.beginTransaction();
            for (final T paramT : paramTs) {
                result = result + db.update(mTableName, DatabaseUtils.getContentValues(paramT, columns), DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + DatabaseUtils.getIdValue(paramT)});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        } finally {
            db.endTransaction();
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int updateById(T paramT, I paramID, String... columns) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();

        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = -1;
        try {
            result = db.update(mTableName, DatabaseUtils.getContentValues(paramT, columns), DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + paramID});
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int delete(T paramT) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = -1;
        try {
            result = db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + DatabaseUtils.getIdValue(paramT)});
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int delete(Collection<T> paramCollection) throws SQLException {
        final StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();
            for (final T t : paramCollection) {
                result = result + db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + DatabaseUtils.getIdValue(t)});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName, e);
        } finally {
            db.endTransaction();
        }
        StrictMode.setThreadPolicy(oldPolicy);
        return result;
    }

    @Override
    public int delete(DeleteBuilder deleteBuilder) throws SQLException {
        return mDatabaseHelper.getWritableDatabase().delete(mTableName, deleteBuilder.getWhere(), deleteBuilder.getWhereArgs());
    }

    @Override
    public int deleteById(I paramID) throws SQLException {
        return mDatabaseHelper.getWritableDatabase().delete(mTableName, DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + paramID});
    }

    @Override
    public int deleteByIds(Collection<I> paramCollection) throws SQLException {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();
            for (final I id : paramCollection) {
                result = result + db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz) + "=?", new String[]{"" + id});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e, e);
        }
        return result;
    }

    @Override
    public int deleteAll() throws SQLException {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();
            result = db.delete(mTableName, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new SQLException(mTableName + " error=" + e, e);
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
