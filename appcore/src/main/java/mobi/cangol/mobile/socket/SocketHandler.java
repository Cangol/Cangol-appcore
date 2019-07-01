/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by weixuewu on 15/11/11.
 */
public abstract class SocketHandler {
    protected static final int FAIL_MESSAGE = -1;
    protected static final int START_MESSAGE = -4;
    protected static final int CONNECTED_MESSAGE = -2;
    protected static final int DISCONNECTED_MESSAGE = -3;
    private InternalHandler handler;
    protected Object readLocker;
    protected Object writeLocker;
    protected boolean isInterrupted = false;

    public SocketHandler() {
        if (Looper.myLooper() != null) {
            handler = new InternalHandler() {
                public void handleMessage(Message msg) {
                    SocketHandler.this.handleMessage(msg);
                }
            };
        }
        readLocker = new Object();
        writeLocker = new Object();
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }
    }


    protected void sendMessage(Message msg) {
        if (handler != null) {
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if (handler != null) {
            msg = this.handler.obtainMessage(responseMessage, response);
        } else {
            msg = new Message();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }

    protected Message obtainMessage(int responseMessage, int arg1, int arg2, Object response) {
        Message msg = null;
        if (handler != null) {
            msg = this.handler.obtainMessage(responseMessage, arg1, arg2, response);
        } else {
            msg = new Message();
            msg.what = responseMessage;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            msg.obj = response;
        }
        return msg;
    }

    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case FAIL_MESSAGE:
                handleFailMessage(((Object[]) msg.obj)[0], new Exception((Throwable) ((Object[]) msg.obj)[1]));
                break;
            case START_MESSAGE:
                handleStartMessage();
                break;
            case CONNECTED_MESSAGE:
                handleConnectedMessage();
                break;
            case DISCONNECTED_MESSAGE:
                handleDisconnectedMessage();
                break;
            default:
                break;
        }
    }

    public abstract boolean handleSocketWrite(DataOutputStream outputStream) throws IOException;

    public abstract boolean handleSocketRead(DataInputStream inputStream) throws IOException, ClassNotFoundException;

    protected abstract Object getSend();

    protected abstract void onFail(Object obj, Exception e);

    protected void onStart() {
    }

    protected void onConnected() {
    }


    protected void onDisconnected() {
    }


    public void sendFailMessage(Object obj) {
        sendMessage(obtainMessage(FAIL_MESSAGE, obj));
    }

    public void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    public void sendConnectedMessage() {
        sendMessage(obtainMessage(CONNECTED_MESSAGE, null));
    }

    public void sendDisconnectedMessage() {
        sendMessage(obtainMessage(DISCONNECTED_MESSAGE, null));
    }

    protected void handleFailMessage(Object obj, Exception exception) {
        onFail(obj, exception);
    }

    protected void handleStartMessage() {
        onStart();
    }

    protected void handleConnectedMessage() {
        onConnected();
    }

    protected void handleDisconnectedMessage() {
        onDisconnected();
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public void interrupted() {
        isInterrupted = true;
    }
}
