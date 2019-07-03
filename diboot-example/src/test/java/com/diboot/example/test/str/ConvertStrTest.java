package com.diboot.example.test.str;

import com.diboot.core.service.impl.BaseServiceImpl;
import org.junit.Test;
import org.springframework.stereotype.Component;

@Component
public class ConvertStrTest{

    @Test
    public void convert2Hump() throws Exception{
        String str = "aaa_bbb_ccc_ddd_eee_fff";
        BaseServiceImpl base = new BaseServiceImpl();
        String a = base.convert2Hump(str);
        System.out.println(a);
    }
}
