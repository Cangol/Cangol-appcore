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

package mobi.cangol.mobile.parser;

import android.test.InstrumentationTestCase;


/**
 * Created by xuewu.wei on 2016/6/6.
 */
public class XmlUtilsTest extends InstrumentationTestCase {


    private String xmlStr="<?xml version='1.0' encoding='utf-8' standalone='yes' ?><ParserObject><name>Nick</name><id>1</id><height>1.75</height><isChild>true</isChild></ParserObject>";


    public void testToXml() {
        ParserObject obj=new ParserObject(1,"Nick",1.75d,true);
        String xml=XmlUtils.toXml(obj,false);
        assertNotNull(xml);
    }

    public void testParserToObject() throws Exception {
        ParserObject obj=XmlUtils.parserToObject(ParserObject.class,xmlStr,false);
        assertNotNull(obj);
        assertEquals(1,obj.getId());
        assertEquals("Nick",obj.getName());
        assertEquals(1.75,obj.getHeight());
        assertEquals(true,obj.isChild());
    }
}