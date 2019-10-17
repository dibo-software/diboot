package com.diboot.component.msg.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.component.msg.entity.Message;
import com.diboot.component.msg.service.MessageService;
import com.diboot.component.msg.vo.MessageVO;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.service.BaseService;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController extends BaseCrudRestController {

    @Autowired
    private MessageService messageService;

    @Override
    protected BaseService getService() {
        return messageService;
    }

    @GetMapping("/list")
    public JsonResult list(Message message, Pagination pagination, HttpServletRequest request) throws Exception {
        //构建查询条件
        QueryWrapper<Message> queryWrapper = super.buildQueryWrapper(message);
        // 查询当前页的Entity主表数据
        List<Message> entityList =  getService().getEntityList(queryWrapper, pagination);
        // 自动转换VO中注解绑定的关联
        List<MessageVO> voList = RelationsBinder.convertAndBind(entityList, MessageVO.class);
        //返回结果
        return new JsonResult(Status.OK, voList).bindPagination(pagination);
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
        MessageVO entityVO = messageService.getViewObject(id, MessageVO.class);
        return new JsonResult(entityVO);
    }

    /***
     * 创建Entity
     * @return
     * @throws Exception
     */
    @PostMapping("/")
    public JsonResult createEntity(@ModelAttribute Message entity, BindingResult result, HttpServletRequest request)
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
    public JsonResult updateModel(@PathVariable("id")Long id, @ModelAttribute Message entity, BindingResult result,
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

}
