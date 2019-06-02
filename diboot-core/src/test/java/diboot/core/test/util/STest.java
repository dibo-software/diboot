package diboot.core.test.util;

import com.diboot.core.util.S;
import org.junit.Assert;
import org.junit.Test;

/**
 * S工具类测试
 * @author Mazhicheng
 * @version 1.0
 * @date 2019/06/02
 */
public class STest {

    @Test
    public void testToSnakeCase(){
        String str = "myOrgName";
        String snakeCaseStr = "my_org_name";
        Assert.assertEquals(S.toSnakeCase(str), snakeCaseStr);
    }

    @Test
    public void testCamelCase(){
        String snakeCaseStr = "my_org_name";
        String camelCaseStr = "myOrgName";
        Assert.assertEquals(S.toLowerCaseCamel(snakeCaseStr), camelCaseStr);
    }

    @Test
    public void testCapFirst(){
        String text = "helloWorld";
        Assert.assertEquals(S.capFirst(text), "HelloWorld");
        Assert.assertEquals(S.uncapFirst("HelloWorld"), text);
    }

}
