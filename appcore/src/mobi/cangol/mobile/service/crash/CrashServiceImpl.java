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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.AsyncHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.service.global.GlobalData;
import mobi.cangol.mobile.utils.DeviceInfo;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.Object2FileUtils;
import mobi.cangol.mobile.utils.TimeUtils;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
/**
 * @author Cangol
 * @hide
 */
@Service("CrashService")
public class CrashServiceImpl implements CrashService,UncaughtExceptionHandler {
	private final static String TAG="CrashService";
	private final static  String _CRASH = ".crash";
	private static boolean debug=true;
	private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
	private Context mContext;
	private GlobalData mGlobalData;
	private ConfigService mConfigService;
	private ServiceProperty mServiceProperty=null;
	private AsyncHttpClient asyncHttpClient;
	private String mUrl;
	private Map<String,String> mParams;
	@Override
	public void onCreate(Context context) {
		mContext=context;
		mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		CoreApplication app=(CoreApplication) mContext.getApplicationContext();
		mGlobalData=(GlobalData) app.getAppService(AppService.GLOBAL_DATA);
		mConfigService=(ConfigService) app.getAppService(AppService.CONFIG_SERVICE);
		FileUtils.newFolder(mConfigService.getTempDir());
	}
	@Override
	public void init(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
		PoolManager.buildPool(mServiceProperty.getString(CRASHSERVICE_THREADPOOL_NAME),mServiceProperty.getInt(CRASHSERVICE_THREAD_MAX));
		asyncHttpClient=AsyncHttpClient.build(mServiceProperty.getString(CRASHSERVICE_THREADPOOL_NAME));
	}

	@Override
	public String getName() {
		return TAG;
	}

	@Override
	public void onDestory() {
		asyncHttpClient.cancelRequests(mContext, true);
	}
	@Override
	public void setDebug(boolean debug) {
		CrashServiceImpl.debug=debug;
	}

	@Override
	public ServiceProperty getServiceProperty() {
		return mServiceProperty;
	}

	@Override
	public ServiceProperty defaultServiceProperty() {
		ServiceProperty sp=new ServiceProperty(TAG);
		sp.putString(CRASHSERVICE_THREADPOOL_NAME, TAG);
		sp.putInt(CRASHSERVICE_THREAD_MAX, 1);
		sp.putString(CRASHSERVICE_REPORT_URL, "");
		sp.putString(CRASHSERVICE_REPORT_ERROR, "error");
		sp.putString(CRASHSERVICE_REPORT_POSITION,"position");
		sp.putString(CRASHSERVICE_REPORT_TIMESTAMP,"timestamp");
		sp.putString(CRASHSERVICE_REPORT_CONTEXT,"content");
		sp.putString(CRASHSERVICE_REPORT_FATAL,"fatal");
		return sp;
	}

	@Override
	public void setReport(String url, Map<String, String> params) {
		this.mUrl=url;
		this.mParams=params;
	}
	
	private void report(final ReportError report) {
		if(debug)Log.d(TAG,"report .crash "+report.path);
		RequestParams params=this.mParams==null?new RequestParams():new RequestParams(this.mParams);
		params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_ERROR), report.error);
		params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_POSITION), report.position);
		params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_CONTEXT), report.context);
		params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_TIMESTAMP), report.timestamp);
		params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_FATAL), report.fatal);
		asyncHttpClient.post(mContext,mUrl, params,  new AsyncHttpResponseHandler(){

			@Override
			public void onStart() {
				super.onStart();
				if(debug)Log.d(TAG, "Start crashfile:"+report.path);
			}
			
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				if(debug)Log.d(TAG, "Success :"+content);
				//提交后删除文件
				if(debug)Log.d(TAG, "delete :"+report.path);
				FileUtils.delFileAsync(report.path);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				if(debug)Log.d(TAG, "Failure :"+content);
			}
			
		});
	}

	protected String throwableToString(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}
	
	protected ReportError makeReportError(Throwable ex) {
		ReportError error=new ReportError();
		error.error=ex.toString();
		error.position=ex.getStackTrace()[0].toString();
		error.context=throwableToString(ex);
		error.timestamp=TimeUtils.getCurrentTime2();
		error.fatal="0";
		error.path=mConfigService.getTempDir()+File.separator+TimeUtils.getCurrentTime2()+_CRASH;
		return error;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Thread.setDefaultUncaughtExceptionHandler(mDefaultExceptionHandler);
		ReportError error=makeReportError(ex);
		Log.e("AndroidRuntime",error.context);
		if(debug)Log.d(TAG,"save .crash "+error.path);
		Object2FileUtils.writeObject(error, error.path);
		System.gc();
		mGlobalData.save("exitCode","1");
		mGlobalData.save("exitVersion",DeviceInfo.getAppVersion(mContext));
		//0 正常推退出  1异常退出
		System.exit(0); 
	}
	@Override
	public void report(final CrashReportListener crashReportListener){
		new AsyncErrorScan(){

			@Override
			protected void onPostExecute(List<ReportError> result) {
				super.onPostExecute(result);
				for(final ReportError errorReport:result){
					if(!TextUtils.isEmpty(mUrl))report(errorReport);
					if(crashReportListener!=null)
						crashReportListener.report(errorReport.path,
								errorReport.error,
								errorReport.position,
								errorReport.context,
								errorReport.timestamp,
								errorReport.fatal);
				}
			}
			
		}.execute(mConfigService.getTempDir());
	}
	
	
	static class ReportError implements java.io.Serializable{
		private static final long serialVersionUID = 0L;
		String error;
		String position;
		String context;
		String timestamp;
		String fatal;
		String path;
		ReportError(){}
	}
	
	static class AsyncErrorScan extends AsyncTask<String, Void, List<ReportError>> {

		@Override
		protected List<ReportError> doInBackground(String... params) {
			List<File> files=FileUtils.searchBySuffix(new File(params[0]), null, _CRASH);
			System.gc();
			List<ReportError> reports=new ArrayList<ReportError>();
			Object obj=null;
			for(final File file:files){
				if(debug)Log.d(TAG,"read .crash "+file.getAbsolutePath());
				obj= FileUtils.readObject(file);
				if(obj!=null){
					reports.add((ReportError)obj);
				}else{
					if(debug)Log.d(TAG, "delete :"+file.getAbsolutePath());
					FileUtils.delFileAsync(file.getAbsolutePath());
				}
			}
			return reports;
		}
	}
}
