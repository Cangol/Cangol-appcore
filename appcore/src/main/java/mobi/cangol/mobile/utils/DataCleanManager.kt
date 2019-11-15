package mobi.cangol.mobile.utils

import android.content.Context
import android.os.Environment

import java.io.File

import mobi.cangol.mobile.logging.Log

/**
 * 本应用数据清除管理器
 */
object DataCleanManager {

    private const val DATA_DATA = "/data/data/"
    private const val SHARED_PREFS = "/shared_prefs"
    private const val DATABASES = "/databases"

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    fun cleanInternalCache(context: Context) {
        deleteFilesByDirectory(context.cacheDir)
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    fun cleanDatabases(context: Context) {
        deleteFilesByDirectory(File(DATA_DATA + context.packageName + DATABASES))
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    fun cleanSharedPreference(context: Context) {
        deleteFilesByDirectory(File(DATA_DATA + context.packageName + SHARED_PREFS))
    }

    /**
     * 按名字清除本应用数据库 * * @param context * @param dbName
     */
    fun cleanDatabaseByName(context: Context, dbName: String) {
        context.deleteDatabase(dbName)
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    fun cleanFiles(context: Context) {
        deleteFilesByDirectory(context.filesDir)
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    fun cleanExternalCache(context: Context) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteFilesByDirectory(context.externalCacheDir)
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath
     */
    fun cleanCustomCache(filePath: String) {
        deleteFilesByDirectory(File(filePath))
    }

    /**
     * 清除本应用所有的数据 * * @param context * @param filepath
     */
    fun cleanApplicationData(context: Context, vararg filepath: String) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
        for (filePath in filepath) {
            cleanCustomCache(filePath)
        }
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件夹，将递归删除文件 * * @param directory
     */
    private fun deleteFilesByDirectory(directory: File?) {
        Log.d("deleteFilesByDirectory=" + directory!!)
        if (directory != null && directory.exists() && directory.isDirectory) {
            for (item in directory.listFiles()) {
                if (!item.isDirectory) {
                    val result = item.delete()
                    Log.d("delete " + item.absoluteFile + ",result=" + result)
                } else {
                    deleteFilesByDirectory(item)
                }
            }
        }
    }

    fun getFolderSize(file: File): Long {
        var size: Long = 0
        for (item in file.listFiles()) {
            if (!item.isDirectory) {
                size = size + getFolderSize(item)
            } else {
                size = size + item.length()
            }
        }
        return size
    }

    fun getAllCacheSize(context: Context, vararg filepath: String): Long {
        var size: Long = 0
        size = size + getFolderSize(context.cacheDir)
        size = size + getFolderSize(context.externalCacheDir!!)
        size = size + getFolderSize(context.filesDir)
        size = size + getFolderSize(File(DATA_DATA + context.packageName + SHARED_PREFS))
        size = size + getFolderSize(File(DATA_DATA + context.packageName + DATABASES))
        for (filePath in filepath) {
            size = size + getFolderSize(File(filePath))
        }
        return size
    }
}