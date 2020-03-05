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


import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            X509TrustManager trustAllCert = getX509TrustManager();
            return new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    .sslSocketFactory(new KitkatSSLSocketFactory(trustAllCert), trustAllCert)
                    .build();
        }else{
            return new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();
        }
    }

    public static X509TrustManager getX509TrustManager(){
        return   new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        };
    }
    /**
     * 创建 auth认证的 HttpClient
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public static OkHttpClient createAuthHttpClient(final String username, final String password) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            X509TrustManager trustAllCert = getX509TrustManager();
            return new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    .sslSocketFactory(new KitkatSSLSocketFactory(trustAllCert), trustAllCert)
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
        }else{
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

    }

    /**
     * 创建 固定证书的 HttpClient
     *
     * @param pattern
     * @param pins
     * @return
     */
    public static OkHttpClient createCertHttpClient(final String pattern, final String... pins) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            X509TrustManager trustAllCert = getX509TrustManager();
            return new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    .sslSocketFactory(new KitkatSSLSocketFactory(trustAllCert), trustAllCert)
                    .certificatePinner(new CertificatePinner.Builder()
                            .add(pattern, pins)
                            .build())
                    .build();
        }else{
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
            final TrustManager[] trustManagers = prepareTrustManager(certificates);
            final KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager = null;
            if (trustManagers != null) {
                trustManager = new MyTrustManager(chooseTrustManager(trustManagers));
            } else {
                trustManager = new UnSafeTrustManager();
            }
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }


        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .socketFactory(sslSocketFactory)
                .build();
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return new TrustManager[0];
        try {

            final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (final InputStream certificate : certificates) {
                final String certificateAlias = Integer.toString(index++);
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

            return trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return new TrustManager[0];

    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return  new KeyManager[0];

            final KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return new KeyManager[0];
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (final TrustManager trustManager : trustManagers) {
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
            final TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            //do nothings
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
        final X509TrustManager trustManager = new UnSafeTrustManager();
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(new UnSafeHostnameVerifier())
                .build();
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
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            //do nothings
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            //do nothings
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    //https://www.cnblogs.com/renhui/p/6591347.html
    public static class KitkatSSLSocketFactory extends SSLSocketFactory {

        private SSLSocketFactory defaultFactory;
        // Android 5.0+ (API level21) provides reasonable default settings
        // but it still allows SSLv3
        // https://developer.android.com/about/versions/android-5.0-changes.html#ssl
        static String protocols[] = null, cipherSuites[] = null;

        static {
            try {
                SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket();
                if (socket != null) {
                    /* set reasonable protocol versions */
                    // - enable all supported protocols (enables TLSv1.1 and TLSv1.2 on Android <5.0)
                    // - remove all KitkatSSLSocketFactory versions (especially SSLv3) because they're insecure now
                    List<String> protocols = new LinkedList<>();
                    for (String protocol : socket.getSupportedProtocols())
                        if (!protocol.toUpperCase().contains("KitkatSSLSocketFactory"))
                            protocols.add(protocol);
                    KitkatSSLSocketFactory.protocols = protocols.toArray(new String[protocols.size()]);
                    /* set up reasonable cipher suites */
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        // choose known secure cipher suites
                        List<String> allowedCiphers = Arrays.asList(
                                // TLS 1.2
                                "TLS_RSA_WITH_AES_256_GCM_SHA384",
                                "TLS_RSA_WITH_AES_128_GCM_SHA256",
                                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                                "TLS_ECHDE_RSA_WITH_AES_128_GCM_SHA256",
                                // maximum interoperability
                                "TLS_RSA_WITH_3DES_EDE_CBC_SHA",
                                "TLS_RSA_WITH_AES_128_CBC_SHA",
                                // additionally
                                "TLS_RSA_WITH_AES_256_CBC_SHA",
                                "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
                                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                                "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
                                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");
                        List<String> availableCiphers = Arrays.asList(socket.getSupportedCipherSuites());
                        // take all allowed ciphers that are available and put them into preferredCiphers
                        HashSet<String> preferredCiphers = new HashSet<>(allowedCiphers);
                        preferredCiphers.retainAll(availableCiphers);
                        /* For maximum security, preferredCiphers should *replace* enabled ciphers (thus disabling
                         * ciphers which are enabled by default, but have become unsecure), but I guess for
                         * the security level of DAVdroid and maximum compatibility, disabling of insecure
                         * ciphers should be a server-side task */
                        // add preferred ciphers to enabled ciphers
                        HashSet<String> enabledCiphers = preferredCiphers;
                        enabledCiphers.addAll(new HashSet<>(Arrays.asList(socket.getEnabledCipherSuites())));
                        KitkatSSLSocketFactory.cipherSuites = enabledCiphers.toArray(new String[enabledCiphers.size()]);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public KitkatSSLSocketFactory(X509TrustManager tm) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, (tm != null) ? new X509TrustManager[]{tm} : null, null);
                defaultFactory = sslContext.getSocketFactory();
            } catch (GeneralSecurityException e) {
                throw new AssertionError(); // The system has no TLS. Just give up.
            }
        }

        private void upgradeTLS(SSLSocket ssl) {
            // Android 5.0+ (API level21) provides reasonable default settings
            // but it still allows SSLv3
            // https://developer.android.com/about/versions/android-5.0-changes.html#ssl
            if (protocols != null) {
                ssl.setEnabledProtocols(protocols);
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && cipherSuites != null) {
                ssl.setEnabledCipherSuites(cipherSuites);
            }
        }

        @Override public String[] getDefaultCipherSuites() {
            return cipherSuites;
        }

        @Override public String[] getSupportedCipherSuites() {
            return cipherSuites;
        }

        @Override public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            Socket ssl = defaultFactory.createSocket(s, host, port, autoClose);
            if (ssl instanceof SSLSocket)
                upgradeTLS((SSLSocket) ssl);
            return ssl;
        }

        @Override public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            Socket ssl = defaultFactory.createSocket(host, port);
            if (ssl instanceof SSLSocket)
                upgradeTLS((SSLSocket) ssl);
            return ssl;
        }

        @Override public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            Socket ssl = defaultFactory.createSocket(host, port, localHost, localPort);
            if (ssl instanceof SSLSocket)
                upgradeTLS((SSLSocket) ssl);
            return ssl;
        }

        @Override public Socket createSocket(InetAddress host, int port) throws IOException {
            Socket ssl = defaultFactory.createSocket(host, port);
            if (ssl instanceof SSLSocket)
                upgradeTLS((SSLSocket) ssl);
            return ssl;
        }

        @Override public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            Socket ssl = defaultFactory.createSocket(address, port, localAddress, localPort);
            if (ssl instanceof SSLSocket)
                upgradeTLS((SSLSocket) ssl);
            return ssl;
        }

    }
}
