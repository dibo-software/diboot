package com.example.api.controller.system;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.ai.entity.AiSession;
import com.diboot.ai.entity.AiSessionRecord;
import com.diboot.ai.service.AiSessionService;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI会话
 *
 * @author : uu
 * @version : v1.0
 * @Date 2024/4/29
 */
@RestController
@RequestMapping("/ai-session")
@BindPermission(name = "AI 会话")
@Slf4j
public class AiSessionController extends BaseCrudRestController<AiSession> {

    @Autowired
    private AiSessionService aiSessionService;

    /**
     * 查询会话对象的列表VO记录
     * <p>
     * url请求参数示例: ?fieldA=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping()
    public JsonResult<List<AiSession>> getListVOMapping(AiSession queryDto, Pagination pagination) throws Exception {
        return super.getViewObjectList(queryDto, pagination, AiSession.class);
    }

    /**
     * 查询会话对象的列表VO记录
     * <p>
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping("/list")
    public JsonResult<List<AiSession>> getList() throws Exception {
        return JsonResult.OK(
                aiSessionService.getEntityList(
                        Wrappers.<AiSessionRecord>lambdaQuery()
                                .eq(AiSessionRecord::getCreateBy, IamSecurityUtils.getCurrentUserId())
                                .orderByDesc(AiSessionRecord::getCreateTime)
                )
        );
    }

    /**
     * 根据id会话对象的详情VO
     *
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult<AiSession> getDetailVOMapping(@PathVariable("id") String id) throws Exception {
        return JsonResult.OK(aiSessionService.getEntity(id));
    }

    /**
     * 创建会话对象数据
     *
     * @param aiSession
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping()
    public JsonResult<?> createModelMapping(@RequestBody AiSession aiSession) throws Exception {
        aiSessionService.createEntity(aiSession);
        return JsonResult.OK(aiSession);
    }


    /**
     * 根据id更新会话对象
     *
     * @param aiSession
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult<?> updateModelMapping(@PathVariable("id") String id, @RequestBody AiSession aiSession) throws Exception {
        boolean success = aiSessionService.updateEntity(aiSession);
        log.debug("更新数据 会话:{} {}", id, success ? "成功" : "失败");
        return JsonResult.OK(success);
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
    public JsonResult<?> deleteModelMapping(@PathVariable("id") String id) throws Exception {
        boolean success = aiSessionService.deleteEntity(id);
        log.debug("删除数据 会话:{} {}", id, success ? "成功" : "失败");
        return JsonResult.OK(success);
    }
}
