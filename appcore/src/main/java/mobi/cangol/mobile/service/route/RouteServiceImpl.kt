package mobi.cangol.mobile.service.route

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import mobi.cangol.mobile.logging.Log
import mobi.cangol.mobile.service.Service
import mobi.cangol.mobile.service.ServiceProperty
import java.util.*

/**
 * Created by xuewu.wei on 2018/10/15.
 */
@Service("RouteService")
class RouteServiceImpl : RouteService {
    private var mServiceProperty = ServiceProperty(TAG)
    private var mRouteMap: MutableMap<String, Class<*>>? = null
    private var mOnNavigation: OnNavigation? = null
    private var mDebug = false

    override fun onCreate(context: Application) {
        mRouteMap = HashMap()
    }

    override fun setDebug(mDebug: Boolean) {
        this.mDebug = mDebug
    }

    override fun init(serviceProperty: ServiceProperty) {
        this.mServiceProperty = serviceProperty
    }

    override fun getServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun defaultServiceProperty(): ServiceProperty {
        return mServiceProperty
    }

    override fun getName(): String {
        return TAG
    }

    override fun onDestroy() {
        mRouteMap!!.clear()
    }


    override fun register(clazz: Class<*>) {
        if (clazz.isAnnotationPresent(Route::class.java)) {
            register(clazz.getAnnotation(Route::class.java)!!.path, clazz)
        } else {
            throw IllegalStateException("$clazz is not Annotation Route")
        }
    }

    override fun register(path: String, clazz: Class<*>) {
        if (!mRouteMap!!.containsKey(path)) {
            if (mDebug) Log.i(TAG, "registerRoute " + path + "--->" + clazz.name)
            mRouteMap!![path] = clazz
        } else {
            Log.i(TAG, "$path is registered")
        }
    }

    override fun unregister(path: String) {
        if (mRouteMap!!.containsKey(path)) {
            if (mDebug) Log.i(TAG, "unregisterRoute $path")
            mRouteMap!!.remove(path)
        } else {
            Log.i(TAG, "$path is not registered")
        }
    }

    override fun build(path: String): RouteBuilder {
        return RouteBuilder(this, path)
    }

    override fun registerNavigation(onNavigation: OnNavigation) {
        this.mOnNavigation = onNavigation
    }

    fun getClassByPath(path: String): Class<*>? {
        return mRouteMap!![path]
    }

    fun handleNavigation(clazz: Class<*>, bundle: Bundle, context: Context) {
        if (clazz.superclass == Activity::class.java) {
            this.mOnNavigation!!.toActivity(navigationActivity(clazz, bundle, context))
        } else if (clazz.superclass == Fragment::class.java) {
            this.mOnNavigation!!.toFragment(navigationFragment(clazz, bundle, context))
        } else {
            Log.i(TAG, " not navigation")
        }
    }

    fun navigationActivity(clazz: Class<*>, bundle: Bundle, context: Context): Intent {
        val intent = Intent(context, clazz)
        intent.putExtras(bundle)
        return intent
    }

    fun navigationFragment(clazz: Class<*>, bundle: Bundle, context: Context): Fragment {
        return Fragment.instantiate(context, clazz.name, bundle)
    }

    companion object {
        private const val TAG = "RouteService"
    }

}
