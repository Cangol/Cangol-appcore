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

package mobi.cangol.mobile.soap;


import android.test.AndroidTestCase;

import java.util.HashMap;

/**
 * Created by xuewu.wei on 2016/6/8.
 */
public class SoapClientTest extends AndroidTestCase {

    public void testSend() throws Exception {
        String url="http://www.webxml.com.cn/WebServices/WeatherWebService.asmx";
        String namespace="http://WebXml.com.cn/";
        String action="getSupportCity";
        HashMap<String, String> params=new HashMap<String, String>();
        params.put("byProvinceName","河北");
        SoapClient mSoapClient = new SoapClient();
        mSoapClient.send(getContext(),url,namespace,action,params,new SoapResponseHandler(){
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(String error) {
                        super.onFailure(error);
                    }

                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                    }
                }
        );
    }
}