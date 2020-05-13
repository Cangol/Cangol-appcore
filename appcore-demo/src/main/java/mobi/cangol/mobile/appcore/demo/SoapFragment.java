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

import java.util.HashMap;

import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.soap.SoapClient;
import mobi.cangol.mobile.soap.SoapResponseHandler;
import mobi.cangol.mobile.stat.StatAgent;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * Created by weixuewu on 16/4/30.
 */
public class SoapFragment extends Fragment {

    private static final String TAG="HttpFragment";
    private SoapClient mSoapClient;
    private TextView textView1;
    private Button button1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSoapClient = new SoapClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_soap, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    private void initViews() {
        textView1 = this.getView().findViewById(R.id.textView1);
        button1 = this.getView().findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request2();
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
        String url="http://www.webxml.com.cn/WebServices/WeatherWebService.asmx";
        String namespace="http://WebXml.com.cn/";
        String action="getSupportCity";
        HashMap<String, String> params=new HashMap<String, String>();
        params.put("byProvinceName","河北");
        mSoapClient.send(getActivity(),url,namespace,action,params,new SoapResponseHandler(){
            ProgressDialog progressDialog;
                    @Override
                    public void onStart() {
                        super.onStart();
                        progressDialog=ProgressDialog.show(getActivity(),null,"loading...");
                        printLog("onStart\n");
                    }

                    @Override
                    public void onFailure(String error) {
                        super.onFailure(error);
                        progressDialog.dismiss();
                        printLog("onFailure "+error);
                    }

                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        progressDialog.dismiss();
                        printLog("onSuccess "+content);
                    }
                }
        );
    }
    private void request2(){
        String url="http://www.webxml.com.cn/WebServices/ChinaTVprogramWebService.asmx";
        String namespace="http://WebXml.com.cn/";
        String action="getTVprogramString";
        HashMap<String, String> params=new HashMap<String, String>();
        params.put("theTVchannelID","606");
        params.put("theDate", TimeUtils.getCurrentDate());
        mSoapClient.send(getActivity(),url,namespace,action,params,new SoapResponseHandler(){
                    ProgressDialog progressDialog;
                    @Override
                    public void onStart() {
                        super.onStart();
                        progressDialog=ProgressDialog.show(getActivity(),null,"loading...");
                        printLog("onStart\n");
                    }

                    @Override
                    public void onFailure(String error) {
                        super.onFailure(error);
                        progressDialog.dismiss();
                        printLog("onFailure "+error);
                    }

                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        progressDialog.dismiss();
                        printLog("onSuccess "+content);
                    }
                }
        );
    }
    @Override
    public void onPause() {
        super.onPause();
        StatAgent.getInstance().onFragmentPause(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatAgent.getInstance().onFragmentResume(TAG);
    }
}
