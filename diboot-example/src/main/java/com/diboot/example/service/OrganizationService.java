package com.diboot.example.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Organization;
import com.diboot.example.entity.Tree;
import com.diboot.example.vo.OrganizationVO;

import java.util.List;

/**
 * 组织相关Service
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
public interface OrganizationService extends BaseService<Organization> {

    List<OrganizationVO> getOrganizationList(Wrapper wrapper, Pagination pagination);

    List<OrganizationVO> getEntityTreeList();

    List<Tree> getViewTreeList(List<OrganizationVO> voList);

}
