package com.diboot.file.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.file.entity.UploadFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件相关Mapper
 * @author Mazc
 * @version 2017/4/18
 */
@Mapper
public interface UploadFileMapper extends BaseCrudMapper<UploadFile> {

}
