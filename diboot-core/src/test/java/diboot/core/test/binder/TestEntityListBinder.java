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
import diboot.core.test.binder.entity.Sysuser;
import diboot.core.test.binder.service.DepartmentService;
import diboot.core.test.binder.service.SysuserService;
import diboot.core.test.binder.vo.DepartmentVO;
import diboot.core.test.binder.vo.EntityListComplexBinderVO;
import diboot.core.test.binder.vo.EntityListSimpleBinderVO;
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
    SysuserService sysuserService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    DictionaryService dictionaryService;

    /**
     * 验证直接关联的绑定
     */
    @Test
    public void testSimpleBinder(){
        // 加载测试数据
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getId, 10001L);
        List<Department> entityList = departmentService.getEntityList(queryWrapper);
        // 自动绑定
        List<EntityListSimpleBinderVO> voList = Binder.convertAndBindRelations(entityList, EntityListSimpleBinderVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityListSimpleBinderVO vo : voList){
            // 验证直接关联的绑定
            Assert.assertTrue(V.notEmpty(vo.getChildren()));
            System.out.println(JSON.stringify(vo));

            if(vo.getChildren() != null){
                for(DepartmentVO dept : vo.getChildren()){
                    System.out.println(dept.toString());
                }
            }
        }
    }

    /**
     * 验证通过中间表间接关联的绑定
     */
    @Test
    public void testComplexBinder(){
        // 加载测试数据
        LambdaQueryWrapper<Sysuser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Sysuser::getId, 1001L, 1002L);
        List<Sysuser> userList = sysuserService.getEntityList(queryWrapper);
        // 自动绑定
        List<EntityListComplexBinderVO> voList = Binder.convertAndBindRelations(userList, EntityListComplexBinderVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityListComplexBinderVO vo : voList){
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
}
