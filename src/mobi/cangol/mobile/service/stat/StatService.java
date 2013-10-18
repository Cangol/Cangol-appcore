package mobi.cangol.mobile.service.stat;

import mobi.cangol.mobile.service.AppService;

public interface StatService extends AppService{
	
	void sendStat(StatModel statModel);
	
}
