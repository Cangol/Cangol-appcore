package mobi.cangol.mobile.db;

/**
 * Created by weixuewu on 2017/11/26.
 */

public interface DbProcessor {

    Object process(Dao dao);

    void completed(Object result);
}
