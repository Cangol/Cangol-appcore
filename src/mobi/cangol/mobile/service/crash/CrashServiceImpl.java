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
 */
package mobi.cangol.mobile.service.crash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.Map;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.AsyncHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.TimeUtils;
import android.content.Context;
import android.util.Log;
@Service("CrashService")
 class CrashServiceImpl implements CrashService,UncaughtExceptionHandler {
	private final static String TAG="CrashService";
	private final static  String _CRASH = ".crash";
	private final static  int DEFAULT_MAX = 2;
	private boolean debug=false;
	private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
	private Context mContext;
	private ConfigService mConfigService;
	private ServiceProperty mServiceProperty=null;
	private AsyncHttpClient asyncHttpClient;
	private String url;
	private Map<String,String> params;
	@Override
	public void onCreate(Context context) {
		mContext=context;
		mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		CoreApplication app=(CoreApplication) mContext.getApplicationContext();
		mConfigService=(ConfigService) app.getAppService(AppService.CONFIG_SERVICE);
	}
	public void init(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
		PoolManager.buildPool(mServiceProperty.getString(CRASHSERVICE_THREADPOOL_NAME,TAG),mServiceProperty.getInt(CRASHSERVICE_THREAD_MAX,DEFAULT_MAX));
		asyncHttpClient=AsyncHttpClient.build(mServiceProperty.getString(CRASHSERVICE_THREADPOOL_NAME,TAG));
	}
	
	@Override
	public String getName() {
		return "CrashService";
	}

	@Override
	public void onDestory() {
		asyncHttpClient.cancelRequests(mContext, true);
	}
	@Override
	public void setDebug(boolean debug) {
		this.debug=debug;
	}

	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}

	@Override
	public void setReport(String url, Map<String, String> params) {
		this.url=url;
		this.params=params;
	}
	
	protected void save(String path,String error) {
		FileUtils.writeStr(new File(path), error);
		if(debug)Log.d(TAG, "Save Exception:"+path);
	}
	@Override
	public void report() {
		List<File> list=FileUtils.searchBySuffix(new File(mConfigService.getTempDir()), null, _CRASH);
		for(final File file:list){
			RequestParams params=new RequestParams(this.params);
			try {	
				params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_ERROR), file);
				params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_TIMESTAMP), file.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			asyncHttpClient.post(mContext,url, params,  new AsyncHttpResponseHandler(){

				@Override
				public void onStart() {
					super.onStart();
					if(debug)Log.d(TAG, "Start");
				}
				
				@Override
				public void onSuccess(String content) {
					super.onSuccess(content);
					if(debug)Log.d(TAG, "Success :"+content);
					//提交后删除文件
					FileUtils.deleteFile(file.getAbsolutePath());
					if(debug)Log.d(TAG, "delete :"+file.getAbsolutePath());
				}

				@Override
				public void onFailure(Throwable error, String content) {
					super.onFailure(error, content);
					if(debug)Log.d(TAG, "Failure :"+content);
				}
				
			});
		}
	}
	protected String error(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Thread.setDefaultUncaughtExceptionHandler(mDefaultExceptionHandler);
		String error= error(ex);
		String savePath=mConfigService.getTempDir()+File.separator+TimeUtils.getCurrentTime2()+_CRASH;
		save(savePath,error);
	}



}
