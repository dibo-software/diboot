# release notes

## v2.1.0 版本发布
#### diboot-core v2.1.0

* 增强@BindQuery注解，支持跨表字段的Join查询
* 新增@BindFieldList注解，用于绑定某个字段的集合
* 新增@DataAccessCheckpoint注解，以支持数据权限相关处理
* 新增@Accept(name)注解，支持非同名字段间的值拷贝
* 优化: 统一注入request到BaseController中，移除方法中的request参数
* 优化: @Bind注解的中间表查询解析支持首选调用对应Mapper执行，无Mapper再用JDBC
* BaseExtEntity添加Deprecated注解，不再推荐继承
* fastjson全局配置增加条件初始化，首选以用户自定义配置为准
* fastjson移除日期格式的全局配置，以字段指定为准
* fix bug: ContextHelper缓存支持刷新，以解决启用热更新时报错的问题
* ...

* 升级依赖组件: Spring Boot 2.3.x，Mybatis-Plus 3.3.x，fastjson 1.2.7x

#### diboot-devtools v2.1.0
* PC前端CRUD功能代码生成
* 新增组件初始化页面，starter启动检测生成改为手动点击按钮生成
* 优化数据库表管理: 
* 支持中文路径下的项目启动
* devtools启动优化
* ...
* 

#### IAM-base v2.1.0
* 排序的前后端集成
* 新增配置参数: diboot.iam.enable-permission-check，结合anno-urls=/**配置，可忽略权限检查，便于开发环境调试

#### diboot-file v2.1.0

#### 前端: diboot-antd-admin & diboot-element-admin
* 前端支持排序(与后端实现整合)
