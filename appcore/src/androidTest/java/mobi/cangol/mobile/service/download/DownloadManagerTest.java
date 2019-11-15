package mobi.cangol.mobile.service.download;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.test.ApplicationTestCase;

import java.io.File;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.utils.FileUtils;

/**
 * Created by weixuewu on 16/6/11.
 */
public class DownloadManagerTest extends ApplicationTestCase<CoreApplication> {
    private static final String TAG = "DownloadManagerTest";
    private CoreApplication coreApplication;
    private DownloadManager downloadManager;
    private AppDownloadExecutor downloadExecutor;
    public DownloadManagerTest() {
        super(CoreApplication.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        coreApplication = getApplication();
        downloadManager = (DownloadManager) coreApplication.getAppService(AppService.Companion.getDOWNLOAD_MANAGER());
    }

    public void testGetDownloadExecutor() {
        downloadManager.getDownloadExecutor(TAG);
    }

    public void testRegisterExecutor() {
        //downloadManager.registerExecutor(TAG,new Dow);
        downloadManager.registerExecutor("app", AppDownloadExecutor.class, 2);
        downloadExecutor = (AppDownloadExecutor) downloadManager.getDownloadExecutor("app");
        downloadExecutor.registerDownloadStatusListener(new DownloadStatusListener() {
            @Override
            public void onStatusChange(DownloadResource resource, int type) {

            }
        });
    }

    public void testRecoverAllAllDownloadExecutor() {
        downloadManager.recoverAllAllDownloadExecutor();
    }

    public void testInterruptAllDownloadExecutor() {
        downloadManager.interruptAllDownloadExecutor();
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