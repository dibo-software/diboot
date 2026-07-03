# PC 管理端规范

## 适用范围

用于 `diboot-admin-ui` 以及 Diboot 业务项目中的 PC 管理端页面。先遵守 `common-code-style.md`。

## 技术栈

- Vue 3，优先使用 `<script setup lang="ts">`。
- UI 控件优先使用 Element Plus。
- 样式使用 SCSS，页面或组件内样式优先 scoped，确需共享时再抽公共样式。
- 遵循项目别名和目录约定，尤其是 `@` 指向 `src`。

## 自动导入

不要手写项目 `vite.config.ts` 已配置自动导入的内容。

常见自动导入项包括：

- Vue、Vue Router、Pinia API，例如 `ref`、`reactive`、`computed`、`watch`、`onMounted`、`defineProps`、`defineEmits`。
- Lodash：`_`。
- Element Plus 反馈 API，例如 `ElMessage`、`ElMessageBox`、`ElNotification`。
- 请求工具：`api`、`baseURL`。
- CRUD hooks：`useList`、`useDetail`、`useForm`、`useOption`、`useSort`、`useUploadFile`、`useTreeCrud`。
- 权限工具：`checkPermission`、`checkRole`。

类型导入、未自动注册的本地组件、业务类型仍可显式 import。

## 类型组织

- 页面或业务组件涉及的实体、DTO、VO、表单模型、列表项、树节点、选项结构等业务类型，统一放在同目录 `type.ts` 中维护。
- `.vue` 业务组件内只保留 props、emits 等强组件局部类型；跨方法、跨组件或表达接口数据结构的类型不要直接定义在组件内。
- 业务组件使用 `import type { Xxx } from './type'` 导入同目录业务类型。
- 修改组件时，如果新增 `interface` 或 `type` 属于业务数据结构，应同步提取到 `type.ts`，避免组件混入类型定义。
- hooks、store、工具函数内部仅服务于自身实现的私有配置类型，可保留在对应 `.ts` 文件中。

## CRUD Hooks

写自定义请求或状态逻辑前，先使用现有 hooks：

- `useList`：列表加载、查询参数、分页、搜索、重置、删除、批量删除。
- `useForm`：新增、更新、表单校验和提交状态。
- `useDetail`：详情加载。
- `useOption`：字典、关联选项、异步选项和联动。
- `useSort`、`useUploadFile`、`useTreeCrud`：排序、上传、树形 CRUD 场景。

只有 hooks 覆盖不到的定制动作，才直接调用 `api.*`。

### useList 示例

```vue
<script setup lang="ts">
import type { UserModel } from './type'

const baseApi = '/iam/user'

// 列表查询：查询参数、分页、搜索、重置、删除统一使用 useList
const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, remove } = useList<UserModel>({
  baseApi,
  initQueryParam: { status: 'A' }
})

onMounted(getList)
</script>
```

### useForm 示例

```vue
<script setup lang="ts">
import type { FormInstance } from 'element-plus'

const baseApi = '/iam/user'
const formRef = ref<FormInstance>()
const visible = ref(false)
const model = reactive<Partial<UserModel>>({})

const emit = defineEmits<{
  (e: 'complete', id?: string): void
}>()

// 表单提交：新增、更新、校验、提交状态统一交给 useForm
const { submitting, submit } = useForm({
  baseApi,
  successCallback(id) {
    visible.value = false
    emit('complete', id)
  }
})
</script>
```

### useDetail 示例

```vue
<script setup lang="ts">
const baseApi = '/iam/user'
const detailVisible = ref(false)
const { loadData, loading, model } = useDetail<UserModel>(baseApi)

// 打开详情弹窗，并加载服务端详情数据
const openDetail = async (id: string) => {
  detailVisible.value = true
  await loadData(id)
}
</script>
```

### useOption 示例

```vue
<script setup lang="ts">
// 选项数据：字典和关联对象统一使用 useOption
const { relatedData, initRelatedData } = useOption({
  dict: ['USER_STATUS'],
  load: {
    roleOptions: { type: 'IamRole', label: 'name' }
  }
})

onMounted(initRelatedData)
</script>
```

## Vue 文件组织

同一个逻辑关注点相关代码放在相邻区域，避免为了一个功能在文件上下反复跳转。
在每个关注点内部，局部 state 应紧邻首次核心消费它的业务方法，例如选项 state 后紧跟加载选项方法，弹窗状态后紧跟打开/关闭方法，表单 model 后紧跟提交或回填方法。
不要把所有 `ref`、`reactive`、`computed` 统一堆到 `<script setup>` 顶部后再集中写方法；优先保持 hooks、局部 state、加载、提交、回填等代码局部聚合。

推荐顺序：

1. 类型 import 和业务常量。
2. 列表查询：`baseApi`、`useList`、搜索、重置、分页、删除。
3. 选项数据：`useOption`、选项初始化、远程过滤。
4. 表单弹窗：`visible`、`model`、`useForm`、打开/关闭/提交。
5. 详情弹窗：`useDetail`、打开/关闭详情。
6. 生命周期：只放启动当前关注点的调用。

新增前端方法必须加简洁注释，单个方法不超过 80 行。

## 组件

- 基础 UI 优先使用 Element Plus 组件。
- 共享能力优先使用 `src/components` 下已有 Diboot 组件，例如导入导出、富文本、文档展示、DI 组件、下载、图表封装等。
- 只有存在明确复用边界，或能隔离明显复杂度时，才新增组件。
- 组件名和 CSS class 名保持语义化、通用化，不使用临时实现命名。

## 样式

- 使用 `lang="scss"`。
- 页面组件避免写过宽的全局选择器。
- 标准间距、表单、表格、弹窗、按钮等优先使用 Element Plus 属性和已有结构，不随意堆自定义 CSS。
- 样式保持与相邻页面一致。

## 请求与工具

通过已有 `api` wrapper 使用 `src/utils/request.ts`：

- `api.get`、`api.post`、`api.put`、`api.patch`、`api.delete`
- `api.upload`
- `api.download`、`api.postDownload`

新增局部工具前，先检查并复用 `src/utils` 下已有 auth、file、i18n、list、permission、route、str、theme、tools、validate 等工具。
