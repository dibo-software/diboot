# CRUD快速集成

## 开始之前

* 在diboot-antd-admin中，我们对CRUD等常用功能进行了一些抽象，将常用的列表、详情、新建、更新、删除等功能需要的相关属性与方法都抽象成了vue的mixins文件，这些文件在**src/components/diboot/mixins**文件夹下。
* 也可以对已有的一些页面组件代码进行阅读，比如**src/views/system/iamUser**文件夹下的相关组件代码。
::: tip
在阅读文档之前，建议您准备好[diboot-antd-admin 最新版的环境](https://github.com/dibo-software/diboot-antd-admin/releases)源码环境，搭配代码使用更佳。
:::
## 列表页

1. 引入列表的mixins文件，如下：
```javascript
import list from '@/components/diboot/mixins/list'

export default {
  mixins: [list]
}
```
2. 配置列表页的接口前缀，这个接口前缀在mixins的处理中，会自动拼接'/list'，如果后端代码是通过diboot自动生成的，这也是后端默认的接口规则；
3. 自定义列表接口：如果您的列表接口最后面不是'/list'，而是'/getList'，那么可以在data中设置listApi属性，如下：
```javascript
export default {
  data () {
    return {
        listApi: 'getList'
    }
  }
}
```
4. 配置columns数据：columns数据是定义该列表页需要显示哪些列的，默认的column只需要配置title与dataIndex属性即可，如下：
```javascript
export default {
  data () {
    return {
      columns: [
          {
            title: '姓名',
            dataIndex: 'realname'
          }
      ]
    }
  }
}
```
5. 属性配置

| 属性             | 说明          | 类型	|默认值 | 版本|
| -------------   |:-------------| -----| -----| -----|
| baseApi         | 请求接口基础路径（必须配置）|String | / | - |
| listApi         | 列表数据接口|    String |  /list  |- |
| deleteApiPrefix | 删除接口前缀 |    String | / | - |
| exportApi       | 导出接口|    String |~~/export~~ /excel/export|~~2.0.5~~ **2.1.x**|
| customQueryParam| 自定义参数（不被查询表单重置和改变的参数） | object | {} | - |
| queryParam      | 与查询条件绑定的参数（会被查询表单重置和改变的参数）      |   object | {} | - |
| dateRangeQuery  | 日期区间选择配置 <br/>**时间区间字段请放在这个对象中，会自动构建参与查询**      |   object | {} | **2.1.x** |
| advanced        | 高级搜索 展开/关闭      |   boolean | false | **2.1.x** |
| data            | 存储列表数据      |   array | [] |-|
| getMore         | 是否使mixin在当前业务的attachMore接口中自动获取关联数据<br/>**<a href="#业务对象关联详解">:point_right:业务对象关联详解</a>**|    boolean | false | - |
| attachMoreList  | 获取关联数据列表的配置列表<br/>**<a href="#业务对象关联详解">:point_right:业务对象关联详解</a>**      | array    |   []  |   **2.1.x**    |
| more            | 存储当前对象的关联数据对象<br/>**<a href="#业务对象关联详解">:point_right:业务对象关联详解</a>**| object    |   {}  |  -   |
| getListFromMixin| 是否在页面初始化时自动加载列表数据    |    boolean | true | - |
| loadingData     | 标记页面加载数据状态      |    boolean | false | - |
| pagination      | 分页配置      |    object | {pageSize: 10,current: 1,total: 0,showSizeChanger: true,pageSizeOptions: ['10', '20', '30', '50', '100'],showTotal: (total, range) => `当前显示 ${range[0]} - ${range[1]} 条/共 ${total} 条` } | - |

6. 功能函数

| 名称             | 说明          | 参数	| 版本|
| -------------   |:-------------| -----| -----|
| handleTableChange | 分页、排序、筛选变化时触发 |function(pagination, filters, sorter) | - |
| appendSorterParam | 构建排序 handleTableChange调用|function(sorter) | **2.1.x**  |
| toggleAdvanced  | 切换展示更多搜索框（绑定**advanced**属性）|  -   | **2.1.x** |
| onSearch  | 搜索，查询第一页（默认查询按钮触发）|   -  | - |
| postList  | post请求的获取列表（可以传递更长、更复杂参数） |  -   | - |
| getList  | get请求获取列表 |  -   | - |
| attachMore  | 加载当前页面关联的对象或者字典，参考属性：getMore、attachMoreList、more |  -  | - |
| reset  | 重置查询 |  -  | - |
| remove  | 根据id删除 |  function(id)  | - |
| exportData  | 导出数据至excel |  -  | **2.1.x** |
| downloadFile  | 下载文件 |  function(res)  | **2.1.x** |
| getPopupContainer  | 解决带有下拉框组件在滚动时下拉框不随之滚动的问题 |  function(trigger)  |-|
| contentTransform  | 处理查询参数中的moment数据 默认转化为YYYY-MM-DD |  function(content, transform = {})  | **2.1.x** |
| dateRange2queryParam  | 构建区间查询参数，（转化dateRangeQuery属性内容） |  -  | **2.1.x** |

7. 钩子函数

| 名称             | 说明          | 参数	| 版本|
| -------------   |:-------------| -----| -----|
| afterLoadList | 加载数据之后操作 |function(list) | - |
| rebuildQuery | 重新构建查询条件 (接收已经定义的customQueryParam与queryParam的合并值)|function(query) | - |

## 新建与更新

1. 引入表单的mixins文件，如下：
```javascript
import form from '@/components/diboot/mixins/form'

export default {
  mixins: [form]
}
```
2. 属性配置：

| 属性             | 说明          | 类型	|默认值 | 版本|
| -------------   |:-------------| -----| -----| -----|
| primaryKey      | 主键字段名      |    string | id | **2.1.x** |
| baseApi         | 请求接口基础路径(必须配置)|String | / | - |
| createApi       | 新建接口，自动拼接在*baseApi*之后|    String |  /  |- |
| updateApiPrefix | 更新接口前缀，自动拼接在*baseApi*之后 |    String | / | - |
| labelCol        | label 默认布局样式           |    object |{xs: {span: 24}, sm: {span: 5}}|-|
| wrapperCol      | form控件默认布局样式           |    object |{xs: {span: 24}, sm: {span: 16}}|-|
| model| 存放form数据 | object | {} | - |
| title           | 标题           |    String |新建/更新|-|
| more            | 存储当前对象的关联数据对象<br/>**<a href="#业务对象关联详解">:point_right:业务对象关联详解</a>**   | object    |   {}  |  -   |
| attachMoreList  | 获取关联数据列表的配置列表<br/>**<a href="#业务对象关联详解">:point_right:业务对象关联详解</a>**      | array    |   []  |   **2.1.x**    |
| getMore         | 是否使mixin在当前业务的attachMore接口中自动获取关联数据<br/>**<a href="#业务对象关联详解">:point_right:业务对象关联详解</a>**|    boolean | false | - |
| state      | 当前组件状态对象      |    object | {visible: false, confirmSubmit: false} | - |
| isUpload      | 当前form是否包含上传<br/>**<a href="#文件上传详解">:point_right:文件上传详解</a>**      |    boolean | false | **2.1.x** |
| fileWrapper      | 文件包装容器 <br/>**<a href="#文件上传详解">:point_right:文件上传详解</a>**      |    object | {} | **2.1.x** |
| fileUuidList      | 文件存储服务器后返回的唯一标识集合<br/>**<a href="#文件上传详解">:point_right:文件上传详解</a>**       |    array | [] | **2.1.x** |

3. 功能函数

| 名称             | 说明          | 参数	| 版本|
| -------------   |:-------------| -----| -----|
| moment | moment时间相关操作 |- | - |
| open | 打开表单 (根据参数id存在与否，设置为更新or新建操作) |function(id) | - |
| close | 关闭表单 |- | - |
| validate | 提交前的验证流程 |- | - |
| add | 新建记录的提交 |function(values) | - |
| update | 更新记录的提交 |function(values) | - |
| onSubmit | 表单提交事件 |- | - |
| getPopupContainer | 解决带有下拉框组件在滚动时下拉框不随之滚动的问题 |function(trigger) | - |
| attachMore | 加载当前页面关联的对象或者字典，参考属性：getMore、attachMoreList、more |- | - |
| filterOption | select选择框启用search功能后的过滤器 |- | - |
| clearForm | 清除form内容（关闭的时候自动调用） |- | - |
| __setFileUuidList__ | 设置文件uuid，参考属性：isUpload、fileWrapper、fileUuidList  |- | - |
| __defaultFileWrapperKeys__ | 初始化fileWrapper（关闭时候自动调用）  |- | - |
4. 钩子函数

| 名称             | 说明          | 参数	| 版本|
| -------------   |:-------------| -----| -----|
| afterOpen | 在组件打开后，或者更新时数据加载完毕后，执行该函数 |function(id) | - |
| afterClose | 在组件关闭后，执行该函数 |- | - |
| enhance | 在校验完成后，对提交数据进行处理的函数 |function(values) | - |
| submitSuccess | 提交成功后，执行该函数，默认关闭该组件，并发送complete和changeKey事件 |function(result) | - |
| submitFailed | 提交失败后，执行该函数，默认提示错误消息 |function(result) | - | 

## 查看详情

1. 引入表单的mixins文件，如下：
```javascript
import detail from '@/components/diboot/mixins/detail'

export default {
  mixins: [detail]
}
```

2. 属性配置

| 属性             | 说明          | 类型	|默认值 | 版本|
| -------------   |:-------------| -----| -----| -----|
| baseApi         | 请求接口基础路径(必须配置)|String | / | - |
| visible         | 当前组件显示状态 |    String | / | - |
| model           | 当前详情框详情数据           |    object |{}|-|
| title           | 标题           |    String |详情|-|
| spinning       | loading状态      |    boolean | false | - |

3.功能函数

| 名称             | 说明          | 参数	| 版本|
| -------------   |:-------------| -----| -----|
| open         | 打开详情（加载服务端数据）|function(id) | - |
| close         | 关闭详情 |    - | - |
| downloadFile  | 下载文件(传入接口地址)|    function(path) | **2.1.x** |

4. 钩子函数

| 属性             | 说明          | 参数	|
| -------------   |:-------------| -----|
| afterOpen | 打开之后的操作|function(id) |
| afterClose| 关闭之后操作 |    - |

## 详解
- <a id="业务对象关联详解">业务对象关联详解</a>
    - more: 值来源于*getMore*或*attachMoreList* 配置请求接口后返回的结果；
    - getMore: 开启关联数据会从当前业务的/attachMore接口中读取，开启后优于attachMoreList使用；
    - attachMoreList: **2.1.x 新增** 实现关联数据从/common/attachMore接口统一读取，配置如下：
        ```javascript
        // type：D(字典数据)/T（关联业务对象）
        attattachMoreList: [
            {
              type: 'D',            //  查询字典
              target: 'GENDER'      // 指向字典的 type = GENDER字段值
            },
            {
              type: 'D',            //查询字典
              target: 'USER_STATUS' // 指向字典的 type = USER_STATUS字段值
            },
            {
              type: 'T',          //  查询对象
              target: 'iamRole',  // 指向IamRole对象
              key: 'name',        // 指向IamRole#name字段，需要查询作为key的字段名称
              value: 'id'         // 指向IamRole#id字段，需要查询作为value的字段名称
            }
        ]
        ```
    - attachMoreList 返回值会自动绑定至more属性中，上述配置样例返回值为(⚠️data的key规则是上述target的小驼峰命名 + KvList)：
        ```json
        {
            "code":0,
            "data":{
                "userStatusKvList":[
                    {
                        "k":"有效",
                        "v":"A"
                    }
                ],
                "iamRoleKvList":[
                    {
                        "k":"超级管理员",
                        "v":10000
                    }
                ],
                "genderKvList":[
                    {
                        "k":"女",
                        "v":"F"
                    },
                    {
                        "k":"男",
                        "v":"M"
                    }
                ]
            },
            "msg":"操作成功"
        }
        ```
    - 如非特殊，建议使用attachMoreList配置用以简化代码 
- <a id="文件上传详解">文件上传详解 (2.1.x新增)</a>

   **以下属性讲解，基于Upload组件** :point_right: [Upload.vue组件概述](/guide/diboot-antd-admin/组件.html#upload组件)
   - isUpload: 标记当前form表单中是否包含上传属性，使用如：图片、文件，默认不包含，如果引入组件，请手动开启：
   ```javascript
    data() {
      return {
        isUpload: true
      }
    }
   ```
   - fileWrapper: 所有文件的集合都放置与fileWrapper对象中，提交的时候会自动遍历，然后提交至服务端进行数据处理：
      - template内容
      ```html{5}
      <upload
        v-if="state.visible"
        :prefix="filePrefix"
        :action="fileAction"
        :file-list="fileWrapper.slideshowImgsList"
        :rel-obj-type="relObjType"
        rel-obj-field="slideshowImgs"
        :limit-count="9"
        :is-image="true"
        list-type="picture-card"
        v-model="form.slideshowImgs"
      ></upload>
      ``` 
      - script内容
      ```javascript
        data() {
          return {
            // 包含属性
            fileWrapper: {
             //存储form.slideshowImgs属性对应的文件集合
             slideshowImgsList: []
            }
          }
        }
       ```
   - fileUuidList: 如果包含上传，那么会自动构建文件的提交数据用于绑定当前对象  