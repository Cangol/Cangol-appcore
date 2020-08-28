package mobi.cangol.mobile.appcore.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.JsonHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.http.download.DownloadHttpClient;
import mobi.cangol.mobile.http.download.DownloadResponseHandler;
import mobi.cangol.mobile.http.polling.PollingHttpClient;
import mobi.cangol.mobile.http.polling.PollingResponseHandler;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.stat.StatAgent;
import mobi.cangol.mobile.utils.FileUtils;

/**
 * Created by weixuewu on 16/4/30.
 */
public class HttpFragment extends Fragment {
    private static final String TAG="HttpFragment";
    private AsyncHttpClient mAsyncHttpClient;
    private String url="https://freevideotv.com/cmweb/api/station/sync.do";
    private String pollUrl="https://hq.sinajs.cn/list=rt_hkHSI";
    private String download="https://www1.hkexnews.hk/listedco/listconews/sehk/2020/0228/2020022800003.pdf";
    private TextView textView1;
    private Button button1,button2,button3,button4,button5;
    private ConfigService configService;
    private DownloadHttpClient downloadHttpClient;
    private String saveFile;
    private PollingHttpClient pollingHttpClient;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAsyncHttpClient = AsyncHttpClient.build(TAG);
        configService = (ConfigService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.CONFIG_SERVICE);
         saveFile=configService.getDownloadDir().getAbsolutePath()+"/aa.apk";
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
        textView1 = this.getView().findViewById(R.id.textView1);
        button1 = this.getView().findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        button2 = this.getView().findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(download,saveFile);
            }
        });


        button3 = this.getView().findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downloadHttpClient!=null){
                    downloadHttpClient.cancelRequests(HttpFragment.this,true);
                }
            }
        });
        button4 = this.getView().findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    poll(pollUrl);
            }
        });
        button5 = this.getView().findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pollingHttpClient!=null){
                    pollingHttpClient.cancelRequests(HttpFragment.this,true);
                    pollingHttpClient.shutdown();
                    pollingHttpClient=null;
                }
            }
        });
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.setText("--------------Request---------------\n");
        FileUtils.delete(saveFile);
    }

    private void printLog(String message) {
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView1.append(message);
        Log.d(message);
    }

    private void request(){
        RequestParams params=new RequestParams();
        params.put("appVersion","1.0.1");
        params.put("apiVersion","1");
        params.put("osVersion","5.0.2");
        params.put("appId","Hunao");
        params.put("sign","56d24ee613b70d9fc1d80c05e80737b8");
        params.put("deviceId","9cf64d9c047c36a9");
        params.put("platform","Android");
        params.put("channelId","google");
        String url="http://192.168.1.2:8080/Hunao/api/user/autoRegister.do";
        mAsyncHttpClient.post(getActivity(),url,params,new JsonHttpResponseHandler(){
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
                printLog("onSuccess "+response+"\n");
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                super.onFailure(e, errorResponse);
                progressDialog.dismiss();
                printLog("onFailure "+e.getMessage()+",error="+errorResponse+"\n");
            }
        });
    }
    private void download(String url,String saveFile){
        if(downloadHttpClient==null){
            downloadHttpClient=DownloadHttpClient.build("http");
        }
        downloadHttpClient.send(this,url,new DownloadResponseHandler(){
            @Override
            public void onWait() {
                super.onWait();
                printLog("onWait\n");
            }

            @Override
            public void onStart(long start, long length) {
                super.onStart(start, length);
                printLog("onStart start="+start+",length="+length+"\n");
            }

            @Override
            public void onStop(long end) {
                super.onStop(end);
                printLog("onStop end="+end+"\n");
            }

            @Override
            public void onFinish(long end) {
                super.onFinish(end);
                printLog("onFinish end="+end+"\n");
            }

            @Override
            public void onProgressUpdate(long end, int progress, int speed) {
                super.onProgressUpdate(end, progress, speed);
                printLog("onProgressUpdate progress="+progress+",speed="+speed+"\n");
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                printLog("onFailure "+content+"\n");
            }
        },0,saveFile);
    }

    private void poll(String url){
        if(pollingHttpClient==null){
            pollingHttpClient=PollingHttpClient.build("poll");
        }
        pollingHttpClient.send(TAG, url, new HashMap<String, String>(), new PollingResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                printLog("onStart\n");
            }

            @Override
            public void onPollingFinish(int execTimes, String content) {
                super.onPollingFinish(execTimes, content);
                printLog("onPollingFinish execTimes"+execTimes+",content"+content);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                printLog("onPollingFinish statusCode"+statusCode+",content"+content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                printLog("onFailure error"+error.getMessage()+",content"+content);
            }
        },10,1000L);
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
