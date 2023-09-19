
<div align="center">

![logo](public/logo.png)

<h1>Diboot Admin UI</h1>

</div>

## 介绍
diboot-admin-ui前端基础项目，是一个基于Element Plus，与diboot后端基础组件配套的前端项目。

Diboot写的少，性能好 - 开发人员的低代码框架。

## 演示和文档

| 类型 | 链接 |
| -------- | -------- |
| 文档地址 | http://v3.diboot.com/ |
| 演示地址  | http://demo-v3.diboot.com/#/login |
| 项目地址  | https://gitee.com/dibo_software/diboot |



## 特点

* 登录、权限、接口对接上，与diboot相关后端组件无缝集成且开箱可用；
* 提取CRUD页面相关通用属性与方法到hooks文件中，简化代码；
* 菜单到按钮级别的细粒度权限控制、自动token刷新；
* 基于后端自动提取的智能化权限配置方案；
* 预置多种常用请求方式，轻松完成异步文件下载等；
* 预置上传、富文本、导入、预览打印、水印组件
* 预置:
  * 组织人员岗位管理功能；
  * 数据字典管理功能；
  * 菜单资源管理功能；
  * 角色与权限管理功能；
  * 定时任务管理功能；
  * 消息模板管理功能、消息记录管理功能；
  * 文件记录管理功能；
  * 登录日志管理功能、操作日志管理功能；
  * 系统配置管理功能；


## 部分截图

![dashboard](public/systemScreenshot/dashboard.png)
![echarts](public/systemScreenshot/echarts.png)
![dictionary](public/systemScreenshot/dictionary.png)

## 安装教程
``` sh
# 克隆项目
git clone https://gitee.com/dibo_software/diboot.git

# 进入项目目录
cd diboot-admin-ui

# 安装依赖
pnpm install

# 启动项目(开发模式)
pnpm run vite
```
启动完成后浏览器访问 http://localhost:5173

## 支持
如果觉得本项目还不错或在工作中有所启发，请在Gitee(码云)帮开发者点亮星星，这是对开发者最大的支持和鼓励！
