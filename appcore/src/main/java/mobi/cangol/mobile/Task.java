package mobi.cangol.mobile;


import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by xuewu.wei on 2018/5/3.
 */
public abstract class Task<R> implements Runnable {
    private InnerHandler handler;

    public Task() {
        this.handler = new InnerHandler(this);
    }

    @Override
    public void run() {
        final R r = call();
        Message.obtain(handler, 1, r).sendToTarget();
    }

    public abstract R call();

    public abstract void result(R r);

    public static class InnerHandler extends Handler {
        private final WeakReference<Task> taskWeakReference;

        public InnerHandler(Task task) {
            taskWeakReference = new WeakReference<>(task);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 && taskWeakReference.get() != null) {
                taskWeakReference.get().result(msg.obj);
                taskWeakReference.clear();
            }
        }
    }
}
