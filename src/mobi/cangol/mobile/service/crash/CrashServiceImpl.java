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
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.TimeUtils;
import android.content.Context;
import android.util.Log;
@Service("CrashService")
public class CrashServiceImpl implements CrashService,UncaughtExceptionHandler {
	private final static String TAG="CrashHandler";
	private final static  String _CRASH = ".crash";
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
		init();
	}
	private void init(){
		PoolManager.buildPool(mServiceProperty.getString(CRASHHANDLER_THREADPOOL_NAME),mServiceProperty.getInt(CRASHHANDLER_THREAD_MAX));
		asyncHttpClient=AsyncHttpClient.build(mServiceProperty.getString(CRASHHANDLER_THREADPOOL_NAME));
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
	public void setServiceProperty(ServiceProperty serviceProperty) {
		this.mServiceProperty=serviceProperty;
		init();
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
				params.put(mServiceProperty.getString(CRASHHANDLER_REPORT_ERROR), file);
				params.put(mServiceProperty.getString(CRASHHANDLER_REPORT_TIMESTAMP), file.getName());
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
