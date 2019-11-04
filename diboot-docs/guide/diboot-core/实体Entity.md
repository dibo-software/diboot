# 实体Entity

> diboot-core中的实体Entity是与数据库表对应的映射对象，下文简称实体。

所有实体统一存放在entity包名下，命名采用将表名转换为首字母大写的驼峰命名法命名，比如**sys_user**的实体名为**SysUser。**

## BaseEntity

> BaseEntity是diboot-core提供的基础实体类，提供了我们默认数据表结构的默认字段，比如id、is_deleted、create_time等，默认的方法如toMap等。

## BaseExtEntity

> BaseExtEntity是基于BaseEntity的扩展实体类，对数据表结构的扩展字段extdata添加了相关处理方法，extdata将以json字符串形式存储在数据库中。

* getFromExt方法

```java
/***
* 从extdata JSON中提取扩展属性值
* @param extAttrName
* @return
*/
public Object getFromExt(String extAttrName){
  if(this.extdataMap == null){
  	return null;
  }
  return this.extdataMap.get(extAttrName);
}

```

该方法传入一个属性名称，返回该属性值

* addIntoExt

```java
/***
* 添加扩展属性和值到extdata JSON中
* @param extAttrName
* @param extAttrValue
*/
public void addIntoExt(String extAttrName, Object extAttrValue){
  if(extAttrName == null && extAttrValue == null){
  	return;
  }
  if(this.extdataMap == null){
  	this.extdataMap = new LinkedHashMap<>();
  }
  this.extdataMap.put(extAttrName, extAttrValue);
}
```

该方法传入一个属性名与属性值，即可将该值设置到extdata的json字符串中。

## 数据校验

> 我们在数据提交过程中可能会需要后端进行数据格式校验，我们默认是用validation来做后端数据校验，我们可以在实体中需要校验的字段上添加校验相关的注解，如下：

```java
@NotNull(message = "上级ID不能为空，如无请设为0")
private Long parentId;
```

## @TableField注解

> 如果mapper也继承我们core的BaseMapper来处理，那么实体中的所有字段都被认为在相对应的数据表中是存在的，如果某个字段在对应数据表中不存在，则会执行SQL时报错。

所以，如果某个字段在相对应的数据表中不存在，而我们又需要使用到该字段，那么可以添加@Tablefield注解，并且设置@TableField(exist = false)中的exist参数为false即可，如下：

```java
@TableField(exist = false)
private List<Long> userIdList;
```