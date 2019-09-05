package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Position;
import com.diboot.example.entity.PositionDepartment;
import com.diboot.example.entity.Tree;
import com.diboot.example.mapper.PositionMapper;
import com.diboot.example.service.DepartmentService;
import com.diboot.example.service.PositionDepartmentService;
import com.diboot.example.service.PositionService;
import com.diboot.example.util.TreeUtil;
import com.diboot.example.vo.PositionVO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 职位相关Service实现
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/5
 */
@Service
@Slf4j
public class PositionServiceImpl extends BaseServiceImpl<PositionMapper, Position> implements PositionService {
    private static final Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);

    @Autowired
    PositionDepartmentService positionDepartmentService;

    @Autowired
    DepartmentService departmentService;

    @Override
    public List<PositionVO> getPositionList(Wrapper wrapper, Pagination pagination, Long orgId) {
        List<PositionVO> positionList = new ArrayList<>();
        List<PositionVO> voList = super.getViewObjectList(wrapper, pagination, PositionVO.class);
        List<PositionVO> list = new ArrayList<>();
        if(V.notEmpty(voList)){
            for(PositionVO vo : voList){
                List<Department> deptList = vo.getDepartmentList();
                if(V.notEmpty(deptList)){
                    for(Department dept : deptList){
                        if(dept.getOrgId().intValue() == orgId.intValue()){
                            list.add(vo);
                            break;
                        }
                    }
                }
            }
        }
        List<PositionVO> positionVOList = getEntityTreeList(orgId, null);
        if(V.notEmpty(list)){
            for(PositionVO vo1 : list){
                if(V.notEmpty(positionVOList)){
                    for(PositionVO  vo2: positionVOList){
                        if(vo1.getId().intValue() == vo2.getId().intValue()){
                            positionList.add(vo2);
                        }
                    }
                }
            }
        }

        return positionList;
    }

    @Override
    public List<Position> getPositionList(Long orgId) {
        List<Position> positionList = new ArrayList<>();
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper<Department>()
                .eq(Department::getOrgId, orgId);
        List<Department> deptList = departmentService.getEntityList(wrapper);
        if(V.notEmpty(deptList)){
            List<Long> deptIdList = new ArrayList<>();
            for(Department dept : deptList){
                deptIdList.add(dept.getId());
            }
            if(V.notEmpty(deptIdList)){
                wrapper = new LambdaQueryWrapper<PositionDepartment>().in(PositionDepartment::getDepartmentId, deptIdList);
                List<PositionDepartment> pdList = positionDepartmentService.getEntityList(wrapper);
                Set<Long> positionIdSet = new HashSet<>();
                if(V.notEmpty(pdList)){
                    for(PositionDepartment pd : pdList){
                        positionIdSet.add(pd.getPositionId());
                    }
                }
                if(V.notEmpty(positionIdSet)){
                    positionList = super.getEntityListByIds(new ArrayList(positionIdSet));
                }
            }
        }

       return positionList;
    }

    @Override
    @Transactional
    public boolean createPosition(PositionVO positionVO) {
        Position position = BeanUtils.convert(positionVO, Position.class);
        boolean success = super.createEntity(position);
        if(!success){
            return false;
        }

        try {
            List<Department> departmentList = positionVO.getDepartmentList();
            if(V.notEmpty(departmentList)){
                List<PositionDepartment> pdList = new ArrayList<>();
                for(Department department : departmentList){
                    PositionDepartment positionDepartment = new PositionDepartment();
                    positionDepartment.setPositionId(position.getId());
                    positionDepartment.setDepartmentId(department.getId());
                    pdList.add(positionDepartment);
                }
                if(V.notEmpty(pdList)){
                    positionDepartmentService.createEntities(pdList);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }

    @Override
    @Transactional
    public boolean updatePosition(PositionVO positionVO) {
        Position position = BeanUtils.convert(positionVO, Position.class);
        boolean success = super.updateEntity(position);
        if(!success){
            return false;
        }

        try {
            //获取职位-部门对应信息
            QueryWrapper<PositionDepartment> query = new QueryWrapper();
            query.lambda().eq(PositionDepartment::getPositionId, positionVO.getId());
            List<PositionDepartment> oldList = positionDepartmentService.getEntityList(query);

            List<Department> newDepartmentList = positionVO.getDepartmentList();
            StringBuffer oldBuffer = new StringBuffer();
            StringBuffer newBuffer = new StringBuffer();
            if(V.notEmpty(oldList)){
                for(PositionDepartment pd : oldList){
                    oldBuffer.append(pd.getDepartmentId()).append(",");
                }
            }
            if(V.notEmpty(newDepartmentList)){
                for(Department d : newDepartmentList){
                    newBuffer.append(d.getId()).append(",");
                }
            }
            //删除页面取消选择的部门
            if(V.notEmpty(oldList)){
                List<Long> idList = new ArrayList<>();
                for(PositionDepartment pd : oldList){
                    if(!(newBuffer.toString().contains(pd.getDepartmentId().toString()))){
                        idList.add(pd.getId());
                    }
                }
                Wrapper wrapper = new LambdaQueryWrapper<PositionDepartment>().in(PositionDepartment::getId, idList);
                positionDepartmentService.deleteEntities(wrapper);
            }
            //新增页面选择的部门
            if(V.notEmpty(newDepartmentList)){
                List<PositionDepartment> pdList = new ArrayList<>();
                for(Department d : newDepartmentList){
                    if(!(oldBuffer.toString().contains(d.getId().toString()))){
                        PositionDepartment entity = new PositionDepartment();
                        entity.setDepartmentId(d.getId());
                        entity.setPositionId(positionVO.getId());
                        pdList.add(entity);
                    }
                }
                if(V.notEmpty(pdList)){
                    positionDepartmentService.createEntities(pdList);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deletePosition(Long id) {
        boolean success = super.deleteEntity(id);
        if(!success){
            return false;
        }

        QueryWrapper<PositionDepartment> query = new QueryWrapper();
        query.lambda().eq(PositionDepartment::getPositionId, id);
        try {
            positionDepartmentService.deleteEntities(query);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return true;
    }


    @Override
    public List<PositionVO> getEntityTreeList(Long orgId, Long deptId) {
        //获取公司下的部门
        LambdaQueryWrapper<Department> deptWrapper = new LambdaQueryWrapper<Department>()
                                    .eq(Department::getOrgId, orgId)
                                    .select(Department::getId);
        if(V.notEmpty(deptId)){
            deptWrapper.eq(Department::getId, deptId);
        }
        List<Department> deptList = departmentService.getEntityList(deptWrapper);
        List<Long> deptIdList = new ArrayList<>();
        if(V.notEmpty(deptList)){
            for(Department dept : deptList){
                deptIdList.add(dept.getId());
            }
        }
        if(V.isEmpty(deptIdList)){
            return null;
        }
        //获取部门-职位对应信息
        LambdaQueryWrapper<PositionDepartment> pdWrapper = new LambdaQueryWrapper<PositionDepartment>()
                .in(PositionDepartment::getDepartmentId, deptIdList);
        List<PositionDepartment> pdList = positionDepartmentService.getEntityList(pdWrapper);
        List<Long> positionIdList = new ArrayList<>();
        if(V.notEmpty(pdList)){
            for(PositionDepartment pd : pdList){
                positionIdList.add(pd.getPositionId());
            }
        }
        if(V.isEmpty(positionIdList)){
            return null;
        }
        //获取职位
        List<Position> positionList = super.getEntityListByIds(positionIdList);
        List<PositionVO> voList = RelationsBinder.convertAndBind(positionList, PositionVO.class);
        List<PositionVO> voTreeList = BeanUtils.buildTree(null, voList);
        return voTreeList;
    }

    @Override
    public List<Tree> getViewTreeList(List<PositionVO> voList) {
        List<Tree> treeList = null;
        try {
            treeList = TreeUtil.getTreeList(voList, "getName", "getId", "getId", "getChildren" ,true);
        } catch (Exception e) {
            logger.warn("职位树转化失败");
            return null;
        }
        return treeList;
    }

}
