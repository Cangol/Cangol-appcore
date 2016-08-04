package mobi.cangol.mobile.service.cache;

import java.io.Serializable;

import mobi.cangol.mobile.service.AppService;

public interface CacheManager extends AppService {
    /**
     * 缓存路径
     */
    public final static String CACHE_DIR = "cache_dir";
    /**
     * 缓存最大大小
     */
    public final static String CACHE_SIZE = "cache_size";

    /**
     * 获取缓存对象
     *
     * @param context 上下文标示，可当做分类
     * @param id      缓存标示
     * @return
     */
    Serializable getContent(String context, String id);

    /**
     * 获取缓存对象
     *
     * @param context     上下文标示，可当做分类
     * @param id          缓存标示
     * @param cacheLoader
     */
    void getContent(final String context, final String id, final CacheLoader cacheLoader);

    /**
     * 判断是否存在缓存对象
     *
     * @param context 上下文标示，可当做分类
     * @param id      缓存标示
     * @return
     */
    boolean hasContent(String context, String id);

    /**
     * 添加缓存对象
     *
     * @param context 上下文标示，可当做分类
     * @param id      缓存标示
     * @param data    必须实现Serializable
     */
    void addContent(String context, String id, Serializable data);

    /**
     * 移除缓存对象
     *
     * @param context
     */
    void removeContext(String context);

    /**
     * 移除缓存对象
     *
     * @param context 上下文标示，可当做分类
     * @param id      缓存标示
     */
    void removeContent(String context, String id);

    /**
     * 获取缓存大小
     *
     * @return
     */
    long size();

    /**
     * 刷新缓存对象
     */
    void flush();

    /**
     * 关闭缓存
     */
    void close();

    /**
     * 清除缓存
     */
    void clearCache();

}
