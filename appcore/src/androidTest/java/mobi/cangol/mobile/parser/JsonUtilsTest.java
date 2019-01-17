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

import org.json.JSONObject;

/**
 * Created by xuewu.wei on 2016/6/6.
 */
public class JsonUtilsTest extends InstrumentationTestCase {
    private String jsonStr=" {\"name\":\"Nick\",\"id\":1,\"height\":1.75,\"isChild\":true}";

    public void testToJSONObject() throws Exception {
        ParserObject obj=new ParserObject(1,"Nick",1.75d,true);
        JSONObject jsonObject=JsonUtils.toJSONObject(obj,false);
        assertNotNull(jsonObject);
        assertEquals(jsonObject.getInt("id"),obj.getId());
        assertEquals(jsonObject.getString("name"),obj.getName());
        assertEquals(jsonObject.getDouble("height"),obj.getHeight());
        assertEquals(jsonObject.getBoolean("isChild"),obj.isChild());
    }

    public void testParserToObject() throws Exception {
        JSONObject jsonObject=JsonUtils.formatJSONObject(jsonStr);
        ParserObject obj=JsonUtils.parserToObject(ParserObject.class,jsonStr,false);
        assertNotNull(obj);
        assertEquals(jsonObject.getInt("id"),obj.getId());
        assertEquals(jsonObject.getString("name"),obj.getName());
        assertEquals(jsonObject.getDouble("height"),obj.getHeight());
        assertEquals(jsonObject.getBoolean("isChild"),obj.isChild());
    }
}
class ParserObject {
    @Element("id")
    private int id;
    @Element("name")
    private String name;
    @Element("_HEIGHT")
    private double height;
    @Element("_IS_CHILD")
    private boolean isChild;

    public ParserObject() {

    }

    public ParserObject(int id, String name, double height, boolean isChild) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.isChild = isChild;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setChild(boolean child) {
        isChild = child;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParserObject{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", height=").append(height);
        sb.append(", isChild=").append(isChild);
        sb.append('}');
        return sb.toString();
    }
}