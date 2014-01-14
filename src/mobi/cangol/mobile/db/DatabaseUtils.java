package mobi.cangol.mobile.db;

import java.lang.reflect.Field;

import mobi.cangol.mobile.logging.Log;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtils {
	
	public static void createTable(SQLiteDatabase db,Class<?> clazz) {
		if (clazz.isAnnotationPresent(DatabaseTable.class)){
			StringBuilder sql=new StringBuilder("CREATE TABLE IF NOT EXISTS ");
			DatabaseTable table = clazz.getAnnotation(DatabaseTable.class);
			String tableName="".equals(table.value())?clazz.getSimpleName():table.value();
			sql.append(""+tableName+"(");
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
			             sql.append(" PRIMARY KEY AUTOINCREMENT");  
			        }  
			        if(dbField.notNull()) { 
			             sql.append(" NOT NULL");  
			        }
				}
			}
			sql.append(")");
			Log.d(""+sql.toString());
			db.execSQL(sql.toString());	
		}else{
			throw new IllegalStateException(clazz+" not DatabaseTable Annotation");
		}
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
		if (clazz.isAnnotationPresent(DatabaseTable.class)){
			StringBuilder sql=new StringBuilder("DROP TABLE IF EXISTS ");
			DatabaseTable table = clazz.getAnnotation(DatabaseTable.class);
			String tableName="".equals(table.value())?clazz.getSimpleName():table.value();
			sql.append(""+tableName);
			db.execSQL(sql.toString());
		}else{
			throw new IllegalStateException(clazz+" not DatabaseTable Annotation");
		}
	}
	public static <T> String getIdColumnName(Class<?> clazz){
		String columnName=null;
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (field.isAnnotationPresent(DatabaseField.class)) {
				DatabaseField dbField = field.getAnnotation(DatabaseField.class);
				if(dbField.primaryKey()==true){
					columnName="".equals(dbField.value())?field.getName():dbField.value();
					break ;
				}
			}
		}
		return columnName;
	}
	public static Object getIdValue(Object obj) throws IllegalAccessException, IllegalArgumentException{
		Object value=null;
		for (Field field : obj.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (field.isAnnotationPresent(DatabaseField.class)) {
				DatabaseField dbField = field.getAnnotation(DatabaseField.class);
				if(dbField.primaryKey()==true){
					value= field.get(obj);
					break ;
				}
			}
		}
		return value;
	}
	
	public static ContentValues getContentValues(Object object) throws IllegalAccessException, IllegalArgumentException{
		ContentValues v = new ContentValues();
		for( Field field : object.getClass().getDeclaredFields() ) {	
			field.setAccessible(true);
			if(field.isAnnotationPresent(DatabaseField.class)) {
				DatabaseField dbField = field.getAnnotation(DatabaseField.class);
				if(!dbField.primaryKey()){
					String filedName="".equals(dbField.value())?field.getName():dbField.value();
					v.put(filedName, String.valueOf(field.get(object)));
				}
			}
		}
		return v;
	}
	
	public static <T> T cursorToObject(T obj,Cursor cursor) throws InstantiationException, IllegalAccessException {
		while(cursor.moveToNext()){
			Field[] fields = obj.getClass().getDeclaredFields();
			String columnName=null;
			for (Field field : fields) {
				field.setAccessible(true);
				if(field.isEnumConstant())continue;
				if (field.isAnnotationPresent(DatabaseField.class)) {
					DatabaseField dbField = field.getAnnotation(DatabaseField.class);
					columnName="".equals(dbField.value())?field.getName():dbField.value();
					setValue(obj,field,columnName,cursor);
				}
			}
		}
		return obj;
	}
	
	public static <T> T cursorToObject(Class<T> clazz,Cursor cursor) throws InstantiationException, IllegalAccessException {
		T obj=clazz.newInstance();
		Field[] fields = clazz.getDeclaredFields();
		String columnName=null;
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.isEnumConstant())continue;
			if (field.isAnnotationPresent(DatabaseField.class)) {
				DatabaseField dbField = field.getAnnotation(DatabaseField.class);
				columnName="".equals(dbField.value())?field.getName():dbField.value();
				setValue(obj,field,columnName,cursor);
			}
		}
		return obj;
	}
	
	public static <T> void setValue(T t,Field field,String columnName,Cursor cursor){
		try{
			if(field.getType()==String.class){
				field.set(t, cursor.getString(cursor.getColumnIndex(columnName)));
			}else if(field.getType()==Integer.class||field.getType()==int.class){
				field.set(t, cursor.getInt(cursor.getColumnIndex(columnName)));
			}else if(field.getType()==Long.class||field.getType()==long.class){
				field.set(t, cursor.getLong(cursor.getColumnIndex(columnName)));
			}else if(field.getType()==Double.class||field.getType()==double.class){
				field.set(t, cursor.getDouble(cursor.getColumnIndex(columnName)));
			}else if(field.getType()==Boolean.class||field.getType()==boolean.class){
				field.set(t, cursor.getInt(cursor.getColumnIndex(columnName))==1?true:false);
			}else if(field.getType()==Float.class||field.getType()==float.class){
				field.set(t, cursor.getFloat(cursor.getColumnIndex(columnName)));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
