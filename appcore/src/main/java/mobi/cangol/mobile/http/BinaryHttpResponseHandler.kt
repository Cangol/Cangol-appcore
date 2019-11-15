package mobi.cangol.mobile.http

import android.content.Context
import android.os.Message
import android.util.Log
import okhttp3.Response
import java.io.IOException
import java.io.UnsupportedEncodingException

open class BinaryHttpResponseHandler : AsyncHttpResponseHandler {
    // Allow images by default
    private var mAllowedContentTypes = arrayOf("application/zip", "application/x-zip-compressed", "application/octet-stream")

    /**
     * Creates a new BinaryHttpResponseHandler
     */
    constructor() : super() {}

    /**
     * Creates a new BinaryHttpResponseHandler with context
     */
    constructor(context: Context) : super(context) {}

    /**
     * Creates a new BinaryHttpResponseHandler, and overrides the default allowed
     * content types with passed String array (hopefully) of content types.
     */
    constructor(allowedContentTypes: Array<String>) : this() {
        mAllowedContentTypes = allowedContentTypes
    }

    /**
     * Creates a new BinaryHttpResponseHandler, and overrides the default allowed
     * content types with passed String array (hopefully) of content types.
     */
    constructor(context: Context, allowedContentTypes: Array<String>) : this(context) {
        mAllowedContentTypes = allowedContentTypes
    }


    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param binaryData the body of the HTTP response from the server
     */
    fun onSuccess(binaryData: ByteArray) {
        //do nothings
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param statusCode the status code of the response
     * @param binaryData the body of the HTTP response from the server
     */
    fun onSuccess(statusCode: Int, binaryData: ByteArray) {
        onSuccess(binaryData)
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param error      the underlying cause of the failure
     * @param binaryData the response body, if any
     */
    fun onFailure(error: Throwable, binaryData: ByteArray) {
        // By default, call the deprecated onFailure(Throwable) for compatibility
        onFailure(error)
    }


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    protected fun sendSuccessMessage(statusCode: Int, responseBody: ByteArray) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, arrayOf<Any>(statusCode, responseBody)))
    }

    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    protected fun handleSuccessMessage(statusCode: Int, responseBody: ByteArray) {
        onSuccess(statusCode, responseBody)
    }

    protected fun handleFailureMessage(e: Throwable, responseBody: ByteArray) {
        onFailure(e, responseBody)
    }

    override fun handleFailureMessage(e: Throwable, responseBody: String) {
        if (responseBody != null) {
            try {
                onFailure(e, responseBody.toByteArray(charset("utf-8")))
            } catch (e1: UnsupportedEncodingException) {
                Log.d("UnsupportedEncoding", e.message)
            }

        } else {
            onFailure(e, null as ByteArray)
        }
    }

    // Methods which emulate android's Handler and Message methods
    override fun handleMessage(msg: Message) {
        val response: Array<Any>
        when (msg.what) {
            SUCCESS_MESSAGE -> {
                response = msg.obj as Array<Any>
                handleSuccessMessage((response[0] as Int).toInt(), response[1] as ByteArray)
            }
            FAILURE_MESSAGE -> {
                response = msg.obj as Array<Any>
                if (response[1] is ByteArray) {
                    handleFailureMessage(response[0] as Throwable, response[1] as ByteArray)
                } else {
                    handleFailureMessage(response[0] as Throwable, response[1] as String)
                }
            }
            else -> super.handleMessage(msg)
        }
    }

    internal override fun sendResponseMessage(response: Response) {
        Log.d(">>", "sendResponseMessage=")
        val responseBody = response.body()
        if (response.isSuccessful) {
            val contentType = response.body()!!.contentType()!!.toString()
            var foundAllowedContentType = false
            for (anAllowedContentType in mAllowedContentTypes) {
                if (anAllowedContentType == contentType) {
                    foundAllowedContentType = true
                }
            }
            if (!foundAllowedContentType) {
                sendFailureMessage(IOException("Content-Type not allowed! $contentType"), response.message())
                return
            }
            try {
                sendSuccessMessage(response.code(), responseBody!!.bytes())
            } catch (e: IOException) {
                sendFailureMessage(e, null)
            }

        } else {
            sendFailureMessage(IOException("code=" + response.code()), response.message())
        }
    }
}