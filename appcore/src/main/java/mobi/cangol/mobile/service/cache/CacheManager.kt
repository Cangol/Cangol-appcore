package mobi.cangol.mobile.service.cache

import mobi.cangol.mobile.service.AppService
import java.io.Serializable

interface CacheManager : AppService {

    /**
     * 获取缓存对象
     *
     * @param context 上下文标示，可当做分类
     * @param id      缓存标示
     * @return
     */
    fun getContent(context: String, id: String): Serializable?

    /**
     * 获取缓存对象
     *
     * @param context     上下文标示，可当做分类
     * @param id          缓存标示
     * @param cacheLoader
     */
    fun getContent(context: String, id: String, cacheLoader: CacheLoader?)

    /**
     * 判断是否存在缓存对象
     *
     * @param context 上下文标示，可当做分类
     * @param id      缓存标示
     * @return
     */
    fun hasContent(context: String, id: String): Boolean

    /**
     * 添加缓存对象
     *
     * @param context 上下文标示，可当做分类
     * @param id      缓存标示
     * @param data    必须实现Serializable
     */
    fun addContent(context: String, id: String, data: Serializable)

    /**
     * 添加缓存对象
     *
     * @param context 上下文标示，可当做分类
     * @param id      缓存标示
     * @param data    必须实现Serializable
     * @param period  有效期(单位毫秒)
     */
    fun addContent(context: String, id: String, data: Serializable, period: Long)

    /**
     * 移除缓存对象
     *
     * @param context
     */
    fun removeContext(context: String)

    /**
     * 移除缓存对象
     *
     * @param context 上下文标示，可当做分类
     * @param id      缓存标示
     */
    fun removeContent(context: String, id: String)

    /**
     * 获取缓存大小
     *
     * @return
     */
    fun size(): Long

    /**
     * 刷新缓存对象
     */
    fun flush()

    /**
     * 关闭缓存
     */
    fun close()

    /**
     * 清除缓存
     */
    fun clearCache()

    companion object {
        /**
         * 缓存路径
         */
        const val CACHE_DIR = "cache_dir"
        /**
         * 缓存最大大小
         */
        const val CACHE_SIZE = "cache_size"
    }

}
