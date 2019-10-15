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

    /***
     * 获取列表页数据
     * @param wrapper
     * @param pagination
     * @return
     */
    List<OrganizationVO> getOrganizationList(Wrapper wrapper, Pagination pagination);

    /***
     * 获取组织实体树结构
     * @return
     */
    List<OrganizationVO> getEntityTreeList();

    /***
     * 获取组织树结构
     * @param voList
     * @return
     */
    List<Tree> getViewTreeList(List<OrganizationVO> voList);

}
