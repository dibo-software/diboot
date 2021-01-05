# diboot-IAM: 身份认证组件 (基础版)
> 该组件配套的前端基础框架有antd、element-ui，前端代码参考: diboot-antd-admin 及 diboot-element-admin
## 组件特性
* 开箱即用的RBAC角色权限模型
* 基于JWT的认证授权，支持申请token、刷新token
* 简化的BindPermission注解，支持兼容shiro的简化权限绑定与自动鉴权
* 简化的Log注解记录操作日志
* 自动提取需要验证的后端接口, 借助前端功能方便绑定前后端菜单按钮权限
* 预置用户名密码登录(密码带盐加密), 并支持多种登录方式扩展
* 预置默认用户实体，并支持灵活替换用户类型
* 默认启用内存缓存，并支持自定义缓存实现类
* starter启动自动安装依赖的数据表，启用devtools，还可自动生成初始controller代码到本地

## 1、开箱即用的RBAC角色权限模型
基于“用户-角色-权限”的基础模型扩展“账号”实体，以支持多种登录方式。
包含了与此模型相关的后端代码，且依赖的数据结构在组件starter初次启动时将自动初始化。

## 2、基于JWT的认证与授权
* 申请token（后端）: 
~~~java
PwdCredential credential = new PwdCredential("admin", "123456");
String authtoken = AuthServiceFactory.getAuthService(Cons.DICTCODE_AUTH_TYPE.PWD.name()).applyToken(credential);
~~~
* 前端登录拿到token后缓存并将其加入每次请求的header中，属性名为: **authtoken**

* 当token的有效期剩余不足1/4时，组件会自动生成新的token写入response的header中，属性名同样为: **authtoken**。
前端检查response的header如果有新的authtoken，则替换本地的缓存值。

## 3、BindPermission注解的特性
* 支持在Controller的类及方法上添加，权限识别码支持类似Spring @RequestMapping注解的拼接功能，
方法上的注解支持自动鉴权，同时可被继承。如：
~~~java
@RequestMapping("/user")
@BindPermission(name = "用户") // code可选,默认自动识别; sortId可选
//继承类支持自动识别code为当前entity类名："IamUser"
public class IamUserController extends BaseCrudRestController<IamUser> {
    @GetMapping("/test")
    @BindPermission(name = "自定义", code = "test") // 拼接后的code=IamUser:test
    // 以上注解支持自动鉴权，与 @RequiresPermissions(values={"IamUser:test"}) 等效，省掉前缀以简化及继承。
    public JsonResult custom(){
    }
}
~~~

* BindPermission注解支持自动提取需要认证的接口列表，提供给前端进行快捷绑定。

## 4、默认的登录方式及扩展登录方式
默认的登录方式为：用户名密码，如需扩展其他登录方式：
* 创建你的认证凭证对象，继承自AuthCredential
* 实现AuthService接口，定义认证方式及接口实现
* 申请token替换为你的认证方式: 
~~~java
MyAuthCredential credential = new MyAuthCredential();
String authtoken = AuthServiceFactory.getAuthService("WX_CP").applyToken(credential);
~~~

## 5、默认的用户实体及替换用户实体
默认的用户实体为IamUser，获取当前登录用户对象:
~~~java
IamUser currentUser = IamSecurityUtils.getCurrentUser();
~~~
如果预置属性如果不能满足业务场景需要，可替换用户为你的实体：
~~~java
MyAuthCredential credential = new MyAuthCredential();
credential.setUserTypeClass(Employee.class); // 替换用户类型为自定义
String authtoken = AuthServiceFactory.getAuthService("WX_CP").applyToken(credential);
~~~
获取用户对象改为：
~~~java
Employee currentUser = IamSecurityUtils.getCurrentUser();
~~~
        
## 6、IAM组件相关配置：
```
#当前应用程序，多个系统时配置，默认为MS（管理系统）
diboot.iam.application=MS

#JWT的签名key，需自定义
diboot.iam.jwt-signkey=Diboot
#JWT的token过期时间（分），默认为60分钟
diboot.iam.jwt-token-expires-minutes=60

#Shiro的匿名urls，用逗号分隔
diboot.iam.anon-urls=/test/**,/abc/**

#是否开启权限检查，默认true。改为false后结合anno-urls=/**配置，可忽略权限检查，便于开发环境调试
diboot.iam.enable-permission-check=true

#缓存实现类，默认为: org.apache.shiro.cache.MemoryConstrainedCacheManager
diboot.iam.cache-manager-class=org.apache.shiro.cache.MemoryConstrainedCacheManager
```

## 7、样例参考 - [diboot-iam-example](https://github.com/dibo-software/diboot-example/tree/master/diboot-iam-example)

> 使用过程中遇到问题，可加群交流。