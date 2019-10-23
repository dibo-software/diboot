package com.diboot.component.msg.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.component.msg.entity.MessageTemplate;
import com.diboot.component.msg.service.MessageTemplateService;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.KeyValue;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/*
 * 消息模板相关service
 * @author:wangyl
 * */
@RestController
@RequestMapping("/messageTemplate")
public class MessageTemplateController extends BaseCrudRestController {

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    protected BaseService getService() {
        return messageTemplateService;
    }

    @GetMapping("/list")
    public JsonResult list(MessageTemplate messageTemplate, Pagination pagination, HttpServletRequest request) throws Exception {
        //构建查询条件
        QueryWrapper<MessageTemplate> queryWrapper = super.buildQueryWrapper(messageTemplate);
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
    public JsonResult getModel(@PathVariable("id")Long id, HttpServletRequest request)
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
    public JsonResult createEntity(@ModelAttribute MessageTemplate entity, BindingResult result, HttpServletRequest request)
            throws Exception{
        return super.createEntity(entity, result);
    }

    /***
     * 更新Entity
     * @param id
     * @return
     * @throws Exception
     */
    @PutMapping("/{id}")
    public JsonResult updateModel(@PathVariable("id")Long id, @ModelAttribute MessageTemplate entity, BindingResult result,
                                  HttpServletRequest request) throws Exception{
        return super.updateEntity(entity, result);
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

    /***
     * 加载更多数据
     * @param request
     * @param modelMap
     * @return
     */
    @GetMapping("/attachMore")
    public JsonResult attachMore(HttpServletRequest request, ModelMap modelMap){
        Wrapper wrapper = null;
        //消息模板元数据
        List<KeyValue> msgTempCodeKvList = dictionaryService.getKeyValueList(MessageTemplate.METADATA_TYPE.MSG_TEMP_CODE.name());
        modelMap.put("msgTempCodeKvList", msgTempCodeKvList);

        return new JsonResult(modelMap);
    }

    @GetMapping("/getTemplateVaribles/{code}")
    public JsonResult getTemplateVaribles(@PathVariable("code")String code, HttpServletRequest request) throws Exception{
        String[] varibles = messageTemplateService.getTemplateVaribles(code);
        if(V.isEmpty(varibles)){
            return new JsonResult(Status.FAIL_OPERATION, "获取变量列表为空");
        }
        return new JsonResult(Status.OK, varibles,"获取变量列表成功");
    }

}
