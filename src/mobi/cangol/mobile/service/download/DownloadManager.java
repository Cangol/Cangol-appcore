package mobi.cangol.mobile.service.download;

import mobi.cangol.mobile.service.AppService;

public interface DownloadManager extends AppService {
	public final static String DOWNLOADSERVICE_THREAD_MAX="thread_max";
	public final static String DOWNLOADSERVICE_THREADPOOL_NAME="threadpool_name";
	
	void init();
	
	DownloadExecutor getDownloadExecutor(String name);
	
	void registerExecutor(String name,Class<? extends DownloadExecutor> clazz,int max);
	
	void recoverAllAllDownloadExecutor();
	
	void interruptAllDownloadExecutor();
}
