# Service与实现

## Service类

> 对于一个自定义的entity，您可以像以往的习惯一样开发service相关代码，如果需要使用diboot-core中封装好的一些接口，需要继承diboot-core中的BaSeService类，并传入对应的实体类。

```java
package com.example.demo.service;

import com.diboot.core.service.BaseService;
import com.example.demo.entity.Demo;

public interface DemoService extends BaseService<Demo> {

}
```

> 提示：BaseService类并没有继承mybatis-plus中的IService接口，如果需要使用mybatis-plus中的IService接口，需要单独继承IService类.

## 相关接口

### getEntity
```java
T getEntity(Serializable id);
```
> getEntity接口可以通过一个主键参数得到数据库中的一个实体，如：
```java
Demo demo = demoService.getEntity(id);
```

### createEntity
```java
boolean createEntity(T entity);
```
> createEntity接口将一个entity添加到数据库中，返回成功与失败。通过该接口创建记录成功后，会将新建记录的主键自动设置到该entity中，如：

```java
boolean success = demoService.createEntity(demo);
System.out.println(demo.getId());
// 输出结果
===> 1001
```

### updateEntity
```java
boolean updateEntity(T entity);
boolean updateEntity(T entity, Wrapper updateCriteria);
boolean updateEntity(Wrapper updateWrapper);
```
* updateEntity接口可以根据该实体的主键值来更新整个entity的所有字段内容到数据库，返回成功与失败，如：
```java
boolean success = demoService.updateEntity(demo);
```

* 该接口也可以通过条件更新对应的字段（可以通过条件设置需要更新的字段，以及需要更新记录的条件限制），返回成功与失败，如：
```java
/*** 将demo中所有可用的记录的name都更新为“张三” */
Demo demo = new Demo();
demo.setName("张三");
// 设置更新条件
LambdaQueryWrapper<Demo> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Demo::isDeleted, false);
// 执行更新操作
boolean success = demoService.updateEntity(demo, wrapper);
System.out.println(success);

// 输出
===> true
```

* 该接口也可以通过更新条件来执行更新，返回成功与失败，对于更新条件可以参考[mybatis-plus的UpdateWrapper文档](https://mybatis.plus/guide/wrapper.html#updatewrapper)。

### createOrUpdateEntity
```java
boolean createOrUpdateEntity(T entity);
```
> 该接口新建或更新一个实体到数据库，如果该实体主键有值，则更新到数据库，若主键无值，则新建一条记录到数据库，如果主键有值，但数据库中未找到，则报错，如：
```java
boolean success = demoService.createOrUpdateEntity(demo);
System.out.println(success);

// 输出
===> true
```

### createOrUpdateEntities
```java
boolean createOrUpdateEntities(Collection entityList);
```
> 该接口将对一个Collection类型的列表中的每一个实体进行新建或更新到数据库，如：
```java
boolean success = demoService.createOrUpdateEntities(demoList);
System.out.println(success);

// 输出
===> true
```

### deleteEntity
```java
boolean deleteEntity(Serializable id);
```
> 该接口通过主键字段对实体进行删除操作，如：
```java
boolean success = demoService.deleteEntity(demo.getId());
System.out.println(success);

// 输出
===> true
```

### deletedEntities
```java
boolean deleteEntities(Wrapper queryWrapper);
```
> 该接口通过查询条件对符合该查询条件的所有记录进行删除操作，如：
```java
/*** 删除所有名称为“张三”的记录 **/
// 设置查询条件
LambdaQueryWrapper<Demo> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Demo::getName, "张三");
// 执行删除操作
boolean success = demoService.deleteEntities(wrapper);
System.out.println(success);

// 输出
===> true
```

### getEntityListCount
```java
int getEntityListCount(Wrapper queryWrapper);
```
> 该方法将查询到符合条件的所有记录总条数，返回int类型的结果，如：
```java
/*** 查询名称为“张三”的记录总条数 **/
LambdaQueryWrapper<Demo> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Demo::getName, "张三");
// 获取总条数
int count = demoService.getEntityListCount(wrapper);
System.out.println(count);

// 输出
===> true
```

### getEntityList(Wrapper queryWrapper)
* 该方法查询符合条件的所有实体列表，返回查询出的实体列表。
```java
List<T> getEntityList(Wrapper queryWrapper);
```

* 该方法通过查询条件和分页参数查询出当前页的记录列表，返回查询出的实体列表。
```java
List<T> getEntityList(Wrapper queryWrapper, Pagination pagination);
```

### getEntityListLimit
> 该方法查询符合条件的指定数量的实体列表，返回查询出的实体列表。
```java
List<T> getEntityListLimit(Wrapper queryWrapper, int limitCount);
```

### getEntityListByIds
> 该方法通过主键列表，查询出该主键列表的所有实体列表，返回查询出的实体列表。
```java
List<T> getEntityListByIds(List ids);
```

### getMapList
> 该方法通过查询条件查询和分页参数出符合条件的Map列表，其中分页参数是可选参数，返回查询出的Map列表。
```java
List<Map<String, Object>> getMapList(Wrapper queryWrapper);
List<Map<String, Object>> getMapList(Wrapper queryWrapper, Pagination pagination);
```

### getKeyValueList
> 该方法通过查询条件查询出查询出符合条件的 KeyValue 列表，该KeyValue是一个键值对，所以再查询条件中需要指定需要查询的字段。
```java
List<KeyValue> getKeyValueList(Wrapper queryWrapper);
```

### getViewObject
> 该方法通过主键，查询出该主键VO实例，返回一个VO实例。
提示：如果该VO通过相应注解绑定了数据字典关联或者数据表关联，那么该方法也将查询出相对应的数据字典信息或者关联数据信息等。
```java
<VO> VO getViewObject(Serializable id, Class<VO> voClass);
```

### getViewObjectList
> 该方法通过查询条件，分页条件，查询出符合该查询条件的当页数据列表，返回一个VO实例列表。
提示：如果该VO通过相应注解绑定了数据字典关联或数据表关联，那么该方法查询出的VO列表中，每一个VO元素也将有对应的数据字典信息或关联表信息等。
```java
<VO> List<VO> getViewObjectList(Wrapper queryWrapper, Pagination pagination, Class<VO> voClass);
```
### bindingFieldTo

```java
FieldBinder<T> bindingFieldTo(List voList);
EntityBinder<T> bindingEntityTo(List voList);
```

### bindingEntityListTo
```java
EntityListBinder<T> bindingEntityListTo(List voList);
```