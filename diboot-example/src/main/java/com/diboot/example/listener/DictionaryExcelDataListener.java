package com.diboot.example.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.BaseService;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import com.diboot.example.entity.DictionaryExcelData;
import com.diboot.component.excel.listener.BaseExcelDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Scope("prototype")
public class DictionaryExcelDataListener extends BaseExcelDataListener<DictionaryExcelData, Dictionary> {
    private static final Logger logger = LoggerFactory.getLogger(DictionaryExcelDataListener.class);

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    protected List<String> customVerify(List<DictionaryExcelData> dataList) {
        if(V.isEmpty(dataList)){
            return null;
        }
        List<String> errorMsgs = new ArrayList();
        Set isRepeatSet = new HashSet();
        for(int i=0;i<dataList.size();i++){
            String error = "";
            String type = dataList.get(i).getType();
            if(!isRepeatSet.add(type)){
                error += "表格中的元数据类型重复";
            }

            if (V.notEmpty(error)) {
                errorMsgs.add("[第" + (i + 1) + "行]: " + error);
            }
        }

       return errorMsgs;
    }

    @Override
    protected BaseService getBusinessService() {
        return dictionaryService;
    }

    @Override
    protected Class<Dictionary> getModelClass() {
        return Dictionary.class;
    }
}
