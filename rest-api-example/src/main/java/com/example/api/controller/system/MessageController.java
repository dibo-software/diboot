package com.example.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.notification.config.Cons;
import com.diboot.notification.dto.MessageDTO;
import com.diboot.notification.entity.Message;
import com.diboot.notification.service.MessageService;
import com.diboot.notification.vo.MessageDetailVO;
import com.diboot.notification.vo.MessageListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息 相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * * Copyright © MyCompany
 */
@RestController
@RequestMapping("/message")
@BindPermission(name = "消息通知")
@Slf4j
public class MessageController extends BaseCrudRestController<Message> {

    @Autowired
    private MessageService messageService;

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
    public JsonResult getViewObjectListMapping(MessageDTO queryDto, Pagination pagination) throws Exception {
        return super.getViewObjectList(queryDto, pagination, MessageListVO.class);
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
        return super.getViewObject(id, MessageDetailVO.class);
    }

    /**
     * 获取当前登录用的消息
     *
     * @param unread     是否获取未读消息
     * @param pagination
     * @return
     * @throws Exception
     */
    @GetMapping("/own")
    public JsonResult<List<MessageListVO>> getCurrentUserMessages(@RequestParam(required = false) boolean unread,
                                                                  @RequestParam(required = false) String businessCode,
                                                                  Pagination pagination) throws Exception {
        LambdaQueryWrapper<Message> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Message::getChannel, Cons.MESSAGE_CHANNEL.SYS_MSG.name());
        queryWrapper.eq(Message::getReceiver, IamSecurityUtils.getUserTypeAndId());
        queryWrapper.ne(unread, Message::getStatus, Cons.MESSAGE_STATUS.READ.name());
        queryWrapper.eq(V.notEmpty(businessCode), Message::getBusinessCode, businessCode);
        queryWrapper.orderByDesc(Message::getId);
        List<MessageListVO> viewObjectList = messageService.getViewObjectList(queryWrapper, pagination, MessageListVO.class);
        return JsonResult.OK(viewObjectList).bindPagination(pagination);
    }

    /**
     * 标记消息已读
     *
     * @param ids 消息ID列表
     * @return
     */
    @PostMapping("/read")
    public JsonResult<?> markRead(@RequestBody List<String> ids) {
        LambdaUpdateWrapper<Message> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.in(Message::getId, ids).set(Message::getStatus, Cons.MESSAGE_STATUS.READ.name());
        return new JsonResult<>(messageService.updateEntity(updateWrapper));
    }

}