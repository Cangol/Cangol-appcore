/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package mobi.cangol.mobile.http;

import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class AsyncHttpRequest implements Runnable {
    private final AsyncHttpClient client;
    private final OkHttpClient content;
    private final Request request;
    private final AsyncHttpResponseHandler responseHandler;
    private boolean isBinaryRequest;
    private int executionCount;

    public AsyncHttpRequest(AsyncHttpClient client, OkHttpClient content, Request request, AsyncHttpResponseHandler responseHandler) {
        this.client = client;
        this.content = content;
        this.request = request;
        this.responseHandler = responseHandler;
        if (responseHandler instanceof BinaryHttpResponseHandler) {
            this.isBinaryRequest = true;
        }
    }

    public void run() {
        try {
            if (responseHandler != null) {
                responseHandler.sendStartMessage();
            }

            makeRequestWithRetries();

            if (responseHandler != null) {
                responseHandler.sendFinishMessage();
            }
        } catch (IOException e) {
            if (responseHandler != null) {
                responseHandler.sendFinishMessage();
                if (this.isBinaryRequest) {
                    responseHandler.sendFailureMessage(e, (byte[]) null);
                } else {
                    responseHandler.sendFailureMessage(e, (String) null);
                }
            }
        }
    }

    private void makeRequest() throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            final Response response = content.newCall(request).execute();
            if (!Thread.currentThread().isInterrupted()) {
                if (responseHandler != null) {
                    responseHandler.sendResponseMessage(response);
                }
            } else {
                Log.d("AsyncHttpRequest", "Thread.isInterrupted");
            }
        }
    }

    private void makeRequestWithRetries() throws ConnectException {
        // This is an additional layer of retry logic lifted from droid-fu
        // See: https://github.com/kaeppler/droid-fu/blob/master/src/main/java/com/github/droidfu/http/BetterHttpRequestBase.java
        boolean retry = true;
        IOException cause = null;
        final RetryHandler retryHandler = client.getRetryHandler();
        while (retry) {
            try {
                makeRequest();
                return;
            } catch (UnknownHostException e) {
                if (responseHandler != null) {
                    responseHandler.sendFailureMessage(e, "can't resolve host");
                }
                return;
            } catch (SocketException e) {
                // Added to detect host unreachable
                if (responseHandler != null) {
                    responseHandler.sendFailureMessage(e, "can't resolve host");
                }
                return;
            } catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(cause, ++executionCount);
            } catch (NullPointerException e) {
                // there's a bug in HttpClient 4.0.x that on some occasions causes
                // DefaultRequestExecutor to throw an NPE, see
                // http://code.google.com/p/android/issues/detail?id=5255
                cause = new IOException("NPE in HttpClient" + e.getMessage());
                retry = retryHandler.retryRequest(cause, ++executionCount);
            }
        }

        // no retries left, crap out with exception
        final ConnectException ex = new ConnectException();
        ex.initCause(cause);
        throw ex;
    }
}
