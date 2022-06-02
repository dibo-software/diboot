export function list2tree(list) {
  if (!list || list.length === 0) {
    return []
  }
  const parentIdListMap = {}
  for (const item of list) {
    const { parentId } = item
    let currentList = parentIdListMap[parentId]
    if (currentList === undefined) {
      currentList = []
      parentIdListMap[parentId] = currentList
    }
    currentList.push(item)
  }
  return deepGetChildren(parentIdListMap, '0')
}

export function deepGetChildren(parentIdListMap, id) {
  const children = parentIdListMap[id] || []
  for (const item of children) {
    item.children = deepGetChildren(parentIdListMap, item.id)
  }
  return children
}
