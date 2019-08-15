package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.dto.OrganizationDto;
import com.diboot.example.entity.Organization;
import com.diboot.example.entity.Tree;
import com.diboot.example.service.OrganizationService;
import com.diboot.example.vo.OrganizationVO;
import com.diboot.shiro.authz.annotation.AuthorizationPrefix;
import com.diboot.shiro.authz.annotation.AuthorizationWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 组织机构相关Controller
 * @author wangyonglaing
 * @version 2.0
 * @time 2018/8/5
 */
@RestController
@RequestMapping("/organization")
@AuthorizationPrefix(name = "组织机构管理", code = "organization", prefix = "organization")
public class OrganizationController extends BaseCrudRestController {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private DictionaryService dictionaryService;

    /***
     * 获取列表页数据
     * @param dto
     * @param pagination
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    @AuthorizationWrapper(value = @RequiresPermissions("list"), name = "列表")
    public JsonResult getVOList(OrganizationDto dto, Pagination pagination, HttpServletRequest request) throws Exception{
        LambdaQueryWrapper<Organization> queryWrapper = super.buildLambdaQueryWrapper(dto);
        queryWrapper.eq(Organization::getParentId, 0);
        // 查询当前页的Entity主表数据
        List<OrganizationVO> voList = organizationService.getOrganizationList(queryWrapper, pagination);
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
        OrganizationVO vo = organizationService.getViewObject(id, OrganizationVO.class);
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
    public JsonResult createModel(@RequestBody Organization entity, HttpServletRequest request) throws Exception{
        boolean success = organizationService.createEntity(entity);
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
    public JsonResult updateModel(@PathVariable Long id, @RequestBody Organization entity, HttpServletRequest request) throws Exception{
       entity.setId(id);
        boolean success = organizationService.updateEntity(entity);
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
        boolean success = organizationService.deleteEntity(id);
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
        //获取所属行业KV
        List<KeyValue> industryKvList = dictionaryService.getKeyValueList(Organization.DICT_INDUSTRY);
        modelMap.put("industryKvList", industryKvList);

        return new JsonResult(modelMap);
    }

    /***
     * 获取组织树结构
     * @return
     * @throws Exception
     */
    @GetMapping("/getViewTreeList")
    public JsonResult getViewTreeList() throws Exception{
        List<OrganizationVO> voList = organizationService.getEntityTreeList();
        List<Tree> treeList = organizationService.getViewTreeList(voList);
        return new JsonResult(treeList);
    }

    /***
     * 校验组织名唯一性
     * @param id
     * @param name
     * @return
     */
    @GetMapping("/checkNameRepeat")
    public JsonResult checkNameRepeat(@RequestParam(required = false) Long id, @RequestParam String name){
        if(V.isEmpty(name)){
            return new JsonResult(Status.OK);
        }
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<Organization>().eq(Organization::getName, name);
        if(V.notEmpty(id)){
            wrapper.ne(Organization::getId, id);
        }
        List<Organization> orgList = organizationService.getEntityList(wrapper);
        if(V.isEmpty(orgList)){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION, "公司名称已存在");
    }

    /***
     * 校验组织编码唯一性
     * @param id
     * @param code
     * @return
     */
    @GetMapping("/checkCodeRepeat")
    public JsonResult checkCodeRepeat(@RequestParam(required = false) Long id, @RequestParam String code){
        if(V.isEmpty(code)){
            return new JsonResult(Status.OK);
        }
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<Organization>().eq(Organization::getCode, code);
        if(V.notEmpty(id)){
            wrapper.ne(Organization::getId, id);
        }
        List<Organization> orgList = organizationService.getEntityList(wrapper);
        if(V.isEmpty(orgList)){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION, "公司编码已存在");
    }

    /***
     * 校验成立时间合法性
     * @param parentId
     * @param establishTime
     * @return
     */
    @GetMapping("/checkEstablishTime")
    public JsonResult checkEstablishTime(@RequestParam Long parentId, @RequestParam Date establishTime){
        if(V.isEmpty(parentId) || V.isEmpty(establishTime)){
            return new JsonResult(Status.OK);
        }
        Organization org = organizationService.getEntity(parentId);
        if(V.notEmpty(org)){
            if(V.notEmpty(org.getEstablishTime()) && org.getEstablishTime().getTime() > establishTime.getTime()){
                return new JsonResult(Status.FAIL_OPERATION, "公司成立时间应晚于上级公司成立时间");
            }
        }
        return new JsonResult(Status.OK);
    }

    @Override
    protected BaseService getService() {
        return organizationService;
    }
}
