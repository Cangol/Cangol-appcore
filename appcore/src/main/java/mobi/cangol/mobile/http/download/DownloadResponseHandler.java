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
package mobi.cangol.mobile.http.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadResponseHandler {
    public static final  String TAG = "DownloadResponseHandler";
    protected static final int WAIT_MESSAGE = 0;
    protected static final int START_MESSAGE = 1;
    protected static final int PROGRESS_MESSAGE = 2;
    protected static final int STOP_MESSAGE = 3;
    protected static final int FAILURE_MESSAGE = 4;
    protected static final int FINISH_MESSAGE = 5;
    private static final int BUFF_SIZE = 8192;
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

    /**
     * 等候状态
     */
    public void onWait() {
        //do nothings
    }

    /**
     * 开始状态
     *
     * @param start
     * @param length
     */
    public void onStart(long start, long length) {
        //do nothings
    }

    /**
     * 停止状态
     *
     * @param end
     */
    public void onStop(long end) {
        //do nothings
    }

    /**
     * 完成状态
     *
     * @param end
     */
    public void onFinish(long end) {
        //do nothings
    }

    /**
     * 更新状态
     *
     * @param end
     * @param progress
     * @param speed
     */
    public void onProgressUpdate(long end, int progress, int speed) {
        //do nothings
    }

    /**
     * 失败状态
     *
     * @param error
     * @param content
     */
    public void onFailure(Throwable error, String content) {
        //do nothings
    }

    protected void sendWaitMessage() {
        sendMessage(obtainMessage(WAIT_MESSAGE, null));
    }

    protected void sendStartMessage(long start, long length) {
        sendMessage(obtainMessage(START_MESSAGE, new Object[]{start, length}));
    }

    protected void sendStopMessage(long end) {
        sendMessage(obtainMessage(STOP_MESSAGE, new Object[]{end}));
    }

    protected void sendFinishMessage(long end) {
        sendMessage(obtainMessage(FINISH_MESSAGE, new Object[]{end}));
    }

    protected void sendProgressMessage(long end, int progress, int speed) {
        sendMessage(obtainMessage(PROGRESS_MESSAGE, new Object[]{end, progress, speed}));
    }

    protected void sendFailureMessage(Exception e, String responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    void sendResponseMessage(Response response, String saveFile) throws IOException {
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            long length = responseBody.contentLength();
            RandomAccessFile threadfile = new RandomAccessFile(saveFile, "rwd");
            InputStream inputStream = responseBody.byteStream();
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
            if (responseBody != null) {
                responseBody.close();
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
                default:
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