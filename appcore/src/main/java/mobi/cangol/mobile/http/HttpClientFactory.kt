/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.http


import mobi.cangol.mobile.logging.Log
import okhttp3.CertificatePinner
import okhttp3.Credentials
import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

object HttpClientFactory {
    private const val TAG = "HttpClientFactory"
    private const val DEFAULT_CONNECT_TIMEOUT = 20 * 1000
    private const val DEFAULT_READ_TIMEOUT = 20 * 1000
    private const val DEFAULT_WRITE_TIMEOUT = 20 * 1000

    /**
     * 创建默的 HttpClient
     *
     * @return
     */
    fun createDefaultHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .build()

    }

    /**
     * 创建 auth认证的 HttpClient
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    fun createAuthHttpClient(username: String, password: String): OkHttpClient {

        return OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .authenticator { _, response ->
                    val credential = Credentials.basic(username, password)
                    response.request().newBuilder()
                            .header("Authorization", credential)
                            .build()
                }
                .build()
    }

    /**
     * 创建 固定证书的 HttpClient
     *
     * @param pattern
     * @param pins
     * @return
     */
    fun createCertHttpClient(pattern: String, vararg pins: String): OkHttpClient {

        return OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .certificatePinner(CertificatePinner.Builder()
                        .add(pattern, *pins)
                        .build())
                .build()
    }

    /**
     * 创建 auth认证的 HttpClient
     *
     * @param certificates
     * @param bksFile
     * @param password
     * @return
     */
    fun createSafeHttpClient(certificates: Array<InputStream>, bksFile: InputStream, password: String): OkHttpClient {
        var sslContext: SSLContext?
        var sslSocketFactory: SSLSocketFactory? = null
        try {
            val trustManagers = prepareTrustManager(*certificates)
            val keyManagers = prepareKeyManager(bksFile, password)
            sslContext = SSLContext.getInstance("TLS")
            var trustManager: X509TrustManager?
            trustManager = if (trustManagers != null) {
                MyTrustManager(chooseTrustManager(trustManagers))
            } else {
                UnSafeTrustManager()
            }
            sslContext!!.init(keyManagers, arrayOf<TrustManager>(trustManager), SecureRandom())
            sslSocketFactory = sslContext.socketFactory

        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }


        return OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .socketFactory(sslSocketFactory!!)
                .build()
    }

    private fun prepareTrustManager(vararg certificates: InputStream): Array<TrustManager?> {
        if (certificates == null || certificates.isEmpty()) return arrayOfNulls(0)
        try {

            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            for ((index, certificate) in certificates.withIndex()) {
                val certificateAlias = (index).toString()
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate))
                try {
                    certificate.close()
                } catch (e: IOException) {
                    Log.d(TAG, e.message)
                }

            }
            var trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

            trustManagerFactory.init(keyStore)

            return trustManagerFactory.trustManagers
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }

        return arrayOfNulls(0)

    }

    private fun prepareKeyManager(bksFile: InputStream?, password: String?): Array<KeyManager?> {
        try {
            if (bksFile == null || password == null) return arrayOfNulls(0)

            val clientKeyStore = KeyStore.getInstance("BKS")
            clientKeyStore.load(bksFile, password.toCharArray())
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(clientKeyStore, password.toCharArray())
            return keyManagerFactory.keyManagers

        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }

        return arrayOfNulls(0)
    }

    private fun chooseTrustManager(trustManagers: Array<TrustManager?>): X509TrustManager? {
        for (trustManager in trustManagers) {
            if (trustManager is X509TrustManager) {
                return trustManager
            }
        }
        return null
    }

    /**
     * 证书解释器
     */
    private class MyTrustManager @Throws(NoSuchAlgorithmException::class, KeyStoreException::class)
    constructor(private val localTrustManager: X509TrustManager?) : X509TrustManager {
        private val defaultTrustManager: X509TrustManager?

        init {
            val var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            var4.init(null as KeyStore?)
            defaultTrustManager = chooseTrustManager(var4.trustManagers)
        }


        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            //do nothings
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                defaultTrustManager?.checkServerTrusted(chain, authType)
            } catch (ce: CertificateException) {
                localTrustManager?.checkServerTrusted(chain, authType)
            }

        }


        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    /**
     * 创建 不认证证书的 HttpClient
     *
     * @return
     */
    fun createUnSafeHttpClient(): OkHttpClient {
        var sslContext: SSLContext?
        var sslSocketFactory: SSLSocketFactory? = null
        val trustManager = UnSafeTrustManager()
        try {
            sslContext = SSLContext.getInstance("TLS")
            sslContext!!.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
            sslSocketFactory = sslContext.socketFactory
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }

        return OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true)
                .readTimeout(DEFAULT_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslSocketFactory!!, trustManager)
                .hostnameVerifier(UnSafeHostnameVerifier())
                .build()
    }

    /**
     * 不验证host
     */
    private class UnSafeHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }

    /**
     * 不验证证书
     */
    private class UnSafeTrustManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            //do nothings
        }

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            //do nothings
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }
}
