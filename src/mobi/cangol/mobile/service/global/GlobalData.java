package mobi.cangol.mobile.service.global;

import mobi.cangol.mobile.service.AppService;

/**
 * 适合小型数据缓存
 * @author xuewu.wei
 *
 */
public interface GlobalData extends AppService{
	
	/**
	 * value 的class类型,仅限float,int,boolean,long,String,Set<String>(SDK>=11),JSONObject,JSONArray,实现了Serializable
	 * 本地化存储
	 * @param name
	 * @param value
	 */
	void save(String name,Object value);
	
	/**
	 * 内存缓存
	 * @param name
	 * @param value
	 */
	void put(String name,Object value);
	
	/**
	 * 获取本地缓存和内存缓存
	 * @param name
	 * @return
	 */
	Object get(String name);
	
	/**
	 * 刷新本地缓存和内存缓存
	 * @param name
	 * @return
	 */
	void refresh();
	
	/**
	 * 清除内存缓存
	 * @return
	 */
	void clear();
	
}
