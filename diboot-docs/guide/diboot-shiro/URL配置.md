# URL配置

> diboot-shiro URL配置可以让访问变得更加灵活

当前提供匿名访问url配置 和错误url配置

### 缓存配置
```properties
#忽略认证的url
diboot.shiro.configuration.ignore-auth-urls=/public/**，/public2
#错误跳转的url: 默认不配置使用 /error
diboot.shiro.configuration.error-url=/error
```

::: tip
组件中已经设置了部分必要忽略认证的url，详细可以查看com.diboot.shiro.starter.ShiroAutoConfiguration配置
:::