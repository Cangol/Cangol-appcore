package mobi.cangol.mobile.service.cache;

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
	 * @param context
	 * @param id
	 * @return
	 */
	Object getContent(String context, String id);

	/**
	 * 获取缓存对象
	 * 
	 * @param context
	 * @param id
	 * @param cacheLoader
	 */
	void getContent(final String context, final String id, final CacheLoader cacheLoader);

	/**
	 * 判断是否存在缓存对象
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	boolean hasContent(String context, String id);

	/**
	 * 添加缓存对象
	 * 
	 * @param context
	 * @param id
	 * @param data
	 */
	void addContent(String context, String id, Object data);

	/**
	 * 移除缓存对象
	 * 
	 * @param context
	 */
	void removeContext(String context);

	/**
	 * 移除缓存对象
	 * 
	 * @param context
	 * @param id
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
