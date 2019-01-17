package mobi.cangol.mobile.appcore.demo.appservice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.service.download.DownloadExecutor;
import mobi.cangol.mobile.service.download.DownloadManager;
import mobi.cangol.mobile.service.download.DownloadNotification;
import mobi.cangol.mobile.service.download.DownloadResource;
import mobi.cangol.mobile.service.download.DownloadStatusListener;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.StringUtils;

/**
 * Created by weixuewu on 16/4/30.
 */
public class DownloadManagerFragment extends Fragment {
    private String url1="http://180.153.105.145/dd.myapp.com/16891/8E5A9885970F76080F8445C652DE347C.apk?mkey=5715c34fc20a8141&f=d511&fsname=com.tencent.mobileqq_6.3.1_350.apk&p=.apk";
    private String url2="http://ddmyapp.kw.tc.qq.com/16891/1AF80D6B4F5B3365D1A4B755BBC92FD2.apk?mkey=577cc4c4c20a8141&f=5401&c=0&fsname=com.tencent.mm_6.3.22_821.apk&p=.apk";
    private ConfigService configService;
    private DownloadManager downloadManager;
    private AppDownloadExecutor downloadExecutor;
    private DownloadResource downloadResource1,downloadResource2;
    private TextView textView1,textView2;
    private Button button11, button12, button13, button14;
    private Button button21, button22, button23, button24;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configService = (ConfigService) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.CONFIG_SERVICE);
        downloadManager = (DownloadManager) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.DOWNLOAD_MANAGER);
        downloadManager.setDebug(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manager_download, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        textView1 = this.getView().findViewById(R.id.textView1);
        button11 = this.getView().findViewById(R.id.button11);
        button12 = this.getView().findViewById(R.id.button12);
        button13 = this.getView().findViewById(R.id.button13);
        button14 = this.getView().findViewById(R.id.button14);

        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App app=new App("QQ", url1);
                downloadResource1=downloadExecutor.getDownloadResource(app);
                downloadExecutor.add(downloadResource1);
            }
        });
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.start(downloadResource1);
            }
        });
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.stop(downloadResource1);
            }
        });
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.remove(downloadResource1);
            }
        });
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());


        textView2 = this.getView().findViewById(R.id.textView2);
        button21 = this.getView().findViewById(R.id.button21);
        button22 = this.getView().findViewById(R.id.button22);
        button23 = this.getView().findViewById(R.id.button23);
        button24 = this.getView().findViewById(R.id.button24);

        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App app=new App("Wechat", url2);
                downloadResource2=downloadExecutor.getDownloadResource(app);
                downloadExecutor.add(downloadResource2);
            }
        });
        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.start(downloadResource2);
            }
        });
        button23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.stop(downloadResource2);
            }
        });
        button24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.remove(downloadResource2);
            }
        });
        textView2.setMovementMethod(ScrollingMovementMethod.getInstance());


        //String path="/sdcard/appcore";
       // boolean result=configService.setCustomAppDir(path);
        //Log.d("setCustomAppDir " + (result ? "success" : "fail")+" "  + path);

        downloadManager.registerExecutor("app", AppDownloadExecutor.class, 2);
        downloadExecutor = (AppDownloadExecutor) downloadManager.getDownloadExecutor("app");
        downloadExecutor.registerDownloadStatusListener(new DownloadStatusListener() {
            @Override
            public void onStatusChange(DownloadResource resource, int type) {
                if(resource!=null){
                    if(resource.getFileName().contains("QQ")){
                        updateViews(textView1,resource);
                    }else{
                        updateViews(textView2,resource);
                    }
                }
            }
        });
    }

    private void updateViews(TextView textView,DownloadResource downloadResource) {
        textView.setText("--------------Download---------------");
        if (downloadResource != null) {
            textView.append("\ngetFileName=" + downloadResource.getFileName());
            textView.append("\ngetLocalPath=" + downloadResource.getLocalPath());
            textView.append("\ngetStatus=" + downloadResource.getStatus());
            textView.append("\ngetCompleteSize=" + downloadResource.getCompleteSize() + "/" + downloadResource.getFileLength());
            textView.append("\ngetProgress=" + downloadResource.getProgress());
            textView.append("\ngetSpeed=" + StringUtils.formatSpeed(downloadResource.getSpeed()));
        }else{
            textView.append("\ndownloadResource=" + downloadResource);
        }
    }
}

class AppDownloadExecutor extends DownloadExecutor<App> {


    public AppDownloadExecutor(String name) {
        super(name);
    }

    @Override
    protected DownloadResource getDownloadResource(App app) {
        DownloadResource downloadResource=new DownloadResource(this.getDownloadDir().getAbsolutePath(),app.url, app.name + ".apk");
        FileUtils.delete(downloadResource.getConfFile());
        FileUtils.delete(downloadResource.getSourceFile());
        return downloadResource;
    }

    @Override
    protected App getDownloadModel(DownloadResource resource) {
        return new App(resource.getUrl(), resource.getFileName().replace(".apk", ""));
    }

    @Override
    public DownloadNotification notification(Context context, DownloadResource resource) {
        Uri uri = Uri.fromFile(new File(resource.getSourceFile().replace(".tmp","")));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return new DownloadNotification(context, resource.getFileName(), resource.getLocalPath(), intent);
    }

}

class App {
    String url;
    String name;

    public App() {
    }

    public App(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        App app = (App) o;

        return url != null ? url.equals(app.url) : app.url == null;

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}