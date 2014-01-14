package mobi.cangol.mobile.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

class DaoImpl<T,ID> implements Dao<T, ID> {
	private CoreSQLiteOpenHelper mDatabaseHelper;
	private String mTableName;
	private Class<T> mClazz;
	
	public DaoImpl(CoreSQLiteOpenHelper databaseHelper,Class<T> clazz){
		this.mDatabaseHelper=databaseHelper;
		this.mClazz=clazz;
		DatabaseTable dbtable = clazz.getAnnotation(DatabaseTable.class);
		this.mTableName="".equals(dbtable.value())?clazz.getSimpleName():dbtable.value();
	}
	
	public Cursor query(SQLiteDatabase db,QueryBuilder queryBuilder){
		return db.query(queryBuilder.isDistinct(),
				queryBuilder.getTable(), 	
				null,
				queryBuilder.getSelection(), 
				queryBuilder.getSelectionArgs(),
				queryBuilder.getGroupBy(),
				queryBuilder.getHaving(),
				queryBuilder.getOrderBy(),
				queryBuilder.getLimit());
	}
		
	@Override
	public List<T> query(QueryBuilder queryBuilder){
		ArrayList<T> list=new ArrayList<T>();
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			Cursor cursor=query(db,queryBuilder);
			T obj=null;
			while(cursor.moveToNext()){
				obj=DatabaseUtils.cursorToObject(mClazz,cursor);
				list.add(obj);
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public T queryForId(ID paramID) throws SQLException {
		T obj=null;	
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			QueryBuilder queryBuilder=new QueryBuilder(mClazz);
			queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz), paramID, "=");
			Cursor cursor=query(db,queryBuilder);
			if(cursor.getCount()>0)
			obj=DatabaseUtils.cursorToObject(mClazz,cursor);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public List<T> queryForAll() throws SQLException {
		ArrayList<T> list=new ArrayList<T>();
		try {
			SQLiteDatabase db=mDatabaseHelper.getReadableDatabase();
			QueryBuilder queryBuilder=new QueryBuilder(mClazz);
			Cursor cursor=query(db,queryBuilder);
			T obj=null;
			while(cursor.moveToNext()){
				obj=DatabaseUtils.cursorToObject(mClazz,cursor);
				list.add(obj);
			}
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public int refresh(T paramT) throws SQLException {
		int result=-1;
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			QueryBuilder queryBuilder=new QueryBuilder(mClazz);
			queryBuilder.addQuery(DatabaseUtils.getIdColumnName(mClazz), DatabaseUtils.getIdValue(paramT), "=");
			Cursor cursor=query(db,queryBuilder);
			result=cursor.getCount();
			paramT=DatabaseUtils.cursorToObject(paramT,cursor);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	@Override
	public int create(T paramT) throws SQLException {
		long result=-1;
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			result=db.insert(mTableName,null, DatabaseUtils.getContentValues(paramT));
			db.close();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return (int)result;
	}

	@Override
	public int update(T paramT) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		try {
			result = db.update(mTableName, DatabaseUtils.getContentValues(paramT), DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+DatabaseUtils.getIdValue(paramT)});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		db.close();
		return result;
	}	

	@Override
	public int updateId(T paramT, ID paramID) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		try {
			result = db.update(mTableName, DatabaseUtils.getContentValues(paramT), DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+paramID});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		db.close();
		return result;
	}

	@Override
	public int delete(T paramT) throws SQLException {
		
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		try {
			result = db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+DatabaseUtils.getIdValue(paramT)});
			db.close();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int delete(Collection<T> paramCollection) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		try {
			for(T t:paramCollection){
				result = db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+DatabaseUtils.getIdValue(t)});
			}
			db.close();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int deleteById(ID paramID) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result = db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+paramID});
		db.close();
		return result;
	}

	@Override
	public int deleteByIds(Collection<ID> paramCollection) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		for(ID id:paramCollection){
			db.delete(mTableName, DatabaseUtils.getIdColumnName(mClazz)+"=?",new String[]{""+id});
		}
		db.close();
		return result;
	}

	@Override
	public Class<T> getDataClass() {
		return mClazz;
	}
	
}
