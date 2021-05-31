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
package com.diboot.file.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.file.excel.BaseExcelModel;
import com.diboot.file.excel.listener.DynamicHeadExcelListener;
import com.diboot.file.excel.listener.FixedHeadExcelListener;
import com.diboot.file.excel.write.OptionWriteHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/***
 * excel数据导入导出工具类
 * @auther wangyl@dibo.ltd
 * @date 2019-10-9
 */
@Slf4j
public class ExcelHelper {

    /**
     * 预览读取excel文件数据
     *
     * @param filePath
     * @param listener
     * @return
     */
    public static <T extends BaseExcelModel> boolean previewReadExcel(String filePath, FixedHeadExcelListener listener) throws Exception {
        listener.setPreview(true);
        return readAndSaveExcel(filePath, listener);
    }

    /**
     * 预览读取excel文件数据
     *
     * @param inputStream
     * @param listener
     * @return
     */
    public static <T extends BaseExcelModel> boolean previewReadExcel(InputStream inputStream, FixedHeadExcelListener listener) throws Exception {
        listener.setPreview(true);
        return readAndSaveExcel(inputStream, listener);
    }

    /**
     * 读取excel文件数据并保存到数据库
     *
     * @param filePath
     * @param listener
     * @return
     */
    public static <T extends BaseExcelModel> boolean readAndSaveExcel(String filePath, FixedHeadExcelListener listener) throws Exception {
        File excel = getExcelFile(filePath);
        Class<T> headClazz = BeanUtils.getGenericityClass(listener, 0);
        EasyExcel.read(excel).registerReadListener(listener).head(headClazz).sheet().doRead();
        return true;
    }

    /**
     * 读取excel流文件数据并保存到数据库
     *
     * @param listener
     * @return
     */
    public static <T extends BaseExcelModel> boolean readAndSaveExcel(InputStream inputStream, FixedHeadExcelListener listener) throws Exception {
        Class<T> headClazz = BeanUtils.getGenericityClass(listener, 0);
        EasyExcel.read(inputStream).registerReadListener(listener).head(headClazz).sheet().doRead();
        return true;
    }

    /**
     * 读取非确定/动态表头的excel文件数据
     *
     * @param filePath
     * @return
     */
    public static boolean readDynamicHeadExcel(String filePath, DynamicHeadExcelListener listener) {
        File excel = getExcelFile(filePath);
        EasyExcel.read(excel).registerReadListener(listener).sheet().doRead();
        return true;
    }

    /**
     * 读取非确定/动态表头的excel文件数据
     *
     * @param inputStream
     * @param listener
     * @return
     */
    public static boolean readDynamicHeadExcel(InputStream inputStream, DynamicHeadExcelListener listener) {
        EasyExcel.read(inputStream).registerReadListener(listener).sheet().doRead();
        return true;
    }

    /**
     * 简单将数据写入excel文件,列宽自适应数据长度
     *
     * @param filePath
     * @param sheetName
     * @param dataList
     * @return
     */
    public static boolean writeDynamicData(String filePath, String sheetName, List<List<String>> dataList) throws Exception {
        try {
            EasyExcel.write(filePath).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(sheetName).doWrite(dataList);
            return true;
        } catch (Exception e) {
            log.error("数据写入excel文件失败", e);
            return false;
        }
    }

    /**
     * 简单将数据写入excel文件,列宽自适应数据长度
     *
     * @param filePath
     * @param sheetName
     * @param dataList
     * @param <T>
     * @return
     */
    public static <T extends BaseExcelModel> boolean writeData(String filePath, String sheetName, List<T> dataList) throws Exception {
        try {
            if (V.isEmpty(dataList)) {
                return writeDynamicData(filePath, sheetName, Collections.emptyList());
            }
            Class<T> tClass = (Class<T>) dataList.get(0).getClass();
            EasyExcel.write(filePath, tClass).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(sheetName).doWrite(dataList);
            return true;
        } catch (Exception e) {
            log.error("数据写入excel文件失败", e);
            return false;
        }
    }

    /**
     * web 导出excel
     *
     * @param response
     * @param clazz    导出的类
     * @param data     导出的数据
     * @param <T>
     * @throws Exception
     */
    public static <T extends BaseExcelModel> void exportExcel(HttpServletResponse response, String fileName, Class<T> clazz, List<T> data) throws Exception {
        try {
            setExportExcelResponseHeader(response, fileName);
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), clazz)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerWriteHandler(new OptionWriteHandler(clazz))
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("sheet1")
                    .doWrite(data);
        } catch (Exception e) {
            log.error("下载文件失败：", e);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setHeader("err-code", String.valueOf(Status.FAIL_OPERATION.code()));
            response.setHeader("err-msg", URLEncoder.encode("下载文件失败", StandardCharsets.UTF_8.name()));
        }
    }

    /**
     * 设置导出的excel 响应头
     *
     * @param response
     * @param fileName
     * @throws Exception
     */
    public static void setExportExcelResponseHeader(HttpServletResponse response, String fileName) throws Exception {
        response.setContentType("application/x-msdownload");
        response.setCharacterEncoding("utf-8");
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.setHeader("filename", fileName);
        response.setHeader("err-code", String.valueOf(Status.OK.code()));
        response.setHeader("err-msg", URLEncoder.encode("操作成功", StandardCharsets.UTF_8.name()));
    }

    /**
     * 获取本地文件内容
     *
     * @param filePath
     * @return
     */
    private static File getExcelFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("找不到指定文件，路径：" + filePath);
            throw new BusinessException(Status.FAIL_EXCEPTION, "找不到指定文件,导入excel失败");
        }
        return file;
    }


}
