package com.diboot.file.example.custom.listener;

import com.diboot.file.excel.listener.FixedHeadExcelListener;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.JSON;
import com.diboot.file.example.custom.Department;
import com.diboot.file.example.custom.DepartmentExcelModel;
import com.diboot.file.example.custom.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <Description>
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/02/19
 */
@Component
public class DepartmentImportListener extends FixedHeadExcelListener<DepartmentExcelModel> {

    /**
     * 自定义扩展的校验
     * @param dataList
     */
    @Override
    protected void additionalValidate(List<DepartmentExcelModel> dataList, Map<String, Object> paramsMap) {
        dataList.stream().forEach(data->{
            if(!"dibo".equals(data.getOrgName())){
                data.addValidateError("单位名称不匹配");
            }
        });
    }

    /**
     * 自定义保存数据
     * @param dataList
     */
    @Override
    protected void saveData(List<DepartmentExcelModel> dataList, Map<String,Object> paramsMap) {
        // 转换数据
        List<Department> departmentList = BeanUtils.convertList(getDataList(), Department.class);
        // 保存数据
        //departmentService.createEntities(departmentList);

        // 获取导入的UploadFileUuid
        //this.uploadFileUuid

        System.out.println(JSON.stringify(departmentList));
    }

}
