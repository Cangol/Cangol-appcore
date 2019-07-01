package mobi.cangol.mobile.appcore.demo;


import com.squareup.leakcanary.LeakCanary;

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
        //this.getModuleManager().add(new LibApplication());
        super.onCreate();
    }

    @Override
    public void init() {
        initLeakCanary();
        if(DeviceInfo.isAppProcess(this)){
            StatAgent.initInstance(this);
        }
    }
    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public boolean isModule() {
        return false;
    }
}
