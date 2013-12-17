package mobi.cangol.mobile.service.conf;

import mobi.cangol.mobile.service.AppService;

public interface ConfigService extends AppService{
	
	public final static String APP_DIR="app_dir";
	public final static String IMAGE_DIR="image_dir";
	public final static String DOWNLOAD_DIR="download_dir";
	public final static String TEMP_DIR="temp_dir";
	public final static String UPGRADE_DIR="upgrade_dir";
	public final static String DATABASE_NAME="database_name";
	public final static String SHARED_NAME="shared_name";
	
	String getAppDir();
	
	String getCacheDir();
	
	String getImageDir();
	
	String getTempDir();
	
	String getDownloadDir();
	
	String getUpgradeDir();
	
	String getDatabaseName();
	
	String getSharedName();
}
