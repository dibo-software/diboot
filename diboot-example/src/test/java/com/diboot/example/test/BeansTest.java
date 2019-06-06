package com.diboot.example.test;

import com.diboot.core.util.V;
import com.diboot.example.ApplicationTest;
import com.diboot.shiro.service.AuthWayService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BeansTest extends ApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    Map<String, AuthWayService> classMap;

    @Test
    public void testGetImplMap() throws Exception{
        Map<String, AuthWayService> authWayServiceMap = applicationContext.getBeansOfType(AuthWayService.class);
        Assert.assertTrue(V.notEmpty(authWayServiceMap));
    }

    @Test
    public void testAuthwiredImplMap() throws Exception{
        Assert.assertTrue(V.notEmpty(classMap));
        for (Map.Entry<String, AuthWayService> entry : classMap.entrySet()){

        }
    }

}
