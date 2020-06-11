# 无SQL跨表查询

> 用户可自定义相关实体的查询DTO类，添加相应的BindQuery注解，diboot将自动构建QueryWrapper并查询。
> 对于字段条件跨表的查询将自动按需构建LEFT JOIN（无该字段条件时不JOIN），让无需手写SQL覆盖大多数场景。

## 使用方式
### 1. Entity/DTO中定义查询方式
示例代码：
~~~java 
public class UserDTO {
    // 无@BindQuery注解默认会映射为=条件；主表的相等条件无需加注解
    private Long gender;
    
    // 有注解，映射为注解指定条件
    @BindQuery(comparison = Comparison.LIKE)
    private String realname;
    
    // join其他表（跨表查询字段）
    @BindQuery(comparison = Comparison.STARTSWITH, entity=Organization.class, field="name", condition="this.org_id=id")
    private String orgName;
}
~~~
### 2. 调用QueryBuilder自动构建QueryWrapper
> 构建方式有：
##### 方式1. controller中调用super.buildQueryWrapper(entityOrDto) 进行构建
~~~java
    QueryWrapper<User> queryWrapper = super.buildQueryWrapper(userDto);
~~~
> 该方式基于url非空参数取值构建：
> url参数示例: /list?gender=M&realname=张
> 将构建为 queryWrapper.eq("gender", "M").like("realname", "张")

##### 方式2. 直接调用 QueryBuilder.toQueryWrapper(entityOrDto) 进行构建
~~~java
    QueryWrapper<User> queryWrapper = super.buildQueryWrapper(userDto);
~~~
> 该方式基于dto对象非空值字段构建

##### 方式3. 明确构建为QueryBuilder.toDynamicJoinQueryWrapper 进行构建
~~~java
    QueryBuilder.toDynamicJoinQueryWrapper(dto).xxx
~~~
> 该方式支持链式追加查询调用


### 3. 支持动态Join的跨表查询与结果绑定
> 动态查询的调用方式有以下两种：
##### 方式1. 通过调用BaseService.getEntityList接口
~~~java
// 调用join关联查询绑定
List<Entity> list = xxService.getEntityList(queryWrapper);
~~~
#### 方式2. 通过Binder调用joinQueryList查询
~~~java
// 调用join关联查询绑定
List<Entity> list = Binder.joinQueryList(queryWrapper, Department.class);
~~~
##### 方式3. 通过DynamicJoinQueryWrapper链式调用查询
~~~java
QueryBuilder.toDynamicJoinQueryWrapper(dto).queryList(Department.class);
~~~


**绑定调用将自动按需（有从表的查询字段时才Join）构建类似如下动态SQL并绑定结果:**

* 无跨表字段的查询: 
> SELECT self.* FROM user self WHERE self.gender=? AND self.is_deleted=0

* 有跨表字段的查询:
> SELECT self.* FROM user self 
LEFT OUTER JOIN organization r1 ON self.org_id=r1.id AND r1.is_deleted=0
WHERE self.gender=? AND (r1.name LIKE ?) AND self.is_deleted=0

基于最佳性能最少Join的原则，无SQL跨表查询仅查询主表数据。如果需要其他关联绑定对象，可调用Binder实现。

### 4. 数据权限控制
> 某些场景下搜索查询需要绑定一些强制条件，用于数据权限控制，如只能查询本部门的数据。
##### 1. 在需要控制的字段上添加@DataAccessCheckpoint注解，指定CheckpointType。
示例代码：
~~~java
// 数据权限检查点
@DataAccessCheckpoint(type = CheckpointType.ORG)
private Long orgId;
~~~
##### 2. 实现DataAccessCheckInterface接口，返回对应CheckpointType的合法ID集合
~~~java
public class DataAccessCheckImpl implements DataAccessCheckInterface {
    @Override
    public List<Long> getAccessibleIds(CheckpointType type) {
        // 返回对应检查点的合法ID
    }
~~~
通过QueryBuilder构建时，将自动追加IN(合法ID)条件。具体可参考: diboot-IAM组件。
