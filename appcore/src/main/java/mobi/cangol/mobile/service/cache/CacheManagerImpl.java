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
package mobi.cangol.mobile.service.cache;

import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.Task;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.Service;
import mobi.cangol.mobile.service.ServiceProperty;
import mobi.cangol.mobile.service.conf.ConfigService;

/**
 * @author Cangol
 */

@Service("CacheManager")
class CacheManagerImpl implements CacheManager {
    private static final String TAG = "CacheManager";
    private static final int DISK_CACHE_INDEX = 0;
    private static final long DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 20L; // 20MB
    private final Object mDiskCacheLock = new Object();
    private boolean mDebug;
    private DiskLruCache mDiskLruCache;
    private HashMap<String, HashMap<String, CacheObject>> mContextMaps = new HashMap<>();
    private boolean mDiskCacheStarting = true;
    private File mDiskCacheDir;
    private long mDiskCacheSize;
    private ServiceProperty mServiceProperty;
    private CoreApplication mApplication;

    @Override
    public void onCreate(Application context) {
        this.mApplication = (CoreApplication) context;
        if (mDebug) Log.d(TAG, "onCreate");
    }

    @Override
    public void init(ServiceProperty serviceProperty) {
        if (mDebug) Log.d(TAG, "init " + serviceProperty);
        this.mServiceProperty = serviceProperty;
        final String dir = mServiceProperty.getString(CacheManager.CACHE_DIR);
        final long size = mServiceProperty.getLong(CacheManager.CACHE_SIZE);
        final ConfigService configService = (ConfigService) mApplication.getAppService(AppService.CONFIG_SERVICE);
        final String cacheDir = configService.getCacheDir().getAbsolutePath() + File.separator + (!TextUtils.isEmpty(dir) ? dir : "contentCache");
        setDiskCache(new File(cacheDir), size > 0 ? size : DEFAULT_DISK_CACHE_SIZE);
    }

    /**
     * 设置磁盘缓存位置，并初始化
     *
     * @param cacheDir
     * @param cacheSize
     */
    private void setDiskCache(File cacheDir, long cacheSize) {
        if (mDebug) Log.d(TAG, "setDiskCache dir=" + cacheDir + ",size=" + cacheSize);
        this.mDiskCacheDir = cacheDir;
        this.mDiskCacheSize = cacheSize;
        this.mDiskCacheStarting = true;
        initDiskCache(mDiskCacheDir, mDiskCacheSize);
    }

    /**
     * 初始化磁盘缓存
     *
     * @param diskCacheDir
     * @param diskCacheSize
     */
    private void initDiskCache(File diskCacheDir, long diskCacheSize) {
        if (mDebug) Log.d(TAG, "initDiskCache dir=" + diskCacheDir + ",size=" + diskCacheSize);
        // Set up disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
                if (diskCacheDir != null) {
                    if (!diskCacheDir.exists()) {
                        diskCacheDir.mkdirs();
                    }
                    if (getUsableSpace(diskCacheDir) > diskCacheSize) {
                        try {
                            mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, diskCacheSize);
                            if (mDebug) {
                                Log.d(TAG, "Disk cache initialized");
                            }
                        } catch (final IOException e) {
                            Log.e(TAG, "initDiskCache - " + e);
                        }
                    }
                } else {
                    //
                }
            }
            mDiskCacheStarting = false;
            mDiskCacheLock.notifyAll();
        }
    }

    @Override
    public Serializable getContent(String context, String id) {
        if (mDebug) Log.d(TAG, "getContent context=" + context + ",id=" + id);
        HashMap<String, CacheObject> contextMap = mContextMaps.get(context);
        if (null == contextMap) {
            contextMap = new HashMap<>();
            mContextMaps.put(context, contextMap);
        }
        CacheObject cacheObject = contextMap.get(id);
        if (cacheObject == null) {
            cacheObject = getContentFromDiskCache(id);
            if (cacheObject != null) {
                contextMap.put(id, cacheObject);
            }
        }
        if (cacheObject != null) {
            if (cacheObject.isExpired()) {
                Log.e(TAG, "expired:"+cacheObject.getExpired()+",it's expired & removed ");
                removeContent(context, id);
                return null;
            } else {
                return cacheObject.getObject();
            }
        } else {
            return null;
        }
    }

    @Override
    public void getContent(final String context, final String id, final CacheLoader cacheLoader) {
        if (mDebug)
            Log.d(TAG, "getContent context=" + context + ",id=" + id + ",cacheLoader=" + cacheLoader);
        if (cacheLoader != null) cacheLoader.loading();
        HashMap<String, CacheObject> contextMap = mContextMaps.get(context);
        if (null == contextMap) {
            contextMap = new HashMap<>();
            mContextMaps.put(context, contextMap);
        }
        final CacheObject cacheObject = contextMap.get(id);
        if (cacheObject == null) {
            mApplication.post(new Task<CacheObject>() {

                @Override
                public CacheObject call() {
                    return getContentFromDiskCache(id);
                }

                @Override
                public void result(CacheObject cacheObject) {
                    if (cacheObject != null) {
                        if (cacheObject.isExpired()) {
                            Log.e(TAG, "expired:"+cacheObject.getExpired()+",it's expired & removed ");
                            removeContent(context, id);
                            if (cacheLoader != null) cacheLoader.returnContent(null);
                        } else {
                            addContentToMem(context, id, cacheObject.getObject(), cacheObject.getPeriod());
                            if (cacheLoader != null)
                                cacheLoader.returnContent(cacheObject.getObject());
                        }
                    } else {
                        if (cacheLoader != null) cacheLoader.returnContent(null);
                    }
                }
            });
        } else {
            if (cacheObject.isExpired()) {
                Log.e(TAG, "expired:"+cacheObject.getExpired()+",it's expired & removed ");
                removeContent(context, id);
                if (cacheLoader != null) cacheLoader.returnContent(null);
            } else {
                if (cacheLoader != null) cacheLoader.returnContent(cacheObject.getObject());
            }
        }
    }

    @Override
    public boolean hasContent(String context, String id) {
        if (mDebug) Log.d(TAG, "hasContent context=" + context + ",id=" + id);
        HashMap<String, CacheObject> contextMap = mContextMaps.get(context);
        if (null == contextMap) {
            contextMap = new HashMap<>();
            mContextMaps.put(context, contextMap);
        }
        final CacheObject cacheObject = contextMap.get(id);
        if (cacheObject == null) {
            return hasContentFromDiskCache(id);
        } else {
            if (cacheObject.isExpired()) {
                Log.e(TAG, "expired:"+cacheObject.getExpired()+",it's expired & removed ");
                removeContent(context, id);
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 判断磁盘缓存是否含有
     *
     * @param id
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasContentFromDiskCache(String id) {
        if (mDebug) Log.d(TAG, "hasContentFromDiskCache id=" + id);
        final String key = hashKeyForDisk(id);
        synchronized (mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                    Log.d(e.getMessage());
                }
            }
            if (mDiskLruCache != null) {
                InputStream inputStream = null;
                try {
                    final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot != null) {
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                        if (inputStream != null) {
                            final CacheObject cacheObject = (CacheObject) readObject(inputStream);
                            if (cacheObject!=null&&cacheObject.isExpired()) {
                                Log.e(TAG, "expired:"+cacheObject.getExpired()+",it's expired & removed ");
                                mDiskLruCache.remove(hashKeyForDisk(id));
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                } catch (final IOException e) {
                    Log.e(TAG, "getContentFromDiskCache - " + e);
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        Log.d(e.getMessage());
                    }
                }
            }
            return false;
        }
    }

    /**
     * 从磁盘缓存获取
     *
     * @param id
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private CacheObject getContentFromDiskCache(String id) {
        if (mDebug) Log.d(TAG, "getContentFromDiskCache id=" + id);
        final String key = hashKeyForDisk(id);
        synchronized (mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                    Log.d(e.getMessage());
                }
            }
            if (mDiskLruCache != null) {
                InputStream inputStream = null;
                try {
                    final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot != null) {
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                        if (inputStream != null) {
                            return (CacheObject) readObject(inputStream);
                        }
                    }
                } catch (final IOException e) {
                    Log.e(TAG, "getContentFromDiskCache - " + e);
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        Log.d(e.getMessage());
                    }
                }
            }
            return null;
        }

    }

    /**
     * 添加到内存缓存
     *
     * @param context
     * @param id
     * @param data
     */
    private void addContentToMem(String context, String id, Serializable data) {
        HashMap<String, CacheObject> contextMap = mContextMaps.get(context);
        if (null == contextMap) {
            contextMap = new HashMap<>();
        }
        contextMap.put(id, new CacheObject(context, id, data));
        mContextMaps.put(context, contextMap);
    }

    /**
     * 添加到内存缓存
     *
     * @param context
     * @param id
     * @param data
     * @param period
     */
    private void addContentToMem(String context, String id, Serializable data, long period) {
        HashMap<String, CacheObject> contextMap = mContextMaps.get(context);
        if (null == contextMap) {
            contextMap = new HashMap<>();
        }
        contextMap.put(id, new CacheObject(context, id, data, period));
        mContextMaps.put(context, contextMap);
    }

    /**
     * 添加到磁盘缓存（也添加到内存缓存）
     */
    @Override
    public void addContent(String context, String id, Serializable data) {
        if (mDebug) Log.d(TAG, "addContent:" + id + "," + data);
        removeContent(context, id);
        addContentToMem(context, id, data);
        asyncAddContentToDiskCache(id, new CacheObject(context, id, data));
    }

    @Override
    public void addContent(String context, String id, Serializable data, long period) {
        if (mDebug) Log.d(TAG, "addContent:" + id + "," + data + "," + period);
        removeContent(context, id);
        addContentToMem(context, id, data, period);
        asyncAddContentToDiskCache(id, new CacheObject(context, id, data, period));
    }

    /**
     * 异步添加到磁盘缓存
     *
     * @param id
     * @param cacheObject
     */
    private void asyncAddContentToDiskCache(final String id, final CacheObject cacheObject) {
        mApplication.post(new Runnable() {
            @Override
            public void run() {
                addContentToDiskCache(id, cacheObject);
            }
        });
    }

    /**
     * 添加到磁盘缓存
     *
     * @param id
     * @param cacheObject
     */
    private void addContentToDiskCache(String id, CacheObject cacheObject) {

        synchronized (mDiskCacheLock) {
            // Add to disk cache
            if (mDiskLruCache != null) {
                final String key = hashKeyForDisk(id);
                OutputStream out = null;
                try {
                    final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot == null) {
                        final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                        if (editor != null) {
                            out = editor.newOutputStream(DISK_CACHE_INDEX);
                            // 写入out流
                            writeObject(cacheObject, out);
                            editor.commit();
                            out.close();
                            flush();
                        }
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX).close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "addContentToCache - " + e);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        Log.d(e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void removeContext(String context) {
        final HashMap<String, CacheObject> contextMap = mContextMaps.get(context);
        if (null == contextMap || contextMap.isEmpty()) {
            return;
        }
        final Iterator<String> iterator = contextMap.keySet().iterator();
        String id = null;
        while (iterator.hasNext()) {
            id = iterator.next();
            final String key = hashKeyForDisk(id);
            try {
                if (mDiskLruCache != null) {
                    mDiskLruCache.remove(key);
                }
            } catch (IOException e) {
                if (mDebug) {
                    Log.d(TAG, "cache remove" + key, e);
                }
            }
        }
        contextMap.clear();
        mContextMaps.remove(context);
    }

    @Override
    public void removeContent(String context, String id) {
        final HashMap<String, CacheObject> contextMap = mContextMaps.get(context);
        if (null == contextMap || contextMap.isEmpty()) {
            return;
        }
        contextMap.remove(id);
        final String key = hashKeyForDisk(id);
        try {
            if (mDiskLruCache != null) {
                mDiskLruCache.remove(key);
            }
        } catch (IOException e) {
            if (mDebug) {
                Log.d(TAG, "cache remove" + key, e);
            }
        }
    }

    @Override
    public long size() {
        long size = 0;
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                size = mDiskLruCache.size();
            }
        }
        return size;
    }

    @Override
    public void clearCache() {
        if (mContextMaps != null) {
            mContextMaps.clear();
            if (mDebug) {
                Log.d(TAG, "Memory cache cleared");
            }
        }

        synchronized (mDiskCacheLock) {
            mDiskCacheStarting = true;
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.delete();
                    if (mDebug) {
                        Log.d(TAG, "Disk cache cleared");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "clearCache - " + e);
                }
                mDiskLruCache = null;
                initDiskCache(mDiskCacheDir, mDiskCacheSize);
            }
        }
    }

    @Override
    public void flush() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    mDiskLruCache.flush();
                    if (mDebug) {
                        Log.d(TAG, "Disk cache flushed");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "flush - " + e);
                }
            }
        }
    }

    @Override
    public void close() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    if (!mDiskLruCache.isClosed()) {
                        mDiskLruCache.close();
                        mDiskLruCache = null;
                        if (mDebug) {
                            Log.d(TAG, "Disk cache closed");
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "close - " + e);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String hashKeyForDisk(String key) {
        String cacheKey = null;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes(StandardCharsets.UTF_8));
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }
    private Serializable readObject(InputStream is) {
        Object object = null;
        try {
            object = new ObjectInputStream(new BufferedInputStream(is)).readObject();
        } catch (Exception e) {
            Log.e(TAG,"readObject",e);
        }
        return (Serializable) object;
    }
    private void writeObject(Serializable obj, OutputStream out) {
        BufferedOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new BufferedOutputStream(out);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
        } catch (Exception e) {
            Log.e(TAG,"writeObject",e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                Log.e(TAG,"writeObject close",e);
            }

            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                Log.e(TAG,"writeObject close",e);
            }
        }
    }
    private String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            final String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private long getUsableSpace(File path) {
        return path.getUsableSpace();
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void onDestroy() {
        this.close();
    }

    @Override
    public void setDebug(boolean mDebug) {
        this.mDebug = mDebug;
    }

    @Override
    public ServiceProperty getServiceProperty() {
        return mServiceProperty;
    }

    @Override
    public ServiceProperty defaultServiceProperty() {
        final ServiceProperty sp = new ServiceProperty(TAG);
        sp.putString(CACHE_DIR, "contentCache");
        sp.putInt(CACHE_SIZE, 20971520);
        return sp;
    }

}
