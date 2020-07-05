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
package com.diboot.file.example.test;

import com.diboot.core.util.JSON;
import com.diboot.file.excel.listener.DynamicHeadExcelListener;
import com.diboot.file.util.ExcelHelper;
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
