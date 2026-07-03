# 移动端规范

## 适用范围

用于 `diboot-mobile-ui` 以及 Diboot 业务项目中的移动端页面。先遵守 `common-code-style.md`。

## 技术栈

- Vue 3，相邻代码使用 TypeScript 时优先使用 `<script setup lang="ts">`。
- 移动端 UI 控件优先使用 Vant 4。
- 样式使用 SCSS。
- 遵循项目别名和目录约定，尤其是 `@` 指向 `src`。

## 自动导入

不要手写移动端项目 `vite.config.ts` 已配置自动导入的内容。

常见自动导入项包括：

- Vue、Vue Router、Pinia API。
- Lodash：`_`。
- Vant 反馈 API，例如 `showToast`、`showLoadingToast`、`showSuccessToast`、`showFailToast`、`closeToast`、`showNotify`、`showConfirmDialog`。
- 请求工具：`api`、`baseURL`。
- CRUD hooks：`useList`、`useDetail`、`useForm`、`useOption`、`useSort`、`useUploadFile`。

类型导入、未自动导入的 Vant 数据包、本地组件、业务类型仍可显式 import。

## 类型组织

- 页面或业务组件涉及的实体、DTO、VO、表单模型、列表项、树节点、选项结构等业务类型，统一放在同目录 `type.ts` 中维护。
- `.vue` 业务组件内只保留 props、emits 等强组件局部类型；跨方法、跨组件或表达接口数据结构的类型不要直接定义在组件内。
- 业务组件使用 `import type { Xxx } from './type'` 导入同目录业务类型。
- 修改组件时，如果新增 `interface` 或 `type` 属于业务数据结构，应同步提取到 `type.ts`，避免组件混入类型定义。
- hooks、store、工具函数内部仅服务于自身实现的私有配置类型，可保留在对应 `.ts` 文件中。

## CRUD Hooks

写自定义请求或状态逻辑前，先使用现有移动端 hooks：

- `useList`：列表加载、下拉刷新、下一页加载、查询参数、搜索、重置、删除、批量删除。
- `useForm`：新增、更新、校验和提交状态。
- `useDetail`：详情加载。
- `useOption`：字典和关联选项。
- `useSort`、`useUploadFile`：排序和上传场景。

只有 hooks 覆盖不到的定制动作，才直接调用 `api.*`。

### useList 示例

```vue
<script setup lang="ts">
import type { Customer } from './type'

const baseApi = '/customer'

// 移动端列表：查询、刷新、翻页和删除统一使用 useList
const {
  queryParam,
  loading,
  refreshing,
  dataList,
  pagination,
  getList,
  onSearch,
  nextPage,
  refreshList,
  resetFilter,
  remove
} = useList<Customer>({ baseApi, pageSize: 10 })

// 搜索客户名称，并回到第一页
const searchByName = (keyword: string) => {
  queryParam.name = keyword
  onSearch()
}

onMounted(() => getList(true))
</script>
```

### useForm 示例

```vue
<script setup lang="ts">
const baseApi = '/customer'
const model = reactive<Partial<Customer>>({})

// 保存移动端表单，并在成功后返回上一页
const { submitting, submit } = useForm({
  baseApi,
  successCallback() {
    showSuccessToast('保存成功')
    router.back()
  }
})
</script>
```

## Vue 文件组织

同一个逻辑关注点相关代码放在相邻区域。
在每个关注点内部，局部 state 应紧邻首次核心消费它的业务方法，例如列表 state 后紧跟刷新/翻页方法，picker/select 选项 state 后紧跟加载选项方法，表单 model 后紧跟提交或回填方法。
不要把所有 `ref`、`reactive`、`computed` 统一堆到 `<script setup>` 顶部后再集中写方法；移动端列表、表单、选择器和页面跳转逻辑也要保持 state、hooks、加载、提交、回填等代码局部聚合。

推荐顺序：

1. 类型 import 和业务常量。
2. 列表查询：`useList`、搜索、刷新、翻页、删除。
3. 选项数据：`useOption`、picker/select 数据。
4. 表单提交：`model`、`useForm`、提交前处理。
5. 页面跳转和生命周期。

新增前端方法必须加简洁注释，单个方法不超过 80 行。

## 组件

- 移动端交互优先使用 Vant 组件。
- 共享能力优先使用 `src/components` 下已有移动端组件，例如 icon、rich、select、DI、scan-code 等。
- 只有行为可复用，或能明显简化页面时，才新增组件。
- 移动端布局保持适合触控，并与相邻页面一致。

## 样式

- 组件样式使用 `lang="scss"`。
- CSS class 名保持语义化、通用化。
- 不把 PC 端布局假设带到移动端页面。
- 标准表单、列表、选择器、弹窗、按钮、单元格等优先使用 Vant 属性和结构。

## 请求与工具

通过已有 `api` wrapper 使用 `src/utils/request.ts`：

- `api.get`、`api.post`、`api.put`、`api.patch`、`api.delete`
- `api.upload`
- `api.download`、`api.postDownload`

新增局部工具前，先检查并复用 `src/utils` 下已有 auth、file、i18n、list、permission、request、str、validate 等工具。
