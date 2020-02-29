package diboot.core.test.util;

import com.baomidou.mybatisplus.annotation.TableId;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.vo.DictionaryVO;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BeanUtils测试
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/06/02
 */
public class BeanUtilsTest {

    @Test
    public void testCopyBean(){
        String itemName = "在职";
        Dictionary dictionary1 = new Dictionary();
        dictionary1.setType("STATUS");
        dictionary1.setItemName(itemName);

        Dictionary dictionary2 = new Dictionary();
        BeanUtils.copyProperties(dictionary1, dictionary2);
        Assert.assertTrue(dictionary2.getItemName().equals(itemName));

        Map<String, Object> map = new HashMap<>();
        map.put("type", "STATUS");
        map.put("itemName",itemName);
        Dictionary dictionary3 = new Dictionary();
        BeanUtils.bindProperties(dictionary3, map);
        Assert.assertTrue(dictionary2.getItemName().equals(itemName));
    }

    @Test
    public void testGetProperty(){
        Dictionary dictionary1 = new Dictionary();
        dictionary1.setId(1001L);

        // getProperty
        Object id = BeanUtils.getProperty(dictionary1, "id");
        Assert.assertTrue(id instanceof Long);
        // getStringProperty
        Assert.assertTrue(BeanUtils.getStringProperty(dictionary1, "id").equals("1001"));
    }

    @Test
    public void testCollect(){
        List<Dictionary> dictionaryList = new ArrayList<>();
        for(long id=1001; id<1005; id++){
            Dictionary dictionary1 = new Dictionary();
            dictionary1.setId(id);
            dictionaryList.add(dictionary1);
        }
        List<Long> metaIdList = BeanUtils.collectToList(dictionaryList, Dictionary::getId);
        Assert.assertTrue(metaIdList.size() == 4);
    }

    @Test
    public void testGetterSetter(){
        Assert.assertEquals(BeanUtils.convertToFieldName(Dictionary::getItemName), "itemName");
        Assert.assertEquals(BeanUtils.convertToFieldName(Dictionary::setItemName), "itemName");
    }

    @Test
    public void testBindProp(){
        List<Dictionary> dictionaryList = new ArrayList<>();
        for(long id=1001; id<1005; id++){
            Dictionary dictionary1 = new Dictionary();
            dictionary1.setId(id);
            dictionaryList.add(dictionary1);
        }
        Map<String, String> map = new HashMap<>();
        map.put("1001", "在职");
        map.put("1002", "在职");
        map.put("1003", "离职");
        BeanUtils.bindPropValueOfList(Dictionary::setItemName, dictionaryList, Dictionary::getId, map);
        Assert.assertEquals(dictionaryList.get(0).getItemName(), "在职");
        Assert.assertEquals(dictionaryList.get(2).getItemName(), "离职");

        Map<Long, String> map2 = new HashMap<>();
        map2.put(1001L, "在职");
        map2.put(1002L, "在职");
        map2.put(1003L, "离职");
        BeanUtils.bindPropValueOfList("itemName", dictionaryList, "id", map2);
        Assert.assertEquals(dictionaryList.get(0).getItemName(), "在职");
        Assert.assertEquals(dictionaryList.get(2).getItemName(), "离职");
    }

    @Test
    public void testLambdaGetterSetter(){
        Assert.assertEquals("itemName", BeanUtils.convertToFieldName(Dictionary::getItemName));
        Assert.assertEquals("itemName", BeanUtils.convertToFieldName(Dictionary::setItemName));
    }

    @Test
    public void getField(){
        Field field = BeanUtils.extractField(Dictionary.class, "id");
        TableId id = field.getAnnotation(TableId.class);
        Assert.assertTrue(id != null);
    }

    @Test
    public void testLambdaDistinct(){
        List<Dictionary> dictionaryList = new ArrayList<>();
        for(long id=1001; id<=1005; id++){
            Dictionary dictionary1 = new Dictionary();
            dictionary1.setId(id);
            dictionaryList.add(dictionary1);
        }
        for(long id=1003; id<=1007; id++){
            Dictionary dictionary1 = new Dictionary();
            dictionary1.setId(id);
            dictionaryList.add(dictionary1);
        }
        List<Dictionary> dictionaryList2 = BeanUtils.distinctByKey(dictionaryList, Dictionary::getId);
        Assert.assertEquals(dictionaryList2.size(), 7);
    }


    @Test
    public void testBuildTree(){
        List<Dictionary> dictionaryList = new ArrayList<>();
        for(long id=1001; id<=1005; id++){
            Dictionary dictionary1 = new Dictionary();
            dictionary1.setId(id);
            dictionary1.setParentId(1L);
            dictionaryList.add(dictionary1);
        }
        Dictionary parent = new Dictionary();
        parent.setId(1L);
        parent.setParentId(0L);
        dictionaryList.add(parent);

        // 正常数据
        List<DictionaryVO> list = BeanUtils.convertList(dictionaryList, DictionaryVO.class);
        list = BeanUtils.buildTree(list);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getChildren().size(), 5);

        // 异常数据告警
        Dictionary dict2 = new Dictionary();
        dict2.setId(1L);
        dict2.setParentId(1L);
        dictionaryList.add(dict2);
        list = BeanUtils.convertList(dictionaryList, DictionaryVO.class);
        try{
            list = BeanUtils.buildTree(list);
        }
        catch (Exception e){
            Assert.assertTrue(e.getMessage().contains("请检查"));
        }
    }

}
