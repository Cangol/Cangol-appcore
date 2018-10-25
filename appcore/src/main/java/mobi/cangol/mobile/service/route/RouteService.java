package mobi.cangol.mobile.service.route;

import android.app.Activity;
import android.support.v4.app.Fragment;

import mobi.cangol.mobile.service.AppService;

/**
 * Created by xuewu.wei on 2018/10/15.
 */
public interface RouteService extends AppService {

    void registerActivityRoute(String path, Class<Activity> activityClass);

    void registerFragmentRoute(String path, Class<Activity> activityClass, Class<Fragment> fragmentClass);

}
