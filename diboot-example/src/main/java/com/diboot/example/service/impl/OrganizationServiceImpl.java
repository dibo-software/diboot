package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Organization;
import com.diboot.example.mapper.OrganizationMapper;
import com.diboot.example.service.OrganizationService;
import com.diboot.example.vo.OrganizationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 单位相关Service实现
 * @author Mazhicheng
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@Service
@Slf4j
public class OrganizationServiceImpl extends BaseServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    @Override
    public List<OrganizationVO> getOrganizatioList(Wrapper wrapper, Pagination pagination) {
        List<OrganizationVO> voList = super.getViewObjectList(wrapper, pagination, OrganizationVO.class);
        if(V.notEmpty(voList)){
            for(OrganizationVO vo : voList){
                wrapper = new LambdaQueryWrapper<Organization>().eq(Organization::getParentId, vo.getId());
                List<Organization> orgList = super.getEntityList(wrapper);
                List<OrganizationVO> orgVoList = RelationsBinder.convertAndBind(orgList, OrganizationVO.class);
                vo.setChildren(orgVoList);
            }
        }
        return voList;
    }
}
