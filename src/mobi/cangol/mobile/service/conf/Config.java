package mobi.cangol.mobile.service.conf;

import java.io.InputStream;

import mobi.cangol.mobile.service.AppService;

public interface Config extends AppService{
	

	public final static String[] CRASHHANDLER_THREAD_MAX={"CrashHandler","thread_max"};
	public final static String[] CRASHHANDLER_THREADPOOL_NAME={"CrashHandler","threadpool_name"};
	public final static String[] CRASHHANDLER_REPORT_ERROR={"CrashHandler","report","error_param_name"};
	public final static String[] CRASHHANDLER_REPORT_DEVICE={"CrashHandler","report","device_param_name"};
	public final static String[] CRASHHANDLER_REPORT_TIMESTAMP={"CrashHandler","report","timestamp_param_name"};
	
	public final static String[] DOWNLOADSERVICE_THREAD_MAX={"DownloadService","thread_max"};
	public final static String[] DOWNLOADSERVICE_THREADPOOL_NAME={"DownloadService","threadpool_name"};
	
	
	public final static String[] STATSERVICE_THREAD_MAX={"StatService","thread_max"};
	public final static String[] STATSERVICE_THREADPOOL_NAME={"StatService","threadpool_name"};
	
	public final static String[] LOCATIONSERVICE_BETTERTIME={"LocationService","betterTime"};
	public final static String[] LOCATIONSERVICE_GPS_MINTIME={"LocationService","GPS","minTime"};
	public final static String[] LOCATIONSERVICE_GPS_MINDISTANCE={"LocationService","GPS","minDistance"};
	public final static String[] LOCATIONSERVICE_NETWORK_MINTIME={"LocationService","NETWORK","minTime"};
	public final static String[] LOCATIONSERVICE_NETWORK_MINDISTANCE={"LocationService","NETWORK","minDistance"};
	
	void setConfigSource(InputStream is);
	
	String getAppDir();
	
	String getCacheDir();
	
	String getImageDir();
	
	String getTempDir();
	
	String getDownloadDir();
	
	String getUpgradeDir();
	
	String getDatabaseName();
	
	String getSharedName();
	
	String getStringValue(String... nodeName);
	
	int getIntValue(String... nodeName);
	
	float getFloatValue(String... nodeName);
	
	long getLongValue(String... nodeName);
}
