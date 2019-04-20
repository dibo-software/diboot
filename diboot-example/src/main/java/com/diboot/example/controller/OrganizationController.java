package com.diboot.example.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Status;
import com.diboot.example.entity.Organization;
import com.diboot.example.service.OrganizationService;
import com.diboot.example.vo.OrganizationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Organization相关Controller
 * @author Mazhicheng
 * @version 2018/12/23
 * Copyright © www.dibo.ltd
 */
@RestController
@RequestMapping("/organization")
public class OrganizationController extends BaseCrudRestController {

    @Autowired
    private OrganizationService organizationService;

    /***
     * 默认的分页实现
     * <p>
     * url参数示例: /list?_pageSize=20&_pageIndex=1&_orderBy=itemValue&type=GENDAR
     * </p>
     * @return
     * @throws Exception
     */
    @GetMapping("/list")
    public JsonResult getDefaultVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<Organization> queryWrapper = buildQuery(request);
        return super.getEntityListWithPaging(request, queryWrapper, OrganizationVO.class);
    }

    /***
     * 默认的分页实现
     * <p>
     * url参数示例: /listVo?page.size=20&page.index=1&page.orderBy=itemValue&type=GENDAR
     * </p>
     * @return
     * @throws Exception
     */
    @GetMapping("/listVo")
    public JsonResult getCustomVOList(HttpServletRequest request) throws Exception{
        QueryWrapper<Organization> queryWrapper = buildQuery(request);
        // 查询当前页的数据
        List entityList = organizationService.getEntityList(queryWrapper);
        List voList = organizationService.getViewObjectList(entityList, OrganizationVO.class);
        // 返回结果
        return new JsonResult(Status.OK, voList);
    }

    @GetMapping("/kv")
    public JsonResult getKVPairList(HttpServletRequest request){
        Wrapper wrapper = new QueryWrapper<Organization>().lambda()
            .select(Organization::getName, Organization::getId);
        List<KeyValue> list = organizationService.getKeyValueList(wrapper);
        return new JsonResult(list);
    }

    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    public JsonResult createEntity(@ModelAttribute OrganizationVO viewObject, BindingResult result, HttpServletRequest request, ModelMap modelMap)
            throws Exception{
        // 转换
        Organization entity = BeanUtils.convert(viewObject, Organization.class);
        // 创建
        return super.createEntity(entity, result, modelMap);
    }

    /***
     * 查询Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public JsonResult getModel(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap)
            throws Exception{
        OrganizationVO vo = organizationService.getViewObject(id, OrganizationVO.class);
        return new JsonResult(vo);
    }

    /***
     * 更新Entity
     * @param id ID
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public JsonResult updateModel(@PathVariable("id")Long id, @ModelAttribute Organization entity, BindingResult result,
                                  HttpServletRequest request, ModelMap modelMap) throws Exception{
        return super.updateEntity(entity, result, modelMap);
    }

    /***
     * 删除用户
     * @param id 用户ID
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    public JsonResult deleteModel(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
        return super.deleteEntity(id);
    }

    @Override
    protected BaseService getService() {
        return organizationService;
    }

}
