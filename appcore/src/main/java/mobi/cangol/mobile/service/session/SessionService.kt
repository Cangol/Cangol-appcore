package mobi.cangol.mobile.service.session

import org.json.JSONArray
import org.json.JSONObject

import java.io.Serializable

import mobi.cangol.mobile.service.AppService

/**
 * @author Cangol
 */
interface SessionService : AppService {

    /**
     * 获取用户Session
     *
     * @param name
     */
    fun getUserSession(name: String): Session

    /**
     * 是否包含
     *
     * @param key
     */
    fun containsKey(key: String): Boolean

    /**
     * 是否包含
     *
     * @param value
     */
    fun containsValue(value: Any): Boolean

    /**
     * 获取Int
     *
     * @param key
     */
    fun getInt(key: String, defValue: Int): Int

    /**
     * 获取Boolean
     *
     * @param key
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean

    /**
     * 获取Long
     *
     * @param key
     */
    fun getLong(key: String, defValue: Long): Long

    /**
     * 获取Float
     *
     * @param key
     */
    fun getFloat(key: String, defValue: Float): Float

    /**
     * 获取String
     *
     * @param key
     */
    fun getString(key: String, defValue: String?): String?

    /**
     * 获取String
     *
     * @param key
     */
    fun getStringSet(key: String, defValue: Set<String>?): Set<String>?

    /**
     * 获取JSONObject缓存
     *
     * @param key
     */
    fun getJSONObject(key: String): JSONObject?

    /**
     * 获取JSONArray缓存
     *
     * @param key
     */
    fun getJSONArray(key: String): JSONArray?

    /**
     * 获取Serializable缓存
     *
     * @param key
     */
    fun getSerializable(key: String): Serializable?

    /**
     * 存储int到内存和磁盘
     *
     * @param key
     */
    fun saveInt(key: String, value: Int)

    /**
     * 存储boolean到内存和磁盘
     *
     * @param key
     */
    fun saveBoolean(key: String, value: Boolean)

    /**
     * 存储Float到内存和磁盘
     *
     * @param key
     */
    fun saveFloat(key: String, value: Float)

    /**
     * 存储long到内存和磁盘
     *
     * @param key
     */
    fun saveLong(key: String, value: Long)

    /**
     * 存储String到内存和磁盘
     *
     * @param key
     */
    fun saveString(key: String, value: String)

    /**
     * 存储StringSet到内存和磁盘
     *
     * @param key
     */
    fun saveStringSet(key: String, value: Set<String>)

    /**
     * 存储JSONObject到内存和磁盘
     *
     * @param key
     */
    fun saveJSONObject(key: String, value: JSONObject)

    /**
     * 存储JSONArray到内存和磁盘
     *
     * @param key
     */
    fun saveJSONArray(key: String, value: JSONArray)

    /**
     * 存储Serializable到内存和磁盘
     *
     * @param key
     */
    fun saveSerializable(key: String, value: Serializable)

    /**
     * 存储map到内存和磁盘
     *
     * @param map
     */
    fun saveAll(map: Map<String, Any>)

    /**
     * 获取缓存Object
     *
     * @param key
     */
    operator fun get(key: String): Any?

    /**
     * 缓存Object到内存
     *
     * @param key
     * @param value
     */
    fun put(key: String, value: Any)

    /**
     * 缓存map到内存
     *
     * @param map
     */
    fun putAll(map: Map<String, Any>)

    /**
     * 删除磁盘缓存和内存缓存
     *
     * @param key
     */
    fun remove(key: String)

    /**
     * 刷新磁盘缓存和内存缓存
     */
    fun refresh()

    /**
     * 清除磁盘缓存和内存缓存
     */
    fun clearAll()

}
