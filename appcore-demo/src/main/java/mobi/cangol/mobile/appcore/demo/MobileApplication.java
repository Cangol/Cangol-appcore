package mobi.cangol.mobile.appcore.demo;


import com.squareup.leakcanary.LeakCanary;

import mobi.cangol.mobile.CoreApplication;

/**
 * Created by weixuewu on 15/9/14.
 */
public class MobileApplication extends CoreApplication {
    public void onCreate() {
        this.setDevMode(true);
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
