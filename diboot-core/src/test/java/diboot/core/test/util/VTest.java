package diboot.core.test.util;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * V校验工具类测试
 * @author Mazhicheng
 * @version 1.0
 * @date 2019/06/02
 */
public class VTest {

    @Test
    public void testEmpty(){
        String text1 = null;
        Assert.assertTrue(S.isEmpty(text1));
        text1 = "";
        Assert.assertTrue(S.isEmpty(text1));
        text1 = " ";
        Assert.assertFalse(S.isEmpty(text1));

        List<Long> list = new ArrayList<>();
        Assert.assertTrue(V.isEmpty(list));
        list.add(1L);
        Assert.assertTrue(V.notEmpty(list));
    }

    @Test
    public void testIsEmail(){
        String text1 = "jsdjfjsdf@126.com";
        String text2 = "jsdjfjsdf.126.com";
        String text3 = "jsdjfjsdf@126";
        Assert.assertTrue(V.isEmail(text1));

        Assert.assertFalse(V.isEmail(text2));
        Assert.assertFalse(V.isEmail(text3));
    }

    @Test
    public void testIsPhone(){
        String text1 = "17712345678";
        String text2 = "0512-12345678";
        String text3 = "40012345678";
        Assert.assertTrue(V.isPhone(text1));
        Assert.assertTrue(V.isPhone(text2));
        Assert.assertTrue(V.isPhone(text3));

        String text4 = "177123456789";
        Assert.assertFalse(V.isPhone(text4));
    }

    @Test
    public void testEquals(){
        Long val1 = 128L, val2 = 128L;
        Assert.assertFalse(val1 == val2);
        Assert.assertTrue(V.equals(val1, val2));

        List<Long> list1 = new ArrayList<>(), list2 = new ArrayList<>();
        list1.add(127L);
        list1.add(128L);
        list2.add(127L);
        list2.add(128L);

        Assert.assertTrue(V.equals(list1, list2));
    }



}
