package mobi.cangol.mobile.service.analytics;

import android.test.ApplicationTestCase;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * Created by weixuewu on 16/6/11.
 */
public class AnalyticsServiceTest extends ApplicationTestCase<CoreApplication> {
    private static final String TAG = "AnalyticsServiceTest";
    private CoreApplication coreApplication;
    private AnalyticsService analyticsService;

    public AnalyticsServiceTest() {
        super(CoreApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication = getApplication();
        analyticsService = (AnalyticsService) coreApplication.getAppService(AppService.ANALYTICS_SERVICE);
    }

    public void testGetTracker() {
        ITracker tracker = analyticsService.getTracker(TAG);
    }

    public void testSends() {
        ITracker tracker = analyticsService.getTracker(TAG);
        IMapBuilder builder = IMapBuilder.Companion.build();
        builder.setUrl("http://www.cangol.mobi/cmweb/api/countly/event.do");
        builder.set("id", "1")
                .set("name", "Jick")
                .set("age", "24")
                .set("timestamp", TimeUtils.getCurrentTime());
        boolean result = tracker.send(builder);
    }

    public void testCloseTracker() {
        analyticsService.closeTracker(TAG);
    }
}
