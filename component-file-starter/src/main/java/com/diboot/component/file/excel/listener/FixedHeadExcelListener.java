package com.diboot.component.file.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.diboot.component.file.excel.BaseExcelModel;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/***
 * excel数据导入导出listener基类
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
@Slf4j
public abstract class FixedHeadExcelListener<T extends BaseExcelModel> extends AnalysisEventListener<T> {
    //解析出的excel表头
    protected Map<Integer, String> headMap;
    //解析后的数据实体list
    protected List<T> dataList = new ArrayList<>();
    //错误信息
    private List<String> validateErrorMsgs = new ArrayList<>();
    //是否预览模式，默认否
    protected boolean preview = false;
    // 导入文件的uuid
    protected String uploadFileUuid;

    public FixedHeadExcelListener(){
    }

    public void setPreview(boolean isPreview){
        this.preview = isPreview;
    }

    public void setUploadFileUuid(String uploadFileUuid){
        this.uploadFileUuid = uploadFileUuid;
    }

    /**
    * 当前一行数据解析成功后的操作
    **/
    @Override
    public void invoke(T data, AnalysisContext context) {
        // 绑定行号
        data.setRowIndex(context.readRowHolder().getRowIndex());
        dataList.add(data);
    }

    /**
    * 所有数据解析成功后的操作
    **/
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //表头和数据校验
        validateHeaderAndDataList();
        // 收集校验异常信息
        if(V.notEmpty(dataList)){
            //自定义数据校验
            additionalValidate(dataList);
            // 提取校验结果
            dataList.stream().forEach(data->{
                if(V.notEmpty(data.getValidateError())){
                    validateErrorMsgs.add(data.getRowIndex() + "行: " + data.getValidateError());
                }
            });
        }
        // 有错误 抛出异常
        if(V.notEmpty(this.validateErrorMsgs)){
            throw new BusinessException(Status.FAIL_VALIDATION, S.join(this.validateErrorMsgs, "; "));
        }
        // 非预览模式 才保存
        if(preview == false){
            // 保存数据
            saveData(dataList);
        }
    }

    /**
    * 在转换异常、获取其他异常下会调用本接口。
    * 抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
    **/
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        int currentRowNum = context.readRowHolder().getRowIndex();
        //数据类型转化异常
        if (exception instanceof ExcelDataConvertException) {
            log.error("数据转换异常", exception);
            validateErrorMsgs.add(currentRowNum+"行: "+exception.getCause().getMessage());
        }
        else{//其他异常
            log.error("出现暂未处理的异常：",exception);
            validateErrorMsgs.add("解析异常: "+exception.getMessage());
        }
    }

    /**
    * excel表头数据
    **/
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
    }

    /**
     * 校验表头, 校验数据实体list
     * */
    private void validateHeaderAndDataList() {
        // 校验数据是否合法
        if(V.notEmpty(dataList)){
            dataList.stream().forEach(data->{
                String errMsg = V.validateBean(data);
                if(V.notEmpty(errMsg)){
                    data.addValidateError(errMsg);
                }
            });
        }
    }

    /**
     * 自定义数据检验方式，例：数据重复性校验等,返回校验日志信息
     **/
    protected abstract void additionalValidate(List<T> dataList);

    /**
     * 保存数据
     */
    protected abstract void saveData(List<T> dataList);

    /**
     * 校验错误信息
     * @return
     */
    public List<String> getErrorMsgs(){
        return this.validateErrorMsgs;
    }

    /**
     * 返回表头
     * @return
     */
    public Map<Integer, String> getHeadMap(){
        return this.headMap;
    }

    /**
     * 返回按顺序的string表头
     * @return
     */
    public List<String> getHeadList(){
        Integer[] indexArray = (Integer[]) this.headMap.keySet().toArray();
        Arrays.sort(indexArray);
        List<String> headList = new ArrayList<>(indexArray.length);
        for(Integer idx : indexArray){
            headList.add(this.headMap.get(idx));
        }
        return headList;
    }

    /**
     * 返回数据
     * @return
     */
    public List<T> getDataList(){
        return dataList;
    }


    /***
     * 获取Excel映射的Model类
     * @return
     */
    public Class<T> getExcelModelClass(){
        return BeanUtils.getGenericityClass(this, 0);
    }

}
