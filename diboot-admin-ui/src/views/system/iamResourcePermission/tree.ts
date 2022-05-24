import type { ElTree } from 'element-plus'

export interface Tree {
  id: number
  label: string
  children?: Tree[]
}
export interface DataType {
  selectedIdList: number[]
  selectedFullList: Tree[]
}

export default () => {
  const dataState = reactive<DataType>({
    selectedIdList: [],
    selectedFullList: []
  })

  const searchWord = ref('')

  const treeRef = ref<InstanceType<typeof ElTree>>()

  /**
   * 获取树结构
   */
  const getTree = () => {
    console.log('0000')
  }

  /**
   * 复选框被点击的时候触发
   * @param data 被点击节点
   * @param checked 节点是否被选中
   */
  const checkChange = (data: Tree, checked: boolean) => {
    if (checked) {
      dataState.selectedIdList.push(...[data.id])
      dataState.selectedFullList.push(...[data])
    } else {
      dataState.selectedIdList = dataState.selectedIdList.filter((selected: number) => selected !== data.id)
      dataState.selectedFullList = dataState.selectedFullList.filter((selected: Tree) => selected.id !== data.id)
    }
  }
  /**
   * 过滤节点
   * @param value
   * @param data
   */
  const filterNode = (value: string, data: Partial<Tree>) => {
    if (!value) return true
    return (data as Tree).label.includes(value)
  }

  const { selectedIdList, selectedFullList } = toRefs(dataState)
  return {
    checkChange,
    filterNode,
    selectedIdList,
    selectedFullList,
    searchWord,
    treeRef,
    getTree
  }
}
