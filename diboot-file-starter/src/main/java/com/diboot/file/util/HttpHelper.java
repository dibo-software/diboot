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
package com.diboot.file.util;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求相关工具类
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/02/18
 */
@Slf4j
public class HttpHelper {

    /***
     * 默认contextType
     */
    private static final String DEFAULT_CONTEXT_TYPE = "application/octet-stream";
    /***
     * 文件扩展名-ContentType的对应关系
     */
    private static final Map<String, String> EXT_CONTENT_TYPE_MAP = new HashMap(){{
        put("", "text/plain"); //无后缀，默认为文本
        put("xls", "application/x-msdownload");
        put("xlsx", "application/x-msdownload");
        put("doc", "application/x-msdownload");
        put("docx", "application/x-msdownload");
        put("dot", "application/x-msdownload");
        put("ppt", "application/x-msdownload");
        put("pptx", "application/x-msdownload");
        put("pdf", "application/pdf");
        put("avi", "video/avi");
        put("bmp", "application/x-bmp");
        put("css", "text/css");
        put("dll", "application/x-msdownload");
        put("dtd", "text/xml");
        put("exe", "application/x-msdownload");
        put("gif", "image/gif");
        put("htm", "text/html");
        put("html", "text/html");
        put("ico", "image/x-icon");
        put("jpeg", "image/jpeg");
        put("jpg", "image/jpeg");
        put("js", "application/x-javascript");
        put("mp3", "audio/mp3");
        put("mp4", "video/mpeg4");
        put("png", "image/png");
        put("svg", "text/xml");
        put("swf", "application/x-shockwave-flash");
        put("tif", "image/tiff");
        put("tiff", "image/tiff");
        put("tld", "text/xml");
        put("torrent", "application/x-bittorrent");
        put("tsd", "text/xml");
        put("txt", "text/plain");
        put("wav", "audio/wav");
        put("wma", "audio/x-ms-wma");
        put("wmf", "application/x-wmf");
        put("wsdl", "text/xml");
        put("xhtml", "text/html");
        put("xml", "text/xml");
        put("xsd", "text/xml");
        put("xsl", "text/xml");
        put("xslt", "text/xml");
        put("apk", "application/vnd.android.package-archive");
        put("zip", "application/x-zip-compressed");
        put("rar", "application/octet-stream");
    }};

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
     * 根据文件路径下载服务器本地文件
     * @param localFilePath 本地文件路径
     * @param exportFileName 导出文件的文件名
     * @param response
     * @throws Exception
     */
    public static void downloadLocalFile(String localFilePath, String exportFileName, HttpServletResponse response) throws Exception{
        downloadLocalFile(new File(localFilePath), exportFileName, response);
    }

    /**
     * 根据文件对象下载服务器文件
     * @param localFile 本地文件对象
     * @param exportFileName 导出文件的文件名
     * @param response
     * @throws Exception
     */
    public static void downloadLocalFile(File localFile, String exportFileName, HttpServletResponse response) throws Exception {
        downloadFile(Files.newInputStream(localFile.toPath()), localFile.length(), exportFileName, response);
    }

    public static void downloadFile(InputStream inputStream, long fileLength, String exportFileName, HttpServletResponse response) throws Exception{
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try{
            String fileName = new String(exportFileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            response.setContentType(getContextType(fileName));
            response.setHeader("Content-disposition", "attachment; filename="+ fileName);
            response.setHeader("Content-Length", String.valueOf(fileLength));
            response.setHeader("filename", URLEncoder.encode(exportFileName, StandardCharsets.UTF_8.name()));
            bis = new BufferedInputStream(inputStream);
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        }
        catch (Exception e) {
            log.error("下载文件失败:{}", exportFileName, e);
        }
        finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }

    /****
     * HTTP下载文件
     * @param fileUrl
     * @param targetFilePath
     * @return
     */
    public static boolean downloadHttpFile(String fileUrl, String targetFilePath) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(fileUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    FileUtils.copyInputStreamToFile(response.body().byteStream(), new File(targetFilePath));
                }
            }
        });
        return true;
    }

    /**
     * 根据文件类型获取ContentType
     * @param fileName
     * @return
     */
    public static String getContextType(String fileName){
        String ext = FileHelper.getFileExtByName(fileName);
        String contentType = EXT_CONTENT_TYPE_MAP.get(ext);
        if(contentType == null){
            contentType = DEFAULT_CONTEXT_TYPE;
        }
        return contentType + ";charset=utf-8";
    }

    /***
     * 获取请求中的多个文件数据
     * @param request
     * @param fileInputName
     * @return
     */
    public static List<MultipartFile> getFilesFromRequest(HttpServletRequest request, String fileInputName){
        // 获取附件文件名
        if(fileInputName == null){
            throw new BusinessException(Status.FAIL_VALIDATION, "exception.business.httpHelper.noFilename");
        }
        // 解析上传文件
        boolean isMultipart = request.getContentType() != null && request.getContentType().contains("multipart");
        if(!isMultipart){
            throw new BusinessException(Status.FAIL_VALIDATION, "exception.business.httpHelper.invalidFiles");
        }
        // 解析上传文件
        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles(fileInputName);
        return files;
    }

    /***
     * 获取请求中的单个文件数据
     * @param request
     * @param fileInputName
     * @return
     */
    public static MultipartFile getFileFromRequest(HttpServletRequest request, String fileInputName){
        // 解析上传文件
        List<MultipartFile> files = getFilesFromRequest(request, fileInputName);
        return V.notEmpty(files)? files.get(0) : null;
    }

}
