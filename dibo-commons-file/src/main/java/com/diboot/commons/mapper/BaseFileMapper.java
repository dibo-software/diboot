package com.diboot.commons.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.diboot.commons.entity.BaseFile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Mazc@com.ltd
 * @version 2017/4/18
 *
 */
public interface BaseFileMapper extends BaseMapper<BaseFile> {

    @Select("SELECT * FROM file ${ew.customSqlSegment}")
    BaseFile getModel(@Param(Constants.WRAPPER) Wrapper<BaseFile> wrapper);

    int updateModel(@Param("m") BaseFile model);

}
