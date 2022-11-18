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
import com.diboot.core.util.V;
import com.diboot.core.vo.Status;
import com.diboot.file.example.custom.DepartmentExcelModel;
import com.diboot.file.example.custom.listener.DepartmentImportListener;
import com.diboot.file.util.ExcelHelper;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 固定表头excel读测试
 *
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/02/19
 */
public class FixedHeadExcelReadTest extends ExcelWriteTest {

    @Test
    public void testValidate() {
        DepartmentExcelModel department = new DepartmentExcelModel();
        department.setOrgName("dd");
        department.setParentName("产品部");
        department.setMemCount(1);
        department.setUserStatus("S");
        String msg = V.validateBeanErrMsg(department);
        Assert.assertNotNull(msg);
    }

    @Test
    public void testNormalDataRead() {
        try {
            prepareNormalDataExcel();
            // 读且保存
            DepartmentImportListener listener = new DepartmentImportListener();
            ExcelHelper.read(Files.newInputStream(Paths.get(getTempFilePath())), listener);
            System.out.println(JSON.stringify(listener.getFieldHeadMap()));
            System.out.println(JSON.stringify(listener.getDataList()));
            Assert.assertEquals("在职", listener.getDataList().get(0).getUserStatus());
            Assert.assertEquals("产品部", listener.getDataList().get(0).getParentName());
            Assert.assertNull(listener.getDataList().get(1).getParentId());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail();
        } finally {
            deleteTempFile();
        }
    }

    @Test
    public void testErrorDataRead() {
        try {
            prepareErrorDataExcel();
            // 读且保存
            ExcelHelper.read(Files.newInputStream(Paths.get(getTempFilePath())), new DepartmentImportListener());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(e.getMessage().contains(Status.FAIL_VALIDATION.label()));
        } finally {
            deleteTempFile();
        }
    }

    @Test
    public void testNormalDataReadSave() {
        try {
            prepareNormalDataExcel();
            // 读且保存
            DepartmentImportListener listener = new DepartmentImportListener();
            ExcelHelper.read(Files.newInputStream(Paths.get(getTempFilePath())), listener);
            System.out.println(JSON.stringify(listener.getFieldHeadMap()));
            System.out.println(JSON.stringify(listener.getDataList()));
            Assert.assertEquals("A", listener.getDataList().get(0).getUserStatus());
            Assert.assertEquals("产品部", listener.getDataList().get(0).getParentName());
            Assert.assertEquals(10001L, (long) listener.getDataList().get(0).getParentId());
            Assert.assertEquals(0L, (long) listener.getDataList().get(1).getParentId());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Assert.fail();
        } finally {
            deleteTempFile();
        }
    }

}
