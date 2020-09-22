# 无SQL关联绑定

> diboot-core支持通过注解实现数据字典关联、从表Entity/VO的(1-1)及其集合(1-n)关联，从表字段(1-1)及字段集合(1-n)关联等实现。
> 通过重构查询方式 (拆解关联查询,程序中Join) ，简化开发、提高性能

## 如何开始
> 您可以使用diboot-devtools一键生成表的各层代码及关联绑定。
> 如需初次体验绑定功能，需要准备表相对应的Entity实体类(推荐继承自diboot的BaseEntity)、service接口及实现(继承自diboot的BaseService或mybatis-plus的IService)、mapper(继承自diboot的BaseCrudMapper或mybatis-plus的BaseMapper)等，
完成之后可以创建一个VO类来体验绑定功能，如：
```java
import com.diboot.core.binding.annotation.*;
import com.example.demo.entity.Demo;

public class DemoVO extends Demo  {
    private static final long serialVersionUID = 5476618743424368148L;
    // @BindXX 注解
}
```

:::warning
注：@BindDict注解需要依赖dictionary表，初次启动时diboot-core-starter会自动安装。 如果只依赖diboot-core，可以手动创建该表以使用字典绑定功能。
注: 推荐实践：Bind注解请写在独立的类中（如VO），不要跟Entity类混合在一起，难以维护。
:::

## 绑定调用方式
1. 通过在VO类中添加相关字段，以及对应的关联绑定注解，来定义我们的绑定类型和需要得到的结果以及额外的条件等信息；
2. 注解添加后，调用**Binder**类中的相关方法(*bindRelations)执行这个绑定关系，目前提供了两种方式适配不同场景：
### * 自动绑定关联
> 该关联会自动将相关信息查询并设置到voList中，适用于对已有的voList做处理，如：
```java
//List<MyUserVO> voList = ...; 
Binder.bindRelations(voList);
```
### 自动转型并绑定关联
> 该关联会自动将vo所继承的父类的实体列表进行绑定并自动转型为voList，适用于对于非voList的实体列表等做处理，如：
```java
// 查询单表获取Entity集合
// List<User> entityList = userService.list(queryWrapper);
// 转型并绑定关联
List<MyUserVO> voList = Binder.convertAndBindRelations(userList, MyUserVO.class);
```
### * 通过BaseService接口实现绑定
```java
// 绑定单个VO对象
service.getViewObject()
// 绑定多个VO集合
service.getViewObjectList()
```
## * 数据字典关联绑定
::: tip
当表中的字段为数据字典类型的值时，可使用数据字典关联来绑定表字段与数据字典的关联关系。
<br>
通过@BindDict注解，数据字典关联时无需写大量java代码和SQL关联查询，即可快速转换值字典value值为label。
:::
* 使用@BindDict注解时需传两个参数，分别是type和field。
 * type表示关联的数据字典类型；
 * field表示关联字段。
* 示例如下：
```java
@BindDict(type="USER_STATUS", field = "status")
private String statusLabel;
```

## * 数据表关联绑定
* 数据表关联按照关联表的方式上可分为**单数据表直接关联**与**中间表关联**这两种，中间表关联是一种通过中间表进行“"1-1"或"多-多"的关联处理方案。
* 按照得到结果的形式可分为**绑定关联表中对应字段（集合）**和**绑定关联表实体（集合）** 这两种关联方式，前者得到关联表中的目标字段(集合)，后者得到关联表的整个实体(集合)。
* 支持对关联查询添加附加条件。
* diboot关联的实现是拆解关联查询为单表查询，可以更高效利用数据库缓存和索引，降低死锁概率，提高性能。

### * 绑定从表Entity实体
> 绑定单个实体(或实体对应的VO)使用**@BindEntity**注解进行处理，将得到关联表对应的单个实体。
* 使用@BindEntity注解时需指定两个参数：entity和condition。
    * entity 表示关联实体类；
    * condition 表示关联条件（当前对象表中的字段加“'this.'前缀或写在比较条件的左侧，被绑定Entity表的字段在右侧）。
    * deepBind 是否深度绑定，默认false。当绑定Entity类为VO类(还有Bind注解)，开启deepBind=true，可以实现对被绑定VO的注解的再次绑定。
* 主表1-1直接关联从表，获取从表Entity，注解示例如下：
```java
@BindEntity(entity = Department.class, condition="this.department_id=id")
private Department department;
```
* 主表1-1通过中间表间接关联从表，获取从表Entity，注解示例如下(condition不同)：
```java
@BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id")
private Organization organization;
```

### * 绑定从表Entity实体列表
> 绑定实体(或实体对应的VO)列表使用**@BindEntityList**注解进行处理，将得到关联表对应的实体列表。
* @BindEntityList多数注解参数与@BindEntity相同。
  * 新增参数: orderBy 对绑定结果集合的排序标识，用法同列表页查询的orderBy排序，具体格式为'列名:排序方式,列名:排序方式'，示例如下：
    * orderBy="sort_id" //按sort_id升序（默认）
    * orderBy="sort_id:ASC" //按sort_id升序
    * orderBy="sort_id:DESC" //按sort_id降序
    * orderBy="type:ASC,id:DESC" //先按type升序，type相同按id降序
    
* 主表1-n直接关联从表，获取从表的Entity列表，注解示例如下：
```java
// 关联其他表
@BindEntityList(entity = Department.class, condition="department_id=id")
private List<Department> departmentList;

// 关联自身，实现加载子级
@BindEntityList(entity = Department.class, condition = "id=parent_id")
private List<Department> children;
```
* 主表1-n通过中间表关联从表，绑定从表的Entity列表，结果按code升序排序。示例如下：
```java
@BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id", orderBy="code")
private List<Role> roleList;
```

### * 绑定从表字段
> 绑定字段使用**@BindField**注解进行处理，将得到关联表的目标字段的值（集合）。
* @BindField 或 @BindFieldList 注解需三个参数，分别是entity、field、condition。
    * entity表示关联实体；
    * field表示关联表字段名；
    * condition表示关联条件，用法同BindEntity*。

* 主表1-1直接关联从表，获取从表字段值，注解示例如下：
```java
@BindField(entity=Department.class, field="name", condition="department_id=id AND parent_id>=0")
private String deptName;
```

* 主表1-1通过中间表关联从表的级联关联，注解示例如下：
```java
@BindField(entity = Organization.class, field="name", condition="this.department_id=department.id AND department.org_id=id")
private String orgName;
```

### * 绑定从表字段列表
* since v2.1.1
> 绑定实体列表使用**@BindFieldList**注解进行处理，将得到关联表对应的实体列表。
* @BindFieldList注解参数同@BindField。
* 主表1-n直接关联从表字段集合，注解示例如下：
```java
// 关联其他表
@BindFieldList(entity = Department.class, field="id", condition="department_id=id")
private List<Long> departmentIds;

```
* 主表1-n通过中间表关联从表，绑定从表的Entity某字段的列表，示例如下：
```java
@BindFieldList(entity = Role.class, field="code", condition="this.id=user_role.user_id AND user_role.role_id=id")
private List<String> roleCodes;
```

### * 复杂绑定
* since v2.1.2
> 绑定操作中的条件等同于SQL中的JOIN ON，diboot可以支持类似如下的复杂场景：
* 支持多列的关联绑定
```java
// 示例：通过 utype+uid 关联中间表的 user_type+user_id ，绑定目标对象Role
@BindEntityList(entity = Role.class, condition = "this.utype=user_role.user_type AND this.uid=user_role.user_id AND user_role.role_id=id")
private List<Role> roles;
```
