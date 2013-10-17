package mobi.cangol.mobile.service.download;

import mobi.cangol.mobile.service.AppService;

public interface DownloadManager extends AppService {
	
	void init();
	
	DownloadExecutor getDownloadExecutor(String name);
	
	void registerExecutor(String name,Class<? extends DownloadExecutor> clazz,int max);
	
	void recoverAllAllDownloadExecutor();
	
	void interruptAllDownloadExecutor();
}
