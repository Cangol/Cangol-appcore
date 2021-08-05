package mobi.cangol.mobile.service.route;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;

/**
 * Created by xuewu.wei on 2018/10/15.
 */
@Service("RouteService")
class RouteServiceImpl implements RouteService {
    private static final String TAG = "RouteService";
    private ServiceProperty mServiceProperty = null;
    private Map<String, Class<?>> mRouteMap = null;
    private OnNavigation mOnNavigation;

    private boolean mDebug = false;

    @Override
    public void onCreate(Application context) {
        mRouteMap = new HashMap<>();
    }

    @Override
    public void setDebug(boolean mDebug) {
        this.mDebug = mDebug;
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
    public void registerByAnnotation(Class clazz) {
        if (clazz.isAnnotationPresent(Route.class)) {
            final Route route = (Route) clazz.getAnnotation(Route.class);
            register(route.path(), clazz);
        } else {
            throw new IllegalStateException(clazz + " is not Annotation Route");
        }
    }

    @Override
    public void unregisterByAnnotation(Class clazz) {
        if (clazz.isAnnotationPresent(Route.class)) {
            final Route route = (Route) clazz.getAnnotation(Route.class);
            unregister(route.path());
        } else {
            throw new IllegalStateException(clazz + " is not Annotation Route");
        }
    }

    @Override
    public void register(String path, Class clazz) {
        if (!mRouteMap.containsKey(path)) {
            if(mDebug)Log.i(TAG, "registerRoute " + path + "--->" + clazz.getName());
            mRouteMap.put(path, clazz);
        } else {
            Log.i(TAG, path + " is registered");
        }
    }

    @Override
    public void unregister(String path) {
        if (mRouteMap.containsKey(path)) {
            if(mDebug) Log.i(TAG, "unregisterRoute " + path);
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
    public void handleIntent(Context context,Intent intent) {
        Uri uri = intent.getData();
        Log.i(TAG, "handleIntent uri="+uri+",host="+uri.getHost()+",path="+uri.getPath());
        Bundle bundle=new Bundle();
        for (String key : uri.getQueryParameterNames()) {
            bundle.putString(key,uri.getQueryParameter(key));
        }
        this.handleNavigation(uri.getPath().replaceFirst("/",""),
                bundle,
                context,
                uri.getBooleanQueryParameter("newStack",false));
    }
    public void handleNavigation(String path,Bundle bundle,Context context, boolean newStack) {
        final Class clazz = mRouteMap.get(path);
        if (clazz != null) {
            if (clazz.getSuperclass() == Activity.class) {
                this.mOnNavigation.toActivity(navigationActivity(clazz, bundle,context),newStack);
            } else if (clazz.getSuperclass() == Fragment.class) {
                this.mOnNavigation.toFragment(clazz, bundle,newStack);
            } else {
                Log.e(TAG, path+" not navigation");
                this.mOnNavigation.notFound(path);
            }
        }else{
            Log.e(TAG, path+" not register");
            this.mOnNavigation.notFound(path);
        }
    }

    @Override
    public void registerNavigation(OnNavigation onNavigation) {
        this.mOnNavigation = onNavigation;
    }

    Intent navigationActivity(Class clazz, Bundle bundle, Context context) {
        final Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        return intent;
    }

}
