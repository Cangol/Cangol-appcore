package mobi.cangol.mobile.service.crash;

import mobi.cangol.mobile.service.AppService;

public interface CrashHandler  extends AppService{
	
	void save(String path,String error);
	
	void report();
	
}
