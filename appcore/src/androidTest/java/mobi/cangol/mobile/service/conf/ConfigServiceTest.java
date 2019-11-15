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
        configService = (ConfigService) coreApplication.getAppService(AppService.Companion.getCONFIG_SERVICE());
    }
    public void testGetCacheDir() {
        configService.getCacheDir();
    }
    public void testGetImageDir() {
        configService.getImageDir();
    }

    public void testGetTempDir() {
        configService.getTempDir();
    }

    public void testGetDownloadDir() {
        configService.getDownloadDir();
    }

    public void testGetUpgradeDir() {
        configService.getUpgradeDir();
    }

    public void testGetDatabaseName() {
        configService.getDatabaseName();
    }

    public void testGetSharedName() {
        configService.getSharedName();
    }

    public void testSetUseInternalStorage() {
        configService.setUseInternalStorage(true);
        configService.setUseInternalStorage(false);
    }

    public void testIsUseInternalStorage() {
        configService.isUseInternalStorage();
    }

    public void testGetAppDir() {
        configService.getAppDir();
    }

    public void testSetCustomAppDir() {
        String path="/sdcard/appcore";
        //configService.setCustomAppDir(path);
    }

    public void testIsCustomAppDir() {
        configService.isCustomAppDir();
    }

    public void testResetAppDir() {
        configService.resetAppDir();
    }
}