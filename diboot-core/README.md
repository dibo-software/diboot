# diboot-core: 全新优化内核
主要实现：
1. 多表关联的自动绑定, 实现单表CRUD和多表关联的无SQL化
2. 提供其他常用开发场景的最佳实践封装。

## ** 一. 单表CRUD无SQL
   > 依赖Mybatis-Plus实现（Mybatis-Plus具备通用Mapper方案和灵活的查询构造器）
## ** 二. 多表关联查询无SQL（适用于大多数场景，拆分成单表查询自动实现结果绑定）
   > 通过注解实现多数场景下的关联查询无SQL
### 1. 注解自动绑定数据字典(枚举值)的显示值Label
~~~java
@BindDict(type="GENDER", field = "gender")
private String genderLabel;
~~~  
### 2. 注解自动绑定其他表的字段
~~~java
// 支持关联条件+附加条件绑定字段
@BindField(entity=Department.class, field="name", condition="department_id=id AND parent_id>=0")
private String deptName;

// 支持通过中间表的级联关联绑定字段
@BindField(entity = Organization.class, field="name", condition="this.department_id=department.id AND department.org_id=id")
private String orgName;
~~~
### 3. 注解自动绑定其他表实体Entity
~~~java
// 支持关联条件+附加条件绑定Entity
@BindEntity(entity = Department.class, condition="department_id=id")
private Department department;

// 通过中间表的级联关联绑定Entity（支持附加条件）
@BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id AND department.deleted=0")
private Organization organization;
~~~
### 4. 注解自动绑定其他表实体集合List<Entity>
~~~java
// 支持关联条件+附加条件绑定多个Entity
@BindEntityList(entity = Department.class, condition = "id=parent_id")
private List<Department> children;

// 通过中间表的 多对多关联 绑定Entity（支持附加条件）
@BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
private List<Role> roleList;
~~~

## ** 三. 使用方式
### 引入依赖
Gradle:
~~~gradle
compile("com.diboot:diboot-core:2.0.1")
~~~
或Maven
~~~xml
<dependency>
    <groupId>com.diboot</groupId>
    <artifactId>diboot-core</artifactId>
    <version>2.0.1</version>
</dependency>
~~~
### 定义你的Service（继承diboot的BaseService或Mybatis-plus的ISerivice）及Mapper

### 使用注解绑定：
调用RelationsBinder自动绑定注解相关关联：
#### 方式1. 自动绑定关联（不需要转型）
~~~java
//List<MyUserVO> voList = ...; 
RelationsBinder.bind(voList);
~~~
#### 方式2. 自动转型并绑定关联（需要转型）
~~~java
// 查询单表获取Entity集合
// List<User> entityList = userService.list(queryWrapper);
List<MyUserVO> voList = RelationsBinder.convertAndBind(userList, MyUserVO.class);
~~~

## 使用样例请参考 - [diboot-core-example](https://github.com/dibo-software/diboot-v2-example/tree/master/diboot-core-example)
