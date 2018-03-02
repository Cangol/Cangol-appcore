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


import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import mobi.cangol.mobile.logging.Log;
import okhttp3.Authenticator;
import okhttp3.CertificatePinner;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class HttpClientFactory {
    private final static String TAG = "HttpClientFactory";
    private final static int DEFAULT_RETRYTIMES = 3;
    private final static int DEFAULT_CONNECT_TIMEOUT = 20 * 1000;
    private final static int DEFAULT_READ_TIMEOUT = 20 * 1000;
    private final static int DEFAULT_WRITE_TIMEOUT = 20 * 1000;

    private static OkHttpClient httpClient;

    /**
     * 创建默的 HttpClient
     *
     * @return
     */
    public static OkHttpClient createDefaultHttpClient() {

        httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();

        return httpClient;
    }

    /**
     * 创建 auth认证的 HttpClient
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public static OkHttpClient createAuthHttpClient(final String username, final String password) {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        String credential = Credentials.basic(username, password);
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .build();
                    }
                })
                .build();

        return httpClient;
    }

    /**
     * 创建 固定证书的 HttpClient
     *
     * @param pattern
     * @param pins
     * @return
     */
    public static OkHttpClient createCertHttpClient(final String pattern, final String... pins) {

        OkHttpClient httpClient = new OkHttpClient.Builder()
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

        return httpClient;
    }

    /**
     * 创建 auth认证的 HttpClient
     *
     * @param certificates
     * @param bksFile
     * @param password
     * @return
     */
    public static OkHttpClient createSafeHttpClient(InputStream[] certificates, InputStream bksFile, String password) {
        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager = null;
            if (trustManagers != null) {
                trustManager = new MyTrustManager(chooseTrustManager(trustManagers));
            } else {
                trustManager = new UnSafeTrustManager();
            }
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();

        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage());
        } catch (KeyStoreException e) {
            Log.d(TAG, e.getMessage());
        } catch (KeyManagementException e) {
            Log.d(TAG, e.getMessage());
        }

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .socketFactory(sslSocketFactory)
                .build();

        return httpClient;
    }
    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return null;
        try {

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
            TrustManagerFactory trustManagerFactory = null;

            trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            return trustManagers;
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage());
        } catch (CertificateException e) {
            Log.d(TAG, e.getMessage());
        } catch (KeyStoreException e) {
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return null;

    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;

            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException e) {
            Log.d(TAG, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage());
        } catch (UnrecoverableKeyException e) {
            Log.d(TAG, e.getMessage());
        } catch (CertificateException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    /**
     * 证书解释器
     */
    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
    /**
     * 创建 不认证证书的 HttpClient
     *
     * @return
     */
    public static OkHttpClient createUnSafeHttpClient() {
        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;
        X509TrustManager trustManager = new UnSafeTrustManager();
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(new UnSafeHostnameVerifier())
                .build();
        return httpClient;
    }

    /**
     * 不验证host
     */
    private static class UnSafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * 不验证证书
     */
    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }
}
