package com.diboot.example.util;

import com.diboot.core.config.BaseConfig;
import com.diboot.core.util.S;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author wangyongliang
 * @version 2019/7/4
 */
public class QiniuHelper {
    private static final Logger logger = LoggerFactory.getLogger(QiniuHelper.class);

    private static String accessKey = BaseConfig.getProperty("qiniu.key.access");
    private static String secretKey = BaseConfig.getProperty("qiniu.key.secret");
    private static String bucket = BaseConfig.getProperty("qiniu.bucket.name");
    private static String imageDomain = BaseConfig.getProperty("qiniu.image.domain");

    public static String getAccessKey(){
        return accessKey;
    }
    public static String getSecretKey(){
        return secretKey;
    }
    public static String getBucket(){
        return bucket;
    }

    /***
     * 上传文件到七牛
     * @param fileName
     * @return
     */
    public static String uploadFile2Qiniu(String fileName, String savePath){
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        String localImageFullPath = savePath+fileName;
        try {
            Response response = uploadManager.put(localImageFullPath, S.newUuid(), upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            // 返回全路径URL
            if(putRet.key != null){
                //删除零时文件
                File wavFile = new File(localImageFullPath);
                if(wavFile.exists()){
                    wavFile.delete();
                }
                return imageDomain + putRet.key;
            }
        }
        catch (QiniuException ex) {
            Response r = ex.response;
            logger.error(r.toString());
            try {
                logger.error(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
        return fileName;
    }
}
