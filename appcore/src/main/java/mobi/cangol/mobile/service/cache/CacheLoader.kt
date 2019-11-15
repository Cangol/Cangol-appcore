package mobi.cangol.mobile.service.cache

interface CacheLoader {

    /**
     * 加载中...
     */
    fun loading()

    /**
     * 获取缓存数据
     */
    fun returnContent(content: Any)
}
