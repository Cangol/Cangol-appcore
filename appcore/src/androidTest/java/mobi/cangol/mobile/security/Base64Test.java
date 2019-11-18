package mobi.cangol.mobile.security;


import android.test.InstrumentationTestCase;

/**
 * Created by weixuewu on 16/6/4.
 */
public class Base64Test extends InstrumentationTestCase{

    public void testEncode() {
        assertEquals("MTIzNDU2Nzg5",Base64.encode("123456789"));
    }

    public void testDecode() {
        assertEquals("123456789",Base64.decode("MTIzNDU2Nzg5"));
    }
}