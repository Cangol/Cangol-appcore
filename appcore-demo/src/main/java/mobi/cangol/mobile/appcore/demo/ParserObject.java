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

package mobi.cangol.mobile.appcore.demo;

import mobi.cangol.mobile.parser.Element;

/**
 * Created by xuewu.wei on 2016/5/31.
 */
public class ParserObject {
    @Element("_ID")
    private int id;
    @Element("_NAME")
    private String name;
    @Element("_HEIGHT")
    private double height;
    @Element("_IS_CHILD")
    private boolean isChild;

    public ParserObject() {

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
        final StringBuffer sb = new StringBuffer("ParserObject{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", height=").append(height);
        sb.append(", isChild=").append(isChild);
        sb.append('}');
        return sb.toString();
    }
}
