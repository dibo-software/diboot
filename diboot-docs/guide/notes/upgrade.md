## 版本升级向导

### diboot v2.0.x 升级至 v2.1.x
#### 1. diboot-core：
* v2.1.x 版本开始，BaseCrudRestController移除了方法中的request参数，改为在BaseController中统一注入，以简化继承代码。
子类需要时直接用request变量即可，无需再定义request参数。
修改: 调用父类方法的Controller方法，需删除方法中的request参数定义及传递。
示例：
~~~java
public JsonResult getDepartmentVOList(DepartmentDto departmentDto, HttpServletRequest request) throws Exception{
    QueryWrapper<Department> queryWrapper = super.buildQueryWrapper(departmentDto, request);
    ...
}
~~~
修改为
~~~java
public JsonResult getDepartmentVOList(DepartmentDto departmentDto) throws Exception{
    QueryWrapper<Department> queryWrapper = super.buildQueryWrapper(departmentDto);
    ...
}
~~~

* v2.1.x 版本开始，BaseCrudRestController移除了VO泛型参数，便于子类灵活指定不同VO，同时父类方法getViewObject*增加VO class参数用于指定VO。
修改示例：
~~~java
public class DepartmentController extends BaseCustomCrudRestController<Department, DepartmentVO> {
    ...
    super.getViewObjectList(entity, pagination);
“}
~~~
修改为
~~~java
public class DepartmentController extends BaseCustomCrudRestController<Department> {
    ...
    super.getViewObjectList(entity, pagination, DepartmentVO.class);
“}
~~~

* v2.1.x版本开始，新增了通用的/common/attachMore接口，用于统一提供key-value形式数据，用于select下拉框等组件。
建议升级步骤：
    * 备份DictionaryController及各Base基础代码
    * 启动diboot-devtools，在组件初始化页面找到diboot-core，点击生成代码
    * 将你改动过的Base基础代码合并至新生成的类

* v2.1.x版本开始，core-starter中不再默认指定Date类型转json的默认格式，而是通过Date字段注解@JSONField(format=)去指定。
如果Date日期格式非预期，您可以通过以下两种方式调整：
1. 需要在Date字段上添加@JSONField(format=)注解。
或
2. 重新定义HttpMessageConverters，统一指定Date类型默认格式。

* v2.1.x版本core-starter自动初始化增加了String-Date转换的convertor至Spring FormatterRegistry。
如果您不需要request查询参数的String转Date，可重写addFormatters，移除String-Date转换。
~~~java
@Override
public void addFormatters(FormatterRegistry registry) {
   registry.removeConvertible(String.class, Date.class);
}
~~~

* v2.1.x版本开始，extdata扩展字段将不再推荐使用，该字段设计目的用于字段冗余的json存储，可以通过数据库的json数据类型实现。
devtools从2.1版本开始不再支持extdata的特殊处理。

* v2.1.x版本依赖组件升级为: Spring Boot 2.3.0，Mybatis-Plus 3.3.2，fastjson 1.2.70。根据您的依赖情况，可能会有依赖冲突需要解决。

#### 2. diboot-devtools
* v2.1版本开始，配置参数：
新增 **diboot.devtools.output-path** 代码的生成根路径配置项，
如entity, dto, controller, mapper, service, vo等路径无自定义需求，仅配置该根路径即可。
示例：
~~~properties
diboot.devtools.output-path=example/src/main/java/com/diboot/example/
~~~
同时开放更多的自定义配置项，如：
~~~properties
diboot.devtools.output-path-mapper-xml=
diboot.devtools.output-path-service-impl=
diboot.devtools.output-path-dto=
diboot.devtools.output-path-exception-handler=
~~~

v2.1.x版本开始支持前端代码生成，如果需要该功能，则需配置。如
~~~properties
diboot.devtools.output-path-frontend=/Workspace/diboot-antd-admin-ent/
~~~

#### 3. diboot-iam
