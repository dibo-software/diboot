/**
 * list 转 tree
 *
 * @param {Array} list 数据源
 * @param {string | number} rootId 根Id 默认 '0'
 * @param {string} id ID字段 默认 'id' (同 value)
 * @param {string} parentId 父节点字段 默认 'parentId'
 * @param {string} children 孩子节点字段 默认 'children'
 */
export const list2Tree = <T>(
  list: T[],
  rootId: string | number = '0',
  id = 'id',
  parentId = 'parentId',
  children = 'children'
) => {
  //对源数据深度克隆
  const cloneData = _.cloneDeep(list)
  const treeData = cloneData.filter(father => {
    const childArr = cloneData.filter(
      child => (father as Record<string, unknown>)[id] === (child as Record<string, unknown>)[parentId]
    )
    if (childArr.length > 0) (father as Record<string, unknown>)[children] = childArr
    return (father as Record<string, unknown>)[parentId] === rootId
  })
  return treeData.length === 0 ? list : treeData
}
