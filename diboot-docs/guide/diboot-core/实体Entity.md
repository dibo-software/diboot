# 实体Entity

> diboot-core中的实体Entity是与数据库表对应的映射对象，下文简称实体。

所有实体命名约定采用将表名转换为首字母大写的驼峰命名法命名，比如**sys_user**的实体名为**SysUser**

## BaseEntity

> BaseEntity是diboot-core提供的基础实体类，提供了我们默认数据表结构的默认字段，比如id、is_deleted、create_time等。

## 数据校验

> 数据提交过程中一般需要后端进行数据格式校验，默认是用validation来做后端数据校验，字段上校验注解示例如下：

```java
@NotNull(message = "上级ID不能为空，如无请设为0")
private Long parentId;
```

## 数据库表中不存在的列

> Entity中的属性默认会自动映射为数据库列，如果某个字段在对应数据表中不存在，需要使用Mybatis-plus的 @TableField(exist = false) 注解告知Mybatis-plus忽略该字段。

```java
@TableField(exist = false)
private String ignoreMe;
```