package diboot.core.test.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.diboot.core.util.D;
import com.diboot.core.util.DateConverter;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/***
 * Spring配置文件
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/6/10
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement(proxyTargetClass=true)
@ComponentScan(basePackages={"com.diboot", "diboot.core"})
@MapperScan({"com.diboot.**.mapper", "diboot.**.mapper"})
public class SpringMvcConfig implements WebMvcConfigurer{
    private static final Logger log = LoggerFactory.getLogger(SpringMvcConfig.class);

    /**
     * JSON转换组件替换为fastJson
     */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(fastMediaTypes);
        // 配置转换格式
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 设置fastjson的序列化参数：禁用循环依赖检测，数据兼容浏览器端（避免JS端Long精度丢失问题）
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
                                            SerializerFeature.BrowserCompatible);
        fastJsonConfig.setDateFormat(D.FORMAT_DATETIME_Y4MDHM);
        converter.setFastJsonConfig(fastJsonConfig);

        HttpMessageConverter<?> httpMsgConverter = converter;
        return new HttpMessageConverters(httpMsgConverter);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
       registry.addConverter(new DateConverter());
    }

    /**
     * Mybatis-plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        return paginationInterceptor;
    }

}