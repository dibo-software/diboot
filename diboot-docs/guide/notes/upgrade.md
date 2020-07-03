## v2.0.x 升级至 v2.1.x

### 组件更改说明

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

* v2.1.x版本依赖组件升级为: Spring Boot 2.3.x，Mybatis-Plus 3.3.x，fastjson 1.2.7x。根据您的依赖情况，可能会有依赖冲突需要解决。

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

### 开始升级

> 将diboot所有的组件版本号替换至2.1.x的最新版本，然后按照下述内容进行相关更改即可。

#### 1. SpringBoot版本：

* 2.1.x已支持SpringBoot2.3.x版本，请先升级SpringBoot版本为2.3.x
* 更改spring-boot-starter-test依赖项，如下：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

#### Controller文件相关更改：
* 更改BaseCustomCrudRestController类的传入参数，如下：
```java
public class BaseCustomCrudRestController<E extends BaseEntity, VO extends Serializable> extends BaseCrudRestController {

}
// 改为
public class BaseCustomCrudRestController<E extends BaseEntity> extends BaseCrudRestController {

}
```
* 所有继承BaseCrudRestController类的地方，由之前传入Entity类与VO类改为只需要传入Entity类，如下：
```java
public class IamUserController extends BaseCrudRestController<IamUser, IamUserVO>{

}
// 改为
public class IamUserController extends BaseCrudRestController<IamUser> {

}
```
* getViewObjectListMapping 方法中去掉request参数，并传入VO类，如下：
```java
public JsonResult getViewObjectListMapping(IamUser entity, Pagination pagination, HttpServletRequest request) throws Exception{
    return super.getViewObjectList(entity, pagination, request);
}
// 改为
public JsonResult getViewObjectListMapping(IamUser entity, Pagination pagination) throws Exception{
    return super.getViewObjectList(entity, pagination, IamUserVO.class);
}
```
* getViewObjectMapping 方法中去掉request参数，并传入VO类，如下：
```java
public JsonResult getViewObjectMapping(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
    return super.getViewObject(id, request);
}
// 改为
public JsonResult getViewObjectMapping(@PathVariable("id")Long id) throws Exception{
    return super.getViewObject(id, IamUserVO.class);
}
```
* createEntityMapping 方法中去掉request参数，如下：
```java
public JsonResult createEntityMapping(@Valid @RequestBody CodeTemplate entity, HttpServletRequest request) throws Exception {
    return super.createEntity(entity, request);
}
// 改为
public JsonResult createEntityMapping(@Valid @RequestBody CodeTemplate entity) throws Exception {
    return super.createEntity(entity);
}
```
* updateEntityMapping 方法中去掉request参数，如下：
```java
public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody CodeTemplate entity, HttpServletRequest request) throws Exception {
    return super.updateEntity(id, entity, request);
}
// 改为
public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody CodeTemplate entity) throws Exception {
    return super.updateEntity(id, entity);
}
```
* deleteEntityMapping 方法中去掉request参数，如下：
```java
public JsonResult deleteEntityMapping(@PathVariable("id")Long id, HttpServletRequest request) throws Exception {
    return super.deleteEntity(id, request);
}
// 改为
public JsonResult deleteEntityMapping(@PathVariable("id")Long id) throws Exception {
    return super.deleteEntity(id);
}
```
* 使用到buildQueryWrapper方法的地方，去掉传入的request参数
* 其他相关报错地方可查看的新版本方法入参，进行相应调整即可

#### diboot-antd-admin前端项目升级
> 前端项目需要先从github的release notes中下载一个2.1.0版本的源码包，以下升级流程将依赖于此源码包。

* 先更新package.json依赖到最2.1.0版本的依赖，具体如下：
    * 更新 ant-design-vue 版本，如下：
    ```json
      {
          "ant-design-vue": "~1.6.2",
      }
    ```
    * 添加以下依赖项：
    ```json
      {
          "qs": "^6.9.4",
          "quill": "^2.0.0-dev.4",
          "v-viewer": "^1.5.1",
      }   
    ```
* 复制新版本 **src/components/diboot/** 路径下所有文件到旧版本项目中，添加及覆盖相关文件（注意不要将自定义的相关文件删掉了）；
* 复制新版本 **src/components/global.less**文件内容到旧版本项目中，覆盖之前的样式文件（如果您已经在此自定义了相关样式，记得将自定义的这部分提前提取出来）
* 更新 **src/utils/request.js** 文件：
    * 引入qs：
    ```javascript
    import qs from 'qs'
    ```
    * 更改 **service.interceptors.request.use** 的处理如下（如果您对此进行了自定义更改，则需要对比一下内容与您当前内容差异进行处理）：
    ```javascript
    // request interceptor
    service.interceptors.request.use(config => {
      const token = Vue.ls.get(ACCESS_TOKEN)
      if (token) {
        config.headers[JWT_HEADER_KEY] = token // 让每个请求携带自定义 token 请根据实际情况自行修改
      }
      // 只针对get方式进行序列化
      if (config.method === 'get') {
        config.paramsSerializer = function (params) {
          return qs.stringify(params, { arrayFormat: 'repeat' })
        }
      }
      return config
    }, err)
    ```
    * 更改 **service.interceptors.response.use** 的处理如下（如果您对此进行了自定义更改，则需要对比一下内容与您当前内容差异进行处理）：
    ```javascript
    // response interceptor
    service.interceptors.response.use((response) => {
      // 检查是否携带有新的token
      const newToken = response.headers[JWT_HEADER_KEY]
      if (newToken) {
        // 将该token设置到vuex以及本地存储中
        Vue.ls.set(ACCESS_TOKEN, newToken, 7 * 24 * 60 * 60 * 1000)
        store.commit('SET_TOKEN', newToken)
      }
      // 如果请求成功，则重置心跳定时器
      if (response.status === 200) {
        resetPingTimer()
      }
    
      // 如果返回的自定义状态码为 4001， 则token过期，需要清理掉token并跳转至登录页面重新登录
      if (response.data && response.data.code === 4001) {
        Vue.ls.remove(ACCESS_TOKEN)
        store.commit('SET_TOKEN', '')
        store.commit('SET_ROLES', [])
        router.push('/login')
        throw new Error('登录过期，请重新登录')
      }
    
      // 如果当前请求是下载请求
      if (response.headers.filename) {
        return {
          data: response.data,
          filename: decodeURI(response.headers.filename),
          code: parseInt(response.headers['err-code'] || '0'),
          msg: decodeURI(response.headers['err-msg'] || '')
        }
      }
      return response.data
    }, err)
    ```
  
* 更改 **src/utils/treeDataUtil.js** 文件（如果您对该文件没有更改，可直接替换为新版本内容）：
    * 更改 **permissionTreeListFormatter** 方法，代码如下：
    ```javascript
    /***
     * 权限树状结构转化（用于角色权限配置中）
     * @param treeList
     * @param valueField
     * @param titleField
     * @returns {[]|undefined}
     */
    const permissionTreeListFormatter = function (treeList, valueField, titleField) {
      if (treeList === undefined || treeList.length === 0) {
        return undefined
      }
    
      const formatterList = []
      treeList.forEach(item => {
        const slots = {
          icon: item.displayType === 'MENU' ? 'menu' : 'permission'
        }
        const formatterItem = {
          slots,
          type: item.displayType,
          parentId: item.parentId,
          id: item.id,
          sortId: item.sortId,
          key: item[valueField],
          value: item[valueField],
          title: item[titleField]
        }
        if (item.children !== undefined && item.children.length > 0) {
          formatterItem.children = permissionTreeListFormatter(item.children, valueField, titleField)
        }
        formatterList.push(formatterItem)
      })
    
      return formatterList
    }
    ```
    * 添加 **sortTreeListFormatter** 方法，代码如下：
    ```javascript
    /***
     * 排序树状结构格式化
     * @param treeList
     * @param valueField
     * @param titleField
     * @returns {undefined}
     */
    const sortTreeListFormatter = function (treeList, valueField, titleField) {
      if (treeList === undefined || treeList.length === 0) {
        return undefined
      }
    
      const formatterList = []
      treeList.forEach(item => {
        const formatterItem = {
          type: item.displayType,
          parentId: item.parentId,
          id: item.id,
          sortId: item.sortId,
          key: item[valueField],
          value: item[valueField],
          title: item[titleField]
        }
        if (item.children !== undefined && item.children.length > 0) {
          formatterItem.children = sortTreeListFormatter(item.children, valueField, titleField)
        }
        formatterList.push(formatterItem)
      })
    
      return formatterList
    }
    ```
    * 导出地方增加 **sortTreeListFormatter** 方法，如下：
    ```javascript
     export {
       treeListFormatter,
       clearNullChildren,
       treeList2list,
       treeList2IndentList,
       list2childrenMap,
       routersFormatter,
       apiListFormatter,
       permissionTreeListFormatter,
       sortTreeListFormatter
     }
    ```

* 更改所有a-drawe的 **body-style** 属性:
    * 更新 **a-drawer** 标签上的**body-style**属性如下（此处如果没有自定义相关数值，可以使用批量替换）：
    ```html
    :wrapStyle="{height: 'calc(100% - 108px)',overflow: 'auto',paddingBottom: '108px'}"
     // 更改为
    :body-style="{ paddingBottom: '80px' }"
    ```
* 更改所有列表页搜索表单的 **提交事件**，更改列表页中的form标签即可，如下（此处如果没有自定义相关数值，可以使用批量替换）：
```html
    <a-form layout="inline" @submit.native="getList">
    // 改为
    <a-form layout="inline" @submit.native="onSearch">
``` 
* 更改 **src/views/system/dictionary/list.vue** 文件
    * 更改当前文件的columns属性下的createTime，如下：
    ```javascript
    {
      title: '创建时间',
      dataIndex: 'createTime'
    },
    // 改为
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true
    },
    ```
* 更改 **src/views/system/iamFrontendPermission/form.vue** 文件，使用新版本内容直接替换即可；
* 更改 **src/views/system/iamLoginTrace/list.vue** 文件，使用新版本内容直接替换即可；
* 更改 **src/views/system/iamRole/list.vue** 文件
    * 更改当前文件的columns属性下的createTime，如下：
    ```javascript
    {
      title: '创建时间',
      dataIndex: 'createTime'
    },
    // 改为
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true
    },
    ```
* 更改 ** src/views/system/iamUser/form.vue **，如下：
    * 角色选择标签更换如下：
    ```html
     <a-form-item label="角色">
         <a-select
           v-if="more.iamRoleKvList"
           :getPopupContainer="getPopupContainer"
           mode="multiple"
           placeholder="请选择角色"
           v-decorator="[
             'roleIdList',
             {
               initialValue: model.roleList && model.roleList.map(role => {return `${role.id}`}),
               rules: [{ required: true, message: '角色不能为空' }]
             }
           ]"
         >
           <a-select-option
             v-for="(role, index) in more.iamRoleKvList"
             :key="index"
             :value="`${role.v}`"
           >
             {{ role.k }}
           </a-select-option>
         </a-select>
    </a-form-item>
    ```
    * 在data属性中新增如下数据：
    ```javascript
    attachMoreList: [
        {
          type: 'D',
          target: 'GENDER'
        },
        {
          type: 'D',
          target: 'USER_STATUS'
        },
        {
          type: 'T',
          target: 'iamRole',
          key: 'name',
          value: 'id'
        }
    ]
    ```
* 更改 ** src/views/system/iamUser/list.vue **文件，在data属性中新增如下数据：
```javascript
attachMoreList: [
    {
      type: 'D',
      target: 'GENDER'
    },
    {
      type: 'D',
      target: 'USER_STATUS'
    },
    {
      type: 'T',
      target: 'iamRole',
      key: 'name',
      value: 'id'
    }
],
```
* 更改 **src/main.js** 文件：
    * 添加import项，如下：
    ```javascript
    import Viewer from 'v-viewer'
    import 'viewerjs/dist/viewer.css'
    ```
    * 添加其他代码，如下：
    ```javascript
    Vue.use(Viewer)
    Viewer.setDefaults({
      // 需要配置的属性
      toolbar: true
    })
    ```