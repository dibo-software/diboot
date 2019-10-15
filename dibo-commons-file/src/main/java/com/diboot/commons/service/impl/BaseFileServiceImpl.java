package com.diboot.commons.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.commons.entity.BaseFile;
import com.diboot.commons.mapper.BaseFileMapper;
import com.diboot.commons.service.BaseFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Lishuaifei
 * @description 文件下载实现类
 * @creatime 2019-07-18 15:29
 */
@Service
public class BaseFileServiceImpl extends BaseServiceImpl<BaseFileMapper, BaseFile> implements BaseFileService {

    @Autowired
    private BaseFileMapper baseFileMapper;

    @Override
    public BaseFile getModel(Wrapper<BaseFile> wrapper) {
        return baseFileMapper.getModel(wrapper);
    }

    @Override
    public boolean updateModel(BaseFile model) {
        return baseFileMapper.updateModel(model)>0? true:false;
    }
}
