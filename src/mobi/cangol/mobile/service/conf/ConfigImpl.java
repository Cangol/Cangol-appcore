package mobi.cangol.mobile.service.conf;

import java.io.File;
import java.io.InputStream;

import mobi.cangol.mobile.parser.DocumentParser;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.utils.StorageUtils;
import android.content.Context;
@Service("config")
public class ConfigImpl implements Config {
	private Context mContext = null;
	private DocumentParser mDocumentParser;
	
	private String appDir;
	private String imageDir;
	private String downloadDir;
	private String tempDir;
	private String upgradedDir;
	private String dbName;
	private String sharedName;
	
	@Override
	public void init() {
		InputStream is=this.getClass().getResourceAsStream("config.xml");
		setConfigSource(is);
	}
	@Override
	public void setConfigSource(InputStream is) {
		mDocumentParser=new DocumentParser(is);
		appDir=mDocumentParser.getNodeValue("AppDir");
		imageDir=mDocumentParser.getNodeValue("AppImageDir");
		downloadDir=mDocumentParser.getNodeValue("AppDownloadDir");
		tempDir=mDocumentParser.getNodeValue("AppTempDir");
		upgradedDir=mDocumentParser.getNodeValue("AppUpgradeDir");
		dbName=mDocumentParser.getNodeValue("DatabaseName");
		sharedName=mDocumentParser.getNodeValue("SharedName");
	}
	
	@Override
	public void setContext(Context ctx) {
		mContext=ctx;
	}

	@Override
	public String getName() {
		return "config";
	}

	@Override
	public void destory() {
		
	}
	
	@Override
	public String getAppDir() {
		return StorageUtils.getExternalStorageDir(mContext, appDir);
	}

	@Override
	public String getCacheDir() {
		return StorageUtils.getExternalCacheDir(mContext).getAbsolutePath();
	}

	@Override
	public String getImageDir() {
		return getAppDir()+File.separator+imageDir;
	}

	@Override
	public String getTempDir() {
		return getAppDir()+File.separator+tempDir;
	}

	@Override
	public String getDownloadDir() {
		return getAppDir()+File.separator+downloadDir;
	}
	
	@Override
	public String getUpgradeDir() {
		return getAppDir()+File.separator+upgradedDir;
	}
	
	@Override
	public String getDatabaseName() {
		return dbName;
	}

	@Override
	public String getSharedName() {
		return sharedName;
	}
	
	@Override
	public String getStringValue(String... nodeName) {
		return mDocumentParser.getNodeValue(nodeName);
	}
	
	@Override
	public int getIntValue(String... nodeName) {
		String str=mDocumentParser.getNodeValue(nodeName);
		return Integer.parseInt(str);
	}
	
	@Override
	public float getFloatValue(String... nodeName) {
		String str=mDocumentParser.getNodeValue(nodeName);
		return Float.parseFloat(str);
	}
	
	@Override
	public long getLongValue(String... nodeName) {
		String str=mDocumentParser.getNodeValue(nodeName);
		return Long.parseLong(str);
	}

}
