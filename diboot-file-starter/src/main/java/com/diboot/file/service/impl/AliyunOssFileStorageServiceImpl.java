/*
 * Copyright (c) 2015-2023, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.file.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.OSSObject;
import com.diboot.core.util.S;
import com.diboot.file.dto.UploadFileResult;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.starter.FileProperties;
import com.diboot.file.util.FileHelper;
import com.diboot.file.util.HttpHelper;
import com.diboot.iam.config.Cons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Date;


/**
 * 阿里云OSS
 *
 * @author : wind
 * @version : v2.9.0
 * @Date 2023/01/18  11:36
 */
@Service("fileStorageService")
@ConditionalOnClass(OSSClientBuilder.class)
@ConditionalOnProperty(prefix = "diboot.component.file.oss.aliyun", name = {"endpoint", "access-key-id", "access-key-secret"})
public class AliyunOssFileStorageServiceImpl implements FileStorageService {

    @Autowired
    private FileProperties fileProperties;
    private OSS ossClient;

    @PostConstruct
    public void init() {
        FileProperties.OSS.Aliyun aliyun = fileProperties.getOss().getAliyun();
        ossClient = new OSSClientBuilder().build(aliyun.getEndpoint(), aliyun.getAccessKeyId(), aliyun.getAccessKeySecret());
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    @Override
    public UploadFileResult upload(MultipartFile file) throws Exception {
        UploadFileResult uploadFileResult = new UploadFileResult();
        // 文件后缀
        String fileUid = S.newUuid();
        String ext = FileHelper.getFileExtByName(file.getOriginalFilename());
        String newFileName = fileUid + "." + ext;
        //调用oss上传返回url
        String fileFullPath = save(file.getInputStream(), fileUid + Cons.SEPARATOR_CROSSBAR + file.getOriginalFilename());
        uploadFileResult.setOriginalFilename(file.getOriginalFilename())
                .setExt(ext)
                .setUuid(fileUid)
                .setFilename(newFileName)
                .setStorageFullPath(fileFullPath);
        return uploadFileResult;
    }

    @Override
    public UploadFileResult upload(InputStream inputStream, String fileName) throws Exception {
        UploadFileResult uploadFileResult = new UploadFileResult();
        // 文件后缀
        String fileUid = S.newUuid();
        String ext = FileHelper.getFileExtByName(fileName);
        String newFileName = fileUid + "." + ext;
        //调用oss上传返回url
        String fileFullPath = save(inputStream, fileUid + Cons.SEPARATOR_CROSSBAR + fileName);
        uploadFileResult.setOriginalFilename(fileName)
                .setExt(ext)
                .setUuid(fileUid)
                .setFilename(newFileName)
                .setStorageFullPath(fileFullPath);
        return uploadFileResult;
    }

    private String save(InputStream inputStream, String fileName) {
        FileProperties.OSS.Aliyun aliyun = fileProperties.getOss().getAliyun();
        //校验bucket桶是否存在 不存在自动创建
        String bucketName = aliyun.getBucketName();
        if (!ossClient.doesBucketExist(bucketName)) {
            //创建桶
            //根据当前的认证信息来进行创建Bucket桶，默认是私有
            ossClient.createBucket(bucketName);
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            //设置读写权限为公开读
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
            ossClient.createBucket(createBucketRequest);
        }
        //上传文件
        ossClient.putObject(bucketName, fileName, inputStream);
        //把上传之后文件路径返回
        return ossClient.generatePresignedUrl(bucketName, fileName, new Date(System.currentTimeMillis() + aliyun.getExpiration() * 60 * 1000)).toString();
    }

    @Override
    public InputStream getFile(String filePath) throws Exception {
        FileProperties.OSS.Aliyun aliyun = fileProperties.getOss().getAliyun();
        //拼接云储存的文件名
        String filename = S.substringBefore(S.substringAfterLast(filePath, Cons.SEPARATOR_SLASH), "?");
        //调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息
        OSSObject ossObject = ossClient.getObject(aliyun.getBucketName(), filename);//bucketName需要自己设置
        return ossObject.getObjectContent();
    }

    @Override
    public void download(UploadFile uploadFile, HttpServletResponse response) throws Exception {
        FileProperties.OSS.Aliyun aliyun = fileProperties.getOss().getAliyun();
        String bucketName = aliyun.getBucketName();
        //拼接云储存的文件名
        String filename = uploadFile.getUuid() + Cons.SEPARATOR_CROSSBAR + uploadFile.getFileName();
        //调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息
        OSSObject ossObject = ossClient.getObject(bucketName, filename);//bucketName需要自己设置
        HttpHelper.downloadFile(ossObject.getObjectContent(), null, uploadFile.getFileName(), response);
    }

    @Override
    public boolean delete(String filePath) {
        FileProperties.OSS.Aliyun aliyun = fileProperties.getOss().getAliyun();
        ossClient.deleteObject(aliyun.getBucketName(), S.substringBefore(S.substringAfterLast(filePath, Cons.SEPARATOR_SLASH), "?"));
        return true;
    }

}

