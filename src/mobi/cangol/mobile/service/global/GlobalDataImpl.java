/** 
 * Copyright (c) 2013 Cangol
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */package mobi.cangol.mobile.service.global;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.Session;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.Object2FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
@Service("global")
 class GlobalDataImpl implements GlobalData {
	
	private final static  String JSON = ".json";
	private final static  String JSONA = ".jsona";
	private final static  String SER = ".ser";
	private Context mContext = null;
	private Session mSession=null;
	private SharedPreferences mShared;
	private ConfigService mConfig=null;
	private boolean debug=false;
	@Override
	public void onCreate(Context context) {
		mContext=context;
		//这里使用挂载在application总的session也可实例化一个新的
		CoreApplication app=(CoreApplication) mContext.getApplicationContext();
		mSession=app.mSession;
		
		mConfig=(ConfigService) app.getAppService("config");
		mShared=mContext.getSharedPreferences(mConfig.getSharedName(), Context.MODE_PRIVATE);
		refresh();
	}
	@Override
	public void init(ServiceProperty serviceProperty) {
		
	}
	@Override
	public String getName() {
		return "global";
	}

	@Override
	public void onDestory() {
		mSession.clear();
	}
	@Override
	public void setDebug(boolean debug) {
		this.debug=debug;
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
	public ServiceProperty getServiceProperty() {
		return null;
	}
}
