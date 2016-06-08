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

package mobi.cangol.mobile.utils;

import android.test.InstrumentationTestCase;

import java.util.Map;

/**
 * Created by xuewu.wei on 2016/6/8.
 */
public class UrlUtilsTest extends InstrumentationTestCase {

    
    public void testIsUrl() throws Exception {
        assertEquals(false,UrlUtils.isUrl(null));
        assertEquals(false,UrlUtils.isUrl(""));
        assertEquals(false,UrlUtils.isUrl("123g"));
        assertEquals(false,UrlUtils.isUrl("123@g.cn"));
        assertEquals(false,UrlUtils.isUrl("wxw@g.cn"));
        assertEquals(true,UrlUtils.isUrl("https://cangol.mobi"));
        assertEquals(true,UrlUtils.isUrl("http://cangol.mobi"));
    }

    public void testGetHost() throws Exception {
        String host=UrlUtils.getHost("http://cangol.mobi:8080/action?key=123%flag=1");
        assertNotNull("host="+host,host);
        assertEquals("cangol.mobi",host);
    }

    public void testGetParams() throws Exception {
        Map<String,String> map=UrlUtils.getParams("http://cangol.mobi:8080/action?key=123&flag=1");
        assertEquals("123",map.get("key"));
        assertEquals("1",map.get("flag"));
    }
}