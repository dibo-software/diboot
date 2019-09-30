<p align="center">
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="https://img.shields.io/hexpm/l/plug.svg">
    </a>
</p>

# diboot-v2
diboot 2.0版本项目，实现: diboot-core全新内核 + diboot-shiro-*权限控制 + diboot-components-*基础组件 + diboot-devtools代码生成平台。

> diboot的设计目标：面向开发人员的低代码开发平台，提高开发质量和效率，提高代码可维护性。

## 技术交流QQ群: 731690096

> 复杂的事情简单化，简单的事情标准化，标准的事情流程化，流程的事情自动化

**2.0版devtools将于国庆节后发布，敬请期待。不多说了，我要给祖国母亲庆生去了 : )**
   
## 一、 diboot-core: 精简优化内核
全新精简内核，主要实现<font color="red">单表CRUD无SQL 和 多表关联查询绑定的无SQL</font>实现方案，并提供其他常用开发场景的简单封装。

### 单表CRUD无SQL
   > 基于Mybatis-Plus实现（Mybatis-Plus具备通用Mapper方案和灵活的查询构造器）
### 多表关联查询无SQL（适用于多数关联场景，自动实现拆分成单表查询和结果绑定，保障性能佳和维护易）
   > 通过注解实现多数场景下的关联查询无SQL化自动绑定
   
##### 1. @BindDict 注解自动绑定数据字典(枚举值)的显示值Label
##### 2. @BindField 注解自动绑定其他表的字段
##### 3. @BindEntity 注解自动绑定单个其他表实体Entity
##### 4. @BindEntityList 注解自动绑定其他表实体集合List<Entity>

具体请查看: [diboot-core README](https://github.com/dibo-software/diboot-v2/tree/master/diboot-core "注解自动绑定多表关联"). 

 
   > .
     

## 二、 diboot-shiro*: 基于RBAC+Shiro的权限认证模块
RBAC的角色权限+基于Shiro的细粒度权限控制

### diboot-shiro 权限基础模块

### diboot-shiro-wx-* 微信服务号/企业号相关权限

具体请查看: [diboot-shiro README](https://github.com/dibo-software/diboot-v2/tree/master/diboot-shiro "注解自动绑定多表关联"). 


## 三、 diboot-devtools 代码生成工具
   > 比 [diboot devtools 1.x 版本](https://www.diboot.com/) 更通用更强大的代码生成工具 ...
    
...