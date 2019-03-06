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

import mobi.cangol.mobile.http.route.RouteHttpClient;
import mobi.cangol.mobile.http.route.RouteResponseHandler;

/**
 * Created by xuewu.wei on 2016/6/8.
 */
public class RouteHttpClientTest extends AndroidTestCase {
    private String url = "http://www.cangol.mobi/cmweb/api/station/sync.do";
    public void testSend() {
        RouteHttpClient httpClient = new RouteHttpClient();
        httpClient.send(getContext(), url, null, new RouteResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
            }
        },"www.cangol.mobi");
    }
}