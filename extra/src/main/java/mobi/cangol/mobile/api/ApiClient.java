package mobi.cangol.mobile.api;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.JsonHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.utils.DeviceInfo;

/**
 * API请求Cient
 */
class ApiClient {
    public enum Method {
        GET,
        POST
    }

    public static final String TAG = "ApiClient";
    public static final boolean DEBUG = true;
    public static final int MAX_THREAD = 5;
    public static final String ERROR = "-1";
    public static final String ERROR_CONNECT = "网络无法连接,请检查网络";
    private AsyncHttpClient mAsyncHttpClient;
    private Context mContext;
    private static ApiClient client = null;

    private ApiClient(Context context) {
        mContext = context;
        mAsyncHttpClient = AsyncHttpClient.build(TAG);
        mAsyncHttpClient.setThreadool(PoolManager.buildPool(TAG, MAX_THREAD));
    }

    public static ApiClient getInstance(Context context) {
        if (client == null) {
            client = new ApiClient(context);
        }
        return client;
    }

    public void cancel(Object tag, boolean mayInterruptIfRunning) {
        mAsyncHttpClient.cancelRequests(tag, mayInterruptIfRunning);
    }

    public <E, T> void execute(Object tag, Method method, final String url, HashMap<String, Object> params, String root, final OnResponse onResponse) {
        RequestParams requestParams = new RequestParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof File) {
                try {
                    requestParams.put(entry.getKey(), (File) entry.getValue());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (entry.getValue() instanceof InputStream) {
                requestParams.put(entry.getKey(), (InputStream) entry.getValue());
            } else if (entry.getValue() != null) {
                requestParams.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        if (DEBUG) Log.d(AsyncHttpClient.getUrlWithQueryString(url, requestParams));
        if (DEBUG) Log.d(requestParams.toDebugString());
        if (onResponse != null) onResponse.onStart();
        if (!DeviceInfo.isConnection(mContext)) {
            if (onResponse != null) onResponse.onFailure(ERROR, ERROR_CONNECT);
            return;
        }
        if (method == Method.GET) {
            executeGet(tag, url, requestParams, root, onResponse);
        } else {
            executePost(tag, url, requestParams, root, onResponse);
        }
    }

    private void executeGet(Object tag, final String url, RequestParams params, final String root, final OnResponse onResponse) {
        mAsyncHttpClient.get(tag, url, params, new JsonHttpResponseHandler() {
            long lastTime = 0;

            @Override
            public void onStart() {
                super.onStart();
                lastTime = System.currentTimeMillis();
            }

            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                if (DEBUG) Log.d("executeGet idle:" + (System.currentTimeMillis() - lastTime));
                if (onResponse != null)
                    onResponse.onSuccess(response);

            }

            @Override
            public void onFailure(Throwable e, String errorResponse) {
                if (DEBUG) Log.d("executeGet idle:" + (System.currentTimeMillis() - lastTime));
                if (onResponse != null)
                    onResponse.onFailure(ERROR, errorResponse);
            }


        });
    }

    private void executePost(Object tag, final String url, RequestParams params, final String root, final OnResponse onResponse) {
        mAsyncHttpClient.post(tag, url, params, new JsonHttpResponseHandler() {
            long lastTime = 0;

            @Override
            public void onStart() {
                super.onStart();
                lastTime = System.currentTimeMillis();
            }

            @Override
            public void onSuccess(JSONObject response) {
                super.onSuccess(response);
                if (DEBUG) Log.d("executePost idle:" + (System.currentTimeMillis() - lastTime));
                if (onResponse != null)
                    onResponse.onSuccess(response);

            }

            @Override
            public void onFailure(Throwable e, String errorResponse) {
                if (DEBUG) Log.d("executePost idle:" + (System.currentTimeMillis() - lastTime));
                if (onResponse != null)
                    onResponse.onFailure(ERROR, errorResponse);
            }

        });
    }

    public interface OnResponse {
        void onStart();

        void onSuccess(JSONObject response);

        void onFailure(String code, String response);
    }
}
