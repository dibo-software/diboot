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
  const treeData = cloneData.filter((father: T) => {
    const childArr = cloneData.filter(
      (child: T) => (father as Record<string, unknown>)[id] === (child as Record<string, unknown>)[parentId]
    )
    if (childArr.length > 0) (father as Record<string, unknown>)[children] = childArr
    return (father as Record<string, unknown>)[parentId] === rootId
  })
  return treeData.length === 0 ? list : treeData
}

/**
 * tree转化为list
 * @param tree
 * @param children
 */
export const tree2List = <T>(tree: T[], children = 'children') => {
  const list: T[] = []
  const cloneData = _.cloneDeep(tree)
  for (const node of cloneData) {
    list.push(node)
    const nodeChildren = ((node as Record<string, unknown>)[children] ?? []) as T[]
    for (const nodeChild of tree2List(nodeChildren)) {
      list.push(nodeChild)
    }
  }
  return list
}
type StandardTree = {
  key: unknown
  value: unknown
  label: string
  children?: StandardTree[]
}
/**
 * 将不规则的树转化为不规则的树
 * @param treeList
 * @param valueField
 * @param titleField
 * @param valueToString
 * @param valPrefix
 */
export const treeListFormatter = <T extends { children?: T[] }>(
    treeList: T[],
    valueField: keyof T,
    titleField: keyof T,
    valueToString: boolean,
    valPrefix = ''
) => {
  if (treeList == null || treeList.length === 0) {
    return undefined
  }
  const formatterList: StandardTree[] = []
  treeList.forEach(item => {
    let formatterItem: StandardTree
    if (valueToString) {
      formatterItem = {
        key: `${item[valueField]}`,
        value: `${valPrefix}${item[valueField]}`,
        label: item[titleField] as string
      }
    } else {
      formatterItem = {
        key: item[valueField],
        value: item[valueField],
        label: item[titleField] as string
      }
    }
    if (item.children != null && item.children.length > 0) {
      formatterItem.children = treeListFormatter(item.children, valueField, titleField, valueToString, valPrefix)
    }
    formatterList.push(formatterItem)
  })

  return formatterList
}
/**
 * 将树转化为带缩进的列表
 * @param treeList
 * @param level
 */
export const treeList2IndentList = function (treeList: StandardTree[], level: number) {
  if (treeList == null || treeList.length === 0) return []
  level = level == null ? 0 : level
  const allList: unknown[] = []
  let prefix = ''
  for (let i = 0; i < level; i++) prefix += '　'
  treeList.forEach(item => {
    item.label = prefix + item.label
    allList.push(item)
    if (item.children != null && item.children.length > 0) {
      allList.push(...treeList2IndentList(item.children, level + 1))
      delete item.children
    }
  })
  return allList
}
