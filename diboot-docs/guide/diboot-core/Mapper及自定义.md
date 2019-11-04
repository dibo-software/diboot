# Mapper及自定义

## Gradle设置
> 如果您使用了gradle，并且您的Mapper.xml文件实在java代码目录下，那么除了MapperScan的注解之外，还需要对gradle进行相关设置，使其能够找到相对应的Mapper.xml文件，如：
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

## Maven设置
> 如果您使用Maven作为您的构建工具，并且您的Mapper.xml文件是在java代码目录下，那么除了MapperScan的注解之外，还需要再项目中的pom.xml文件中的添加如下配置，使其能够找到您的Mapper.xml文件，如：
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
package com.example.demo.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.example.demo.entity.Demo;
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

> 自定义Mapper接口可以使用mybatis增加mapper接口的处理方案，如：
```java
package com.example.demo.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.example.demo.entity.Demo;
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