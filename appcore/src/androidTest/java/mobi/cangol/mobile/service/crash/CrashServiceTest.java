package mobi.cangol.mobile.service.crash;

import android.test.ApplicationTestCase;

import java.util.HashMap;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.AppService;


/**
 * Created by weixuewu on 16/6/11.
 */
public class CrashServiceTest extends ApplicationTestCase<CoreApplication> {
    private static final String TAG = "CrashServiceTest";
    private CoreApplication coreApplication;
    private CrashService crashService;

    public CrashServiceTest() {
        super(CoreApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication = getApplication();
        crashService = (CrashService) coreApplication.getAppService(AppService.Companion.getCRASH_SERVICE());
    }

    public void testSetReport() {
        String url="http://www.cangol.mobi/cmweb/api/countly/crash.do";
        crashService.setReport(url,new HashMap<String, String>());
    }

    public void testReport() {
        crashService.report(new CrashReportListener() {
            @Override
            public void report(String path, String error, String position, String context, String timestamp, String fatal) {

            }
        });
    }
}