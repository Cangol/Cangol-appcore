/**
 * Copyright (c) 2013 Cangol
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.http;


import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.CertificatePinner;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class HttpClientFactory {
    private static final String TAG = "HttpClientFactory";
    private static final int DEFAULT_CONNECT_TIMEOUT = 20 * 1000;
    private static final int DEFAULT_READ_TIMEOUT = 20 * 1000;
    private static final int DEFAULT_WRITE_TIMEOUT = 20 * 1000;

    private HttpClientFactory(){}

    /**
     * 创建默的 HttpClient
     * @return
     */
    public static OkHttpClient createDefaultHttpClient() {
            return new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();
    }
    /**
     * 创建 auth认证的 HttpClient
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public static OkHttpClient createAuthHttpClient(final String username, final String password) {
            return new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    .authenticator(new Authenticator() {
                        @Override
                        public Request authenticate(Route route, Response response) {
                            final  String credential = Credentials.basic(username, password);
                            return response.request().newBuilder()
                                    .header("Authorization", credential)
                                    .build();
                        }
                    })
                    .build();
    }

    /**
     * 创建 固定证书的 HttpClient
     *
     * @param pattern
     * @param pins
     * @return
     */
    public static OkHttpClient createCertHttpClient(final String pattern, final String... pins) {
            return new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    .certificatePinner(new CertificatePinner.Builder()
                            .add(pattern, pins)
                            .build())
                    .build();
    }
}
