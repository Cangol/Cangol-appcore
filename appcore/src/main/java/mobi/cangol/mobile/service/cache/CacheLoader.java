package mobi.cangol.mobile.service.cache;

public interface CacheLoader {

	/**
	 * 加载中...
	 */
	void loading();

	/**
	 * 获取缓存数据
	 */
	void returnContent(Object content);
}
