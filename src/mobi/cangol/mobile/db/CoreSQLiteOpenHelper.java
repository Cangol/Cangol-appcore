package mobi.cangol.mobile.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoreSQLiteOpenHelper extends SQLiteOpenHelper {

	public CoreSQLiteOpenHelper(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try{
			//DbUtils.createTable(db, clzss);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		try{
			
			if(oldVersion<14&&newVersion<15){
				//DbUtils.dropTable(db, clzss);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public <T> Dao<T, ?> getDao(Class<T> clazz) {
		return new DaoImpl(this,clazz);
	}
}
