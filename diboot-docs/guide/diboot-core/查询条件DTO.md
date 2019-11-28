# 查询条件DTO

> 用户可自定义相关实体的DTO类，将其在Controller类的相关方法中，自动转换为QueryWrapper进行条件查询时使用：

## 创建DTO
```java
public class UserDTO{
    // 无@BindQuery注解默认会映射为=条件
    private Long gender;
    
    // 有注解，映射为注解指定条件
    @BindQuery(comparison = Comparison.LIKE)
    private String realname;
    
    //... getter, setter方法等
}
```

## 自动转换
> Controller类中对相关路由的查询条件可进行如下绑定：
```java
/**
 * url参数示例: /list?gender=M&realname=张
 * 将映射为 queryWrapper.eq("gender", "M").like("realname", "张")
 */
@GetMapping("/list")
public JsonResult getVOList(UserDto userDto, HttpServletRequest request) throws Exception{
    //调用super.buildQueryWrapper(entityOrDto, request) 或者直接调用 QueryBuilder.toQueryWrapper(entityOrDto) 进行转换
    QueryWrapper<User> queryWrapper = super.buildQueryWrapper(userDto, request);
    //... 查询list
    return new JsonResult(Status.OK, list);
}
```