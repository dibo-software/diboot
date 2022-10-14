interface SortOption {
  sortApi: string
  callback?: () => void
}

export default <T extends { id: string; parentId?: string; sortId: string | number }>(option: SortOption) => {
  /**
   * 排序请求
   *
   * @param param 参数
   */
  const sortRequest = (
    param: { id: string; newSortId: string | number } & (
      | { newParentId: string; oldSortId?: string | number }
      | { oldSortId: string | number }
    )
  ) => {
    api
      .patch(option.sortApi, param)
      .catch(err => {
        ElMessage.error(err.msg || err.message || '排序失败')
      })
      .finally(option.callback)
  }

  /**
   * Tree 排序（拖拽节点）
   *
   * @param node 拖动节点
   * @param dragNode 目标节点
   * @param dropPosition 相对位置
   */
  const nodeDrag = (node: { data: T }, dragNode: { data: T }, dropPosition: 'before' | 'inner' | 'after') => {
    const data = node.data
    const dragData = dragNode.data
    console.log(node)
    console.log(dragNode)
    console.log(data)
    console.log(dragData)
    console.log(dropPosition)
    const id = data.id
    let newParentId
    let newSortId
    let oldSortId
    switch (dropPosition) {
      case 'inner':
        newParentId = dragData.id
        newSortId = 1
        break
      case 'after':
        newParentId = dragData.parentId
        newSortId = (BigInt(dragData.sortId) + BigInt(1)).toString()
        oldSortId = data.parentId === dragData.parentId ? data.sortId : undefined
        break
      case 'before':
        newParentId = dragData.parentId
        newSortId = dragData.sortId
        oldSortId = data.parentId === dragData.parentId ? data.sortId : undefined
        break
      default:
        return
    }
    sortRequest({ id, newParentId: newParentId as string, newSortId, oldSortId })
  }

  /**
   * List 排序
   *
   * @param item 拖动元素
   * @param newSortId 新序号（目标元素序号）
   */
  const itemDrag = (item: T, newSortId: string | number) =>
    sortRequest({ id: item.id, newSortId, oldSortId: item.sortId })

  return {
    nodeDrag,
    itemDrag
  }
}
