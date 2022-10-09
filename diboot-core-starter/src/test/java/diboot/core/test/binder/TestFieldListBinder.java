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
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.DemoTest;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.entity.Organization;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.DemoTestService;
import diboot.core.test.binder.service.DepartmentService;
import diboot.core.test.binder.service.OrganizationService;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.*;
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
public class TestFieldListBinder {

    @Autowired
    UserService userService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    OrganizationService organizationService;
    @Autowired
    DemoTestService demoTestService;

    /**
     * 验证直接关联的绑定
     */
    @Test
    public void testSimpleBinder(){
        // 加载测试数据
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getId, 10001L);
        List<Department> entityList = departmentService.list(queryWrapper);
        // 自动绑定
        List<EntityListSimpleVO> voList = Binder.convertAndBindRelations(entityList, EntityListSimpleVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityListSimpleVO vo : voList){
            // 验证直接关联的绑定
            Assert.assertTrue(V.notEmpty(vo.getChildrenIds()));
            System.out.println(JSON.stringify(vo.getChildrenIds()));
            // 验证直接关联的绑定
            Assert.assertTrue(V.notEmpty(vo.getChildrenNames()));
            System.out.println(JSON.stringify(vo.getChildrenNames()));
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
            Assert.assertTrue(V.notEmpty(vo.getRoleCodes()));
            Assert.assertTrue(V.notEmpty(vo.getRoleCreateDates()));

            System.out.println(JSON.stringify(vo.getRoleCodes()));
        }
    }

    /**
     * 验证DemoTestJoin反向绑定
     */
    @Test
    public void testDemoTestJoinBinder(){
        // 加载测试数据
        LambdaQueryWrapper<DemoTest> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.in(DemoTest::getId, 1001L, 1002L);
        List<DemoTest> entList = demoTestService.getEntityList(queryWrapper);
        // 自动绑定
        List<DemoTestVO> voList = Binder.convertAndBindRelations(entList, DemoTestVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(DemoTestVO vo : voList){
            Assert.assertTrue(vo.getEmails() != null);
        }
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
            Assert.assertTrue(V.notEmpty(vo.getManagerNames()));
            System.out.println(JSON.stringify(vo));
            if(vo.getCharacter().contains(",")){
                String[] valueArr = S.clearNonConst(vo.getCharacter()).split(",");
                if(valueArr[0].equals(valueArr[1])){
                    Assert.assertTrue(vo.getManagerNames().size() == 1);
                    Assert.assertTrue(vo.getManagerNamesByJson().size() == 1);
                }
                else{
                    Assert.assertTrue(vo.getManagerNames().size() > 1);
                    Assert.assertTrue(vo.getManagerNamesByJson().size() > 1);
                }
            }
            else{
                Assert.assertTrue(vo.getManagerNames().size() == 1);
            }
        }
    }

    /**
     * 验证通过中间表间接关联的绑定
     */
    @Test
    public void testUUIDSplitBinder(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, 1001L, 1002L);
        List<User> userList = userService.getEntityList(queryWrapper);
        // 自动绑定
        List<SimpleUuidSplitVO> voList = Binder.convertAndBindRelations(userList, SimpleUuidSplitVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(SimpleUuidSplitVO vo : voList){
            // 验证通过中间表间接关联的绑定
            if(vo.getId().equals(1002L)){
                Assert.assertTrue(vo.getPhotos().size() == 2);
                Assert.assertTrue(vo.getPhotoNames().size() == 2);
            }
            else{
                Assert.assertTrue(vo.getPhotos() != null);
                Assert.assertTrue(vo.getPhotoNames() != null);
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
                Assert.assertTrue(vo.getManagerPhotoNames().size() == 1);
            }
            else{
                Assert.assertTrue(vo.getManagerPhotoNames().size() == 2);
            }
            System.out.println(JSON.stringify(vo));
        }
    }

}
