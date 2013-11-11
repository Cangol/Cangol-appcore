package mobi.cangol.mobile.service.crash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.AsyncHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.conf.Config;
import mobi.cangol.mobile.service.conf.ServiceConfig;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.TimeUtils;
import android.content.Context;
import android.util.Log;
@Service("crash")
public class CrashHandlerImpl implements CrashHandler,UncaughtExceptionHandler {
	private final static String TAG="CrashHandler";
	private final static String URL_REPORT="";
	private final static  String CRASH = ".crash";
	private boolean debug=false;
	private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
	private Context mContext;
	private Config mConfigService;
	private ServiceConfig mServiceConfig=null;
	private AsyncHttpClient asyncHttpClient;
	@Override
	public void create(Context context) {
		mContext=context;
		mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		CoreApplication app=(CoreApplication) mContext.getApplicationContext();
		mConfigService=(Config) app.getAppService("config");
		mServiceConfig=mConfigService.getServiceConfig("crash");
		asyncHttpClient=new AsyncHttpClient();
		asyncHttpClient.setThreadool((ThreadPoolExecutor)PoolManager
				.buildPool(mServiceConfig.getString(Config.CRASHHANDLER_THREADPOOL_NAME),
						mServiceConfig.getInt(Config.CRASHHANDLER_THREAD_MAX))
				.getExecutorService());
	}

	@Override
	public String getName() {
		return "crash";
	}

	@Override
	public void destory() {

	}

	@Override
	public void save(String path,String error) {
		FileUtils.writeStr(new File(path), error);
		if(debug)Log.d(TAG, "Save Exception:"+path);
	}

	@Override
	public void report() {
		List<File> list=FileUtils.searchBySuffix(new File(mConfigService.getTempDir()), null, CRASH);
		for(final File file:list){
			RequestParams params=new RequestParams(getMobileInfo());
			try {	
				params.put(mServiceConfig.getString(Config.CRASHHANDLER_REPORT_ERROR), file);
				params.put(mServiceConfig.getString(Config.CRASHHANDLER_REPORT_TIMESTAMP), file.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			asyncHttpClient.post(mContext,URL_REPORT, params,  new AsyncHttpResponseHandler(){

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
	public String error(Throwable ex) {
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
		String savePath=mConfigService.getTempDir()+File.separator+TimeUtils.getCurrentTime2()+CRASH;
		save(savePath,error);
	}
	private Map<String,String> getMobileInfo() {
		Map<String,String> map=new HashMap<String,String>();
		
		return map;
	}

	@Override
	public void setDebug(boolean debug) {
		
	}
}
