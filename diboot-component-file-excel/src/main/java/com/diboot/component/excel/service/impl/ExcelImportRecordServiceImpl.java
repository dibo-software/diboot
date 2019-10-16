package com.diboot.component.excel.service.impl;

import com.diboot.component.excel.entity.ExcelImportRecord;
import com.diboot.component.excel.mapper.ExcelImportRecordMapper;
import com.diboot.component.excel.service.ExcelImportRecordService;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.service.impl.BaseServiceImpl;
import com.diboot.core.util.V;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lishuaifei
 * @description
 * @creatime 2019-07-11 17:06
 */
@Service
public class ExcelImportRecordServiceImpl extends BaseServiceImpl<ExcelImportRecordMapper, ExcelImportRecord> implements ExcelImportRecordService {

    @Override
    public boolean batchCreateRecords(String fileUid, List<? extends BaseEntity> modelList) {
        List<ExcelImportRecord> recordList = new ArrayList<>();
        if(V.notEmpty(modelList)){
            for(BaseEntity entity : modelList){
                ExcelImportRecord record = new ExcelImportRecord();
                record.setFileUuid(fileUid);
                record.setRelObjType(entity.getClass().getSimpleName());
                record.setRelObjId(entity.getId());
                recordList.add(record);
            }
        }
        return createEntities(recordList);
    }
}
