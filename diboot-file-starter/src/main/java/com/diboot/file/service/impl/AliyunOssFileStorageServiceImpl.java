/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.file.config.FileProperties;
import com.diboot.file.entity.FileRecord;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.util.FileHelper;
import com.diboot.file.util.HttpHelper;
import com.diboot.iam.config.Cons;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;


/**
 * 阿里云OSS
 *
 * @author : wind
 * @version : v3.0.0
 * @Date 2023/01/18  11:36
 */
@Service("fileStorageService")
@ConditionalOnClass(OSSClientBuilder.class)
@ConditionalOnProperty(prefix = "diboot.file.oss.aliyun", name = {"endpoint", "access-key-id", "access-key-secret"})
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
    public FileRecord save(MultipartFile file) throws Exception {
        FileRecord fileRecord = new FileRecord(S.newUuid());
        // 文件后缀
        String ext = FileHelper.getFileExtByName(file.getOriginalFilename());
        //调用oss上传返回url
        String fileFullPath = save(file.getInputStream(), fileRecord.getId() + Cons.SEPARATOR_CROSSBAR + file.getOriginalFilename());
        return fileRecord.setFileName(file.getOriginalFilename())
                .setFileType(ext)
                .setFileSize(file.getSize())
                .setStoragePath(fileFullPath)
                .setAccessUrl(fileFullPath);
    }

    @Override
    public FileRecord save(InputStream inputStream, String fileName, long size) throws Exception {
        FileRecord fileRecord = new FileRecord(S.newUuid());
        // 文件后缀
        String ext = FileHelper.getFileExtByName(fileName);
        String fileFullPath = save(inputStream, fileRecord.getId() + Cons.SEPARATOR_CROSSBAR + fileName);
        return fileRecord
                .setFileName(fileName)
                .setFileType(ext)
                .setFileSize(size)
                .setStoragePath(fileFullPath)
                .setAccessUrl(fileFullPath);
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
        // 截取云储存的文件完整路径
        String filename = S.substringAfter(S.substringBefore(filePath, "?"), aliyun.getEndpoint() + "/");
        // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息
        OSSObject ossObject = ossClient.getObject(aliyun.getBucketName(), URLDecoder.decode(filename, StandardCharsets.UTF_8)); //bucketName需要自己设置
        return ossObject.getObjectContent();
    }

    @Override
    public void download(FileRecord fileRecord, HttpServletResponse response) throws Exception {
        FileProperties.OSS.Aliyun aliyun = fileProperties.getOss().getAliyun();
        String bucketName = aliyun.getBucketName();
        //拼接云储存的文件名
        String filename = fileRecord.getId() + Cons.SEPARATOR_CROSSBAR + fileRecord.getFileName();
        //调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息
        OSSObject ossObject = ossClient.getObject(bucketName, filename);//bucketName需要自己设置
        HttpHelper.downloadFile(ossObject.getObjectContent(), fileRecord.getFileSize(), fileRecord.getFileName(), response);
    }

    @Override
    public boolean delete(String filePath) {
        FileProperties.OSS.Aliyun aliyun = fileProperties.getOss().getAliyun();
        String filename = S.substringAfter(S.substringBefore(filePath, "?"), aliyun.getEndpoint() + "/");
        ossClient.deleteObject(aliyun.getBucketName(), URLDecoder.decode(filename, StandardCharsets.UTF_8));
        return true;
    }

}

