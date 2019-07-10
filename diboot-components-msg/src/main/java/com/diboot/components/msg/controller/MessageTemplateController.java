package com.diboot.components.msg.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.components.msg.entity.MessageTemplate;
import com.diboot.components.msg.service.MessageTemplateService;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/messageTemplate")
public class MessageTemplateController extends BaseCrudRestController {

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Override
    protected BaseService getService() {
        return messageTemplateService;
    }

    @GetMapping("/list")
    public JsonResult list(HttpServletRequest request) throws Exception {
        //构建查询条件
        QueryWrapper<MessageTemplate> queryWrapper = buildQuery(request);
        //构建分页
        Pagination pagination = buildPagination(request);
        // 查询当前页的Entity主表数据
        List<MessageTemplate> entityList =  getService().getEntityList(queryWrapper, pagination);
        //返回结果
        return new JsonResult(Status.OK, entityList).bindPagination(pagination);
    }

    /***
     * 查询Entity
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public JsonResult getModel(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap)
            throws Exception{
        MessageTemplate entity = messageTemplateService.getEntity(id);
        return new JsonResult(entity);
    }

    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    public JsonResult createEntity(@ModelAttribute MessageTemplate entity, BindingResult result, HttpServletRequest request, ModelMap modelMap)
            throws Exception{
        return super.createEntity(entity, result, modelMap);
    }

    /***
     * 更新Entity
     * @param id
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public JsonResult updateModel(@PathVariable("id")Long id, @ModelAttribute MessageTemplate entity, BindingResult result,
                                  HttpServletRequest request, ModelMap modelMap) throws Exception{
        return super.updateEntity(entity, result, modelMap);
    }

    /***
     * 删除
     * @param id
     * @return
     * @throws Exception
     */
    @DeleteMapping("/{id}")
    public JsonResult deleteModel(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
        return super.deleteEntity(id);
    }

}
