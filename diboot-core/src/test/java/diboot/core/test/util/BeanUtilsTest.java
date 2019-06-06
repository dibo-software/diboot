package diboot.core.test.util;

import com.diboot.core.entity.Metadata;
import com.diboot.core.util.BeanUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BeanUtils测试
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/06/02
 */
public class BeanUtilsTest {

    @Test
    public void testCopyBean(){
        String itemName = "在职";
        Metadata metadata1 = new Metadata();
        metadata1.setType("STATUS");
        metadata1.setItemName(itemName);

        Metadata metadata2 = new Metadata();
        BeanUtils.copyProperties(metadata1, metadata2);
        Assert.assertTrue(metadata2.getItemName().equals(itemName));

        Map<String, Object> map = new HashMap<>();
        map.put("type", "STATUS");
        map.put("itemName",itemName);
        Metadata metadata3 = new Metadata();
        BeanUtils.bindProperties(metadata3, map);
        Assert.assertTrue(metadata2.getItemName().equals(itemName));
    }

    @Test
    public void testGetProperty(){
        Metadata metadata1 = new Metadata();
        metadata1.setId(1001L);

        // getProperty
        Object id = BeanUtils.getProperty(metadata1, "id");
        Assert.assertTrue(id instanceof Long);
        // getStringProperty
        Assert.assertTrue(BeanUtils.getStringProperty(metadata1, "id").equals("1001"));
    }

    @Test
    public void testConvert(){
        List<Metadata> metadataList = new ArrayList<>();
        for(long id=1001; id<1005; id++){
            Metadata metadata1 = new Metadata();
            metadata1.setId(id);
            metadataList.add(metadata1);
        }
        List<Long> metaIdList = BeanUtils.collectToList(metadataList, Metadata::getId);
        Assert.assertTrue(metaIdList.size() == 4);
    }

    @Test
    public void testGetterSetter(){
        Assert.assertEquals(BeanUtils.convertToFieldName(Metadata::getItemName), "itemName");
        Assert.assertEquals(BeanUtils.convertToFieldName(Metadata::setItemName), "itemName");
    }

    @Test
    public void testBindProp(){
        List<Metadata> metadataList = new ArrayList<>();
        for(long id=1001; id<1005; id++){
            Metadata metadata1 = new Metadata();
            metadata1.setId(id);
            metadataList.add(metadata1);
        }
        Map<String, String> map = new HashMap<>();
        map.put("1001", "在职");
        map.put("1002", "在职");
        map.put("1003", "离职");
        BeanUtils.bindPropValueOfList(Metadata::setItemName, metadataList, Metadata::getId, map);
        Assert.assertEquals(metadataList.get(0).getItemName(), "在职");
        Assert.assertEquals(metadataList.get(2).getItemName(), "离职");

        Map<Long, String> map2 = new HashMap<>();
        map2.put(1001L, "在职");
        map2.put(1002L, "在职");
        map2.put(1003L, "离职");
        BeanUtils.bindPropValueOfList("itemName", metadataList, "id", map2);
        Assert.assertEquals(metadataList.get(0).getItemName(), "在职");
        Assert.assertEquals(metadataList.get(2).getItemName(), "离职");
    }

}
