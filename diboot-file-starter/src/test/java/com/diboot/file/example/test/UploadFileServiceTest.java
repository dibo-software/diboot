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

import com.diboot.file.entity.UploadFile;
import com.diboot.file.example.ApplicationTest;
import com.diboot.file.example.custom.Department;
import com.diboot.file.service.UploadFileService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件上传记录service测试
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2020/02/19
 */
public class UploadFileServiceTest extends ApplicationTest {

    @Autowired
    private UploadFileService uploadFileService;

    @Test
    @Transactional
    public void testCreateAndQuery(){
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFileName("测试").setFileType("xlsx").setRelObjType(Department.class.getSimpleName())
                .setRelObjId("1001").setDataCount(10).setStoragePath("/123.xlsx");
        boolean success = uploadFileService.createEntity(uploadFile);
        Assert.assertTrue(success);

        String uuid = uploadFile.getUuid();
        UploadFile uploadFile1 = uploadFileService.getEntity(uuid);
        Assert.assertTrue(uploadFile1 != null);

        List<UploadFile> list = uploadFileService.getUploadedFiles(Department.class.getSimpleName(), 1001L);
        Assert.assertTrue(list.size() > 0);
    }

}
