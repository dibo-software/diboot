# Mapper及自定义
> 如果您的mapper.xml文件放置于src下的mapper接口同目录下，需要配置编译包含该路径下的xml文件。具体参考如下：

* Gradle设置
```groovy
sourceSets {
    main {
        resources {
            srcDirs "src/main/java"
            include '**/*.xml'
            include '**/*.dtd'
            include '**/*.class'
        }
        resources {
            srcDirs "src/main/resources"
            include '**'
        }
    }
}
```
* Maven设置
```xml
<build>
    <resources>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
                <include>**/*.dtd</include>
            </includes>
            <filtering>false</filtering>
        </resource>
    </resources>
</build>
```

## Mapper类
> Mapper类需要继承diboot-core中的BaseCrudMapper基础类，并传入相对应的实体类，如：
```java
import com.diboot.core.mapper.BaseCrudMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoMapper extends BaseCrudMapper<Demo> {
}
```
 * BaseCrudMapper类继承了mybatis-plus提供的BaseMapper类，对于BaseCrudMapper中已有的相关接口可以参考[mybatis-plus关于Mapper类的文档](https://mybatis.plus/guide/crud-interface.html#mapper-crud-%E6%8E%A5%E5%8F%A3)
 
## Mapper.xml文件
> 默认的Mapper.xml文件如下所示：
```java
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "./mybatis-3-mapper.dtd">
<mapper namespace="com.example.demo.mapper.DemoMapper">

</mapper>
```

## 自定义Mapper接口

> 自定义Mapper接口使用Mybatis处理方式，增加mapper接口的处理方案，如：
```java
import com.diboot.core.mapper.BaseCrudMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DemoMapper extends BaseCrudMapper<Demo> {
    int forceDeleteEntities(@Param("name") String name);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "./mybatis-3-mapper.dtd">
<mapper namespace="com.example.demo.mapper.DemoMapper">
    
    <update id="forceDeleteEntities" parameterType="String">
        DELETE FROM demo WHERE name=#{name}
    </update>
    
</mapper>
```