package mobi.cangol.mobile.db;

import mobi.cangol.mobile.logging.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class CoreSQLiteOpenHelper {
	private static final String TAG = "CoreSQLiteOpenHelper";
	/**
	 * SQLite数据库实例
	 */
	public SQLiteDatabase mDb = null;

	/**
	 * 数据库创建帮手
	 */
	protected CreateDBHelper mDbHelper = null;
	
	/**
	 * 获得数据库名称
	 * 
	 * @return
	 */
	protected abstract String getDataBaseName();

	/**
	 * 获得数据库版本，值至少为1。 当数据库结构发生改变的时候，请将此值加1，系统会在初始化时自动调用
	 * createDBTables和dropDBTables方法更新数据库结构。
	 * 
	 * @return
	 */
	protected abstract int getDataBaseVersion();
	
	public abstract void onCreate(SQLiteDatabase db);
	
	public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	/**
	 * 
	 * TODO 内部数据库创建帮手类
	 * 
	 */
	private class CreateDBHelper extends SQLiteOpenHelper {
		
		public CreateDBHelper(Context context) {
			super(context, CoreSQLiteOpenHelper.this.getDataBaseName(), null, CoreSQLiteOpenHelper.this.getDataBaseVersion());
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			CoreSQLiteOpenHelper.this.onCreate(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			CoreSQLiteOpenHelper.this.onUpgrade(db,oldVersion,newVersion);
		}
	}
	
	public void open(Context context) {
		Log.i(TAG, "Open database '" + getDataBaseName() + "'");
		mDbHelper = new CreateDBHelper(context);
	}
	
	public SQLiteDatabase getWritableDatabase() {
		if(mDbHelper!=null){
			return mDbHelper.getWritableDatabase();
		}else{
			throw new IllegalStateException("mDbHelper==null,please invoke open method");
		}
	}
	
	public SQLiteDatabase getReadableDatabase() {
		if(mDbHelper!=null){
			return mDbHelper.getReadableDatabase();
		}else{
			throw new IllegalStateException("mDbHelper==null,please invoke open method");
		}
	}
	
	public <T, ID> Dao<T, ID> getDao(Class<T> clazz) {
		return new DaoImpl<T, ID>(this,clazz);	
	}
	
	public void close() {
		if (mDbHelper != null) {
			Log.i(TAG, "Close database '" + getDataBaseName() + "'");
			mDbHelper.close();
		}
	}
}
