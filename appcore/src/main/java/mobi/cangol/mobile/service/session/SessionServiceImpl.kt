package mobi.cangol.mobile.service.session

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build
import mobi.cangol.mobile.CoreApplication
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.AppService
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import mobi.cangol.mobile.service.conf.ConfigService
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by weixuewu on 15/10/24.
 */
@Service("SessionService")
internal class SessionServiceImpl : SessionService {
    private var mContext: CoreApplication? = null
    private var mServiceProperty: ServiceProperty? = null
    private var mDebug = false
    private var mSessionMap: MutableMap<String, Session>? = null
    private var mSession: Session? = null

    override fun onCreate(context: Application) {
        mContext = context as CoreApplication
        //这里使用application中的session也可实例化一个新的
        mSessionMap = ConcurrentHashMap()
        val configService = mContext!!.getAppService(AppService.CONFIG_SERVICE) as ConfigService?
        mSession = newSession(mContext, configService!!.getSharedName()!!)
    }

    override fun getName(): String {
        return TAG
    }

    override fun onDestroy() {
        mSession!!.clear()
        for ((_, session) in mSessionMap!!) {
            session?.clear()
        }
    }

    override fun setDebug(mDebug: Boolean) {
        this.mDebug = mDebug
    }

    override fun init(serviceProperty: ServiceProperty) {
        this.mServiceProperty = serviceProperty
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty!!
    }

    override fun defaultServiceProperty(): ServiceProperty {
        return ServiceProperty(TAG)
    }

    override fun getUserSession(name: String): Session {
        if (mDebug) Log.d(TAG, "getUserSession $name")
        return if (mSessionMap!!.containsKey(name)) {
            mSessionMap!![name]!!
        } else {
            newSession(mContext, name)
        }
    }

    private fun newSession(context: Context?, name: String): Session {
        if (mDebug) Log.d(TAG, "newSession $name")
        val session = Session(context!!, name)
        mSessionMap!![name] = session
        session.refresh()
        return session
    }

    override fun containsKey(key: String): Boolean {
        return mSession!!.containsKey(key)
    }

    override fun containsValue(value: Any): Boolean {
        return mSession!!.containsValue(value)
    }

    override fun getInt(key: String, defValue: Int): Int {
        return mSession!!.getInt(key, defValue)
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mSession!!.getBoolean(key, defValue)
    }

    override fun getLong(key: String, defValue: Long): Long {
        return mSession!!.getLong(key, defValue)
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return mSession!!.getFloat(key, defValue)
    }

    override fun getString(key: String, defValue: String?): String? {
        return mSession!!.getString(key, defValue)
    }

    override fun getStringSet(key: String, defValue: Set<String>?): Set<String>? {
        return mSession!!.getStringSet(key, defValue)
    }

    override fun getJSONObject(key: String): JSONObject {
        return mSession!!.getJSONObject(key)!!
    }

    override fun getJSONArray(key: String): JSONArray {
        return mSession!!.getJSONArray(key)!!
    }

    override fun getSerializable(key: String): Serializable? {
        return mSession!!.getSerializable(key)
    }

    override fun saveInt(key: String, value: Int) {
        mSession!!.saveInt(key, value)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        mSession!!.saveBoolean(key, value)
    }

    override fun saveFloat(key: String, value: Float) {
        mSession!!.saveFloat(key, value)
    }

    override fun saveLong(key: String, value: Long) {
        mSession!!.saveLong(key, value)
    }

    override fun saveString(key: String, value: String) {
        mSession!!.saveString(key, value)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun saveStringSet(key: String, value: Set<String>) {
        mSession!!.saveStringSet(key, value)
    }

    override fun saveJSONObject(key: String, value: JSONObject) {
        mSession!!.saveJSONObject(key, value)
    }

    override fun saveJSONArray(key: String, value: JSONArray) {
        mSession!!.saveJSONArray(key, value)
    }

    override fun saveSerializable(key: String, value: Serializable) {
        mSession!!.saveSerializable(key, value)
    }

    override fun saveAll(map: Map<String, Any>) {
        mSession!!.saveAll(map)
    }

    override fun get(key: String): Any? {
        return mSession!![key]
    }

    override fun put(key: String, value: Any) {
        mSession!!.put(key, value)
    }

    override fun putAll(map: Map<String, Any>) {
        mSession!!.putAll(map)
    }

    override fun remove(key: String) {
        mSession!!.remove(key)
    }

    override fun refresh() {
        mSession!!.refresh()
    }

    override fun clearAll() {
        mSession!!.clearAll()
    }

    companion object {
        private val TAG = "SessionService"
    }
}
