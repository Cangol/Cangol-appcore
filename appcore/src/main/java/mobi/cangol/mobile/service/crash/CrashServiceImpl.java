/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.service.crash;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.Task;
import mobi.cangol.mobile.http.AsyncHttpClient;
import mobi.cangol.mobile.http.AsyncHttpResponseHandler;
import mobi.cangol.mobile.http.RequestParams;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.PoolManager;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.service.session.SessionService;
import mobi.cangol.mobile.utils.DeviceInfo;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.Object2FileUtils;
import mobi.cangol.mobile.utils.TimeUtils;

/**
 * @author Cangol
 */
@Service("CrashService")
class CrashServiceImpl implements CrashService, UncaughtExceptionHandler {
    private final static String TAG = "CrashService";
    private final static String _CRASH = ".crash";
    private boolean debug = true;
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
    private CoreApplication mApplication;
    private SessionService mSessionService;
    private ServiceProperty mServiceProperty = null;
    private AsyncHttpClient asyncHttpClient;
    private String mUrl;
    private Map<String, String> mParams;
    private String mCrashDir;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCreate(Application context) {
        mApplication = (CoreApplication) context;
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mSessionService = (SessionService) mApplication.getAppService(AppService.SESSION_SERVICE);
        ConfigService configService = (ConfigService) mApplication.getAppService(AppService.CONFIG_SERVICE);
        mCrashDir =configService.getTempDir().getAbsolutePath()+File.separator+ "crash";

        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        FileUtils.newFolder(mCrashDir);
        StrictMode.setThreadPolicy(oldPolicy);
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
        PoolManager.buildPool(mServiceProperty.getString(CRASHSERVICE_THREADPOOL_NAME), mServiceProperty.getInt(CRASHSERVICE_THREAD_MAX));
        asyncHttpClient = AsyncHttpClient.build(mServiceProperty.getString(CRASHSERVICE_THREADPOOL_NAME));
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        asyncHttpClient.cancelRequests(mApplication, true);
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        ServiceProperty sp = new ServiceProperty(TAG);
        sp.putString(CRASHSERVICE_THREADPOOL_NAME, TAG);
        sp.putInt(CRASHSERVICE_THREAD_MAX, 1);
        sp.putString(CRASHSERVICE_REPORT_URL, "");
        sp.putString(CRASHSERVICE_REPORT_ERROR, "error");
        sp.putString(CRASHSERVICE_REPORT_POSITION, "position");
        sp.putString(CRASHSERVICE_REPORT_TIMESTAMP, "timestamp");
        sp.putString(CRASHSERVICE_REPORT_CONTEXT, "content");
        sp.putString(CRASHSERVICE_REPORT_FATAL, "fatal");
        return sp;
    }

    @Override
    public void setReport(String url, Map<String, String> params) {
        this.mUrl = url;
        this.mParams = params;
    }

    private void report(final ReportError report) {
        if (debug) {
            Log.d(TAG, "report .crash " + report.path);
        }

        RequestParams params = this.mParams == null ? new RequestParams() : new RequestParams(this.mParams);
        params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_ERROR), report.error);
        params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_POSITION), report.position);
        params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_CONTEXT), report.context);
        params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_TIMESTAMP), report.timestamp);
        params.put(mServiceProperty.getString(CRASHSERVICE_REPORT_FATAL), report.fatal);
        asyncHttpClient.post(mApplication, mUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                mApplication.post(new Runnable() {
                    @Override
                    public void run() {
                        FileUtils.delFile(report.path);
                    }
                });
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

        });
    }

    protected String throwableToString(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        pw.close();
        String error = writer.toString();
        return error;
    }

    protected ReportError makeReportError(Throwable ex) {
        String timestamp = TimeUtils.getCurrentTime();
        String filename = timestamp.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
        ReportError error = new ReportError();
        error.error = ex.toString();
        error.position = ex.getStackTrace()[0].toString();
        error.context = throwableToString(ex);
        error.timestamp = timestamp;
        error.fatal = "0";
        error.path = mCrashDir + File.separator + filename + _CRASH;
        return error;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Thread.setDefaultUncaughtExceptionHandler(mDefaultExceptionHandler);
        ReportError error = makeReportError(ex);
        Log.e("AndroidRuntime", error.context);
        if (debug) {
            Log.d(TAG, "save .crash " + error.path);
        }
        Object2FileUtils.writeObject(error, error.path);
        //System.gc();
        mSessionService.saveString("exitCode", "1");
        mSessionService.saveString("exitVersion", DeviceInfo.getAppVersion(mApplication));
        //0 正常推退出  1异常退出
        System.exit(0);
    }

    @Override
    public void report(final CrashReportListener crashReportListener) {
        mApplication.post(new Task<List<ReportError>>() {
            @Override
            public List<ReportError> call() {
                List<File> files = FileUtils.searchBySuffix(new File(mCrashDir), null, _CRASH);
                //System.gc();
                List<ReportError> reports = new ArrayList<ReportError>();
                Object obj = null;
                for (final File file : files) {
                    obj = FileUtils.readObject(file);
                    if (obj != null) {
                        reports.add((ReportError) obj);
                    } else {
                        FileUtils.delFile(file.getAbsolutePath());
                    }
                }
                return reports;
            }

            @Override
            public void result(List<ReportError> result) {
                for (final ReportError errorReport : result) {
                    if (!TextUtils.isEmpty(mUrl)) {
                        report(errorReport);
                    }
                    if (crashReportListener != null) {
                        crashReportListener.report(errorReport.path,
                                errorReport.error,
                                errorReport.position,
                                errorReport.context,
                                errorReport.timestamp,
                                errorReport.fatal);
                    }
                }
            }
        });
    }


    static class ReportError implements java.io.Serializable {
        private static final long serialVersionUID = 0L;
        String error;
        String position;
        String context;
        String timestamp;
        String fatal;
        String path;

        ReportError() {}
    }
}
