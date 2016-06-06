package mobi.cangol.mobile.security;

import android.test.InstrumentationTestCase;

/**
 * Created by weixuewu on 16/6/4.
 */
public class AESUtilsTest extends InstrumentationTestCase {

    public void testEncrypt() throws Exception {
        String source="test";
        String seed="12345678";
        String dst=AESUtils.encrypt(seed, source);
        assertNotNull(dst);
        assertEquals("73C58BAFE578C59366D8C995CD0B9D6D",dst);
    }

    public void testDecrypt() throws Exception {
        String source="73C58BAFE578C59366D8C995CD0B9D6D";
        String seed="12345678";
        String dst=AESUtils.decrypt(seed, source);
        assertNotNull(dst);
        assertEquals("test",dst);
        //assertEquals(AESUtils.decrypt("12345678", AESUtils.encrypt("12345678", "test")), "test");
    }
}