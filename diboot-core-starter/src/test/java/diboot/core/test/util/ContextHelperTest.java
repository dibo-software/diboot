/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
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

import com.diboot.core.service.BaseService;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.ContextHelper;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.DemoTest;
import diboot.core.test.binder.service.DemoTestService;
import diboot.core.test.binder.service.impl.DemoTestServiceImpl;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ContextHelper单元测试
 * @author mazc@dibo.ltd
 * @version 1.0
 * @date 2022/06/22
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class ContextHelperTest {

    @Test
    public void testDatabaseType(){
        String dbType = ContextHelper.getDatabaseType();
        Assert.assertTrue(dbType.equals("mysql") || dbType.equals("dm"));

        BaseService baseService = ContextHelper.getBaseServiceByEntity(DemoTest.class);
        Assert.assertTrue(BeanUtils.getTargetClass(baseService).getName().equals(DemoTestServiceImpl.class.getName()));

    }

}
