export interface Dictionary {
  id?: string
  parentId?: string
  type?: string
  itemName?: string
  itemNameI18n?: string
  itemValue?: string
  description?: string
  color?: string
  children?: Dictionary[]
}
