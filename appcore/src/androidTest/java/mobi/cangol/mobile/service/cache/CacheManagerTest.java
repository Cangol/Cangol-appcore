package mobi.cangol.mobile.service.cache;

import android.test.ApplicationTestCase;

import java.io.Serializable;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.AppService;

/**
 * Created by weixuewu on 16/6/11.
 */
public class CacheManagerTest extends ApplicationTestCase<CoreApplication> {
    private static final String TAG = "CacheManagerTest";
    private CoreApplication coreApplication;
    private CacheManager cacheManager;
    public CacheManagerTest() {
        super(CoreApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication=getApplication();
        cacheManager= (CacheManager) coreApplication.getAppService(AppService.CACHE_MANAGER);
    }
    public void testGetContent() throws Exception {
        cacheManager.getContent(TAG,"user");
    }

    public void testGetContent1() throws Exception {
        cacheManager.getContent(TAG, "user", new CacheLoader() {
            @Override
            public void loading() {

            }

            @Override
            public void returnContent(Object content) {

            }
        });
    }

    public void testHasContent() throws Exception {
        cacheManager.hasContent(TAG,"user");
    }

    public void testAddContent() throws Exception {
        User user=new User(1,"Jick","12");
        cacheManager.addContent(TAG,"user",user);
    }

    public void testRemoveContext() throws Exception {
        cacheManager.removeContext(TAG);
    }

    public void testRemoveContent() throws Exception {
        cacheManager.removeContent(TAG,"user");
    }

    public void testSize() throws Exception {
        cacheManager.size();
    }

    public void testFlush() throws Exception {
        cacheManager.flush();
    }

    public void testClose() throws Exception {
        cacheManager.close();
    }

    public void testClearCache() throws Exception {
        cacheManager.clearCache();
    }

}
class User implements Serializable {
    private String name;
    private String age;
    private int id;
    public User(){}

    public User(int id,String name,  String age) {
        this.age = age;
        this.id = id;
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id  +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}