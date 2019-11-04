<p align="center">
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="https://img.shields.io/hexpm/l/plug.svg">
    </a>
</p>

# diboot-v2   (重复的工作自动做)
diboot 2.0版本项目，实现: diboot-core全新内核 + diboot-devtools代码生成平台 + 基础功能组件。

> diboot的设计目标：面向开发人员的低代码开发平台，提高开发效率和质量，提高代码可维护性。

## 技术交流:  QQ群加: [731690096]()      微信群加: [wx20201024]()

> 把重复有规律的事情自动化

   
## 一、 diboot-core: 精简优化内核
全新精简内核，主要实现<font color="red">单表CRUD无SQL 和 多表关联查询绑定的无SQL</font>实现方案，并提供查询绑定等常用开发场景的简单封装。

(基于diboot-core 2.x版本的CRUD和简单关联的功能实现，代码量比1.x版本减少60%+）
### 单表CRUD无SQL
   > 基于Mybatis-Plus实现（Mybatis-Plus具备通用Mapper方案和灵活的查询构造器）
### 多表关联查询无SQL（适用于多数关联场景，自动拆解成单表查询和绑定结果，提高性能，简化代码）
   > 通过注解实现多数场景下的关联查询无SQL化自动绑定
   
##### 1. @BindDict 注解自动绑定数据字典(枚举值)的显示值Label
##### 2. @BindField 注解自动绑定其他表的字段
##### 3. @BindEntity 注解自动绑定单个其他表实体Entity
##### 4. @BindEntityList 注解自动绑定其他表实体集合List<Entity>

### Entity/DTO自动转换为QueryWrapper
   > @BindQuery注解声明映射的查询条件，Controller中直接绑定转换为QueryWrapper
    
具体请查看: [diboot-core README](https://github.com/dibo-software/diboot-v2/tree/master/diboot-core "注解自动绑定多表关联"). 

 
   > .

## 二、 diboot-devtools 代码生成工具
#### 1. 支持常用的五大数据库（MySQL，MariaDB，ORACLE，SQLServer, PostgreSQL）。
#### 2. 使用简单，只需在项目中引入devtools依赖，添加相关配置信息后，即可启动运行。
#### 3. 基于主流框架（SpringBoot + Mybatis-Plus），打造全新优化内核，保证生成的代码更简洁，质量更高。
#### 4. 功能强大，实现数据结构变更与代码联动同步，更方便维护数据库表结构及关联关系，一键生成/更新代码。
#### 5. 通过devtools维护数据结构，标准化了数据结构定义，同时数据结构变动SQL会被自动记录，便于同步更新生产等环境数据库。
#### 6. 使用灵活，可按需启用更多功能。例如：是否开启引入 `Lombok`、`Swagger`等。

>  [我要试试](https://github.com/dibo-software/diboot-v2/blob/master/diboot-docs/guide/diboot-devtools/%E4%BB%8B%E7%BB%8D.md)
    
...