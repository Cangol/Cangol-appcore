package mobi.cangol.mobile.utils;

import java.io.File;

import mobi.cangol.mobile.logging.Log;
import android.content.Context;
import android.os.Environment;

/** * 本应用数据清除管理器 */
public class DataCleanManager {
	/** * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context */
	public static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	/** * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context */
	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/databases"));
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
	 * context
	 */
	public static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/shared_prefs"));
	}

	/** * 按名字清除本应用数据库 * * @param context * @param dbName */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
	 * context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	/** * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath */
	public static void cleanCustomCache(String filePath) {
		deleteFilesByDirectory(new File(filePath));
	}
	/** * 清除本应用所有的数据 * * @param context * @param filepath */
	public static void cleanApplicationData(Context context, String... filepath) {
		cleanInternalCache(context);
		cleanExternalCache(context);
		//cleanDatabases(context);
		//cleanSharedPreference(context);
		//cleanFiles(context);
		for (String filePath : filepath) {
			cleanCustomCache(filePath);
		}
	}

	/** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件夹，将递归删除文件 * * @param directory */
	private static void deleteFilesByDirectory(File directory) {
		Log.d("deleteFilesByDirectory="+directory.getAbsolutePath());
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				if(!item.isDirectory()){
					Log.d("delete "+item.getAbsoluteFile()+",result="+item.delete());
				}else{
					deleteFilesByDirectory(item);
				}
			}
		}
	}
	public static long getFolderSize(File file) {
		long size = 0;
		try {
			for (File item : file.listFiles()) {
				if(!item.isDirectory()){
					size = size + getFolderSize(item);
				}else{
					size = size + item.length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}
	
	public static long getAllCacheSize(Context context, String... filepath) {
		long size = 0;
		size=size+getFolderSize(context.getCacheDir());
		size=size+getFolderSize(context.getExternalCacheDir());
		//size=size+getFolderSize(context.getFilesDir());
		//size=size+getFolderSize(new File("/data/data/"+ context.getPackageName() + "/shared_prefs"));
		//size=size+getFolderSize(new File("/data/data/"+ context.getPackageName() + "/databases"));
		for (String filePath : filepath) {
			size=size+getFolderSize(new File(filePath));
		}
		return size;
	}
}