package mobi.cangol.mobile.appcore.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.JsonHttpResponseHandler;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.PoolManager;

/**
 * Created by weixuewu on 16/4/30.
 */
public class HttpFragment extends Fragment {
    private static final String TAG="HttpFragment";
    private AsyncHttpClient mAsyncHttpClient;
    private String url="http://www.cangol.mobi/cmweb/api/station/sync.do";
    private TextView textView1;
    private Button button1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAsyncHttpClient = AsyncHttpClient.build(TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_http, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    private void initViews() {
        textView1 = (TextView) this.getView().findViewById(R.id.textView1);
        button1 = (Button) this.getView().findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------Request---------------\n");
    }

    private void printLog(String message) {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.append(message);
        Log.d(message);
    }
    private void request(){

        mAsyncHttpClient.get(getActivity(),url,null,new JsonHttpResponseHandler(){
            ProgressDialog progressDialog;
            @Override
            public void onStart() {
                super.onStart();
                progressDialog=ProgressDialog.show(getActivity(),null,"loading...");
                printLog("onStart\n");
            }

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                super.onSuccess(statusCode, response);
                progressDialog.dismiss();
                printLog("onSuccess "+response);
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                super.onFailure(e, errorResponse);
                progressDialog.dismiss();
                printLog("onFailure "+errorResponse);
            }
        });
    }
}
