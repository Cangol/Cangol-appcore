package mobi.cangol.mobile.service.conf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.parser.XMLParserException;
import mobi.cangol.mobile.parser.XmlUtils;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.utils.StorageUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
@Service("config")
public class ConfigeImpl implements Config {
	private Context mContext = null;
	private ServiceConfig mServiceConfig=null;
	private Map<String,ServiceConfig> configs=new HashMap<String,ServiceConfig>();
	@Override
	public void init() {
		InputStream is=this.getClass().getResourceAsStream("config.xml");
		setConfigSource(is);
		mServiceConfig=configs.get("config");
	}
	@Override
	public void setConfigSource(InputStream is) {
		try {
			parser(is);
			is.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void parser(InputStream is) throws Exception{
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document=builder.parse(is);
		Element root=document.getDocumentElement();
		NodeList nodeList=root.getChildNodes();
		for(int i=0;i<nodeList.getLength();i++){
			Node node=nodeList.item(i);
			if(node instanceof Element){
				Element element=(Element)node;
				String name=element.getAttribute("name");
				NodeList nodeList2=element.getChildNodes();
				ServiceConfig config=new ServiceConfig(name);
				for(int j=0;j<nodeList2.getLength();j++){
					Node node2=nodeList2.item(j);
					if(node2 instanceof Element){
						Element element2=(Element)node2;	
						config.put(element2.getAttribute("name"), element2.getTextContent());
					}
				}
				configs.put(name, config);
			}
		}
	}
	@Override
	public ServiceConfig getServiceConfig(String name){
		return configs.get(name);
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
		return StorageUtils.getExternalStorageDir(mContext, mServiceConfig.getString(Config.APP_DIR));
	}

	@Override
	public String getCacheDir() {
		return StorageUtils.getExternalCacheDir(mContext).getAbsolutePath();
	}

	@Override
	public String getImageDir() {
		return getAppDir()+File.separator+mServiceConfig.getString(Config.IMAGE_DIR);
	}

	@Override
	public String getTempDir() {
		return getAppDir()+File.separator+mServiceConfig.getString(Config.TEMP_DIR);
	}

	@Override
	public String getDownloadDir() {
		return getAppDir()+File.separator+mServiceConfig.getString(Config.DOWNLOAD_DIR);
	}
	
	@Override
	public String getUpgradeDir() {
		return getAppDir()+File.separator+mServiceConfig.getString(Config.UPGRADE_DIR);
	}
	
	@Override
	public String getDatabaseName() {
		return mServiceConfig.getString(Config.DATABASE_NAME);
	}

	@Override
	public String getSharedName() {
		return mServiceConfig.getString(Config.SHARED_NAME);
	}
}
