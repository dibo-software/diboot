> 可以告别常规SQL和CRUD了！diboot新用户: [看视频快速了解diboot](https://www.bilibili.com/video/BV17V411r7Cc) 、 [手把手跟我来体验](https://www.diboot.com/guide/newer/bootstrap.html) 、 
> [看diboot如何把关联查询性能提升10倍](https://www.bilibili.com/video/BV1tL411p7CD)

# diboot - 基础组件化繁为简，高效工具以简驭繁
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

spring boot版本目前实现: core高效内核 + devtools开发助理 + IAM身份认证、file文件处理、scheduler定时任务等基础组件 + diboot-*-admin基础后台。

> [spring cloud版本，移步这里->](https://github.com/dibo-software/diboot-cloud)

> [diboot-workflow 工作流授权](https://www.diboot.com/ent/service.html)

## diboot基础组件
### 1、 diboot-core: 精简优化内核
高效精简内核，重构查询方式，提高10倍查询性能，简化开发。主要实现：
#### 1). 单表CRUD无SQL
   > 基于Mybatis-Plus实现（Mybatis-Plus具备通用Mapper方案和灵活的查询构造器）
#### 2). 关联绑定无SQL（注解自动绑定）
   > 扩展实现了多表关联查询的无SQL方案，只需要一个简单注解@Bind*，就可以实现关联对象（含字段、字段集合、实体、实体集合等）的数据绑定，且实现方案是将关联查询拆解为单表查询，保障最佳性能。
#### 3). 数据字典无SQL（注解自动绑定）
   > 通过@BindDict注解实现数据字典(枚举)的存储值value与显示值name的转换。
#### 4). 跨表查询无SQL（自动构建QueryWrapper与查询）
   > @BindQuery注解绑定字段查询方式及关联表，自动构建QueryWrapper，并动态执行单表或Join联表查询。
#### 5). BaseService扩展增强，支持常规的单表及关联开发场景接口
   > createEntityAndRelatedEntities、getValuesOfField、exists、getKeyValueList、getViewObject*等接口
#### 6). 其他常用工具类、状态码、异常处理的最佳实践封装
   > JsonResult、字符串处理、常用校验、BeanUtils、DateUtils等

基于diboot-core 2.x版本的CRUD和简单关联的常规功能实现，代码量比传统Mybatis项目减少80%+），且实现更高效更易维护。
> 详细文档: [diboot-core文档](https://www.diboot.com/guide/diboot-core/%E7%AE%80%E4%BB%8B.html). 

### 2、IAM 身份认证基础组件 及 配套VUE前端框架（diboot-antd-admin、diboot-element-admin）

* 开箱即用的RBAC角色权限模型与预置组织人员岗位模型
* 基于JWT的认证授权，支持申请token、刷新token
* 简化的BindPermission注解，支持兼容shiro的简化权限绑定与自动鉴权
* 简化的Log注解记录操作日志
* 自动提取需要验证的后端接口, 借助前端功能方便绑定前后端菜单按钮权限
* 支持基于注解的数据权限实现
* 支持灵活的扩展能力（扩展多种登录方式、灵活替换用户实体类、自定义缓存等）
> 详细文档: [diboot-iam文档](https://www.diboot.com/guide/diboot-iam/%E4%BB%8B%E7%BB%8D.html). 

### 3、diboot-file 文件相关处理组件

* EasyExcel轻量封装，支持Java注解校验与@ExcelBind*注解实现字典及关联字段的name-value转换，并提供完善的校验错误提示
* 封装常用的文件本地存储、上传下载、图片压缩水印等常用处理
> 详细文档: [diboot-file文档](https://www.diboot.com/guide/diboot-file/%E4%BB%8B%E7%BB%8D.html). 

### 4、diboot-scheduler 定时任务组件

* Quartz定时任务统一管理及日志的最佳实践封装
* @CollectThisJob注解提供定时任务定义，自动收集供前端选择
> 详细文档: [diboot-scheduler文档](https://www.diboot.com/guide/diboot-scheduler/%E4%BB%8B%E7%BB%8D.html). 

### 5. diboot-message 消息通知组件
* 通用的消息模板&模板变量的设计方案
* 支持多通道的消息通知发送

## devtools开发助理

* 使用很简单（UI界面操作，引入依赖配置参数后，即可随Spring boot/Spring cloud本地项目启动运行）
* 功能很强大：
    * 单表与关联场景CRUD导入导出的完整功能全自动生成，无需手写代码
    * 结合前端面板组件编排能力，覆盖更多场景的自动化生成
    * 数据结构变更与代码联动同步，自动记录变更SQL、维护索引
    * 一键生成代码&非覆盖式更新本地后端代码
* 配置很灵活（可按需配置生成代码路径，是否启用`Lombok`、`Swagger`等）
* SQL与代码很标准（devtools标准化了数据结构定义与代码实现）
* 支持多数据库（MySQL、MariaDB、ORACLE、SQLServer、PostgreSQL）
> 详细文档: [diboot-devtools文档](https://www.diboot.com/guide/diboot-devtools/%E4%BB%8B%E7%BB%8D.html). 

## 捐助支持
<img src="https://www.diboot.com/wechat_donate.png" width = "200" height = "200" alt="捐助二维码" align=center />

感谢所有捐助的朋友为开源事业的发展做出的努力。

## 技术交流
如遇diboot相关技术问题，欢迎加群交流：

* **VIP技术支持QQ群**（捐助/付费用户尊享）: [931266830]()

* 技术交流QQ群: [731690096]() 

* 技术交流微信群 加: [wx20201024]() (备注diboot)