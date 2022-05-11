package diboot.core.test.service;

import com.diboot.core.converter.*;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

/**
 * 扩展的转换service
 * @author JerryMa
 * @version v2.6.0
 * @date 2022/5/11
 * Copyright © diboot.com
 */
@Primary
@Component
public class EnhancedConversionService2 extends EnhancedConversionService {

    public EnhancedConversionService2(){
        super();
        //添加扩展
        //addConverter(new String2DateConverter());
    }

}
