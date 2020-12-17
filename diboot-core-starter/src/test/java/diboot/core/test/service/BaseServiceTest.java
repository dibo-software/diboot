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
package diboot.core.test.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.impl.DictionaryServiceExtImpl;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.*;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.UserRole;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *  BaseService接口实现测试 (需先执行example中的初始化SQL)
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/06/15
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class BaseServiceTest {

    @Autowired
    DictionaryServiceExtImpl dictionaryService;

    @Autowired
    UserService userService;

    @Test
    public void testGet(){
        // 查询总数
        int count = dictionaryService.getEntityListCount(null);
        Assert.assertTrue(count > 0);
        // 查询list
        List<Dictionary> dictionaryList = dictionaryService.getEntityList(null);
        Assert.assertTrue(V.notEmpty(dictionaryList));
        Assert.assertTrue(dictionaryList.size() == count);
        // 第一页数据
        List<Dictionary> pageList = dictionaryService.getEntityList(null, new Pagination());
        Assert.assertTrue(pageList.size() > 0 && pageList.size() <= BaseConfig.getPageSize());

        // 查询单个记录
        Long id = dictionaryList.get(0).getId();
        Dictionary first = dictionaryService.getEntity(id);
        Assert.assertTrue(first != null);

        // 只查询第一条记录对应type类型的
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getType, first.getType());
        dictionaryList = dictionaryService.getEntityList(queryWrapper);
        Assert.assertTrue(V.notEmpty(dictionaryList));
        // 结果type值一致
        dictionaryList.stream().forEach(m -> {
            Assert.assertTrue(m.getType().equals(first.getType()));
        });

        // 根据id集合去批量查询
        List<Long> ids = BeanUtils.collectIdToList(dictionaryList);
        dictionaryList = dictionaryService.getEntityListByIds(ids);
        Assert.assertTrue(V.notEmpty(dictionaryList));

        // 获取map
        List<Map<String, Object>> mapList = dictionaryService.getMapList(null, new Pagination());
        Assert.assertTrue(mapList.size() > 0 && mapList.size() <= BaseConfig.getPageSize());

    }

    @Test
    @Transactional
    public void testCreateUpdateAndDelete(){
        // 创建
        String TYPE = "ID_TYPE";
        Dictionary dictionary = new Dictionary();
        dictionary.setType(TYPE);
        dictionary.setItemName("证件类型");
        dictionary.setParentId(0L);
        dictionaryService.createEntity(dictionary);
        Assert.assertTrue(dictionary.getPrimaryKeyVal() != null);
        // 查询是否创建成功
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getType, TYPE);
        List<Dictionary> dictionaryList = dictionaryService.getEntityList(queryWrapper);
        Assert.assertTrue(V.notEmpty(dictionaryList));

        // 更新
        dictionary.setItemName("证件类型定义");
        dictionaryService.updateEntity(dictionary);
        Dictionary dictionary2 = dictionaryService.getEntity(dictionary.getId());
        Assert.assertTrue(dictionary2.getItemName().equals(dictionary.getItemName()));
    }

    @Test
    @Transactional
    public void testBatchCreate(){
        // 创建
        String TYPE = "ID_TYPE";
        // 定义
        Dictionary dictionary = new Dictionary();
        dictionary.setType(TYPE);
        dictionary.setItemName("证件类型");
        dictionary.setParentId(0L);
        boolean success = dictionaryService.createEntity(dictionary);
        Assert.assertTrue(success);

        // 子项
        List<Dictionary> dictionaryList = new ArrayList<>();
        String[] itemNames = {"身份证", "驾照", "护照"}, itemValues = {"SFZ","JZ","HZ"};
        for(int i=0; i<itemNames.length; i++){
            Dictionary dict = new Dictionary();
            dict.setType(TYPE);
            dict.setItemName(itemNames[i]);
            dict.setItemValue(itemValues[i]);
            dict.setParentId(dictionary.getId());
            dictionaryList.add(dict);
        }
        success = dictionaryService.createEntities(dictionaryList);
        Assert.assertTrue(success);

        dictionaryList.get(2).setCreateTime(new Date());
        dictionaryList.get(2).setItemValue("HZ2");
        dictionaryService.updateEntity(dictionaryList.get(2));
        Assert.assertTrue(success);

    }

    @Test
    public void testKV(){
        List<KeyValue> keyValues = dictionaryService.getKeyValueList("GENDER");
        Assert.assertTrue(keyValues.size() >= 2);
        Assert.assertTrue(keyValues.get(0).getV().equals("M") || keyValues.get(1).getV().equals("M"));
        Map<String, Object> kvMap = BeanUtils.convertKeyValueList2Map(keyValues);
        Assert.assertTrue(kvMap.get("女").equals("F"));
    }

    @Test
    public void testIpage2PagingJsonResult(){
        // 查询是否创建成功
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getType, "GENDER");
        queryWrapper.orderByDesc(Dictionary::getId);
        IPage<Dictionary> dp = new Page<>();
        ((Page<Dictionary>) dp).addOrder(OrderItem.desc("id"));

        IPage<Dictionary> dictionaryPage = dictionaryService.page(dp, queryWrapper);

        PagingJsonResult pagingJsonResult = new PagingJsonResult(dictionaryPage);
        Assert.assertTrue(V.notEmpty(pagingJsonResult));
        Assert.assertTrue(pagingJsonResult.getPage().getOrderBy().equals("id:DESC"));
    }

    /**
     * 测试1-多的批量新建/更新/删除操作
     */
    @Test
    @Transactional
    public void testCreateUpdateDeleteEntityAndRelatedEntities(){
        // 创建
        String TYPE = "ID_TYPE";
        // 定义
        Dictionary dictionary = new Dictionary();
        dictionary.setType(TYPE);
        dictionary.setItemName("证件类型");
        dictionary.setParentId(0L);
        // 子项
        List<Dictionary> dictionaryList = new ArrayList<>();
        String[] itemNames = {"身份证", "驾照", "护照"}, itemValues = {"SFZ","JZ","HZ"};
        for(int i=0; i<itemNames.length; i++){
            Dictionary dict = new Dictionary();
            dict.setType(TYPE);
            dict.setItemName(itemNames[i]);
            dict.setItemValue(itemValues[i]);
            dict.setParentId(dictionary.getId());
            dictionaryList.add(dict);
        }
        boolean success = dictionaryService.createEntityAndRelatedEntities(dictionary, dictionaryList, Dictionary::setParentId);
        Assert.assertTrue(success);

        dictionary.setItemName(dictionary.getItemName()+"_2");
        dictionaryList.remove(1);

        Dictionary dict = new Dictionary();
        dict.setType(TYPE);
        dict.setItemName("港澳通行证");
        dict.setItemValue("GATXZ");
        dictionaryList.add(dict);
        success = dictionaryService.updateEntityAndRelatedEntities(dictionary, dictionaryList, Dictionary::setParentId);
        Assert.assertTrue(success);

        // 查询是否创建成功
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getType, TYPE).ne(Dictionary::getParentId, 0);

        List<Dictionary> dictionaryList2 = dictionaryService.getEntityList(queryWrapper);
        Assert.assertTrue(V.notEmpty(dictionaryList2));
        List<String> itemNames2 = BeanUtils.collectToList(dictionaryList2, Dictionary::getItemName);
        Assert.assertTrue(itemNames2.contains("港澳通行证"));

        //success = dictionaryService.updateEntityAndRelatedEntities(dictionary, null, Dictionary::setParentId);
        //Assert.assertTrue(success);
        //dictionaryList2 = dictionaryService.getEntityList(queryWrapper);
        //Assert.assertTrue(V.isEmpty(dictionaryList2));

        success = dictionaryService.deleteEntityAndRelatedEntities(dictionary.getId(), Dictionary.class, Dictionary::setParentId);
        Assert.assertTrue(success);
        dictionaryList2 = dictionaryService.getEntityList(queryWrapper);
        Assert.assertTrue(V.isEmpty(dictionaryList2));
    }

    @Test
    public void testJsonResult(){
        Map map = new HashMap();
        map.put("k", "123");
        JsonResult jsonResult = JsonResult.OK(map);
        JsonResult jsonResult2 = JsonResult.OK(map);
        Assert.assertEquals(jsonResult.getData(), jsonResult2.getData());

        String msg = "参数name不匹配";
        jsonResult = new JsonResult(Status.FAIL_VALIDATION, msg);
        jsonResult2 = JsonResult.FAIL_VALIDATION(msg);
        Assert.assertTrue(jsonResult.getMsg().endsWith(msg));
        Assert.assertTrue(jsonResult2.getMsg().endsWith(msg));
    }

    @Test
    public void testExist(){
        boolean exists = dictionaryService.exists(Dictionary::getType, "GENDER");
        Assert.assertTrue(exists);
    }

    @Test
    public void testContextHelper(){
        String database = ContextHelper.getDatabaseType();
        System.out.println(database);
        Assert.assertTrue(database.equals("mysql") || database.equals("oracle"));
    }

    @Test
    public void testGetValuesOfField(){
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "GENDER");
        List<Long> ids = dictionaryService.getValuesOfField(queryWrapper, Dictionary::getId);
        Assert.assertTrue(ids.size() > 0);

        LambdaQueryWrapper<Dictionary> wrapper = new QueryWrapper<Dictionary>().lambda()
                .eq(Dictionary::getType, "GENDER");
        List<String> itemValues = dictionaryService.getValuesOfField(wrapper, Dictionary::getItemValue);
        Assert.assertTrue(itemValues.size() > 0);
        System.out.println(JSON.stringify(ids) + " : " + JSON.stringify(itemValues));
    }

    @Test
    public void testGetValueOfField(){
        String val = dictionaryService.getValueOfField(Dictionary::getId, 2L, Dictionary::getItemValue);
        Assert.assertTrue("M".equals(val));
        System.out.println(val);
    }

    @Test
    public void testGetLimit(){
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "GENDER");
        queryWrapper.gt("parent_id", 0);

        Dictionary dictionary = dictionaryService.getSingleEntity(queryWrapper);
        Assert.assertTrue(dictionary != null);

        List<Dictionary> ids = dictionaryService.getEntityListLimit(queryWrapper, 5);
        Assert.assertTrue(ids.size() >= 2);
    }

    @Test
    public void testPagination(){
        Dictionary dict = new Dictionary();
        dict.setType("GENDER");
        dict.setParentId(null);
        QueryWrapper<Dictionary> queryWrapper = QueryBuilder.toQueryWrapper(dict);

        // 查询当前页的数据
        Pagination pagination = new Pagination();
        pagination.setPageSize(1);

        List<DictionaryVO> voList = dictionaryService.getViewObjectList(queryWrapper, pagination, DictionaryVO.class);
        Assert.assertTrue(voList.size() == 1);
        Assert.assertTrue(pagination.getTotalPage() >= 2);
        Assert.assertTrue(V.isEmpty(voList.get(0).getChildren()));

        pagination.setPageIndex(2);
        voList = dictionaryService.getViewObjectList(queryWrapper, pagination, DictionaryVO.class);
        Assert.assertTrue(voList.size() == 1);
    }

    @Test
    public void testDictVo(){
        Dictionary dict = new Dictionary();
        dict.setParentId(0L);
        dict.setType("GENDER");

        QueryWrapper<Dictionary> queryWrapper = QueryBuilder.toQueryWrapper(dict);

        List<DictionaryVO> voList = dictionaryService.getViewObjectList(queryWrapper, null, DictionaryVO.class);
        Assert.assertTrue(voList.size() == 1);
        Assert.assertTrue(voList.get(0).getChildren().size() >= 2);

        List<KeyValue> keyValues = dictionaryService.getKeyValueList("GENDER");
        Assert.assertTrue(keyValues.size() >= 2);
    }

    /**
     * 测试n-n的批量新建/更新
     */
    @Test
    @Transactional
    public void testCreateUpdateN2NRelations(){
        Long userId = 10001L;
        LambdaQueryWrapper<UserRole> queryWrapper = new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, userId);

        // 新增
        List<Long> roleIdList = Arrays.asList(10L, 11L, 12L);
        userService.createOrUpdateN2NRelations(UserRole::getUserId, userId, UserRole::getRoleId, roleIdList);
        List<UserRole> list = ContextHelper.getBaseMapperByEntity(UserRole.class).selectList(queryWrapper);
        Assert.assertTrue(list.size() == roleIdList.size());

        // 更新
        roleIdList = Arrays.asList(13L);
        userService.createOrUpdateN2NRelations(UserRole::getUserId, userId, UserRole::getRoleId, roleIdList);
        list = ContextHelper.getBaseMapperByEntity(UserRole.class).selectList(queryWrapper);
        Assert.assertTrue(list.size() == 1);

        // 删除
        roleIdList = null;
        userService.createOrUpdateN2NRelations(UserRole::getUserId, userId, UserRole::getRoleId, roleIdList);
        list = ContextHelper.getBaseMapperByEntity(UserRole.class).selectList(queryWrapper);
        Assert.assertTrue(list.size() == 0);
    }

}