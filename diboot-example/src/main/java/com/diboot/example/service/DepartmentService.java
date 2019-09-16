package com.diboot.example.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Tree;
import com.diboot.example.vo.DepartmentVO;

import java.util.List;

/**
 * 部门相关Service
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
public interface DepartmentService extends BaseService<Department> {

    /***
     * 获取列表页数据
     * @param wrapper
     * @param pagination
     * @param orgId
     * @return
     */
    List<DepartmentVO> getDepartmentList(Wrapper wrapper, Pagination pagination, Long orgId);

    /***
     * 获取部门实体树结构
     * @param orgId
     * @return
     */
    List<DepartmentVO> getEntityTreeList(Long orgId);

    /***
     * 获取部门树结构
     * @param voList
     * @return
     */
    List<Tree> getViewTreeList(List<DepartmentVO> voList);

}
