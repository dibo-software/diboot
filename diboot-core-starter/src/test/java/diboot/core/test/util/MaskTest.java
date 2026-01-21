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
package diboot.core.test.util;

import com.diboot.core.util.JSON;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.entity.Customer;
import diboot.core.test.binder.service.CustomerService;
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
 * 测试数据加密
 *
 * @author mazc@dibo.ltd
 * @version v3.9.0
 * @date 2026/01/21
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class MaskTest {

    @Autowired
    private CustomerService customerService;
    @Test
    public void testMask() {
        List<Customer> customers = customerService.getEntityList(null);
        for (Customer customer : customers) {
            Assert.assertTrue(!customer.getCellphone().contains("*"));

            Customer maskedCustomer = JSON.parseObject(JSON.toJSONString(customer), Customer.class);
            Assert.assertTrue(maskedCustomer.getCellphone().contains("*"));
        }
    }
}
