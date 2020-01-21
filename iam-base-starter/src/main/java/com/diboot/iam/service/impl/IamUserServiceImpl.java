package com.diboot.iam.service.impl;

import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamPermission;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.mapper.IamUserMapper;
import com.diboot.iam.service.IamPermissionService;
import com.diboot.iam.service.IamUserRoleService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.vo.IamRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* 系统用户相关Service实现
* @author mazc@dibo.ltd
* @version 2.0
* @date 2019-12-17
*/
@Service
@Slf4j
public class IamUserServiceImpl extends BaseIamServiceImpl<IamUserMapper, IamUser> implements IamUserService {

    @Autowired
    private IamUserRoleService iamUserRoleService;

    @Autowired
    private IamPermissionService iamPermissionService;

    @Override
    public IamRoleVO buildRoleVo4FrontEnd(IamUser iamUser) {
        List<IamRoleVO> roleVOList = getAllRoleVOList(iamUser);
        if (V.isEmpty(roleVOList)){
            return null;
        }
        // 附加额外的一些权限给与特性的角色
        attachExtraPermissions(roleVOList);
        // 对RoleList做聚合处理，以适配前端
        List<String> nameList = new ArrayList<>();
        List<IamPermission> allPermissionList = new ArrayList<>();
        roleVOList.forEach(vo -> {
            nameList.add(vo.getName());
            if (V.notEmpty(vo.getPermissionList())){
                allPermissionList.addAll(vo.getPermissionList());
            }
        });
        // 对permissionList进行去重
        List permissionList = BeanUtils.distinctByKey(allPermissionList, IamPermission::getId);
        IamRoleVO roleVO = new IamRoleVO();
        roleVO.setName(S.join(nameList));
        roleVO.setPermissionList(permissionList);

        return roleVO;
    }

    @Override
    public List<IamRoleVO> getAllRoleVOList(IamUser iamUser) {
        List<IamRole> roleList = iamUserRoleService.getUserRoleList(IamUser.class.getSimpleName(), iamUser.getId());
        if (V.isEmpty(roleList)){
            return null;
        }
        return RelationsBinder.convertAndBind(roleList, IamRoleVO.class);
    }

    @Override
    public void attachExtraPermissions(List<IamRoleVO> roleVOList) {
        if (V.isEmpty(roleVOList)){
            return;
        }
        for (IamRoleVO roleVO : roleVOList){
            if (Cons.ROLE_SUPER_ADMIN.equalsIgnoreCase(roleVO.getCode())){
                List<IamPermission> iamPermissions = iamPermissionService.getAllPermissions(Cons.APPLICATION);
                roleVO.setPermissionList(iamPermissions);
            }
        }
    }

}
