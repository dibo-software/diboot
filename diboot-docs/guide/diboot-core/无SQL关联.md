# 无SQL关联

> diboot-core支持通过注解实现数据字典关联、单数据表关联、多数据表关联等功能。

## 如何开始
> 您可以创建完一个表相对应的实体类、service接口、mapper等代码后，创建一个VO类来尝试该功能，如：
```java
package com.example.demo.vo;

import com.diboot.core.binding.annotation.BindDict;
import com.diboot.core.binding.annotation.BindEntity;
import com.diboot.core.binding.annotation.BindEntityList;
import com.diboot.core.binding.annotation.BindField;
import com.example.demo.entity.Demo;

import java.util.List;

public class DemoVO extends Demo  {
    private static final long serialVersionUID = 5476618743424368148L;

}
```

:::warning
注：@BindDict注解需要依赖dictionary表，初次启动时starter会自动安装。
:::

## 处理方式
1. 通过在VO类中添加相关字段，以及对应的关联绑定注解，来定义我们的绑定类型和需要得到的结果以及额外的条件等信息；
2. 绑定完成后，我们需要调用**Binder**类中的相关方法(*bindRelations)执行这个绑定关系，我们目前提供了两种方式可供处理：
### 自动绑定关联
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
List<MyUserVO> voList = Binder.convertAndBindRelations(userList, MyUserVO.class);
```

## 数据字典关联绑定
:::tip
当表中的字段为数据字典类型的值时，可使用数据字典关联来绑定表字段与数据字典的关联关系。
<br>
在通过diboot-core的封装后，数据字典关联时开发者不再需要写大量java代码和SQL关联查询，只要使用相关注解即可快速转换值value为标签/名称name。
:::
* 当您需要将数据字典表中的某类数据映射到实体中的对应字段时，可以使用数据字典关联方式来对此建立关联关系。
* 我们提供了**BindDict**注解来进行数据字典关联绑定。
* 使用@BindDict注解时需传两个参数，分别是type和field。
 * type表示关联的数据字典类型；
 * field表示关联字段。
* 示例如下：
```java
@BindDict(type="USER_STATUS", field = "status")
private String statusLabel;
```

## 数据表关联绑定
* 数据表关联按照关联表的方式上可分为**单数据表直接关联**与**中间表关联**这两种，中间表关联是一种通过中间表进行多对多的关联处理方案。
* 按照得到结果的形式可分为**绑定关联表中对应字段**和**绑定关联表实体**这两种关联方式，前者得到关联表中的目标字段，后者得到关联表的整个实体。
* **绑定关联表实体**即支持绑定单个实体，也支持绑定实体列表。
* 支持对关联查询添加附加条件。
> 直接关联与中间表关联只是传入的参数不同，而绑定表字段与绑定表实体是使用的不同注解。


### 单表关联
:::tip
当两张表数据之间有直接的依赖关系时，如主外键关联关系，即可使用单表关联。
<br>
在通过diboot-core的封装后，
单表关联时开发者不再需要写大量java代码和SQL查询，只要使用相关注解即可快速绑定它们之间的关联关系。
<br>
同时关联的实现是拆解关联查询为单表查询，可以更加高效利用数据库缓存和索引，提高查询效率。
:::

### 多表关联
:::tip
当两张表数据之间是通过第三张表来产生依赖关系时，即可使用多表关联。
<br>
在通过diboot-core的封装后，多表关联时开发者不再需要写大量java代码和SQL查询，只要使用相关注解即可快速绑定它们之间的关联关系。
<br>
同时关联的实现是拆解关联查询为单表查询，可以更加高效利用数据库缓存和索引，提高查询效率。
:::

### 绑定目标字段
> 绑定字段使用**@BindField**注解进行处理，将得到关联表的目标字段的值。

* 使用@BindField注解时需传三个参数，分别是entity、field、condition。
    * entity表示关联实体；
    * field表示关联表字段名；
    * condition表示关联条件。

* 单数据表直接关联 **（单表关联）** 获取目标字段值，用来将数据表中的某个字段映射到实体的对应字段，注解及参数示例如下：
```java
@BindField(entity=Department.class, field="name", condition="department_id=id AND parent_id>=0")
private String deptName;
```

* 通过中间表进行多表关联的级联关联 **（多表关联）** 绑定字段
```java
@BindField(entity = Organization.class, field="name", condition="this.department_id=department.id AND department.org_id=id")
private String orgName;
```

### 绑定单个实体
> 绑定单个实体使用**@BindEntity**注解进行处理，将得到关联表对应的单个实体。
> 如果属性类型为VO等非Entity对象类型，将自动转换为您指定的类型再绑定。
* 使用@BindEntity注解时需传两个参数，分别是entity和condition。
    * entity表示关联实体类；
    * condition表示关联条件。
* 单数据表直接关联 **（单表关联）** 获取目标单个实体，注解及参数示例如下：
```java
@BindEntity(entity = Department.class, condition="department_id=id")
private Department department;
```
* 通过中间表进行多表关联的级联关联 **（多表关联）** 绑定对应单个实体，示例如下：
```java
@BindEntity(entity = Organization.class, condition = "this.department_id=department.id AND department.org_id=id")
private Organization organization;
```

### 绑定实体列表
> 绑定实体列表使用**@BindEntityList**注解进行处理，将得到关联表对应的实体列表。
> 如果待绑定List中的泛型参数类型为VO等非Entity对象，将自动转换为您指定的类型再绑定。
* 使用@BindEntityList注解时需传两个参数，分别是entity和condition。
    * entity表示关联实体类；
    * condition表示关联条件。
* 单数据表直接关联 **（单表关联）** 获取目标的实体列表，注解及参数示例如下：
```java
// 关联其他表
@BindEntity(entity = Department.class, condition="department_id=id")
private List<Department> departmentList;

// 关联自身，实现无限极分类等
@BindEntityList(entity = Department.class, condition = "id=parent_id")
private List<Department> children;
```
* 通过中间表进行多表关联的级联关联 **（多表关联）** 绑定对应的实体列表，示例如下：
```java
@BindEntityList(entity = Role.class, condition="this.id=user_role.user_id AND user_role.role_id=id")
private List<Role> roleList;
```

