/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.parser;

import org.json.JSONObject;

import java.lang.reflect.Field;


public class JSONParserException extends ParserException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JSONObject mNode;

    public JSONParserException(String message, Throwable throwable) {
        super(throwable);
    }

    public JSONParserException(Class<?> clazz, String message, Throwable throwable) {
        super(clazz, message, throwable);
    }

    public JSONParserException(Class<?> clazz, Field field, String message, Throwable throwable) {
        super(clazz, field, message, throwable);
    }

    public JSONParserException(JSONObject node, String message, Throwable throwable) {
        super(throwable);
        mNode = node;
    }

    @Override
    public String getMessage() {
        if (mNode != null) {
            return "\nError '" + super.getMessage() + "' occurred in \n" + mNode.toString() + "\n" + getCause().getMessage();
        } else {
            return super.getMessage();
        }
    }
}
