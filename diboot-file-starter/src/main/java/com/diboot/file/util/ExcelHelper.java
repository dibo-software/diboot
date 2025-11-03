/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
package com.diboot.file.util;

import cn.idev.excel.FastExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.enums.CacheLocationEnum;
import cn.idev.excel.metadata.FieldWrapper;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.support.ExcelTypeEnum;
import cn.idev.excel.util.ClassUtils;
import cn.idev.excel.write.builder.ExcelWriterBuilder;
import cn.idev.excel.write.builder.ExcelWriterSheetBuilder;
import cn.idev.excel.write.handler.WriteHandler;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.I18n;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.TableHead;
import com.diboot.file.excel.listener.FixedHeadExcelListener;
import com.diboot.file.excel.write.ColorWriteHandler;
import com.diboot.file.excel.write.CommentWriteHandler;
import com.diboot.file.excel.write.MergeWriteHandler;
import com.diboot.file.excel.write.OptionWriteHandler;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * excel数据导入导出工具类
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
@Slf4j
public class ExcelHelper {

    /**
     * 读取ecxel
     *
     * @param inputStream
     * @param listener
     */
    public static void read(InputStream inputStream, ReadListener<?> listener) {
        read(inputStream, listener, null);
    }

    /**
     * 读取ecxel
     *
     * @param inputStream
     * @param listener
     * @param headClazz   ExcelModel.class
     */
    public static <T> void read(InputStream inputStream, ReadListener<T> listener, Class<T> headClazz) {
        read(inputStream, null, listener, headClazz);
    }

    /**
     * 读取ecxel
     *
     * @param inputStream
     * @param excelType   excel类型，为空自动推断
     * @param listener
     * @param headClazz   ExcelModel.class
     */
    public static <T> void read(InputStream inputStream, ExcelTypeEnum excelType, ReadListener<T> listener, Class<T> headClazz) {
        FastExcel.read(inputStream).excelType(excelType).registerReadListener(listener).head(headClazz).sheet().doRead();
    }

    /**
     * 从指定本地文件中读取excel数据
     * @param localFilePath
     * @param listener
     * @return
     * @param <T>
     */
    public static <T extends BaseExcelModel> void read(String localFilePath, FixedHeadExcelListener<T> listener) {
        // 本地读excel文件
        File excelFile = new File(localFilePath);
        if (!excelFile.exists()) {
            throw new BusinessException("exception.business.file.nonexist");
        }
        try {
            FastExcel.read(new FileInputStream(excelFile)).excelType(null)
                    .registerReadListener(listener).head(listener.getExcelModelClass()).sheet().doRead();
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (Exception ex) {
            throw ex;
        }
        if (V.notEmpty(listener.getErrorMsgs())) {
            throw new BusinessException(S.join(listener.getErrorMsgs()));
        }
    }

    /**
     * 简单将数据写入excel文件
     * <p>默认列宽自适应数据长度, 可自定义</p>
     *
     * @param outputStream
     * @param sheetName
     * @param dataList
     * @param writeHandlers
     */
    public static void write(OutputStream outputStream, String sheetName, List<List<String>> headList,
                             List<List<String>> dataList, WriteHandler... writeHandlers) {
        ExcelWriterBuilder write = FastExcel.write(outputStream);
        write = write.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
        for (WriteHandler handler : writeHandlers) {
            write = write.registerWriteHandler(handler);
        }
        ExcelWriterSheetBuilder sheet = write.sheet(sheetName).head(headList);
        sheet.doWrite(dataList);
    }

    /**
     * Excel写入
     * <p>
     * 默认支持：批注写入、单元格下拉选项写入
     *
     * @param outputStream
     * @param clazz         导出的ExcelModel
     * @param dataList
     * @param writeHandlers 写入处理程序
     */
    public static <T> void write(OutputStream outputStream, Class<T> clazz, List<T> dataList, WriteHandler... writeHandlers) {
        write(outputStream, clazz, null, dataList, writeHandlers);
    }

    /**
     * Excel写入
     *
     * @param outputStream
     * @param clazz          导出的ExcelModel
     * @param columnNameList 需要导出的列属性名，为空时导出所有列
     * @param dataList
     * @param writeHandlers  写入处理程序
     */
    public static <T> void write(OutputStream outputStream, Class<T> clazz, Collection<String> columnNameList,
                                 List<T> dataList, WriteHandler... writeHandlers) {
        AtomicBoolean first = new AtomicBoolean(true);
        write(outputStream, clazz, columnNameList, () -> first.getAndSet(false) ? dataList : null, writeHandlers);
    }

    /**
     * 分批多次写入
     *
     * @param outputStream
     * @param clazz
     * @param columnNameList
     * @param dataList
     * @param writeHandlers
     */
    public static <T> void write(OutputStream outputStream, Class<T> clazz, Collection<String> columnNameList,
                                 Supplier<List<T>> dataList, WriteHandler... writeHandlers) {
        write(outputStream, clazz, columnNameList, null, dataList, writeHandlers);
    }

    /**
     * 多次写入
     *
     * @param outputStream
     * @param clazz
     * @param columnNameList
     * @param autoClose      是否自动关闭流
     * @param dataList
     * @param writeHandlers
     */
    public static <T> void write(OutputStream outputStream, Class<T> clazz, Collection<String> columnNameList,
                                 Boolean autoClose, Supplier<List<T>> dataList, WriteHandler... writeHandlers) {
        ExcelWriter excel = FastExcel.write(outputStream).autoCloseStream(autoClose).build();
        writeSheet(excel, "Sheet1", clazz, columnNameList, dataList, writeHandlers);
        excel.finish();
    }

    /**
     * Sheet 写入
     *
     * @param excel         Excel 编写器
     * @param sheetName     Sheet名称
     * @param clazz         导出的ExcelModel
     * @param dataList      数据列表
     * @param writeHandlers 写入处理程序
     * @param <T>           导出的ExcelModel泛型
     */
    public static <T> void writeSheet(ExcelWriter excel, String sheetName, Class<T> clazz, List<T> dataList, WriteHandler... writeHandlers) {
        AtomicBoolean first = new AtomicBoolean(true);
        writeSheet(excel, sheetName, clazz, () -> first.getAndSet(false) ? dataList : null, writeHandlers);
    }

    /**
     * Sheet 写入
     *
     * @param excel         Excel 编写器
     * @param sheetName     Sheet名称
     * @param clazz         导出的ExcelModel
     * @param dataList      数据列表提供者
     * @param writeHandlers 写入处理程序
     * @param <T>           导出的ExcelModel泛型
     */
    public static <T> void writeSheet(ExcelWriter excel, String sheetName, Class<T> clazz, Supplier<List<T>> dataList, WriteHandler... writeHandlers) {
        writeSheet(excel, sheetName, clazz, null, dataList, writeHandlers);
    }

    /**
     * Sheet 写入
     *
     * @param excel          Excel 编写器
     * @param sheetName      Sheet名称
     * @param clazz          导出的ExcelModel
     * @param columnNameList 需要导出的列属性名，为空时导出所有列
     * @param dataList       数据列表提供者
     * @param writeHandlers  写入处理程序
     * @param <T>            导出的ExcelModel泛型
     */
    public static <T> void writeSheet(ExcelWriter excel, String sheetName, Class<T> clazz, Collection<String> columnNameList,
                                 Supplier<List<T>> dataList, WriteHandler... writeHandlers) {
        buildWriteSheet(sheetName, clazz, columnNameList, (commentWriteHandler, writeSheet) -> {
            List<T> list = dataList.get();
            boolean assignableFrom = BaseExcelModel.class.isAssignableFrom(clazz);
            do {
                if (assignableFrom) commentWriteHandler.setDataList((List<? extends BaseExcelModel>) list);
                excel.write(list, writeSheet);
            } while (V.notEmpty(list = dataList.get()));
        }, writeHandlers);
    }

    /**
     * 构建WriteSheet
     * <p>
     * 默认：自列适应宽、单元格下拉选项（验证）写入，批注写入
     *
     * @param sheetName      可指定sheetName
     * @param columnNameList 需要导出的ExcelModel列字段名称列表，为空时导出所有列
     * @param clazz          导出的ExcelModel
     * @param consumer       分批写入
     * @param writeHandlers  写入处理程序
     */
    public static <T> void buildWriteSheet(String sheetName, Class<T> clazz, Collection<String> columnNameList,
                                           BiConsumer<CommentWriteHandler, WriteSheet> consumer, WriteHandler... writeHandlers) {
        ExcelWriterSheetBuilder writerSheet = FastExcel.writerSheet().sheetName(sheetName).head(clazz);
        CommentWriteHandler commentWriteHandler = new CommentWriteHandler();
        writerSheet.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
        writerSheet.registerWriteHandler(new OptionWriteHandler());
        writerSheet.registerWriteHandler(new ColorWriteHandler());
        writerSheet.registerWriteHandler(new MergeWriteHandler());
        writerSheet.registerWriteHandler(commentWriteHandler);
        for (WriteHandler handler : writeHandlers) {
            writerSheet.registerWriteHandler(handler);
        }
        if (V.notEmpty(columnNameList)) {
            writerSheet.includeColumnFieldNames(columnNameList);
        }
        consumer.accept(commentWriteHandler, writerSheet.build());
    }

    /**
     * web 导出excel
     * <p>
     * 默认：自列适应宽、单元格下拉选项（验证）写入，批注写入
     *
     * @param response
     * @param fileName
     * @param clazz         导出的ExcelModel
     * @param dataList
     * @param writeHandlers 写入处理程序
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName, Class<T> clazz, List<T> dataList,
                                       WriteHandler... writeHandlers) {
        exportExcel(response, fileName, clazz, null, dataList, writeHandlers);
    }

    /**
     * web 导出excel
     * <p>
     * 默认 自列适应宽、单元格下拉选项（验证）写入，批注写入
     *
     * @param response
     * @param clazz          导出的ExcelModel
     * @param dataList       导出的数据
     * @param columnNameList 需要导出的ExcelModel列字段名称列表，为空时导出所有列
     * @param writeHandlers  写入处理程序
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName, Class<T> clazz, Collection<String> columnNameList,
                                       List<T> dataList, WriteHandler... writeHandlers) {
        AtomicBoolean first = new AtomicBoolean(true);
        exportExcel(response, fileName, clazz, columnNameList, () -> first.getAndSet(false) ? dataList : null, writeHandlers);
    }

    /**
     * web 导出excel
     * <p>
     * 分批多次写入
     *
     * @param response
     * @param fileName
     * @param clazz
     * @param columnNameList
     * @param dataList
     * @param writeHandlers
     * @param <T>
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName, Class<T> clazz, Collection<String> columnNameList,
                                       Supplier<List<T>> dataList, WriteHandler... writeHandlers) {
        exportExcel(response, fileName, excel -> writeSheet(excel, "Sheet1", clazz, columnNameList, dataList, writeHandlers));
    }

    /**
     * web 导出excel 自定义Sheet写入
     *
     * @param response
     * @param fileName
     * @param writerConsumer
     */
    public static void exportExcel(HttpServletResponse response, String fileName, Consumer<ExcelWriter> writerConsumer) {
        setExportExcelResponseHeader(response, fileName);
        try {
            ExcelWriter excel = FastExcel.write(response.getOutputStream()).autoCloseStream(Boolean.FALSE).build();
            writerConsumer.accept(excel);
            excel.finish();
        } catch (Exception e) {
            log.error("下载文件失败：", e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            try {
                response.getWriter().println(JsonResult.FAIL_OPERATION(I18n.message("exception.business.excel.downloadFailed")));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 设置导出的excel 响应头
     *
     * @param response
     * @param fileName
     */
    public static void setExportExcelResponseHeader(HttpServletResponse response, String fileName) {
        response.setContentType("application/x-msdownload");
        response.setCharacterEncoding("utf-8");
        response.setHeader("code", String.valueOf(Status.OK.code()));
        try {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            response.setHeader("filename", fileName);
            response.setHeader("msg", URLEncoder.encode("操作成功", StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            log.error("不支持字符编码", e);
        }
    }

    /**
     * 获取 Excel 模板中的表头
     *
     * @param clazz ExcelModel
     * @return excel表头映射
     */
    public static List<TableHead> getTableHeads(Class<?> clazz) {
        WriteSheetHolder ws = new WriteSheetHolder();
        GlobalConfiguration configuration = new GlobalConfiguration();
        configuration.setFiledCacheLocation(CacheLocationEnum.MEMORY);
        ws.setGlobalConfiguration(configuration);
        Map<Integer, FieldWrapper> sortedFieldMap = ClassUtils.declaredFields(clazz, ws).getSortedFieldMap();
        TreeMap<Integer, List<String>> headNameMap = new TreeMap<>();
        HashMap<Integer, String> fieldNameMap = new HashMap<>();
        sortedFieldMap.forEach((index, field) -> {
            fieldNameMap.put(index, field.getFieldName());
            headNameMap.put(index, Arrays.asList(field.getHeads()));
        });
        return buildTableHeads(headNameMap, fieldNameMap);
    }

    /**
     * 构建 Excel 表头映射
     *
     * @param headNameMap
     * @param fieldNameMap
     * @return 表头映射
     */
    public static List<TableHead> buildTableHeads(Map<Integer, List<String>> headNameMap, Map<Integer, String> fieldNameMap) {
        List<TableHead> tableHead = new ArrayList<>();
        Map<String, TableHead> hashMap = new HashMap<>();
        int col = Integer.MIN_VALUE;
        for (Map.Entry<Integer, List<String>> entry : headNameMap.entrySet()) {
            if (entry.getKey() - 1 != col) {
                // 表头断列
                hashMap = new HashMap<>();
            }
            col = entry.getKey();
            List<String> list = entry.getValue();
            // 移除尾部重复列名
            boolean bool = true;
            while (list.size() > 1 && bool) {
                int lastIndex = list.size() - 1;
                if (list.get(lastIndex).equals(list.get(lastIndex - 1))) {
                    list.remove(lastIndex);
                } else {
                    bool = false;
                }
            }
            List<String> path = new ArrayList<>();
            // 当前节点
            TableHead node = null;
            int lastIndex = list.size() - 1;
            for (int i = 0; i <= lastIndex; i++) {
                String name = list.get(i);
                path.add(name);
                String key = S.join(path, "→");
                TableHead item;
                if (hashMap.containsKey(key)) {
                    item = hashMap.get(key);
                } else {
                    if (i == 0) {
                        // 避免跨列合并
                        hashMap = new HashMap<>();
                    }
                    item = new TableHead() {{
                        setTitle(name);
                    }};
                    hashMap.put(key, item);
                    if (node == null) {
                        tableHead.add(item);
                    } else {
                        List<TableHead> children = node.getChildren();
                        if (children == null) {
                            // 创建children
                            children = new ArrayList<>();
                            node.setChildren(children);
                        }
                        if (node.getKey() != null) {
                            // 原节点延伸
                            TableHead originalNode = new TableHead();
                            originalNode.setKey(node.getKey());
                            originalNode.setTitle(node.getTitle());
                            node.setKey(null);
                            children.add(originalNode);
                        }
                        children.add(item);
                    }
                }
                node = item;
                if (i == lastIndex) {
                    // 添加key
                    List<TableHead> children = item.getChildren();
                    if (children == null) {
                        item.setKey(fieldNameMap.get(col));
                    } else {
                        // 当前节点延伸
                        TableHead thisNode = new TableHead();
                        thisNode.setKey(fieldNameMap.get(col));
                        thisNode.setTitle(item.getTitle());
                        children.add(thisNode);
                    }
                }
            }
        }
        return tableHead;
    }

}
