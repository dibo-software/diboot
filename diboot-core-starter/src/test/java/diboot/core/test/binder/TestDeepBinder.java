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
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.Department;
import diboot.core.test.binder.service.DepartmentService;
import diboot.core.test.binder.vo.DeepBindVO;
import diboot.core.test.binder.vo.DepartmentVO;
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
public class TestDeepBinder {

    @Autowired
    DepartmentService departmentService;

    @Test
    public void testEntityListDeepBinder(){
        // 加载测试数据
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        List<Department> entityList = departmentService.list(queryWrapper);
        // 自动绑定
        List<DeepBindVO> voList = Binder.convertAndBindRelations(entityList, DeepBindVO.class);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));
        for(DeepBindVO vo : voList) {
            if(vo.getParentId().equals("0")){
                Assert.assertTrue(vo.getParentDept() == null);
            }
            else{
                Assert.assertTrue(vo.getParentDept() != null);
                Assert.assertTrue(vo.getParentDept().getOrganizationVO() != null);
                if(vo.getParentId().equals("10001")){
                    Assert.assertTrue(vo.getParentDept().getChildren().size() == 3);
                }
                else if(vo.getParentId().equals("10003")){
                    Assert.assertTrue(vo.getParentDept().getChildren().size() == 2);
                }
            }
            if(vo.getOrgId().equals("100001")){
                Assert.assertTrue(vo.getOrganizationVO().getParentOrg() == null);
            }
            else{
                Assert.assertTrue(vo.getOrganizationVO().getParentOrg() != null);
                Assert.assertTrue(vo.getOrganizationVO().getParentOrgName() != null);
            }
            if(vo.getId().equals("10001")){
                Assert.assertTrue(vo.getChildren().size() == 3);
                // 验证深度绑定
                List<DepartmentVO> children = vo.getChildren();
                Assert.assertTrue(children.get(0).getOrganizationVO() != null);
                Assert.assertTrue(children.get(1).getOrganizationVO() != null);
                // 测试第4层
                Assert.assertTrue(children.get(0).getOrganizationVO().getManagerGenderLabel() != null);
                Assert.assertTrue(children.get(1).getOrganizationVO().getManagerGenderLabel() != null);
            }
            else if(vo.getId().equals("10003")){
                Assert.assertTrue(vo.getChildren().size() == 2);
                // 验证深度绑定
                Assert.assertTrue(vo.getChildren().get(0).getOrganizationVO() != null);
            }
            else if(vo.getId().equals("10005")){
                Assert.assertTrue(V.isEmpty(vo.getChildren()));
            }
            System.out.println(JSON.stringify(vo));
        }
    }

}
