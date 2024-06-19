/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.util;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * HTTP请求相关工具类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/02/18
 */
@Slf4j
public class HttpHelper extends com.diboot.core.util.HttpHelper {

    /**
     * 调用Http Get请求
     * @param url
     * @return
     */
    public static String callGet(String url, Map<String, String> headerMap) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if(S.startsWithIgnoreCase(url, "https://")){
            withHttps(clientBuilder);
        }
        Request.Builder builder = new Request.Builder().url(url);
        if(V.notEmpty(headerMap)){
            for(Map.Entry<String, String> entry : headerMap.entrySet()){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Call call = clientBuilder.build().newCall(builder.build());
        return executeCall(call, url);
    }


    /**
     * 调用Http Post请求
     * @param url
     * @return
     */
    public static Response callPostResponse(String url, Map<String, String> formBody) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if(S.startsWithIgnoreCase(url, "https://")){
            withHttps(clientBuilder);
        }
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if(V.notEmpty(formBody)){
            for(Map.Entry<String, String> entry : formBody.entrySet()){
                bodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().url(url)
                .post(bodyBuilder.build())
                .build();
        Call call = clientBuilder.build().newCall(request);
        try {
            Response response = call.execute();
            // 判断状态码
            if(response.code() >= 400){
                log.warn("请求调用异常 : {}", url);
                return null;
            }
            return response;
        } catch (IOException e) {
            log.warn("请求调用解析异常 : {}", url, e);
            return null;
        }
    }

    /**
     * 调用Http Post请求
     * @param url
     * @return
     */
    public static String callPost(String url, Map<String, String> formBody) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if(S.startsWithIgnoreCase(url, "https://")){
            withHttps(clientBuilder);
        }
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if(V.notEmpty(formBody)){
            for(Map.Entry<String, String> entry : formBody.entrySet()){
                bodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().url(url)
                .post(bodyBuilder.build())
                .build();
        Call call = clientBuilder.build().newCall(request);
        return executeCall(call, url);
    }


    /**
     * 执行调用 返回结果
     * @param call
     * @param url
     * @return
     */
    private static String executeCall(Call call, String url){
        try {
            Response response = call.execute();
            // 判断状态码
            if(response.code() >= 400){
                log.warn("请求调用异常 : {}", url);
                return null;
            }
            return response.body().string();
        } catch (Exception e) {
            log.warn("请求调用解析异常 : {}", url, e);
            return null;
        }
    }

    /**
     * 嵌入Https
     * @param builder
     */
    public static void withHttps(OkHttpClient.Builder builder) {
        try {
            TrustManager[] trustManagers = buildTrustManagers();
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManagers[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        }
        catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.warn("构建https请求异常", e);
        }
    }

    /**
     * 构建TrustManager
     * @return
     */
    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }
                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
        };
    }

}
