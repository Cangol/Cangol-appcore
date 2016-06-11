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
    public void testGetCacheDir() throws Exception {
        configService.getCacheDir();
    }
    public void testGetImageDir() throws Exception {
        configService.getImageDir();
    }

    public void testGetTempDir() throws Exception {
        configService.getTempDir();
    }

    public void testGetDownloadDir() throws Exception {
        configService.getDownloadDir();
    }

    public void testGetUpgradeDir() throws Exception {
        configService.getUpgradeDir();
    }

    public void testGetDatabaseName() throws Exception {
        configService.getDatabaseName();
    }

    public void testGetSharedName() throws Exception {
        configService.getSharedName();
    }

    public void testSetUseInternalStorage() throws Exception {
        configService.setUseInternalStorage(true);
        configService.setUseInternalStorage(false);
    }

    public void testIsUseInternalStorage() throws Exception {
        configService.isUseInternalStorage();
    }

    public void testGetAppDir() throws Exception {
        configService.getAppDir();
    }

    public void testSetCustomAppDir() throws Exception {
        String path="/sdcard/appcore";
        //configService.setCustomAppDir(path);
    }

    public void testIsCustomAppDir() throws Exception {
        configService.isCustomAppDir();
    }

    public void testResetAppDir() throws Exception {
        configService.resetAppDir();
    }
}