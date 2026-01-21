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
package diboot.core.test.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.diboot.core.data.access.DataScopeManager;
import com.diboot.core.data.protect.DataEncryptHandler;
import com.diboot.core.data.protect.DataMaskHandler;
import com.diboot.core.data.protect.DefaultDataEncryptHandler;
import com.diboot.core.data.protect.DefaultDataMaskHandler;
import com.diboot.core.extension.sequence.DefaultSequenceGenerator;
import com.diboot.core.extension.sequence.Part;
import com.diboot.core.extension.sequence.SequenceGenerator;
import com.diboot.core.extension.sequence.counter.MemoryCacheSeqCounter;
import com.diboot.core.extension.sequence.counter.SeqCounter;
import com.diboot.core.handler.DataAccessControlHandler;
import com.diboot.core.serial.deserializer.LocalDateTimeDeserializer;
import com.diboot.core.serial.serializer.BigDecimal2StringSerializer;
import com.diboot.core.util.ContextHolder;
import com.diboot.core.util.D;
import com.fasterxml.jackson.annotation.JsonInclude;
import diboot.core.test.binder.DataAccessPermissionTestImplForDepartment;
import diboot.core.test.binder.entity.Problem;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Spring配置文件
 * @author mazc@dibo.ltd
 * @version v2.0
 * @date 2019/6/10
 */
@TestConfiguration
@ComponentScan(basePackages={"com.diboot.core", "diboot.core.test"})
@MapperScan({"com.diboot.core.mapper", "diboot.core.test.binder.mapper"})
public class SpringMvcConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(SpringMvcConfig.class);

    @Value("${spring.jackson.date-format:"+D.FORMAT_DATETIME_Y4MDHMS+"}")
    private String defaultDatePattern;

    @Value("${spring.jackson.time-zone:GMT+8}")
    private String defaultTimeZone;

    @Value("${spring.jackson.default-property-inclusion:NON_NULL}")
    private JsonInclude.Include defaultPropertyInclusion;

    @Bean
    @ConditionalOnMissingBean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.enable(SerializationFeature.INDENT_OUTPUT);
            builder.addModule(new SimpleModule()
                    .addSerializer(Long.class, ToStringSerializer.instance)
                    .addSerializer(Long.TYPE, ToStringSerializer.instance)
                    .addSerializer(BigInteger.class, ToStringSerializer.instance)
                    // BigDecimal转换成String避免JS超长问题，以及格式化数值
                    .addSerializer(BigDecimal.class, new BigDecimal2StringSerializer())
                    // 日期时间格式
                    .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(D.FORMATTER_DATETIME_Y4MDHMS))
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer())
                    .addSerializer(LocalDate.class, new LocalDateSerializer(D.FORMATTER_DATE_Y4MD))
                    .       addDeserializer(LocalDate.class, new LocalDateDeserializer(D.FORMATTER_DATE_Y4MD))
                    .addSerializer(LocalTime.class, new LocalTimeSerializer(D.FORMATTER_TIME_HM))
                    .addDeserializer(LocalTime.class, new LocalTimeDeserializer(D.FORMATTER_TIME_HM))
            );
            // 设置序列化包含策略
            builder.changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(defaultPropertyInclusion));
            builder.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 时间格式化
            builder.defaultTimeZone(TimeZone.getTimeZone(defaultTimeZone));
            SimpleDateFormat dateFormat = new SimpleDateFormat(defaultDatePattern) {
                @Override
                public Date parse(String dateStr) {
                    return D.fuzzyConvert(dateStr);
                }
            };
            builder.defaultDateFormat(dateFormat);
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public JacksonJsonHttpMessageConverter jacksonMessageConverter() {
        return new JacksonJsonHttpMessageConverter(JsonMapper.builder().build());
    }

    /**
     * 数据加密解密处理器
     */
    @Bean
    @ConditionalOnMissingBean(DataEncryptHandler.class)
    public DataEncryptHandler dataEncryptHandler() {
        return new DefaultDataEncryptHandler();
    }

    /**
     * 数据脱敏处理器
     */
    @Bean
    @ConditionalOnMissingBean(DataMaskHandler.class)
    public DataMaskHandler dataMaskHandler() {
        return new DefaultDataMaskHandler();
    }

    /**
     * 默认支持String-Date类型转换
     *
     * @param registry registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        List<Converter> converterList = ContextHolder.getBeans(Converter.class);
        if (converterList != null && !converterList.isEmpty()) {
            converterList.forEach(registry::addConverter);
        }
    }

    /**
     * 配置拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据权限拦截器
        //interceptor.addInnerInterceptor(new DataPermissionInterceptor(new DataAccessControlHandler()));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    @Bean
    public DataAccessControlHandler dataAccessControlHandler() {
        return new DataAccessControlHandler();
    }

    /**
     * 通过spring初始化一个实例
     * @return
     */
    @Bean
    public DataScopeManager dataScopeManager() {
        return new DataAccessPermissionTestImplForDepartment();
    }

    /**
     * 计数器
     */
    @Bean
    public SeqCounter memoryCacheSeqCounter() {
        log.info("初始化 流水号计数器 内存缓存: MemoryCacheSeqCounter");
        return new MemoryCacheSeqCounter();
    }

    @Bean
    public SequenceGenerator sequenceGenerator(SeqCounter seqCounter) {
        /*List<Part> parts = Part.cons("No.")
                    .append(Part.date(D.FORMAT_DATE_y4Md))
                    .append(Part.seq(4))
                    .build();*/
        List<Part> partList = Arrays.asList(
            Part.cons("No."),
            Part.date(D.FORMAT_DATE_y4Md),
            Part.seq(4)
        );
        return new DefaultSequenceGenerator<>(seqCounter, Problem::getSn, partList);
    }

}