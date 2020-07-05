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

import com.diboot.core.util.S;
import org.junit.Assert;
import org.junit.Test;

/**
 * S工具类测试
 * @author mazc@dibo.ltd
 * @version 1.0
 * @date 2019/06/02
 */
public class STest {

    @Test
    public void testToSnakeCase(){
        String camelCaseStr = "myOrgName";
        String snakeCaseStr = "my_org_name";
        Assert.assertEquals(S.toSnakeCase(camelCaseStr), snakeCaseStr);
        Assert.assertEquals(S.toSnakeCase(S.capFirst(camelCaseStr)), snakeCaseStr);
    }

    @Test
    public void testCamelCase(){
        String snakeCaseStr = "my_org_name";
        String camelCaseStr = "myOrgName";
        Assert.assertEquals(S.toLowerCaseCamel(snakeCaseStr), camelCaseStr);
        Assert.assertEquals(S.toLowerCaseCamel(snakeCaseStr.toUpperCase()), camelCaseStr);
    }

    @Test
    public void testCapFirst(){
        String text = "helloWorld";
        Assert.assertEquals(S.capFirst(text), "HelloWorld");
        Assert.assertEquals(S.uncapFirst("HelloWorld"), text);
    }

}