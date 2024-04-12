/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.config.Cons;
import com.diboot.iam.entity.IamOrg;
import com.diboot.iam.mapper.IamOrgMapper;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.vo.IamOrgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* 组织机构相关Service实现
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-03
*/
@Service
@Slf4j
public class IamOrgServiceImpl extends BaseServiceImpl<IamOrgMapper, IamOrg> implements IamOrgService {

    @Override
    public boolean createEntity(IamOrg iamOrg){
        // 设置层级及公司ID及level
        enhanceIamOrg(iamOrg);
        return super.createEntity(iamOrg);
    }

    @Override
    public boolean updateEntity(IamOrg iamOrg){
        // 设置层级及公司ID及level
        enhanceIamOrg(iamOrg);
        return super.updateEntity(iamOrg);
    }

    /**
     * 增强IamOrg的属性
     * @param iamOrg
     */
    private void enhanceIamOrg(IamOrg iamOrg) {
        // 设置层级及公司ID
        if (!Cons.TREE_ROOT_ID.equals(iamOrg.getParentId())) {
            IamOrg parentOrg = getEntity(iamOrg.getParentId());
            if (parentOrg != null) {
                // 设置公司ID
                if (Cons.DICTCODE_ORG_TYPE.COMP.name().equals(parentOrg.getType())) {
                    iamOrg.setRootOrgId(parentOrg.getId());
                } else {
                    if (Cons.DICTCODE_ORG_TYPE.COMP.name().equals(iamOrg.getType())) {
                        throw new BusinessException("部门下不应有公司");
                    }
                    iamOrg.setRootOrgId(parentOrg.getRootOrgId());
                }
                // 设置ParentIdsPath
                if (V.isEmpty(parentOrg.getParentIdsPath())) {
                    iamOrg.setParentIdsPath(parentOrg.getId());
                } else {
                    iamOrg.setParentIdsPath(S.joinWith(Cons.SEPARATOR_COMMA, parentOrg.getParentIdsPath(), parentOrg.getId()));
                }
            }
        }
    }

    @Override
    public List<String> getChildOrgIds(String rootOrgId) {
        if(rootOrgId == null){
            return Collections.emptyList();
        }
        IamOrg parentOrg = getEntity(rootOrgId);
        LambdaQueryWrapper<IamOrg> select = Wrappers.lambdaQuery();
        if (parentOrg != null) {
            String parentIds = parentOrg.getParentIdsPath() == null ? rootOrgId : S.joinWith(Cons.SEPARATOR_COMMA, parentOrg.getParentIdsPath(), rootOrgId);
            select.likeRight(IamOrg::getParentIdsPath, parentIds);
        }
        select.orderByAsc(IamOrg::getSortId);
        return getValuesOfField(select, IamOrg::getId);
    }

    @Override
    public List<IamOrgVO> getOrgTree(String rootOrgId) {
        LambdaQueryWrapper<IamOrg> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByAsc(IamOrg::getSortId);
        IamOrg parentOrg = getEntity(rootOrgId);
        if (parentOrg != null) {
            String parentIds = parentOrg.getParentIdsPath() == null ? rootOrgId : S.joinWith(Cons.SEPARATOR_COMMA, parentOrg.getParentIdsPath(), rootOrgId);
            queryWrapper.likeRight(IamOrg::getParentIdsPath, parentIds);
        }
        List<IamOrg> orgList = getEntityList(queryWrapper);
        if (V.isEmpty(orgList)) {
            return Collections.emptyList();
        }
        List<IamOrgVO> orgVOList = BeanUtils.convertList(orgList, IamOrgVO.class);
        return BeanUtils.buildTree(orgVOList, rootOrgId);
    }

    @Override
    public List<LabelValue> getSimpleOrgTree(String rootOrgId) {
        LambdaQueryWrapper<IamOrg> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper
                .select(IamOrg::getName, IamOrg::getId, IamOrg::getCode, IamOrg::getParentId)
                .orderByAsc(IamOrg::getSortId);
        IamOrg parentOrg = getEntity(rootOrgId);
        if (parentOrg != null) {
            String parentIds = parentOrg.getParentIdsPath() == null ? rootOrgId : S.joinWith(Cons.SEPARATOR_COMMA, parentOrg.getParentIdsPath(), rootOrgId);
            queryWrapper.likeRight(IamOrg::getParentIdsPath, parentIds);
        }
        return getLabelValueList(queryWrapper);
    }

    @Override
    public List<String> getParentOrgIds(String orgId) {
        IamOrg parentOrg = getEntity(orgId);
        if (parentOrg == null || parentOrg.getParentIdsPath() == null) {
            return Collections.emptyList();
        }
        return S.splitToList(parentOrg.getParentIdsPath());
    }

    @Override
    public List<String> getOrgIdsByManagerId(String managerId) {
        LambdaQueryWrapper<IamOrg> queryWrapper = Wrappers.<IamOrg>lambdaQuery()
                .eq(IamOrg::getManagerId, managerId).orderByAsc(IamOrg::getSortId);
        return getValuesOfField(queryWrapper, IamOrg::getId);
    }

    @Override
    public Map<String, LabelValue> getLabelValueMap(List<String> ids) {
        LambdaQueryWrapper<IamOrg> queryWrapper = Wrappers.<IamOrg>lambdaQuery()
                .select(IamOrg::getName, IamOrg::getId, IamOrg::getCode)
                .in(IamOrg::getId, ids);
        // 返回构建条件
        return getEntityList(queryWrapper).stream().collect(
                Collectors.toMap(ent -> ent.getId(),
                        ent -> new LabelValue(ent.getName(), ent.getId()).setExt(ent.getCode())));
    }

    @Override
    public String getTenantRootOrgId(String tenantId) {
        return getMapper().getTenantRootOrgId(tenantId, BaseConfig.getActiveFlagValue());
    }

}
