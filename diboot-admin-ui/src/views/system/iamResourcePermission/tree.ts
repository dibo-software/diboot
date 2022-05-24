import type { ElTree } from 'element-plus'
import { ApiData } from '@/utils/request'
export interface DataType<T> {
  selectedIdList: string[]
  treeDataList: T[]
}
export interface TreeOption {
  baseApi: string
  treeApi: string
  transformField?: { id?: string; label?: string; children?: string }
}

export default <T extends Record<string, any>>(option: TreeOption) => {
  const optionsTransformField = {
    id: 'id',
    label: 'label',
    children: 'children'
  }
  Object.assign(optionsTransformField, option.transformField || {})
  const dataState: DataType<T> = reactive({
    selectedIdList: [],
    treeDataList: []
  })

  const searchWord = ref('')
  // tree实例
  const treeRef = ref<InstanceType<typeof ElTree>>()

  const loading = ref(false)

  //监听keyword变化
  watch(searchWord, val => {
    treeRef.value!.filter(val)
  })
  /**
   * 获取树结构
   */
  const getTree = async () => {
    loading.value = true
    try {
      const result = await api.get<T[]>(`${option.baseApi}${option.treeApi}`)
      if (result && result.code === 0 && result.data) {
        dataState.treeDataList.push(...(result.data ?? []))
      } else {
        throw new Error(result.msg)
      }
    } catch (err: any) {
      ElNotification.error({
        title: '获取树列表数据失败',
        message: err.msg || err.message
      })
    } finally {
      loading.value = false
    }
  }

  /**
   * 复选框被点击的时候触发
   * @param data 被点击节点
   * @param checked 节点是否被选中
   */
  const checkChange = (data: T, checked: boolean) => {
    if (checked) {
      dataState.selectedIdList.push(...[data[optionsTransformField.id]])
    } else {
      dataState.selectedIdList = dataState.selectedIdList.filter(
        (selected: string) => selected !== data[optionsTransformField.id]
      )
    }
  }
  /**
   * 过滤节点
   * @param value
   * @param data
   */
  const filterNode = (value: string, data: Partial<T>) => {
    if (!value) return true
    return (data as T)[optionsTransformField.label].includes(value)
  }

  /**
   * 添加节点
   * @param treeNode
   */
  const addTreeNode = async (treeNode: T) => {
    try {
      const result = await api.post<T>(option.baseApi, treeNode)
      if (result && result.code === 0) {
        await getTree()
      } else {
        throw new Error(result.msg)
      }
    } catch (err: any) {
      ElMessage.error(err.msg || err.message || '添加数据失败！')
    }
  }

  /**
   * 删除节点
   * @param ids
   */
  const removeTreeNode = async () => {
    console.log(dataState.selectedIdList)
    if (!(dataState.selectedIdList && dataState.selectedIdList.length)) {
      ElMessage.warning('未选择数据')
      return
    }

    ElMessageBox.confirm('确认删除已选节点吗？', '删除树节点', { type: 'warning' })
      .then(() => {
        api
          .post(`${option.baseApi}/batchDelete`, dataState.selectedIdList)
          .then(() => {
            ElMessage.success('删除树节点成功！')
            getTree()
          })
          .catch(err => {
            ElMessage.error(err.msg || err.message || '删除失败！')
          })
      })
      .catch(() => null)
  }

  const { selectedIdList, treeDataList } = toRefs(dataState)
  return {
    checkChange,
    filterNode,
    getTree,
    removeTreeNode,
    addTreeNode,
    treeDataList,
    selectedIdList,
    searchWord,
    treeRef
  }
}
