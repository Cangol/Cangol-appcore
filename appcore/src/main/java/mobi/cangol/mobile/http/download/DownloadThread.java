/**
 * Copyright (c) 2013 Cangol
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.http.download;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InterruptedIOException;

public class DownloadThread implements Runnable {
    private final static boolean DEBUG=true;
    public final static  String TAG = "DownloadThread";
    private AbstractHttpClient client;
    private HttpContext context;
    private HttpUriRequest request;
    private final DownloadResponseHandler responseHandler;
    private int executionCount;
    private long from;
    private String saveFile;
    public DownloadThread(AbstractHttpClient client,
                          HttpContext context, HttpUriRequest request, DownloadResponseHandler responseHandler, long from, String saveFile) {
        this.client=client;
        this.context=context;
        this.request=request;
        this.responseHandler=responseHandler;
        this.from=from;
        this.saveFile=saveFile;
    }
    @Override
    public void run(){
        try {
            if(responseHandler != null) {
                responseHandler.sendWaitMessage();
            }
            makeRequestWithRetries();
        } catch (Exception e) {
            if(responseHandler != null) {
                responseHandler.sendFailureMessage(e, e.getMessage());
            }
        }
    }

    private void makeRequest() throws IOException,InterruptedException {
        if(!Thread.currentThread().isInterrupted()) {
            request.addHeader("Range", "bytes="+from+"-");
            HttpResponse response = client.execute(request, context);
            if(!Thread.currentThread().isInterrupted()) {
                if(responseHandler != null) {
                    responseHandler.sendResponseMessage(response,saveFile);
                }
            } else{
                if(DEBUG)Log.d(TAG, "Thread.isInterrupted");
                responseHandler.sendStopMessage(from);
            }
        }else{
            if(DEBUG)Log.d(TAG, "Thread.isInterrupted");
            responseHandler.sendStopMessage(from);
        }
    }

    private void makeRequestWithRetries() throws Exception {
        boolean retry = true;
        Exception cause = null;
        HttpRequestRetryHandler retryHandler = this.client.getHttpRequestRetryHandler();
        while (retry) {
            try {
                makeRequest();
                return;
            } catch (InterruptedIOException e){
                responseHandler.sendStopMessage(from);
                return;
            } catch (InterruptedException e){
                responseHandler.sendStopMessage(from);
                return;
            } catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(e, ++executionCount, context);
            } catch (Exception e) {
                cause = e;
                retry=false;
            }
        }

        Exception ex = new Exception();
        ex.initCause(cause);
        throw ex;
    }


}
