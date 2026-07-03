# 通用代码规范

## 方法与复杂度

- 前后端新增方法必须加简洁注释，说明业务意图、关键入参、返回语义或副作用。
- 注释解释“为什么”和“业务含义”，避免只把代码翻译成自然语言。
- 单个方法不得超过 80 行。超过时按校验、查询、转换、提交、回调、异常处理等步骤拆分。
- 避免大类、大方法、深层嵌套和重复分支；复杂条件优先提取为语义明确的小方法或变量。

```java
/**
 * 构建用户详情视图对象，并绑定角色与部门展示字段。
 */
private UserVO buildUserView(User user) {
    UserVO userVO = BeanUtils.convert(user, UserVO.class);
    Binder.bindRelations(Collections.singletonList(userVO));
    return userVO;
}
```

```ts
// 打开编辑弹窗，并在编辑模式下加载详情数据
const openForm = async (id?: string) => {
  currentId.value = id
  visible.value = true
  if (id) await loadData(id)
}
```

## 命名规约

- 类、方法、变量、常量命名必须表达业务含义，避免 `data`、`temp`、`item1` 这类无语义名称。
- 布尔变量使用肯定语义，例如 `enabled`、`visible`、`loading`、`hasPermission`。
- 常量使用大写下划线命名，魔法值必须提取为常量或枚举。
- 方法名使用动宾结构或清晰动作语义，例如 `buildQueryParam`、`loadUserOptions`、`validateBeforeSubmit`。
- 前端事件方法使用一致前缀，例如 `openXxx`、`closeXxx`、`handleXxx`、`onXxx`。

## 注释规约

- public API、Service 方法、复杂私有方法、前端新增业务方法都要有注释。
- 注释保持短句，不写流水账。
- 删除过期注释，不保留误导性待办标记。
- 注释中不要泄露账号、token、密钥、手机号、身份证号等敏感信息。

## 日志规约

- 后端使用项目已有 logger，不使用 `System.out.println` 或 `printStackTrace`。
- 异常日志保留异常对象，不能只记录 `e.getMessage()`。
- 日志内容要能定位业务场景，包含必要的业务标识，但不要输出密码、token、密钥、身份证、手机号、完整地址等敏感信息。
- 能被预期处理的业务失败优先返回业务错误或提示，不滥用 error 日志。

```java
log.warn("用户状态不允许执行当前操作, userId={}, status={}", userId, status);
log.error("导入用户数据失败, batchNo={}", batchNo, e);
```

## OOP 规约

- Controller 保持轻量，只做参数接收、权限入口、结果返回，业务逻辑放到 Service。
- Service 聚合业务流程，Mapper 只处理数据访问。
- VO/DTO 承载展示和传输结构，Entity 保持表映射职责。
- 优先组合清晰的小方法，不把所有逻辑堆进一个方法。
- 避免重复代码；相同规则出现两次以上时，考虑提取为方法、hook、组件或工具。

## 前端 ES6 与 Vue 组织

- 前端使用 ES6+：`const/let`、箭头函数、解构、模板字符串、可选链、空值合并、数组方法。
- 不使用 `var`；不使用字符串拼接替代模板字符串。
- 同一个 Vue 文件中，同一逻辑关注点的 state、computed、watch、方法、生命周期放在相邻区域。
- 推荐按业务关注点分组，例如“列表查询”“选项数据”“弹窗表单”“详情查看”“提交处理”。
- Composition API 中不要把所有 `ref`、`reactive`、`computed` 统一堆在顶部，再把消费它们的方法放到很远的位置；每个局部 state 应尽量紧邻首次核心消费它的业务入口方法。
- “首次核心消费”指加载、提交、回填、打开弹窗、刷新列表等业务入口，不按模板渲染引用排序。
- 辅助转换、查找、格式化方法可放在业务入口方法后面，以突出 state 与业务入口的关系；如果项目启用了禁止使用前声明的 lint 规则，则以 lint 为准，但仍要保持同一关注点局部相邻。
- 不要为了一个逻辑关注点在多个选项块之间来回跳转；优先使用 Composition API 的局部聚合优势。

```ts
const optionList = ref<LabelValue[]>([])

// 加载选项数据，并统一写入页面选项状态
const loadOptionList = async () => {
  const { data } = await api.get('/example/options')
  optionList.value = normalizeOptionTree(data ?? [])
}

// 标准化树形选项，屏蔽不可选择节点
const normalizeOptionTree = (list: LabelValue[] = []): LabelValue[] =>
  list.map(item => ({
    ...item,
    disabled: item.ext === 'folder',
    children: item.children?.length ? normalizeOptionTree(item.children) : item.children
  }))
```
