/**
 * Copyright (c) 2013 Cangol
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class CoreSQLiteOpenHelper {
	private static final String TAG = "CoreSQLiteOpenHelper";

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
	/**
	 * 创建数据库
	 * @param db
	 */
	public abstract void onCreate(SQLiteDatabase db);
	/**
	 * 升级数据库
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 */
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
	/**
	 * 初始化数据库
	 * @param context
	 */
	public void open(Context context) {
		Log.i(TAG, "Open database '" + getDataBaseName() + "'");
		mDbHelper = new CreateDBHelper(context);
	}
	/**
	 * 获取读写数据库
	 * @return
	 */
	public SQLiteDatabase getWritableDatabase() {
		if(mDbHelper!=null){
			return mDbHelper.getWritableDatabase();
		}else{
			throw new IllegalStateException("mDbHelper==null,please invoke open method");
		}
	}
	/**
	 * 获取只读数据库
	 * @return
	 */
	public SQLiteDatabase getReadableDatabase() {
		if(mDbHelper!=null){
			return mDbHelper.getReadableDatabase();
		}else{
			throw new IllegalStateException("mDbHelper==null,please invoke open method");
		}
	}
	/**
	 * 获取对象dao
	 * @param clazz
	 * @return
	 */
	public <T, ID> Dao<T, ID> getDao(Class<T> clazz) {
		return new DaoImpl<T, ID>(this,clazz);	
	}
	/**
	 * 关闭
	 */
	public void close() {
		if (mDbHelper != null) {
			Log.i(TAG, "Close database '" + getDataBaseName() + "'");
			mDbHelper.close();
		}
	}
}
