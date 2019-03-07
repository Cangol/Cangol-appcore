package mobi.cangol.mobile.service.upgrade;

import android.test.ApplicationTestCase;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.AppService;


/**
 * Created by weixuewu on 16/6/11.
 */
public class UpgradeServiceTest extends ApplicationTestCase<CoreApplication> {
    private static final String TAG = "UpgradeServiceTest";
    private CoreApplication coreApplication;
    private UpgradeService upgradeService;

    public UpgradeServiceTest() {
        super(CoreApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication = getApplication();
        upgradeService = (UpgradeService) coreApplication.getAppService(AppService.UPGRADE_SERVICE);
    }

    public void testUpgrade() {
        String url="http://180.153.105.145/dd.myapp.com/16891/8E5A9885970F76080F8445C652DE347C.apk?mkey=5715c34fc20a8141&f=d511&fsname=com.tencent.mobileqq_6.3.1_350.apk&p=.apk";
        upgradeService.upgrade(TAG,url,true);
    }

    public void testCancel() {
        upgradeService.cancel(TAG);
    }

    public void testSetOnUpgradeListener() {
        upgradeService.setOnUpgradeListener(TAG, new OnUpgradeListener() {
            @Override
            public void upgrade(boolean force) {

            }

            @Override
            public void progress(int speed, int progress) {

            }

            @Override
            public void onFinish(String filePath) {

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
}