package com.example.api.controller.system;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.ai.client.AiClient;
import com.diboot.ai.common.AiMessage;
import com.diboot.ai.common.request.AiChatRequest;
import com.diboot.ai.common.request.AiEnum;
import com.diboot.ai.common.response.AiChatResponse;
import com.diboot.ai.common.response.AiChoice;
import com.diboot.ai.config.AiConfiguration;
import com.diboot.ai.entity.AiSessionRecord;
import com.diboot.ai.service.AiSessionRecordService;
import com.diboot.ai.vo.AiSessionRecordVO;
import com.diboot.core.config.Cons;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AI会话
 *
 * @author : uu
 * @version : v1.0
 * @Date 2024/4/29
 */
@RestController
@RequestMapping("/ai-session-record")
@BindPermission(name = "AI 会话记录")
@Slf4j
public class AiSessionRecordController extends BaseCrudRestController<AiSessionRecord> {

    @Autowired
    private AiClient client;

    @Autowired
    private AiSessionRecordService aiSessionRecordService;

    /**
     * 查询会话记录对象的列表VO记录
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
    public JsonResult<List<AiSessionRecordVO>> getListVOMapping(AiSessionRecord queryDto, Pagination pagination) throws Exception {
        return super.getViewObjectList(queryDto, pagination, AiSessionRecordVO.class);
    }

    /**
     * 根据id会话记录对象的详情VO
     *
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult<AiSessionRecord> getDetailVOMapping(@PathVariable("id") String id) throws Exception {
        return JsonResult.OK(aiSessionRecordService.getEntity(id));
    }

    /**
     * 创建会话记录对象数据
     *
     * @param aiSessionRecord
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping()
    public JsonResult<?> createModelMapping(@RequestBody AiSessionRecord aiSessionRecord) throws Exception {
        return JsonResult.OK(aiSessionRecordService.createEntity(aiSessionRecord));
    }

    /**
     * 获取模型列表
     */
    @GetMapping("/models")
    public JsonResult<?> getAiModels() {
        List<String> list = new ArrayList<>();
        AiConfiguration config = client.getConfiguration();
        if (config.getQwen() != null) list.add("qwen");
        if (config.getKimi() != null) list.add("kimi");
        if (config.getWenxin() != null) list.add("wenxin");
        if (config.getDeepseek() != null) list.add("deepseek");
        return JsonResult.OK(list);
    }

    /**
     * AI 问答
     * @param aiChatRequest
     * @param response
     * @return
     * @throws Exception
     */
    @Log(operation = "AI 问答")
    @BindPermission(name = "AI 问答", code = OperationCons.CODE_WRITE)
    @PostMapping("/chat")
    public SseEmitter chat(@RequestBody AiChatRequest aiChatRequest, HttpServletResponse response) throws Exception {
        // 配置响应为：流式输出、编码、禁用缓存
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(Cons.CHARSET_UTF8);
        response.setHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue());

        SseEmitter sseEmitter = new SseEmitter(120_000L);
        try {
            client.executeStream(aiChatRequest, new EventSourceListener() {
                @Override
                public void onClosed(@NotNull EventSource eventSource) {
                    sseEmitter.complete();
                }

                @Override
                public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                    try {
                        sseEmitter.send(data);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                    sseEmitter.completeWithError(t);
                }
            });
        } catch (Exception e) {
            // 异常处理
            AiChatResponse chatResponse = new AiChatResponse()
                    .setChoices(Collections.singletonList(new AiChoice().setFinishReason("stop")
                            .setMessage((new AiMessage()).setRole(AiEnum.Role.ASSISTANT.getCode()).setContent(e.getMessage()))));

            sseEmitter.send(JSON.stringify(chatResponse));

            // 关闭连接
            sseEmitter.complete();

        }

        return sseEmitter;
    }

    /**
     * 查询会话所有记录
     * <p>
     * url请求参数示例: ?fieldA=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = "获取会话下所有记录")
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping("/list-by-session-id/{sessionId}")
    public JsonResult<List<AiMessage>> getList(@PathVariable("sessionId") String sessionId) throws Exception {
        List<AiSessionRecord> entityList = aiSessionRecordService.getEntityList(
                Wrappers.<AiSessionRecord>lambdaQuery()
                        .eq(AiSessionRecord::getSessionId, sessionId)
                        .orderByAsc(AiSessionRecord::getCreateTime)
        );
        List<AiMessage> aiMessages = new ArrayList<>();
        if (V.notEmpty(entityList)) {
            for (AiSessionRecord aiSessionRecord : entityList) {
                if (V.notEmpty(aiSessionRecord.getRequestBody()) && V.notEmpty(aiSessionRecord.getResponseBody())) {
                    aiMessages.add(JSON.parseObject(aiSessionRecord.getRequestBody(), AiMessage.class));
                    aiMessages.add(JSON.parseObject(aiSessionRecord.getResponseBody(), AiMessage.class));
                }
            }
        }
        return JsonResult.OK(aiMessages);
    }

}
