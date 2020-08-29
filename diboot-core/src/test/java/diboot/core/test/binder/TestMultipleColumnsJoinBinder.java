package diboot.core.test.binder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.binding.Binder;
import com.diboot.core.binding.QueryBuilder;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.service.DictionaryService;
import com.diboot.core.util.V;
import diboot.core.test.StartupApplication;
import diboot.core.test.binder.vo.MulColJoinVO;
import diboot.core.test.binder.vo.MulColMiddleJoinVO;
import diboot.core.test.config.SpringMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试多列条件JOIN
 * @author mazc@dibo.ltd
 * @version v2.1.2
 * @date 2020/08/25
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {SpringMvcConfig.class})
@SpringBootTest(classes = {StartupApplication.class})
public class TestMultipleColumnsJoinBinder {

    @Autowired
    DictionaryService dictionaryService;

    @Test
    public void testBinder(){
        QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "GENDER");
        queryWrapper.gt("parent_id", 0);
        List<Long> ids = dictionaryService.getValuesOfField(queryWrapper, Dictionary::getId);
        // 加载测试数据
        List<MulColJoinVO> voList = new ArrayList<>();
        MulColJoinVO vo1 = new MulColJoinVO();
        vo1.setDictType("GENDER");
        vo1.setDictId(ids.get(0));

        vo1.setOrgPid(0L);
        vo1.setTelphone("0512-62988949");
        voList.add(vo1);

        MulColJoinVO vo2 = new MulColJoinVO();
        vo2.setDictType("GENDER");
        vo2.setDictId(ids.get(1));

        vo2.setOrgPid(0L);
        vo2.setTelphone(null);
        voList.add(vo2);

        // 自动绑定
        Binder.bindRelations(voList);
        // 验证绑定结果
        Assert.assertTrue(V.notEmpty(voList));

        // 验证直接关联和通过中间表间接关联的绑定
        Assert.assertNotNull(voList.get(0).getParentDict().getType().equals(vo1.getDictType()));
        Assert.assertNotNull(voList.get(0).getParentDictName().equals("性别"));
        Assert.assertNotNull(voList.get(0).getOrgList().size() == 1);
        // 验证枚举值已绑定
        Assert.assertNotNull(voList.get(0).getOrgNames().contains("苏州帝博"));

        Assert.assertNotNull(voList.get(1).getParentDict().getType().equals(vo1.getDictType()));
        Assert.assertNotNull(voList.get(1).getParentDictName().equals("男"));
        Assert.assertNotNull(voList.get(1).getOrgList().size() == 1);
        // 验证枚举值已绑定
        Assert.assertNotNull(voList.get(1).getOrgNames().contains("成都帝博"));
    }

    @Test
    public void testMiddleTableBinder(){
        // 加载测试数据
        List<MulColMiddleJoinVO> voList = new ArrayList<>();
        MulColMiddleJoinVO vo1 = new MulColMiddleJoinVO();
        vo1.setUtype("SysUser");
        vo1.setUid(1001L);
        voList.add(vo1);

        MulColMiddleJoinVO vo2 = new MulColMiddleJoinVO();
        vo2.setUtype("SysUser");
        vo2.setUid(1002L);
        voList.add(vo2);
        // 自动绑定
        Binder.bindRelations(voList);

        vo1 = voList.get(0);
        Assert.assertTrue(vo1.getRoles().size() == 2);
        Assert.assertTrue(vo2.getRoles() == null);
    }

    @Test
    public void testMiddleTableBinder2(){
        // 加载测试数据
        List<MulColMiddleJoinVO> voList = new ArrayList<>();
        MulColMiddleJoinVO vo1 = new MulColMiddleJoinVO();
        vo1.setDepartmentId(10002L);

        vo1.setOrgId(100001L);
        vo1.setTelphone("0512-62988949");

        vo1.setUtype("SysUser");
        vo1.setUid(1001L);
        voList.add(vo1);

        MulColMiddleJoinVO vo2 = new MulColMiddleJoinVO();
        vo2.setDepartmentId(10003L);
        vo2.setOrgId(100001L);
        vo2.setTelphone(null); //不匹配
        vo2.setUtype("OrgUser");
        vo2.setUid(1002L);
        voList.add(vo2);

        // 自动绑定
        Binder.bindRelations(voList);

        Assert.assertTrue(vo1.getRoles().size() == 2);
        Assert.assertTrue(vo1.getRoleCodes().size() == 2);
        Assert.assertTrue(vo1.getRoleNames().size() == 2);
        Assert.assertTrue(vo1.getOrganization() != null);
        Assert.assertTrue(vo1.getOrgName() != null);
        Assert.assertTrue(vo1.getOrgParentId() != null);

        Assert.assertTrue(vo2.getRoles().size() == 1);
        Assert.assertTrue(vo2.getRoleCodes().size() == 1);
        Assert.assertTrue(vo2.getRoleNames().size() == 1);
        Assert.assertTrue(vo2.getOrganization() == null);
        Assert.assertTrue(vo2.getOrgName() != null);
        Assert.assertTrue(vo2.getOrgParentId() != null);
    }

}
