package mobi.cangol.mobile.appcore.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.JsonHttpResponseHandler;
import mobi.cangol.mobile.service.PoolManager;

/**
 * Created by weixuewu on 16/4/30.
 */
public class HttpFragment extends Fragment {
    private static final String TAG="HttpFragment";
    private AsyncHttpClient mAsyncHttpClient;
    private String url="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAsyncHttpClient = AsyncHttpClient.build(TAG);
        mAsyncHttpClient.setThreadool(PoolManager.buildPool(TAG, 5));
        mAsyncHttpClient.setUserAgent("android");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void request(){
        mAsyncHttpClient.get(getActivity(),url,new JsonHttpResponseHandler(){

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, JSONArray response) {
                super.onSuccess(statusCode, response);
            }
        });
    }
}
