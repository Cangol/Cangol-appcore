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
package mobi.cangol.mobile.service.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.parser.JsonUtils;
import mobi.cangol.mobile.service.PoolManager.Pool;
import mobi.cangol.mobile.utils.FileUtils;
import mobi.cangol.mobile.utils.Object2FileUtils;

public abstract class DownloadExecutor<T> {
    protected ArrayList<DownloadResource> mDownloadRes = new ArrayList<>();
    private String mTag = "DownloadExecutor";
    private ArrayList<SoftReference<DownloadStatusListener>> listeners = new ArrayList<>();
    private Pool mPool;
    private Context mContext;
    private File mDownloadDir;
    private String mName;
    private DownloadEvent mDownloadEvent;
    private ExecutorHandler mHandler;
    private boolean mHttpSafe = true;

    public DownloadExecutor(String name) {
        this.mName = name;
        this.mHandler = new ExecutorHandler(this);
        this.mTag = "DownloadExecutor_" + name;
    }

    public void setHttpSafe(boolean safe) {
        this.mHttpSafe = safe;
    }

    protected void setContext(Context context) {
        this.mContext = context;
    }

    public File getDownloadDir() {
        return mDownloadDir;
    }

    protected void setDownloadDir(File directory) {
        mDownloadDir = directory;
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    protected void setPool(Pool pool) {
        this.mPool = pool;
    }

    protected void init() {
        mDownloadRes.addAll(scanResource(mDownloadDir));
    }

    public void setDownloadEvent(DownloadEvent downloadEvent) {
        this.mDownloadEvent = downloadEvent;
    }

    /**
     * 下载对象转换为DownloadResource
     *
     * @param t
     * @return
     */
    protected abstract DownloadResource getDownloadResource(T t);

    /**
     * DownloadResource转换为下载对象
     *
     * @param resource
     * @return
     */
    protected abstract T getDownloadModel(DownloadResource resource);

    /**
     * 创建按一个状态栏通知
     *
     * @param context
     * @param resource
     * @return
     */
    public abstract DownloadNotification notification(Context context, DownloadResource resource);

    /**
     * 扫描本地的下载任务资源
     *
     * @return
     */
    protected ArrayList<DownloadResource> scanResource(File scanDir) {
        final ArrayList<DownloadResource> list = new ArrayList<>();
        final ArrayList<File> fileList = new ArrayList<>();
        //耗时操作
        FileUtils.searchBySuffix(scanDir, fileList, Download.SUFFIX_CONFIG);
        for (int i = 0; i < fileList.size(); i++) {
            list.add(readResource(fileList.get(i).getAbsolutePath()));
        }
        return list;
    }

    /**
     * 读取文件下载资源
     *
     * @param filePath
     * @return
     */
    protected DownloadResource readResource(String filePath) {
        Log.d(mTag, "read DownloadResource <" + filePath);
        //使用json格式存储
        DownloadResource downloadResource = null;
        try {
            //downloadResource= (DownloadResource) Object2FileUtils.readObject(new File(filePath));
            final JSONObject jsonObject = Object2FileUtils.readFile2JSONObject(new File(filePath));
            downloadResource = JsonUtils.parserToObject(DownloadResource.class, jsonObject, false,true);

        } catch (Exception e) {
            Log.d(mTag, e.getMessage());
        }
        return downloadResource;
    }
    /**
     * 存除下载资源到本地
     *
     * @param resource
     */
    protected void writeResource(final DownloadResource resource) {
        Log.d(mTag, "write DownloadResource >" + resource.getConfFile());

        //Object2FileUtils.writeObject(resource, resource.getConfFile());
        if(mContext!=null)((CoreApplication)mContext.getApplicationContext()).post(new Runnable() {
            @Override
            public void run() {
                //使用json格式存储
                final  JSONObject jsonObject = JsonUtils.toJSONObject(resource, false,true);
                Object2FileUtils.writeJSONObject2File(jsonObject, resource.getConfFile());
                Log.d(mTag, "write DownloadResource exist=" + new File(resource.getConfFile()).exists());
            }
        });
    }

    /**
     * 通过唯一识别符获取下载资源
     *
     * @param key
     * @return
     */
    public DownloadResource getDownloadResource(String key) {
        for (final DownloadResource resource : mDownloadRes) {
            if (key != null && key.equals(resource.getKey())) {
                return resource;
            }
        }
        return null;
    }

    /**
     * 开始下载
     *
     * @param resource
     */
    public void start(DownloadResource resource) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null");
            return;
        }
        if (mDownloadRes.contains(resource)) {
            DownloadTask downloadTask = resource.getDownloadTask();
            if (downloadTask == null) {
                downloadTask = new DownloadTask(resource, mPool, mHandler, true);
                resource.setDownloadTask(downloadTask);
                downloadTask.setDownloadNotification(notification(mContext, resource));
            }
            if (!downloadTask.isRunning()) {
                downloadTask.start();
            }
        } else {
            final DownloadTask downloadTask = new DownloadTask(resource, mPool, mHandler, true);
            resource.setDownloadTask(downloadTask);
            downloadTask.setDownloadNotification(notification(mContext, resource));
            downloadTask.start();
            synchronized (mDownloadRes) {
                mDownloadRes.add(resource);
            }
        }
    }

    /**
     * 停止下载
     *
     * @param resource
     */
    public void stop(DownloadResource resource) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null");
            return;
        }
        if (mDownloadRes.contains(resource)) {
            DownloadTask downloadTask = resource.getDownloadTask();
            if (downloadTask.isRunning()) {
                downloadTask.stop();
            }
        } else {
            Log.e(mTag, "resource isn't exist");
        }
    }

    /**
     * 恢复下载
     *
     * @param resource
     */
    public void resume(DownloadResource resource) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null");
            return;
        }
        if (mDownloadRes.contains(resource)) {
            final DownloadTask downloadTask = resource.getDownloadTask();
            downloadTask.resume();
        }
    }

    /**
     * 重启下载
     *
     * @param resource
     */
    public void restart(DownloadResource resource) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null");
            return;
        }
        if (mDownloadRes.contains(resource)) {
            final DownloadTask downloadTask = resource.getDownloadTask();
            downloadTask.restart();
        }
    }

    /**
     * 添加下载任务
     *
     * @param resource
     */
    public void add(DownloadResource resource) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null");
            return;
        }
        if (!mDownloadRes.contains(resource)) {
            final  DownloadTask downloadTask = new DownloadTask(resource, mPool, mHandler, mHttpSafe);
            resource.setDownloadTask(downloadTask);
            downloadTask.setDownloadNotification(notification(mContext, resource));
            downloadTask.start();
            synchronized (mDownloadRes) {
                mDownloadRes.add(resource);
            }
        }else if(resource.getStatus()!=Download.STATUS_FINISH){
            final  DownloadTask downloadTask = new DownloadTask(resource, mPool, mHandler, mHttpSafe);
            resource.setDownloadTask(downloadTask);
            downloadTask.setDownloadNotification(notification(mContext, resource));
            downloadTask.start();
        }
    }

    /**
     * 移除下载任务
     *
     * @param resource
     */
    public void remove(DownloadResource resource) {
        if (resource == null) {
            Log.e(mTag, "resource isn't null");
            return;
        }
        synchronized (mDownloadRes) {
            if (mDownloadRes.contains(resource)) {
                final DownloadTask downloadTask = resource.getDownloadTask();
                downloadTask.remove();
                mDownloadRes.remove(resource);
            } else {
                Log.e(mTag, "resource isn't exist");
            }
        }
    }

    /**
     * 恢复所有下载
     */
    public void recoverAll() {
        synchronized (mDownloadRes) {
            DownloadTask downloadTask = null;
            for (final DownloadResource resource : mDownloadRes) {
                downloadTask = resource.getDownloadTask();
                if (resource.getStatus() == Download.STATUS_RERUN) {
                    downloadTask.resume();
                }
            }
        }
    }

    /**
     * 中断所有下载
     */
    public void interruptAll() {
        synchronized (mDownloadRes) {
            DownloadTask downloadTask = null;
            for (final DownloadResource resource : mDownloadRes) {
                downloadTask = resource.getDownloadTask();
                if (resource.getStatus() < Download.STATUS_STOP) {
                    downloadTask.interrupt();
                }
            }
        }
    }

    /**
     * 关闭所有下载
     */
    public void close() {
        synchronized (mDownloadRes) {
            DownloadTask downloadTask = null;
            for (final DownloadResource resource : mDownloadRes) {
                downloadTask = resource.getDownloadTask();
                if (downloadTask != null) {
                    downloadTask.stop();
                }
            }
        }
        mDownloadRes.clear();
        mPool.close(false);
    }

    /**
     * 注册下载状态监听
     */
    public void registerDownloadStatusListener(DownloadStatusListener downloadStatusListener) {
        if (null == downloadStatusListener) {
            throw new IllegalArgumentException("downloadStatusListener is null!");
        }
        boolean isExist = false;
        for (final SoftReference<DownloadStatusListener> listener : listeners) {
            if (downloadStatusListener.equals(listener.get())) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            listeners.add(new SoftReference<DownloadStatusListener>(downloadStatusListener));
        }
    }

    /**
     * 移除下载状态监听
     */
    public void unregisterDownloadStatusListener(DownloadStatusListener downloadStatusListener) {
        if (null == downloadStatusListener) {
            throw new IllegalArgumentException("downloadStatusListener is null!");
        }
        for (final SoftReference<DownloadStatusListener> listener : listeners) {
            if (downloadStatusListener.equals(listener.get())) {
                listeners.remove(listener);
                break;
            }
        }
    }

    private void notifyUpdateStatus(DownloadResource resource, int action) {
        for (final SoftReference<DownloadStatusListener> listener : listeners) {
            if (null != listener.get()) {
                listener.get().onStatusChange(resource, action);
            }
        }
    }

    private void handleMessage(Message msg) {
        final DownloadResource resource = (DownloadResource) msg.obj;
        switch (msg.what) {
            case Download.ACTION_DOWNLOAD_START:
                if (null != mDownloadEvent) {
                    mDownloadEvent.onStart(resource);
                }
                writeResource(resource);
                break;
            case Download.ACTION_DOWNLOAD_STOP:
                writeResource(resource);
                break;
            case Download.ACTION_DOWNLOAD_FINISH:
                if (null != mDownloadEvent) {
                    mDownloadEvent.onFinish(resource);
                }
                writeResource(rename(resource));
                break;
            case Download.ACTION_DOWNLOAD_FAILED:
                if (null != mDownloadEvent) {
                    mDownloadEvent.onFailure(resource);
                }
                writeResource(resource);
                break;
            default:
                break;
        }
        notifyUpdateStatus(resource, msg.what);
    }
    private DownloadResource rename(DownloadResource resource){
        if(resource.getStatus()==Download.STATUS_FINISH){
            File file=new File(resource.getSourceFile());
            boolean result=file.renameTo(new File(resource.getSourceFile().replace(Download.SUFFIX_SOURCE, "")));
            if(result)resource.setSourceFile(resource.getSourceFile().replace(Download.SUFFIX_SOURCE, ""));
        }
        return resource;
    }
    static final class ExecutorHandler extends Handler {
        private final SoftReference<DownloadExecutor> mDownloadExecutor;

        public ExecutorHandler(DownloadExecutor downloadExecutor) {
            mDownloadExecutor = new SoftReference<>(downloadExecutor);
        }

        public void handleMessage(Message msg) {
            final DownloadExecutor downloadExecutor = mDownloadExecutor.get();
            if (downloadExecutor != null) {
                downloadExecutor.handleMessage(msg);
            }
        }
    }
}
