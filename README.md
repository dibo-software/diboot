<p align="center">
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="https://img.shields.io/hexpm/l/plug.svg">
    </a>
</p>

# diboot-v2
diboot 2.0版本项目，实现: diboot-core全新内核 + diboot-components-*基础组件 + diboot-devtools代码生成平台。
### 技术交流QQ群: 731690096

## 一、 diboot-core: 精简优化内核
全新精简内核，主要实现<font color="red">单表CRUD无SQL 和 多表关联查询绑定的无SQL</font>实现方案，并提供其他常用开发场景的简单封装。

### 单表CRUD无SQL
   > 基于Mybatis-Plus实现（Mybatis-Plus具备通用Mapper方案和灵活的查询构造器）
### 多表关联查询无SQL（适用于多数关联场景，自动实现拆分成单表查询和结果绑定）
   > 通过注解实现多数场景下的关联查询无SQL化自动绑定
   
##### 1. @BindDict 注解自动绑定数据字典(枚举值)的显示值Label
##### 2. @BindField 注解自动绑定其他表的字段
##### 3. @BindEntity 注解自动绑定单个其他表实体Entity
##### 4. @BindEntityList 注解自动绑定其他表实体集合List<Entity>

具体请查看: [diboot-core README](https://github.com/dibo-software/diboot-v2/tree/master/diboot-core "注解自动绑定多表关联"). 

 
   > .
     

## 二、 diboot-shiro: 基于RBAC+Shiro的权限认证模块
RBAC的角色权限+基于Shiro的细粒度权限控制

### 1、@AuthorizationPrefix 
类注解，与@AuthorizationWrapper搭配使用，设置通用权限前缀，作用域为当前类的所有方法

### 2、@AuthorizationWrapper 
类/方法注解，在保证shiro的@RequirePermissions注解的功能基础上，增加名称、权限前缀特性，使用方式同@RequiresPermissions

### 3、@AuthorizationCache 
方法注解，在资源授权校验过程中，系统会频繁与数据库进行交互，故而提供缓存机制
 * 缓存时机：缓存会在用户第一次进行权限验证的之后缓存数据
 * 当前注解作用：如果通过系统调整权限，只需要将该注解加在更新或添加权限处，将会清空权限缓存，下次进入将重新加载权限

### 4、AuthorizationProperties
提供一些权限相关的配置，主要包括：
- 权限环境变量：提供dev、test、prod三种选项
  - 默认dev，非dev状态下，每次都将读取当前启动系统中所有权限，不在系统内的权限将会自动物理删除，多人协作的开发环境建议使用默认配置
- 权限是否入库：
  - 默认false，但是我们建议开启，将会自动读取系统中的权限到数据库
- 拥有所有权限的角色：
  - 如果想给予角色所有资源权限，那么直接设置用户为配置的角色即可，如果不配置，那么默认使用的是ADMIN角色

示例配置：
```
#权限是否存储数据库：默认不开启
diboot.shiro.auth.storage=true
#存储环境：默认dev环境，不会删除代码中不存在的权限
diboot.shiro.auth.env=dev
#配置所有资源均可访问的角色：ALL1、ALL2、ALL3，不配置时，默认最高权限为ADMIN
diboot.shiro.auth.has-all-permissions-role-list[0]=ALL1
diboot.shiro.auth.has-all-permissions-role-list[1]=ALL2
diboot.shiro.auth.has-all-permissions-role-list[2]=ALL3
#配置权限缓存机制
##是否开启缓存
diboot.shiro.cache.permission-caching-enabled=true
##缓存方式：暂时提供shiro内置的内存缓存
diboot.shiro.cache.cache-way=memory
```

### 5、AuthorizationStorage
调用该类autoStorage传入spring上下文参数，使用参考diboot-example 中ExampleListener类


## 三、 diboot-devtools 代码生成工具
   > 比 1.x 版本更强大的代码生成工具 ...
    
...