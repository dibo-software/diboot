package com.example.api.controller.system;

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.file.dto.FileRecordDTO;
import com.diboot.file.entity.FileRecord;
import com.diboot.file.vo.FileRecordVO;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 文件记录 相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/file-record")
@BindPermission(name = "文件记录")
@Slf4j
public class FileRecordController extends BaseCrudRestController<FileRecord> {

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
    public JsonResult getViewObjectListMapping(FileRecordDTO queryDto, Pagination pagination) throws Exception {
        // 默认按createTime倒叙排序
        if(pagination != null && V.isEmpty(pagination.getOrderBy())) {
            pagination.setOrderBy(Pagination.ORDER_BY_CREATE_TIME_DESC);
        }
        return super.getViewObjectList(queryDto, pagination, FileRecordVO.class);
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
        return super.getViewObject(id, FileRecordVO.class);
    }

    /**
     * 更新文件记录备注
     *
     * @param fileRecord
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") String id, @RequestBody FileRecord fileRecord) throws Exception {
        return super.updateEntity(id, new FileRecord().setDescription(S.defaultValueOf(fileRecord.getDescription())));
    }
}
