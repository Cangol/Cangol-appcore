package mobi.cangol.mobile.appcore.demo;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.DeviceInfo;

/**
 * Created by weixuewu on 15/9/14.
 */
public class MobileApplication extends CoreApplication {
    public void onCreate() {
        this.setDevMode(true);
        super.onCreate();
        Log.d(DeviceInfo.getSHA1Fingerprint(this));
    }
}
