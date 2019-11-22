# diboot-core: 全新优化内核
主要实现：
1. 单表CRUD和多表关联查询的无SQL化
2. Entity/DTO自动转换为QueryWrapper（@BindQuery注解绑定字段参数对应的查询条件，无注解默认映射为等于=条件）
3. 提供其他常用开发场景的最佳实践封装。

## ** 一. 单表CRUD无SQL
   > 依赖Mybatis-plus实现（Mybatis-plus具备通用Mapper方案和灵活的查询构造器）
## ** 二. 多表关联查询无SQL（通过注解绑定关联，自动拆分成单表查询并绑定结果）
   > 通过注解实现多数场景下的关联查询无SQL
### 1. 注解自动绑定数据字典(自定义枚举)的显示值Label
~~~java
@BindDict(type="USER_STATUS", field = "status")
private String statusLabel;
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

## ** 三. 注解绑定关联的使用方式
### 1. 引入依赖
Gradle:
~~~gradle
compile("com.diboot:diboot-core-spring-boot-starter:2.0.3-RC2")
~~~
或Maven
~~~xml
<dependency>
    <groupId>com.diboot</groupId>
    <artifactId>diboot-core-spring-boot-starter</artifactId>
    <version>2.0.3-RC2</version>
</dependency>
~~~
> * 使用diboot-devtools，会自动引入diboot-core，无需配置此依赖。
> * @BindDict注解需要依赖dictionary表，初次启动时starter会自动创建该表。

### 2. 定义你的Service（继承diboot的BaseService或Mybatis-plus的ISerivice）及Mapper

### 3. 使用注解绑定：
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

## ** 四. Entity/DTO自动转换为QueryWrapper的使用方式
### 1. Entity/DTO中声明映射查询条件
示例代码：
~~~java 
public class UserDTO{
    // 无@BindQuery注解默认会映射为=条件
    private Long gender;
    
    // 有注解，映射为注解指定条件
    @BindQuery(comparison = Comparison.LIKE)
    private String realname;
    
    //... getter, setter
}
~~~
### 2. 调用QueryBuilder.toQueryWrapper(entityOrDto)进行转换
~~~java
/**
 * url参数示例: /list?gender=M&realname=张
 * 将映射为 queryWrapper.eq("gender", "M").like("realname", "张")
 */
@GetMapping("/list")
public JsonResult getVOList(UserDto userDto) throws Exception{
    //调用super.buildQueryWrapper(entityOrDto) 或者直接调用 QueryBuilder.toQueryWrapper(entityOrDto) 进行转换
    QueryWrapper<User> queryWrapper = super.buildQueryWrapper(userDto);
    //... 查询list
    return new JsonResult(Status.OK, list);
}
~~~

## 五. 样例参考 - [diboot-core-example](https://github.com/dibo-software/diboot-v2-example/tree/master/diboot-core-example)
