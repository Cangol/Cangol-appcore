package mobi.cangol.mobile.service.status;


import android.content.Context;
import android.test.ApplicationTestCase;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.AppService;

/**
 * Created by weixuewu on 16/6/11.
 */
public class StatusServiceTest  extends ApplicationTestCase<CoreApplication> {
    private static final String TAG = "StatusServiceTest";
    private CoreApplication coreApplication;
    private StatusService statusService;
    private StatusListener statusListener;
    public StatusServiceTest() {
        super(CoreApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication = getApplication();
        statusService = (StatusService) coreApplication.getAppService(AppService.STATUS_SERVICE);
        statusListener=new StatusListener() {
            @Override
            public void networkConnect(Context context) {

            }

            @Override
            public void networkDisconnect(Context context) {

            }

            @Override
            public void networkTo3G(Context context) {

            }

            @Override
            public void storageRemove(Context context) {

            }

            @Override
            public void storageMount(Context context) {

            }

            @Override
            public void callStateIdle() {

            }

            @Override
            public void callStateOffhook() {

            }

            @Override
            public void callStateRinging() {

            }
        };
    }

    public void testIsConnection() throws Exception {
        statusService.isConnection();
    }

    public void testIsWifiConnection() throws Exception {
        statusService.isWifiConnection();
    }

    public void testIsGPSLocation() throws Exception {
        statusService.isGPSLocation();
    }

    public void testIsNetworkLocation() throws Exception {
        statusService.isNetworkLocation();
    }

    public void testIsCallingState() throws Exception {
        statusService.isCallingState();
    }

    public void testRegisterStatusListener() throws Exception {
        statusService.registerStatusListener(statusListener);
        statusService.unregisterStatusListener(statusListener);
    }

    public void testUnregisterStatusListener() throws Exception {

    }
}