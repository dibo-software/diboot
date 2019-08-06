package com.diboot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Position;
import com.diboot.example.entity.PositionDepartment;
import com.diboot.example.mapper.PositionMapper;
import com.diboot.example.service.PositionDepartmentService;
import com.diboot.example.service.PositionService;
import com.diboot.example.vo.PositionVO;
import com.diboot.shiro.entity.RolePermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 职位相关Service实现
 * @author wangyongliang
 * @version v2.0
 * @date 2019/8/5
 */
@Service
@Slf4j
public class PositionServiceImpl extends BaseServiceImpl<PositionMapper, Position> implements PositionService {

    @Autowired
    PositionDepartmentService positionDepartmentService;

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
                for(Department department : departmentList){
                    PositionDepartment positionDepartment = new PositionDepartment();
                    positionDepartment.setPositionId(position.getId());
                    positionDepartment.setDepartmentId(department.getId());
                    positionDepartmentService.createEntity(positionDepartment);
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
            List<PositionDepartment> oldDepartmentList = positionDepartmentService.getEntityList(query);

            List<Department> newDepartmentList = positionVO.getDepartmentList();
            StringBuffer oldBuffer = new StringBuffer();
            StringBuffer newBuffer = new StringBuffer();
            if(V.notEmpty(oldDepartmentList)){
                for(PositionDepartment pd : oldDepartmentList){
                    oldBuffer.append(pd.getDepartmentId()).append(",");
                }
            }
            if(V.notEmpty(newDepartmentList)){
                for(Department d : newDepartmentList){
                    newBuffer.append(d.getId()).append(",");
                }
            }

            //删除页面取消选择的部门
            if(V.notEmpty(oldDepartmentList)){
                for(PositionDepartment pd : oldDepartmentList){
                    if(!(newBuffer.toString().contains(pd.getDepartmentId().toString()))){
                        positionDepartmentService.deleteEntity(pd.getId());
                    }
                }
            }

            //新增页面选择的部门
            if(V.notEmpty(newDepartmentList)){
                for(Department d : newDepartmentList){
                    if(!(oldBuffer.toString().contains(d.getId().toString()))){
                        PositionDepartment entity = new PositionDepartment();
                        entity.setDepartmentId(d.getId());
                        entity.setPositionId(positionVO.getId());
                        positionDepartmentService.createEntity(entity);
                    }
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
}
