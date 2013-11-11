package mobi.cangol.mobile.service.global;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.cache.Object2FileUtils;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.conf.Config;
import mobi.cangol.mobile.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
@Service("global")
public class GlobalDataImpl implements GlobalData {
	
	private final static  String JSON = ".json";
	private final static  String JSONA = ".jsona";
	private final static  String SER = ".ser";
	private Context mContext = null;
	private Map<String,Object> mSession=null;
	private SharedPreferences mShared;
	private Config mConfig=null;
	
	@Override
	public void create(Context context) {
		mContext=context;
		//这里使用挂载在application总的session也可实例化一个新的
		CoreApplication app=(CoreApplication) mContext.getApplicationContext();
		mSession=app.session;
		
		mConfig=(Config) app.getAppService("config");
		mShared=mContext.getSharedPreferences(mConfig.getSharedName(), Context.MODE_PRIVATE);
		refresh();
	}
	
	@Override
	public String getName() {
		return "global";
	}

	@Override
	public void destory() {
		mSession.clear();
	}
	
	@SuppressWarnings("unchecked")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void save(String key, Object value) {
		Editor editor=mShared.edit();
		if(value instanceof Float){
			editor.putFloat(key, (Float) value).commit();
		}else if(value instanceof Boolean){
			editor.putBoolean(key, (Boolean) value).commit();
		}else if(value instanceof String){
			editor.putString(key, (String) value).commit();
		}else if(value instanceof Integer){
			editor.putInt(key, (Integer) value).commit();
		}else if(value instanceof Long){
			editor.putLong(key, (Long) value).commit();
		}else if(value instanceof Set&&Build.VERSION.SDK_INT>=11){
			editor.putStringSet(key, (Set<String>) value).commit();
		}else if(value instanceof JSONObject){
			Object2FileUtils.writeJSONObject2File((JSONObject) value, mConfig.getCacheDir()+File.separator+key+JSON);
		}else if(value instanceof JSONArray){
			Object2FileUtils.writeJSONObject2File((JSONObject) value, mConfig.getCacheDir()+File.separator+key+JSONA);
		}else if(Serializable.class.isAssignableFrom(value.getClass())){
			Object2FileUtils.writeObject(value, mConfig.getCacheDir()+File.separator+key+SER);
		}else{
			//其他缓存方案
			
		}
		mSession.put(key, value);
		
	}
	
	@Override
	public void put(String key, Object value) {
		mSession.put(key, value);
	}

	@Override
	public Object get(String key) {
		return mSession.get(key);
	}

	@Override
	public void refresh() {
		Map<String,?> map=mShared.getAll();
		mSession.clear();
		mSession.putAll(map);
		
		List<File> list=FileUtils.searchBySuffix(new File(mConfig.getCacheDir()), null, JSON,JSONA,SER);
		for(File file:list){
			if(file.getName().endsWith(JSON)){
				JSONObject json=Object2FileUtils.readFile2JSONObject(file);
				String key=file.getName().substring(0, file.getName().lastIndexOf(JSON));
				mSession.put(key, json);
			}else if(file.getName().endsWith(JSONA)){
				JSONObject jsona=Object2FileUtils.readFile2JSONObject(file);
				String key=file.getName().substring(0, file.getName().lastIndexOf(JSONA));
				mSession.put(key, jsona);
			}else if(file.getName().endsWith(SER)){
				Object obj=Object2FileUtils.readObject(file);
				String key=file.getName().substring(0, file.getName().lastIndexOf(SER));
				mSession.put(key, obj);
			}else{
				//其他缓存方案
				
			}
		}
	}

	@Override
	public void clear() {
		mSession.clear();
	}

	@Override
	public void setDebug(boolean debug) {
		// TODO Auto-generated method stub
		
	}

}
