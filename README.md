<p align="center">
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="https://img.shields.io/hexpm/l/plug.svg">
    </a>
    <a href="https://mvnrepository.com/artifact/com.diboot" target="_blank">
        <img src="https://img.shields.io/maven-central/v/com.diboot/diboot-core">
    </a>
</p>

# diboot  
>  [设计目标](https://segmentfault.com/a/1190000020906742)：面向开发人员的低代码开发平台，将重复性的工作自动化，提高质量、效率、可维护性。

diboot v2版本，实现: diboot-core全新内核 + diboot-devtools代码生成平台 + IAM等基础功能组件。


## 一、 diboot-core: 精简优化内核
全新精简内核，(基于diboot-core 2.x版本的CRUD和简单关联的常规功能实现，代码量比1.x版本减少70%+），主要实现：
#### 1. 单表CRUD无SQL
   > 基于Mybatis-Plus实现（Mybatis-Plus具备通用Mapper方案和灵活的查询构造器）
#### 2. 关联查询无SQL（注解自动绑定）
   > 扩展实现了多表关联查询的无SQL方案，只需要一个简单注解@Bind*，就可以实现关联对象（含字段、实体、实体集合等）的数据绑定，且实现方案是将关联查询拆解为单表查询，保障最佳性能。
#### 3. 数据字典无SQL（注解自动绑定）
   > 通过@BindDict注解实现数据字典(枚举)的存储值value与显示值name的转换。
#### 4. Entity/DTO自动转换为QueryWrapper
   > @BindQuery注解绑定字段参数对应的查询条件类型，Controller中直接绑定转换为QueryWrapper，无需再手动构建QueryWrapper查询条件
#### 5. 其他常用工具类的最佳实践封装
   > 字符串处理、常用校验、BeanUtils、DateUtils等
   
更多介绍请查看: [diboot-core README](https://github.com/dibo-software/diboot-v2/tree/master/diboot-core "注解自动绑定多表关联"). 


## 二、 diboot-devtools 自动化开发助理

#### 1. 支持多数据库（MySQL、MariaDB、ORACLE、SQLServer、PostgreSQL）
#### 2. 使用很简单（引入依赖jar，配置参数后，即可随SpringBoot启动运行）
#### 3. 功能很强大（数据结构变更与代码联动同步，一键生成/更新代码，自动记录变更SQL）
#### 4. 配置很灵活（可按需配置生成代码是否启用`Lombok`、`Swagger`、`Shiro`等）
#### 5. 代码很标准（devtools标准化了数据结构定义与代码实现，降低维护成本）
> [我要试试](https://www.diboot.com/guide/diboot-devtools/%E4%BB%8B%E7%BB%8D.html)

## 三、iam-base 身份认证基础组件

#### 1. RBAC角色权限模型 + JWT的认证授权 实现
#### 2. BindPermission注解, 支持两级权限控制 + 自动鉴权
#### 3. BindPermission注解, 支持自动收集权限码并更新数据库
#### 4. 支持灵活的扩展能力（扩展多种登录方式、灵活替换用户实体类、自定义缓存等）

更多介绍请查看: [iam-base-starter README](https://github.com/dibo-software/diboot-v2/tree/master/iam-base-starter "身份认证管理组件"). 
> 其他组件逐步开发中 ...

## 四、技术交流群

如果您有技术问题，欢迎加群交流：

> QQ群: [731690096]() 

> 微信群（备注diboot）加: [wx20201024]()  
