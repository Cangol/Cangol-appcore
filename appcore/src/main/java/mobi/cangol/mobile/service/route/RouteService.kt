package mobi.cangol.mobile.service.route

import mobi.cangol.mobile.service.AppService

/**
 * Created by xuewu.wei on 2018/10/15.
 */
interface RouteService : AppService {
    /**
     * 注册 （注解）
     *
     * @param clazz
     */
    fun register(clazz: Class<*>)

    /**
     * 注册 （非注解）
     *
     * @param path
     * @param clazz
     */
    fun register(path: String, clazz: Class<*>)

    /**
     * @param path
     */
    fun unregister(path: String)

    /**
     * @param onNavigation
     */
    fun registerNavigation(onNavigation: OnNavigation)

    /**
     * 构造器
     *
     * @param path
     * @return
     */
    fun build(path: String): RouteBuilder
}
