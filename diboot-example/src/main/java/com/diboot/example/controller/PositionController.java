package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.Organization;
import com.diboot.example.entity.Position;
import com.diboot.example.entity.PositionDepartment;
import com.diboot.example.service.OrganizationService;
import com.diboot.example.service.PositionDepartmentService;
import com.diboot.example.service.PositionService;
import com.diboot.example.vo.PositionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/*
* 职位 controller
* */
@RestController
@RequestMapping("/position")
public class PositionController extends BaseCrudRestController {
    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);

    @Autowired
    private PositionService positionService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private PositionDepartmentService positionDepartmentService;

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/list")
    public JsonResult getVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<Position> queryWrapper = buildQuery(request);
        // 构建分页
        Pagination pagination = buildPagination(request);
        // 查询当前页的Entity主表数据
        List<Position> entityList = positionService.getEntityList(queryWrapper, pagination);
        //筛选出在列表页展示的字段
        List<PositionVO> voList = RelationsBinder.convertAndBind(entityList, PositionVO.class);
        // 返回结果
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    @GetMapping("/{id}")
    public JsonResult getModel(@PathVariable Long id, HttpServletRequest request) throws Exception{
        PositionVO vo = positionService.getViewObject(id, PositionVO.class);
        return new JsonResult(vo);
    }

    @PostMapping("/")
    public JsonResult createModel(@RequestBody PositionVO entity, HttpServletRequest request) throws Exception{
        boolean success = positionService.createPosition(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @PutMapping("/{id}")
    public JsonResult updateModel(@PathVariable Long id, @RequestBody PositionVO entity, HttpServletRequest request) throws Exception{
       entity.setId(id);
        boolean success = positionService.updatePosition(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @DeleteMapping("/{id}")
    public JsonResult deleteModel(@PathVariable Long id, HttpServletRequest request) throws Exception{
        boolean success = positionService.deletePosition(id);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap){
        Wrapper wrapper = null;
        //获取组织机构KV
        wrapper = new QueryWrapper<Organization>()
                .lambda()
                .select(Organization::getName, Organization::getId);
        List<KeyValue> orgKvList = organizationService.getKeyValueList(wrapper);
        modelMap.put("orgKvList", orgKvList);

        //获取职级KV
        List<KeyValue> levelKvList = dictionaryService.getKeyValueList(Position.POSITION_LEVEL);
        modelMap.put("levelKvList", levelKvList);

        return new JsonResult(modelMap);
    }

    /*
     * 根据部门ID获取职位kv list
     * */
    @GetMapping("/getPositionKV/{deptId}")
    public JsonResult getPositionKV(@PathVariable Long deptId, HttpServletRequest request){
        Wrapper wrapper = null;
        List<Long> positionIdList = new ArrayList<>();
        wrapper  = new LambdaQueryWrapper<PositionDepartment>()
                .eq(PositionDepartment::getDepartmentId, deptId);
        List<PositionDepartment> positionDepartmentList = positionDepartmentService.getEntityList(wrapper);
        if(V.notEmpty(positionDepartmentList)){
            for(PositionDepartment positionDepartment : positionDepartmentList){
                positionIdList.add(positionDepartment.getPositionId());
            }
        }
        //获取职位KV
        wrapper = new QueryWrapper<Position>()
                .lambda()
                .select(Position::getName, Position::getId)
                .in(Position::getId, positionIdList);
        List<KeyValue> positionKvList = positionService.getKeyValueList(wrapper);

        return new JsonResult(positionKvList);
    }


    @Override
    protected BaseService getService() {
        return positionService;
    }
}
