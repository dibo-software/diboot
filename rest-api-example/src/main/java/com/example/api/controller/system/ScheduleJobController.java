package com.example.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.scheduler.entity.ScheduleJob;
import com.diboot.scheduler.entity.ScheduleJobLog;
import com.diboot.scheduler.service.ScheduleJobLogService;
import com.diboot.scheduler.service.ScheduleJobService;
import com.diboot.scheduler.vo.ScheduleJobLogVO;
import com.diboot.scheduler.vo.ScheduleJobVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * * Copyright © MyCompany
 */
@RestController
@RequestMapping("/schedule-job")
@BindPermission(name = "定时任务")
@Slf4j
public class ScheduleJobController extends BaseCrudRestController<ScheduleJob> {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleJobLogService scheduleJobLogService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: ?field=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult getJobVOListMapping(ScheduleJob entity) throws Exception {
        return super.getViewObjectList(entity, null, ScheduleJobVO.class);
    }

    /**
     * 根据资源id查询ViewObject
     *
     * @param id ID
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult getJobVOMapping(@PathVariable("id") String id) throws Exception {
        return super.getViewObject(id, ScheduleJobVO.class);
    }

    /**
     * 新建job
     *
     * @param scheduleJob
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult createEntityMapping(@Valid @RequestBody ScheduleJob scheduleJob) throws Exception {
        return super.createEntity(scheduleJob);
    }

    /**
     * 更新定时任务job
     *
     * @param scheduleJob
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") String id, @Valid @RequestBody ScheduleJob scheduleJob) throws Exception {
        return super.updateEntity(id, scheduleJob);
    }

    /**
     * 更新定时任务job状态
     *
     * @param id
     * @param action
     */
    @Log(operation = "更新定时任务状态")
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}/{action}")
    public JsonResult updateJobStateMapping(@PathVariable("id") String id, @PathVariable("action") String action) throws Exception {
        scheduleJobService.changeScheduleJobStatus(id, action);
        return JsonResult.OK();
    }


    /**
     * 执行一次定时任务
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = "执行一次定时任务")
    @BindPermission(name = "执行一次定时任务", code = OperationCons.CODE_WRITE)
    @PutMapping("/execute-once/{id}")
    public JsonResult executeOnce(@PathVariable("id") String id) throws Exception {
        scheduleJobService.executeOnceJob(id);
        return JsonResult.OK();
    }

    /**
     * 根据id删除资源对象
     *
     * @param id
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id") String id) throws Exception {
        return super.deleteEntity(id);
    }

    /**
     * job名称列表
     *
     * @return
     */
    @GetMapping("/all-job")
    public JsonResult getAllJob() throws Exception {
        return JsonResult.OK(scheduleJobService.getAllJobs());
    }

    /**
     * 获取定时任务日志list
     *
     * @param entity
     * @param pagination
     * @return
     * @throws Exception
     */
    @Log(operation = "定时日志列表")
    @BindPermission(name = "定时日志列表", code = OperationCons.CODE_READ)
    @GetMapping("/log")
    public JsonResult getJobLogVOListMapping(ScheduleJobLog entity, Pagination pagination) throws Exception {
        QueryWrapper<ScheduleJobLog> queryWrapper = super.buildQueryWrapperByDTO(entity);
        if (pagination != null && V.isEmpty(pagination.getOrderBy())) {
            pagination.setOrderBy(Pagination.ORDER_BY_ID_DESC);
        }
        List<ScheduleJobLogVO> logList = scheduleJobLogService.getViewObjectList(queryWrapper, pagination, ScheduleJobLogVO.class);
        return JsonResult.OK(logList).bindPagination(pagination);
    }

    /**
     * 根据定时任务日志id查询ViewObject
     *
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = "定时日志详情")
    @BindPermission(name = "定时日志详情", code = OperationCons.CODE_READ)
    @GetMapping("/log/{id}")
    public JsonResult getJobLogVOMapping(@PathVariable("id") String id) throws Exception {
        ScheduleJobLogVO jobLog = scheduleJobLogService.getViewObject(id, ScheduleJobLogVO.class);
        return JsonResult.OK(jobLog);
    }

    /**
     * 根据定时任务日志id删除任务日志
     *
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = "删除任务日志")
    @BindPermission(name = "删除任务日志", code = OperationCons.CODE_WRITE)
    @DeleteMapping("/log/{id}")
    public JsonResult deleteJobLogMapping(@PathVariable("id") String id) throws Exception {
        if (id == null) {
            return new JsonResult(Status.FAIL_INVALID_PARAM, "请选择需要删除的条目！");
        }
        boolean success = scheduleJobLogService.deleteEntity(id);
        if (success) {
            log.info("删除任务日志操作成功，{}:{}", ScheduleJobLog.class.getSimpleName(), id);
            return JsonResult.OK();
        } else {
            log.warn("删除任务日志操作未成功，{}:{}", ScheduleJobLog.class.getSimpleName(), id);
            return new JsonResult(Status.FAIL_OPERATION);
        }
    }
}
