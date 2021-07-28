package mobi.cangol.mobile.service.session;

import mobi.cangol.mobile.service.AppService;

/**
 * @author Cangol
 */
public interface SessionService extends AppService {

    /**
     * 获取公共Session
     */
    Session getSession();

    /**
     * 获取一个独立Session
     *
     * @param name
     */
    Session getSession(String name);

    /**
     * 清除所有Session（磁盘缓存和内存缓存）
     */
    void clearAll();

    /**
     * 刷新所有Session（磁盘缓存和内存缓存）
     */
    void refreshAll();


}
