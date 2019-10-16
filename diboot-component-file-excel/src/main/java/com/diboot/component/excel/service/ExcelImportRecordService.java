package com.diboot.component.excel.service;

import com.diboot.component.excel.entity.ExcelImportRecord;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.service.BaseService;

import java.util.List;

/**
 * @author Lishuaifei
 * @description
 * @creatime 2019-07-11 17:05
 */
public interface ExcelImportRecordService extends BaseService<ExcelImportRecord> {

    boolean batchCreateRecords(String fileUid, List<? extends BaseEntity> modelList);

}
