package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.Organization;
import com.diboot.example.entity.Tree;
import com.diboot.example.service.OrganizationService;
import com.diboot.example.vo.OrganizationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
/*
* 组织结构 controller
* */
@RestController
@RequestMapping("/organization")
public class OrganizationController extends BaseCrudRestController {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/list")
    public JsonResult getVOList(Organization entity, Pagination pagination, HttpServletRequest request) throws Exception{
        LambdaQueryWrapper<Organization> queryWrapper = super.buildLambdaQueryWrapper(entity);
        queryWrapper.eq(Organization::getParentId, 0);
        // 查询当前页的Entity主表数据
        List<OrganizationVO> voList = organizationService.getOrganizationList(queryWrapper, pagination);
        // 返回结果
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
    }

    @GetMapping("/{id}")
    public JsonResult getModel(@PathVariable Long id, HttpServletRequest request) throws Exception{
        OrganizationVO vo = organizationService.getViewObject(id, OrganizationVO.class);
        return new JsonResult(vo);
    }

    @PostMapping("/")
    public JsonResult createModel(@RequestBody Organization entity, HttpServletRequest request) throws Exception{
        boolean success = organizationService.createEntity(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @PutMapping("/{id}")
    public JsonResult updateModel(@PathVariable Long id, @RequestBody Organization entity, HttpServletRequest request) throws Exception{
       entity.setId(id);
        boolean success = organizationService.updateEntity(entity);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @DeleteMapping("/{id}")
    public JsonResult deleteModel(@PathVariable Long id, HttpServletRequest request) throws Exception{
        boolean success = organizationService.deleteEntity(id);
        if(success){
            return new JsonResult(Status.OK);
        }
        return new JsonResult(Status.FAIL_OPERATION);
    }

    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap){
        Wrapper wrapper = null;
        //获取所属行业KV
        List<KeyValue> industryKvList = dictionaryService.getKeyValueList(Organization.DICT_INDUSTRY);
        modelMap.put("industryKvList", industryKvList);

        return new JsonResult(modelMap);
    }

    @GetMapping("/getViewTreeList")
    public JsonResult getViewTreeList() throws Exception{
        List<Tree> treeList = organizationService.getViewTreeList();
        return new JsonResult(treeList);
    }

    @Override
    protected BaseService getService() {
        return organizationService;
    }
}
