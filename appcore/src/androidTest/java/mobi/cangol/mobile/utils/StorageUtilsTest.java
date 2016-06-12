package mobi.cangol.mobile.utils;

import android.test.AndroidTestCase;

/**
 * Created by weixuewu on 16/6/12.
 */
public class StorageUtilsTest extends AndroidTestCase {

    public void testGetExternalStorageDir() throws Exception {
        assertNotNull(StorageUtils.getExternalStorageDir(getContext(),"test"));
    }

    public void testGetFileDir() throws Exception {
        assertNotNull(StorageUtils.getFileDir(getContext(),"test"));
    }

    public void testIsExternalStorageRemovable() throws Exception {
       StorageUtils.isExternalStorageRemovable();
    }

    public void testGetExternalFileDir() throws Exception {
        assertNotNull(StorageUtils.getExternalFileDir(getContext(),"test"));
    }

    public void testGetExternalCacheDir() throws Exception {
        assertNotNull(StorageUtils.getExternalCacheDir(getContext()));
    }

    public void testGetUsableSpace() throws Exception {
        assertNotNull(StorageUtils.getUsableSpace(StorageUtils.getExternalCacheDir(getContext())));
    }
}