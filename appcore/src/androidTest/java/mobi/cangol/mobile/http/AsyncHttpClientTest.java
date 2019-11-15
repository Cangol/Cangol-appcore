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

package mobi.cangol.mobile.http;

import android.test.AndroidTestCase;

import org.json.JSONObject;

/**
 * Created by xuewu.wei on 2016/6/8.
 */
public class AsyncHttpClientTest extends AndroidTestCase {
    private String url = "http://www.cangol.mobi/cmweb/api/station/sync.do";

    public void testGet() {
        AsyncHttpClient httpClient = AsyncHttpClient.Companion.build("test");
        RequestParams params=new RequestParams();
        params.put("deviceId","111");
        httpClient.get(getContext(), url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                super.onSuccess(statusCode, response);
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                super.onFailure(e, errorResponse);
            }
        });
    }
}