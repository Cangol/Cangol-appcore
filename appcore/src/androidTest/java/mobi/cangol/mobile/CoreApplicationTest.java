package mobi.cangol.mobile;

import android.test.ApplicationTestCase;

import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.conf.ConfigService;


/**
 * Created by weixuewu on 16/6/11.
 */
public class CoreApplicationTest extends ApplicationTestCase<CoreApplication> {
    private CoreApplication coreApplication;
    public CoreApplicationTest() {
        super(CoreApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication=getApplication();
    }

    public void testGetAppService() {
        ConfigService configService= (ConfigService) coreApplication.getAppService(AppService.CONFIG_SERVICE);
        assertNotNull(configService.getAppDir());
    }
}