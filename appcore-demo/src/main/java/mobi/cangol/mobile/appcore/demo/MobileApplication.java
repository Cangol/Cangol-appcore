package mobi.cangol.mobile.appcore.demo;



import androidx.appcompat.app.AppCompatDelegate;

import hugo.weaving.DebugLog;
import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.fragment.TestFragment;
import mobi.cangol.mobile.appcore.libdemo.LibApplication;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.route.RouteService;
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
        this.registerRoute();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    @Override
    public void init() {
        if (DeviceInfo.isAppProcessByFile(this)) {
            StatAgent.initInstance(this);
        }
    }
    private void registerRoute() {
        RouteService routeService=  this.getAppService(AppService.ROUTE_SERVICE);
        routeService.registerByAnnotation(TestFragment.class);
    }

    @Override
    public void onExit() {
        RouteService routeService= this.getAppService(AppService.ROUTE_SERVICE);
        routeService.unregisterByAnnotation(TestFragment.class);
        super.onExit();
    }
}
