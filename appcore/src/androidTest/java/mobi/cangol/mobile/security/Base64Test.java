package mobi.cangol.mobile.security;


import android.test.InstrumentationTestCase;

/**
 * Created by weixuewu on 16/6/4.
 */
public class Base64Test extends InstrumentationTestCase{

    public void testEncode() {
        assertNotNull(Base64.encode("123456789"));
    }

    public void testDecode() {
        assertEquals(Base64.decode(Base64.encode("123456789")),"123456789");
    }
}