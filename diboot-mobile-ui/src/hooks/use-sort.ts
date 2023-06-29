interface SortOption {
  sortApi: string
  callback?: () => void
  // 默认 id
  idKey?: string
  // 默认 parentId
  parentIdKey?: string
  // 默认 sortId
  sortIdKey?: string
}

export default ({ sortApi, callback, idKey = 'id', parentIdKey = 'parentId', sortIdKey = 'sortId' }: SortOption) => {
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
      .patch(sortApi, param)
      .catch(err => {
        showNotify({ type: 'danger', message: err.msg || err.message || '排序失败' })
      })
      .finally(callback)
  }

  /**
   * Tree 排序（拖拽节点）
   *
   * @param node 拖动节点
   * @param dragNode 目标节点
   * @param dropPosition 相对位置
   */
  const nodeDrag = (
    node: { data: Record<string, string | unknown> },
    dragNode: { data: Record<string, string> },
    dropPosition: 'before' | 'inner' | 'after'
  ) => {
    const data = node.data
    const dragData = dragNode.data
    const id = data[idKey] as string
    let newParentId
    let newSortId
    let oldSortId: string | undefined
    switch (dropPosition) {
      case 'inner':
        newParentId = dragData[idKey]
        newSortId = '1'
        break
      case 'after':
        newParentId = dragData[parentIdKey]
        newSortId = (BigInt(dragData[sortIdKey]) + BigInt(1)).toString()
        oldSortId = data[parentIdKey] === dragData[parentIdKey] ? (data[sortIdKey] as string) : undefined
        break
      case 'before':
        newParentId = dragData[parentIdKey]
        newSortId = dragData[sortIdKey]
        oldSortId = data[parentIdKey] === dragData[parentIdKey] ? (data[sortIdKey] as string) : undefined
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
  const itemDrag = (item: Record<string, string | unknown>, newSortId: string | number) =>
    sortRequest({ id: item.id as string, newSortId, oldSortId: item[sortIdKey] as string | number })

  return {
    nodeDrag,
    itemDrag
  }
}
