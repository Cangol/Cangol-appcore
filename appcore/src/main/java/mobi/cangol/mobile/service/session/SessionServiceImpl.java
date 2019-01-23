package mobi.cangol.mobile.service.session;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;

/**
 * Created by weixuewu on 15/10/24.
 */
@Service("SessionService")
class SessionServiceImpl implements SessionService {
    private static final String TAG = "SessionService";
    private CoreApplication mContext = null;
    private ServiceProperty mServiceProperty = null;
    private boolean mDebug = false;
    private Map<String, Session> mSessionMap = null;
    private Session mSession = null;

    @Override
    public void onCreate(Application context) {
        mContext = (CoreApplication) context;
        //这里使用application中的session也可实例化一个新的
        mSessionMap = new ConcurrentHashMap<>();
        ConfigService configService = (ConfigService) mContext.getAppService(AppService.CONFIG_SERVICE);
        mSession = newSession(mContext, configService.getSharedName());
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        mSession.clear();
        for (Map.Entry<String, Session> entry : mSessionMap.entrySet()) {
            Session session = entry.getValue();
            if (session != null) {
                session.clear();
            }
        }
    }

    @Override
    public void setDebug(boolean mDebug) {
        this.mDebug = mDebug;
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        return new ServiceProperty(TAG);
    }

    @Override
    public Session getUserSession(String name) {
        if (mDebug) Log.d(TAG, "getUserSession " + name);
        if (mSessionMap.containsKey(name)) {
            return mSessionMap.get(name);
        } else {
            return newSession(mContext, name);
        }
    }

    private Session newSession(Context context, String name) {
        if (mDebug) Log.d(TAG, "newSession " + name);
        Session session = new Session(context, name);
        mSessionMap.put(name, session);
        session.refresh();
        return session;
    }

    @Override
    public boolean containsKey(String key) {
        return mSession.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return mSession.containsValue(value);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mSession.getInt(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mSession.getBoolean(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mSession.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mSession.getFloat(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        return mSession.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValue) {
        return mSession.getStringSet(key, defValue);
    }

    @Override
    public JSONObject getJSONObject(String key) {
        return mSession.getJSONObject(key);
    }

    @Override
    public JSONArray getJSONArray(String key) {
        return mSession.getJSONArray(key);
    }

    @Override
    public Serializable getSerializable(String key) {
        return mSession.getSerializable(key);
    }

    @Override
    public void saveInt(String key, int value) {
        mSession.saveInt(key, value);
    }

    @Override
    public void saveBoolean(String key, boolean value) {
        mSession.saveBoolean(key, value);
    }

    @Override
    public void saveFloat(String key, float value) {
        mSession.saveFloat(key, value);
    }

    @Override
    public void saveLong(String key, long value) {
        mSession.saveLong(key, value);
    }

    @Override
    public void saveString(String key, String value) {
        mSession.saveString(key, value);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void saveStringSet(String key, Set<String> value) {
        mSession.saveStringSet(key, value);
    }

    @Override
    public void saveJSONObject(String key, JSONObject value) {
        mSession.saveJSONObject(key, value);
    }

    @Override
    public void saveJSONArray(String key, JSONArray value) {
        mSession.saveJSONArray(key, value);
    }

    @Override
    public void saveSerializable(String key, Serializable value) {
        mSession.saveSerializable(key, value);
    }

    @Override
    public void saveAll(Map<String, ?> map) {
        mSession.saveAll(map);
    }

    @Override
    public Object get(String key) {
        return mSession.get(key);
    }

    @Override
    public void put(String key, Object value) {
        mSession.put(key, value);
    }

    @Override
    public void putAll(Map<String, ?> map) {
        mSession.putAll(map);
    }

    @Override
    public void remove(String key) {
        mSession.remove(key);
    }

    public void refresh() {
        mSession.refresh();
    }

    @Override
    public void clearAll() {
        mSession.clearAll();
    }
}
