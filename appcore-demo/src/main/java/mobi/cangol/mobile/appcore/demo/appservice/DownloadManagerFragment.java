package mobi.cangol.mobile.appcore.demo.appservice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.download.DownloadExecutor;
import mobi.cangol.mobile.service.download.DownloadManager;
import mobi.cangol.mobile.service.download.DownloadNotification;
import mobi.cangol.mobile.service.download.DownloadResource;
import mobi.cangol.mobile.service.download.DownloadStatusListener;

/**
 * Created by weixuewu on 16/4/30.
 */
public class DownloadManagerFragment extends Fragment {
    private String url="http://180.153.105.145/dd.myapp.com/16891/8E5A9885970F76080F8445C652DE347C.apk?mkey=5715c34fc20a8141&f=d511&fsname=com.tencent.mobileqq_6.3.1_350.apk&p=.apk";

    private DownloadManager downloadManager;
    private TextView textView1;
    private Button button1, button2, button3, button4;
    private AppDownloadExecutor downloadExecutor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadManager = (DownloadManager) ((CoreApplication) this.getActivity().getApplicationContext()).getAppService(AppService.DOWNLOAD_MANAGER);
        downloadManager.setDebug(true);
        downloadManager.registerExecutor("app", AppDownloadExecutor.class, 5);
        downloadExecutor = (AppDownloadExecutor) downloadManager.getDownloadExecutor("app");
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
        updateViews();
    }

    private DownloadResource downloadResource;

    private void initViews() {
        textView1 = (TextView) this.getView().findViewById(R.id.textView1);
        button1 = (Button) this.getView().findViewById(R.id.button1);
        button2 = (Button) this.getView().findViewById(R.id.button2);
        button3 = (Button) this.getView().findViewById(R.id.button3);
        button4 = (Button) this.getView().findViewById(R.id.button4);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.add(downloadExecutor.getResource(new App("qq", url)));
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.start(downloadResource);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.stop(downloadResource);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadExecutor.remove(downloadResource);
            }
        });
        downloadExecutor.registerDownloadStatusListener(new DownloadStatusListener() {
            @Override
            public void onStatusChange(DownloadResource resource, int type) {
                updateViews();
            }
        });
        textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void updateViews() {
        textView1.setText("--------------Download---------------");
        if (downloadResource != null) {
            textView1.append("\ngetFileName=" + downloadResource.getFileName());
            textView1.append("\ngetLocalPath=" + downloadResource.getLocalPath());
            textView1.append("\ngetStatus=" + downloadResource.getStatus());
            textView1.append("\ngetCompleteSize=" + downloadResource.getCompleteSize() + "/" + downloadResource.getFileLength());
            textView1.append("\ngetProgress=" + downloadResource.getProgress());
            textView1.append("\ngetSpeed=" + downloadResource.getSpeed());
        }
    }
}

class AppDownloadExecutor extends DownloadExecutor<App> {


    public AppDownloadExecutor(String name) {
        super(name);
    }

    @Override
    protected void add(App app) {
        this.add(getResource(app));
    }

    @Override
    protected DownloadResource getResource(App app) {
        return new DownloadResource(app.url, app.name + ".apk");
    }

    @Override
    protected App getDownloadModel(DownloadResource resource) {
        return new App(resource.getUrl(), resource.getFileName().replace(".apk", ""));
    }

    @Override
    protected DownloadResource readResource(String filePath) {
        return null;
    }

    @Override
    protected void writeResource(DownloadResource resource) {

    }

    @Override
    public ArrayList<DownloadResource> scanResource() {
        return new ArrayList<DownloadResource>();
    }

    @Override
    public DownloadNotification notification(Context context, DownloadResource resource) {
        Uri uri = Uri.fromFile(new File(resource.getLocalPath()));
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
}
