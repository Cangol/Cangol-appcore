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

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

public class HttpClientFactory {
    private static final int DEFAULT_MAX_CONNECTIONS = 20;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;
    private static final int DEFAULT_SOCKET_TIMEOUT = 20 * 1000;
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static DefaultHttpClient httpClient;
    private static IdleConnectionMonitorThread monitorThread;
    private static boolean sslVerifiy = false;

    public static DefaultHttpClient getDefaultHttpClient() {
        DefaultHttpClient httpClient = createDefaultHttpClient();
        return httpClient;

    }

    public static void setSslVerifiy(boolean sslVerifiy) {
        HttpClientFactory.sslVerifiy = sslVerifiy;
    }

    public synchronized static DefaultHttpClient getThreadSafeClient() {

        if (httpClient != null){
            return httpClient;
        }

        httpClient = createDefaultHttpClient();
        monitorThread = new IdleConnectionMonitorThread(httpClient.getConnectionManager());
        monitorThread.start();
        return httpClient;
    }

    public static void shutdown() {
        httpClient.getConnectionManager().shutdown();
        monitorThread.shutdown();
    }

    public static DefaultHttpClient createDefaultHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_MAX_CONNECTIONS_PER_ROUTE));
        ConnManagerParams.setTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, String.format("android"));
        HttpProtocolParams.setContentCharset(httpParams, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(httpParams, true);

        HttpConnectionParams.setTcpNoDelay(httpParams, true);//no delay
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);//--30ms


        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);
        HttpClientParams.setRedirecting(httpParams, true);


        //强安全的ssl 自命名证书无法通过，须使用android信任的证书 Verisign颁发的
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        SSLSocketFactory socketFactory = null;
        if (sslVerifiy) {
            /**
             KeyStore trustStore = null;
             InputStream is =null;
             try {
             trustStore = KeyStore.getInstance("BKS");
             is= this.getAssets().open("discretio.bks");
             trustStore.load(is, "discretio".toCharArray());
             is.close();
             } catch (IOException e) {
             e.printStackTrace();
             } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
             } catch (CertificateException e) {
             e.printStackTrace();
             } catch (KeyStoreException e) {
             e.printStackTrace();
             }**/

            hostnameVerifier = SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
            socketFactory = SSLSocketFactory.getSocketFactory();
        } else {
            hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            socketFactory = MySSLSocketFactory.getSocketFactory();
        }
        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", socketFactory, 443));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

        DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, cm.getSchemeRegistry()), httpParams);

        // Set verifier
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        httpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_MAX_RETRIES));

        //set proxy
//		httpClient.setRoutePlanner(new HttpRoutePlanner() {
//			public HttpRoute determineRoute(HttpHost target,
//					HttpRequest request, HttpContext context)
//					throws HttpException {
//				return new HttpRoute(target, null, new HttpHost("8.8.8.8",8080), "https".equalsIgnoreCase(target.getSchemeName()));
//			}
//		});

        //DefaultRequestDirector:line 464 keepAlive==idletime
        httpClient.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                long keepAlive = super.getKeepAliveDuration(response, context);
                if (keepAlive == -1) {
                    return keepAlive;
                }
                return 5000;
            }
        });
        return httpClient;
    }

    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return wrappedEntity.getContentLength();
        }
    }
}
