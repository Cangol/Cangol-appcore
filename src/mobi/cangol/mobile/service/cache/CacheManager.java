package mobi.cangol.mobile.service.cache;

import mobi.cangol.mobile.service.AppService;

public interface CacheManager extends AppService {
	
	public final static String CACHE_DIR="cache_dir";
	
	public final static String CACHE_SIZE="cache_size";
	
	
	Object getContent(String context,String id);
	
	void getContent(final String context,final String id,final CacheLoader cacheLoader);
	
	boolean hasContent(String context,String id);
	
	void addContent(String context, String id, Object data);
	
	void removeContext(String context);
	
	void removeContent(String context,String id);
	
	long size();
	
	void flush();
	
	void close();
	
	void clearCache();
	
}
