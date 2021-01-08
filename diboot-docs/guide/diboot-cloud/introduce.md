# 介绍

## diboot-cloud 简介

基于diboot spring boot版本打造，将diboot的优势延申到微服务开发场景：
1. diboot-core 基础组件适配，并基于微服务特性进行调整优化
2. diboot-devtools 无缝兼容cloud微服务环境下的生成
3. 权限体系基于Spring security的标准OAuth2重构
4. 


## diboot-cloud 模块说明
- **api-gateway**:  接口网关
- **auth-server**:  认证中心
- **business-modules**: 业务模块
    - example-api:  业务模块示例
- **diboot-commons**: diboot通用组件
    - diboot-common-api:    通用接口
    - diboot-common-base:   通用实体类等
    - diboot-common-redis:  通用redis配置
- **diboot-modules**: diboot预置模块
    - diboot-file:  文件模块
    - diboot-scheduler: 定时任务模块
- **diboot-antd-admin**:    前端项目(antdv)
 
## diboot-cloud 依赖：
* **spring-boot**: 2.3.7.RELEASE
* **spring-cloud**: Hoxton.SR9
* **spring-cloud-alibaba**: 2.2.3.RELEASE
* **diboot-core**: 2.2.x

> 使用过程中遇到问题，可加群交流。
