/**
 * Copyright (c) 2013 Cangol
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.http.extras;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RouteResponseHandler {
    protected static final int START_MESSAGE = 1;
    protected static final int SUCCESS_MESSAGE = 2;
    protected static final int FAILURE_MESSAGE = 3;
    private final static boolean DEBUG = true;
    private Handler handler;

    public RouteResponseHandler() {
        if (Looper.myLooper() != null) {
            handler = new Handler() {
                public void handleMessage(Message msg) {
                    RouteResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }

    public void onStart() {

    }

    public void onSuccess(int statusCode, String content) {

    }

    public void onFailure(Throwable error, String content) {

    }

    public void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    public void sendSuccessMessage(int statusCode, String responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, responseBody}));
    }

    public void sendFailureMessage(IOException e, String responseBody) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
    }

    boolean sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        String responseBody = null;
        boolean result = false;
        try {
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if (temp != null) {
                entity = new BufferedHttpEntity(temp);
                responseBody = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            sendFailureMessage(e, (String) null);
        }

        if (status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
        } else {
            sendSuccessMessage(status.getStatusCode(), responseBody);
            result = true;
        }
        return result;
    }

    protected void handleMessage(Message msg) {
        Object[] response;
        switch (msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[]) msg.obj;
                handleSuccessMessage(((Integer) response[0]).intValue(), (String) response[1]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                handleFailureMessage((Throwable) response[0], (String) response[1]);
                break;
            case START_MESSAGE:
                handleStartMessage();
                break;
        }
    }

    protected void handleStartMessage() {
        onStart();
    }

    protected void handleSuccessMessage(int statusCode, String responseBody) {
        onSuccess(statusCode, responseBody);
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