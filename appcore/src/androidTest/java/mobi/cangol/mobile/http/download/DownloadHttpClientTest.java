/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.http.download;

import android.test.AndroidTestCase;

import mobi.cangol.mobile.utils.StorageUtils;

/**
 * Created by xuewu.wei on 2016/6/8.
 */
public class DownloadHttpClientTest extends AndroidTestCase {
    private String url="http://180.153.105.145/dd.myapp.com/16891/8E5A9885970F76080F8445C652DE347C.apk?mkey=5715c34fc20a8141&f=d511&fsname=com.tencent.mobileqq_6.3.1_350.apk&p=.apk";

    public void testSend() throws Exception {
        DownloadHttpClient downloadHttpClient=DownloadHttpClient.build("test");

        downloadHttpClient.send("test",url,new DownloadResponseHandler(){
            @Override
            public void onWait() {
                super.onWait();
            }

            @Override
            public void onStart(long length) {
                super.onStart(length);
            }

            @Override
            public void onStop(long end) {
                super.onStop(end);
            }

            @Override
            public void onFinish(long end) {
                super.onFinish(end);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onProgressUpdate(long end, int progress, int speed) {
                super.onProgressUpdate(end, progress, speed);
            }
        },0, StorageUtils.getExternalCacheDir(getContext()).getAbsolutePath());

    }
}