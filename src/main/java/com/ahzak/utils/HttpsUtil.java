package com.ahzak.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Slf4j
public class HttpsUtil {
    private static HttpsUtil httpUtil;
    private static RequestConfig config;
    private static final Set<String> TRUST_HOSTNAME = new HashSet<>(Arrays.asList(
            "124.225.144.70"
    ));


    public static HttpsUtil createHttpClientUtil(int connTimeout, int reqTimeout) {
        config = RequestConfig.custom().setConnectTimeout(connTimeout).setSocketTimeout(reqTimeout).build();
        httpUtil = new HttpsUtil();
        return httpUtil;
    }

    private HttpsUtil() {
    }

    public static String requestWithHttpsWithP12(String url, String reqXml, String mchId, String cerPath) {
        try {
            ContentType contentType = ContentType.create("application/xml", "UTF-8");
            HttpsUtil httpClient = createHttpClientUtil(8000, 8000);
            return httpClient.invokeRequestWithP12(url, reqXml, true, contentType, mchId, cerPath);
        } catch (Exception e) {
            log.error("执行http请求异常:", e);
        }

        return null;
    }

    /**
     * 执行后台Http请求
     *
     * @param reqUrl      请求URl
     * @param params      请求参数
     * @param contentType contentType
     * @param sslFlg      是否需要ssl加密
     * @return
     */
    public String invokeRequestWithP12(String reqUrl, String params, boolean sslFlg,
                                       ContentType contentType, String mchId, String cerPath) {

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(config);
        if (sslFlg) {
            try {
                KeyStore keyStore = initCert(mchId, cerPath);
                httpClientBuilder.setSSLSocketFactory(getSf(keyStore, mchId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CloseableHttpClient httpClient = httpClientBuilder.build();


//        if (sslFlg) {
//            try {
//                registerSSlp12(httpClient, keyStore, mchId);
//            } catch (NoSuchAlgorithmException e) {
//                log.error("注册SSL失败", e);
//            } catch (KeyManagementException e) {
//                log.error("注册SSL失败", e);
//            }
//        }
        HttpPost httpPost = new HttpPost(reqUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(config);
            httpPost.setEntity(new StringEntity(params, contentType));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String value = EntityUtils.toString(entity, contentType.getCharset());
            EntityUtils.consume(entity);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("http请求异常", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("response IOException", e);
                }
            }
            httpPost.releaseConnection();
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("httpClient IOException", e);
            }
        }

        return null;
    }


    /**
     * 加载证书
     *
     * @param mchId    商户ID
     * @param certPath 证书位置
     * @throws Exception
     */
    private static KeyStore initCert(String mchId, String certPath) throws Exception {

        // 证书密码，默认为商户ID
        String key = mchId;
        // 指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 从classpath下读取存放的PKCS12证书文件
        String currentPath = Thread.currentThread().getContextClassLoader().getResource(certPath).getPath();
        log.info("路径:{}", currentPath);

        try (FileInputStream instream = new FileInputStream(new File(currentPath))) {
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, key.toCharArray());
        } catch (Exception e) {
            log.error("keyStoreException", e);
        }
        return keyStore;
    }


//    private static void registerSSlp12(HttpClient httpclient, KeyStore keyStore, String mch) throws NoSuchAlgorithmException,
//            KeyManagementException {
//        SSLContext sslcontext = SSLContext.getInstance("SSL");
//        X509TrustManager tm = new X509TrustManager() {
//            @Override
//            public void checkClientTrusted(X509Certificate[] arg0,
//                                           String arg1) throws CertificateException {
//            }
//
//            @Override
//            public void checkServerTrusted(X509Certificate[] arg0,
//                                           String arg1) throws CertificateException {
//            }
//
//            @Override
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//        };
//        String alg = KeyManagerFactory.getDefaultAlgorithm();
//        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(alg);
//        try {
//            keyManagerFactory.init(keyStore, mch.toCharArray());
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableKeyException e) {
//            e.printStackTrace();
//        }
//        KeyManager[] kms = keyManagerFactory.getKeyManagers();
//
//        sslcontext.init(kms, new TrustManager[]{tm}, null);
//        SSLSocketFactory sf = new SSLSocketFactory(sslcontext,
//                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//        Scheme https = new Scheme("https", 443, sf);
//        httpclient.getConnectionManager().getSchemeRegistry().register(https);
//    }


    private static SSLConnectionSocketFactory getSf(KeyStore keyStore, String mch) throws NoSuchAlgorithmException,
            KeyManagementException, UnrecoverableKeyException, KeyStoreException {
        SSLContext sslcontext = SSLContext.getInstance("SSL");
        X509TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(alg);
        keyManagerFactory.init(keyStore, mch.toCharArray());
        KeyManager[] kms = keyManagerFactory.getKeyManagers();

        sslcontext.init(kms, new TrustManager[]{tm}, null);
        SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(sslcontext, (hostname, sslSession) -> {
            if (TRUST_HOSTNAME.contains(hostname)) {
                return true;
            } else {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify(hostname, sslSession);
            }
        });
        return sf;
}


    public static void main(String[] args) {
        String v = createHttpClientUtil(10000, 10000)
                .invokeRequestWithP12(
                        "https://124.225.144.70:8016/shixiang/catalog",
                        "",
                        true,
                        ContentType.DEFAULT_TEXT,
                        "tyTvzP6Zvmjjb3Lnrhjiq09F",
                        "pp.p12");


        System.out.println(v);
    }
}







