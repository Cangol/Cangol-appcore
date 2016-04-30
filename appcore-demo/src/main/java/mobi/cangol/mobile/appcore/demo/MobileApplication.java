package mobi.cangol.mobile.appcore.demo;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.service.crash.CrashReportListener;
import mobi.cangol.mobile.service.crash.CrashService;
import mobi.cangol.mobile.stat.StatAgent;
import mobi.cangol.mobile.utils.FileUtils;

/**
 * Created by weixuewu on 15/9/14.
 */
public class MobileApplication extends CoreApplication {
    public void onCreate() {
        this.setDevMode(true);
        super.onCreate();
    }
}
