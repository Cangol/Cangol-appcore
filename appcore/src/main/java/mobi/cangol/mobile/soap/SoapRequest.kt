/**
 * Copyright (c) 2013 Cangol.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.soap


import mobi.cangol.mobile.logging.Log
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException

/**
 * @author Cangol
 */
class SoapRequest(private val ht: HttpTransportSE, private val envelope: SoapSerializationEnvelope,
                  private val namespace: String, private val responseHandler: SoapResponseHandler?) : Runnable {

    override fun run() {
        if (!Thread.currentThread().isInterrupted) {
            try {
                responseHandler?.sendStartMessage()

                ht.debug = true
                ht.call(namespace, envelope)
                if (envelope.response != null) {
                    val response = envelope.response.toString()
                    if (!Thread.currentThread().isInterrupted) {
                        responseHandler?.sendResponseMessage(response)
                    }
                } else {
                    responseHandler?.sendFailureMessage(envelope.bodyIn.toString())
                }
            } catch (e: IOException) {
                Log.d("IOException", e)
                responseHandler?.sendFailureMessage(e.message)
            } catch (e: XmlPullParserException) {
                Log.d("XmlPullParserException", e)
                responseHandler?.sendFailureMessage(e.message)
            } finally {
                responseHandler?.sendFinishMessage()
            }
        }
    }

}
