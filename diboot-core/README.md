## diboot-core: 高效精简内核
主要实现：
1. 单表CRUD和多表关联查询的无SQL化
2. Entity/DTO自动转换为QueryWrapper（@BindQuery注解绑定字段参数的查询条件，可自动构建关联查询）
3. 提供其他常用开发场景的最佳实践封装。

### ** 一. 单表CRUD无SQL
   > 依赖Mybatis-plus实现（Mybatis-plus具备通用Mapper方案和灵活的查询构造器）
### ** 二. 多表关联查询无SQL
   > 通过@Bind*注解绑定关联，自动拆分成单表查询并绑定结果
   [（了解拆解关联查询的价值）](https://www.kancloud.cn/ddupl/sql_optimize/1141077)
#### 1. 注解自动绑定数据字典(自定义枚举)的显示值Label
~~~java
@BindDict(type="USER_STATUS", field = "status")
private String statusLabel;
~~~  
#### 2. 注解自动绑定其他表的字段
~~~java
// 支持关联条件+附加条件绑定字段
@BindField(entity=Department.class, field="name", condition="department_id=id AND parent_id>=0")
private String deptName;

// 支持通过中间表的级联关联绑定字段
@BindField(entity = Organization.class, field="name", condition="this.department_id=department.id AND department.org_id=id")
private String orgName;
~~~
#### 3. 注解自动绑定其他表实体Entity/VO
~~~java
// 支持关联条件+附加条件绑定Entity
@BindEntity(entity = Department.class, condition="department_id=id")
private Department department;

// 通过中间表的级联关联绑定Entity（支持附加条件）
@BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id AND department.deleted=0")
private Organization organization;
~~~
#### 4. 注解自动绑定其他表实体集合List<Entity>
~~~java
// 支持关联条件+附加条件绑定多个Entity
@BindEntityList(entity = Department.class, condition = "id=parent_id")
private List<Department> children;

// 通过中间表的 多对多关联 绑定Entity（支持附加条件）
@BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
private List<Role> roleList;
~~~
#### 5. 注解自动绑定其他表某字段集合List<>
~~~java
// 支持关联条件+附加条件绑定多个Entity的某字段
@BindFieldList(entity = Department.class, field="id", condition = "id=parent_id")
private List<Long> childrenIds;

// 通过中间表的 多对多关联 绑定Entity某字段（支持附加条件）
@BindEntityList(entity = Role.class, field="code", condition="this.id=user_role.user_id AND user_role.role_id=id")
private List<String> roleCodes;
~~~

### ** 三. 注解绑定关联的使用方式

#### 1. 定义你的Entity对应的Service（继承diboot的BaseService）及Mapper
> * 启用diboot-devtools，自动生成后端各层代码。
#### 2. 参照以上注解说明在VO中定义你的关联

#### 3. 使用注解绑定：
调用Binder自动绑定注解相关关联：
##### 方式1. 自动绑定关联（不需要转型）
~~~java
//List<MyUserVO> voList = ...; 
Binder.bindRelations(voList);
~~~
##### 方式2. 自动转型并绑定关联（需要转型）
~~~java
// 查询单表获取Entity集合
// List<User> entityList = userService.list(queryWrapper);
List<MyUserVO> voList = Binder.convertAndBindRelations(userList, MyUserVO.class);
~~~

### ** 四. Entity/DTO自动转换QueryWrapper自动构建Join查询
#### 1. Entity/DTO中声明映射查询条件
示例代码：
~~~java 
public class UserDTO {
    // 无@BindQuery注解默认会映射为=条件
    private Long gender;
    
    // 有注解，映射为注解指定条件
    @BindQuery(comparison = Comparison.LIKE)
    private String realname;
    
    // join其他表
    @BindQuery(comparison = Comparison.STARTSWITH, entity=Organization.class, field="name", condition="this.org_id=id")
    private String orgName;
    
    // 非null值属性需要从QueryWrapper中剔除
    @BindQuery(ignore = true)
    private int xxx = 1;
    
}
~~~
#### 2. 调用QueryBuilder.toQueryWrapper(entityOrDto)进行转换
~~~java
/**
 * url参数示例: /list?gender=M&realname=张
 * 将映射为 queryWrapper.eq("gender", "M").like("realname", "张")
 */
@GetMapping("/list")
public JsonResult getVOList(UserDto userDto) throws Exception{
    // 调用super.buildQueryWrapper(entityOrDto) 进行转换，仅转换request中有的参数值
    QueryWrapper<User> queryWrapper = super.buildQueryWrapper(userDto);
    // 或者
    // 直接调用 QueryBuilder.toQueryWrapper(entityOrDto) 转换，转换DTO中全部非空值字段
    //QueryWrapper<User> queryWrapper = QueryBuilder.buildQueryWrapper(userDto);
    
    //... 查询list
    return JsonResult.OK(list);
}
~~~

#### 3. 支持动态Join的关联查询与结果绑定
> 动态查询的调用方式有以下两种：
##### 方式1. 通过QueryBuilder链式调用
~~~java
QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(Department.class);
~~~
##### 方式2. 通过QueryBuilder构建QueryWrapper，再调用Binder或JoinsBinder
~~~java
// 构建QueryWrapper
QueryWrapper<DTO> queryWrapper = QueryBuilder.toQueryWrapper(dto);
// 调用join关联查询绑定
List<Entity> list = Binder.joinQueryList(queryWrapper, Department.class);
~~~

绑定调用将自动按需（有表的查询字段时才Join）构建类似如下动态SQL并绑定结果: 
> SELECT self.* FROM user self 
LEFT OUTER JOIN organization r1 ON self.org_id=r1.id 
WHERE (r1.name LIKE ?) AND self.is_deleted=0

### 五. 使用步骤与样例参考
#### 1. 引入依赖
Gradle:
~~~gradle
compile("com.diboot:diboot-core-spring-boot-starter:{latestVersion}")
~~~
或Maven
~~~xml
<dependency>
    <groupId>com.diboot</groupId>
    <artifactId>diboot-core-spring-boot-starter</artifactId>
    <version>{latestVersion}</version>
</dependency>
~~~

#### 2. 配置数据源
以Mysql为例：
~~~properties
#datasource config
spring.datasource.url=jdbc:mysql://localhost:3306/diboot_example?characterEncoding=utf8&serverTimezone=GMT%2B8
spring.datasource.username=diboot
spring.datasource.password=123456
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.driver-class-name=com.mysql.cj.jdbc.Driver
~~~

> * @BindDict注解需要依赖dictionary表，依赖diboot-core-spring-boot-starter，初次启动时starter会自动创建该表。

#### 3. 详细文档 - [diboot-core 官方文档](https://www.diboot.com/guide/diboot-core/%E5%AE%89%E8%A3%85.html)

#### 4. 参考样例 - [diboot-core-example](https://github.com/dibo-software/diboot-example/tree/master/diboot-core-example)
