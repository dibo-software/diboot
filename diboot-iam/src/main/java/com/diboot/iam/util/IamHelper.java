/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.util;

import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.vo.IamRoleVO;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * IAM相关辅助类
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/06/28
 */
@Slf4j
public class IamHelper {

    /***
     * 构建请求参数Map
     * @return
     */
    @Deprecated
    public static Map<String, Object> buildParamsMap(HttpServletRequest request) {
        return HttpHelper.buildParamsMap(request);
    }

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
        List<IamResourcePermission> allPermissionList = new ArrayList<>();
        roleVOList.forEach(vo -> {
            nameList.add(vo.getName());
            codeList.add(vo.getCode());
            if (V.notEmpty(vo.getPermissionList())){
                allPermissionList.addAll(vo.getPermissionList());
            }
        });
        // 对permissionList进行去重
        List permissionList = BeanUtils.distinctByKey(allPermissionList, IamResourcePermission::getId);
        IamRoleVO roleVO = new IamRoleVO();
        roleVO.setName(S.join(nameList));
        roleVO.setCode(S.join(codeList));
        roleVO.setPermissionList(permissionList);

        return roleVO;
    }

}
