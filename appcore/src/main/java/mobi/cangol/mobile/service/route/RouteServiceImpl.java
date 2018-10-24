package mobi.cangol.mobile.service.route;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;

/**
 * Created by xuewu.wei on 2018/10/15.
 */
@Service("RouteService")
public class RouteServiceImpl implements RouteService {
    private final static String TAG = "RouteService";
    private CoreApplication mContext = null;
    private ServiceProperty mServiceProperty = null;
    private Map<String, Class<Activity>> mActivityMap = null;
    private Map<String, Class<Fragment>> mFragmentMap = null;
    private boolean debug = false;

    @Override
    public void onCreate(Application context) {
        mContext = (CoreApplication) context;
        mActivityMap = new HashMap<>();
        mFragmentMap = new HashMap<>();
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        return new ServiceProperty(TAG);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void registerActivityRoute(String path, Class<Activity> activityClass) {
        mActivityMap.put(path, activityClass);
    }

    @Override
    public void registerFragmentRoute(String path, Class<Activity> activityClass, Class<Fragment> fragmentClass) {
        mFragmentMap.put(path, fragmentClass);
    }

    public Intent navigationActivity(RouteAction routeAction) {
        if(mActivityMap.containsKey(routeAction.getPath())){
            Class<Activity> activityClass = mActivityMap.get(routeAction.getPath());
            Intent intent = new Intent(routeAction.getContext(), activityClass);
            intent.putExtras(routeAction.getBundle());
            return intent;
        }else{
            throw new IllegalArgumentException(routeAction.getPath()+" is not registered");
        }
    }

    public Fragment navigationFragment(RouteAction routeAction) {
        if(mFragmentMap.containsKey(routeAction.getPath())){
            Class<Fragment> fragmentClass = mFragmentMap.get(routeAction.getPath());
            Bundle bundle = routeAction.getBundle();
            return Fragment.instantiate(routeAction.getContext(), fragmentClass.getName(), bundle);
        }else{
            throw new IllegalArgumentException(routeAction.getPath()+" is not registered");
        }
    }

}
