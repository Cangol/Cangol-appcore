package mobi.cangol.mobile.utils;

import android.test.AndroidTestCase;

/**
 * Created by weixuewu on 16/6/12.
 */
public class StorageUtilsTest extends AndroidTestCase {

    public void testGetExternalStorageDir() {
        assertNotNull(StorageUtils.getExternalStorageDir(getContext(),"test"));
    }

    public void testGetFileDir() {
        assertNotNull(StorageUtils.getFileDir(getContext(),"test"));
    }

    public void testIsExternalStorageRemovable() {
       StorageUtils.isExternalStorageRemovable();
    }

    public void testGetExternalFileDir() {
        assertNotNull(StorageUtils.getExternalFileDir(getContext(),"test"));
    }

    public void testGetExternalCacheDir() {
        assertNotNull(StorageUtils.getExternalCacheDir(getContext()));
    }

    public void testGetUsableSpace() {
        assertNotNull(StorageUtils.getUsableSpace(StorageUtils.getExternalCacheDir(getContext())));
    }
}