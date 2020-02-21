package com.diboot.component.file.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.diboot.component.file.excel.BaseExcelModel;
import com.diboot.component.file.excel.listener.DynamicHeadExcelListener;
import com.diboot.component.file.excel.listener.FixedHeadExcelListener;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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
     * 预览读取excel文件数据,不保存到数据库
     * @param filePath
     * @param listener
     * @return
     */
    public static <T extends BaseExcelModel> List<T> previewRead(String filePath, FixedHeadExcelListener listener) throws Exception{
        File excel = getExcelFile(filePath);
        listener.setPreview(true);
        Class<T> headClazz = BeanUtils.getGenericityClass(listener, 0);
        EasyExcel.read(excel).head(headClazz).registerReadListener(listener).sheet().doRead();
        return listener.getDataList();
    }

    /**
     * 简单读取excel文件数据并保存到数据库
     * @param filePath
     * @param listener
     * @return
     */
    public static <T extends BaseExcelModel> boolean readAndSave(String filePath, FixedHeadExcelListener listener) throws Exception{
        File excel = getExcelFile(filePath);
        Class<T> headClazz = BeanUtils.getGenericityClass(listener, 0);
        EasyExcel.read(excel).registerReadListener(listener).head(headClazz).sheet().doRead();
        return true;
    }

    /**
     * 读取非确定/动态表头的excel文件数据
     * @param filePath
     * @return
     */
    public static boolean readDynamicHeadExcel(String filePath, DynamicHeadExcelListener listener){
        File excel = getExcelFile(filePath);
        EasyExcel.read(excel).registerReadListener(listener).sheet().doRead();
        return true;
    }

    /**
     * 简单将数据写入excel文件,列宽自适应数据长度
     * @param filePath
     * @param sheetName
     * @param dataList
     * @return
     */
    public static boolean writeData(String filePath, String sheetName, List<List<String>> dataList) throws Exception{
        try {
            EasyExcel.write(filePath).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(sheetName).doWrite(dataList);
            return true;
        }
        catch (Exception e) {
            log.error("数据写入excel文件失败",e);
            return false;
        }
    }

    /**
     * 简单将数据写入excel文件,列宽自适应数据长度
     * @param filePath
     * @param sheetName
     * @param dataList
     * @param <T>
     * @return
     */
    public static <T extends BaseExcelModel> boolean writeDate(String filePath, String sheetName, List<T> dataList) throws Exception{
        try {
            if(V.isEmpty(dataList)){
                return writeData(filePath, sheetName, Collections.emptyList());
            }
            Class<T> tClass = (Class<T>) dataList.get(0).getClass();
            EasyExcel.write(filePath, tClass).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(sheetName).doWrite(dataList);
            return true;
        }
        catch (Exception e) {
            log.error("数据写入excel文件失败",e);
            return false;
        }
    }

    private static File getExcelFile(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            log.error("找不到指定文件，路径："+filePath);
            throw new BusinessException(Status.FAIL_EXCEPTION, "找不到指定文件,导入excel失败");
        }
        return file;
    }


}
