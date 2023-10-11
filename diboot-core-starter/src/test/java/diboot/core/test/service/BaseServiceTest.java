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

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.binding.RelationsBinder;
import com.diboot.core.binding.cache.BindingCacheManager;
import com.diboot.core.binding.parser.EntityInfoCache;
import com.diboot.core.binding.query.dynamic.ExtQueryWrapper;
import com.diboot.core.config.BaseConfig;
import com.diboot.core.config.Cons;
import com.diboot.core.data.access.DataScopeManager;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.impl.DictionaryServiceExtImpl;
import com.diboot.core.util.*;
import com.diboot.core.vo.*;
import com.fasterxml.jackson.core.type.TypeReference;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.dto.UserDTO;
import diboot.core.test.binder.entity.*;
import diboot.core.test.binder.service.*;
import diboot.core.test.binder.vo.RegionVO;
import diboot.core.test.binder.vo.SimpleDictionaryVO;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    DepartmentService departmentService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    RegionService regionService;

    @Test
    public void testGet(){
        // 查询总数
        long count = dictionaryService.getEntityListCount(null);
        Assert.assertTrue(count > 0);
        // 查询list
        List<Dictionary> dictionaryList = dictionaryService.getEntityList(null);
        Assert.assertTrue(V.notEmpty(dictionaryList));
        Assert.assertTrue(dictionaryList.size() == count);
        // 第一页数据
        List<Dictionary> pageList = dictionaryService.getEntityList(null, new Pagination());
        Assert.assertTrue(pageList.size() > 0 && pageList.size() <= BaseConfig.getPageSize());

        // 查询单个记录
        String id = dictionaryList.get(0).getId();
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
        List<String> ids = BeanUtils.collectIdToList(dictionaryList);
        dictionaryList = dictionaryService.getEntityListByIds(ids);
        Assert.assertTrue(V.notEmpty(dictionaryList));

        // 获取map
        List<Map<String, Object>> mapList = dictionaryService.getMapList(null, new Pagination());
        Assert.assertTrue(mapList.size() > 0 && mapList.size() <= BaseConfig.getPageSize());

        List<String> userIds = Arrays.asList("1001", "1002");
        Map<String, String> id2NameMap = userService.getId2NameMap(userIds, User::getUsername);
        Assert.assertTrue(id2NameMap != null);
        Assert.assertTrue(id2NameMap.get("1001") != null);
    }

    @Test
    @Transactional
    public void testCreateUpdateAndDelete(){
        // 创建
        String TYPE = "ID_TYPE";
        Dictionary dictionary = new Dictionary();
        dictionary.setType(TYPE);
        dictionary.setItemName("证件类型");
        dictionary.setParentId("0");
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
        dictionary.setParentId("0");
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

        dictionaryList.get(2).setCreateTime(LocalDateTime.now());
        dictionaryList.get(2).setItemValue("HZ2");
        dictionaryService.updateEntity(dictionaryList.get(2));
        Assert.assertTrue(success);

    }

    @Test
    public void testLabelValue(){
        List<LabelValue> labelValueList = dictionaryService.getLabelValueList("GENDER");
        Assert.assertTrue(labelValueList.size() >= 2);
        Assert.assertTrue(labelValueList.get(0).getValue().equals("M") || labelValueList.get(1).getValue().equals("M"));
        Map<String, Object> labelValueMap = BeanUtils.convertLabelValueList2Map(labelValueList);
        Assert.assertTrue(labelValueMap.get("女").equals("F"));
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

        PagingJsonResult pagingJsonResult = new JsonResult().data(dictionaryPage.getRecords()).bindPagination(new Pagination());
        Assert.assertTrue(V.notEmpty(pagingJsonResult));

        PagingJsonResult<List<Dictionary>> pagingJsonResult2 = new PagingJsonResult(dictionaryPage);
        String jsonStr = JSON.stringify(pagingJsonResult2);
        System.out.println(jsonStr);

        PagingJsonResult<List<Dictionary>> pagingJsonResult3 = JSON.parseObject(jsonStr,
                new TypeReference<PagingJsonResult<List<Dictionary>>>() {}
        );
        Assert.assertTrue(pagingJsonResult3.getPage() != null);
        Assert.assertTrue(pagingJsonResult3.getData() != null);
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
        dictionary.setParentId("0");
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
        boolean success = dictionaryService.createEntityAndRelatedEntities(dictionary,
                dictionaryList, Dictionary::setParentId);
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
    public void testEscInService(){
        LambdaQueryWrapper<Department> queryWrapper = new QueryWrapper<Department>()
                .lambda().isNotNull(Department::getCharacter);
        List departments = departmentService.list(queryWrapper);
        Assert.assertTrue(departments != null);
        Department dept = (Department) departments.get(0);
        Assert.assertTrue(dept.getCharacter() != null);

        LambdaQueryWrapper<User> queryWrapperUser = new QueryWrapper<User>()
                .lambda().isNotNull(User::getCharacter);
        List users = userService.getEntityList(queryWrapperUser);
        Assert.assertTrue(users != null);
        User user = (User) users.get(0);
        Assert.assertTrue(user.getCharacter() != null);
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

        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "GENDER");
        queryWrapper.eq("item_value", "F");
        exists = dictionaryService.exists(queryWrapper);
        Assert.assertTrue(exists);
    }

    @Test
    public void testGetValuesOfField(){
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "GENDER");
        List<String> ids = dictionaryService.getValuesOfField(queryWrapper, Dictionary::getId);
        Assert.assertTrue(ids.size() > 0);

        LambdaQueryWrapper<Dictionary> wrapper = new QueryWrapper<Dictionary>().lambda()
                .eq(Dictionary::getType, "GENDER");
        List<String> itemValues = dictionaryService.getValuesOfField(wrapper, Dictionary::getItemValue);
        Assert.assertTrue(itemValues.size() > 0);
        System.out.println(JSON.stringify(ids) + " : " + JSON.stringify(itemValues));
    }

    @Test
    public void testGetValueOfField(){
        Dictionary dictionary = dictionaryService.getSingleEntity(new QueryWrapper<Dictionary>().ne("parent_id", Cons.ID_PREVENT_NULL));
        String val = dictionaryService.getValueOfField(dictionary.getId(), Dictionary::getItemValue);
        Assert.assertTrue(dictionary.getItemValue().equals(val));
        System.out.println(val);

        // 初始化DTO，测试不涉及关联的情况
        UserDTO dto = new UserDTO();
        dto.setUsername("张三");
        // builder直接查询，不分页 3条结果
        ExtQueryWrapper queryWrapper = QueryBuilder.toDynamicJoinQueryWrapper(dto);
        List<String> values = userService.getValuesOfField(queryWrapper, User::getUsername);
        Assert.assertTrue(values.size() == 1);
        dto.setUsername(null);

        dto.setDeptId("10002");
        dto.setDeptName("研发组");
        dto.setOrgName("苏州帝博");
        queryWrapper = QueryBuilder.toDynamicJoinQueryWrapper(dto);
        List<String> values2 = userService.getValuesOfField(queryWrapper, User::getUsername);
        Assert.assertTrue(values2.size() == 2);
    }

    @Test
    public void testGetLimit(){
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "GENDER");
        queryWrapper.isNotNull("parent_id").ne("parent_id", Cons.ID_PREVENT_NULL);

        Dictionary dictionary = dictionaryService.getSingleEntity(queryWrapper);
        Assert.assertTrue(dictionary != null);

        List<Dictionary> ids = dictionaryService.getEntityListLimit(queryWrapper, 5);
        Assert.assertTrue(ids.size() >= 2);
    }

    @Test
    public void testPagination(){
        Dictionary dict = new Dictionary();
        dict.setType("GENDER");
        //dict.setParentId(null);
        QueryWrapper<Dictionary> queryWrapper = QueryBuilder.toQueryWrapper(dict);

        // 查询当前页的数据
        Pagination pagination = new Pagination();
        pagination.setPageSize(1);

        List<DictionaryVO> voList = dictionaryService.getViewObjectList(queryWrapper, pagination, DictionaryVO.class);
        Assert.assertTrue(voList.size() == 1);
        Assert.assertTrue(pagination.getTotalPage() >= 2);

        pagination.setPageIndex(2);
        voList = dictionaryService.getViewObjectList(queryWrapper, pagination, DictionaryVO.class);
        Assert.assertTrue(voList.size() == 1);

        // 测试 ORDER BY name
        pagination = new Pagination();
        pagination.setOrderBy("name:DESC");
        List<Organization> organizations = organizationService.getEntityList(null, pagination);
        Assert.assertTrue(organizations != null && organizations.get(0).getName().contains("苏州帝博"));
    }

    @Test
    public void testDictVo(){
        Dictionary dict = new Dictionary();
        dict.setParentId("0");
        dict.setType("GENDER");

        QueryWrapper<Dictionary> queryWrapper = QueryBuilder.toQueryWrapper(dict);

        List<DictionaryVO> voList = dictionaryService.getViewObjectList(queryWrapper, null, DictionaryVO.class);
        Assert.assertTrue(voList.size() == 1);
        Assert.assertTrue(voList.get(0).getChildren().size() >= 2);

        List<LabelValue> options = dictionaryService.getLabelValueList("GENDER");
        Assert.assertTrue(options.size() >= 2);
    }

    @Test
    public void testSimpleDictVo(){
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", Cons.ID_PREVENT_NULL).eq("type", "GENDER");

        List<SimpleDictionaryVO> simpleVOList = dictionaryService.getViewObjectList(queryWrapper, null, SimpleDictionaryVO.class);
        Assert.assertTrue(simpleVOList.size() == 1);
        Assert.assertTrue(simpleVOList.get(0).getChildren().size() >= 2);
    }

    /**
     * 测试n-n的批量新建/更新
     */
    @Test
    @Transactional
    public void testCreateUpdateN2NRelations(){
        String userId = S.valueOf("10001");
        LambdaQueryWrapper<UserRole> queryWrapper = new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, userId);

        // 新增
        List<String> roleIdList = Arrays.asList("10", "11", "12");
        userService.createOrUpdateN2NRelations(UserRole::getUserId, userId, UserRole::getRoleId, roleIdList);
        List<UserRole> list = ContextHolder.getBaseMapperByEntity(UserRole.class).selectList(queryWrapper);
        Assert.assertTrue(list.size() == roleIdList.size());

        // 更新
        roleIdList = Arrays.asList("12", "13");
        userService.createOrUpdateN2NRelations(UserRole::getUserId, userId, UserRole::getRoleId, roleIdList);
        list = ContextHolder.getBaseMapperByEntity(UserRole.class).selectList(queryWrapper);
        Assert.assertTrue(list.size() == 2);

        // 删除
        roleIdList = Collections.emptyList();
        userService.createOrUpdateN2NRelations(UserRole::getUserId, userId, UserRole::getRoleId, roleIdList);
        list = ContextHolder.getBaseMapperByEntity(UserRole.class).selectList(queryWrapper);
        Assert.assertTrue(list.size() == 0);
    }

    @Test
    public void testCache(){
        EntityInfoCache entityInfoCache = BindingCacheManager.getEntityInfoByClass(Dictionary.class);
        Assert.assertTrue(entityInfoCache != null);
        Assert.assertTrue(entityInfoCache.getDeletedColumn().equals("is_deleted"));

        entityInfoCache = BindingCacheManager.getEntityInfoByClass(CcCityInfo.class);
        Assert.assertTrue(entityInfoCache != null);
        Assert.assertTrue(entityInfoCache.getIdColumn().equals("id"));
        Assert.assertTrue(entityInfoCache.getDeletedColumn() == null);

        BaseMapper baseMapper = BindingCacheManager.getMapperByTable("user_role");
        Assert.assertTrue(baseMapper != null);

        Class<?> entityClass = BindingCacheManager.getEntityClassBySimpleName("Dictionary");
        Assert.assertTrue(entityClass != null && entityClass.getName().equals(Dictionary.class.getName()));

        // 测试PropInfo缓存
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "GENDER").eq("item_value", "F");
        Dictionary dictionary = dictionaryService.getSingleEntity(queryWrapper);

        DictionaryVO dictionaryVO = RelationsBinder.convertAndBind(dictionary, DictionaryVO.class);
        Assert.assertTrue(dictionaryVO.getPrimaryKeyVal().equals(dictionary.getId()));
        Assert.assertTrue(ContextHolder.getIdFieldName(dictionaryVO.getClass()).equals("id"));
    }

    @Test
    public void testMap(){
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .select("sum(id) as count");
        Map<String, Object> map = dictionaryService.getMap(queryWrapper);
        Assert.assertTrue(map!=null);
        Assert.assertTrue(MapUtils.getIgnoreCase(map, "count") != null);
        Assert.assertTrue(MapUtils.getIgnoreCase(map, "COUNT") != null);
    }

    @Test
    public void tesExecuteMultipleUpdateSqls() throws Exception {
        List<String> sqls = new ArrayList<>();
        Long dictId = 20000l;
        if(ContextHolder.getDatabaseType().equals("dm")) {
            sqls.add("SET IDENTITY_INSERT dbt_dictionary ON");
        }
        sqls.add("INSERT INTO dbt_dictionary(id, parent_id, type, item_name) VALUES('"+dictId+"', '0', 'TEST', '')");
        sqls.add("DELETE FROM dbt_dictionary WHERE id='20000' AND is_deleted!="+BaseConfig.getActiveFlagValue());
        boolean success = SqlFileInitializer.executeMultipleUpdateSqlsWithTransaction(sqls);
        Assert.assertTrue(success);
        Dictionary dict = dictionaryService.getEntity(dictId);
        Assert.assertTrue(dict != null);

        sqls.clear();
        sqls.add("DELETE FROM dbt_dictionary WHERE id='20000'");
        success = SqlFileInitializer.executeMultipleUpdateSqlsWithTransaction(sqls);
        Assert.assertTrue(success);

        sqls.clear();
        sqls.add("INSERT INTO dbt_dictionary(id, parent_id, type, item_name) VALUES('"+dictId+"', '0', 'TEST', '')");
        sqls.add("UPDATE dbt_dictionary SET is_deleted=1 WHERE id='20000' AND deleted=0");
        try{
            success = SqlFileInitializer.executeMultipleUpdateSqlsWithTransaction(sqls);
            Assert.assertTrue(false);
        }
        catch (Exception e) {
            Assert.assertTrue(true);
        }
        dict = dictionaryService.getEntity(dictId);
        Assert.assertTrue(dict == null);
    }

    @Test
    public void testDictExtdata(){
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", Cons.ID_PREVENT_NULL).eq("type", "GENDER");
        Dictionary dictionary = dictionaryService.getSingleEntity(queryWrapper);
        if(dictionary.getExtension() == null){
            Map<String, Object> map = new HashMap<>();
            map.put("createByName", "zhangs");
            dictionary.setExtension(map);
            dictionaryService.updateEntity(dictionary);
            dictionary = dictionaryService.getSingleEntity(queryWrapper);
        }
        Assert.assertTrue(dictionary.getExtension() != null);
        Assert.assertTrue(dictionary.getFromExt("createByName").equals("zhangs"));
    }

    @Test
    public void testGetEntityList(){
        DataScopeManager checkImpl = ContextHolder.getBean(DataScopeManager.class);
        Assert.assertTrue(checkImpl != null);
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "item_name", "item_value")
                .eq("id", -1L);
        List<Dictionary> dictionaries = dictionaryService.getEntityList(queryWrapper);
        Assert.assertTrue(dictionaries != null);
        Assert.assertTrue(dictionaries.size() == 0);
    }

    @Test
    public void testDelete(){
        CcCityInfo cityInfo = ContextHolder.getBean(CcCityInfoService.class).list().get(0);
        //ContextHolder.getBean(CcCityInfoService.class).removeById(cityInfo.getId());
    }

    @Test
    public void testJdbcTemplate(){
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("SELECT * FROM department");
        Assert.assertTrue(mapList != null);
        System.out.println(mapList);

        List<Long> objList = jdbcTemplate.queryForList("SELECT id FROM department", Long.class);
        Assert.assertTrue(objList != null);
        Assert.assertTrue(objList.get(0) != null);

        jdbcTemplate.execute("UPDATE department SET name='A' WHERE id=0");
    }

    @Test
    public void testJson() {
        List<Department> departments = departmentService.getEntityList(null);
        Assert.assertTrue(departments != null);
        for(Department dept : departments) {
            if(V.notEmpty(dept.getCharacter()) && dept.getCharacter().contains(",")){
                Assert.assertTrue(dept.getExtjsonarr().size() == S.split(dept.getCharacter()).length);
                Assert.assertTrue(dept.getExtjsonobj() != null);
            }
        }
    }

    @Test
    public void testGetVoListByChainQuery() {
        // 测试 QueryChainWrapper 与 WrapperHelper.optimizeSelect 方法联动
        testGetVoListByChainQuery(dictionaryService.query().eq("parent_id", Cons.ID_PREVENT_NULL).eq("type", "GENDER"));
        // 测试 LambdaQueryChainWrapper 与 WrapperHelper.optimizeSelect 方法联动
        testGetVoListByChainQuery(dictionaryService.lambdaQuery().eq(Dictionary::getParentId, "0").eq(Dictionary::getType, "GENDER"));
    }

    private void testGetVoListByChainQuery(Wrapper<?> query) {
        List<SimpleDictionaryVO> simpleVOList = dictionaryService.getViewObjectList(query, null, SimpleDictionaryVO.class);
        Assert.assertEquals(1, simpleVOList.size());
        Assert.assertTrue(simpleVOList.get(0).getChildren().size() >= 2);
        Assert.assertTrue(dictionaryService.exists(query));
        Assert.assertTrue(dictionaryService.getValuesOfField(query, Dictionary::getItemValue).isEmpty());
    }

    @Test
    public void test() {
        List<RegionVO> tree = regionService.getViewObjectTree("0", RegionVO.class, Region::getParentIdsPath, null);
        Assert.assertTrue(tree != null);
        for (RegionVO vo : tree) {
            if (vo.getId().equals("800")) {
                Assert.assertTrue(vo.getChildren().size() > 1);
            }
        }
        tree = regionService.getViewObjectTree("800", RegionVO.class, Region::getParentIdsPath, null );
        for (RegionVO vo : tree) {
            if(vo.getId().equals("839")) {
                Assert.assertTrue(vo.getChildren().size() > 1);
            }
        }
    }

}
