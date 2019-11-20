package com.diboot.component.excel.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.diboot.component.excel.entity.BaseExcelDataEntity;
import com.diboot.component.excel.listener.BaseExcelDataListener;
import com.diboot.core.util.V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/***
 * excel数据导入导出工具类
 * @auther wangyl
 * @date 2019-10-9
 */
public class EasyExcelHelper {
    private static final Logger logger = LoggerFactory.getLogger(EasyExcelHelper.class);

    /**
     * 简单读取excel文件数据,当isPreview=false时，保存到数据库
     * @param filePath
     * @param clazz
     * @param listener
     * @return
     */
    public static <T extends BaseExcelDataEntity> List<T> simpleRead(String filePath, Class<T> clazz, BaseExcelDataListener listener, boolean isPreview) throws Exception{
        File file = new File(filePath);
        if(!file.exists()){
            logger.error("找不到指定文件，路径："+filePath);
            throw new Exception("找不到指定文件,导入excel失败");
        }
        listener.setPreview(isPreview);
        EasyExcel.read(filePath, clazz, listener).sheet().doRead();
        return listener.getDataList();
    }

    public static <T extends BaseExcelDataEntity> boolean simpleReadAndSave(String filePath, Class<T> clazz, BaseExcelDataListener listener) throws Exception{
        File file = new File(filePath);
        if(!file.exists()){
            logger.error("找不到指定文件，路径："+filePath);
            throw new Exception("找不到指定文件,导入excel失败");
        }
        listener.setPreview(false);
        EasyExcel.read(filePath, clazz, listener).sheet().doRead();
        if(V.notEmpty(listener.getErrorMsgs())){
            return false;
        }
        return true;
    }

    /**
     * 简单将数据写入excel文件
     * @param filePath
     * @param clazz
     * @param dataList
     * @param <T>
     * @return
     */
    public static <T extends BaseExcelDataEntity> boolean simpleWrite(String filePath, Class<T> clazz, List dataList) throws Exception{
        return simpleWrite(filePath, clazz, null, dataList);
    }

    /**
     * 简单将数据写入excel文件
     * @param filePath
     * @param clazz
     * @param sheetName
     * @param dataList
     * @param <T>
     * @return
     */
    public static <T extends BaseExcelDataEntity> boolean simpleWrite(String filePath, Class<T> clazz, String sheetName, List dataList) throws Exception{
        try {
            EasyExcel.write(filePath, clazz).sheet(sheetName).doWrite(dataList);
        } catch (Exception e) {
            logger.error("数据写入excel文件失败",e);
            return false;
        }
        return true;
    }

    /**
     * 简单将数据写入excel文件,列宽自适应数据长度
     * @param filePath
     * @param clazz
     * @param dataList
     * @param <T>
     * @return
     */
    public static <T extends BaseExcelDataEntity> boolean simpleWriteWithAdaptColumnWidth(String filePath, Class<T> clazz, List dataList) throws Exception{
        try {
            EasyExcel.write(filePath, clazz).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet().doWrite(dataList);
        } catch (Exception e) {
            logger.error("数据写入excel文件失败",e);
            return false;
        }
        return true;
    }

}
