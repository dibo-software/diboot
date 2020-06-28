package com.diboot.iam.util;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamFrontendPermission;
import com.diboot.iam.vo.IamRoleVO;

import java.util.ArrayList;
import java.util.List;

/**
 * IAM相关辅助类
 *
 * @author mazc@dibo.ltd
 * @version v1.0
 * @date 2020/06/28
 */
public class IamHelper {

    /**
     * 构建role-permission角色权限数据格式(合并role等)，用于前端适配
     * @param roleVOList
     * @return
     */
    public static IamRoleVO buildRoleVo4FrontEnd(List<IamRoleVO> roleVOList) {
        if (V.isEmpty(roleVOList)){
            return null;
        }
        // 对RoleList做聚合处理，以适配前端
        List<String> nameList = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        List<IamFrontendPermission> allPermissionList = new ArrayList<>();
        roleVOList.forEach(vo -> {
            nameList.add(vo.getName());
            codeList.add(vo.getCode());
            if (V.notEmpty(vo.getPermissionList())){
                allPermissionList.addAll(vo.getPermissionList());
            }
        });
        // 对permissionList进行去重
        List permissionList = BeanUtils.distinctByKey(allPermissionList, IamFrontendPermission::getId);
        IamRoleVO roleVO = new IamRoleVO();
        roleVO.setName(S.join(nameList));
        roleVO.setCode(S.join(codeList));
        roleVO.setPermissionList(permissionList);

        return roleVO;
    }

}
