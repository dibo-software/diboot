package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.dto.PositionDto;
import com.diboot.example.entity.Position;
import com.diboot.example.entity.Tree;
import com.diboot.example.service.PositionDepartmentService;
import com.diboot.example.service.PositionService;
import com.diboot.example.vo.PositionVO;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 职位相关Controller
 * @author wangyonglaing
 * @version 2.0
 * @time 2018/8/5
 */
@RestController
@RequestMapping("/position")
@AuthorizationPrefix(name = "职位管理", code = "position", prefix = "position")
public class PositionController extends BaseCrudRestController {
    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionDepartmentService positionDepartmentService;

    @Autowired
    private DictionaryService dictionaryService;

    /***
     * 获取列表页数据
     * @param orgId
     * @param dto
     * @param pagination
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    @AuthorizationWrapper(value = @RequiresPermissions("list"), name = "列表")
    public JsonResult getVOList(Long orgId, PositionDto dto, Pagination pagination, HttpServletRequest request) throws Exception{
        if(V.isEmpty(orgId)){
            return new JsonResult(Status.FAIL_OPERATION, "请先选择所属公司").bindPagination(pagination);
        }
        QueryWrapper<Position> queryWrapper = super.buildQueryWrapper(dto);
        queryWrapper.lambda().eq(Position::getParentId, 0);
        // 查询当前页的Entity主表数据
        List<PositionVO> voList = positionService.getPositionList(queryWrapper, pagination, orgId);
        // 返回结果
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    /***
     * 查询详细
     * @param id
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("read"), name = "读取")
    public JsonResult getModel(@PathVariable Long id, HttpServletRequest request) throws Exception{
        PositionVO vo = positionService.getViewObject(id, PositionVO.class);
        return new JsonResult(vo);
    }

    /***
     * 新建
     * @param entity
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    @AuthorizationWrapper(value = @RequiresPermissions("create"), name = "新建")
    public JsonResult createModel(@RequestBody PositionVO entity, HttpServletRequest request) throws Exception{
        boolean success = positionService.createPosition(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    /***
     * 更新
     * @param id
     * @param entity
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("update"), name = "更新")
    public JsonResult updateModel(@PathVariable Long id, @RequestBody PositionVO entity, HttpServletRequest request) throws Exception{
       entity.setId(id);
        boolean success = positionService.updatePosition(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    /***
     * 删除
     * @param id
     * @param request
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    @AuthorizationWrapper(value = @RequiresPermissions("delete"), name = "删除")
    public JsonResult deleteModel(@PathVariable Long id, HttpServletRequest request) throws Exception{
        boolean success = positionService.deletePosition(id);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    /***
     * 加载更多数据
     * @param request
     * @param modelMap
     * @return
     */
    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap){
        Wrapper wrapper = null;
        //获取职级KV
        List<KeyValue> levelKvList = dictionaryService.getKeyValueList(Position.DICT_POSITION_LEVEL);
        modelMap.put("levelKvList", levelKvList);

        return new JsonResult(modelMap);
    }

    /***
     * 获取职位树结构
     * @param orgId
     * @param deptId
     * @return
     * @throws Exception
     */
    @GetMapping("/getViewTreeList")
    public JsonResult getViewTreeList(@RequestParam Long orgId, @RequestParam(required = false) Long deptId) throws Exception{
        List<PositionVO> voList = positionService.getEntityTreeList(orgId, deptId);
        List<Tree> treeList = positionService.getViewTreeList(voList);
        return new JsonResult(treeList);
    }

    /***
     * 校验职位名唯一性
     * @param orgId
     * @param id
     * @param name
     * @return
     */
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

    /***
     * 校验职位编号唯一性
     * @param orgId
     * @param id
     * @param number
     * @return
     */
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
