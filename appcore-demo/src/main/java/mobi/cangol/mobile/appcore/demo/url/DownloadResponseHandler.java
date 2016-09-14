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
package mobi.cangol.mobile.appcore.demo.url;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadResponseHandler {
    public final static String TAG = "DownloadResponseHandler";
    protected static final int WAIT_MESSAGE = 0;
    protected static final int START_MESSAGE = 1;
    protected static final int PROGRESS_MESSAGE = 2;
    protected static final int STOP_MESSAGE = 3;
    protected static final int FAILURE_MESSAGE = 4;
    protected static final int FINISH_MESSAGE = 5;
    private final static boolean DEBUG = true;
    private final int BUFF_SIZE = 8192;
    private Handler handler;

    public DownloadResponseHandler() {
        if (Looper.myLooper() != null) {
            handler = new Handler() {
                public void handleMessage(Message msg) {
                    DownloadResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }

    public void onWait() {

    }

    public void onStart(long start, long length) {
    }

    public void onStop(long end) {

    }

    public void onFinish(long end) {

    }

    public void onProgressUpdate(long end, int progress, int speed) {

    }

    public void onFailure(Throwable error, String content) {

    }

    public void sendWaitMessage() {
        sendMessage(obtainMessage(WAIT_MESSAGE, null));
    }

    public void sendStartMessage(long start, long length) {
        sendMessage(obtainMessage(START_MESSAGE, new Object[]{start, length}));
    }

    public void sendStopMessage(long end) {
        sendMessage(obtainMessage(STOP_MESSAGE, new Object[]{end}));
    }

    public void sendFinishMessage(long end) {
        sendMessage(obtainMessage(FINISH_MESSAGE, new Object[]{end}));
    }

    public void sendProgressMessage(long end, int progress, int speed) {
        sendMessage(obtainMessage(PROGRESS_MESSAGE, new Object[]{end, progress, speed}));
    }

    public void sendFailureMessage(Exception e, String responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    void sendResponseMessage(Response response, String saveFile) throws IOException {
        if (response.isSuccessful()) {
            ResponseBody requestBody=response.body();
            long length = requestBody.contentLength();
            RandomAccessFile threadfile = new RandomAccessFile(saveFile, "rwd");
            InputStream inputStream = requestBody.byteStream();
            long oldLength = threadfile.length();
            sendStartMessage(oldLength, length);
            if (oldLength < length) {
                threadfile.seek(oldLength);
                byte[] block = new byte[BUFF_SIZE];
                long starTime = System.currentTimeMillis();
                long startLength = 0;
                int readCount = 0;
                while (!Thread.currentThread().isInterrupted() && (readCount = inputStream.read(block, 0, BUFF_SIZE)) != -1) {
                    threadfile.write(block, 0, readCount);
                    oldLength += readCount;
                    startLength += readCount;
                    if ((System.currentTimeMillis() - starTime) > 500L) {
                        int progress = (int) (oldLength * 1.0f / length * 100);
                        int speed = (int) (startLength * 1000.0f / (System.currentTimeMillis() - starTime));
                        sendProgressMessage(oldLength, progress, speed);
                        starTime = System.currentTimeMillis();
                        startLength = 0;
                    }
                }
                if (threadfile != null) {
                    threadfile.close();
                }
                if (Thread.currentThread().isInterrupted()) {
                    sendStopMessage(oldLength);
                } else {
                    if (oldLength == length) {
                        sendProgressMessage(oldLength, 100, 0);
                        sendFinishMessage(length);
                    } else {
                        sendFinishMessage(oldLength);
                    }
                }
            } else if (oldLength == length) {
                sendProgressMessage(oldLength, 100, 0);
                sendFinishMessage(oldLength);
            } else {
                sendFailureMessage(new IOException(), "oldfile error oldLength>length");
            }
            if (requestBody != null) {
                requestBody.close();
            }
        } else {
            sendFailureMessage(new IOException(), "StatusCode " + response.code());
        }
    }

    protected void handleMessage(Message msg) {
        Object[] response = null;
        switch (msg.what) {
            case PROGRESS_MESSAGE:
                response = (Object[]) msg.obj;
                handleProgressMessage(((Long) response[0]).longValue(), ((Integer) response[1]).intValue(), ((Integer) response[2]).intValue());
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                handleFailureMessage((Throwable) response[0], (String) response[1]);
                break;
            case STOP_MESSAGE:
                response = (Object[]) msg.obj;
                handleStopMessage(((Long) response[0]).longValue());
                break;
            case START_MESSAGE:
                response = (Object[]) msg.obj;
                handleStartMessage(((Long) response[0]).longValue(), ((Long) response[1]).longValue());
                break;
            case WAIT_MESSAGE:
                handleWaitMessage();
                break;
            case FINISH_MESSAGE:
                response = (Object[]) msg.obj;
                handleFinishMessage(((Long) response[0]).longValue());
                break;
        }
    }

    protected void handleWaitMessage() {
        onWait();
    }

    protected void handleStartMessage(long start, long length) {
        onStart(start, length);
    }

    protected void handleFinishMessage(long end) {
        onFinish(end);
    }

    protected void handleStopMessage(long end) {
        onStop(end);
    }

    protected void handleProgressMessage(long end, int progress, int speed) {
        onProgressUpdate(end, progress, speed);
    }

    protected void handleFailureMessage(Throwable e, String responseBody) {
        onFailure(e, responseBody);
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
}