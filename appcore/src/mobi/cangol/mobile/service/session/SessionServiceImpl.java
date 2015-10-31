package mobi.cangol.mobile.service.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.Object2FileUtils;

/**
 * Created by weixuewu on 15/10/24.
 */
@Service("SessionService")
public class SessionServiceImpl implements SessionService {
    private final static String TAG = "SessionService";
    private final static String JSON = ".json";
    private final static String JSONA = ".jsona";
    private final static String SER = ".ser";
    private Context mContext = null;
    private ConfigService mConfigService = null;
    private ServiceProperty mServiceProperty = null;
    private boolean debug = false;
    private Map<String, Object> mMap = null;

    @Override
    public void onCreate(Context context) {
        mContext = context;
        //这里使用application中的session也可实例化一个新的
        CoreApplication app = (CoreApplication) mContext.getApplicationContext();
        mMap = new ConcurrentHashMap<String, Object>();
        mConfigService = (ConfigService) app.getAppService(AppService.CONFIG_SERVICE);
        refresh();
    }

    private SharedPreferences getShared() {
        return mContext.getSharedPreferences(mConfigService.getSharedName(), Context.MODE_MULTI_PROCESS);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestory() {
        mMap.clear();
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
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
        ServiceProperty sp = new ServiceProperty(TAG);
        return sp;
    }

    @Override
    public boolean containsKey(String key) {
        return mMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return mMap.containsValue(value);
    }

    @Override
    public int getInt(String key, int defValue) {
        if(mMap.containsKey(key)){
           return (int) mMap.get(key);
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if(mMap.containsKey(key)){
            return (boolean) mMap.get(key);
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        if(mMap.containsKey(key)){
            return (long) mMap.get(key);
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        if(mMap.containsKey(key)){
            return (float) mMap.get(key);
        }
        return defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        if(mMap.containsKey(key)){
            return (String) mMap.get(key);
        }
        return defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValue) {
        if(mMap.containsKey(key)){
            return (Set<String>) mMap.get(key);
        }
        return defValue;
    }

    @Override
    public JSONObject getJSONObject(String key) {
        if(mMap.containsKey(key)){
            return (JSONObject) mMap.get(key);
        }
        return null;
    }

    @Override
    public JSONArray getJSONArray(String key) {
        if(mMap.containsKey(key)){
            return (JSONArray) mMap.get(key);
        }
        return null;
    }

    @Override
    public Serializable getSerializable(String key) {
        if(mMap.containsKey(key)){
            return (Serializable) mMap.get(key);
        }
        return null;
    }

    @Override
    public void saveInt(String key, int value) {
        getShared().edit().putInt(key, value).commit();
        mMap.put(key, value);
    }

    @Override
    public void saveBoolean(String key, boolean value) {
        getShared().edit().putBoolean(key, value).commit();
        mMap.put(key, value);
    }

    @Override
    public void saveFloat(String key, float value) {
        getShared().edit().putFloat(key, value).commit();
        mMap.put(key, value);
    }

    @Override
    public void saveLong(String key, long value) {
        getShared().edit().putLong(key, value).commit();
        mMap.put(key, value);
    }

    @Override
    public void saveString(String key, String value) {
        getShared().edit().putString(key, value).commit();
        mMap.put(key, value);
    }

    @Override
    public void saveStringSet(String key, Set<String> value) {
        getShared().edit().putStringSet(key, value).commit();
        mMap.put(key, value);
    }

    @Override
    public void saveJSONObject(String key, JSONObject value) {
        Object2FileUtils.writeJSONObject2File( value, mConfigService.getCacheDir() + File.separator + key + JSON);
        mMap.put(key, value);
    }

    @Override
    public void saveJSONArray(String key, JSONArray value) {
        Object2FileUtils.writeJSONArray2File(value, mConfigService.getCacheDir() + File.separator + key + JSONA);
        mMap.put(key, value);
    }

    @Override
    public void saveSerializable(String key, Serializable value) {
        Object2FileUtils.writeObject(value, mConfigService.getCacheDir() + File.separator + key + SER);
        mMap.put(key, value);
    }
    @Override
    public void saveAll(Map<String, ?> map) {
        for (String key : map.keySet()) {
            if (map.get(key) instanceof Float) {
                saveFloat(key, (Float) map.get(key));
            } else if (map.get(key) instanceof Boolean) {
                saveBoolean(key, (Boolean) map.get(key));
            } else if (map.get(key) instanceof String) {
                saveString(key, (String) map.get(key));
            } else if (map.get(key) instanceof Integer) {
                saveInt(key, (Integer) map.get(key));
            } else if (map.get(key) instanceof Long) {
                saveLong(key, (Long) map.get(key));
            } else if (map.get(key) instanceof Set && Build.VERSION.SDK_INT >= 11) {
                saveStringSet(key, (Set<String>) map.get(key));
            } else if (map.get(key) instanceof JSONObject) {
                saveJSONObject(key, (JSONObject) map.get(key));
            } else if (map.get(key) instanceof JSONArray) {
                saveJSONArray(key, (JSONArray) map.get(key));
            } else if (Serializable.class.isAssignableFrom(map.get(key).getClass())) {
                saveSerializable(key, (Serializable) map.get(key));
            } else {
                //其他缓存方案
                throw new IllegalArgumentException(map.get(key).getClass()+" is not cache type");
            }
        }
    }

    @Override
    public void put(String key, Object value) {
        mMap.put(key,value);
    }

    @Override
    public void putAll(Map<String, ?> map) {
        mMap.putAll(map);
    }

    @Override
    public void remove(String key) {
        mMap.remove(key);
        getShared().edit().remove(key).commit();
        FileUtils.delete(mConfigService.getCacheDir() + File.separator + key + JSON);
        FileUtils.delete(mConfigService.getCacheDir() + File.separator + key + JSONA);
        FileUtils.delete(mConfigService.getCacheDir() + File.separator + key + SER);
    }

    @Override
    public void refresh() {
        Map<String, ?> map = getShared().getAll();
        mMap.putAll(map);
        if (debug) Log.d("scan cache file");
        new AsyncTask<String, Void, List<File>>(){
            @Override
            protected List<File> doInBackground(String... params) {
                List<File> files=FileUtils.searchBySuffix(new File(params[0]), null, params[1],params[2],params[3]);
                System.gc();
                return files;
            }
            @Override
            protected void onPostExecute(List<File> files) {
                super.onPostExecute(files);
                for (File file : files) {
                    if (file.getName().endsWith(JSON)) {
                        JSONObject json = Object2FileUtils.readFile2JSONObject(file);
                        String key = file.getName().substring(0, file.getName().lastIndexOf(JSON));
                        mMap.put(key, json);
                    } else if (file.getName().endsWith(JSONA)) {
                        JSONObject jsona = Object2FileUtils.readFile2JSONObject(file);
                        String key = file.getName().substring(0, file.getName().lastIndexOf(JSONA));
                        mMap.put(key, jsona);
                    } else if (file.getName().endsWith(SER)) {
                        Object obj = Object2FileUtils.readObject(file);
                        String key = file.getName().substring(0, file.getName().lastIndexOf(SER));
                        mMap.put(key, obj);
                    } else {
                        //其他缓存方案
                        Log.e("found cache file");
                    }
                }
            }
        }.execute(mConfigService.getCacheDir(), JSON, JSONA, SER);
    }

    @Override
    public void clearAll() {
        mMap.clear();
        getShared().edit().clear().commit();
        FileUtils.delAllFile(mConfigService.getCacheDir());
    }
}
