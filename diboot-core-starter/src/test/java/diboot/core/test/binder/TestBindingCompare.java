/*
 * Copyright (c) 2015-2099, www.dibo.ltd (service@dibo.ltd).
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
import com.diboot.core.binding.helper.VirtualThreadExecutor;
import com.diboot.core.util.JSON;
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.Organization;
import diboot.core.test.binder.entity.User;
import diboot.core.test.binder.service.OrganizationService;
import diboot.core.test.binder.service.UserService;
import diboot.core.test.binder.vo.ComplexSplitVO;
import diboot.core.test.binder.vo.EntityListComplexVO;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 关联绑定性能测试对比（是否开启虚拟线程）
 * @author diboot@dibo.ltd
 * @version v3.7.0
 * @date 2025/5/30
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestBindingCompare {

    @Autowired
    UserService userService;

    @Autowired
    OrganizationService organizationService;

    @Test
    public void testBindingPerformance() {
        long start = System.currentTimeMillis();
        int loopCount = 100;
        for (int i = 0; i < 100; i++) {
            testComplexBinder();
            testComplexSplitBinder();
        }
        int sqlCount = 13 * loopCount;
        long end = System.currentTimeMillis();
        SimpleAsyncTaskExecutor executorService = VirtualThreadExecutor.getVirtualThreadExecutor();
        String message = executorService==null? "未":"";
        System.out.println(message + "开启虚拟线程，执行 "+ sqlCount +" 次查询绑定，共耗时: " + (end - start) + "ms");
    }

    /**
     * 验证通过中间表间接关联的绑定
     */
    public void testComplexBinder(){
        // 加载测试数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        List<User> userList = userService.getEntityList(queryWrapper);
        // 自动绑定
        List<EntityListComplexVO> voList = Binder.convertAndBindRelations(userList, EntityListComplexVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(EntityListComplexVO vo : voList){
            // 验证通过中间表间接关联的绑定
            if(V.equals(vo.getId(), "1001")) {
                Assert.assertTrue(vo.getRoleCount() == 2);
            }
            else {
                Assert.assertTrue(vo.getRoleCount() == 0 || vo.getRoleCount() == 1);
            }
        }
    }

    /**
     * 测试复杂拆分绑定
     */
    public void testComplexSplitBinder(){
        // 加载测试数据
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        List<ComplexSplitVO> voList = organizationService
                .getViewObjectList(queryWrapper, null, ComplexSplitVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(ComplexSplitVO vo : voList){
            // 验证通过中间表间接关联的绑定
            if(vo.getManagerId().equals("1001")){
                Assert.assertTrue(vo.getManagerPhotos().size() == 1);
                Assert.assertEquals(2, vo.getManagerPhotoList().size());
            }
            else{
                Assert.assertTrue(vo.getManagerPhotos().size() == 2);
                Assert.assertTrue(V.isEmpty(vo.getManagerPhotoList()));
            }
            System.out.println(JSON.stringify(vo));
        }
    }

}
