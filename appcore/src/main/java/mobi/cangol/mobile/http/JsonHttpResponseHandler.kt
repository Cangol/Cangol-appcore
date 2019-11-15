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

package mobi.cangol.mobile.http

import android.content.Context
import android.os.Message

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

open class JsonHttpResponseHandler : AsyncHttpResponseHandler {

    constructor() : super() {}

    constructor(context: Context) : super(context) {}

    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param response the parsed json object found in the server response (if any)
     */
    fun onSuccess(response: JSONObject) {
        //do nothings
    }


    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param response the parsed json array found in the server response (if any)
     */
    fun onSuccess(response: JSONArray) {
        //do nothings
    }

    /**
     * Fired when a request returns successfully and contains a json object
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param statusCode the status code of the response
     * @param response   the parsed json object found in the server response (if any)
     */
    open fun onSuccess(statusCode: Int, response: JSONObject) {
        onSuccess(response)
    }


    /**
     * Fired when a request returns successfully and contains a json array
     * at the base of the response string. Override to handle in your
     * own code.
     *
     * @param statusCode the status code of the response
     * @param response   the parsed json array found in the server response (if any)
     */
    open fun onSuccess(statusCode: Int, response: JSONArray) {
        onSuccess(response)
    }

    open fun onFailure(e: Throwable, errorResponse: JSONObject?) {
        //do nothings
    }

    fun onFailure(e: Throwable, errorResponse: JSONArray?) {
        //do nothings
    }


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    override fun sendSuccessMessage(statusCode: Int, responseBody: String) {
        try {
            val jsonResponse = parseResponse(responseBody)
            sendMessage(obtainMessage(SUCCESS_JSON_MESSAGE, arrayOf(statusCode, jsonResponse)))
        } catch (e: JSONException) {
            sendFailureMessage(e, responseBody)
        }

    }


    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    override fun handleMessage(msg: Message) {
        if (msg.what == SUCCESS_JSON_MESSAGE) {
            val response = msg.obj as Array<Any>
            handleSuccessJsonMessage((response[0] as Int).toInt(), response[1])
        } else {
            super.handleMessage(msg)
        }
    }

    private fun handleSuccessJsonMessage(statusCode: Int, jsonResponse: Any) {
        when (jsonResponse) {
            is JSONObject -> onSuccess(statusCode, jsonResponse)
            is JSONArray -> onSuccess(statusCode, jsonResponse)
            else -> onFailure(JSONException("Unexpected type " + jsonResponse.javaClass.name), null as JSONObject)
        }
    }

    @Throws(JSONException::class)
    protected fun parseResponse(responseBody: String): Any {
        var responseBody = responseBody
        var result: Any? = null
        //trim the string to prevent start with blank, and test if the string is valid JSON, because the parser don't do this :(. If Json is not valid this will return null
        responseBody = responseBody.trim { it <= ' ' }
        if (responseBody.startsWith("{") || responseBody.startsWith("[")) {
            result = JSONTokener(responseBody).nextValue()
        }
        if (result == null) {
            result = responseBody
        }
        return result
    }

    override fun handleFailureMessage(e: Throwable, responseBody: String) {
        try {
            if (responseBody != null) {
                val jsonResponse = parseResponse(responseBody)
                if (jsonResponse is JSONObject) {
                    onFailure(e, jsonResponse)
                } else if (jsonResponse is JSONArray) {
                    onFailure(e, jsonResponse)
                } else {
                    onFailure(e, responseBody)//父类方法
                }
            } else {
                onFailure(e, "")//父类方法
            }
        } catch (ex: JSONException) {
            onFailure(e, responseBody)//父类方法
        }

    }

    companion object {
        protected const val SUCCESS_JSON_MESSAGE = 100
    }
}
