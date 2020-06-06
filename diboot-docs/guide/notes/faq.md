# diboot-iam-base-spring-boot-starter相关

只依赖core

tree

fastjson配置

datetime格式

找不到mapper.xml

后端代码如何来

1. **为什么我首次启动后，接口不能访问？**

答：您好，首次启动，devtools将为您的项目进行数据库初始化和生成基础代码等工作，这些都需要您在这些流程完成之后，重启应用方可生效。

2. **为何引入iam后启动报错？**

答：请确保您配置了**@EnableTransactionManagement**注解，可参考[IAM参数配置-注解配置](/guide/diboot-iam/开始使用.html#_2、参数配置：)