/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.file.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.annotation.DuplicateStrategy;
import com.diboot.file.excel.annotation.EmptyStrategy;
import com.diboot.file.excel.annotation.ExcelBindDict;
import com.diboot.file.excel.annotation.ExcelBindField;
import com.diboot.file.excel.cache.ExcelBindAnnoHandler;
import com.diboot.file.util.ExcelHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * excel数据导入导出listener基类
 *
 * @author wangyl@dibo.ltd
 * @date 2019-10-9
 */
@Slf4j
public abstract class FixedHeadExcelListener<T extends BaseExcelModel> implements ReadListener<T> {
    /**
     * 解析出的excel表头
     */
    @Getter
    protected Map<Integer, String> headMap = new HashMap<>();
    /**
     * 字段名-列名的映射
     */
    @Getter
    private final Map<String, String> fieldHeadMap = new HashMap<>();
    /**
     * 属性名映射
     */
    @Getter
    private final HashMap<Integer, String> fieldNameMap = new HashMap<>();
    /**
     * 列名映射
     */
    private final TreeMap<Integer, List<String>> headNameMap = new TreeMap<>();
    /**
     * 解析后的数据实体list
     */
    @Getter
    private final List<T> dataList = new ArrayList<>();
    @Getter
    private List<T> errorDataList;
    @Getter
    private List<T> properDataList;
    /**
     * 异常信息
     */
    @Getter
    private List<String> exceptionMsgs = null;
    /**
     * 错误信息
     */
    @Getter
    private List<String> errorMsgs = null;
    /**
     * 注入request
     */
    @Setter
    private Map<String, Object> requestParams;
    /**
     * 是否为预览模式
     */
    @Setter
    protected boolean preview = false;
    /**
     * 导入文件的uuid
     */
    @Setter
    protected String uploadFileUuid;

    /**
     * excel表头数据
     **/
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        ExcelReadHeadProperty excelReadHeadProperty = context.currentReadHolder().excelReadHeadProperty();
        for (Map.Entry<Integer, Head> entry : excelReadHeadProperty.getHeadMap().entrySet()) {
            Integer index = entry.getKey();
            Head head = entry.getValue();
            String fieldName = head.getFieldName();
            List<String> headNameList = head.getHeadNameList();
            String name = headNameList.get(headNameList.size() - 1);
            this.headMap.put(index, name);
            fieldHeadMap.put(fieldName, name);
            headNameMap.put(index, headNameList);
            fieldNameMap.put(index, fieldName);
        }
    }

    /**
     * 当前一行数据解析成功后的操作
     **/
    @Override
    public void invoke(T data, AnalysisContext context) {
        // 绑定行号
        data.setRowIndex(context.readRowHolder().getRowIndex());
        dataList.add(data);
        // 校验异常
        Set<ConstraintViolation<T>> constraintViolations = V.validateBean(data);
        if (V.notEmpty(constraintViolations)) {
            for (ConstraintViolation<T> violation : constraintViolations) {
                data.addComment(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
    }

    /**
     * 所有数据解析成功后的操作
     **/
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (V.isEmpty(dataList)) {
            return;
        }
        // 检查或转换字典和关联字段
        validateOrConvertDictAndRefField();
        // 自定义数据校验
        additionalValidate(dataList, requestParams);
        // 获取数据异常
        StringBuilder warnMsg = new StringBuilder();
        dataList.forEach(data -> {
            int rowNum = data.getRowIndex() + 1;
            if (V.notEmpty(data.getComment()) || V.notEmpty(data.getValidateError())) {
                if (errorDataList == null) {
                    errorDataList = new ArrayList<>();
                }
                if (V.notEmpty(data.getComment()) && errorDataList.size() <= 20) {
                    warnMsg.append("第 ").append(rowNum).append(" 行: \n");
                    data.getComment().forEach((k, v) -> {
                        warnMsg.append(fieldHeadMap.get(k)).append("：").append(S.join(v)).append("；\n");
                    });
                    if (errorMsgs == null) {
                        errorMsgs = new ArrayList<>();
                    }
                    errorMsgs.add(warnMsg.toString());
                    warnMsg.setLength(0);
                }
                // 提取错误信息
                if (V.notEmpty(data.getValidateError())) {
                    addErrorMsg(rowNum + "行: " + data.getValidateError());
                }
                errorDataList.add(data);
            } else {
                if (properDataList == null) {
                    properDataList = new ArrayList<>();
                }
                properDataList.add(data);
            }
        });
        // 有错误 抛出异常
        if (V.notEmpty(this.exceptionMsgs)) {
            throw new BusinessException(Status.FAIL_VALIDATION, S.join(this.exceptionMsgs, "; "));
        }
        // 保存
        if (preview == false && V.notEmpty(properDataList)) {
            // 保存数据
            saveData(properDataList, requestParams);
        }
    }

    /**
     * 在转换异常、获取其他异常下回调并停止读取
     **/
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        int currentRowNum = context.readRowHolder().getRowIndex() + 1;
        //数据类型转化异常
        if (exception instanceof ExcelDataConvertException) {
            log.error("数据转换异常", exception);
            ExcelDataConvertException ex = (ExcelDataConvertException) exception;
            String type = ex.getExcelContentProperty().getField().getType().getSimpleName();
            String data = ex.getCellData().getStringValue();
            String errorMsg = currentRowNum + "行" + headMap.get(ex.getColumnIndex())
                    + ": 数据格式转换异常，'" + data + "' 非期望的数据类型[" + type + "]";
            addErrorMsg(errorMsg);
        } else {//其他异常
            log.error("出现未预知的异常：", exception);
            addErrorMsg(currentRowNum + "行，解析异常: " + exception.getMessage());
        }
    }

    /**
     * 校验或转换字典name-value
     */
    protected void validateOrConvertDictAndRefField() {
        Map<String, Annotation> fieldName2BindAnnoMap = ExcelBindAnnoHandler.getField2BindAnnoMap(this.getExcelModelClass());
        if (fieldName2BindAnnoMap.isEmpty()) {
            return;
        }
        Class<T> tClass = getExcelModelClass();
        for (Map.Entry<String, Annotation> entry : fieldName2BindAnnoMap.entrySet()) {
            List nameList = (entry.getValue() instanceof ExcelBindField) ? BeanUtils.collectToList(dataList, entry.getKey()) : null;
            Map<String, List> map = ExcelBindAnnoHandler.convertToNameValueMap(entry.getValue(), nameList);
            Field field = BeanUtils.extractField(tClass, entry.getKey());
            boolean valueNotNull = (field.getAnnotation(NotNull.class) != null);
            for (T data : dataList) {
                String name = BeanUtils.getStringProperty(data, entry.getKey());
                if (S.isEmpty(name)) {
                    continue;
                }
                List valList = map.get(name);
                if (entry.getValue() instanceof ExcelBindField) {
                    ExcelBindField excelBindField = (ExcelBindField) entry.getValue();
                    if (V.isEmpty(valList)) {
                        if (excelBindField.empty().equals(EmptyStrategy.SET_0)) {
                            // 非预览时 赋值
                            if (!preview) {
                                BeanUtils.setProperty(data, excelBindField.setIdField(), 0);
                            }
                        } else if (excelBindField.empty().equals(EmptyStrategy.WARN)) {
                            data.addComment(entry.getKey(), name + " 值不存在");
                        } else if (excelBindField.empty().equals(EmptyStrategy.IGNORE) && valueNotNull) {
                            log.warn("非空字段 {} 不应设置 EmptyStrategy.IGNORE.", entry.getKey());
                        }
                    } else if (valList.size() == 1) {
                        // 非预览时 赋值
                        if (!preview) {
                            BeanUtils.setProperty(data, excelBindField.setIdField(), valList.get(0));
                        }
                    } else {
                        if (excelBindField.duplicate().equals(DuplicateStrategy.WARN)) {
                            data.addComment(entry.getKey(), name + " 匹配到多个值");
                        } else if (excelBindField.duplicate().equals(DuplicateStrategy.FIRST)) {
                            // 非预览时 赋值
                            if (!preview) {
                                BeanUtils.setProperty(data, excelBindField.setIdField(), valList.get(0));
                            }
                        }
                    }
                } else if (entry.getValue() instanceof ExcelBindDict || entry.getValue() instanceof BindDict) {
                    if (V.isEmpty(valList)) {
                        if (name.contains(S.SEPARATOR)) {
                            valList = new LinkedList<>();
                            for (String item : name.split(S.SEPARATOR)) {
                                valList.addAll(map.get(item));
                            }
                            if (valList.size() > 0) {
                                valList.add(0, S.join(valList));
                            }
                        }
                        // 非空才报错
                        if (valueNotNull && V.isEmpty(valList)) {
                            data.addComment(entry.getKey(), name + " 无匹配字典");
                            continue;
                        }
                    }
                    // 非预览时 赋值
                    if (!preview) {
                        BeanUtils.setProperty(data, entry.getKey(), valList.get(0));
                    }
                }
            }
        }
    }

    /**
     * 自定义数据检验方式，例：数据重复性校验等,返回校验日志信息
     **/
    protected abstract void additionalValidate(List<T> dataList, Map<String, Object> requestParams);

    /**
     * 保存数据
     */
    protected abstract void saveData(List<T> dataList, Map<String, Object> requestParams);

    /**
     * 获取Excel表头
     *
     * @return 表头映射
     */
    public List<ExcelHelper.TableHead> getTableHead() {
        return  ExcelHelper.buildTableHead(headNameMap, fieldNameMap);
    }

    /***
     * 获取Excel映射的Model类
     * @return
     */
    public Class<T> getExcelModelClass() {
        return BeanUtils.getGenericityClass(this, 0);
    }

    /**
     * 添加错误信息
     *
     * @param errorMsg
     */
    private void addErrorMsg(String errorMsg) {
        if (this.exceptionMsgs == null) {
            this.exceptionMsgs = new ArrayList<>();
        }
        this.exceptionMsgs.add(errorMsg);
    }
}
