package com.diboot.example.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Organization;
import com.diboot.example.vo.OrganizationVO;

import java.util.List;

/**
 * 单位相关Service
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
public interface OrganizationService extends BaseService<Organization> {

    List<OrganizationVO> getOrganizatioList(Wrapper wrapper, Pagination pagination);

}
