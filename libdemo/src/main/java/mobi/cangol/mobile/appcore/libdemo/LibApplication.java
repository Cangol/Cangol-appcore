package mobi.cangol.mobile.appcore.libdemo;

import mobi.cangol.mobile.ModuleApplication;
import mobi.cangol.mobile.logging.Log;

/**
 * Created by xuewu.wei on 2018/10/15.
 */
public class LibApplication extends ModuleApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("onCreate");
        getApplication().getSession().put("lib","LibApplication");
    }
}
