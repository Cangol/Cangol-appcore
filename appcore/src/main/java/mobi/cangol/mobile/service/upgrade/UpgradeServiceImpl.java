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
package mobi.cangol.mobile.service.upgrade;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.http.download.DownloadHttpClient;
import mobi.cangol.mobile.http.download.DownloadResponseHandler;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.service.download.DownloadNotification;
import mobi.cangol.mobile.utils.AppUtils;

/**
 * @author Cangol
 */
@Service("UpgradeService")
class UpgradeServiceImpl implements UpgradeService {
    private static final  String TAG = "UpgradeService";
    private boolean debug = false;
    private Application mContext = null;
    private ServiceProperty mServiceProperty = null;
    private ConfigService mConfigService;
    private List<Integer> mIds = new ArrayList<>();
    private Map<String, OnUpgradeListener> mOnUpgradeListeners;
    private DownloadHttpClient mDownloadHttpClient;

    @Override
    public void onCreate(Application context) {
        mContext = context;
        mConfigService = (ConfigService) ((CoreApplication) mContext).getAppService(AppService.CONFIG_SERVICE);
        mOnUpgradeListeners = new HashMap<>();
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        this.mServiceProperty = serviceProperty;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        if (debug) Log.d("onDestory");
        if (mDownloadHttpClient != null)
            mDownloadHttpClient.cancelAll();
        final NotificationManager notificationManager = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        for (final Integer id : mIds) {
            notificationManager.cancel(id);
            if (debug) Log.d("notification cancel " + id);
        }
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        return new ServiceProperty(TAG);
    }

    @Override
    public void setDebug(boolean mDebug) {
        this.debug = mDebug;
    }

    @Override
    public void upgrade(final String filename, String url, final boolean notification) {
        upgrade(filename, url, notification, UpgradeType.APK, false, true);
    }

    @Override
    public void upgrade(String filename, String url, boolean notification, boolean install) {
        upgrade(filename, url, notification, UpgradeType.APK, install, true);
    }

    @Override
    public void upgrade(String filename, String url, boolean notification, boolean install, boolean safe) {
        upgrade(filename, url, notification, UpgradeType.APK, install, safe);
    }

    private void upgrade(final String filename, String url,  final boolean notification, final UpgradeType upgradeType, final boolean install, final boolean safe) {
        if(notification&&!NotificationManagerCompat.from(mContext).areNotificationsEnabled()){
            String error="NotificationsEnabled=false notification not show! ";
            Log.e(TAG,error);
            notifyUpgradeFailure(filename, error);
            return;
        }

        final String savePath = mConfigService.getUpgradeDir() + File.separator + filename;
        final File saveFile = new File(savePath);
        if (debug) Log.d("upgrade savePath:" + savePath);
        if (saveFile.exists()) {
            final boolean result=saveFile.delete();
            if(!result){
                Log.d("delete oldFile fail:" + savePath);
                notifyUpgradeFailure(savePath, "delete oldFile fail:");
                return;
            }
        } else {
            try {
                final boolean result=saveFile.createNewFile();
                if(!result)Log.d("createNewFile  fail:" + savePath);
            } catch (IOException e) {
                Log.e(e.getMessage());
            }
        }
        DownloadNotification downloadNotification =null;
        if (notification) {
            Intent intent=null;
            try {
                intent=createFinishIntent(savePath, upgradeType);
            }catch (Exception e){
                Log.e(TAG,"createFinishIntent fail!",e);
                return;
            }
            if(intent==null){
                Log.e(TAG,"createFinishIntent fail!");
                return;
            }
            downloadNotification = new DownloadNotification(mContext, filename, savePath,intent );
            mIds.add(downloadNotification.getId());
        }
        mDownloadHttpClient = DownloadHttpClient.build(TAG, safe);

        final DownloadNotification finalDownloadNotification = downloadNotification;
        mDownloadHttpClient.send(filename, url, new DownloadResponseHandler() {
            @Override
            public void onWait() {
                super.onWait();
                if (notification) {
                    finalDownloadNotification.createNotification();
                }
            }

            @Override
            public void onStart(long start, long length) {
                Log.d(TAG, "onStart " + start + "/" + length);
            }

            @Override
            public void onStop(long end) {
                super.onStop(end);
                if (notification) {
                    finalDownloadNotification.cancelNotification();
                    mIds.remove(Integer.valueOf(finalDownloadNotification.getId()));
                }
                notifyUpgradeFailure(filename, "stop");
            }

            @Override
            public void onFinish(long end) {
                super.onFinish(end);
                if (notification) {
                    finalDownloadNotification.finishNotification();
                }
                if (install) {
                    try {
                        makeLoad(savePath, upgradeType);
                        notifyUpgradeFinish(filename, savePath);
                    }catch (Exception e){
                        if (debug) Log.e("install " + e.getMessage());
                        notifyUpgradeFailure(savePath, e.getMessage());
                    }
                }else{
                    notifyUpgradeFinish(filename, savePath);
                }
            }

            @Override
            public void onProgressUpdate(long end, int progress, int speed) {
                super.onProgressUpdate(end, progress, speed);
                if (notification) {
                    finalDownloadNotification.updateNotification(progress, speed);
                }

                notifyUpgradeProgress(filename, speed, progress);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                if (notification) {
                    finalDownloadNotification.failureNotification();
                }
                notifyUpgradeFailure(filename, content);
            }

        }, saveFile.length(), savePath);
    }

    private void makeLoad(String savePath, UpgradeType upgradeType) {
        switch (upgradeType) {
            case APK:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    final String authority = mContext.getPackageName() + ".fileprovider";
                    if (debug) Log.e("authority=" + authority);
                    final Uri contentUri = FileProvider.getUriForFile(mContext, authority, new File(savePath));
                    AppUtils.install(mContext, contentUri);
                } else {
                    AppUtils.install(mContext, savePath);
                }
                break;
            case RES:

                break;
            case DEX:
                /**
                 DexClassLoader dexClassLoader = new DexClassLoader(savePath, mConfigService.getTempDir().getAbsolutePath(), null, mContext.getClassLoader());
                 try {
                 Class clazz = dexClassLoader.loadClass("className");
                 } catch (ClassNotFoundException e) {
                 Log.e(e.getMessage());
                 }**/
                break;
            case SO:
                System.load(savePath);
                break;
            case OTHER:
                new Intent();
                break;
            default:
                break;
        }
    }

    private Intent createFinishIntent(String savePath, UpgradeType upgradeType) {
        Intent intent = null;
        final File file = new File(savePath);
        switch (upgradeType) {
            case APK:
                intent = new Intent(Intent.ACTION_VIEW);
                //判断是否是AndroidN以及更高的版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    final String authority = mContext.getPackageName() + ".fileprovider";
                    if (debug) Log.e("authority=" + authority);
                    final Uri contentUri = FileProvider.getUriForFile(mContext, authority, file);
                    if (debug) Log.e("uri=" + contentUri);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                break;
            case RES:
                break;
            case DEX:
                break;
            case SO:
                break;
            case OTHER:
                break;
            default:
                break;
        }
        return intent;
    }

    @Override
    public void cancel(String filename) {
        if (mDownloadHttpClient != null)
            mDownloadHttpClient.cancelRequests(filename, true);
    }

    public void notifyUpgradeFinish(String filename, String filepath) {
        if (mOnUpgradeListeners.containsKey(filename)) {
            mOnUpgradeListeners.get(filename).onFinish(filepath);
        }
    }

    public void notifyUpgradeProgress(String filename, int speed, int progress) {
        if (mOnUpgradeListeners.containsKey(filename)) {
            mOnUpgradeListeners.get(filename).progress(speed, progress);
        }
    }

    public void notifyUpgradeFailure(String filename, String error) {
        if (mOnUpgradeListeners.containsKey(filename)) {
            mOnUpgradeListeners.get(filename).onFailure(error);
        }
    }

    @Override
    public void setOnUpgradeListener(String filename, OnUpgradeListener onUpgradeListener) {
        if (!mOnUpgradeListeners.containsKey(filename)) {
            mOnUpgradeListeners.put(filename, onUpgradeListener);
        }
    }
}
