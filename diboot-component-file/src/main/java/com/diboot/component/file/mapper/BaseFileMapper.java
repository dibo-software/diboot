package com.diboot.component.file.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.diboot.component.file.entity.BaseFile;
import com.diboot.core.mapper.BaseCrudMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文件相关Mapper
 * @author Mazc
 * @version 2017/4/18
 */
public interface BaseFileMapper extends BaseCrudMapper<BaseFile> {

    @Select("SELECT * FROM file ${ew.customSqlSegment}")
    BaseFile getModel(@Param(Constants.WRAPPER) Wrapper<BaseFile> wrapper);

    int updateModel(@Param("m") BaseFile model);

}
