# Controller接口

## BaseController

> BaseController是BaseCrudRestController的父类，提供请求参数处理等方法的封装。

## BaseCrudRestController

> 增删改查通用controller, Entity对应的controller都可以继承该类，减少代码量。通用方法: 

* protected BaseService getService() 方法
该方法是用来获取当前Entity中相关的Service实例。

* getViewObject 获取详情页VO
```java
//方法定义
JsonResult getViewObject(Serializable id, Class<VO> voClass) 
//子类调用示例
JsonResult jsonResult = super.getViewObject(id, UserDetailVO.class);
```
该方法用于获取单个对象的详情VO视图对象。

* getVOListWithPaging 获取列表页VO（带分页）
```java
//方法定义
protected <T> JsonResult getVOListWithPaging(Wrapper queryWrapper, Pagination pagination, Class<T> clazz) {...}
//调用示例
JsonResult jsonResult = super.getVOListWithPaging(queryWrapper, pagination, UserListVO.class);
```
列表页查询与分页的url参数示例: /${bindURL}?pageSize=20&pageIndex=1&orderBy=itemValue&type=GENDAR
orderBy排序: 格式为“”“”"字段:排序方式"，如"id:DESC"，多个以,分隔

* getEntityList(queryWrapper)，getEntityListWithPaging(queryWrapper, pagination)
返回entity对象的集合，供子类需要时调用

* createEntity 新建保存Entity
```java
//方法定义
protected JsonResult createEntity(E entity)
//方法调用示例
JsonResult jsonResult = super.createEntity(entity);
```

* updateEntity 根据ID更新Entity
```java
//方法定义
protected JsonResult updateEntity(Serializable id, E entity) 
//方法调用示例
JsonResult jsonResult = super.updateEntity(id, entity);
```

* deleteEntity 根据ID删除Entity
```java
 //方法定义
protected JsonResult deleteEntity(Serializable id) {...}
//方法调用示例
JsonResult jsonResult = super.deleteEntity(id);
```

* batchDeleteEntities 批量删除Entities
```java
 //方法定义
protected JsonResult batchDeleteEntities(Collection<? extends Serializable> ids) 
//方法调用示例
JsonResult jsonResult = super.batchDeleteEntities(ids);
```

* beforeCreate 方法
```java
//方法定义
protected String beforeCreate(BaseEntity entity){...}
//方法调用示例
String str = this.beforeCreate(entity);
```
该方法用来处理新建数据之前的检查/预处理逻辑，如数据校验等，供子类重写实现。

* afterCreated 方法
```java
 //方法定义
 protected String afterCreated(BaseEntity entity){...}
 //方法调用示例
 String str = this.afterCreated(entity);
```
该方法用来处理新建数据之后的逻辑，如日志记录等，供子类重写实现。
注意：该接口在create entity创建完成之后调用，请勿用于事务性处理。

* beforeUpdate 方法
```java
//方法定义
protected String beforeUpdate(BaseEntity entity){...}
//方法调用示例
String str = this.beforeUpdate(entity);
```
该方法用来处理更新数据之前的逻辑，供子类重写实现。

* afterUpdated 方法
```java
//方法定义
protected String afterUpdated(BaseEntity entity){...}
//方法调用示例
String str = this.afterUpdated(entity);
```
该方法用来处理更新数据之后的逻辑，供子类重写实现。
注意：该接口在create entity创建完成之后调用，请勿用于事务性处理。

* beforeDelete 方法
```java
//方法定义
protected String beforeDelete(BaseEntity entity){...}
//方法调用示例
String str = this.beforeDelete(entity);
```
该方法主要用来处理删除数据之前的逻辑，如检验是否具有删除权限等，供子类重写实现。

## 数据校验

> 默认使用**hibernate-validator**进行后端数据校验。进行数据校验至少需要两步操作，在entity中设置每个字段的校验规则，以及在controller中对实体添加@Valid注解。

* 在entity中对字段进行校验规则的设置

```java
@NotNull(message = "名称不能为空")
@Length(max=100, message="名称长度应小于100")
@TableField()
private String name;
```

* 在controller中添加@Valid注解

```java
@PostMapping("/")
public JsonResult createEntity(@Valid Demo entity, BindingResult result)
        throws Exception{
    return super.createEntity(entity, result);
}
```

* 如果您使用**json格式**进行数据提交，那么可以在@RequestBody注解前添加@Valid注解，如下：
```java
public JsonResult createEntity(@Valid @RequestBody Demo entity, BindingResult result)
        throws Exception{
    return super.createEntity(entity, result);
}
```

## 统一异常处理
异常处理全局实现类为DefaultExceptionHandler，继承自该类并添加@ControllerAdvice注解即可自动支持: 
* 兼容JSON请求和Html页面请求的Exception异常处理
* Entity绑定校验的统一处理（BindException.class, MethodArgumentNotValidException.class）

* 示例代码
~~~java
@ControllerAdvice
public class GeneralExceptionHandler extends DefaultExceptionHandler{
}
~~~

## JsonResult 格式
```json
{
    code: 0,
    msg: "OK",
    data: {     
    }
}
```
调用方式
```java
JsonResult okResult = JsonResult.OK();
JsonResult failResult = JsonResult.FAIL_VALIDATION("xxx验证错误");
```
Status状态码定义:
```java
//请求处理成功
OK(0, "操作成功"),

// 部分成功（一般用于批量处理场景，只处理筛选后的合法数据）
WARN_PARTIAL_SUCCESS(1001, "部分成功"),

//有潜在的性能问题
WARN_PERFORMANCE_ISSUE(1002, "潜在的性能问题"),

// 传入参数不对
FAIL_INVALID_PARAM(4000, "请求参数不匹配"),

// Token无效或已过期
FAIL_INVALID_TOKEN(4001, "Token无效或已过期"),

// 没有权限执行该操作
FAIL_NO_PERMISSION(4003, "无权执行该操作"),

// 请求资源不存在
FAIL_NOT_FOUND(4004, "请求资源不存在"),

// 数据校验不通过
FAIL_VALIDATION(4005, "数据校验不通过"),

// 操作执行失败
FAIL_OPERATION(4006, "操作执行失败"),

// 后台异常
FAIL_EXCEPTION(5000, "系统异常"),

// 缓存清空
MEMORY_EMPTY_LOST(9999, "缓存清空");
```