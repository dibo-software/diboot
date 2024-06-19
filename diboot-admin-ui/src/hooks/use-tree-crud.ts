import type { ElTree } from 'element-plus'
import { tree2List } from '@/utils/list'
import type { TreeNodeData } from 'element-plus/es/components/tree/src/tree.type'
import i18n from '@/i18n'
export interface DataType<T> {
  selectedIdList: string[]
  treeDataList: T[]
}
type TransformField = { value?: string; label?: string; children?: string; parentId?: string }
/**
 * 收集树节点上所有父节点
 * @param parentId 父节点
 * @param dataList 子节点
 * @param result 存储结果
 * @param transformField 字段转化
 */
const collectDeepParent = <T>(parentId: string, dataList: T[], result: T[], transformField: TransformField) => {
  if (parentId === '0') return

  const data = (dataList.find(val => collectField<T, string>(val, transformField.value as string) === parentId) ??
    {}) as Record<string, unknown>
  if (data) {
    result.push(data as T)
  }
  collectDeepParent((data[transformField.parentId as string] ?? '0') as string, dataList, result, transformField)
}
/**
 * 收集对象中的指定字段
 * @param data 对象
 * @param fieldName 对象的属性名
 * @param defaultValue 属性默认值，不设置默认值，表示对象中一定可以获取到非null、非undefined值
 */
const collectField = <T, R>(data: T, fieldName: string, defaultValue?: R) => {
  const val = Object.getOwnPropertyDescriptor(data, fieldName)?.value
  return defaultValue ? ((val ?? defaultValue) as R) : (val as R)
}
/**
 * 收集指定字段列表
 * @param dataList 对象列表
 * @param fieldName 对象的名称
 * @param defaultValue 属性默认值
 */
const collectFieldList = <T, R>(dataList: T[], fieldName: string, defaultValue?: R) => {
  return dataList.map(val => collectField(val, fieldName, defaultValue))
}
export interface TreeOption<T> {
  baseApi: string
  treeApi: string
  sortApi?: string
  transformField?: TransformField
  clickNodeCallback?: (node: T) => void
}
export default <T>(option: TreeOption<T>) => {
  const optionsTransformField = {
    value: 'id',
    label: 'label',
    children: 'children',
    parentId: 'parentId'
  }
  const { baseApi, treeApi, sortApi, transformField, clickNodeCallback } = option
  Object.assign(optionsTransformField, transformField || {})
  const dataState: DataType<T> = reactive({
    selectedIdList: [],
    treeDataList: []
  })
  // 搜索值
  const searchWord = ref('')
  // tree实例
  const treeRef = ref<InstanceType<typeof ElTree>>()
  // 树加载loading
  const loading = ref(false)
  // 当前选中的node节点
  const currentNodeKey = ref('')

  //监听searchWord变化
  watch(searchWord, val => {
    treeRef.value?.filter(val)
  })

  // 树转list
  const dataList = computed(() => {
    return tree2List<T>(dataState.treeDataList)
  })
  /**
   * 获取树结构
   */
  const getTree = async () => {
    loading.value = true
    try {
      const result = await api.get<T[]>(`${baseApi}${treeApi}`)
      if (result && result.code === 0) {
        dataState.treeDataList = []
        dataState.treeDataList.push(...(result.data ?? []))
      } else {
        throw new Error(result.msg)
      }
    } catch (err: any) {
      ElNotification.error({
        title: i18n.global.t('hooks.fetchTreeFailed'),
        message: err.msg || err.message
      })
    } finally {
      loading.value = false
    }
  }

  /**
   * 复选框被点击的时候触发: 父子不互相关联
   * @param data 被点击节点
   * @param checked 节点是否被选中
   */
  const checkStrictlyChange = (data: T, checked: boolean) => {
    if (checked) dataState.selectedIdList.push(...[collectField<T, string>(data, optionsTransformField.value)])
    else
      dataState.selectedIdList = dataState.selectedIdList.filter(
        (selected: string) => selected !== collectField<T, string>(data, optionsTransformField.value)
      )
  }

  /**
   * 点击节点复选框之后触发	：详细规则如下
   * 选择框被勾选==> 当前选择框、子选择框、父/祖父选择框都需要被勾选，
   * 取消勾选 ==> 当前选择框、子选择框取消勾选，如果是父/祖父选择框下只有这当前选择框这一个子项，父/祖也要取消勾选，否则只取消当前选择框
   * @param data 被点击节点
   * @param checked 节点是否被选中
   */
  const checkNode = (currentNode: T, data: { checkedKeys: string[] }) => {
    const checkedKeys = data.checkedKeys
    const value = collectField<T, string>(currentNode, optionsTransformField.value)
    const result = [currentNode]
    // 递归查找子项
    const children = collectField<T, T[]>(currentNode, optionsTransformField.children, [])
    if (children && children.length > 0) {
      const childrenData = tree2List(children)
      result.push(...childrenData)
    }
    // 递归查找父项
    const parentId = collectField<T, string>(currentNode, optionsTransformField.value, '0')
    const parentResult: T[] = []
    collectDeepParent(parentId, dataList.value, parentResult, optionsTransformField)
    if (checkedKeys.includes(value)) {
      result.push(...parentResult)
      // 获取所有id，且已经选中的数据中不包含的id
      const values = collectFieldList<T, string>(result, optionsTransformField.value).filter(
        val => !dataState.selectedIdList.includes(val)
      )

      dataState.selectedIdList.push(...new Set(values))
    } else {
      const values = collectFieldList<T, string>(result, optionsTransformField.value)
      const parentValues = collectFieldList<T, string>(parentResult, optionsTransformField.value)
      // 查找父项下的所有子项(包含父项)
      const childrenResult = tree2List(parentResult) ?? []
      const mergeValues = [...values, ...parentValues]
      // 获取抛开当前节点下的所有子项和当前节点下所有父项 的剩余项
      const remainValues = collectFieldList<T, string>(childrenResult, optionsTransformField.value).filter(
        val => !mergeValues.includes(val)
      )
      // 判断剩余节点是否存在已选中的节点中, 当前节点的父级下有其他子节点，那么只移除当前节点及子节点，如果父级下无其他子节点，那么移除当前节点的父节点和子节点
      const resultValues = dataState.selectedIdList.some(val => remainValues.includes(val)) ? values : mergeValues
      dataState.selectedIdList = dataState.selectedIdList.filter((selected: string) => !resultValues.includes(selected))
    }
    // 设置选中状态
    treeRef.value?.setCheckedKeys(dataState.selectedIdList)
  }

  /**
   * 节点点击时触发
   * @param node 被点击节点
   */
  const nodeClick = (node: T) => {
    currentNodeKey.value = collectField<T, string>(node, optionsTransformField.value)
    currentNodeKey.value && treeRef.value?.setCurrentKey(currentNodeKey.value)
    clickNodeCallback && clickNodeCallback(node)
  }
  /**
   * 过滤节点
   * @param value
   * @param data
   */
  const filterNode = (value: string, data: Partial<T>) => {
    if (!value) return true
    return collectField<Partial<T>, string>(data, optionsTransformField.label).includes(value)
  }

  /**
   * 添加节点
   * @param treeNode
   */
  const addTreeNode = async (treeNode: T) => {
    try {
      const result = await api.post<string>(`${baseApi}/`, treeNode)
      if (result && result.code === 0) {
        Object.assign(treeNode as Record<string, unknown>, { [optionsTransformField.value]: result.data })
        dataState.treeDataList = []
        await getTree()
        // 设置当前节点选中
        treeRef.value?.setCurrentKey(result.data as string)
        const currentNode = treeRef.value?.getCurrentNode()
        if (currentNode) nodeClick(currentNode as T)
        else nodeClick(treeNode)
      } else {
        throw new Error(result.msg)
      }
    } catch (err: any) {
      ElMessage.error(err.msg || err.message || i18n.global.t('hooks.addDataFailed'))
    }
  }

  /**
   * 删除节点
   * @param ids
   */
  const removeTreeNode = async () => {
    if (!(dataState.selectedIdList && dataState.selectedIdList.length)) {
      ElMessage.warning(i18n.global.t('hooks.nonChooseData'))
      return
    }

    ElMessageBox.confirm(i18n.global.t('hooks.confirmDeleteNode'), i18n.global.t('hooks.delete'), { type: 'warning' })
      .then(() => {
        api
          .post(`${baseApi}/batch-delete`, dataState.selectedIdList)
          .then(() => {
            ElMessage.success(i18n.global.t('hooks.deleteSuccess'))
            getTree().then(() => setSelectNode())
          })
          .catch(err => {
            ElMessage.error(err.msg || err.message || i18n.global.t('hooks.deleteFailed'))
          })
      })
      .catch(() => null)
  }
  /**
   * 删除单个节点
   */
  const removeSingleTreeNode = async (id: string) => {
    dataState.selectedIdList.push(id)
    removeTreeNode()
  }
  /**
   * 设置选中的节点，指定时设置为指定节点，否则设置为树的第一个节点
   * @param node
   */
  const setSelectNode = (node?: T) => {
    if (node) {
      nodeClick(node)
    } else {
      if (dataState.treeDataList && dataState.treeDataList.length > 0) {
        // 设置当前节点选中
        treeRef.value?.setCurrentKey(
          (dataState.treeDataList as Record<string, unknown>[])[0][optionsTransformField.value as string] as string
        )
        const currentNode = treeRef.value?.getCurrentNode()
        if (currentNode) nodeClick(currentNode as T)
      }
    }
  }
  /**
   * 扁平化树最后一组节点
   * @param data
   */
  const flatTreeNodeClass = (data: TreeNodeData) => {
    const falseVal = { 'flat-tree-node-container': false }
    const children = (data[optionsTransformField.children] ?? []) as Record<string, unknown>[]
    if (!children || children.length === 0) return falseVal
    // 检查子节点是否是最后一组
    for (const child of children) {
      const temp = (child[optionsTransformField.children] ?? []) as Record<string, unknown>[]
      if (temp && temp.length !== 0) return falseVal
    }
    return { 'flat-tree-node-container': true }
  }
  // 拖拽相关
  /**
   * 拖拽排序
   * @param draggingNode 当前拖拽节点
   * @param dropNode 拖拽至目标节点
   * @param type 拖拽类型 before/after/inner
   */
  const nodeDrop = async (draggingNode: { data: T }, dropNode: { data: T }, type: string) => {
    loading.value = true
    const parentId =
      type !== 'inner'
        ? // 拖拽类型为before/after，当前拖拽父id为目标节点的父id
          collectField<T, string>(dropNode.data, optionsTransformField.parentId)
        : // 拖拽类型为inner，当前拖拽的父id为目标节点id
          collectField<T, string>(dropNode.data, optionsTransformField.value)
    let submitData: T[]
    // 查找parentId对应对象的所有子项
    if (parentId === '0') {
      submitData = dataState.treeDataList
    } else {
      const parentData = dataList.value.find(
        item => collectField<T, string>(item, optionsTransformField.value) === parentId
      )
      submitData = parentData
        ? _.clone(collectField<T, T[]>(parentData, optionsTransformField.children, []))
        : ([] as T[])
    }
    submitData.forEach(item => ((item as Record<string, unknown>)[optionsTransformField.parentId] = parentId))
    // TODO submitData为 被拖拽的节点所在的同级数据集合，提交至后端自行处理
    try {
      const result = await api.put<T[]>(`${baseApi}${sortApi}`, submitData)
      if (result && result.code === 0) {
        getTree().then(() => ElMessage?.success(i18n.global.t('hooks.sortSuccess')))
      } else {
        throw new Error(result.msg)
      }
    } catch (err: any) {
      ElNotification.error({
        title: i18n.global.t('hooks.fetchTreeFailed'),
        message: err.msg || err.message
      })
    } finally {
      loading.value = false
    }
  }

  const { selectedIdList, treeDataList } = toRefs(dataState)
  return {
    loading,
    dataList,
    treeDataList,
    selectedIdList,
    searchWord,
    treeRef,
    currentNodeKey,
    checkStrictlyChange,
    checkNode,
    filterNode,
    getTree,
    removeTreeNode,
    removeSingleTreeNode,
    addTreeNode,
    nodeClick,
    setSelectNode,
    flatTreeNodeClass,
    nodeDrop
  }
}
