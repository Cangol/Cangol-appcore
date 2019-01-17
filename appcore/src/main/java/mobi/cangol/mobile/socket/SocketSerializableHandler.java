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

import android.annotation.SuppressLint;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import mobi.cangol.mobile.logging.Log;

/**
 * Created by weixuewu on 15/11/11.
 */
@SuppressLint("LongLogTag")
public abstract class SocketSerializableHandler extends SocketHandler {

    private static final  String TAG = "SocketSerializableHandler";
    protected static final boolean DEBUG = false;
    protected static final int RECEIVE_MESSAGE = 0;

    public SocketSerializableHandler() {
        super();
    }

    public abstract  void onReceive(Serializable msg);


    public void sendReceiveMessage(Serializable obj) {
        sendMessage(obtainMessage(RECEIVE_MESSAGE, obj));
    }

    protected void handleReceiveMessage(Serializable response) {
        onReceive(response);
    }


    protected void write(OutputStream os, Serializable obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os));
        oos.writeObject(obj);
    }

    protected Serializable read(DataInputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
        Object object = ois.readObject();
        return (Serializable) object;
    }

    protected void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == RECEIVE_MESSAGE) {
            handleReceiveMessage((Serializable) msg.obj);
        }
    }

    @Override
    public boolean handleSocketWrite(DataOutputStream outputStream) throws IOException {
        Serializable sendMsg = (Serializable) getSend();
        if (sendMsg == null || outputStream == null) return false;
        if (DEBUG) Log.d(TAG, "sendMsg=" + sendMsg);
        write(outputStream, sendMsg);
        return true;
    }

    @Override
    public boolean handleSocketRead(DataInputStream inputStream) throws IOException, ClassNotFoundException {
        if (inputStream == null) return false;
        Serializable receivedMsg = read(inputStream);
        if (DEBUG) Log.d(TAG, "receivedMsg=" + receivedMsg);
        sendReceiveMessage(receivedMsg);
        return true;
    }
}
