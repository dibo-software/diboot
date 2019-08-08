package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Tree;
import com.diboot.example.mapper.DepartmentMapper;
import com.diboot.example.service.DepartmentService;
import com.diboot.example.util.TreeUtil;
import com.diboot.example.vo.DepartmentVO;
import com.diboot.example.vo.OrganizationVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门相关Service实现
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/30
 */
@Service
@Slf4j
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Override
    public List<DepartmentVO> getDepartmentList(Wrapper wrapper, Pagination pagination, Long orgId) {
        List<DepartmentVO> voList = super.getViewObjectList(wrapper, pagination, DepartmentVO.class);
        List<DepartmentVO> treeList = getEntityTreeList(orgId);
        List<DepartmentVO> deptList = new ArrayList<>();
        if(V.notEmpty(voList) && V.notEmpty(treeList)){
            for(DepartmentVO vo1 : voList){
                for(DepartmentVO vo2 : treeList){
                    if(vo1.getId().intValue() == vo2.getId().intValue()){
                        deptList.add(vo2);
                        break;
                    }
                }
            }
        }
        return deptList;
    }

    @Override
    public List<DepartmentVO> getEntityTreeList(Long orgId) {
        LambdaQueryWrapper wrapper = null;
        if(V.notEmpty(orgId)){
            wrapper = new LambdaQueryWrapper<Department>().eq(Department::getOrgId, orgId);
        }
        List<Department> deptList = super.getEntityList(wrapper);
        List<DepartmentVO> volist = RelationsBinder.convertAndBind(deptList, DepartmentVO.class);
        List<DepartmentVO> voTreeList = BeanUtils.buildTree(volist);
        return voTreeList;
    }

    @Override
    public List<Tree> getViewTreeList(List<DepartmentVO> voList) {
        List<Tree> treeList = null;
        try {
            treeList = TreeUtil.getTreeList(voList, "getName", "getId", "getId", "getChildren" ,true);
        } catch (Exception e) {
            logger.warn("部门树转化失败");
            return null;
        }
        return treeList;
    }
}
