package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Organization;
import com.diboot.example.entity.Tree;
import com.diboot.example.mapper.OrganizationMapper;
import com.diboot.example.service.OrganizationService;
import com.diboot.example.util.TreeUtil;
import com.diboot.example.vo.OrganizationVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织相关Service实现
 * @author Mazhicheng
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@Service
@Slf4j
public class OrganizationServiceImpl extends BaseServiceImpl<OrganizationMapper, Organization> implements OrganizationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Override
    public List<OrganizationVO> getOrganizationList(Wrapper wrapper, Pagination pagination) {
        List<OrganizationVO> voList = super.getViewObjectList(wrapper, pagination, OrganizationVO.class);
        List<OrganizationVO> treeList = getEntityTreeList();
        List<OrganizationVO> orgList = new ArrayList<>();
        if(V.notEmpty(voList) && V.notEmpty(treeList)){
            for(OrganizationVO vo1 : voList){
                for(OrganizationVO vo2 : treeList){
                    if(vo1.getId().intValue() == vo2.getId().intValue()){
                        orgList.add(vo2);
                        break;
                    }
                }
            }
        }
        return orgList;
    }

    @Override
    public List<OrganizationVO> getEntityTreeList() {
        List<Organization> orgList = super.getEntityList(null);
        List<OrganizationVO> volist = RelationsBinder.convertAndBind(orgList, OrganizationVO.class);
        List<OrganizationVO> voTreeList = BeanUtils.buildTree(volist);
        return voTreeList;
    }

    @Override
    public List<Tree> getViewTreeList() {
        List<Tree> treeList = null;
        List<OrganizationVO> entityTreeList = getEntityTreeList();
        try {
            treeList = TreeUtil.getTreeList(entityTreeList, "getName", "getId", null, "getChildren" ,null);
        } catch (Exception e) {
            logger.warn("组织机构树转化失败");
            return null;
        }
        return treeList;
    }
}
