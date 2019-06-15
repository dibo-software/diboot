package diboot.core.test.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.entity.Metadata;
import com.diboot.core.service.MetadataService;
import com.diboot.core.util.V;
import com.diboot.core.vo.Pagination;
import diboot.core.test.StartupApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 *  BaseService接口实现测试 (需先执行example中的初始化SQL)
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/06/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {StartupApplication.class})
public class BaseServiceTest {

    @Autowired
    MetadataService metadataService;

    @Test
    public void testGet(){
        // 查询总数
        int count = metadataService.getEntityListCount(null);
        Assert.assertTrue(count > 0);
        // 查询list
        List<Metadata> metadataList = metadataService.getEntityList(null);
        Assert.assertTrue(V.notEmpty(metadataList));
        Assert.assertTrue(metadataList.size() == count);
        // 第一页数据
        List<Metadata> pageList = metadataService.getEntityList(null, new Pagination());
        Assert.assertTrue(pageList.size() > 0 && pageList.size() <= BaseConfig.getPageSize());

        // 查询单个记录
        Long id = metadataList.get(0).getId();
        Metadata first = metadataService.getEntity(id);
        Assert.assertTrue(first != null);

        // 只查询第一条记录对应type类型的
        LambdaQueryWrapper<Metadata> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Metadata::getType, first.getType());
        metadataList = metadataService.getEntityList(queryWrapper);
        Assert.assertTrue(V.notEmpty(metadataList));
        // 结果type值一致
        metadataList.stream().forEach( m -> {
            Assert.assertTrue(m.getType().equals(first.getType()));
        });
    }

    @Test
    public void testCreateUpdateAndDelete(){
        // 创建
        String TYPE = "ID_TYPE";
        Metadata metadata = new Metadata();
        metadata.setType(TYPE);
        metadata.setItemName("证件品类");
        metadata.setParentId(0L);
        metadataService.createEntity(metadata);

        // 查询是否创建成功
        LambdaQueryWrapper<Metadata> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Metadata::getType, TYPE);
        List<Metadata> metadataList = metadataService.getEntityList(queryWrapper);
        Assert.assertTrue(V.notEmpty(metadataList));

        // 更新
        metadata.setItemName("证件类型");
        metadataService.updateEntity(metadata);
        Metadata metadata2 = metadataService.getEntity(metadata.getId());
        Assert.assertTrue(metadata2.getItemName().equals(metadata.getItemName()));

        // 删除
        metadataService.deleteEntity(metadata.getId());
        metadata2 = metadataService.getEntity(metadata.getId());
        Assert.assertTrue(metadata2 == null);
    }

}