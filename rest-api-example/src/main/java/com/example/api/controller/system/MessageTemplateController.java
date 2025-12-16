package com.example.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.notification.dto.MessageTemplateDTO;
import com.diboot.notification.entity.MessageTemplate;
import com.diboot.notification.service.MessageTemplateService;
import com.diboot.notification.vo.MessageTemplateDetailVO;
import com.diboot.notification.vo.MessageTemplateListVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 消息模版 相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * * Copyright © MyCompany
 */
@RestController
@RequestMapping("/message-template")
@BindPermission(name = "消息通知模版")
@Slf4j
public class MessageTemplateController extends BaseCrudRestController<MessageTemplate> {


    @Autowired
    private MessageTemplateService messageTemplateService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: ?field=abc&pageIndex=1&orderBy=abc:DESC
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult getViewObjectListMapping(MessageTemplateDTO queryDto, Pagination pagination) throws Exception {
        return super.getViewObjectList(queryDto, pagination, MessageTemplateListVO.class);
    }

    /**
     * 根据资源id查询ViewObject
     *
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id") String id) throws Exception {
        return super.getViewObject(id, MessageTemplateDetailVO.class);
    }

    /**
     * 创建资源对象
     *
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult createEntityMapping(@Valid @RequestBody MessageTemplate entity) throws Exception {
        return super.createEntity(entity);
    }

    /**
     * 根据ID更新资源对象
     *
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") String id, @Valid @RequestBody MessageTemplate entity) throws Exception {
        return super.updateEntity(id, entity);
    }

    /**
     * 根据id删除资源对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id") String id) throws Exception {
        return super.deleteEntity(id);
    }

    /**
     * 检查是否有重复的code
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/check-temp-code-duplicate")
    public JsonResult checkTempCodeDuplicate(@RequestParam(required = false) String id, @RequestParam String code) throws Exception {
        if (V.isEmpty(code)) {
            return JsonResult.OK();
        }
        LambdaQueryWrapper<MessageTemplate> wrapper = Wrappers.<MessageTemplate>lambdaQuery().eq(MessageTemplate::getCode, code);
        // 如果id存在，那么需要排除当前id进行查询
        wrapper.ne(V.notEmpty(id), MessageTemplate::getId, id);
        if (messageTemplateService.exists(wrapper)) {
            return JsonResult.FAIL_VALIDATION("模版编码[" + code + "]已存在");
        }
        return JsonResult.OK();
    }

    /**
     * 获取变量列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/variable-list")
    public JsonResult getVariableList() throws Exception {
        return JsonResult.OK(messageTemplateService.getAllTemplateVariables());
    }
}