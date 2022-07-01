/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ModelBuildEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.file.config.Cons;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.annotation.DuplicateStrategy;
import com.diboot.file.excel.annotation.EmptyStrategy;
import com.diboot.file.excel.annotation.ExcelBindDict;
import com.diboot.file.excel.annotation.ExcelBindField;
import com.diboot.file.excel.cache.ExcelBindAnnoHandler;
import com.diboot.file.excel.write.CommentWriteHandler;
import com.diboot.file.util.ExcelHelper;
import com.diboot.file.util.FileHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 读取Excel
 *
 * @author wind
 * @version v2.4.0
 * @date 2021/11/22
 */
@Slf4j
public abstract class ReadExcelListener<T extends BaseExcelModel> implements ReadListener<T> {

    /**
     * 解析出的excel表头
     */
    @Getter
    @Deprecated
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
     * 预览数据
     */
    @Getter
    private List<T> previewDataList;

    /**
     * 异常信息
     */
    @Getter
    private List<String> exceptionMsgs = null;

    /**
     * 错误信息
     */
    @Getter
    private List<String> errorMsgs;

    @Getter
    private Integer totalCount = 0;
    @Getter
    protected Integer errorCount = 0;

    /**
     * <h3>保存错误数据</h3>
     * 写到excel文件中
     */
    @Getter
    private String errorDataFilePath;
    private ExcelWriter excelWriter;
    private WriteSheet writeSheet;
    private CommentWriteHandler commentWriteHandler;

    /**
     * 获取正确数据数量
     */
    public Integer getProperCount() {
        return totalCount - errorCount;
    }

    /**
     * excel表头数据
     **/
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        this.headMap.clear();
        fieldHeadMap.clear();
        fieldNameMap.clear();
        headNameMap.clear();
        ExcelReadHeadProperty excelReadHeadProperty = context.currentReadHolder().excelReadHeadProperty();
        for (Map.Entry<Integer, Head> entry : excelReadHeadProperty.getHeadMap().entrySet()) {
            Integer index = entry.getKey();
            Head head = entry.getValue();
            String fieldName = head.getFieldName();
            List<String> headNameList = head.getHeadNameList();
            String name = headNameList.get(headNameList.size() - 1);
            this.headMap.put(index, name);
            fieldHeadMap.put(fieldName, name);
            fieldNameMap.put(index, fieldName);
            headNameMap.put(index, headNameList);
        }
    }

    /**
     * 当前一行数据解析成功后的操作
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        // 绑定行号
        data.setRowIndex(context.readRowHolder().getRowIndex());
        // 校验异常
        Set<ConstraintViolation<T>> constraintViolations = V.validateBean(data);
        if (V.notEmpty(constraintViolations)) {
            for (ConstraintViolation<T> violation : constraintViolations) {
                data.addComment(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
        this.cachedData(data);
    }

    /**
     * <h3>完成</h3>
     * 处理结束后需调用一次
     */
    protected void finish() {
        if (excelWriter != null) {
            excelWriter.finish();
        }
        // 有错误 抛出异常
        if (V.notEmpty(this.exceptionMsgs)) {
            throw new BusinessException(Status.FAIL_VALIDATION, S.join(this.exceptionMsgs, "; "));
        }
    }

    /**
     * <h3>异常处理</h3>
     * 修补数据，回写错误
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        //数据类型转化异常
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException dataConvertException = (ExcelDataConvertException) exception;

            Map<Integer, ReadCellData<?>> cellMap = new HashMap<>((Map) context.readRowHolder().getCellMap());
            Map<String, String> errorDataMap = new HashMap<>();
            Map<String, String> errorMsgMap = new HashMap<>();

            Consumer<ExcelDataConvertException> addErrorData = e -> {
                Integer columnIndex = e.getColumnIndex();
                String key = fieldNameMap.get(columnIndex);
                errorDataMap.put(key, cellMap.remove(columnIndex).getStringValue());
                errorMsgMap.put(key, "数据格式转换异常，非期望的数据类型[" + e.getExcelContentProperty().getField().getType().getSimpleName() + "]");
            };
            addErrorData.accept(dataConvertException);

            ReadListener<?> readListener = context.readWorkbookHolder().getReadListenerList().get(0);
            if (readListener instanceof ModelBuildEventListener) {
                while (true) {
                    try {
                        ((ModelBuildEventListener) readListener).invoke(cellMap, context);
                        break;
                    } catch (ExcelDataConvertException convertException) {
                        addErrorData.accept(convertException);
                    }
                }
            } else {
                log.error("数据转换异常", exception);
                StringBuilder errorMsg = new StringBuilder().append("第 ").append(context.readRowHolder().getRowIndex() + 1).append(" 行，");
                errorMsgMap.forEach((fieldName, msg) -> errorMsg.append(fieldHeadMap.get(fieldName)).append("：").append(msg));
                addExceptionMsg(errorMsg.toString());
                return;
            }
            T currentRowAnalysisResult = (T) context.readRowHolder().getCurrentRowAnalysisResult();
            currentRowAnalysisResult.setRowIndex(context.readRowHolder().getRowIndex());
            errorDataMap.forEach(currentRowAnalysisResult::addInvalidValue);
            errorMsgMap.forEach(currentRowAnalysisResult::addComment);
            // 校验异常
            Set<ConstraintViolation<T>> constraintViolations = V.validateBean(currentRowAnalysisResult);
            if (V.notEmpty(constraintViolations)) {
                for (ConstraintViolation<T> violation : constraintViolations) {
                    String propertyName = violation.getPropertyPath().toString();
                    // 剔除解析识别的数据校验
                    if (!errorDataMap.containsKey(propertyName)) {
                        currentRowAnalysisResult.addComment(propertyName, violation.getMessage());
                    }
                }
            }
            this.cachedData(currentRowAnalysisResult);
        } else {
            log.error("出现未预知的异常：", exception);
            addExceptionMsg("第 " + (context.readRowHolder().getRowIndex() + 1) + " 行，解析异常: " + exception.getMessage());
        }
    }

    /**
     * <h3>缓存数据</h3>
     */
    protected abstract void cachedData(T data);

    /**
     * 添加异常信息
     *
     * @param exceptionMsg
     */
    protected void addExceptionMsg(String exceptionMsg) {
        if (this.exceptionMsgs == null) {
            this.exceptionMsgs = new ArrayList<>();
        }
        this.exceptionMsgs.add(exceptionMsg);
    }

    /**
     * <h3>处理数据</h3>
     *
     * @param dataList 数据列表
     */
    protected void handle(List<T> dataList) {
        if (preview && previewDataList == null) {
            int pageSize = BaseConfig.getPageSize();
            previewDataList = dataList.size() > pageSize ? dataList.subList(0, pageSize) : dataList;
        }
        totalCount += dataList.size();
        // 检查 字典和关联字段
        validateOrConvertDictAndRefField(dataList, true);
        // 自定义校验
        additionalValidate(dataList, requestParams);
        dataList.stream().collect(Collectors.groupingBy(this::isProper)).forEach((proper, list) -> {
            if (proper) {
                if (!preview) {
                    // 转换 字典和关联字段
                    validateOrConvertDictAndRefField(list, false);
                    this.saveData(list, requestParams);
                }
            } else {
                this.errorData(list);
            }
        });
    }

    /**
     * <h3>是否为正确数据</h3>
     * 供子类重写
     *
     * @param data 数据
     * @return
     */
    protected boolean isProper(T data) {
        return V.isEmpty(data.getComment());
    }

    /**
     * <h3>错误数据处理</h3>
     *
     * @param dataList
     */
    protected void errorData(List<T> dataList) {
        errorCount += dataList.size();
        if (errorMsgs == null || errorMsgs.size() < BaseConfig.getPageSize()) {
            if (errorMsgs == null) {
                errorMsgs = new ArrayList<>();
            }
            StringBuilder builder = new StringBuilder();
            dataList.stream().limit(BaseConfig.getPageSize() - errorMsgs.size()).map(data -> {
                builder.setLength(0);
                builder.append("第 ").append(data.getRowIndex() + 1).append(" 行，");
                data.getComment().forEach((k, v) -> {
                    builder.append(fieldHeadMap.get(k)).append("：")
                            .append(S.getIfEmpty(data.getField2InvalidValueMap().get(k), () -> S.defaultValueOf(BeanUtils.getProperty(data, k)))).append(" ")
                            .append(S.join(v)).append("；");
                });
                return builder.toString();
            }).forEach(errorMsgs::add);
        }
        if (preview) {
            return;
        }
        if (excelWriter == null) {
            if (FileHelper.isLocalStorage()) {
                errorDataFilePath = FileHelper.getFullPath(S.newUuid() + ".xlsx");
            } else {
                errorDataFilePath = FileHelper.getSystemTempDir() + BaseConfig.getProperty("spring.application.name", "diboot")
                        + Cons.FILE_PATH_SEPARATOR + S.newUuid() + ".xlsx";
            }
            FileHelper.makeDirectory(errorDataFilePath);
            excelWriter = EasyExcel.write(errorDataFilePath, getExcelModelClass()).build();
            ExcelHelper.buildWriteSheet(null, (commentWriteHandler, writeSheet) -> {
                this.commentWriteHandler = commentWriteHandler;
                this.writeSheet = writeSheet;
            });
        }
        commentWriteHandler.setDataList(dataList);
        excelWriter.write(dataList, writeSheet);
    }

    /**
     * 检查或转换字典和关联字段
     */
    protected void validateOrConvertDictAndRefField(List<T> dataList, boolean preview) {
        Class<T> tClass = getExcelModelClass();
        Map<String, Annotation> fieldName2BindAnnoMap = ExcelBindAnnoHandler.getField2BindAnnoMap(tClass);
        if (fieldName2BindAnnoMap.isEmpty()) {
            return;
        }
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
                    String setFieldName = S.defaultIfEmpty(excelBindField.setIdField(), entry.getKey());
                    if (V.isEmpty(valList)) {
                        if (excelBindField.empty().equals(EmptyStrategy.SET_0)) {
                            // 非预览时 赋值
                            if (!preview) {
                                BeanUtils.setProperty(data, setFieldName, 0);
                            }
                        } else if (excelBindField.empty().equals(EmptyStrategy.WARN)) {
                            data.addComment(entry.getKey(), "关联值不存在");
                        } else if (excelBindField.empty().equals(EmptyStrategy.IGNORE) && valueNotNull) {
                            log.warn("非空字段 {} 不应设置 EmptyStrategy.IGNORE.", entry.getKey());
                        }
                    } else if (valList.size() == 1) {
                        // 非预览时 赋值
                        if (!preview) {
                            BeanUtils.setProperty(data, setFieldName, valList.get(0));
                        }
                    } else {
                        if (excelBindField.duplicate().equals(DuplicateStrategy.WARN)) {
                            data.addComment(entry.getKey(), "匹配到多个关联值");
                        } else if (excelBindField.duplicate().equals(DuplicateStrategy.FIRST)) {
                            // 非预览时 赋值
                            if (!preview) {
                                BeanUtils.setProperty(data, setFieldName, valList.get(0));
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
                            data.addComment(entry.getKey(), "无匹配字典");
                            continue;
                        }
                    }
                    // 非预览时 赋值
                    if (!preview && V.notEmpty(valList)) {
                        BeanUtils.setProperty(data, entry.getKey(), valList.get(0));
                    }
                }
            }
        }
    }

    /**
     * <h3>自定义数据检验方式</h3>
     * 例：数据重复性校验等，添加校验批注信息
     */
    protected abstract void additionalValidate(List<T> dataList, Map<String, Object> requestParams);

    /**
     * <h3>保存数据</h3>
     */
    protected abstract void saveData(List<T> dataList, Map<String, Object> requestParams);

    /**
     * 获取Excel表头
     *
     * @return 表头映射
     */
    public List<ExcelHelper.TableHead> getTableHead() {
        return ExcelHelper.buildTableHead(headNameMap, fieldNameMap);
    }

    /**
     * 获取Excel映射的Model类
     */
    public Class<T> getExcelModelClass() {
        return BeanUtils.getGenericityClass(this, 0);
    }

}
