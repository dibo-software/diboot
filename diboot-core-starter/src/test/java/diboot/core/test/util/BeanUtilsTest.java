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
package diboot.core.test.util;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.config.Cons;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.*;
import com.diboot.core.vo.DictionaryVO;
import com.diboot.core.vo.LabelValue;
import com.sun.management.OperatingSystemMXBean;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.TestRegion;
import diboot.core.test.binder.entity.TestUploadFile;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.entity.UserImportModel;
import diboot.core.test.binder.service.RegionService;
import diboot.core.test.binder.vo.RegionVO;
import diboot.core.test.binder.vo.SimpleDictionaryVO;
import diboot.core.test.binder.vo.UserVO;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

/**
 * BeanUtils测试
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/06/02
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class BeanUtilsTest {
    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private RegionService regionService;

    @Test
    public void testClass() {
        List<TestService> services = ContextHolder.getBeans(TestService.class);
        Class class1 = BeanUtils.getGenericityClass(services.get(0), 0);
        Assert.assertTrue(class1.getName().equals(Dictionary.class.getName()));

        Class class2 = BeanUtils.getGenericityClass(dictionaryService, 1);
        Assert.assertTrue(class2.getName().equals(Dictionary.class.getName()));

        Class class3 = BeanUtils.getGenericityClass(dictionaryService, 2);
        Assert.assertTrue(class3 == null);

        Class class4 = BeanUtils.getGenericityClass(new D(), 1);
        Assert.assertTrue(class4 == null);

    }

    @Test
    public void testFields(){
        List<Field> fields = BindingCacheManager.getFields(Dictionary.class);
        Assert.assertTrue(fields.size() > 0);
        long start = System.currentTimeMillis();
        for(int i=0; i<10000; i++){
            Class<?> dict = BeanUtils.getGenericityClass(dictionaryService, 1);
        }
        long end = System.currentTimeMillis() - start;
        System.out.println(" takes "+ end);
        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++){
            Class<?> dict = ((IService)dictionaryService).getEntityClass();
        }
        end = System.currentTimeMillis() - start;
        System.out.println(" takes "+ end);
    }

    @Test
    public void testCopyBean(){
        String itemName = "在职";
        Dictionary dictionary1 = new Dictionary();
        dictionary1.setType("STATUS");
        dictionary1.setItemName(itemName);

        Dictionary dictionary2 = BeanUtils.copyProperties(dictionary1, new Dictionary());
        Assert.assertTrue(dictionary2.getItemName().equals(itemName));

        Map<String, Object> map = new HashMap<>();
        map.put("type", "STATUS");
        map.put("itemName",itemName);
        map.put("isEditable", true);
        map.put("createTime", "2018-09-12 23:09");
        Dictionary dictionary3 = new Dictionary();
        BeanUtils.bindProperties(dictionary3, map);
        Assert.assertTrue(dictionary3.getItemName().equals(itemName));
        Assert.assertTrue(dictionary3.getIsEditable() == true);
        Assert.assertTrue(dictionary3.getCreateTime() != null);

        // Accept注解拷贝
        User user = new User();
        BeanUtils.copyProperties(dictionary3, user);
        Assert.assertTrue(user.getGender().equals(dictionary3.getItemName()));
        user.setGender("123");
        BeanUtils.copyProperties(dictionary3, user);
        Assert.assertTrue(user.getGender().equals("123"));
    }

    /**
     * 测试注解拷贝
     */
    @Test
    public void testAcceptAnnoCopy() {
        // Accept注解拷贝
        UserVO user = new UserVO().setGenderLabel(new LabelValue("男", "M"));
        user.setBirthdate(LocalDate.now());
        UserImportModel importModel = new UserImportModel();
        BeanUtils.copyProperties(user, importModel);
        Assert.assertTrue(importModel.getLabel().equals("男"));
        Assert.assertTrue(importModel.getDate() != null);
    }

    @Test
    public void testDateCopy(){
        User user2 = new User();
        Map<String, Object> map = new HashMap<>();
        map.put("username", "test");
        map.put("birthdate", "1980-10-12");
        map.put("localDatetime", new Date());
        BeanUtils.setProperty(user2, "birthdate", "1980-10-12");
        Assert.assertTrue(user2.getBirthdate() != null);
        BeanUtils.bindProperties(user2, map);
        Assert.assertTrue(user2.getLocalDatetime() != null);
        System.out.println(JSON.stringify(user2));
    }

    @Test
    public void testGetProperty(){
        Dictionary dictionary1 = new Dictionary();
        dictionary1.setId("1001");

        // getProperty
        Object id = BeanUtils.getProperty(dictionary1, "id");
        Assert.assertTrue(id instanceof String);
        // getStringProperty
        Assert.assertTrue(BeanUtils.getStringProperty(dictionary1, "id").equals("1001"));
    }

    @Test
    public void testCollect(){
        List<Dictionary> dictionaryList = new ArrayList<>();
        for(long id=1001; id<1005; id++){
            Dictionary dictionary1 = new Dictionary();
            dictionary1.setId(S.valueOf(id));
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
            dictionary1.setId(S.valueOf(id));
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
        Assert.assertEquals(dictionaryList.get(0).getItemName(), null);
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
            dictionary1.setId(S.valueOf(id));
            dictionaryList.add(dictionary1);
        }
        for(long id=1003; id<=1007; id++){
            Dictionary dictionary1 = new Dictionary();
            dictionary1.setId(S.valueOf(id));
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
            dictionary1.setId(S.valueOf(id));
            dictionary1.setParentId(S.valueOf(1L));
            dictionaryList.add(dictionary1);
        }
        Dictionary parent = new Dictionary();
        parent.setId("1");
        parent.setParentId(Cons.TREE_ROOT_ID);
        dictionaryList.add(parent);

        // 正常数据
        List<DictionaryVO> list = BeanUtils.convertList(dictionaryList, DictionaryVO.class);
        list = BeanUtils.buildTree(list, Cons.TREE_ROOT_ID);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getChildren().size(), 5);

        list = BeanUtils.convertList(dictionaryList, DictionaryVO.class);
        list = BeanUtils.buildTree(list, Cons.TREE_ROOT_ID, Cons.FieldName.id.name());
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getChildren().size(), 5);

        // 异常数据告警
        Dictionary dict2 = new Dictionary();
        dict2.setId("1");
        dict2.setParentId("1");
        dictionaryList.add(dict2);
        list = BeanUtils.convertList(dictionaryList, DictionaryVO.class);
        try{
            list = BeanUtils.buildTree(list, Cons.TREE_ROOT_ID);
            Assert.assertTrue(false);
        }
        catch (Exception e){
            Assert.assertTrue(e.getMessage().contains("请检查"));
        }
    }

    @Test
    public void testBuildTreeWithUUID(){
        // 准备节点数据
        List<TestRegion> regionList = new ArrayList<>();
        TestRegion province1 = new TestRegion().setUuid(S.newUuid()).setName("江苏省").setLevel(1).setCode("JS");
        regionList.add(province1);
        TestRegion province2 = new TestRegion().setUuid(S.newUuid()).setName("浙江省").setLevel(1).setCode("ZJ");
        regionList.add(province2);

        TestRegion city1 = new TestRegion().setUuid(S.newUuid()).setName("南京市").setLevel(2).setCode("NJ").setParentId(province1.getUuid());
        regionList.add(city1);
        TestRegion city2 = new TestRegion().setUuid(S.newUuid()).setName("苏州市").setLevel(2).setCode("SZ").setParentId(province1.getUuid());
        regionList.add(city2);
        TestRegion city3 = new TestRegion().setUuid(S.newUuid()).setName("杭州市").setLevel(2).setCode("HZ").setParentId(province2.getUuid());
        regionList.add(city3);

        TestRegion area1 = new TestRegion().setUuid(S.newUuid()).setName("建邺区").setLevel(3).setCode("JY").setParentId(city1.getUuid());
        regionList.add(area1);
        TestRegion area2 = new TestRegion().setUuid(S.newUuid()).setName("工业园区").setLevel(3).setCode("SIP").setParentId(city2.getUuid());
        regionList.add(area2);
        TestRegion area3 = new TestRegion().setUuid(S.newUuid()).setName("姑苏区").setLevel(3).setCode("GS").setParentId(city2.getUuid());
        regionList.add(area3);

        // 构建树形结构
        List<TestRegion> list = BeanUtils.buildTree(regionList, null, "uuid");
        //BeanUtils.buildTree(regionList, null, "uuid", Cons.FieldName.parentId.name(), Cons.FieldName.children.name());

        // 检测结果
        Assert.assertEquals(list.size(), 2);
        for(TestRegion region : list) {
            if(region.getCode().equals("JS")) {
                Assert.assertEquals(region.getChildren().size(), 2);
                for(TestRegion city : region.getChildren()) {
                    if(city.getCode().equals("NJ")) {
                        Assert.assertEquals(city.getChildren().size(), 1);
                    }
                    else{
                        Assert.assertEquals(city.getChildren().size(), 2);
                    }
                }
            }
            else {
                Assert.assertEquals(region.getChildren().size(), 1);
                Assert.assertNull(region.getChildren().get(0).getChildren());
            }
        }
    }

    /**
     * 测试buildTree性能
     */
    @Test
    public void testBuildTreePerformance() {
        List<RegionVO> regions = regionService.getViewObjectList(Wrappers.query(), null, RegionVO.class);
        long begin = System.currentTimeMillis();
        // List<RegionVO> topLevel = BeanUtils.buildTree(regions);
        List<RegionVO> topLevel = BeanUtils.buildTree(regions, RegionVO::getId, RegionVO::getParentId, RegionVO::setChildren);
        Assert.assertTrue(topLevel.size() > 30 && topLevel.size() < 40);
        long end = System.currentTimeMillis();
        System.out.println( (end - begin) + "ms");
    }

    @Test
    public void testSetProperty(){
        TestUploadFile object = new TestUploadFile();
        BeanUtils.setProperty(object, "id", 123l);
        BeanUtils.setProperty(object, "storagePath", "/test/xxx");
        Assert.assertTrue(object.getStoragePath() != null);
        BeanUtils.setProperty(object, "createTime", new Timestamp(System.currentTimeMillis()));
        Assert.assertTrue(object.getCreateTime() != null);
        BeanUtils.setProperty(object, "createTime", new Date());
        Assert.assertTrue(object.getCreateTime() != null);
        BeanUtils.setProperty(object, "localDateTime", new Timestamp(System.currentTimeMillis()));
        Assert.assertTrue(object.getLocalDateTime() != null);
    }

    /**
     * 测试属性赋值的性能优化
     */
    @Test
    public void testSetPropertyOptimize(){
        User user2 = new User();
        long begin = System.currentTimeMillis();
        for(int i=0; i< 10000; i++){
            BeanUtils.setProperty(user2, "username", "test");
            BeanUtils.setProperty(user2, "birthdate", "1980-10-12");
            BeanUtils.setProperty(user2, "localDatetime", new Date());
        }
        long end = System.currentTimeMillis();
        long takes1 = (end - begin);
        begin = System.currentTimeMillis();

        BeanWrapper beanWrapper = BeanUtils.getBeanWrapper(user2);
        for(int i=0; i< 10000; i++){
            beanWrapper.setPropertyValue("username", "test");
            beanWrapper.setPropertyValue("birthdate", "1980-10-12");
            beanWrapper.setPropertyValue("localDatetime", new Date());
        }
        end = System.currentTimeMillis();
        long takes2 = (end - begin);
        System.out.println(takes1 + " ms , after: " + takes2 + " ms") ;
        Assert.assertTrue(takes2 < takes1);
    }

    @Test
    public void testConvertType() {
        Collection<String> list = new ArrayList<>();
        list.add("123");
        list.add("234");

        Collection list2 = BeanUtils.convertIdValuesToType(list, Long.class);
        Assert.assertTrue(list2.size() == 2);
        Assert.assertTrue(V.equals(list2.iterator().next(), 123l));
    }

    @Test
    public void testMap2BeanConvert() throws Exception {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "苏州");
        dataMap.put("level", 2);
        dataMap.put("code", "SZ");
        dataMap.put("parentId", "1");

        Map<String, Object> children1 = new HashMap<>();
        dataMap.put("name", "工业园区");
        dataMap.put("level", 3);
        dataMap.put("code", "GYYQ");
        dataMap.put("parentId", "2");

        Map<String, Object> children2 = new HashMap<>();
        dataMap.put("name", "姑苏区");
        dataMap.put("level", 3);
        dataMap.put("code", "GSQ");
        dataMap.put("parentId", "2");

        List<Map<String, Object>> children = new ArrayList<>();
        children.add(children1);
        children.add(children2);
        dataMap.put("children",  children);

        TestRegion testRegion2 = MapUtils.buildEntity(dataMap, TestRegion.class);
        Assert.assertEquals(2, testRegion2.getChildren().size());

        List<Map<String, Object>> entityMapList = new ArrayList<>();
        entityMapList.add(dataMap);
        entityMapList.add(dataMap);

        List<TestRegion> testRegions = MapUtils.buildEntityList(entityMapList, TestRegion.class);
        Assert.assertEquals(2, testRegions.size());
        Assert.assertEquals(2, testRegions.get(0).getChildren().size());
    }

    @Test
    public void testBeanConvert() throws Exception {
        Dictionary dictionary = new Dictionary();
        dictionary.setId("1");
        SimpleDictionaryVO vo = BeanUtils.convert(dictionary, SimpleDictionaryVO.class);
        Assert.assertEquals(vo.getId(), "1");
    }

}
