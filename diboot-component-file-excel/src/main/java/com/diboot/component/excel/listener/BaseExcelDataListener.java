package com.diboot.component.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.component.excel.entity.BaseExcelDataEntity;
import com.diboot.component.excel.entity.ExcelColumn;
import com.diboot.component.excel.service.ExcelColumnService;
import com.diboot.core.entity.BaseEntity;
import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * excel数据导入导出listener基类
 * @auther wangyl
 * @date 2019-10-9
 */
@Component
public abstract class BaseExcelDataListener <T extends BaseExcelDataEntity, E extends BaseEntity> extends AnalysisEventListener<T> {
    private static final Logger logger = LoggerFactory.getLogger(BaseExcelDataListener.class);

    @Autowired
    protected ExcelColumnService excelColumnService;

    private Map<Integer, String> headMap;//解析出的excel表头
    private List<T> dataList = new ArrayList<>();//解析后的数据实体list
    private List<E> entityList = new ArrayList<>();//可存入数据库的数据实体list
    private List<String> errorMsgs = new ArrayList<>();//错误日志
    private boolean isPreview = false;//是否预览模式，默认否
    private static Map<String, List<ExcelColumn>> excelColumnMap = null;//Excel列定义缓存

    /*
    * 当前一行数据解析成功后的操作
    * */
    @Override
    public void invoke(T data, AnalysisContext context) {
        dataList.add(data);
    }

    /*
    * 所有数据解析成功后的操作
    * */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if(isPreview){
            return;
        }
        checkHeader(headMap);//表头校验
        checkDataList(dataList);//数据校验
        List<String> errors = customVerify(dataList);//自定义数据校验
        if(V.notEmpty(errors)){
            errorMsgs.addAll(errors);
        }
        try {
            convertToEntityList(dataList);
        } catch (Exception e) {
            logger.error("excel数据转化失败",e);
            errorMsgs.add("excel数据解析失败");
        }
        if(V.notEmpty(errorMsgs)){
            return;
        }
        try {
            saveData();
        } catch (Exception e) {
            logger.error("保存excel数据失败", e);
            errorMsgs.add("保存excel数据失败");
        }
    }

    /*
    * 在转换异常、获取其他异常下会调用本接口。
    * 抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
    * */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        int currentRowNum = context.readRowHolder().getRowIndex();
        //数据类型转化异常
        if (exception instanceof ExcelDataConvertException) {
            logger.error("数据转化异常", exception);
            errorMsgs.add("第"+currentRowNum+"行"+exception.getCause().getMessage());
        }else{//其他异常
            logger.error("出现暂未处理的异常：",exception);
            errorMsgs.add(exception.getMessage());
        }
    }

    /*
    * excel表头数据
    * */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        logger.error("解析到一条表头数据：",JSON.toJSONString(headMap));
        this.headMap = headMap;
    }

    /*
     * 校验表头
     * */
    private void checkHeader(Map<Integer, String> headMap) {
        List<ExcelColumn> excelColumnList = null;
        try {
            excelColumnList = getExcelColumnList(getModelClass().getSimpleName());
        } catch (Exception e) {
            logger.error("获取表格列定义失败");
            errorMsgs.add("获取表格列定义失败");
            return;
        }
        if(V.isEmpty(headMap) || V.isEmpty(excelColumnList)){
            logger.error("请设置excel表头");
            errorMsgs.add("请设置excel表头");
            return;
        }
        if(headMap.size() != excelColumnList.size()){
            logger.error("Excel文件中的标题列数与期望不符");
            errorMsgs.add("Excel文件中的标题列数与预期不符,期望为"+excelColumnList.size()+"列");
            return;
        }
        for(ExcelColumn excelColumn : excelColumnList){
            if(V.notEquals(excelColumn.getColName(), headMap.get(excelColumn.getColIndex()-1))){
                errorMsgs.add("标题名:["+headMap.get(excelColumn.getColIndex()-1)+"]与预期不符，请改为["+excelColumn.getColName()+"]");
            }
        }
    }

    /*
     * 校验数据实体list
     * */
    private void checkDataList(List<T> dataList) {
        List<ExcelColumn> excelColumnList = null;
        try {
            excelColumnList = getExcelColumnList(getModelClass().getSimpleName());
        } catch (Exception e) {
            logger.error("获取表格列定义失败", e);
            errorMsgs.add("获取表格列定义失败");
            return;
        }
        if(V.isEmpty(excelColumnList)){
            logger.error("获取表格列定义为空");
            errorMsgs.add("获取表格列定义为空");
            return;
        }
        if(V.notEmpty(dataList)){
            for(int i=0;i<dataList.size();i++){
                checkData(dataList.get(i), i+1,excelColumnList);
            }
        }
    }

    /*
     * 校验数据实体
     * */
    private void checkData(T data, Integer currentRowNum, List<ExcelColumn> excelColumnList) {
        if(V.notEmpty(excelColumnList)){
            for(ExcelColumn excelColumn : excelColumnList){
                String value = BeanUtils.getStringProperty(data,excelColumn.getModelField());
                String error = V.validate(value, excelColumn.getValidation());
                if(V.notEmpty(error)){
                    errorMsgs.add("第"+currentRowNum+"行["+excelColumn.getColName()+"]"+error);
                }
            }
        }
    }

    /*
     * 将解析后的数据实体list转化为可存入数据库的实体list
     * */
    private void convertToEntityList(List<T> dataList) throws Exception{
        entityList = BeanUtils.convertList(dataList, getModelClass());
    }

    /*
     * 自定义数据检验方式，例：数据重复性校验等,返回校验日志信息
     * */
    protected abstract List<String> customVerify(List<T> dataList) ;

    /*
     * 保存解析的数据到数据库
     * */
    private boolean saveData() throws Exception{
        return getBusinessService().createEntities(entityList);
    }

    /***
     * 获取业务的service
     * @return
     */
    protected abstract BaseService getBusinessService();

    /***
     * 获取Model类
     * @return
     */
    protected abstract Class<E> getModelClass();

    /**
     * 加载表格列定义
     */
    public List<ExcelColumn> getExcelColumnList(String modelClass) throws Exception{
        if(excelColumnMap == null){
            excelColumnMap = new ConcurrentHashMap<>();
        }
        List<ExcelColumn> list = excelColumnMap.get(modelClass);
        if(list == null){
            // 构建查询时的排序定义，根据列序号进行升序排列
            LambdaQueryWrapper<ExcelColumn> wrapper = new LambdaQueryWrapper();
            wrapper.eq(ExcelColumn::getModelClass, modelClass)
                    .orderByAsc(ExcelColumn::getColIndex);
            list = excelColumnService.getEntityList(wrapper);
            excelColumnMap.put(modelClass, list);
        }
        return list;
    }

    public List<T> getDataList(){
        return dataList;
    }

    public List<E> getEntityList(){
        return entityList;
    }

    public List<String> getErrorMsgs(){
        return errorMsgs;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }
}
