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
package diboot.core.test.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.diboot.core.handler.DataAccessControlInterceptor;
import com.diboot.core.util.D;
import com.diboot.core.util.DateConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/***
 * Spring配置文件
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/6/10
 */
@TestConfiguration
@ComponentScan(basePackages={"com.diboot", "diboot.core"})
@MapperScan({"com.diboot.core.mapper", "diboot.core.**.mapper"})
public class SpringMvcConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(SpringMvcConfig.class);

    @Value("${spring.jackson.date-format:"+D.FORMAT_DATETIME_Y4MDHMS+"}")
    private String defaultDatePattern;

    @Value("${spring.jackson.time-zone:GMT+8}")
    private String defaultTimeZone;

    /**
     * 覆盖Jackson转换
     **/
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = converter.getObjectMapper();
        // Long转换成String避免JS超长问题
        SimpleModule simpleModule = new SimpleModule();

        // 不显示为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);

        objectMapper.registerModule(simpleModule);
        // 时间格式化
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setTimeZone(TimeZone.getTimeZone(defaultTimeZone));
        SimpleDateFormat dateFormat = new SimpleDateFormat(defaultDatePattern) {
            @Override
            public Date parse(String dateStr) {
                return D.fuzzyConvert(dateStr);
            }
        };
        objectMapper.setDateFormat(dateFormat);
        // 设置格式化内容
        converter.setObjectMapper(objectMapper);

        converters.add(0, converter);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
       registry.addConverter(new DateConverter());
    }

    /**
     * 配置拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据权限拦截器
        interceptor.addInnerInterceptor(new DataAccessControlInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

}