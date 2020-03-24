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
                .setRelObjId(1001L).setDataCount(10).setStoragePath("/123.xlsx");
        boolean success = uploadFileService.createEntity(uploadFile);
        Assert.assertTrue(success);

        String uuid = uploadFile.getUuid();
        UploadFile uploadFile1 = uploadFileService.getEntity(uuid);
        Assert.assertTrue(uploadFile1 != null);

        List<UploadFile> list = uploadFileService.getUploadedFiles(Department.class.getSimpleName(), 1001L);
        Assert.assertTrue(list.size() > 0);
    }

}
