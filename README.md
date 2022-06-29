> 用上diboot，告别常规SQL和CRUD，写的更少，性能更好！

> 新用户指南: [手把手来体验](https://www.diboot.com/guide/beginner/bootstrap.html) 、[看视频了解 Diboot](https://www.bilibili.com/video/BV17P4y1p7L4) 、[了解Diboot如何做到高性能](https://www.bilibili.com/video/BV1tL411p7CD)

# diboot - 基础组件化繁为简，高效工具以简驭繁
<hr>
<p align="center">
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="https://img.shields.io/hexpm/l/plug.svg">
    </a>
    <a href="https://mvnrepository.com/artifact/com.diboot" target="_blank">
        <img src="https://img.shields.io/maven-central/v/com.diboot/diboot-core-spring-boot-starter">
    </a>
</p>

> 设计目标: 为开发人员打造的低代码开发平台，将复杂的工作简单化、重复的工作自动化，提高质量、效率、可维护性。

![diboot平台组成结构图](https://www.diboot.com/structure.png)

> [diboot-cloud 微服务版本，看这里->](https://www.diboot.com/guide/diboot-cloud/introduce.html)

> [diboot-workflow 工作流版本，看这里->](https://www.diboot.com/guide/diboot-workflow/introduce.html)

## diboot基础组件
### 1、 diboot-core: 精简优化内核：写的更少，性能更好
主要特性：
* 单表CRUD无SQL
* 关联绑定无SQL（注解自动绑定）
* 数据字典无SQL（注解自动绑定）
* 跨表查询无SQL（自动构建QueryWrapper与查询）
* BaseService扩展增强，支持常规的单表及关联开发场景接口
* 其他常用工具类、状态码、异常处理的更优实践封装

基于diboot-core的CRUD和常规关联的功能实现，代码量比传统Mybatis项目减少80%+，且性能更好更易维护。
> 详细文档: [diboot-core文档](https://www.diboot.com/guide/diboot-core/introduce.html).

### 2、IAM 身份认证基础组件 及 配套VUE前端框架（diboot-antd-admin、diboot-element-admin）

* 开箱即用的RBAC角色权限模型与预置组织人员岗位模型
* 基于无状态token的认证授权，支持刷新token
* 简化的BindPermission注解，支持兼容shiro的简化权限配置与自动鉴权
* 自动提取需要鉴权的后端接口, 借助前端功能方便配置菜单按钮权限
* 无缝适配redis，引入redis依赖即可启用shiro的redis缓存
* 支持基于注解的数据权限实现、简化的Log注解记录操作日志等
* 支持灵活的扩展能力（扩展多种登录方式、灵活替换用户实体类、自定义缓存等）
> 详细文档: [diboot-iam文档](https://www.diboot.com/guide/diboot-iam/introduce.html).

### 3、diboot-file 文件相关处理组件

* EasyExcel轻量封装，支持Java注解校验与@ExcelBind*注解实现字典及关联字段的name-value转换，并提供完善的校验错误提示
* 文件存储接口化，预置本地存储，简单扩展OSS、分布式存储等实现
* 封装常用的文件上传下载、图片压缩水印等常用处理
> 详细文档: [diboot-file文档](https://www.diboot.com/guide/diboot-file/introduce.html).

### 4、diboot-scheduler 定时任务组件

* Quartz定时任务统一管理及日志的最佳实践封装
* @CollectThisJob注解提供定时任务定义，自动收集供前端选择
> 详细文档: [diboot-scheduler文档](https://www.diboot.com/guide/diboot-scheduler/introduce.html).

### 5. diboot-message 消息通知组件

* 通用的消息模板&模板变量的设计方案
* 支持多通道的消息通知发送
> 详细文档: [diboot-message文档](https://www.diboot.com/guide/diboot-message/introduce.html).

### 6. diboot-mobile 移动端组件

* 提供了配套的 [diboot-mobile-ui](https://gitee.com/dibo_software/diboot-mobile-ui) ，内置了多种登录方式
* 支持 账号密码登录、微信小程序登录（自动注册）微信公众号登录（自动注册）
> 详细文档: [diboot-mobile文档](https://www.diboot.com/guide/diboot-mobile/introduce.html).

## devtools开发助理
* 极简易用（引入依赖jar，配置参数后，即可随应用启动运行）
* 功能强大（数据结构与代码同步、前后端代码一键生成、前端面板组件编排）
* 配置灵活（可按需配置生成代码路径及启用`Lombok`、`Swagger`等）
* 代码标准（devtools标准化了数据结构定义与代码实现，降低维护成本）
* 支持多库（MySQL、MariaDB、ORACLE、SQLServer、PostgreSQL、达梦、人大金仓）

> 详细文档: [diboot-devtools文档](https://www.diboot.com/guide/diboot-devtools/introduce.html).

## 捐助支持
<img src="https://www.diboot.com/wechat_donate.png" width = "200" height = "200" alt="捐助二维码" align=center />

感谢每一位支持diboot的粉丝朋友。

## 技术交流
如遇diboot相关技术问题，欢迎加群交流：

* **VIP技术支持QQ群**（付费用户尊享）: [931266830]()

* 技术交流QQ群: [731690096]()

* 技术交流微信群 加: [wx20201024]() (备注diboot)
