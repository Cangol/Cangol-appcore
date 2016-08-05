package mobi.cangol.mobile.http;

import org.apache.http.conn.ClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class IdleConnectionMonitorThread extends Thread {
    private final static int IDLE_TIME_SECONDS = 30;
    private final static int WAIT_TIME = 5000;
    private final ClientConnectionManager connMgr;
    private volatile boolean isShutdown;

    public IdleConnectionMonitorThread(ClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!isShutdown) {
                synchronized (this) {
                    wait(WAIT_TIME);
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than IDLE_TIME_SECONDS
                    connMgr.closeIdleConnections(IDLE_TIME_SECONDS, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            shutdown();
        }
    }

    public void shutdown() {
        isShutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
