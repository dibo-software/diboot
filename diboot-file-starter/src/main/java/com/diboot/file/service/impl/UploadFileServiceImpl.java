package com.diboot.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.file.entity.UploadFile;
import com.diboot.file.mapper.UploadFileMapper;
import com.diboot.file.service.UploadFileService;
import com.diboot.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件下载实现类
 * @author Lishuaifei@dibo.ltd
 * @date 2019-07-18
 */
@Service
public class UploadFileServiceImpl extends BaseServiceImpl<UploadFileMapper, UploadFile> implements UploadFileService {

    @Override
    public List<UploadFile> getUploadedFiles(String relObjClass, Long relObjId) {
        LambdaQueryWrapper<UploadFile> queryWrapper = new QueryWrapper<UploadFile>().lambda()
                .eq(UploadFile::getRelObjType, relObjClass)
                .eq(UploadFile::getRelObjId, relObjId)
                .orderByAsc(UploadFile::getCreateTime);
        return getEntityList(queryWrapper);
    }

}
