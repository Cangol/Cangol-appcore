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
package mobi.cangol.mobile.http.extras;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Message;

public class RouteBinaryResponseHandler extends RouteResponseHandler {
	// Allow images by default
    private static String[] mAllowedContentTypes = new String[] {
        "application/zip", 
        "application/x-zip-compressed", 
        "application/octet-stream"
    };
	public RouteBinaryResponseHandler(){
		super();
	}
	
    public void onStart() {
    	super.onStart();
    }
    
    public void onSuccess(int statusCode, byte[] content) {
    }
    
    public void onFailure(Throwable error, byte[] content) {
    }
    
    public void sendSuccessMessage(int statusCode, byte[] responseBody) {
    	sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, responseBody}));
	}
    
	public void sendFailureMessage(IOException e, byte[] responseBody) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{e, responseBody}));
	}
	
	boolean sendResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        byte[]  responseBody = null;
        boolean result=false;
        Header[] contentTypeHeaders = response.getHeaders("Content-Type");
        if(contentTypeHeaders.length != 1) {
            //malformed/ambiguous HTTP Header, ABORT!
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), "None, or more than one, Content-Type Header found!"), responseBody);
            return false;
        }
        boolean foundAllowedContentType = false;
        Header contentTypeHeader = contentTypeHeaders[0];
        for(String anAllowedContentType : mAllowedContentTypes) {
            if(anAllowedContentType.equals(contentTypeHeader.getValue())) {
                foundAllowedContentType = true;
            }
        }
        if(!foundAllowedContentType) {
            //Content-Type not in allowed list, ABORT!
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), "Content-Type not allowed!"), responseBody);
            return false;
        }
        try {
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if(temp != null) {
                entity = new BufferedHttpEntity(temp);
            }
            responseBody = EntityUtils.toByteArray(entity);
        } catch(IOException e) {
            sendFailureMessage(e, (String) null);
        }

        if(status.getStatusCode() >= 300) {
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), responseBody);
        } else {
    		sendSuccessMessage(status.getStatusCode(), responseBody);
    		result=true;
        }
        return result;
    }
	protected void handleMessage(Message msg) {
		Object[] response;
    	 switch(msg.what) {
	    	 case SUCCESS_MESSAGE:
	             response = (Object[])msg.obj;
	             handleSuccessMessage(((Integer) response[0]).intValue(), (byte[]) response[1]);
	             break;
	         case FAILURE_MESSAGE:
	             response = (Object[])msg.obj;
	             handleFailureMessage((Throwable)response[0], (byte[])response[1]);
	             break;
	         case START_MESSAGE:
	             handleStartMessage();
	             break;
    	 }
    }
	protected void handleStartMessage() {
		 onStart();
	}
    protected void handleSuccessMessage(int statusCode, byte[] responseBody) {
        onSuccess(statusCode, responseBody);
    }

    protected void handleFailureMessage(Throwable e, byte[] responseBody) {
        onFailure(e, responseBody);
    }
}