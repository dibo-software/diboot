# component-file: 文件处理组件
## 组件特性
* 封装常用的文件上传下载等处理
* 提供图片压缩水印等常用处理
* EasyExcel轻度封装，支持基于Java validation注解的自动数据校验，支持字典显示值-存储值自动转换
* Starter启动自动安装依赖的数据表


~~~java
@NotNull(message = "必须指定status")
@BindDict(type = "USER_STATUS")
@ExcelProperty(value = "状态", index = 4, converter = DictConverter.class)
private String userStatus;
~~~

## 、样例参考 - [component-file-example](https://github.com/dibo-software/diboot-v2-example/tree/master/component-file-example)
