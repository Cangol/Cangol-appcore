package mobi.cangol.mobile.service.session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import mobi.cangol.mobile.service.AppService;

/**
 * Created by weixuewu on 15/10/24.
 */
public interface SessionService extends AppService {

    /**
     * 是否包含
     * @param key
     * @return
     */
    boolean containsKey(String key);
    /**
     * 是否包含
     * @param value
     * @return
     */
    boolean containsValue(Object value);
    /**
     * 获取Int
     * @param key
     * @return
     */
    int getInt(String key, int defValue);
    /**
     * 获取Boolean
     * @param key
     * @return
     */
    boolean getBoolean(String key, boolean defValue);
    /**
     * 获取Long
     * @param key
     * @return
     */
    long getLong(String key, long defValue);
    /**
     * 获取Float
     * @param key
     * @return
     */
    float getFloat(String key, float defValue);
    /**
     * 获取String
     * @param key
     * @return
     */
    String getString(String key, String defValue);
    /**
     * 获取String
     * @param key
     * @return
     */
    Set<String> getStringSet(String key, Set<String> defValue);
    /**
     * 获取JSONObject缓存
     * @param key
     * @return
     */
    JSONObject getJSONObject(String key);

    /**
     * 获取JSONArray缓存
     * @param key
     * @return
     */
    JSONArray getJSONArray(String key);

    /**
     * 获取Serializable缓存
     * @param key
     * @return
     */
    Serializable getSerializable(String key);
    /**
     * 存储int
     * @param key
     * @return
     */
     void saveInt(String key, int value);
    /**
     * 存储boolean
     * @param key
     * @return
     */
     void saveBoolean(String key, boolean value);
    /**
     * 存储Float
     * @param key
     * @return
     */
     void saveFloat(String key, float value);
    /**
     * 存储long
     * @param key
     * @return
     */
     void saveLong(String key, long value);
    /**
     * 存储String
     * @param key
     * @return
     */
     void saveString(String key, String value);
    /**
     * Set<String>
     * @param key
     * @return
     */
     void saveStringSet(String key, Set<String> value);
    /**
     * 存储JSONObject
     * @param key
     * @return
     */
     void saveJSONObject(String key, JSONObject value);
    /**
     * 存储JSONArray
     * @param key
     * @return
     */
     void saveJSONArray(String key, JSONArray value);
    /**
     * 存储Serializable
     * @param key
     * @return
     */
    void saveSerializable(String key, Serializable value);

    /**
     * 
     * @param map
     */
    void saveAll(Map<String, ?> map);

    /**
     * 
     * @param key
     * @param value
     */
    void put(String key, Object value);
    /**
     * 
     * @param map
     */
    void putAll(Map<String, ?> map);

    /**
     * 删除磁盘缓存和内存缓存
     * @param key
     */
    void remove(String key);

    /**
     * 刷新磁盘缓存和内存缓存
     * @return
     */
    void refresh();

    /**
     * 清除内存缓存
     * @return
     */
    void clear();

}
