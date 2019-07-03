package com.diboot.example.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.example.entity.SysUser;
import org.springframework.stereotype.Component;

/**
 * 用户Mapper
 * @author Yangzhao
 * @version v2.0
 * @date 2019/6/6
 */
@Component("sysUserMapper1")
public interface SysUserMapper extends BaseCrudMapper<SysUser> {

}

