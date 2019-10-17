package mobi.cangol.mobile.appcore.demo.appservice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.appcore.demo.R;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.service.download.Download;
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
    private static final String TAG = "DownloadManagerFragment";
    private String url2="http://music-hotpot.oss-cn-hongkong.aliyuncs.com/songs/803263126016617416171574.mp3";
    private String url1="http://music-hotpot.oss-cn-hongkong.aliyuncs.com/songs/626841935812471620885564.mp3";
    private ConfigService configService;
    private DownloadManager downloadManager;
    private AppDownloadExecutor downloadExecutor;
    private DownloadResource downloadResource1,downloadResource2;
    private TextView textView1,textView2;
    private Button button11, button12, button13, button14;
    private Button button21, button22, button23, button24;
    private CoreApplication application;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application=((CoreApplication) this.getActivity().getApplicationContext());
        configService = (ConfigService) application.getAppService(AppService.CONFIG_SERVICE);
        downloadManager = (DownloadManager) application.getAppService(AppService.DOWNLOAD_MANAGER);
        downloadManager.setDebug(true);
        downloadManager.registerExecutor("app", AppDownloadExecutor.class, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_download, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    @Override
    public void onPause() {
        super.onPause();
        //StatAgent.getInstance().onFragmentPause(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        //StatAgent.getInstance().onFragmentResume(TAG);
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
                FileUtils.delete(downloadResource1.getConfFile());
                FileUtils.delete(downloadResource1.getSourceFile());
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
                //FileUtils.delete(downloadResource2.getConfFile());
                //FileUtils.delete(downloadResource2.getSourceFile());
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

        downloadExecutor = (AppDownloadExecutor) downloadManager.getDownloadExecutor("app");
        downloadExecutor.registerDownloadStatusListener(new DownloadStatusListener() {
            @Override
            public void onStatusChange(DownloadResource resource, int type) {
                Log.d(TAG,"onStatusChange "+type);
                if(resource!=null){
                    if(resource.getFileName().contains("QQ")){
                        updateViews(textView1,resource);
                    }else{
                        updateViews(textView2,resource);
                    }
                    if(resource.getStatus()== Download.STATUS_FINISH&&type==Download.ACTION_DOWNLOAD_FINISH){
                        copyToDownloads(resource.getFileName(),resource.getSourceFile());
                    }
                }
            }
        });

    }
    private void copyToDownloads(final String filename,final String filepath){
        Log.d(TAG," copyToDownloads "+filepath );
        final String downloadDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
//        FileUtils.copyFile(filepath,downloadDir+"/"+filename);
//        scanFileAsync(downloadDir+"/"+filename);


//        application.post(new Task() {
//            @Override
//            public Object call() {
//                Log.d(TAG,"Task copyFile "+filepath+"=>"+downloadDir+"/"+filename);
//                FileUtils.copyFile(filepath,downloadDir+"/"+filename);
//                return  downloadDir+"/"+filename;
//            }
//
//            @Override
//            public void result(Object o) {
//                scanFileAsync(downloadDir+"/"+filename);
//            }
//        });
        application.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Runnable copyFile "+filepath+"=>"+downloadDir+"/"+filename);
                FileUtils.copyFile(filepath,downloadDir+"/"+filename);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scanFileAsync(downloadDir+"/"+filename);
                    }
                });
            }
        });
    }
    public void scanFileAsync(String filePath) {
        File file=new File(filePath);
        Log.d(TAG,"scanFileAsync "+file.exists()+","+file.getAbsolutePath());
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        getContext().sendBroadcast(mediaScanIntent);
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
        return new DownloadResource(this.getDownloadDir().getAbsolutePath(),app.url, app.name + ".mp3");
    }

    @Override
    protected App getDownloadModel(DownloadResource resource) {
        return new App(resource.getFileName().replace(".mp3", ""),resource.getUrl());
    }

    @Override
    public DownloadNotification notification(Context context, DownloadResource resource) {
        Log.d("resource: "+resource.getSourceFile());
        File file=new File(resource.getSourceFile().replace(".tmp", ""));
        Uri uri = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            final String authority = context.getPackageName() + ".fileprovider";
            uri = FileProvider.getUriForFile(context, authority, file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "audio/*");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return new DownloadNotification(context, resource.getFileName(), resource.getLocalPath(), intent);
    }

}

class App {
    String url;
    String name;

    public App() {
    }

    public App( String name,String url) {
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