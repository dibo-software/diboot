# 接口的艺术Controller

## BaseController

> BaseController是BaseCrudRestController的父类，提供请求参数处理等方法的封装。

## BaseCrudRestController

> 增删改查通用controller,以后的controller都可以继承该类，减少代码量。
接下来会对BaseCrudRestController中的一些通用方法进行介绍。

* getService 抽象方法
```java
/**
 * 获取service实例
 *
 * @return
 */
protected abstract BaseService getService();
```
该方法是用来获取当前类中相关业务Service实例。
所有继承了BaseCrudRestController的实体类都要实现该方法，如下：
* getService 方法实现
```java
@Autowired
private DictionaryService dictionaryService;

@Override
protected BaseService getService() {
    return dictionaryService;
}
```

* getEntityList 方法
```java
//方法定义
protected JsonResult getEntityList(Wrapper queryWrapper) {...}
//方法调用示例
JsonResult jsonResult = super.getEntityList(queryWrapper);
System.out.println(jsonResult.getCode()==0);
//执行结果
===> true
```
该方法用于获取数据集合,入参为查询条件（queryWrapper），
调用该方法成功后会返回所有符合查询条件的数据集合，该方法无分页功能。

* getEntityListWithPaging 方法
```java
//方法定义
protected JsonResult getEntityListWithPaging(Wrapper queryWrapper, Pagination pagination) {...}
//方法调用示例
JsonResult jsonResult = super.getEntityListWithPaging(queryWrapper,pagination);
System.out.println(jsonResult.getCode()==0);
//执行结果
===> true
```
该方法用于获取数据集合,入参为查询条件（queryWrapper）、分页条件（pagination），
调用该方法成功后会返回符合查询条件的当前页数据集合，该方法有分页功能。

* getVOListWithPaging 方法
```java
//方法定义
protected <T> JsonResult getVOListWithPaging(Wrapper queryWrapper, Pagination pagination, Class<T> clazz) {...}
//方法调用示例
JsonResult jsonResult = super.getVOListWithPaging(queryWrapper,pagination,Organization.class);
System.out.println(jsonResult.getCode()==0);
//执行结果
===> true
```
该方法用来获取数据VO集合，入参为查询条件（queryWrapper）、分页条件（pagination）、类类型（clazz），
调用该方法成功后会返回符合查询条件的当前页数据VO集合，该方法有分页功能。

* createEntity 方法
```java
//方法定义
protected JsonResult createEntity(BaseEntity entity, BindingResult result) {...}
//方法调用示例
JsonResult jsonResult = super.createEntity(entity,result);
System.out.println(jsonResult.getCode()==0);
//执行结果
===> true
```
该方法用来新建数据，入参为数据实体（entity）、绑定结果（result），调用该方法成功后会在相关表中插入一条数据。

* updateEntity 方法
```java
//方法定义
protected JsonResult updateEntity(BaseEntity entity, BindingResult result) {...}
//方法调用示例
JsonResult jsonResult = super.updateEntity(entity,result);
System.out.println(jsonResult.getCode()==0);
//执行结果
===> true
```
该方法用来更新数据，入参为数据实体（entity）、绑定结果（result），调用该方法成功后会更新相关表中的数据。

* deleteEntity 方法
```java
 //方法定义
protected JsonResult deleteEntity(Serializable id) {...}
//方法调用示例
JsonResult jsonResult = super.deleteEntity(id);
System.out.println(jsonResult.getCode()==0);
//执行结果
===> true
```
该方法用来删除数据，入参为数据ID（id），调用该方法成功后会删除相关表中的数据。

* convertToVoAndBindRelations 方法
```java
//方法定义
protected <VO> List<VO> convertToVoAndBindRelations(List entityList, Class<VO> voClass) {...}
//方法调用示例
List<OrganizationVO> orgVOList = super.convertToVoAndBindRelations(entityList, OrganizationVO.class);
System.out.println(orgVOList.size()>0);
//执行结果
===> true
```
该方法用来将数据实体集合转化为数据实体VO集合，入参为实体集合（entityList）、类类型（voClass），
调用该方法成功后返回数据实体VO集合。

* beforeCreate 方法
```java
//方法定义
protected String beforeCreate(BaseEntity entity){...}
//方法调用示例
String str = this.beforeCreate(entity);
```
该方法用来处理新建数据之前的逻辑，如数据校验等，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。

* afterCreated 方法
```java
 //方法定义
 protected String afterCreated(BaseEntity entity){...}
 //方法调用示例
 String str = this.afterCreated(entity);
```
该方法用来处理新建数据之后的逻辑，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。

* beforeUpdate 方法
```java
//方法定义
protected String beforeUpdate(BaseEntity entity){...}
//方法调用示例
String str = this.beforeUpdate(entity);
```
该方法用来处理更新数据之前的逻辑，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。

* afterUpdated 方法
```java
//方法定义
protected String afterUpdated(BaseEntity entity){...}
//方法调用示例
String str = this.afterUpdated(entity);
```
该方法用来处理更新数据之后的逻辑，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。

* beforeDelete 方法
```java
//方法定义
protected String beforeDelete(BaseEntity entity){...}
//方法调用示例
String str = this.beforeDelete(entity);
```
该方法主要用来处理删除数据之前的逻辑，如检验是否具有删除权限等，需要子类继承BaseCrudRestController时重写并实现具体处理逻辑。
