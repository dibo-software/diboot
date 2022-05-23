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

  const checkChange = (data: Tree, checked: boolean) => {
    if (checked) {
      dataState.selectedIdList.push(...[data.id])
      dataState.selectedFullList.push(...[data])
    } else {
      dataState.selectedIdList = dataState.selectedIdList.filter((selected: number) => selected !== data.id)
      dataState.selectedFullList = dataState.selectedFullList.filter((selected: Tree) => selected.id !== data.id)
    }
  }
  const { selectedIdList, selectedFullList } = toRefs(dataState)
  return {
    checkChange,
    selectedIdList,
    selectedFullList
  }
}
