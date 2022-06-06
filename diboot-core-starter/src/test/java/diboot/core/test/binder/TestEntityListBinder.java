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
package diboot.core.test.binder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.diboot.core.binding.Binder;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import com.diboot.core.vo.DictionaryVO;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.Organization;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.DepartmentService;
import diboot.core.test.binder.service.OrganizationService;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.ComplexSplitVO;
import diboot.core.test.binder.vo.EntityListComplexVO;
import diboot.core.test.binder.vo.EntityListSimpleVO;
import diboot.core.test.binder.vo.SimpleSplitVO;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 *  测试字段绑定
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/06/22
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestEntityListBinder {

    @Autowired
    UserService userService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    OrganizationService organizationService;

    /**
     * 验证直接关联的绑定
     */
    @Test
    public void testSimpleBinder(){
        // 加载测试数据
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Department::getId, 10001L, 10003L);
        List<Department> entityList = departmentService.list(queryWrapper);
        // 自动绑定
        List<EntityListSimpleVO> voList = Binder.convertAndBindRelations(entityList, EntityListSimpleVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityListSimpleVO vo : voList){
            // 验证直接关联的绑定
            Assert.assertTrue(V.notEmpty(vo.getChildren()));
            System.out.println(JSON.stringify(vo));
            Assert.assertTrue(V.notEmpty(vo.getChildrenNames()));
            for(int i=0; i<vo.getChildren().size(); i++){
                Department dept = vo.getChildren().get(i);
                Assert.assertTrue(dept.getName().equals(vo.getChildrenNames().get(i)));
            }
        }
    }

    /**
     * 验证通过中间表间接关联的绑定
     */
    @Test
    public void testComplexBinder(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, 1001L, 1002L);
        List<User> userList = userService.getEntityList(queryWrapper);
        // 自动绑定
        List<EntityListComplexVO> voList = Binder.convertAndBindRelations(userList, EntityListComplexVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityListComplexVO vo : voList){
            // 验证通过中间表间接关联的绑定
            Assert.assertTrue(V.notEmpty(vo.getRoleList()));
            System.out.println(JSON.stringify(vo));
        }
    }

    @Test
    public void testDictionaryBinder(){
        // 查询是否创建成功
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getType, "GENDER");
        queryWrapper.eq(Dictionary::getParentId, 0L);

        Dictionary dictionary = dictionaryService.getSingleEntity(queryWrapper);
        DictionaryVO vo = Binder.convertAndBindRelations(dictionary, DictionaryVO.class);
        Assert.assertTrue(vo.getChildren().size() > 0);
    }

    /**
     * 测试简单拆分绑定
     */
    @Test
    public void testSplitBinder(){
        // 加载测试数据
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getParentId, 10001L);
        List<Department> departmentList = departmentService.list(queryWrapper);
        // 自动绑定
        List<SimpleSplitVO> voList = Binder.convertAndBindRelations(departmentList,
                SimpleSplitVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(SimpleSplitVO vo : voList){
            // 验证通过中间表间接关联的绑定
            Assert.assertTrue(V.notEmpty(vo.getManagers()));
            System.out.println(JSON.stringify(vo));
            if(vo.getCharacter().contains(",")){
                String[] valueArr = vo.getCharacter().split(",");
                if(valueArr[0].equals(valueArr[1])){
                    Assert.assertTrue(vo.getManagers().size() == 1);
                }
                else{
                    Assert.assertTrue(vo.getManagers().size() > 1);
                }
            }
            else{
                Assert.assertTrue(vo.getManagers().size() == 1);
            }
        }
    }

    /**
     * 测试复杂拆分绑定
     */
    @Test
    public void testComplexSplitBinder(){
        // 加载测试数据
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        List<ComplexSplitVO> voList = organizationService
                .getViewObjectList(queryWrapper, null, ComplexSplitVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(ComplexSplitVO vo : voList){
            // 验证通过中间表间接关联的绑定
            if(vo.getManagerId().equals(1001L)){
                Assert.assertTrue(vo.getManagerPhotos().size() == 1);
                Assert.assertEquals(1, vo.getManagerPhotoList().size());
            }
            else{
                Assert.assertTrue(vo.getManagerPhotos().size() == 2);
            }
            System.out.println(JSON.stringify(vo));
        }
    }

}
