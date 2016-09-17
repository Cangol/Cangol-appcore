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

import android.util.Log;

import java.io.IOException;
import java.io.InterruptedIOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadThread implements Runnable {
    public final static String TAG = "DownloadThread";
    private final DownloadResponseHandler responseHandler;
    private DownloadHttpClient context;
    private OkHttpClient client;
    private Request request;
    private int executionCount;
    private long from;
    private String saveFile;

    public DownloadThread(DownloadHttpClient context, OkHttpClient client, Request request, DownloadResponseHandler responseHandler, String saveFile) {
        this.client = client;
        this.context=context;
        this.request = request;
        this.responseHandler = responseHandler;
        this.saveFile = saveFile;
    }

    @Override
    public void run() {
        try {
            from=parserFrom(request);
            if (responseHandler != null) {
                responseHandler.sendWaitMessage();
            }
            makeRequestWithRetries();
        } catch (Exception e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(e, e.getMessage());
            }
        }
    }

    private void makeRequest() throws IOException, InterruptedException {
        if (!Thread.currentThread().isInterrupted()) {

            Response response = client.newCall(request).execute();
            if (!Thread.currentThread().isInterrupted()) {
                if (responseHandler != null) {
                    responseHandler.sendResponseMessage(response, saveFile);
                }
            } else {
                Log.d(TAG, "Thread.isInterrupted");
                responseHandler.sendStopMessage(from);
            }
        } else {
            Log.d(TAG, "Thread.isInterrupted");
            responseHandler.sendStopMessage(from);
        }
    }
    private long parserFrom(Request request){
        String value=request.header("Range");
        if(value!=null&&value.contains("bytes=")){
            value=value.substring(value.indexOf("bytes=")+"bytes=".length(),value.indexOf("-"));
        }else{
            value="0";
        }
        return Long.valueOf(value);
    }
    private void makeRequestWithRetries() throws Exception {
        boolean retry = true;
        Exception cause = null;
        DownloadRetryHandler retryHandler = this.context.getDownloadRetryHandler();
        while (retry) {
            try {
                makeRequest();
                return;
            } catch (InterruptedIOException e) {
                responseHandler.sendStopMessage(from);
                return;
            } catch (InterruptedException e) {
                responseHandler.sendStopMessage(from);
                return;
            } catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(e, ++executionCount);
            } catch (Exception e) {
                cause = e;
                retry = false;
            }
        }

        Exception ex = new Exception();
        ex.initCause(cause);
        throw ex;
    }
}
