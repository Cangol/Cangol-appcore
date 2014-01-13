package mobi.cangol.mobile.db;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtils {
	
	public static void createTable(SQLiteDatabase db,Class<?> clazz) {
		StringBuilder sql=new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		DatabaseTable table = clazz.getAnnotation(DatabaseTable.class);
		sql.append(""+table.value()+"(");
		Field[] fields = clazz.getDeclaredFields();
		String filedName=null;
		boolean isFirst=true;
		for (Field field : fields) {
			if(!isFirst){
				sql.append(",");  
			}else
				isFirst=false;
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (field.isAnnotationPresent(DatabaseField.class)){
				DatabaseField dbField = field.getAnnotation(DatabaseField.class);
				filedName="".equals(dbField.value())?field.getName():dbField.value();
				sql.append(""+filedName+"");
				sql.append(" "+getDbType(field.getType()));
				if(dbField.primaryKey()) {
		             sql.append(" PRIMARY KEY");  
		        }  
		        if(dbField.notNull()) { 
		             sql.append(" NOT NULL");  
		        }
			}
		}
		sql.append(")");
		db.execSQL(sql.toString());
	}
	
	public static String getDbType(Class<?> clazz) {
		if(clazz == String.class 
				|| clazz == Character.class || clazz == char.class 
				|| clazz == Boolean.class || clazz == boolean.class) {
			return "TEXT";
		}else if(clazz == Integer.class || clazz == int.class 
				|| clazz == Long.class || clazz == long.class				
				|| clazz == Short.class || clazz == short.class) {
			return "INTEGER";
		}else if(clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class) {
			return "REAL";
		}else {
			return "BLOB";
		}
	}
	
	public static void dropTable(SQLiteDatabase db,Class<?> clazz) {
		StringBuilder sql=new StringBuilder("DROP TABLE IF EXISTS ");
		DatabaseTable table = clazz.getAnnotation(DatabaseTable.class);
		sql.append(""+table.value());
		db.execSQL(sql.toString());
	}
	public static <T> String getId(T paramT){
		
		return "";
	}
	
	public static ContentValues getContentValues(Object object) throws IllegalAccessException, IllegalArgumentException{
		ContentValues v = new ContentValues();
		for( Field field : object.getClass().getFields() ) {	
			if(field.isAnnotationPresent(DatabaseField.class)) {
				DatabaseField dbField = field.getAnnotation(DatabaseField.class);
				String filedName="".equals(dbField.value())?field.getName():dbField.value();
				v.put(filedName, field.get(object).toString());
			}
		}
		return v;
	}
}
