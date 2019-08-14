package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.Department;
import com.diboot.example.entity.Position;
import com.diboot.example.entity.PositionDepartment;
import com.diboot.example.entity.Tree;
import com.diboot.example.service.DepartmentService;
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
    private DepartmentService departmentService;

    @Autowired
    private PositionDepartmentService positionDepartmentService;

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/list")
    public JsonResult getVOList(Long orgId, Position entity, Pagination pagination, HttpServletRequest request) throws Exception{
        if(V.isEmpty(orgId)){
            return new JsonResult(Status.FAIL_OPERATION, "请先选择所属公司").bindPagination(pagination);
        }
        QueryWrapper<Position> queryWrapper = super.buildQueryWrapper(entity);
        queryWrapper.lambda().eq(Position::getParentId, 0);
        // 查询当前页的Entity主表数据
        List<PositionVO> voList = positionService.getPositionList(queryWrapper, pagination, orgId);
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
        //获取职级KV
        List<KeyValue> levelKvList = dictionaryService.getKeyValueList(Position.DICT_POSITION_LEVEL);
        modelMap.put("levelKvList", levelKvList);

        return new JsonResult(modelMap);
    }

    @GetMapping("/getViewTreeList")
    public JsonResult getViewTreeList(@RequestParam Long orgId, @RequestParam(required = false) Long deptId) throws Exception{
        List<PositionVO> voList = positionService.getEntityTreeList(orgId);
        List<Tree> treeList = positionService.getViewTreeList(voList);
        return new JsonResult(treeList);
    }

    /*
    * 判断一个职位是否属于一个部门
    * */
    @GetMapping("/checkPositionBelongToDepartment")
    public JsonResult checkPositionBelongToDepartment(@RequestParam Long positionId, @RequestParam Long departmentId){
        Wrapper wrapper = new LambdaQueryWrapper<PositionDepartment>()
                .eq(PositionDepartment::getPositionId, positionId)
                .eq(PositionDepartment::getDepartmentId, departmentId);
        List<PositionDepartment> pdList = positionDepartmentService.getEntityList(wrapper);
        if(V.isEmpty(pdList) || pdList.size() == 0){
            return new JsonResult(Status.FAIL_OPERATION, "该职位不属于该部门，请重新选择职位");
        }
        return new JsonResult(Status.OK);
    }

    @GetMapping("/checkNameRepeat")
    public JsonResult checkNameRepeat(@RequestParam Long orgId, @RequestParam(required = false) Long id, @RequestParam String name){
        if(V.isEmpty(name)){
            return new JsonResult(Status.OK);
        }
        List<Position> positionList = positionService.getPositionList(orgId);
        if(V.isEmpty(positionList)){
            return new JsonResult(Status.OK);
        }
        int count = 0;
        for(Position position : positionList){
            if(name.equals(position.getName())){
                if(V.isEmpty(id)){
                    count++;
                }else{
                    if(V.notEquals(id, position.getId())){
                        count++;
                    }
                }
            }
        }
        if(count == 0){
            return new JsonResult(Status.OK);
        }

        return new JsonResult(Status.FAIL_OPERATION, "职位名称已存在");
    }

    @GetMapping("/checkNumberRepeat")
    public JsonResult checkNumberRepeat(@RequestParam Long orgId, @RequestParam(required = false) Long id, @RequestParam String number){
        if(V.isEmpty(number)){
            return new JsonResult(Status.OK);
        }
        List<Position> positionList = positionService.getPositionList(orgId);
        if(V.isEmpty(positionList)){
            return new JsonResult(Status.OK);
        }
        int count = 0;
        for(Position position : positionList){
            if(number.equals(position.getNumber())){
                if(V.isEmpty(id)){
                    count++;
                }else{
                    if(V.notEquals(id, position.getId())){
                        count++;
                    }
                }
            }
        }
        if(count == 0){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION, "职位编号已存在");
    }



    @Override
    protected BaseService getService() {
        return positionService;
    }
}
