package com.diboot.file.example.test;

import com.diboot.component.file.excel.listener.DynamicHeadExcelListener;
import com.diboot.component.file.util.ExcelHelper;
import com.diboot.core.util.JSON;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * 非固定动态表头excel读测试
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/02/19
 */
public class DynamicHeadExcelReadTest extends ExcelWriteTest {

    @Test
    public void testDynamicHeadRead(){
        // 定义listener
        DynamicHeadExcelListener listener = new DynamicHeadExcelListener(){
            @Override
            protected void saveData(Map<Integer, String> headMap, List<Map<Integer, String>> dataList) {
                //throw new BusinessException(Status.FAIL_VALIDATION, "测试");
                //保存数据
            }
        };
        try{
            prepareNormalDataExcel();

            boolean success = ExcelHelper.readDynamicHeadExcel(getTempFilePath(), listener);
            Assert.assertTrue(success);
            Assert.assertNotNull(listener.getDataList());
            System.out.println(JSON.stringify(listener.getHeadMap()));
            System.out.println(JSON.stringify(listener.getDataList()));
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail();
        }
        finally {
            deleteTempFile();
        }
    }

}
