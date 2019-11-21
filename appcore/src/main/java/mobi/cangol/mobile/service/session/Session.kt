package mobi.cangol.mobile.service.session

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.StrictMode
import mobi.cangol.mobile.CoreApplication
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.AppService
import mobi.cangol.mobile.service.conf.ConfigService
import mobi.cangol.mobile.utils.FileUtils
import mobi.cangol.mobile.utils.Object2FileUtils
import mobi.cangol.mobile.utils.StringUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by xuewu.wei on 2018/5/2.
 */
class Session(context: Context, val name: String) {
    private var shared: SharedPreferences = context.getSharedPreferences("session_$name", Context.MODE_MULTI_PROCESS)
    private val mMap = mutableMapOf<String, Any>()
    private val mSessionDir: String
    private val mCoreApplication: CoreApplication = context as CoreApplication

    init {
        val configService = mCoreApplication.getAppService(AppService.CONFIG_SERVICE) as ConfigService?
        mSessionDir = configService!!.getCacheDir().absolutePath + File.separator + "session_" + name

        val oldPolicy = StrictMode.allowThreadDiskReads()
        FileUtils.newFolder(mSessionDir)
        StrictMode.setThreadPolicy(oldPolicy)
    }

    fun containsKey(key: String): Boolean {
        return mMap.containsKey(key)
    }


    fun containsValue(value: Any): Boolean {
        return mMap.containsValue(value)
    }

    fun getInt(key: String, defValue: Int): Int {
        return if (mMap.containsKey(key)) {
            mMap[key] as Int
        } else defValue
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return if (mMap.containsKey(key)) {
            mMap[key] as Boolean
        } else defValue
    }

    fun getLong(key: String, defValue: Long): Long {
        return if (mMap.containsKey(key)) {
            mMap[key] as Long
        } else defValue
    }

    fun getFloat(key: String, defValue: Float): Float {
        return if (mMap.containsKey(key)) {
            mMap[key] as Float
        } else defValue
    }

    fun getString(key: String, defValue: String?): String? {
        return if (mMap.containsKey(key)) {
            mMap[key] as String
        } else defValue
    }

    fun getStringSet(key: String, defValue: Set<String>?): Set<String>? {
        return if (mMap.containsKey(key)) {
            mMap[key] as Set<String>
        } else defValue
    }

    fun getJSONObject(key: String): JSONObject? {
        return if (mMap.containsKey(key)) {
            mMap[key] as JSONObject?
        } else null
    }

    fun getJSONArray(key: String): JSONArray? {
        return if (mMap.containsKey(key)) {
            mMap[key] as JSONArray?
        } else null
    }

    fun getSerializable(key: String): Serializable? {
        return if (mMap.containsKey(key)) {
            mMap[key] as Serializable?
        } else null
    }

    fun saveInt(key: String, value: Int) {
        shared.edit().putInt(key, value).apply()
        mMap[key] = value
    }

    fun saveBoolean(key: String, value: Boolean) {
        shared.edit().putBoolean(key, value).apply()
        mMap[key] = value
    }

    fun saveFloat(key: String, value: Float) {
        shared.edit().putFloat(key, value).apply()
        mMap[key] = value
    }

    fun saveLong(key: String, value: Long) {
        shared.edit().putLong(key, value).apply()
        mMap[key] = value
    }

    fun saveString(key: String, value: String) {
        shared.edit().putString(key, value).apply()
        mMap[key] = value
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun saveStringSet(key: String, value: Set<String>) {
        shared.edit().putStringSet(key, value).apply()
        mMap[key] = value
    }

    fun saveJSONObject(key: String, value: JSONObject) {
        mMap[key] = value
        mCoreApplication.post(Runnable {
            Log.e(TAG + name, mSessionDir + File.separator + key + JSON)
            FileUtils.delete(mSessionDir + File.separator + key + JSON)
            Object2FileUtils.writeJSONObject2File(value, mSessionDir + File.separator + key + JSON)
            FileUtils.delete(mSessionDir + File.separator + key + JSONA)
            FileUtils.delete(mSessionDir + File.separator + key + SER)
        })
    }

    fun saveJSONArray(key: String, value: JSONArray) {
        mMap[key] = value
        mCoreApplication.post(Runnable {
            FileUtils.delete(mSessionDir + File.separator + key + JSONA)
            Object2FileUtils.writeJSONArray2File(value, mSessionDir + File.separator + key + JSONA)
            FileUtils.delete(mSessionDir + File.separator + key + JSON)
            FileUtils.delete(mSessionDir + File.separator + key + SER)
        })
    }

    fun saveSerializable(key: String, value: Serializable) {
        mMap[key] = value
        mCoreApplication.post(Runnable {
            FileUtils.delete(mSessionDir + File.separator + key + SER)
            Object2FileUtils.writeObject(value, mSessionDir + File.separator + key + SER)
            FileUtils.delete(mSessionDir + File.separator + key + JSON)
            FileUtils.delete(mSessionDir + File.separator + key + JSONA)
        })
    }

    fun saveAll(map: Map<String, Any>) {
        for ((key, value) in map) {
            if (value is Float) {
                saveFloat(key, value)
            } else if (value is Boolean) {
                saveBoolean(key, value)
            } else if (value is String) {
                saveString(key, value)
            } else if (value is Int) {
                saveInt(key, value)
            } else if (value is Long) {
                saveLong(key, value)
            } else if (value is Set<*>) {
                saveStringSet(key, value as Set<String>)
            } else if (value is JSONObject) {
                saveJSONObject(key, value)
            } else if (value is JSONArray) {
                saveJSONArray(key, value)
            } else if (Serializable::class.java.isAssignableFrom(value.javaClass)) {
                saveSerializable(key, value as Serializable)
            } else {
                //其他缓存方案
                throw IllegalArgumentException(value.javaClass.toString() + " is not cache type")
            }
        }
    }

    operator fun get(key: String): Any? {
        return if (mMap.containsKey(key)) {
            mMap[key]
        } else null
    }

    fun put(key: String, value: Any) {
        mMap[key] = value
    }

    fun putAll(map: Map<String, Any>) {
        mMap.putAll(map)
    }

    fun remove(key: String) {
        mMap.remove(key)
        shared.edit().remove(key).apply()
        mCoreApplication.post(Runnable {
            FileUtils.delete(mSessionDir + File.separator + key + JSON)
            FileUtils.delete(mSessionDir + File.separator + key + JSONA)
            FileUtils.delete(mSessionDir + File.separator + key + SER)
        })
    }

    fun clear() {
        mMap.clear()
    }

    fun clearAll() {
        mMap.clear()
        shared.edit().clear().apply()
        mCoreApplication.post(Runnable { FileUtils.delAllFile(mSessionDir) })
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    fun refresh() {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        val map = shared.all as MutableMap<String, Any>
        StrictMode.setThreadPolicy(oldPolicy)
        mMap.putAll(map)
        mCoreApplication.post(Runnable { mMap.putAll(loadDiskMap()) })
    }

    private fun loadDiskMap(): Map<String, Any> {
        Log.d(TAG + name, "scan cache file")
        val files = FileUtils.searchBySuffix(File(mSessionDir), null, JSON, JSONA, SER)
        Log.d(TAG + name, "cache file=$files")
        val map = ConcurrentHashMap<String, Any>()
        for (file in files) {
            if (file.name.endsWith(JSON)) {
                val json = Object2FileUtils.readFile2JSONObject(file)
                val key = file.name.substring(0, file.name.lastIndexOf(JSON))
                if (json != null && StringUtils.isNotBlank(key))
                    map[key] = json
            } else if (file.name.endsWith(JSONA)) {
                val jsona = Object2FileUtils.readFile2JSONArray(file)
                val key = file.name.substring(0, file.name.lastIndexOf(JSONA))
                if (jsona != null && StringUtils.isNotBlank(key))
                    map[key] = jsona
            } else if (file.name.endsWith(SER)) {
                val obj = Object2FileUtils.readObject(file)
                val key = file.name.substring(0, file.name.lastIndexOf(SER))
                if (obj != null && StringUtils.isNotBlank(key))
                    map[key] = obj
            } else {
                //其他缓存方案
                Log.e("found cache file")
            }
        }
        return map
    }

    companion object {
        private const val TAG = "session_"
        private const val JSON = ".json"
        private const val JSONA = ".jsona"
        private const val SER = ".ser"
    }
}
