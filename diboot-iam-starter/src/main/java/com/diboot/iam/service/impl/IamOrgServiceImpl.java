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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.entity.IamOrg;
import com.diboot.iam.mapper.IamOrgMapper;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.vo.IamOrgVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* 组织机构相关Service实现
* @author mazc@dibo.ltd
* @version 2.2
* @date 2019-12-03
*/
@Service
@Slf4j
public class IamOrgServiceImpl extends BaseIamServiceImpl<IamOrgMapper, IamOrg> implements IamOrgService{

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
    private void enhanceIamOrg(IamOrg iamOrg){
        // 设置层级及公司ID
        if (iamOrg.getParentId() != null && iamOrg.getParentId() != null) {
            IamOrg parentOrg = getEntity(iamOrg.getParentId());
            if (parentOrg != null) {
                // 设置层级
                int parentLevel = parentOrg.getDepth().intValue();
                iamOrg.setDepth(parentLevel + 1);
                // 设置公司ID
                if (parentOrg.getParentId() == null || V.isEmpty(parentOrg.getParentId())) {
                    iamOrg.setTopOrgId(parentOrg.getId());
                }
                else {
                    iamOrg.setTopOrgId(parentOrg.getTopOrgId());
                }
            }
        }
    }

    @Override
    public List<String> getChildOrgIds(String rootOrgId) {
        if(rootOrgId == null){
            return Collections.emptyList();
        }
        List<IamOrgVO> childOrgs = getOrgTree(rootOrgId);
        if(V.notEmpty(childOrgs)){
            List<String> childOrgIds = new ArrayList<>();
            extractIds(childOrgs, childOrgIds);
            return childOrgIds;
        }
        return Collections.emptyList();
    }

    @Override
    public List<IamOrgVO> getOrgTree(String rootOrgId) {
        QueryWrapper<IamOrg> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(IamOrg::getSortId);
        List<IamOrg> orgList = getEntityList(queryWrapper);
        if (V.isEmpty(orgList)) {
            return Collections.emptyList();
        }
        List<IamOrgVO> orgVOList = BeanUtils.convertList(orgList, IamOrgVO.class);
        return BeanUtils.buildTree(orgVOList, rootOrgId);
    }

    @Override
    public List<String> getParentOrgIds(String orgId) {
        return getParentOrgIds(orgId, true);
    }

    @Override
    public List<String> getParentOrgIds(String orgId, boolean includeThis) {
        if(orgId == null){
            return Collections.emptyList();
        }
        List<String> scopeIds = new ArrayList<>();
        if(includeThis){
            scopeIds.add(orgId);
        }
        // 查询所有上级
        IamOrg org = getEntity(orgId);
        if(org != null && org.getDepth() != null){
            if(org.getDepth() >= 2){
                scopeIds.add(org.getParentId());
                if(org.getDepth() > 2) {
                    LambdaQueryWrapper<IamOrg> queryWrapper =
                            Wrappers.<IamOrg>lambdaQuery().select(IamOrg::getId, IamOrg::getParentId)
                                    .lt(IamOrg::getDepth, org.getDepth());
                    List<IamOrg> parentOrgs = getEntityList(queryWrapper);
                    if (V.isEmpty(parentOrgs)) {
                        Map<String, IamOrg> orgId2ParentIdMap = BeanUtils.convertToStringKeyObjectMap(parentOrgs,
                                IamOrg::getId);
                        String parentOrgIdStr = S.valueOf(org.getParentId());
                        while (orgId2ParentIdMap.containsKey(parentOrgIdStr)) {
                            String parentOrgId = orgId2ParentIdMap.get(parentOrgIdStr).getParentId();
                            scopeIds.add(parentOrgId);
                            parentOrgIdStr = S.valueOf(parentOrgId);
                        }
                    }
                }
            }
        }
        return scopeIds;
    }

    @Override
    public List<String> getOrgIdsByManagerId(String managerId) {
        LambdaQueryWrapper<IamOrg> queryWrapper = new LambdaQueryWrapper<IamOrg>()
                .eq(IamOrg::getManagerId, managerId);
        List<String> orgIdList = getValuesOfField(queryWrapper, IamOrg::getId);
        return orgIdList;
    }

    /**
     * 提取id
     * @param orgs
     */
    private void extractIds(List<IamOrgVO> orgs, List<String> resultIds){
        if(V.isEmpty(orgs)){
            return;
        }
        for(IamOrgVO orgVO : orgs){
            resultIds.add(orgVO.getId());
            if(V.notEmpty(orgVO.getChildren())){
                extractIds(orgVO.getChildren(), resultIds);
            }
        }
    }

}
