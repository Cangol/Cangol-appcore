package mobi.cangol.mobile.service.conf;


import android.test.ApplicationTestCase;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.AppService;

/**
 * Created by weixuewu on 16/6/11.
 */
public class ConfigServiceTest extends ApplicationTestCase<CoreApplication> {
    private static final String TAG = "AnalyticsServiceTest";
    private CoreApplication coreApplication;
    private ConfigService configService;

    public ConfigServiceTest() {
        super(CoreApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication = getApplication();
        configService = (ConfigService) coreApplication.getAppService(AppService.CONFIG_SERVICE);
    }

    public void testGetCacheDir() {
        assertNotNull(configService.getCacheDir());
    }

    public void testGetImageDir() {
        assertNotNull(configService.getImageDir());
    }

    public void testGetTempDir() {
        assertNotNull(configService.getTempDir());
    }

    public void testGetDownloadDir() {
        assertNotNull(configService.getDownloadDir());
    }

    public void testGetUpgradeDir() {
        assertNotNull(configService.getUpgradeDir());
    }

    public void testGetDatabaseName() {
        assertNotNull(configService.getDatabaseName());
    }

    public void testGetSharedName() {
        assertNotNull(configService.getSharedName());
    }

    public void testSetUseInternalStorage() {
        configService.setUseInternalStorage(true);
        configService.setUseInternalStorage(false);
    }

    public void testIsUseInternalStorage() {
        configService.isUseInternalStorage();
    }

    public void testGetAppDir() {
        assertNotNull(configService.getAppDir());
    }

    public void testSetCustomAppDir() {
        String path = "/sdcard/appcore";
        //configService.setCustomAppDir(path);
    }

    public void testIsCustomAppDir() {
        configService.isCustomAppDir();
    }

    public void testResetAppDir() {
        configService.resetAppDir();
    }
}