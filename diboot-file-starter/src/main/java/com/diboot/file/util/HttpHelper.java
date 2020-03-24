package com.diboot.file.util;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private static Map<String, String> EXT_CONTENT_TYPE_MAP = new HashMap(){{
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
    }};

    /**
     * 调用Http Get请求
     * @param url
     * @return
     */
    public static String callHttpGet(String url){
        return callHttpGet(url, null);
    }

    /**
     * 调用Http Get请求
     * @param url
     * @return
     */
    public static String callHttpGet(String url, Map<String, String> headerMap){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(url);
        if(V.notEmpty(headerMap)){
            for(Map.Entry<String, String> entry : headerMap.entrySet()){
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Call call = okHttpClient.newCall(builder.build());
        return executeCall(call, url);
    }

    /**
     * 发送HTTP Post请求
     * @param url
     * @return
     */
    public static String callHttpPost(String url, Map<String, String> formBody){
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if(V.notEmpty(formBody)){
            for(Map.Entry<String, String> entry : formBody.entrySet()){
                bodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().url(url)
                .post(bodyBuilder.build())
                .build();
        Call call = okHttpClient.newCall(request);
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
                log.warn("请求调用异常 : " + url);
                return null;
            }
            return response.body().string();
        } catch (IOException e) {
            log.warn("请求调用解析异常 : " + url, e);
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
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try{
            String fileName = new String(exportFileName.getBytes("utf-8"), "ISO8859-1");
            long fileLength = new File(localFilePath).length();
            response.setContentType(getContextType(fileName));
            response.setHeader("Content-disposition", "attachment; filename="+ fileName);
            response.setHeader("Content-Length", String.valueOf(fileLength));
            response.setHeader("filename", URLEncoder.encode(exportFileName, StandardCharsets.UTF_8.name()));
            bis = new BufferedInputStream(new FileInputStream(localFilePath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        }
        catch (Exception e) {
            log.error("下载导出文件失败:"+localFilePath, e);
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
        String ext = S.substringAfterLast(fileName, ".");
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
            throw new BusinessException(Status.FAIL_VALIDATION, "未指定文件名！");
        }
        // 解析上传文件
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(!isMultipart){
            throw new BusinessException(Status.FAIL_VALIDATION, "无有效的上传文件！");
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
