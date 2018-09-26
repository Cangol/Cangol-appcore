package mobi.cangol.mobile.stat;

import android.test.ApplicationTestCase;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * Created by weixuewu on 16/6/11.
 */
public class StatAgentTest extends ApplicationTestCase<CoreApplication> {
    private  static final String TAG="StatAgentTest";
    private CoreApplication coreApplication;
    public StatAgentTest() {
        super(CoreApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication=getApplication();
        StatAgent.initInstance(coreApplication);
    }
    public void testSetDebug() throws Exception {
        StatAgent.getInstance().setDebug(true);

    }
    public void testSend() throws Exception {
        StatAgent.getInstance().send(StatAgent.Builder.createAppView(TAG));
        StatAgent.getInstance().send(StatAgent.Builder.createException("test", "1", "test", TimeUtils.getCurrentTime(), "1"));
        StatAgent.getInstance().send(StatAgent.Builder.createEvent("test", TAG, "test", null, null));
        StatAgent.getInstance().send(StatAgent.Builder.createTiming(TAG, 1000L));
    }

    public void testSendLaunch() throws Exception {
        StatAgent.getInstance().sendLaunch();
    }

    public void testSendDevice() throws Exception {
        StatAgent.getInstance().sendDevice();
    }

    public void testSendTraffic() throws Exception {
        StatAgent.getInstance().sendTraffic();
    }

    public void testOnActivityResume() throws Exception {
        StatAgent.getInstance().onActivityResume(TAG);
    }

    public void testOnActivityPause() throws Exception {
        StatAgent.getInstance().onActivityPause(TAG);
    }

    public void testOnFragmentResume() throws Exception {
        StatAgent.getInstance().onFragmentResume(TAG);
    }
    public void testOnFragmentPause() throws Exception {
        StatAgent.getInstance().onFragmentPause(TAG);
    }
}