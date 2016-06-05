package mobi.cangol.mobile.security;

import android.test.InstrumentationTestCase;

/**
 * Created by weixuewu on 16/6/4.
 */
public class AESUtilsTest extends InstrumentationTestCase {

    public void testEncrypt() throws Exception {
        assertNotNull(AESUtils.encrypt("12345678", "test"));
    }

    public void testDecrypt() throws Exception {
        //assertEquals(AESUtils.decrypt("12345678", AESUtils.encrypt("12345678", "test")), "test");
    }

    public void testToByte() throws Exception {

    }

    public void testToHex() throws Exception {

    }
}