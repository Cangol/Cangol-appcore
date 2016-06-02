package mobi.cangol.mobile;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<CoreApplication> {
    private CoreApplication coreApplication;

    public ApplicationTest() {
        super(CoreApplication.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.setContext(getSystemContext());
    }
    @SmallTest
    public void testPreconditions() {
        assertNotNull(coreApplication);
    }
    @MediumTest
    public void testEnable() {
        this.createApplication();
    }
}