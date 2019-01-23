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

import java.lang.reflect.Field;

public class ParserException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Field field = null;
    private Class<?> clazz = null;

    public ParserException(String message) {
        super(message);
    }

    public ParserException(Throwable throwable) {
        super(throwable);
    }

    public ParserException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ParserException(Class<?> clazz, String message, Throwable throwable) {
        super(message, throwable);
        this.field = null;
        this.clazz = clazz;
    }

    public ParserException(Class<?> clazz, Field field, String message, Throwable throwable) {
        super(message, throwable);
        this.field = field;
        this.clazz = clazz;
    }

    @Override
    public String getMessage() {
        if (clazz != null) {
            if (field != null) {
                String fieldName = field.getName();
                return "\nError '" + super.getMessage() + "' occurred in " + clazz.getName() + "@" + fieldName + "\n" + getCause().getMessage();
            } else {
                return "\nError '" + super.getMessage() + "' occurred in " + clazz.getName() + "\n" + getCause().getMessage();
            }
        } else {
            return super.getMessage();
        }
    }
}
