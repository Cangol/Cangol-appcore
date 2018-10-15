package mobi.cangol.mobile.appcore.libdemo;

import mobi.cangol.mobile.ModuleApplication;
import mobi.cangol.mobile.logging.Log;

/**
 * Created by xuewu.wei on 2018/10/15.
 */
public class LibApplication extends ModuleApplication {

    @Override
    public void onCreate() {

        getApplication().getSession().put("lib","LibApplication");

        Log.d("lib="+getApplication().getSession().get("lib"));
    }

    @Override
    public void init() {

    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public void onExit() {

    }
}
