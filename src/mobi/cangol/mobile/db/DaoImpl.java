package mobi.cangol.mobile.db;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

class DaoImpl<T,ID> implements Dao<T, ID> {
	private CoreSQLiteOpenHelper mDatabaseHelper;
	private DatabaseTable mDbtable;
	public DaoImpl(CoreSQLiteOpenHelper databaseHelper,T t){
		this.mDatabaseHelper=databaseHelper;
		mDbtable = t.getClass().getAnnotation(DatabaseTable.class);
	}
	
//	public Cursor query(QueryBuilder queryBuilder){
//		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
//		return db.query(, queryBuilder.getTable(), columns, selection, selectionArgs, queryBuilder.getGroupBy(), queryBuilder.getHaving(), queryBuilder.getOrderBy(), queryBuilder.getLimit(), cancellationSignal)
//	}
	
	@Override
	public T queryForId(ID paramID) throws SQLException {
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			QueryBuilder queryBuilder=new QueryBuilder(null);
			//Cursor cursor=query(queryBuilder);
			
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<T> queryForAll() throws SQLException {
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<T> queryForFieldValues(Map<String, Object> paramMap)
			throws SQLException {
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<T> queryForFieldValuesArgs(Map<String, Object> paramMap)
			throws SQLException {
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int create(T paramT) throws SQLException {
		long result=-1;
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			result=db.insert(mDbtable.value(), "", DatabaseUtils.getContentValues(paramT));
			db.close();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return (int)result;
	}

	@Override
	public T createIfNotExists(T paramT) throws SQLException {
		
		try {
			SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
			
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int update(T paramT) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		try {
			result = db.update(mDbtable.value(), DatabaseUtils.getContentValues(paramT), "id=?",new String[]{DatabaseUtils.getId(paramT)});
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
			result = db.update(mDbtable.value(), DatabaseUtils.getContentValues(paramT), "id=?",new String[]{""+paramID});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		db.close();
		return result;
	}

	@Override
	public int refresh(T paramT) throws SQLException {
		return 0;
	}

	@Override
	public int delete(T paramT) throws SQLException {
		
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result = db.delete(mDbtable.value(), "id=?",new String[]{DatabaseUtils.getId(paramT)});
		db.close();
		return result;
	}

	@Override
	public int delete(Collection<T> paramCollection) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		for(T t:paramCollection){
			result = db.delete(mDbtable.value(), "id=?",new String[]{DatabaseUtils.getId(t)});
		}
		db.close();
		return result;
	}

	@Override
	public int deleteById(ID paramID) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result = db.delete(mDbtable.value(), "id=?",new String[]{""+paramID});
		db.close();
		return result;
	}

	@Override
	public int deleteByIds(Collection<ID> paramCollection) throws SQLException {
		SQLiteDatabase db=mDatabaseHelper.getWritableDatabase();
		int result=-1;
		for(ID id:paramCollection){
			db.delete(mDbtable.value(), "id=?",new String[]{""+id});
		}
		db.close();
		return result;
	}

	@Override
	public String objectToString(T paramT) {
		return null;
	}

	@Override
	public Class<T> getDataClass() {
		return null;
	}

}
