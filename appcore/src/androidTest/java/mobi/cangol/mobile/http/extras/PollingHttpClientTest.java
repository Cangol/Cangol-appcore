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

package mobi.cangol.mobile.http.extras;

import android.test.AndroidTestCase;

import java.util.HashMap;

import mobi.cangol.mobile.http.polling.PollingHttpClient;
import mobi.cangol.mobile.http.polling.PollingResponseHandler;

/**
 * Created by xuewu.wei on 2016/6/8.
 */
public class PollingHttpClientTest extends AndroidTestCase {

    private String url = "http://www.cangol.mobi/cmweb/api/station/sync.do";

    public void testGet() throws Exception {
        PollingHttpClient httpClient = new PollingHttpClient("polling");
        httpClient.send(getContext(), url, new HashMap<String, String>(), new PollingResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public boolean isFailResponse(String content) {
                return super.isFailResponse(content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
            }

            @Override
            public void onPollingFinish(int execTimes, String content) {
                super.onPollingFinish(execTimes, content);
            }
        },3,1000L);
    }
}