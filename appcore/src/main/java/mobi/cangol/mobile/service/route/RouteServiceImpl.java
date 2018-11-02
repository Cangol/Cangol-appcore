package mobi.cangol.mobile.service.route;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;

/**
 * Created by xuewu.wei on 2018/10/15.
 */
@Service("RouteService")
class RouteServiceImpl implements RouteService {
    private static final String TAG = "RouteService";
    private CoreApplication mContext = null;
    private ServiceProperty mServiceProperty = null;
    private Map<String, Class<?>> mRouteMap = null;
    private OnNavigation mOnNavigation;

    private static boolean debug = true;

    @Override
    public void onCreate(Application context) {
        mContext = (CoreApplication) context;
        mRouteMap = new HashMap<>();
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
        mRouteMap.clear();
    }


    @Override
    public void register(Class clazz) {
        if (clazz.isAnnotationPresent(Route.class)) {
            Route route = (Route) clazz.getAnnotation(Route.class);
            register(route.path(), clazz);
        } else {
            throw new IllegalStateException(clazz + " is not Annotation Route");
        }
    }

    @Override
    public void register(String path, Class clazz) {
        if (!mRouteMap.containsKey(path)) {
            Log.i(TAG, "registerRoute " + path + "--->" + clazz.getName());
            mRouteMap.put(path, clazz);
        } else {
            Log.i(TAG, path + " is registered");
        }
    }

    @Override
    public void unregister(String path) {
        if (mRouteMap.containsKey(path)) {
            Log.i(TAG, "unregisterRoute " + path);
            mRouteMap.remove(path);
        } else {
            Log.i(TAG, path + " is not registered");
        }
    }

    @Override
    public RouteBuilder build(String path) {
        return new RouteBuilder(this, path);
    }

    @Override
    public void registerNavigation(OnNavigation onNavigation) {
        this.mOnNavigation = onNavigation;
    }

    Class getClassByPath(String path) {
        return mRouteMap.get(path);
    }

    void handleNavigation(Class clazz, Bundle bundle, Context context, boolean standalone) {
        if (clazz.getSuperclass() == Activity.class) {
            this.mOnNavigation.toActivity(navigationActivity(clazz, bundle, context), standalone);
        } else if (clazz.getSuperclass() == Fragment.class) {
            this.mOnNavigation.toFragment(navigationFragment(clazz, bundle, context), standalone);
        } else {
            Log.i(TAG, " not navigation");
        }
    }

    Intent navigationActivity(Class clazz, Bundle bundle, Context context) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        return intent;
    }

    Fragment navigationFragment(Class clazz, Bundle bundle, Context context) {
        return Fragment.instantiate(context, clazz.getName(), bundle);
    }

}
