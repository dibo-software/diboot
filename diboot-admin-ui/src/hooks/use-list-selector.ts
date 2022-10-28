export interface ListSelectorOption {
  keyName?: string
  multi?: boolean
}
export default <T>(option: ListSelectorOption) => {
  const keyName = option.keyName || 'id'

  const rowSelectChangeHandler = (
    selectedRows: T[],
    parentSelectedRows: T[],
    single: boolean,
    dataList: T[]
  ): { allSelectedKeys?: T[keyof T][]; allSelectedRows: T[] } => {
    let allSelectedRows = selectedRows || []
    const currentKeyName = keyName as keyof T
    const selectedKeys = selectedRows.map((item: T) => item[currentKeyName])
    let allSelectedKeys = selectedKeys
    if (!single) {
      // 合并已存在和当前选中数据列表
      const existIdList = parentSelectedRows.map(item => item[currentKeyName])
      const allSelectedKeySet = new Set([...existIdList, ...allSelectedKeys])
      allSelectedKeys = Array.from(allSelectedKeySet)
      // 过滤当前页面已存在数据，却不在已选中数据的数据
      const currentPageKeys = dataList.map(item => item[currentKeyName])
      allSelectedKeys = allSelectedKeys.filter(key => {
        return selectedKeys.includes(key) || !currentPageKeys.includes(key)
      })
      allSelectedRows = []
      for (const key of allSelectedKeys) {
        let row = selectedRows.find(item => item[currentKeyName] === key)
        if (row === undefined) {
          row = parentSelectedRows.find(item => item[currentKeyName] === key)
        }
        if (row !== undefined) {
          allSelectedRows.push(row)
        }
      }
    }
    return {
      allSelectedKeys,
      allSelectedRows
    }
  }
  return {
    keyName,
    rowSelectChangeHandler
  }
}
