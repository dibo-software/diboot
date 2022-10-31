export interface Dictionary {
  id?: string
  parentId?: string
  type?: string
  itemName?: string
  itemNameI18n?: string
  itemValue?: string
  description?: string
  extension?: Record<string, unknown>
  createTime?: string
  children?: Dictionary[]
}
