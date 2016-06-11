package mobi.cangol.mobile.service.download;


import android.test.ApplicationTestCase;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.AppService;

/**
 * Created by weixuewu on 16/6/11.
 */
public class DownloadManagerTest extends ApplicationTestCase<CoreApplication> {
    private static final String TAG = "DownloadManagerTest";
    private CoreApplication coreApplication;
    private DownloadManager downloadManager;

    public DownloadManagerTest() {
        super(CoreApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication = getApplication();
        downloadManager = (DownloadManager) coreApplication.getAppService(AppService.DOWNLOAD_MANAGER);
    }

    public void testGetDownloadExecutor() throws Exception {
        downloadManager.getDownloadExecutor(TAG);
    }

    public void testRegisterExecutor() throws Exception {
        //downloadManager.registerExecutor(TAG,new Dow);
    }

    public void testRecoverAllAllDownloadExecutor() throws Exception {
        downloadManager.recoverAllAllDownloadExecutor();
    }

    public void testInterruptAllDownloadExecutor() throws Exception {
        downloadManager.interruptAllDownloadExecutor();
    }
}