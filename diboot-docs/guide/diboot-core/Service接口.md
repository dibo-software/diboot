# BaseService接口

## BaseService类

> 对于entity对应的service，推荐继承diboot-core中封装好的BaseService接口及BaseServiceImpl实现，以使用增强扩展。

```java
import com.diboot.core.service.BaseService;
public interface DemoService extends BaseService<Demo> {
}
```

> 提示：BaseService类具备mybatis-plus中的IService接口大多数接口，但并没有继承IService，如果需要使用IService接口，可单独继承IService类.

## 查询相关接口

### getEntity
```java
T getEntity(Serializable id);
```
> getEntity接口可以通过一个主键值得到数据库中的对应记录转换为Entity，如：
```java
Demo demo = demoService.getEntity(id);
```
### getSingleEntity
> 获取符合条件的一个Entity实体
```java
T getSingleEntity(Wrapper queryWrapper);
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
log.debug(count);
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

### getValuesOfField 
* since v2.1
> 获取指定条件的Entity ID或其他字段的集合，示例如:
```java
QueryWrapper<Dictionary> queryWrapper = new QueryWrapper<>().eq("type", "GENDER");
// 提取符合条件的id集合
List<Long> ids = dictionaryService.getValuesOfField(queryWrapper, Dictionary::getId);
```

### getMapList
> 该方法通过查询条件查询和分页参数出符合条件的Map列表，其中分页参数是可选参数，返回查询出的Map列表。
```java
List<Map<String, Object>> getMapList(Wrapper queryWrapper);
List<Map<String, Object>> getMapList(Wrapper queryWrapper, Pagination pagination);
```

### getKeyValueList, getKeyValueMap
> 该方法通过查询条件查询出查询出符合条件的 KeyValue 列表/map，该KeyValue是一个键值对，所以再查询条件中需要指定需要查询的字段。
> 注意: KeyValue对象支持第三个扩展字段ext，默认从queryWrapper构建指定select的第三个字段中取值。如queryWrapper.select(name, value, ext)
```java
List<KeyValue> getKeyValueList(Wrapper queryWrapper);
```

### getViewObject
> 通过Entity主键，查询出该主键VO实例，返回一个VO实例。
提示：如果该VO通过相应注解绑定了数据字典关联或者数据表关联，那么该方法也将查询出相对应的数据字典信息或者关联数据信息等。
```java
<VO> VO getViewObject(Serializable id, Class<VO> voClass);
```

### getViewObjectList
> 通过查询条件，分页条件，查询出符合该查询条件的当页数据列表，返回一个VO实例列表。
提示：如果该VO通过相应注解绑定了数据字典关联或数据表关联，那么该方法查询出的VO列表中，每一个VO元素也将有对应的数据字典信息或关联表信息等。
```java
<VO> List<VO> getViewObjectList(Wrapper queryWrapper, Pagination pagination, Class<VO> voClass);
```

### exists 是否存在匹配记录
```java
/**
 * 是否存在符合条件的记录
 * @param getterFn entity的getter方法
 * @param value 需要检查的值
 * @return true/false
 */
boolean exists(IGetter<T> getterFn, Object value);

/**
 * 是否存在符合条件的记录
 * @param queryWrapper
 * @return true/false
 */
boolean exists(Wrapper queryWrapper);
```

## 更新相关接口

### createEntity
```java
boolean createEntity(T entity);
```
> createEntity接口将一个entity添加到数据库中，返回成功与失败。创建成功后，会将该记录的主键自动设置到该entity中，如：

```java
boolean success = demoService.createEntity(demo);
log.debug(demo.getId());
```

### updateEntity
```java
boolean updateEntity(T entity);
boolean updateEntity(T entity, Wrapper updateCriteria);
boolean updateEntity(Wrapper updateWrapper);
```
* updateEntity接口可以根据该实体的主键值来更新整个entity的所有非空字段值到数据库，返回成功与失败，如：
```java
boolean success = demoService.updateEntity(demo);
```
* 该接口也可以通过更新条件来执行更新，具体可参考[mybatis-plus的UpdateWrapper文档](https://mybatis.plus/guide/wrapper.html#updatewrapper)。

### createOrUpdateEntity
```java
boolean createOrUpdateEntity(T entity);
```
> 该接口新建或更新一个实体记录到数据库，如果该实体主键有值，则更新，无值，则新建。如主键有值，但数据库中未找到，则报错，如：
```java
boolean success = demoService.createOrUpdateEntity(demo);
log.debug(success);
```

### createOrUpdateEntities
```java
boolean createOrUpdateEntities(Collection entityList);
```
> 批量创建或更新实体集合，如：
```java
boolean success = demoService.createOrUpdateEntities(demoList);
log.debug(success);
```

### createEntityAndRelatedEntities
* since v2.0.5
```java
/**
 * 添加entity 及 其关联子项entities
 * @param entity 主表entity
 * @param relatedEntities 从表/关联表entities
 * @param relatedEntitySetter 设置关联从表绑定id的setter，如Dictionary::setParentId
 * @return
 */
boolean createEntityAndRelatedEntities(T entity, List<RE> relatedEntities, ISetter<RE, R> relatedEntitySetter);
```
> 该接口将对一个1对多关联数据的设置关联id并批量保存，如：
```java
boolean success = dictionaryService.createEntityAndRelatedEntities(dictionary, dictionaryList, Dictionary::setParentId);
```
类似的还有1对多数据的批量更新与删除：
~~~java
boolean updateEntityAndRelatedEntities(T entity, List<RE> relatedEntities, ISetter<RE, R> relatedEntitySetter);
boolean deleteEntityAndRelatedEntities(T entity, List<RE> relatedEntities, ISetter<RE, R> relatedEntitySetter);
~~~

### deleteEntity
```java
boolean deleteEntity(Serializable id);
```
> 该接口通过主键字段对实体进行删除操作，如：
```java
boolean success = demoService.deleteEntity(demo.getId());
log.debug(success);
```

### deletedEntities
```java
boolean deleteEntities(Wrapper queryWrapper);
```
> 该接口通过查询条件对符合该查询条件的所有记录进行删除操作

