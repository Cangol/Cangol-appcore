package mobi.cangol.mobile.service.crash;

import java.util.Map;

import mobi.cangol.mobile.service.AppService;

public interface CrashService  extends AppService{
	public final static String CRASHHANDLER_THREAD_MAX="thread_max";
	public final static String CRASHHANDLER_THREADPOOL_NAME="threadpool_name";
	public final static String CRASHHANDLER_REPORT_URL="report_url";
	public final static String CRASHHANDLER_REPORT_ERROR="report_param_error";
	public final static String CRASHHANDLER_REPORT_DEVICE="report_param_device";
	public final static String CRASHHANDLER_REPORT_TIMESTAMP="report_param_timestamp";
	
	void setReport(String url,Map<String,String> params);
	
	void report();
}
