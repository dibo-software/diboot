package com.diboot.component.file.service;

import com.diboot.component.file.entity.UploadFile;
import com.diboot.core.service.BaseService;

import java.util.List;

/**
 * 基础文件Service
 * @author Lishuaifei@dibo.ltd
 * @date 2019-07-18
 */
public interface UploadFileService extends BaseService<UploadFile> {

    /**
     * 获取指定对象记录关联的上传文件列表
     * @param relObjClass
     * @param relObjId
     * @return
     */
    List<UploadFile> getUploadedFiles(String relObjClass, Long relObjId);

}