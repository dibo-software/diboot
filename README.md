<p align="center">
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="https://img.shields.io/hexpm/l/plug.svg">
    </a>
</p>

# diboot-v2   (重复的工作自动做)
diboot 2.0版本项目，实现: diboot-core全新内核 + diboot-devtools代码生成平台 + 基础功能组件。

> diboot的设计目标：面向开发人员的低代码开发平台，提高开发效率和质量，提高代码可维护性。
   
## 一、 diboot-core: 精简优化内核
全新精简内核，(基于diboot-core 2.x版本的CRUD和简单关联的常规功能实现，代码量比1.x版本减少60%+），主要实现：
#### 1. 单表CRUD无SQL
   > 基于Mybatis-Plus实现（Mybatis-Plus具备通用Mapper方案和灵活的查询构造器）
#### 2. 关联查询无SQL（注解自动绑定）
   > 扩展实现了多表关联查询的无SQL方案，只需要一个简单注解@Bind*，就可以实现关联对象（含字段、实体、实体集合等）的数据绑定，且实现方案是将关联查询拆解为单表查询，保障最佳性能。
#### 3. 数据字典无SQL（注解自动绑定）
   > 通过@BindDict注解实现数据字典(枚举)的存储值value与显示值name的转换。
#### 4. Entity/DTO自动转换为QueryWrapper
   > @BindQuery注解绑定字段参数对应的查询条件类型，Controller中直接绑定转换为QueryWrapper，无需再手动构建QueryWrapper查询条件
#### 4. 其他常用开发场景的最佳实践封装
   > 字符串处理、常用校验、BeanUtils、DateUtils等
   
更多介绍请查看: [diboot-core README](https://github.com/dibo-software/diboot-v2/tree/master/diboot-core "注解自动绑定多表关联"). 



## 二、 diboot-devtools 代码生成工具 [(我要试试)](https://github.com/dibo-software/diboot-v2/blob/master/diboot-docs/guide/diboot-devtools/%E4%BB%8B%E7%BB%8D.md)

##### 1. 支持常用的五大数据库（MySQL，MariaDB，ORACLE，SQLServer, PostgreSQL）。
##### 2. 使用简单，只需在项目中引入devtools依赖，添加相关配置信息后，即可启动运行。
##### 3. 基于主流框架（SpringBoot + Mybatis-Plus）打造的优化内核，保证生成的代码更简洁，质量更高。
##### 4. 功能强大，实现数据结构变更与代码联动同步，方便维护数据结构及关联，一键生成/更新代码。
##### 5. 通过devtools维护数据结构，标准化了数据结构定义，同时数据结构变动SQL会被自动记录，便于同步更新生产等环境数据库。
##### 6. 使用灵活，可按需启用更多功能。例如：是否开启引入 `Lombok`、`Swagger`等。


## 三、技术交流群
如果Diboot对您有用，欢迎您为Diboot的发展提供赞助。我们不喝咖啡，所有赞助费用都将用于Diboot服务器支出。
<p align="center">
    <img src="https://www.diboot.com/img/donate.jpg" width = "500" alt="支持Diboot发展">
</p>
如果您有技术问题，欢迎加群交流：

> QQ群: [731690096]() 

> 微信群加: [wx20201024]()
