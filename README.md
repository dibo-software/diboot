<p align="center">
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="https://img.shields.io/hexpm/l/plug.svg">
    </a>
</p>

# diboot-v2
diboot 2.0版本项目，实现: diboot-core全新内核 + diboot-devtools代码生成。

### diboot-core: 优化内核
全新精简内核，主要实现单表CRUD和多表关联绑定的无SQL实现方案，并提供其他常用开发场景的简单封装。

#### 单表CRUD无SQL
   > 基于Mybatis-Plus实现（Mybatis-Plus具备通用Mapper方案和灵活的查询构造器）
#### 多表关联查询无SQL（适用于大多数场景，拆分成单表查询自动实现结果绑定）
   > 通过注解实现多数场景下的关联查询无SQL
##### 1. 注解自动绑定元数据(枚举值)的显示值Label
~~~java
@BindMetadata(type="GENDER", field = "gender")
private String genderLabel;
~~~  
##### 2. 注解自动绑定其他表的字段
~~~java
// 支持关联条件+附加条件绑定字段
@BindField(entity=Department.class, field="name", condition="department_id=id AND code IS NOT NULL")
private String deptName;

// 支持通过中间表的级联关联绑定字段
@BindField(entity = Organization.class, field="name", condition="this.department_id=department.id AND department.org_id=id")
private String orgName;
~~~
##### 3. 注解自动绑定其他表实体Entity
~~~java
// 支持关联条件+附加条件绑定Entity
@BindEntity(entity = Department.class, condition="department_id=id")
private Department department;

// 通过中间表的级联关联绑定Entity（支持附加条件）
@BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id AND department.deleted=0")
private Organization organization;
~~~
##### 4. 注解自动绑定其他表实体集合List<Entity>
~~~java
// 支持关联条件+附加条件绑定多个Entity
@BindEntityList(entity = Department.class, condition = "id=parent_id")
private List<Department> children;

// 通过中间表的 多对多关联 绑定Entity（支持附加条件）
@BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
private List<Role> roleList;
~~~

   > 本地运行example需先执行/resources/init-mysql.sql到数据库。 

### diboot-devtools 代码生成工具
提供数据库表管理功能

提供CRUD及关联绑定的相关代码生成（Entity,VO,Service,Mapper,Controller...）

### diboot-example: 示例
各组件使用示例项目

...
