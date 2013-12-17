package mobi.cangol.mobile.service.stat;

import mobi.cangol.mobile.service.AppService;

public interface StatService extends AppService{
	public final static String STATSERVICE_THREAD_MAX="thread_max";
	public final static String STATSERVICE_THREADPOOL_NAME="threadpool_name";
	
	void sendStat(StatModel statModel);
	
}
