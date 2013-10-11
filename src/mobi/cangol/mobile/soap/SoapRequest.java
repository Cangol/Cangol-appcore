/** 
 * Copyright (c) 2013 Cangol.
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
package mobi.cangol.mobile.soap;

import java.io.IOException;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

/**
 * @Description SoapRequest.java
 * @author xuewu.wei
 * @date 2013-8-14
 */
public class SoapRequest implements Runnable {
	private static final boolean DEBUG = false;
	private static final String TAG = "SoapRequest";
	private HttpTransportSE ht;
	private SoapSerializationEnvelope envelope;
	private String namespace;
	private final SoapResponseHandler responseHandler;

	public SoapRequest(HttpTransportSE ht, SoapSerializationEnvelope envelope,
			String namespace, SoapResponseHandler responseHandler) {
		this.ht = ht;
		this.envelope = envelope;
		this.namespace = namespace;
		this.responseHandler = responseHandler;
	}

	@Override
	public void run() {
		 if(!Thread.currentThread().isInterrupted()) {
			 try {
					if (responseHandler != null) 
						responseHandler.sendStartMessage();
				
					ht.debug = true;
					ht.call(namespace, envelope);
					if (envelope.getResponse() != null) {
						if (DEBUG)
							Log.d(TAG, "response:" + envelope.bodyIn.toString());
						String response =envelope.getResponse().toString() ;
						if (!Thread.currentThread().isInterrupted()) {
							if (responseHandler != null)
								responseHandler.sendResponseMessage(response);
						}
					} else {
						if (responseHandler != null)
							responseHandler.sendFailureMessage( envelope.bodyIn.toString());
					}
				} catch (IOException e) {
					if (DEBUG)e.printStackTrace();
					Log.d(TAG, "IOException",e);
					if (responseHandler != null)
						responseHandler.sendFailureMessage(e.getMessage());
				} catch (XmlPullParserException e) {
					if (DEBUG)e.printStackTrace();
					Log.d(TAG, "XmlPullParserException",e);
					if (responseHandler != null)
						responseHandler.sendFailureMessage(e.getMessage());
				}finally{
					if (responseHandler != null) 
						responseHandler.sendFinishMessage();
				}
		 }
	}

}
