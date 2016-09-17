

package mobi.cangol.mobile.http1;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class BinaryHttpResponseHandler extends AsyncHttpResponseHandler {
    // Allow images by default
    private String[] mAllowedContentTypes = new String[]{
            "application/zip",
            "application/x-zip-compressed",
            "application/octet-stream"
    };

    /**
     * Creates a new BinaryHttpResponseHandler
     */
    public BinaryHttpResponseHandler() {
        super();
    }

    /**
     * Creates a new BinaryHttpResponseHandler with context
     */
    public BinaryHttpResponseHandler(Context context) {
        super(context);
    }

    /**
     * Creates a new BinaryHttpResponseHandler, and overrides the default allowed
     * content types with passed String array (hopefully) of content types.
     */
    public BinaryHttpResponseHandler(String[] allowedContentTypes) {
        this();
        mAllowedContentTypes = allowedContentTypes;
    }

    /**
     * Creates a new BinaryHttpResponseHandler, and overrides the default allowed
     * content types with passed String array (hopefully) of content types.
     */
    public BinaryHttpResponseHandler(Context context, String[] allowedContentTypes) {
        this(context);
        mAllowedContentTypes = allowedContentTypes;
    }


    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param binaryData the body of the HTTP response from the server
     */
    public void onSuccess(byte[] binaryData) {
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     *
     * @param statusCode the status code of the response
     * @param binaryData the body of the HTTP response from the server
     */
    public void onSuccess(int statusCode, byte[] binaryData) {
        onSuccess(binaryData);
    }

    /**
     * Fired when a request fails to complete, override to handle in your own code
     *
     * @param error      the underlying cause of the failure
     * @param binaryData the response body, if any
     * @deprecated
     */
    public void onFailure(Throwable error, byte[] binaryData) {
        // By default, call the deprecated onFailure(Throwable) for compatibility
        onFailure(error);
    }


    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    protected void sendSuccessMessage(int statusCode, byte[] responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, responseBody}));
    }

    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    protected void handleSuccessMessage(int statusCode, byte[] responseBody) {
        onSuccess(statusCode, responseBody);
    }

    protected void handleFailureMessage(Throwable e, byte[] responseBody) {
        onFailure(e, responseBody);
    }

    protected void handleFailureMessage(Throwable e, String responseBody) {
        byte[] response = null;
        if (responseBody != null) {
            try {
                onFailure(e, responseBody.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e1) {
                Log.d("UnsupportedEncoding", e.getMessage());
            }
        } else {
            onFailure(e, response);
        }
    }

    // Methods which emulate android's Handler and Message methods
    protected void handleMessage(Message msg) {
        Object[] response;
        switch (msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[]) msg.obj;
                handleSuccessMessage(((Integer) response[0]).intValue(), (byte[]) response[1]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                if (response[1] instanceof byte[]) {
                    handleFailureMessage((Throwable) response[0], (byte[]) response[1]);
                } else {
                    handleFailureMessage((Throwable) response[0], (String) response[1]);
                }
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
    void sendResponseMessage(Response response) {
        Log.d(">>", "sendResponseMessage=");
        ResponseBody responseBody=response.body();
        if(response.isSuccessful()){
            String contentType=response.body().contentType().toString();
            boolean foundAllowedContentType = false;
            for (String anAllowedContentType : mAllowedContentTypes) {
                if (anAllowedContentType.equals(contentType)) {
                    foundAllowedContentType = true;
                }
            }
            if (!foundAllowedContentType) {
                sendFailureMessage(new IOException("Content-Type not allowed! "+contentType), response.message());
                return;
            }
            try {
                sendSuccessMessage(response.code(), responseBody.bytes());
            } catch (IOException e) {
                sendFailureMessage(e, (byte[]) null);
            }
        }else{
            sendFailureMessage(new IOException("code="+response.code()), response.message());
        }
    }
}