package mobi.cangol.mobile.appcore.libdemo;

import mobi.cangol.mobile.ModuleApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.route.RouteService;

/**
 * Created by xuewu.wei on 2018/10/15.
 */
public class LibApplication extends ModuleApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("onCreate");
        getApplication().getSession().put("lib","LibApplication");
        this.registerRoute();
    }

    private void registerRoute() {
        RouteService routeService=  this.getAppService(AppService.ROUTE_SERVICE);
        routeService.registerByAnnotation(LibTestFragment.class);
    }
    public void onExit() {
        RouteService routeService= this.getAppService(AppService.ROUTE_SERVICE);
        routeService.unregisterByAnnotation(LibTestFragment.class);
    }
}
