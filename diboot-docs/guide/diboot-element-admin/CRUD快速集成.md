# CRUD快速集成

## 开始之前

* 在diboot-element-admin中，我们对CRUD等常用功能进行了一些抽象，将常用的列表、详情、新建、更新、删除等功能需要的相关属性与方法都抽象成了vue的mixins文件，这些文件在**src/components/diboot/mixins**文件夹下，强烈建议您先阅览以下他们。
* 也可以对已有的一些页面组件代码进行阅读，比如**src/views/system/iamUser**文件夹下的相关组件代码。

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
5. 删除数据：直接在删除按钮上调用remove函数即可，传入需要删除的当前id。
6. 导出数据：直接在导出按钮上调用exportData函数即可，将会传入当前查询参数，并异步下载文件。
6. 钩子函数：afterLoadList，在列表加载完毕将会执行该操作，另外，删除函数返回的是 Promise对象，所以可以使用.then在删除完毕时执行某些操作。
7. 相关配置：
    * customQueryParam: 不会被搜索栏改变的初始查询参数对象，一般用于页面固定的查询参数
    * queryParam：与查询条件绑定的查询参数对象
    * more：当前对象的关联数据对象
    * getMore：是否在该页面初始化时加载当前对象的关联数据对象，加载的关联数据对象存储在more中，默认为false
    * getListFromMixin：是否在页面初始化时自动加载列表数据，默认为true
    * loadingData：标记页面加载数据状态
    * pagination：分页配置
    * baseApi：接口前缀（必须配置）
    * listApi：列表数据接口，默认为 /list
    * deleteApiPrefix：删除接口前缀，默认为 / 
    * exportApi: 导出接口，默认为 /export

## 新建与更新

1. 引入表单的mixins文件，如下：
```javascript
import form from '@/components/diboot/mixins/form'

export default {
  mixins: [form]
}
```
2. 相同业务对象的新建与更新使用同一个表单组件，默认以抽屉形式打开，可在引入地方，直接调用该组件的open方法进行打开。
3. 使用以上引入列表mixins的方式引入form的mixins文件，将会具有mixins中的所有能力。
4. 已有功能：
    * open函数，可直接调用打开该表单
    * 提供根据open中参数情况自动切换新建与更新操作
    * 提供打开与关闭完成的钩子函数
    * 提供表单提交函数 onSubmit
    * 对form下校验规则的自动校验
    * 提供对校验的自定义操作
    * 提供校验完成后的enhance钩子函数，可对需要提交的数据进行再处理
    * 可以对新建数据或更新数据的方法重写
    * 提供提交成功与提交失败的钩子函数
    * 提供表单默认布局参数
    * 提供对关联数据的自动加载
    * 提供各项配置支持自定义接口等
5. 相关配置：
    * baseApi：与列表页相同
    * createApi：新建接口，将自动拼接在baseApi之后，默认为 / 
    * updateApiPrefix: 更新接口前缀，将自动拼接在baseApi之后，默认为 /
    * initFormData：表单初始数据（表示表单数据结构）
    * form: 更新时装载加载的原数据，新建时为上述initFormData数据的克隆
    * title：页面标题，默认为新建或更新
    * reloadMore：加载的关联数据对象，默认为{}，如果list页面以及由more数据，且不与自身关联，可通过传参方式直接使用list中的more参数
    * getMore：初始化时是否加载reloadMore数据，默认为false
    * state：当前组件状态对象，默认为：{visible: false, confirmSubmit: false}
6. 钩子函数：
    * afterOpen：在组件打开后，或者更新时数据加载完毕后，执行该函数
    * afterClose： 在组件关闭后，执行该函数
    * enhance：在校验完成后，对提交数据进行处理的函数
    * submitSuccess： 提交成功后，执行该函数，默认关闭该组件，并发送complete和changeKey事件
    * submitFailed： 提交失败后，执行该函数，默认提示错误消息

## 查看详情

1. 引入表单的mixins文件，如下：
```javascript
import detail from '@/components/diboot/mixins/detail'

export default {
  mixins: [detail]
}
```
2. 已有功能：
    * 自动加载当前记录数据
    * 关闭弹窗或者抽屉
    * 可通过父组件传入width参数，控制抽屉的宽度
3. 相关配置：
    * baseApi：与列表页相同
    * visible：当前组件显示状态，默认为false
    * model：当前详情框详情数据
    * title：当前详情框标题
4. 钩子函数：
    * afterOpen: 打开详情之后执行的函数
    * afterClose：关闭之后执行的函数
    