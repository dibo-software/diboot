<p align="center">
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="https://img.shields.io/hexpm/l/plug.svg">
    </a>
</p>

# diboot-v2
diboot 2.0版本项目，实现: diboot-core全新内核 + diboot-devtools代码生成。

### ** diboot-core: 精简优化内核
全新精简内核，主要实现单表CRUD和多表关联绑定的无SQL实现方案，并提供其他常用开发场景的简单封装。

#### 单表CRUD无SQL
   > 基于Mybatis-Plus实现（Mybatis-Plus具备通用Mapper方案和灵活的查询构造器）
#### 多表关联查询无SQL（适用于大多数场景，拆分成单表查询自动实现结果绑定）
   > 通过注解实现多数场景下的关联查询无SQL化自动绑定
   
##### 1. @BindDict 注解自动绑定数据字典(枚举值)的显示值Label
##### 2. @BindField 注解自动绑定其他表的字段
##### 3. @BindEntity 注解自动绑定单个其他表实体Entity
##### 4. @BindEntityList 注解自动绑定其他表实体集合List<Entity>

具体请查看: [diboot-core 注解自动绑定多表关联](https://github.com/dibo-software/diboot-v2/tree/master/diboot-core "注解自动绑定多表关联"). 

 
   > ...
     

### ** diboot-shiro: 基于RBAC+Shiro的权限认证模块
RBAC的角色权限+基于Shiro的细粒度权限控制

#### 1、@AuthorizationPrefix 
类注解，与@AuthorizationWrapper搭配使用，设置通用权限前缀，作用域为当前类的所有方法

#### 2、@AuthorizationWrapper 
类/方法注解，在保证shiro的@RequirePermissions注解的功能基础上，增加名称、权限前缀特性，使用方式同@RequiresPermissions

#### 3、AuthorizationProperties
提供自动入库的配置：包括权限环境变量 和权限是否入库
#### 4、AuthorizationStorage
调用该类autoStorage传入spring上下文参数，使用参考diboot-example 中ExampleListener类

### ** diboot-example: 示例
各组件使用示例项目
   > 运行example需先执行/resources/init-mysql.sql到数据库。 
   
### ** diboot-devtools 代码生成工具
   > 比 1.x 版本更强大的代码生成工具 ...
    
...