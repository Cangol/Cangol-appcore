/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package mobi.cangol.mobile.http;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {

    protected ConcurrentHashMap<String, String> urlParams;
    protected ConcurrentHashMap<String, File> fileParams;

    public RequestParams() {
        init();
    }

    public RequestParams(Map<String, String> source) {
        init();

        for (final Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public RequestParams(String key, String value) {
        init();

        put(key, value);
    }

    public RequestParams(Object... keysAndValues) {
        init();
        final int len = keysAndValues.length;
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Supplied arguments must be even");
        }
        for (int i = 0; i < len; i += 2) {
            final String key = String.valueOf(keysAndValues[i]);
            final String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    public void put(String key, File value) {
        if (key != null && value != null) {
            fileParams.put(key, value);
        }
    }

    public void remove(String key) {
        urlParams.remove(key);
        fileParams.remove(key);
    }

    public boolean isEmpty() {
        return urlParams.isEmpty() && fileParams.isEmpty();
    }

    public String toDebugString() {
        final StringBuilder result = new StringBuilder();
        for (final Map.Entry<String, String> entry : urlParams.entrySet()) {
            result.append('\t')
                    .append(entry.getKey())
                    .append('=')
                    .append(entry.getValue())
                    .append('\n');
        }

        for (final Map.Entry<String, File> entry : fileParams.entrySet()) {
            result.append(entry.getKey())
                    .append("=FILE\n");
        }

        return result.toString();
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (final Map.Entry<String, String> entry : urlParams.entrySet()) {
            if (result.length() > 0) {
                result.append('&');
            }

            result.append(entry.getKey())
                    .append('=')
                    .append(entry.getValue());
        }

        for (final Map.Entry<String, File> entry : fileParams.entrySet()) {
            if (result.length() > 0) {
                result.append('&');
            }

            result.append(entry.getKey())
                    .append("=FILE");
        }

        return result.toString();
    }

    private void init() {
        urlParams = new ConcurrentHashMap<>();
        fileParams = new ConcurrentHashMap<>();
    }
}