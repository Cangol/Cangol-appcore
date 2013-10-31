package mobi.cangol.mobile.service.conf;

import java.io.InputStream;

import mobi.cangol.mobile.service.AppService;

public interface Config extends AppService{
	
	public final static String APP_DIR="app_dir";
	public final static String IMAGE_DIR="image_dir";
	public final static String DOWNLOAD_DIR="download_dir";
	public final static String TEMP_DIR="temp_dir";
	public final static String UPGRADE_DIR="upgrade_dir";
	public final static String DATABASE_NAME="database_name";
	public final static String SHARED_NAME="shared_name";
	
	public final static String CRASHHANDLER_THREAD_MAX="thread_max";
	public final static String CRASHHANDLER_THREADPOOL_NAME="threadpool_name";
	public final static String CRASHHANDLER_REPORT_URL="report_url";
	public final static String CRASHHANDLER_REPORT_ERROR="report_param_error";
	public final static String CRASHHANDLER_REPORT_DEVICE="report_param_device";
	public final static String CRASHHANDLER_REPORT_TIMESTAMP="report_param_timestamp";
	
	public final static String DOWNLOADSERVICE_THREAD_MAX="thread_max";
	public final static String DOWNLOADSERVICE_THREADPOOL_NAME="threadpool_name";
	
	
	public final static String STATSERVICE_THREAD_MAX="thread_max";
	public final static String STATSERVICE_THREADPOOL_NAME="threadpool_name";
	
	public final static String LOCATIONSERVICE_BETTERTIME="better_time";
	public final static String LOCATIONSERVICE_TIMEOUT="timeout";
	public final static String LOCATIONSERVICE_BAIDU_AK="baidu_ak";
	public final static String LOCATIONSERVICE_GPS_MINTIME="gps_min_time";
	public final static String LOCATIONSERVICE_GPS_MINDISTANCE="gps_min_distance";
	public final static String LOCATIONSERVICE_NETWORK_MINTIME="network_min_time";
	public final static String LOCATIONSERVICE_NETWORK_MINDISTANCE="network_min_distance";
	
	
	void setConfigSource(InputStream is);
	
	ServiceConfig getServiceConfig(String name);
	
	String getAppDir();
	
	String getCacheDir();
	
	String getImageDir();
	
	String getTempDir();
	
	String getDownloadDir();
	
	String getUpgradeDir();
	
	String getDatabaseName();
	
	String getSharedName();
}
