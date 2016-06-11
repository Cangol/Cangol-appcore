package mobi.cangol.mobile.socket;

import android.test.AndroidTestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by weixuewu on 16/6/11.
 */
public class SocketClientTest extends AndroidTestCase {

    public void testSend() throws Exception {
        final SocketClient socketClient=new SocketClient();
        socketClient.connect(getContext(), "127.0.0.1", 8080, true, 10 * 1000, new SocketHandler() {
            @Override
            public boolean handleSocketWrite(OutputStream outputStream) throws IOException {
                return false;
            }

            @Override
            public boolean handleSocketRead(int timeout, InputStream inputStream) throws IOException, ClassNotFoundException {
                return false;
            }

            @Override
            protected Object getSend() {
                return null;
            }

            @Override
            protected void onFail(Object obj, Exception e) {
                socketClient.cancel(true);
            }
        });
    }
}