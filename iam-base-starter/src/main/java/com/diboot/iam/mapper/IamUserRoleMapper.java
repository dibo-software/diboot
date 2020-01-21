package com.diboot.iam.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* 用户角色关联Mapper
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Mapper
public interface IamUserRoleMapper extends BaseCrudMapper<IamUserRole> {

    /**
     * 获取用户所有的全部角色
     * @param userType
     * @param userId
     * @return
     */
    List<IamRole> getUserRoleList(@Param("userType") String userType, @Param("userId") Long userId);

}

