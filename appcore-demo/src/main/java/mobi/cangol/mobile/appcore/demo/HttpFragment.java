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

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.url.DownloadHttpClient;
import mobi.cangol.mobile.appcore.demo.url.DownloadResponseHandler;
import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.JsonHttpResponseHandler;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.utils.FileUtils;

/**
 * Created by weixuewu on 16/4/30.
 */
public class HttpFragment extends Fragment {
    private static final String TAG="HttpFragment";
    private AsyncHttpClient mAsyncHttpClient;
    private String url="http://www.cangol.mobi/cmweb/api/station/sync.do";
    private String download="http://ddmyapp.kw.tc.qq.com/16891/1AF80D6B4F5B3365D1A4B755BBC92FD2.apk?mkey=577cc4c4c20a8141&f=5401&c=0&fsname=com.tencent.mm_6.3.22_821.apk&p=.apk";
    private TextView textView1;
    private Button button1,button2,button3;
    private ConfigService configService;
    private DownloadHttpClient downloadHttpClient;
    private String saveFile;
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
        textView1 = (TextView) this.getView().findViewById(R.id.textView1);
        button1 = (Button) this.getView().findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

        button2 = (Button) this.getView().findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(download,saveFile);
            }
        });


        button3 = (Button) this.getView().findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downloadHttpClient!=null){
                    downloadHttpClient.cancelRequests(HttpFragment.this,true);
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
}
