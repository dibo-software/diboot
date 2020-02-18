package com.diboot.iam.service;

import com.diboot.iam.dto.ChangePwdDTO;
import com.diboot.iam.dto.IamUserAccountDTO;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.vo.IamRoleVO;

import java.util.List;

/**
* 系统用户相关Service
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
public interface IamUserService extends BaseIamService<IamUser> {

    /**
     * 构建role-permission角色权限数据格式(合并role等)，用于前端适配
     * @param iamUser
     * @return
     */
    IamRoleVO buildRoleVo4FrontEnd(IamUser iamUser);

    /***
     * 获取用户的所有角色列表（包括扩展的关联角色）
     * @param iamUser
     * @return
     */
    List<IamRoleVO> getAllRoleVOList(IamUser iamUser);

    /***
     * 附加额外的权限（主要用于给超级管理员权限赋予所有权限）
     * @param roleVOList
     */
    void attachExtraPermissions(List<IamRoleVO> roleVOList);

    /***
     * 添加用户和账号
     * @param userAccountDTO
     * @return
     */
    boolean createUserAndAccount(IamUserAccountDTO userAccountDTO);

    /***
     * 更新用户和账号
     * @param userAccountDTO
     * @return
     */
    boolean updateUserAndAccount(IamUserAccountDTO userAccountDTO) throws Exception;

    /***
     * 删除用户和账号
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteUserAndAccount(Long id) throws Exception;

}