# 查询条件DTO

> 用户可自定义相关实体的DTO类，并通过注解，自动转换为QueryWrapper并进行查询。

#### 1. Entity/DTO中声明映射查询条件
示例代码：
~~~java 
public class UserDTO {
    // 无@BindQuery注解默认会映射为=条件
    private Long gender;
    
    // 有注解，映射为注解指定条件
    @BindQuery(comparison = Comparison.LIKE)
    private String realname;
    
    // join其他表（跨表查询字段）
    @BindQuery(comparison = Comparison.STARTSWITH, entity=Organization.class, field="name", condition="this.org_id=id")
    private String orgName;
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
    //调用super.buildQueryWrapper(entityOrDto) 进行转换
    QueryWrapper<User> queryWrapper = super.buildQueryWrapper(userDto);
    // 或者直接调用 QueryBuilder.toQueryWrapper(entityOrDto) 转换
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

无跨表字段的查询: 
> SELECT self.* FROM user self WHERE self.gender=? AND self.is_deleted=0

有跨表字段的查询:
> SELECT self.* FROM user self 
LEFT OUTER JOIN organization r1 ON self.org_id=r1.id 
WHERE self.gender=? AND (r1.name LIKE ?) AND self.is_deleted=0