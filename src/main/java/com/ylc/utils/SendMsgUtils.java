package com.ylc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Category;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendMsgUtils {

    /**
     * 日志对象
     */
    private static final Category log = Category.getInstance(SendMsgUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送请求
     *
     * @param url       请求地址
     * @param params    发送报文
     * @param method    http交互方式 (目前仅支持Get,Post,Put,Delete四种方式)
     * @param headerMap 请求头map
     * @return json 格式的返回报文
     */
    public static String sendMsg(String params, String url, String method, ContentType contentType, Map<String, String> headerMap) throws Exception {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        PoolingHttpClientConnectionManager cm = null;
        String result;
        try {
            // 设置连接时长
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).build();
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(30000).build();
            if (url.startsWith("https")) {
                SSLContext sc;
                try {
                    sc = SSLContext.getInstance("SSL", "SunJSSE");
                } catch (Exception e) {
                    e.printStackTrace();
                    // 上述写法在非SUN提供的jdk里报错（websphere）。去掉SunJSSE，让程序自动检索
                    sc = SSLContext.getInstance("SSL");
                }
                sc.init(null, new X509TrustManager[]{new MyX509TrustManager()}, new SecureRandom());
                SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sc, NoopHostnameVerifier.INSTANCE);
                Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory()).register("https", factory).build();
                cm = new PoolingHttpClientConnectionManager(registry);
                cm.setMaxTotal(5);
                cm.setDefaultMaxPerRoute(4);
                client = HttpClients.custom().setSSLSocketFactory(factory).setConnectionManager(cm)
                    .setDefaultRequestConfig(requestConfig).setDefaultSocketConfig(socketConfig).build();
            } else {
                client = HttpClients.custom().setDefaultRequestConfig(requestConfig).setDefaultSocketConfig(socketConfig).build();
            }
            if (contentType == null) {
                contentType = ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8);
            }
            if (headerMap == null) {
                headerMap = new HashMap<>();
            }
            if (!headerMap.containsKey("Content-Type")) {
                headerMap.put("Content-Type", contentType.toString());
            }
            if (!headerMap.containsKey("Accept")) {
                headerMap.put("Accept", contentType.toString());
            }
            if (!headerMap.containsKey("Accept-Charset")) {
                headerMap.put("Accept-Charset", contentType.getCharset().toString());
            }
            params = StringUtils.trimToEmpty(params);
            if (StringUtils.isNotBlank(method)) {
                // GET请求
                if (StringUtils.equalsIgnoreCase("GET", method)) {
                    HttpGet get = new HttpGet(url);
                    for (String key : headerMap.keySet()) {
                        get.addHeader(key, headerMap.get(key));
                    }
                    response = client.execute(get);
                }
                // POST请求
                else if (StringUtils.equalsIgnoreCase("POST", method)) {
                    HttpPost post = new HttpPost(url);
                    for (String key : headerMap.keySet()) {
                        post.addHeader(key, headerMap.get(key));
                    }
                    setEntity(params, contentType, post);
                    response = client.execute(post);
                }
                // PUT请求
                else if (StringUtils.equalsIgnoreCase("PUT", method)) {
                    HttpPut put = new HttpPut(url);
                    for (String key : headerMap.keySet()) {
                        put.addHeader(key, headerMap.get(key));
                    }
                    setEntity(params, contentType, put);
                    response = client.execute(put);
                }
                // DELETE请求
                else if (StringUtils.equalsIgnoreCase("DELETE", method)) {
                    MyHttpDelete delete = new MyHttpDelete(url);
                    for (String key : headerMap.keySet()) {
                        delete.addHeader(key, headerMap.get(key));
                    }
                    setEntity(params, contentType, delete);
                    response = client.execute(delete);
                } else {
                    throw new Exception("interface.util.requesttypeerror");
                }
            } else {
                throw new Exception("interface.util.requesttypisnull");
            }
            result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("sendMsg异常！", e);
            throw e;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("response关闭异常！", e);
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    log.error("client关闭异常！", e);
                }
            }
            if (cm != null) {
                cm.close();
            }
        }
        return result;
    }

    public static String sendMsg(String params, String url, String method, ContentType contentType) throws Exception {
        return sendMsg(params, url, method, contentType, null);
    }

    public static String sendMsg(String params, String url, String method) throws Exception {
        return sendMsg(params, url, method, null, null);
    }

    public static String sendMsg(String params, String url) throws Exception {
        return sendMsg(params, url, "POST", null, null);
    }

    /**
     * 设置请求体
     *
     * @param params
     * @param contentType
     * @param requestBase
     */
    private static void setEntity(String params, ContentType contentType, HttpEntityEnclosingRequestBase requestBase) throws IOException {
        if (contentType.getMimeType().equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
            List<BasicNameValuePair> list = new ArrayList<>();
            Map map = objectMapper.readValue(params, Map.class);
            for (Object key : map.keySet()) {
                list.add(new BasicNameValuePair((String) key, (String) map.get(key)));
            }
            requestBase.setEntity(new UrlEncodedFormEntity(list, StandardCharsets.UTF_8));
        } else if (contentType.getMimeType().equals(ContentType.MULTIPART_FORM_DATA.getMimeType())) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setContentType(contentType);
            Map map = objectMapper.readValue(params, Map.class);
            for (Object key : map.keySet()) {
                builder.addTextBody((String) key, (String) map.get(key));
            }
            HttpEntity entity = builder.build();
            requestBase.setHeader(entity.getContentType());
            requestBase.setEntity(entity);
        } else {
            requestBase.setEntity(new StringEntity(params, StandardCharsets.UTF_8));
        }
    }

    private static class MyX509TrustManager implements X509TrustManager {
        public MyX509TrustManager() {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] ax509certificate, String s) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] ax509certificate, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    /**
     * HttpClient自带的HttpDelete方法是不支持上传body的，所以重写delete方法
     */
    private static class MyHttpDelete extends HttpEntityEnclosingRequestBase {

        public static final String METHOD_NAME = "DELETE";

        public MyHttpDelete(final String uri) {
            super();
            setURI(URI.create(uri));
        }

        public MyHttpDelete(final URI uri) {
            super();
            setURI(uri);
        }

        public MyHttpDelete() {
            super();
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }
}
