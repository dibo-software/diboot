import type { LinkageControl, RelatedData } from '@/hooks/use-option'
import type { FormItemRule } from 'element-plus/es/tokens/form'

// tree 配置
export interface TreeConfig extends RelatedData {
  parent: string
  // 指定排序接口 （需同时也应指定 ext为排序属性）
  sortApi?: string
}

// list 配置
export interface ListConfig {
  baseApi: string
  // 主键属性名（默认值：id）
  primaryKey?: string
  // 默认 8 （24/8 三列）
  searchSpan?: number
  // FormItem 其中 'span' | 'rule' | 'required' 失效
  searchProps?: FormItem[]
  columns: TableColumn[]
  // 左树右列表 不指定时默认：parentId
  relatedKey?: string
  operation?: ListOperation
}

// 列表操作
export type ListOperation = Partial<
  Record<'detail' | 'create' | 'update' | 'remove' | 'batchRemove' | 'exportData' | 'importData', boolean>
>

// 表列
export interface TableColumn {
  hide?: boolean
  prop: string
  label: string
  width?: string | number
  sortable?: false | 'custom'
  fixed?: boolean
  filters?: Array<{ text: string; value: unknown }>
  showOverflowTooltip?: boolean
}

// 表单页配置
export interface FormConfig {
  labelWidth?: string // 默认 80px
  propList: FormItem[]
}

// 详情页配置
export interface DetailConfig {
  column?: number // 默认3
  propList: DetailProp[]
}

export interface DetailProp {
  span?: number // 默认 1
  prop: string
  label: string
  dict?: boolean // 是否为关联字典字段
  files?: string // VO中关联文件记录列表属性名
  iamge?: boolean // 关联文件是否为图片
}

// 输入类型
export interface Input {
  span?: number
  prop: string
  label?: string
  placeholder?: string
  rule?: FormItemRule
  required?: boolean
}

export interface InputText extends Input {
  type: 'input'
  textarea?: boolean
  autosize?: boolean | { minRows: number; maxRows: number }
  showWordLimit?: boolean
}

export interface InputNumber extends Input {
  type: 'input-number'
  min?: number
  max?: number
}

export interface Select extends Input {
  type: 'select' | 'checkbox' | 'radio'
  loader?: string | RelatedData
  labelProp: string
  remote?: boolean
  multiple?: boolean
  filterable?: boolean
  control?: LinkageControl | LinkageControl[]
}

export interface Cascader extends Input {
  type: 'cascader' | 'tree-select'
  lazy?: boolean
  loader?: RelatedData
  labelProp: string
  multiple?: boolean
  filterable?: boolean
  checkStrictly?: boolean
  control?: LinkageControl | LinkageControl[]
}

export interface DateTime extends Input {
  type: 'year' | 'month' | 'date' | 'datetime' | 'week' | 'daterange' | 'datetimerange'
  format?: string
}

export interface Time extends Input {
  type: 'time'
  range?: boolean
}

export interface ListSelector extends Input {
  type: 'list-selector'
  tree?: Omit<TreeConfig, 'sortApi'>
  list: Omit<ListConfig, 'operation'>
  // list 中的对象类型（用户回显树获取）
  dataType: string
  // 列表数据显示字段；默认取list第一列（list.columns[0].prop）
  dataLabel?: string
  multiple?: boolean
}

export type FormItem = InputText | InputNumber | Select | Cascader | DateTime | Time
