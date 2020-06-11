# diboot-iam-base-spring-boot-starter相关

## diboot支持Spring Boot哪些版本？
* diboot 2.0.x 支持 Spring boot 2.2.x
* diboot 2.1.x 支持 Spring boot 2.3+

## 如何自定义fastjson配置
diboot-core-starter中包含默认的HttpMessageConverters配置，启用fastjson并做了初始化配置。
其中关键配置参数为：
~~~java
@Bean
public HttpMessageConverters fastJsonHttpMessageConverters() {
    ...
    // 设置fastjson的序列化参数：禁用循环依赖检测，数据兼容浏览器端（避免JS端Long精度丢失问题）
    fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.BrowserCompatible);
    ...
}
~~~
如果该配置无法满足您的开发场景，可以在Configuration文件中重新定义HttpMessageConverters：
~~~java
@Bean
public HttpMessageConverters fastJsonHttpMessageConverters() {
    ...
}
~~~

## 无数据库连接配置文件的module下，如何使用diboot-core？
diboot-core-starter是在diboot-core的基础上增加了自动配置，配置需要依赖数据库信息。
如果是无数据库信息的模块下使用，可以依赖core，替换core-starter。
~~~xml
<dependency>
    <groupId>com.diboot</groupId>
    <artifactId>diboot-core</artifactId>
    <version>{latestVersion}</version>
</dependency>
~~~

## 启动报错：找不到mapper中的自定义接口
diboot-devtools默认不指定mapper.xml路径时，mapper.xml文件会生成到mapper同路径下便于维护。
此时需要修改pom配置，让编译包含xml、dtd类型文件。
* Maven配置：
~~~xml
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
        <resource>
            <directory>src/main/resources</directory>
        </resource>
    </resources>
</build>
~~~
* Gradle配置：
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

## 如何构建树形结构？
> 树形结构对象约定：要有 parentId属性 和 List children 属性，便于自动构建。
* 1. 先把需要构建树形结构的节点全部查出来，如:
~~~java
List<Menu> menus = menuService.getEntityList(wrapper);
~~~
* 2. 调用BeanUtils.buildTree构建树形结构
~~~java
// 如果children属性在VO中，可以调用BeanUtils.convertList转换后再构建
menus = BeanUtils.buildTree(menus);
~~~

## IAM的后端代码在哪里？
IAM的后端基础代码由devtools自动生成
* 配置好diboot组件依赖和devtools依赖
* 启动项目，进入devtools的组件初始化页面，选择core及IAM等组件，执行初始化

注：[diboot-v2-example](https://github.com/dibo-software/diboot-v2-example) 中包含可供参考的后端示例：diboot-iam-example（IAM示例代码）
及diboot-online-demo（线上演示项目）。

## 为何引入iam后启动报错？
确保您配置了**@EnableTransactionManagement**注解，可参考 [IAM参数配置-注解配置](/guide/diboot-iam/开始使用.html#_2、参数配置：)