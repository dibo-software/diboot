export function list2tree(list: any[]) {
  if (!list || list.length === 0) {
    return []
  }
  const parentIdListMap: Record<string, any[]> = {}
  for (const item of list) {
    const { parentId } = item
    let currentList = parentIdListMap[parentId as keyof typeof parentIdListMap]
    if (currentList === undefined) {
      currentList = []
      parentIdListMap[parentId] = currentList
    }
    currentList.push(item)
  }
  return deepGetChildren(parentIdListMap, '0')
}

export function deepGetChildren(parentIdListMap: Record<string, any[]>, id: string) {
  const children = parentIdListMap[id] || []
  for (const item of children) {
    item.children = deepGetChildren(parentIdListMap, item.id)
  }
  return children
}
