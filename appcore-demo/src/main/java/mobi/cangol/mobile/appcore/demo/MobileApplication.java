package mobi.cangol.mobile.appcore.demo;



import hugo.weaving.DebugLog;
import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.libdemo.LibApplication;
import mobi.cangol.mobile.stat.StatAgent;
import mobi.cangol.mobile.utils.DeviceInfo;

/**
 * Created by weixuewu on 15/9/14.
 */
@DebugLog
public class MobileApplication extends CoreApplication {

    public void onCreate() {
        this.setDevMode(true);
        this.setAsyncInit(false);
        this.getModuleManager().addModule(new LibApplication());
        super.onCreate();
    }

    @Override
    public void init() {
        if (DeviceInfo.isAppProcess(this)) {
            StatAgent.initInstance(this);
        }
    }
}
